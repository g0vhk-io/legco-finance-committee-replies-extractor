package com.g0vhk.finance.extractor;

public class InitialReply {
	final InitialReplyHead head;
	final InitialReplySubHead subHead;
	final InitialReplyProgramme programme;
	
	public InitialReply(InitialReplyHead head, InitialReplySubHead subHead, InitialReplyProgramme programme, InitialReplyDirector director) {
		super();
		this.head = head;
		this.subHead = subHead;
		this.programme = programme;
	}
	
	
}
