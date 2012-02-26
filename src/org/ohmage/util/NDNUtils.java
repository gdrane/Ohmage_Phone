package org.ohmage.util;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bson.BSONEncoder;
import org.ccnx.ccn.KeyManager;
import org.ccnx.ccn.protocol.KeyLocator;
import org.ccnx.ccn.protocol.PublisherPublicKeyDigest;
import org.ohmage.pdc.OhmagePDVManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import edu.ucla.cens.pdc.libpdc.core.GlobalConfig;
import edu.ucla.cens.pdc.libpdc.core.PDCKeyManager;
import edu.ucla.cens.pdc.libpdc.exceptions.PDCEncryptionException;
import edu.ucla.cens.pdc.libpdc.util.EncryptionHelper;
import edu.ucla.cens.pdc.libpdc.util.Log;

public class NDNUtils {

	public static byte[] encryptConfigData(KeyLocator locator, String username, String password) {
		PublicKey public_key = null;
		final BSONEncoder encoder = new BSONEncoder();
		final GlobalConfig config = GlobalConfig.getInstance();
		final PDCKeyManager keymgr = config.getKeyManager();
		final PublisherPublicKeyDigest digest = OhmagePDVManager.getConfigurationDigest();
		DBObject bsonData = new BasicDBObject();
		bsonData.put("username", username);
		bsonData.put("password", password);
		try {
			public_key = keymgr.getPublicKey(digest, locator);
			if (public_key == null) {
				Log.info("Public Key returned is null");
				return null;
			}
			return EncryptionHelper.encryptAsymData(public_key, encoder.encode(bsonData));
		} catch (IOException e) {
			Log.error("Got IOException inside NDNUtils");
			e.printStackTrace();
		} catch (PDCEncryptionException e) {
			Log.error("Got PDCEncryption Exception inside NDNUtils");
		}
		return null;
	}
	
	public static byte[] decryptConfigData(byte[] data) {
		GlobalConfig config = GlobalConfig.getInstance();
		KeyManager keymgr;
		PrivateKey private_key;
		Log.debug("Decrypting data using my key: ");

		keymgr = config.getKeyManager();
		assert keymgr != null;
		private_key = keymgr.getSigningKey(keymgr.getDefaultKeyID());
		
		assert private_key != null;

		try {
			return EncryptionHelper.decryptAsymData(private_key, data);
		} catch (PDCEncryptionException e) {
			Log.error("Got PDCEncryption Exception inside NDNUtils");
			e.printStackTrace();
		}
		return null;
	}

}
