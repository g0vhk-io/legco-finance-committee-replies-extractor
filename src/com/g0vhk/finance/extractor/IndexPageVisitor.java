package com.g0vhk.finance.extractor;

import java.util.List;

public class IndexPageVisitor implements IIndexPageVisitor {
	String html = "";
	
	
	@Override
	public void visitRows(List<IndexRow> rows) {
		// TODO Auto-generated method stub
		html += "<table border=\"1\"  cellspacing=\"0\" cellpadding=\"0\">";
		html += "<tr>";
		html += "<td>" + "答覆編號"+  "</td>";
		html += "<td>" + "問題編號"+  "</td>";
		html += "<td>" + "委員姓名"+  "</td>";
		html += "<td>" + "總目"+  "</td>";
		html += "<td>" + "綱領"+  "</td>";
		for (IndexRow row : rows)
		{
			html += "<tr>";
			html += "<td>" + "<a href=\"" + row.getSerialNo() + ".html\">" + row.getSerialNo() +  "</a></td>";
			html += "<td>" + row.getQuestionNo() +  "</td>";
			html += "<td>" + row.getMemberName() +  "</td>";
			html += "<td>" + row.getHead() +  "</td>";
			html += "<td>" + row.getProgrammeTitle().replaceAll("\n", "<br/>") +  "</td>";
			
			
			
			html += "</tr>";
		}
		html += "</table>";
	}
	
	public String getHtml()
	{
		return html;
		
	}

	@Override
	public void visitHeader(String header) {
		html += header.replaceAll("\n", "<br/>") + "<br/>";
		
	}

}
