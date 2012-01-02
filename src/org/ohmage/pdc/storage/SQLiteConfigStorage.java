package org.ohmage.pdc.storage;

import java.io.IOException;

import edu.ucla.cens.pdc.libpdc.iConfigStorage;

public class SQLiteConfigStorage implements iConfigStorage {
	
	@Override
	public byte[] loadEntry(String group, String name) throws IOException {
		return null;
	}

	@Override
	public void saveEntry(String obj_group, String obj_key, byte[] byte_out)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEntry(String group, String name) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
