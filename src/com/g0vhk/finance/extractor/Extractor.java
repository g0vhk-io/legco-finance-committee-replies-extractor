package com.g0vhk.finance.extractor;


import java.io.File;
import java.io.IOException;
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


public class Extractor {

	public static void main(String[] args) throws InvalidPasswordException, IOException, ParseException {
		Options options = new Options();
		options.addOption("f", "filename", true, "Source PDF File Name");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse( options, args);
 		File file = new File(cmd.getOptionValue("f"));
 		PDDocument document = PDDocument.load(file);
 		printBookmark(document.getDocumentCatalog().getDocumentOutline(), document);
	    PDPageLineExtractor extractor = new PDPageLineExtractor(document);
	    //extractor.extract(18);
	    for (int i = 0 ; i < 1; i++)
	    extractor.extract(i);
	    //extractor.extract(20);
	}
	
	public static void printBookmark( PDOutlineNode bookmark, PDDocument doc ) throws IOException
    {
        PDOutlineItem current = bookmark.getFirstChild();
        while( current != null )
        {
        	PDPage page = current.findDestinationPage(doc);
        	
           // System.out.println(current.getTitle() +","+ doc.getPages().indexOf(page));
            printBookmark( current, doc);
            current = current.getNextSibling();
        }
    }
}
