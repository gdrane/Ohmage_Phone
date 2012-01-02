package org.ohmage.pdc;

public interface OhmagePDVManagerCallback {
	void postProcess(boolean status);
	void postProcess(String text, boolean show_longer);
	void postProcess(String text);
}
