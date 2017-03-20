package com.g0vhk.finance.extractor;

import java.io.IOException;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class BoundedPDFTextStripper extends PDFTextStripper {
	private final float x0, x1, y0, y1;

	public BoundedPDFTextStripper(float x0, float y0, float x1, float y1, int page) throws IOException {
		super();
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.setStartPage(page + 1);
		this.setEndPage(page + 1);
	}

	@Override
	protected void processTextPosition(TextPosition text) {
		
		float x = text.getX();
		float y = text.getY();
		
		if (x >= x0 && x <= x1 && y >= y0 && y <= y1)
		{
			super.processTextPosition(text);
			
		}
		
	}
}
