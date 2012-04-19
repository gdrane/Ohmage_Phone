package edu.ucla.cens.pdc.libpdc.core;

import org.ccnx.ccn.protocol.ContentName;
import org.ccnx.ccn.protocol.Interest;

public class KeyStoreCommand extends GenericCommand {

	public KeyStoreCommand() {
		super(KEY_STORE_CMD);
	}
	
	@Override
	boolean processCommand(ContentName postfix, Interest interest) {
		if(postfix.stringComponent(1).equals(GET))
			return handleGetCmd(postfix, interest);
		if(postfix.stringComponent(1).equals(PUT))
			return handlePutCmd(postfix, interest);
		return false;
	}
	
	private boolean handleGetCmd(ContentName postfix, Interest interest) {
		return false;
	}
	
	private boolean handlePutCmd(ContentName postfix, Interest interest) {
		return false;
	}
	
	private static final String GET = "get";
	private static final String PUT = "put";
	private static final String KEY_STORE_CMD = "key_store";
	
}
