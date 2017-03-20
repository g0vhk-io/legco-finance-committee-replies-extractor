package com.g0vhk.finance.extractor;

public class IndexRow {
	final String serialNo;
	final String questionNo;
	final String memberName;
	final int head;
	final String programmeTitle;
	
	public IndexRow(String serialNo, String questionNo, String memberName, int head, String programmeTitle) {
		super();
		this.serialNo = serialNo;
		this.questionNo = questionNo;
		this.memberName = memberName;
		this.head = head;
		this.programmeTitle = programmeTitle;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public String getQuestionNo() {
		return questionNo;
	}

	public String getMemberName() {
		return memberName;
	}

	public int getHead() {
		return head;
	}

	public String getProgrammeTitle() {
		return programmeTitle;
	}
	
	
}
