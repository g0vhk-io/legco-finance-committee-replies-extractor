package com.g0vhk.finance.extractor;

public class ReplyPageExtractorProcessor implements IExtractorProcessor {
	InitialReply reply;
	@Override
	public void processParagraph(String text) {
		text = text.trim();
		if (text.length() == 0)
		{
			return;
		}
		
		//reply = new InitialReply()
		System.out.println(text);
	}

	@Override
	public void processTable(String[][] cellTexts) {
		// TODO Auto-generated method stub

	}
	
	public InitialReply getReply()
	{
		return reply;
	}

}
