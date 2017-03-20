package com.g0vhk.finance.extractor;

import java.util.ArrayList;

public class IndexPage {
	final ArrayList<IndexRow> rows;
	String header;
	public IndexPage()
	{
		this.rows = new ArrayList<IndexRow>();
	}
	
	public void AddRow(IndexRow row)
	{
		this.rows.add(row);
	}
	
	public void UpdateLastRow(String[] row)
	{
		int l = rows.size();
		IndexRow last = rows.get(l - 1);
		this.rows.set(l - 1, new IndexRow(last.getSerialNo(), last.getQuestionNo(), last.getMemberName(), last.getHead(), last.getProgrammeTitle() + "\n" + row[4]));
	}
	
	public void setHeader(String header)
	{
		this.header = header;
	}
	
	public void visit(IIndexPageVisitor visitor)
	{
		visitor.visitHeader(header);
		visitor.visitRows(rows);
	}
}
