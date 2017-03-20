package com.g0vhk.finance.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.commons.lang3.tuple.Pair;

public class ReplyBookmarkInfoExtractor {
	public List<ReplyBookmarkInfo> GetReplyBookmarkInfoByDocument(PDDocument doc) throws IOException
	{
		ArrayList<ReplyBookmarkInfo> list = new ArrayList<ReplyBookmarkInfo>();
		List<Pair<String, Integer>> pairs = processBookmark(doc.getDocumentCatalog().getDocumentOutline(), doc);
		int l = pairs.size();
		for (int i = 0; i < l; i++)
		{
			Pair<String, Integer> pair = pairs.get(i);
			if (i == l - 1)
			{
				list.add(new ReplyBookmarkInfo(pair.getLeft(), pair.getRight(), doc.getPages().getCount() - 1 ));
			}
			else
			{
				Pair<String, Integer> nextPair = pairs.get(i + 1);
				list.add(new ReplyBookmarkInfo(pair.getLeft(), pair.getRight(), nextPair.getRight() - 1));
			}
		}
		return list;
	}
	
	private List<Pair<String, Integer>> processBookmark( PDOutlineNode bookmark, PDDocument doc ) throws IOException
    {
		ArrayList<Pair<String, Integer>> output = new ArrayList<Pair<String, Integer>>();
        PDOutlineItem current = bookmark.getFirstChild();
        while( current != null )
        {
        	PDPage page = current.findDestinationPage(doc);
        	Pair<String, Integer> pair = Pair.of(current.getTitle(), (Integer)doc.getPages().indexOf(page));
        	output.add(pair);
            output.addAll(processBookmark( current, doc));
            current = current.getNextSibling();
        }
        
        return output;
    }
}
