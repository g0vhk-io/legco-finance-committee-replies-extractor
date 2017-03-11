package com.g0vhk.finance.extractor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

public class StraightLineDetector {
	public List<int[]> detectVerticalLine(IplImage src)
	{
		ArrayList<int[]> lines = new ArrayList<int[]>();
		Mat raw = new Mat(src);
		int w = raw.arrayWidth();
		int h = raw.arrayHeight();
		UByteRawIndexer indexer = raw.createIndexer();
		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				int v = indexer.get(j, i);
				if (v != 0)
				{
					int start = j++;
					while (j < h)
					{
						v = indexer.get(j, i);
						if (v != 0)
						{
							j++;
						}
						else
						{
							break;
						}
					}
					int end = j - 1;
					int l = lines.size();
					boolean updated = false;
					if (l > 0)
					{
						int[] lastLine = lines.get(l - 1);
						if (start - lastLine[3] <= 30 && i == lastLine[0])
						{
							lines.set(l - 1, new int[] {i, lastLine[1], i, end});
							updated = true;
						}
					}

					if (! updated)
					{
						lines.add(new int[] { i, start, i, end});
					}
				}
			}
			
			
		}
		raw.close();
		return removeDuplicatedLines(lines);
	}
	
	public List<int[]> detectHorizontalLine(IplImage src)
	{
		ArrayList<int[]> lines = new ArrayList<int[]>();
		Mat raw = new Mat(src);
		int w = raw.arrayWidth();
		int h = raw.arrayHeight();
		UByteRawIndexer indexer = raw.createIndexer();
		for (int i = 0; i < h; i++)
		{
			for (int j = 0; j < w; j++)
			{
				int v = indexer.get(i, j);
				if (v != 0)
				{
					int start = j++;
					while (j < h)
					{
						v = indexer.get(i, j);
						if (v != 0)
						{
							j++;
						}
						else
						{
							break;
						}
					}
					int end = j - 1;
					int l = lines.size();
					boolean updated = false;
					if (l > 0)
					{
						int[] lastLine = lines.get(l - 1);
						if (start - lastLine[2] <= 30 && i == lastLine[1])
						{
							lines.set(l - 1, new int[] {lastLine[0], i, end, i});
							updated = true;
						}
					}

					if (! updated)
					{
						lines.add(new int[] { start, i, end, i});
					}
				}
			}
			
			
		}
		
		raw.close();
		return removeDuplicatedLines(lines);
	}
	
	private ArrayList<int[]> removeDuplicatedLines(ArrayList<int[]> lines)
	{
		ArrayList<int[]> newLines = new ArrayList<int[]>();
		newLines.addAll(lines);
		Iterator<int[]> it = newLines.iterator();
		
		
		while (it.hasNext())
		{
			int[] line1 = it.next();
			if (getLength(line1) <= 10)
			{
				it.remove();
			}
		}
		
		it = newLines.iterator();
		while (it.hasNext())
		{
			int[] line1 = it.next();
			Iterator<int[]> it2 = newLines.iterator();
			boolean shouldRemove = false;
			while (it2.hasNext())
			{
				int[] line2 = it2.next();
				if (line1 == line2)
				{
					break;
				}
				if (isSimilarLine(line1, line2, 30))
				{
					shouldRemove = true;
					break;
				}
			}
			if (shouldRemove)
			{
				it.remove();
			}
		}
		return newLines;
	}
	
	private boolean isSimilarLine(int[] line1, int[] line2, float threshold)
	{
		CvPoint pt1  = new CvPoint(line1[0], line1[1]);
        CvPoint pt2  = new CvPoint(line2[0], line2[1]);
        CvPoint pt3  = new CvPoint(line1[2], line1[3]);
        CvPoint pt4  = new CvPoint(line2[2], line2[3]);
        double d1 = Math.sqrt(Math.pow(Math.abs(pt1.x() - pt2.x()), 2) + Math.pow(Math.abs(pt1.y() - pt2.y()), 2));
        double d2 = Math.sqrt(Math.pow(Math.abs(pt3.x() - pt4.x()), 2) + Math.pow(Math.abs(pt3.y() - pt4.y()), 2));
		return d1 <= threshold && d2 <= threshold; 
	}
	
	private double getLength(int[] line1)
	{
        return Math.sqrt(Math.pow(Math.abs(line1[0] - line1[2]), 2) + Math.pow(Math.abs(line1[1] - line1[3]), 2));
	}
}
