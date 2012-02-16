/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucla.cens.pdc.libpdc.core;

import edu.ucla.cens.pdc.libpdc.datastructures.Authenticator;
import edu.ucla.cens.pdc.libpdc.Constants;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCDatabaseException;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCEncryptionException;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCException;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCParseException;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCTransmissionException;
import java.io.IOException;
import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.Interest;
import org.ccnx.ccn.protocol.MalformedContentNameStringException;
import edu.ucla.cens.pdc.libpdc.stream.DataStream;
import edu.ucla.cens.pdc.libpdc.datastructures.StreamInfo;
import edu.ucla.cens.pdc.libpdc.stream.DataEncryptor;
import edu.ucla.cens.pdc.libpdc.transport.CommunicationHelper;
import edu.ucla.cens.pdc.libpdc.transport.PDCPublisher;
import edu.ucla.cens.pdc.libpdc.transport.PDCReceiver;
import android.util.Log;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Arrays;
import org.ccnx.ccn.config.SystemConfiguration;
import org.ccnx.ccn.impl.CCNFlowControl.SaveType;
import org.ccnx.ccn.impl.support.DataUtils;
import org.ccnx.ccn.io.content.PublicKeyObject;
import org.ccnx.ccn.protocol.CCNTime;
import org.ccnx.ccn.protocol.ContentObject;
import org.ccnx.ccn.protocol.KeyLocator;
import org.ccnx.ccn.protocol.PublisherID;
import org.ccnx.ccn.protocol.PublisherPublicKeyDigest;
/**
 *
 * @author Derek Kulinski
 */
public class StreamControlCommands extends StreamCommand {
	StreamControlCommands()
			throws MalformedContentNameStringException
	{
		super(Constants.STR_CONTROL);
	}

	private boolean processSetup(DataStream ds, Interest interest)
	{
		final GlobalConfig config = GlobalConfig.getInstance();
		final PDCPublisher publisher = ds.getPublisher();
		ContentName name;
		ContentObject co;

		if (publisher == null) {
			Log.w(TAG, "Got setup request, while a publisher wasn't set up");
			return false;
		}
		try {
			ds.getTransport().publishACK(interest);
		}
		catch (PDCTransmissionException ex) {
			throw new RuntimeException(
					"Error while trying to respond to the request with an ACK", ex);
		}

		Log.v(TAG, "### REQUESTING AUTHENTICATOR ### (Step 6)");

		try {
			name = publisher.uri.append(ds.app.getAppName()).append(ds.data_stream_id).
					append(Constants.STR_CONTROL).append("authenticator").append(config.
					getRoot());

			co = config.getCCNHandle().get(name, SystemConfiguration.MAX_TIMEOUT);
			if (co == null) {
				Log.e(TAG, "Got no response when requesting authenticator");
				return false;
			}

			//Verify if data is in check
			if (!co.verify(config.getKeyManager())) {
				Log.e(TAG, "Authenticator content fails data verification");
				return false;
			}

			final byte[] data = ds.getTransport().getEncryptor().decryptAsymData(co.
					content());
			final Authenticator auth = new Authenticator().fromBSON(data);

			if (!Arrays.equals(auth.getKeyDigest(), co.signedInfo().getPublisher())) {
				Log.e(TAG, 
						"Tempering detected! Data is signed with different key than one stored in authenticator");
				return false;
			}

			if (!auth.getAuthenticator().equals(publisher.getAuthenticator())) {
				Log.e(TAG, "Authenticator value doesn't match");
				return false;
			}

			Log.i(TAG, "### AUTHENTICATOR RECEIVED IS CORRECT ### (Step 6)");

			Log.i(TAG, "Adding key " + DataUtils.base64Encode(auth.getKeyDigest(),
					PublisherID.PUBLISHER_ID_LEN * 2) + " to trusted keys.");
			config.getKeyManager().authenticateKey(auth.getKeyDigest());
			
			Log.i(TAG, "### ASKING FOR STREAM INFORMATION ### (Step 7)");
			
			name = publisher.uri.append(ds.data_stream_id).append(Constants.STR_CONTROL).
					append("stream_info").append(config.getRoot());

			co = config.getCCNHandle().get(name, 
					SystemConfiguration.LONG_TIMEOUT);
			if (co == null) {
				Log.e(TAG, "Got no response when requesting authenticator");
				return false;
			}
			Log.i(TAG, "Received Content Object is : " + co);
			//Verify if data is in check
			if (!co.verify(config.getKeyManager())) {
				Log.e(TAG, "Authenticator content fails data verification");
				return false;
			}

			final byte[] stream_data = ds.getTransport().getEncryptor().
					decryptAsymData(co.content());
			
			ds.getTransport().getEncryptor().setDecryptKey(stream_data);
			ds.getTransport().getEncryptor().setEncryptKey(stream_data);
			
			StreamInfo si = new StreamInfo(ds);
			ds.updateStreamSetupStatus(si);
			

			return true;
		}
		catch (GeneralSecurityException ex) {
			Log.e(TAG, "Problem while trying to verify data signature: " + ex.
					getLocalizedMessage());
		}
		catch (IOException ex) {
			Log.e(TAG, "Communication problem: " + ex.getLocalizedMessage());
		}
		catch (PDCParseException ex) {
			Log.e(TAG, "Unable to parse data (malformaed string?): " + ex.
					getLocalizedMessage());
		}
		catch (PDCEncryptionException ex) {
			Log.e(TAG, "Unable to decrypt message: " + ex.getLocalizedMessage());
		}
		catch (MalformedContentNameStringException ex) {
			throw new Error("Unable to form CCN name", ex);
		}

		return false;
	}

	boolean processAuthenticator(DataStream ds, ContentName remainder,
			Interest interest)
	{
		final GlobalConfig config = GlobalConfig.getInstance();
		PDCReceiver receiver;
		Authenticator auth;
		byte[] encrypted;
		ContentName receiver_uri = remainder.subname(4, remainder.count() - 4);
		Log.i(TAG, "Receiver URI extracted from the interest : " + receiver_uri);
		receiver = ds.name2receiver(receiver_uri);
		if (receiver == null) {
			Log.i(TAG, "Interest for authenticator for unknown " + receiver_uri.
					toURIString());
			return false;
		}

		Log.i(TAG, "### RESPONDING TO AUTHENTICATOR REQUEST ### (Step 6)");

		try {
			auth = new Authenticator(ds, receiver);
		}
		catch (PDCException ex) {
			Log.i(TAG, "No authenticator available: " + ex.getMessage());
			return false;
		}
		try {
			final DataEncryptor encryptor = ds.getTransport().getEncryptor();
			encrypted = encryptor.encryptAsymData(receiver, auth);
		}
		catch (PDCEncryptionException ex) {
			Log.e(TAG, "Error while encrypting data: " + ex.getMessage());
			return false;
		}

		try {
			Log.v(TAG, "Sending authenticator, publisher digest : " + ds.getTransport().getEncryptor().getStreamKeyDigest());
			return CommunicationHelper.publishEncryptedData(config.getCCNHandle(),
					interest, ds.getTransport().getEncryptor().getStreamKeyDigest(),
					encrypted, 1);
		}
		catch (PDCTransmissionException ex) {
			Log.e(TAG, "Error while transmitting data: " + ex.getMessage());
			return false;
		}
	}

	boolean processStreamInfo(DataStream ds, ContentName remainder,
			Interest interest)
	{
		final GlobalConfig config = GlobalConfig.getInstance();
		PDCReceiver receiver;
		StreamInfo si;
		byte[] encrypted;
		ContentName receiver_uri = remainder.subname(4, remainder.count() - 4);
		receiver = ds.name2receiver(receiver_uri);
		if (receiver == null) {
			Log.w(TAG, receiver_uri.toURIString() + " is an unknown URI");
			return false;
		}

		si = new StreamInfo(ds);
		ds.updateStreamSetupStatus(si);
		try {
			encrypted = ds.getTransport().getEncryptor().encryptAsymData(receiver, si);
		}
		catch (PDCEncryptionException ex) {
			Log.e(TAG, "Unable to encrypt stream info: " + ex.getMessage());
			return false;
		}

		Log.i(TAG, "### SENDING STREAMINFO ### (Step 7)");

		assert encrypted != null;

		try {
			return CommunicationHelper.publishEncryptedData(config.getCCNHandle(),
					interest, ds.getTransport().getEncryptor().getStreamKeyDigest(),
					encrypted, 1);
		}
		catch (PDCTransmissionException ex) {
			Log.e(TAG, "Unable to transmit data: " + ex.getMessage());
			return false;
		}
	}

	boolean processKey(DataStream ds, ContentName receiver_uri, Interest interest)
	{
		final GlobalConfig config = GlobalConfig.getInstance();
		final PDCKeyManager keymgr = config.getKeyManager();
		final PublisherPublicKeyDigest digest = ds.getTransport().getEncryptor().
				getStreamKeyDigest();
		final PublicKey key = keymgr.getPublicKey(digest);
		final CCNTime version = keymgr.getKeyVersion(digest);

		assert key != null;
		assert version != null;

		Log.i(TAG, "### GOT KEY REQUEST; SENDING MY KEY ### (" + digest + ")");

		final KeyLocator locator = new KeyLocator(interest.name(), digest);

		try {
			PublicKeyObject pko = new PublicKeyObject(interest.name(), key,
					SaveType.RAW, digest, locator, config.getCCNHandle());
			pko.getFlowControl().disable();
			pko.save(version, interest);
			pko.close();

			return true;
		}
		catch (IOException ex) {
			Log.e(TAG, "Unable to send the key: " + ex.getLocalizedMessage());
		}

		return false;
	}

	boolean processLast(DataStream ds, Interest interest)
	{
		final GlobalConfig config = GlobalConfig.getInstance();
		final PublisherPublicKeyDigest publisher = ds.getTransport().getEncryptor().
				getStreamKeyDigest();
		String last;

		try {

			last = ds.getStorage().getLastEntry();

			//in case of no data, send empty response
			if (last == null)
				last = "";

			return CommunicationHelper.publishUnencryptedData(config.getCCNHandle(),
					interest, publisher, last.getBytes(), 1);
		}
		catch (PDCTransmissionException ex) {
			Log.e(TAG, "Unable to send data: " + ex.getLocalizedMessage());
		}
		catch (PDCDatabaseException ex) {
			Log.e(TAG, "Unable to access the data: " + ex.getLocalizedMessage());
		}
		return false;
	}

	@Override
	boolean processCommand(DataStream ds, ContentName postfix, Interest interest)
	{
		Log.d(TAG, "Got stream interest for: " + postfix.toString());

		if (postfix.count() == 0) {
			Log.e(TAG, "No subcommand!");

			return false;
		}

		String command = postfix.stringComponent(3);

		try {

			if (command.equals("setup"))
				return processSetup(ds, interest);
			else if (command.equals("authenticator"))
				return processAuthenticator(ds, postfix, interest);
			else if (command.equals("stream_info"))
				return processStreamInfo(ds, postfix, interest);
			else if (command.equals("key"))
				return processKey(ds, postfix, interest);
			else if (command.equals("pull"))
				return ds.getTransport().pullInterestHandler(interest);
			else if (command.equals("list"))
				return ds.getTransport().publishRecordList(interest, postfix);
			else if (command.equals("last"))
				return processLast(ds, interest);
			else {
				Log.i(TAG, "Invalid request: " + interest.name().toURIString());

				return false;
			}
		}
		catch (Exception ex) {
			throw new Error("Got problem", ex);
		}
	}
	
	private final static String TAG = "StreamControlCommandClass";
}
