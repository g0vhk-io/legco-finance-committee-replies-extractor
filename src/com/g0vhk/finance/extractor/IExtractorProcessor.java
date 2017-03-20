package com.g0vhk.finance.extractor;

public interface IExtractorProcessor {
	void processParagraph(String text);
	void processTable(String[][] cellTexts);
}
