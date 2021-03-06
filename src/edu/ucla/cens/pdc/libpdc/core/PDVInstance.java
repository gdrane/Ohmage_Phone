/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cens.pdc.libpdc.core;

import edu.ucla.cens.pdc.libpdc.Application;
import edu.ucla.cens.pdc.libpdc.Constants;
import edu.ucla.cens.pdc.libpdc.stream.DataStream;
import edu.ucla.cens.pdc.libpdc.util.MiscFuncs;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.ccnx.ccn.CCNFilterListener;
import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.Interest;
import org.ccnx.ccn.protocol.MalformedContentNameStringException;
import org.ohmage.OhmageApplication;
import org.ohmage.pdc.OhmagePDVManager;
import android.util.Log;

/**
 *
 * @author Derek Kulinski
 */
final public class PDVInstance implements CCNFilterListener {
	public PDVInstance()
	{
		try {
			if (GlobalConfig.hasFeature(GlobalConfig.FEAT_SHARING)) {
				add_stream_command(new StreamDataCommand());
				add_stream_command(new StreamControlCommands());
			}

			if (GlobalConfig.hasFeature(GlobalConfig.FEAT_MANAGE))
				add_generic_command(new ManageCommand());
			
			if (GlobalConfig.hasFeature(GlobalConfig.FEAT_KEYSTORE))
				add_generic_command(new KeyStoreCommand());
		}
		catch (MalformedContentNameStringException ex) {
			throw new Error("Unable to instantiate PDVInstance class", ex);
		}
	}

	public void startListening()
			throws MalformedContentNameStringException, IOException
	{
		ContentName root = _config.getRoot();
		assert root != null;
		_config.getCCNHandle().registerFilter(root, this);

		if (GlobalConfig.hasFeature(GlobalConfig.FEAT_MANAGE)) {
			Log.v(TAG, "PDV Instance Root: " + root.toURIString());
			MiscFuncs.printKeyToLog(this._config.getKeyManager().getKeysForWC().
					getPublic(), "Public Key: ");
		}
	}

	public void stopListening()
	{
		ContentName root = _config.getRoot();
		_config.getCCNHandle().unregisterFilter(root, this);
	}

	public boolean handleInterest(Interest interest)
	{
		/*
		 * <root>/<app_id>/<stream_id>
		 *
		 * Stream commands:
		 * <stream_id>/data/<data_id>
		 * <stream_id>/control/authenticator/<receiver>
		 * <stream_id>/control/stream_info/<receiver>
		 * <stream_id>/control/list[/<last_data_id>]
		 *
		 * Receiver commands:
		 * <app_id>/control/setup/<stream>
		 * <app_id>/control/pull/<stream>
		 *
		 * Manage commands:
		 * manage/list/apps
		 * manage/list/datastreams/<app_id>
		 * manage/list/datarecords/<app_id>/<ds_id>                 *
		 * manage/login
		 * manage/login/isAuthenticated
		 */

		ContentName root, name, postfix;
		String app_name, ds_name, command_name;
		Application app;
		DataStream ds;

		root = _config.getRoot();
		name = interest.name();
		postfix = name.postfix(root);

		Log.v(TAG,"Got interest: " + name + ", postfix: " + postfix);

		if (postfix == null || postfix.count() < 2)
			return false;

		//check for manage command
		if (postfix.stringComponent(0).equals(Constants.STR_MANAGE)) {
			GenericCommand cmd = this._generic_commands.get(Constants.STR_MANAGE);
			if (cmd == null) {
				Log.e(TAG,"manage command not supported by this PDV Instance.");
				return false;
			}
			return cmd.processCommand(postfix.subname(1, postfix.count() - 1),
					interest);
		}

		if(postfix.stringComponent(0).equals(
				OhmagePDVManager.getAppInstance())) {
			String origin_id = postfix.stringComponent(1);
			Log.v(TAG, "Origin Id received in the interest : " + origin_id);
			Log.v(TAG, "Origin Id stored in the device : " + 
			OhmagePDVManager.getHashedDeviceId());
			// Check whether interest has come to the right location
			if(!origin_id.equals(OhmagePDVManager.getHashedDeviceId()))
				return false;
			
			OhmageApplication ohmageapp = (OhmageApplication)
					OhmagePDVManager.getInstance().getAndroidApplication();
			if (ohmageapp == null) {
				Log.w(TAG,"Application "
						+ " doesn't exist.");
				return false;
			}

			ds_name = postfix.stringComponent(2);
			if(ds_name.equals(Constants.STR_MANAGE)) {
				GenericCommand cmd = this._generic_commands.get(Constants.STR_MANAGE);
				if (cmd == null) {
					Log.e(TAG,"manage command not supported by this PDV Instance.");
					return false;
				}
				return cmd.processCommand(
						 postfix.subname(1, postfix.count() - 1),
						interest);
			}
			ds = ohmageapp.getDataStream(ds_name);
			if (ds == null) {
				Log.w(TAG,"Stream " + ds_name + " doesn't exist.");
				return false;
			}

			command_name = postfix.stringComponent(3);
			if (!_stream_commands.containsKey(command_name)) {
				Log.w(TAG,"Unknown command: " + command_name);
				return false;
			}

			ContentName remain = postfix.subname(1, postfix.count() - 1);
			StreamCommand cmd = _stream_commands.get(command_name);
			
			return cmd.processCommand(ds, remain, interest);
		}
		
		app_name = postfix.stringComponent(0);
		app = _config.getApplication(app_name);
		if (app == null) {
			Log.w(TAG,"Application " + app_name
					+ " doesn't exist.");
			return false;
		}

		if (postfix.stringComponent(1).equals("control"))
			//TODO: implement it (receiver side)
			throw new UnsupportedOperationException(
					"Not implemented yet");

		if (postfix.count() < 4)
			return false;

		ds_name = postfix.stringComponent(1);
		ds = app.getDataStream(ds_name);
		if (ds == null) {
			Log.w(TAG,"Stream " + ds_name + " doesn't exist.");
			return false;
		}

		command_name = postfix.stringComponent(2);
		if (!_stream_commands.containsKey(command_name)) {
			Log.w(TAG,"Unknown command: " + command_name);
			return false;
		}

		ContentName remain = postfix.subname(3, postfix.count() - 3);
		StreamCommand cmd = _stream_commands.get(command_name);

		return cmd.processCommand(ds, remain, interest);
	}

	private void add_stream_command(StreamCommand command)
	{
		_stream_commands.put(command.getCommandName(), command);
	}

	private void add_generic_command(GenericCommand command)
	{
		_generic_commands.put(command.getCommandName(), command);
	}

	final private Map<String, GenericCommand> _generic_commands = 
			new HashMap<String, GenericCommand>(1);

	final private Map<String, StreamCommand> _stream_commands = 
			new HashMap<String, StreamCommand>(2);

	final private GlobalConfig _config = GlobalConfig.getInstance();
	
	private final static String TAG = "PDVInstanceTAG";
}
