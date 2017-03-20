package com.g0vhk.finance.extractor;

public class InitialReplyBlock {
	final String[][] cellTexts;
	final String paragraphText;
	
	public InitialReplyBlock(String[][] cellTexts, String paragraphText) {
		super();
		this.cellTexts = cellTexts;
		this.paragraphText = paragraphText;
	}

	public String[][] getCellTexts() {
		return cellTexts;
	}

	public String getParagraphText() {
		return paragraphText;
	}
	
	public String toHtml()
	{
		if (paragraphText != null)
		{
			return paragraphText;
		}
		
		String tableHtml = "<table>\n";
		for (String[] row: cellTexts)
		{
			tableHtml += "<tr>\n";
			for (String cellText: row)
			{
				tableHtml += "<td>\n";
				tableHtml += cellText;
				tableHtml += "</td>\n";
			}
			tableHtml += "</tr>\n";
		}
		tableHtml += "</tr>\n";
		return tableHtml;
	}
}
