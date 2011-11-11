package org.haaproject.converter.reader;

import org.haaproject.converter.dom.Line;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ReadStatus {

	private int batchCount = 0;

	public int lineNum = 0;

	private Line line;

	public int getBatchCount() {
		return batchCount;
	}

	public void increaseBatchCount() {
		this.batchCount += 1;
	}

	public void increaseLineNum() {
		this.lineNum += 1;
	}

	public int lineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

}
