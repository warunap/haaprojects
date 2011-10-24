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
public class BatchStreamReader extends BatchReader {

	private InputStream inputStream;

	public BatchStreamReader(InputStream inputStream, long batchSize, String charset) throws IOException {
		setInputStream(inputStream);
		setBatchSize(batchSize);
		/* the last */
		setReader(inputStream, charset);
	}

	public BatchStreamReader(InputStream inputStream, long batchSize, Component component) throws IOException {
		setCharset(component.getCharset());
		setHasLineFlag(component.isHasLineFlag());
		setLineSize(component.getLineSize());

		setInputStream(inputStream);
		setBatchSize(batchSize);
		/* the last */
		setReader(inputStream, component.getCharset());
	}

	public BatchStreamReader() {
	}

	public void close() {
		super.close();
		if (inputStream != null)
			try {
				inputStream.close();
			} catch (IOException e) {
			}
	}

	protected void resetReadMark() throws IOException {
		if (hasLineFlag) {
			super.resetReadMark();
		} else {
			try {
				inputStream.reset();
			} catch (IOException e) {
				if (!"Stream not marked".equals(e.getMessage()) && !"Resetting to invalid mark".equals(e.getMessage())
						&& !"mark/reset not supported".equals(e.getMessage()))
					throw e;
			}
		}
	}

	protected void setReadMark() throws IOException {
		try {
			if (hasLineFlag) {
				super.setReadMark();
			} else {
				inputStream.mark(10);
			}
		} catch (IOException e) {
			if (!"mark/reset not supported".equals(e.getMessage()))
				throw e;
		}
	}

	protected String cutLine() throws IOException {
		if (hasLineFlag) {
			return super.cutLine();
		} else {
			byte[] barr = new byte[lineSize];
			int flag = inputStream.read(barr);
			if (flag == -1) {
				return null;
			}
			return new String(barr, charset);
		}
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
