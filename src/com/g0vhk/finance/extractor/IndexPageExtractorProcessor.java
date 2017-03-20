package com.g0vhk.finance.extractor;

public class IndexPageExtractorProcessor implements IExtractorProcessor {
	final IndexPage indexPage;
	
	public IndexPageExtractorProcessor()
	{
		this.indexPage = new IndexPage();
	}
	
	@Override
	public void processParagraph(String text) {
		text = text.trim();
		if (text.length() == 0)
		{
			return;
		}
		indexPage.setHeader(text);
		System.out.println(text);
		
	}

	@Override
	public void processTable(String[][] cellTexts) {
		for (String[] row: cellTexts)
		{
			if (row == cellTexts[0])
				continue;
			if (row[0].length() == 0)
			{
				indexPage.UpdateLastRow(row);
				
			}
			else
			{
				indexPage.AddRow(new IndexRow(row[0], row[1], row[2], Integer.parseInt(row[3]), row[4]));
			}
		}
	}
	
	public IndexPage getIndexPage()
	{
		return indexPage;
	}

}
