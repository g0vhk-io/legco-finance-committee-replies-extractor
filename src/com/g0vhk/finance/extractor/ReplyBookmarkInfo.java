package com.g0vhk.finance.extractor;

public class ReplyBookmarkInfo {
	final String serialNo;
	final int startPage;
	final int endPage;
	
	public ReplyBookmarkInfo(String serialNo, int startPage, int endPage)
	{
		this.serialNo = serialNo;
		this.startPage = startPage;
		this.endPage = endPage;
	}
	
	public int getStartPage()
	{
		return startPage;
	}
	
	public int getEndPage()
	{
		return endPage;
	}
	
	public String getSerialNo()
	{
		return serialNo;
	}
}
