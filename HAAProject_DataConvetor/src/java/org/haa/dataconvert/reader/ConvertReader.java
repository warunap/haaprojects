package org.haa.dataconvert.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ConvertReader implements Serializable {

	private static final Log LOG = LogFactory.getLog(ConvertReader.class);

	protected BufferedReader reader = null;

	protected String charset;

	protected boolean hasLineFlag = true;

	protected int lineSize = 0;

	protected boolean end = false;

	public ConvertReader() {
	}

	public ConvertReader(BufferedReader reader) throws IOException {
		setReader(reader);
	}

	/**
	 * if has line flag, invoke the orignal readLine() function. Otherwise, all content is in the same line, read a
	 * fragment conent with the line size.<br>
	 * when has not line flag, replace all chars '\r' and '\r' with ''.
	 */
	protected String readBufferLine() throws IOException {
		if (end) {
			return null;
		}
		String line;
		if (hasLineFlag) {
			line = reader.readLine();
		} else {
			line = cutLine();
			if (line != null) {
				if (line.contains("\r")) {
					LOG.warn("remove \\r from line!");
					line = line.replace("\r", "");
				}
				if (line.contains("\n")) {
					LOG.warn("remove \\n from line!");
					line = line.replace("\n", "");
				}
			}
		}

		if (StringUtils.isEmpty(line))
			return null;
		return line;
	}

	protected String cutLine() throws IOException {
		char[] cbuf = new char[lineSize];
		int lineByteCount = 0;
		int lineCharIndex = 0;
		for (; lineCharIndex < lineSize && lineByteCount < lineSize; lineCharIndex++) {
			int c = reader.read();
			if (c == -1) {
				end = true;
				break;
			}
			char[] cs = Character.toChars(c);
			for (char d : cs) {
				cbuf[lineCharIndex] = d;
			}
			lineByteCount += new String(cs).getBytes(charset).length;
		}

		return new String(cbuf, 0, lineCharIndex);
	}

	public boolean end() {
		return end;
	}

	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}

	public void setReader(InputStream inputStream, String encode) throws IOException {
		InputStreamReader inr = new InputStreamReader(inputStream, encode);
		setReader(new BufferedReader(inr));
	}

	public void setReader(BufferedReader reader) throws IOException {
		if (reader == null)
			throw new IOException("reader can't be null!");
		this.reader = reader;
	}

	public final void setHasLineFlag(boolean hasLineFlag) {
		this.hasLineFlag = hasLineFlag;
	}

	public final void setLineSize(int lineSize) throws IOException {
		if (lineSize < 0)
			throw new IOException("Line size must be great than 0!");
		this.lineSize = lineSize;
	}

	public final void setCharset(String charset) {
		this.charset = charset;
	}

	protected static BufferedReader streamToBufferReader(InputStream is, String charset) throws IOException {
		InputStreamReader inr = new InputStreamReader(is, charset);
		BufferedReader reader = new BufferedReader(inr);
		return reader;
	}
}
