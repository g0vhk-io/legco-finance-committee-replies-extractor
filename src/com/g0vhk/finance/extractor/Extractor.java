package com.g0vhk.finance.extractor;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

import freemarker.template.TemplateException;


public class Extractor {

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("f", "filename", true, "Source PDF File Name");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);
 		File file = new File(cmd.getOptionValue("f"));
 		PDDocument document = PDDocument.load(file);
 		PDDocument document2 = PDDocument.load(file);
 		
	    PDPageLineExtractor extractor = new PDPageLineExtractor(document);
	    ReplyBookmarkInfoExtractor bookmarkInfoExtract = new ReplyBookmarkInfoExtractor();
	    List<ReplyBookmarkInfo> bookmarkInfos = bookmarkInfoExtract.GetReplyBookmarkInfoByDocument(document);
	    SingleReplyExtractor singleReplyExtractor = new SingleReplyExtractor();
	    /*
	    for (ReplyBookmarkInfo info: bookmarkInfos)
	    {
	    	singleReplyExtractor.extract(document, info, document2, null);
	    	System.out.println("" + info.getSerialNo() + "," + info.getStartPage() + "," + info.getEndPage() );
	    }*/
	    IndexPageExtractorProcessor indexPageProcessor = new IndexPageExtractorProcessor();
	    singleReplyExtractor.extract(document, bookmarkInfos.get(0), document2, indexPageProcessor);
	    IndexPageVisitor visitor = new IndexPageVisitor();
	    indexPageProcessor.getIndexPage().visit(visitor);
	    Writer out = new FileWriter(new File("output/" + bookmarkInfos.get(0).serialNo + ".html"));
		out.write(visitor.getHtml());
		out.close();
		
		for (ReplyBookmarkInfo bookmarkInfo: bookmarkInfos.subList(1, 4))
		{
			ReplyPageExtractorProcessor replyPageProcessor = new ReplyPageExtractorProcessor();
			singleReplyExtractor.extract(document, bookmarkInfo, document2, replyPageProcessor);
			Writer pageOut = new FileWriter(new File("output/" + bookmarkInfo.serialNo + ".html"));
			pageOut.write(visitor.getHtml());
			pageOut.close();
		}
	    //extractor.extract(18);
	    //for (int i = 0 ; i < 1; i++)
	    //extractor.extract(i);
	    //extractor.extract(20);
	}
	

}
