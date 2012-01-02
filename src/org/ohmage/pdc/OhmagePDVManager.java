package org.ohmage.pdc;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import org.ccnx.android.ccnlib.CCNxConfiguration;
import org.ccnx.android.ccnlib.CCNxServiceCallback;
import org.ccnx.android.ccnlib.CCNxServiceControl;
import org.ccnx.android.ccnlib.CCNxServiceStatus.SERVICE_STATUS;
import org.ccnx.android.ccnlib.CcndWrapper;
import org.ccnx.android.ccnlib.CcndWrapper.CCND_OPTIONS;
import org.ccnx.android.ccnlib.RepoWrapper.REPO_OPTIONS;
import org.ccnx.ccn.profiles.ccnd.CCNDaemonException;
import org.ccnx.ccn.profiles.ccnd.SimpleFaceControl;
import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.MalformedContentNameStringException;

import org.ohmage.OhmageApplication;
import org.ohmage.SharedPreferencesHelper;
import org.ohmage.pdc.storage.SQLiteConfigStorage;
import org.ohmage.pdc.storage.SQLiteDataStorage;

import edu.ucla.cens.pdc.libpdc.core.GlobalConfig;
import edu.ucla.cens.pdc.libpdc.core.PDVInstance;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCTransmissionException;
import edu.ucla.cens.pdc.libpdc.stream.DataStream;
import edu.ucla.cens.pdc.libpdc.transport.PDCReceiver;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Top level class for NDN code.
 * 
 * @author Gauresh Rane
 */

public class OhmagePDVManager implements CCNxServiceCallback {
	
	static {
		GlobalConfig.setDataStorage(SQLiteDataStorage.class);
		GlobalConfig.setConfigStorage(SQLiteConfigStorage.class);
	}
	
	private OhmagePDVManager(OhmageApplication app, 
			OhmagePDVManagerCallback callback) {
		_callback_interface = callback;
		_ohmage_application = app;
		telephonyManager = (TelephonyManager)app.getSystemService(
				Context.TELEPHONY_SERVICE);
		DEVICE_ID = telephonyManager.getDeviceId();
	}
	
	public void InitializeCCNx(Context ctx) {
		_ccnx_service = new CCNxServiceControl(ctx);
		_ccnx_service.registerCallback(this);
		_ccnx_service.setCcndOption(CCND_OPTIONS.CCND_DEBUG, "INFO");
		_ccnx_service.setRepoOption(REPO_OPTIONS.REPO_DEBUG, "INFO");

		_ccnx_service.startAllInBackground();
		CCNxConfiguration.config(ctx);
	}
	
	public void setNamespace(String namespace)
	{
		_namespace = namespace;
	}

	public void setRemotehost(String remotehost)
	{
		_remotehost = remotehost;
	}

	public void setRemoteport(int remoteport)
	{
		_remoteport = remoteport;
	}

	public void setPubUrl(String pubUrl)
	{
		_pub_url = pubUrl;
	}

	public void setRecAuth(String recAuth)
	{
		_rec_auth = recAuth;
	}
	
	public void setRecUrl(String recUrl)
	{
		_rec_url = recUrl;
	}
	
	public Application getAndroidApplication() {
		assert _ohmage_application != null :"Application Object not initialized";
		return _ohmage_application;
	}
	
	public static OhmagePDVManager getInstance(OhmageApplication app,
			OhmagePDVManagerCallback callback)
	{
		if (_ohmage_NDN_manager == null) {
			createNewInstance(app, callback);
		}

		return _ohmage_NDN_manager;
	}
	
	public static OhmagePDVManager getInstance()
	{
		assert _ohmage_NDN_manager != null : 
			"Ohmage NDN Manager not initialized";
		return _ohmage_NDN_manager;
	}
	
	public static OhmagePDVManager createNewInstance(OhmageApplication app,
			OhmagePDVManagerCallback callback)
	{
		if (_ohmage_NDN_manager == null) {
			_ohmage_NDN_manager = new OhmagePDVManager(app, callback);
		}

		return _ohmage_NDN_manager;
	}
	
	public void create()
	{
		PDCReceiver _pdc_receiver = null;
		try {
			org.ccnx.ccn.impl.support.Log.setDefaultLevel(
					org.ccnx.ccn.impl.support.Log.FAC_ALL, Level.INFO);

			GlobalConfig config = GlobalConfig.loadState();
			assert config == null;
			if (config == null) {
				Log.i(TAG,"Generating new config");
				config = GlobalConfig.getInstance();
				config.setRoot(ContentName.parse(_namespace));
				Log.i(TAG, _namespace);

				config.addApplicationOnPhone(_ohmage_application);
				
				// Setup the Application Object
				_ohmage_application.setupObject(false);

				String username = new SharedPreferencesHelper(
						_ohmage_application.getApplicationContext()).
						getUsername();
				String usernamehash = hashingFunction(username);
				_stream = null;
				_stream = new DataStream(
						config.getCCNHandle(),
						_ohmage_application,
						_mobility_data_stream + usernamehash);
				assert _stream != null;
				_ohmage_application.addDataStream(_stream);
				_pdc_receiver = new PDCReceiver(
						_rec_url);
				_stream.addReceiver(_pdc_receiver);
				
				_stream = null;
				_stream = new DataStream(
						config.getCCNHandle(),
						_ohmage_application,
						_survey_response_stream + usernamehash);
				assert _stream != null;
				_ohmage_application.addDataStream(_stream);
				_pdc_receiver = new PDCReceiver(
						_rec_url);
				_stream.addReceiver(_pdc_receiver);
				
				// config.storeStateRecursive();
				Log.i(TAG, "New Config.");
			} else
				Log.i(TAG, "Using existing config");

		}
		catch (final MalformedContentNameStringException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Initialize the pdv_instance
		assert _pdv_instance != null;

		_pdv_instance = new PDVInstance();
		Log.i(TAG, "PDV Instance initialized");
	}
	
	public synchronized boolean startCCNx()
	{
		Log.i(TAG, "RemoteHost is: " + _remotehost);
		Log.i(TAG, "Remote Port is:" + _remoteport);
		assert _ccnx_service != null : "CCNX Service is NULL";
		Log.i(TAG, "Start All Done");
		Log.i(TAG, _ccnx_service.getCcndStatus().toString());
		Log.i(TAG, _ccnx_service.getRepoStatus().toString());
		if (!_ccnx_service.allReady()) {
			if(!SERVICE_STATUS.SERVICE_RUNNING.equals(
					_ccnx_service.getCcndStatus())) {
				_ccnx_service.startCcnd();
				_callback_interface.postProcess(
						"CCND wasn't running, starting CCND..");
			}
			if(!SERVICE_STATUS.SERVICE_RUNNING.equals(
					_ccnx_service.getRepoStatus())) {
				_ccnx_service.startRepo();
				_callback_interface.postProcess(
						"Key Repository wasn't running, starting Repo..");
			}
			Log.i(TAG, "Something was not started");
			_callback_interface.postProcess("Calm down, CCNx is not up yet", 
					false);
			return false;
		}

		// DataGenerator.start(_context,
		// 		_context.getResources().openRawResource(R.raw.test), 1, 1);

		_ccnx_service.connect();
		try {
		
			SimpleFaceControl.getInstance().connectTcp(_remotehost,
					_remoteport);
		}
		catch (CCNDaemonException e) {
			Log.e(TAG, "Unable to make connection", e);
			_callback_interface.postProcess(
					"Unable to connect to hub: " + e.getLocalizedMessage(), 
					true);
			return false;
		}
		Log.i(TAG, "calling function to change connection state");
		_callback_interface.postProcess(true);
		return true;
	}
	
	public synchronized void startListening()
	{
		if (_listening) {
			return;
		}
		try {
			assert _pdv_instance != null;
			_pdv_instance.startListening();
			_listening = true;
		}
		catch (MalformedContentNameStringException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_callback_interface.postProcess("PDV Started listening..");
	}
	
	public void setupStreams()
	{
		String username = new SharedPreferencesHelper(
				_ohmage_application.getApplicationContext()).
				getUsername();
		String usernamehash = hashingFunction(username);
		DataStream ds = null;
		ds = _ohmage_application.getDataStream(
				_mobility_data_stream + usernamehash);
		String hashedIMEI  = hashingFunction(DEVICE_ID);
		for(PDCReceiver pdc_receiver : ds.getReceivers())
		{
			try {
				ds.setupReceiver(pdc_receiver, _rec_auth, hashedIMEI);
			} catch (PDCTransmissionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ds = _ohmage_application.getDataStream(
				_survey_response_stream + usernamehash);
		for(PDCReceiver pdc_receiver : ds.getReceivers())
		{
			try {
				ds.setupReceiver(pdc_receiver, _rec_auth, hashedIMEI);
			} catch (PDCTransmissionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static String hashingFunction(String username)
	{
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert md != null;
		md.update(username.getBytes());
		byte hashData[] = md.digest();
		StringBuffer hexString = new StringBuffer();
		for(byte single: hashData) {
			String hex = Integer.toString(0xff & single);
			if(hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}
	
	public static String getHashedUsername()
	{
		String username = new SharedPreferencesHelper(
				_ohmage_application.getApplicationContext()).
				getUsername();
		return hashingFunction(username);
	}
	
	public static String getUsername()
	{
		return new SharedPreferencesHelper(
				_ohmage_application.getApplicationContext()).
				getUsername();	
	}
	
	public synchronized void shutdown()
	{
		if (!_listening)
			return;

		if (_pdv_instance == null)
			throw new Error("There's no PDV instance!");

		_pdv_instance.stopListening();
		
		_listening = false;
	}
	
	
	/*
	public synchronized void setup() throws PDCException
	{
		Log.d(TAG, "Running: " + _listening);
		
		if (!_listening)
			return;

		if (_pdv_instance == null)
			throw new Error("There's no PDV instance!");

		if (_stream == null)
			throw new Error("There's no Stream instance!");
		
		try {
			// ContentName pub_uri = ContentName.parse(_pub_url);
			_pdv_receiver = new PDCReceiver(_rec_url);
			_stream.addReceiver(_pdv_receiver);
			_stream.setupReceiver(_pdv_receiver, _rec_auth);
		} catch (MalformedContentNameStringException e) {
			Log.e(TAG, "Invalid URI: " + _pub_url, e);
			return;
		}
	}
	*/
	
	// Accessors 
	
	public boolean isListening() {
		return _listening;
	}
	
	void postProcess(String str) {
		_callback_interface.postProcess(str);
	}
	

	@Override
	public void newCCNxStatus(SERVICE_STATUS st) {
		// TODO Auto-generated method stub
		switch (st) {
		case CCND_INITIALIZING:
			_callback_interface.postProcess("Initializing CCNd ...");
			break;
		case CCND_OFF:
			_callback_interface.postProcess("CCNd is off.");
			break;
		case CCND_RUNNING:
			_callback_interface.postProcess("CCNd is running.");
			break;
		case CCND_TEARING_DOWN:
			_callback_interface.postProcess("Tearing down CCNd ...");
			break;
		case START_ALL_DONE:
			Log.i(TAG, "CCNx started. START_ALL_DONE");
			_callback_interface.postProcess("CCNx started", false);
			break;
		case START_ALL_ERROR:
			_callback_interface.postProcess("Error while starting CCNx");
			_callback_interface.postProcess("Error while starting CCNx", false);
			Log.e(TAG, "CCNx failed to start. START_ALL_ERROR");
			break;
		}
	}
	
	
	static String TAG = "OHMAGE_NDN_MANAGER_CLASS";
	
	public final static String _mobility_data_stream = 
			"MOBILITY_DATA_STREAM";
	public final static String _survey_response_stream = 
			"SURVEY_RESPONSE_STREAM";
	
	static CcndWrapper _ccnd_wrapper;
	
	private CCNxServiceControl _ccnx_service;
	
	private boolean _listening = false;

	private String _namespace;

	private String _remotehost;

	private int _remoteport;

	private String _pub_url;

	private String _rec_auth;
	
	private String _rec_url;

	private PDVInstance _pdv_instance;
	
	private PDCReceiver _pdv_receiver;

	private DataStream _stream;

	private static OhmagePDVManager _ohmage_NDN_manager;
	
	private static OhmagePDVManagerCallback _callback_interface;
	
	private static OhmageApplication _ohmage_application;
	
	private TelephonyManager telephonyManager = null;
	
	private String DEVICE_ID = null;

}