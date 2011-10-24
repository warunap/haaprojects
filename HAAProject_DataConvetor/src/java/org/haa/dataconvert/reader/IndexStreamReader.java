package org.haa.dataconvert.reader;

import java.io.IOException;
import java.io.InputStream;

import org.haa.dataconvert.dom.Component;

/**
 * NOTICE: Used only when the data has not line flag.
 * 
 * @author Geln Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class IndexStreamReader extends IndexReader {

	private InputStream inputStream;

	public IndexStreamReader(InputStream inputStream, String charset) throws IOException {
		setInputStream(inputStream);
		setCharset(charset);

		/* the last */
		setReader(inputStream, charset);
	}

	public IndexStreamReader(InputStream inputStream, Component component) throws IOException {
		setCharset(component.getCharset());
		setHasLineFlag(component.isHasLineFlag());
		setLineSize(component.getLineSize());
		/* the last */
		setReader(inputStream, component.getCharset());
	}

	public IndexStreamReader() {
	}

	public void close() {
		super.close();
		if (inputStream != null)
			try {
				inputStream.close();
			} catch (IOException e) {
			}
	}

	protected String cutLine() throws IOException {
		byte[] barr = new byte[lineSize];
		int flag = inputStream.read(barr);
		if (flag == -1) {
			return null;
		}
		return new String(barr, charset);
	}

	public void setReader(InputStream inputStream, String encode) throws IOException {
		setInputStream(inputStream);
		setCharset(encode);
		if (hasLineFlag) {
			setReader(streamToBufferReader(inputStream, charset));
		}
	}

	protected void checkSource() throws IOException {
		if (inputStream == null) {
			throw new IOException("Input stream not initialized!");
		}
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
