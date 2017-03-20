package com.g0vhk.finance.extractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.bytedeco.javacpp.opencv_core.CvPoint;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class SingleReplyExtractor {
	public void extract(PDDocument doc, ReplyBookmarkInfo info, PDDocument doc2, IExtractorProcessor processor) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		String templateString = "${html}";
		Template temp = new Template("templateName", new StringReader(templateString), cfg);
		Map root = new HashMap();
		String html = "";

		for (int i = info.startPage; i <= info.endPage; i++) {
			System.out.println("Page=" + i);
			float width = doc.getPage(info.startPage).getMediaBox().getWidth();
			float height = doc.getPage(info.startPage).getMediaBox().getHeight();

			PDPageLineExtractor lineExtractor = new PDPageLineExtractor(doc);
			List<int[]> lines = lineExtractor.extract(i);
			ArrayList<int[]> tableRegions = new ArrayList<int[]>();
			for (int[] line1 : lines) {
				for (int[] line2 : lines) {
					if (line2 != line1) {
						if (Math.abs(line1[0] - line2[0]) <= 10 && Math.abs(line1[1] - line2[1]) <= 10) {
							boolean flag = findLine(new int[] { line2[2], line2[3], line1[2], line2[3] }, lines);
							boolean flag2 = findLine(new int[] { line1[2], line1[3], line1[2], line2[3] }, lines);
							if (flag == true && flag2 == true) {
								/*
								 * System.out.println("find intersection " +
								 * flag + " " + flag2);
								 * System.out.println(Arrays.toString(line1));
								 * System.out.println(Arrays.toString(line2));
								 */
								tableRegions.add(new int[] { line1[0], line1[1], line1[2], line2[3] });
							}
						}

					}
				}
			}

			float top = 0;
			float bottom = height;
			float p = top;
			for (int[] table : tableRegions) {
				ArrayList<Integer> ys = new ArrayList<Integer>();
				ArrayList<Integer> xs = new ArrayList<Integer>();
				for (int[] line1 : lines) {
					if (line1[1] == line1[3] && Math.abs(line1[0] - line1[2]) >= 100)
					{
						if (line1[1] >= table[1] && line1[3] <= table[3])
						{
							ys.add(line1[1]);
						}
					}
					if (line1[0] == line1[2] && Math.abs(line1[1] - table[1]) <= 20 &&  Math.abs(line1[3] - table[3]) <= 20)
					{
						if (line1[0] >= table[0] && line1[2] <= table[2])
						{
							xs.add(line1[0]);
						}
						
					}
				}

				BoundedPDFTextStripper stripper = new BoundedPDFTextStripper(0, p, width, table[1], i);
				String text = stripper.getText(doc2);
				
				String tableRowHtml = "";
				ArrayList<String[]> cellTexts = new ArrayList<String[]>();
				for (int k = 0; k <ys.size() - 1; k++)
				{
					int yTop = ys.get(k);
					int yBottom = ys.get(k + 1);
					
					//System.out.println("y=" + yTop + ",y2=" + yBottom);
					tableRowHtml += "<tr>"; 
					ArrayList<String> rowCellTexts = new ArrayList<String>();
					for (int m = 0; m <xs.size() - 1; m++)
					{
						int xLeft = xs.get(m);
						int xRight = xs.get(m + 1);
						BoundedPDFTextStripper stripper2 = new BoundedPDFTextStripper(xLeft, yTop, xRight, yBottom, i);
						String text2 = stripper2.getText(doc2);
						tableRowHtml += "<td>" + text2.replaceAll("\n", "<br/>") + "</td>\n";
						rowCellTexts.add(text2.trim());
					}
					cellTexts.add(rowCellTexts.toArray(new String[rowCellTexts.size()]));
					tableRowHtml += "</tr>"; 
				}
				processor.processParagraph(text);
				processor.processTable(cellTexts.toArray(new String[cellTexts.size()][]));
				html += text.replaceAll("\n", "<br/>");
				html += "<table border=\"1\">" + tableRowHtml + "</table>";
				//System.out.println("from " + p + " to " + table[1]);
				p = table[3];
			}
			if (p < bottom) {
				BoundedPDFTextStripper stripper = new BoundedPDFTextStripper(0, p, width, bottom, i);
				String text = stripper.getText(doc2);
				processor.processParagraph(text);
				html += text.replaceAll("\n", "<br/>");
				System.out.println("from " + p + " to " + bottom);
			}

		}
		root.put("html", html);
		Writer out = new FileWriter(new File("output/" + info.serialNo + ".html"));
		temp.process(root, out);
		out.close();

	}

	private boolean findLine(int[] target, List<int[]> lines) {
		for (int[] line : lines) {
			if (isSimilarLine(target, line, 20)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSimilarLine(int[] line1, int[] line2, float threshold) {
		CvPoint pt1 = new CvPoint(line1[0], line1[1]);
		CvPoint pt2 = new CvPoint(line2[0], line2[1]);
		CvPoint pt3 = new CvPoint(line1[2], line1[3]);
		CvPoint pt4 = new CvPoint(line2[2], line2[3]);
		double d1 = Math.sqrt(Math.pow(Math.abs(pt1.x() - pt2.x()), 2) + Math.pow(Math.abs(pt1.y() - pt2.y()), 2));
		double d2 = Math.sqrt(Math.pow(Math.abs(pt3.x() - pt4.x()), 2) + Math.pow(Math.abs(pt3.y() - pt4.y()), 2));
		return d1 <= threshold && d2 <= threshold;
	}
}
