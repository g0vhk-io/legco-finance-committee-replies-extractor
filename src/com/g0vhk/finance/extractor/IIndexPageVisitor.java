package com.g0vhk.finance.extractor;

import java.util.List;

public interface IIndexPageVisitor {
	void visitRows(List<IndexRow> rows);
	void visitHeader(String header);
}
