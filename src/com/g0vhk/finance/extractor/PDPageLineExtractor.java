package com.g0vhk.finance.extractor;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.CV_PI;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GRAY2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_PROBABILISTIC;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvSobel;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughLines2;
import static org.bytedeco.javacpp.opencv_imgproc.cvLine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class PDPageLineExtractor {
	final float SIMILAR_POINT__DISTANCE_THRESHOLD = 10.0f;
	final PDDocument document;
	
	public PDPageLineExtractor(PDDocument document)
	{
		this.document = document;
	}
	
	public void produceNoTextPage(int realPageIndex) throws IOException
	{
		PDFRenderer pdfRendererOld = new PDFRenderer(document);
	    BufferedImage bimOld = pdfRendererOld.renderImageWithDPI(realPageIndex, 300, ImageType.RGB);
	    String oldFileName = "cmab" + "-" + (realPageIndex + 1) + "_old.png";
	 	ImageIOUtil.writeImage(bimOld, oldFileName, 300);
	 	
 		PDDocument newDocument = new PDDocument();
 		newDocument.setResourceCache(document.getResourceCache());
 		newDocument.setDocumentInformation(document.getDocumentInformation());
 		Iterator<PDStream> oldStreams = document.getPage(realPageIndex).getContentStreams();
 		ArrayList<Object> newTokens = new ArrayList<Object>();
 		
 		
 		BoundedPDFTextStripper pdfStripper = new BoundedPDFTextStripper(0f, 503.2397408f, 591.5526758f, 671.9462443f);
 		Writer outputWriter = new StringWriter();
 		pdfStripper.setStartPage(realPageIndex + 1);
 		pdfStripper.setEndPage(realPageIndex + 1);
 		
        // Extract text for main document:
 		pdfStripper.writeText(document, outputWriter);
 		System.out.println(outputWriter.toString());
 		int k = 0;
 		
 		while (oldStreams.hasNext())
 		{
 			PDStream oldStream = oldStreams.next();
 			PDFStreamParser parser = new PDFStreamParser(oldStream);
 			parser.parse();  
 			List<COSBase> arguments = new ArrayList<COSBase>();
 	       //Object token = parser.parseNextToken();
 	      List<Object> tokens = parser.getTokens();
 	      for (int i = 0; i < tokens.size(); i++)
	        {
 	    	  	Object token = tokens.get(i);
 	    	  	if (token instanceof COSNull)
 	    	  	{
 	    	  		continue;
 	    	  		
 	    	  	}
 	    	   
 	            if (token instanceof Operator)
 	            {
 	            	Operator operator = (Operator) token;
 	            	String name = operator.getName();
 	            	if (! name.equals("Tj")  && ! name.equals("TJ"))
 		        	{
 	            		newTokens.addAll(arguments);
 	            		newTokens.add(operator);
 		        		
 		        		
 		        	}
 	            	else
 	            	{
 	            		for (Object argument : arguments)
 	            		{
 	            		//	System.out.print(argument);
 	            			
 	            		}
 	            		//System.out.println();
 	            		
 	            	}
 	                arguments = new ArrayList<COSBase>();
 	            }
 	            else
 	            {
 	            	arguments.add((COSBase) token);
 	            }

 	            
 	            token = parser.parseNextToken();
	        }
 		}
         
         PDStream newContents = new PDStream(document);
 		COSDictionary pageDict = document.getPage(realPageIndex).getCOSObject();
 		PDPage newPage = new PDPage(pageDict);
 		
 		System.out.println(newPage.getMediaBox().getHeight());
 		System.out.println(newPage.getMediaBox().getWidth());
 		OutputStream output = newContents.createOutputStream();
 		ContentStreamWriter writer = new ContentStreamWriter(output);
        writer.writeTokens(newTokens);
        output.close();
        newContents.addCompression();
        newPage.setContents(newContents);
 		newDocument.addPage(newPage);
 		PDFRenderer pdfRenderer = new PDFRenderer(newDocument);
 		int pageIndex = 0;
 	    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
 	    
 	    // suffix in filename will be used as the file format
 	    String fileName = "cmab" + "-" + (realPageIndex + 1) + ".png";

 		ImageIOUtil.writeImage(bim, fileName, 300);
		
	}
	
	private ArrayList<Pointer> detectLinesFromPage(int realPageIndex)
	{
		
		String fileName = "cmab" + "-" + (realPageIndex + 1) + ".png";
		String oldFileName = "cmab" + "-" + (realPageIndex + 1) + "_old.png";
		
 		System.out.println("Outline");
 		IplImage src = cvLoadImage(fileName, 0);
 		IplImage srcOld = cvLoadImage(oldFileName, 0);
 		IplImage dst = cvCreateImage(cvGetSize(src), src.depth(), 1);
 		IplImage dst2 = cvCreateImage(cvGetSize(src), src.depth(), 1);
 		IplImage colorDst = cvCreateImage(cvGetSize(src), src.depth(), 3);
        cvSobel(src, dst, 0, 1, 1);
        cvSobel(src, dst2, 1, 0, 1);
        //cvSaveImage("sobel_" + (realPageIndex + 1) + ".jpg", dst);
        //cvSaveImage("sobel_" + (realPageIndex + 1) + "_2.jpg", dst2);
 		cvCvtColor(srcOld, colorDst, CV_GRAY2BGR); 
 		StraightLineDetector s = new StraightLineDetector();
 		List<int[]> lines2 =  s.detectHorizontalLine(dst);
 		List<int[]> lines3 =  s.detectVerticalLine(dst2);
 		lines2.addAll(lines3);
 		CvMemStorage storage = cvCreateMemStorage(0);

 		//ArrayList<Pointer> lines = filterLines(seq);
 		CvScalar[] colors = new CvScalar[]{CV_RGB(255, 255, 0), CV_RGB(0, 255, 0), CV_RGB(255, 0, 255), CV_RGB(0, 0, 255), CV_RGB(255, 0, 0)}; 
 		/*
 		for (int i = 0; i < lines.size(); i++) {
            Pointer line = lines.get(i);
            CvPoint pt1  = new CvPoint(line).position(0);
            CvPoint pt2  = new CvPoint(line).position(1);
            cvLine(colorDst, pt1, pt2, colors[i % colors.length], 3, CV_AA, 0); // draw the segment on the image
        }*/
 		
 		for (int i = 0; i < lines2.size(); i++)
 		{
 			int[] line = lines2.get(i);
 			System.out.println("" + line[0] + "," + line[1] + "," + line[2] + "," + line[3]);
 			cvLine(colorDst, new CvPoint(line[0], line[1]), new CvPoint(line[2], line[3]), colors[i % colors.length], 3, CV_AA, 0);
 		}
 		
 		cvSaveImage("detection_" + (realPageIndex + 1) + ".jpg", colorDst);
 		colorDst.close();
 		src.close();
 		dst.close();
 		dst2.close();
 		srcOld.close();
 		return null;
		
	}
	
	public void extract(int realPageIndex) throws IOException
	{
		produceNoTextPage(realPageIndex);
		detectLinesFromPage(realPageIndex);
	}
	
	private ArrayList<Pointer> filterLines(CvSeq seq)
	{
		
		ArrayList<Pointer> lines = new ArrayList<Pointer>();
		
		//Filter non horizontal/vertical lines
		for (int i = 0; i < seq.total(); i++) {
 			Pointer line = cvGetSeqElem(seq, i);
 			CvPoint pt1  = new CvPoint(line).position(0);
            CvPoint pt2  = new CvPoint(line).position(1);
 			if (pt1.x() != pt2.x() && pt1.y() != pt2.y())
            {
            	//continue;
            }
 			lines.add(line);
 		}		
		//Removed similar or duplicated line
		while (true)
 		{
			
 			boolean anyRemoved = false;
 			ArrayList<Integer> indicesToBeRemoved = new ArrayList<Integer>();
 			int l = lines.size();
 			for (int i = 0; i < l; i++) {
 				for (int j = i + 1; j < l; j++)
 				{
 					if (isSimilarLine(lines.get(i), lines.get(j), SIMILAR_POINT__DISTANCE_THRESHOLD))
 					{
 						indicesToBeRemoved.add(j);
 						anyRemoved = true;	
 					}
 					
 				}
 				if (anyRemoved)
 				{
 					break;
 				}
 			}
 			
 			for (int index: indicesToBeRemoved)
 			{
 				System.out.println("Line removed");
 				lines.remove(index);
 			}
 			
 			if (! anyRemoved)
 			{
 				break;
 			}
 		}
		return lines;
	}
	

	
	private boolean isSimilarLine(Pointer line1, Pointer line2, float threshold)
	{
		CvPoint pt1  = new CvPoint(line1).position(0);
        CvPoint pt2  = new CvPoint(line2).position(0);
        CvPoint pt3  = new CvPoint(line1).position(1);
        CvPoint pt4  = new CvPoint(line2).position(1);
        double d1 = Math.sqrt(Math.pow(Math.abs(pt1.x() - pt2.x()), 2) + Math.pow(Math.abs(pt1.y() - pt2.y()), 2));
        double d2 = Math.sqrt(Math.pow(Math.abs(pt3.x() - pt4.x()), 2) + Math.pow(Math.abs(pt3.y() - pt4.y()), 2));
		return d1 <= threshold && d2 <= threshold; 
	}
}
