package org.haa.dataconvert.reader;

import java.io.BufferedReader;
import java.io.IOException;

import org.haa.dataconvert.dom.Component;

/**
 * @author Geln Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class IndexReader extends ConvertReader {

	private String preLine;

	private boolean backspace = false;

	private ReadStatus readStatus = new ReadStatus();

	public IndexReader(BufferedReader reader) throws IOException {
		setReader(reader);
	}

	public IndexReader(BufferedReader reader, Component component) throws IOException {
		setReader(reader);
		setCharset(component.getCharset());
		setHasLineFlag(component.isHasLineFlag());
		setLineSize(component.getLineSize());
	}

	public IndexReader() {
	}

	public final String readLine() throws IOException {
		try {
			if (backspace) {
				backspace = false;
				return preLine;
			}
			readStatus.increaseLineNum();
			preLine = readBufferLine();
			return preLine;
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	public final String popTopLine() throws IOException {
		try {
			if (backspace) {
				return preLine;
			}
			readStatus.increaseLineNum();
			preLine = readBufferLine();
			backspace = true;
			return preLine;
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	public void back() {
		backspace = true;
	}

	public long lineNum() {
		return readStatus.lineNum();
	}
}
