package com.g0vhk.finance.extractor;

public class InitialReplyHead {
	final String text;
	final int index;
	
	public InitialReplyHead(String text, int index) {
		super();
		this.text = text;
		this.index = index;
	}
	
	
	public String getText() {
		return text;
	}
	

	public int getIndex() {
		return index;
	}
}
