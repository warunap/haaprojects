/**
 * $Revision: 1.3 $
 * $Author: geln_yang $
 * $Date: 2011/09/02 13:14:51 $
 *
 * Author: Eric Yang
 * Date  : May 31, 2010 7:27:57 PM
 *
 */
package org.haa.dataconvert.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.haa.dataconvert.processor.Processor;


/**
 * @Author Eric Yang
 * @version 1.0
 */
@SuppressWarnings("serial")
public class BatchReader extends ConvertReader {

	/**
	 * The size of lines batch read. Must be great than 0.
	 */
	protected long batchSize = 0;

	/**
	 * construct to initial reader and batch size
	 */
	public BatchReader(BufferedReader reader, long batchSize) throws IOException {
		setReader(reader);
		setBatchSize(batchSize);
	}

	/**
	 * construct to initial reader , batch size ,line size, charset
	 */
	public BatchReader(BufferedReader reader, long batchSize, boolean hasLineFlag, int lineSize, String charset)
			throws IOException {
		setReader(reader);
		setBatchSize(batchSize);
		setHasLineFlag(hasLineFlag);
		setLineSize(lineSize);
		setCharset(charset);
	}

	/**
	 * default constructor
	 */
	public BatchReader() {
	}

	/**
	 * batch read lines and process the lines using the processor
	 * 
	 * @param processor
	 *            which to process a line
	 * @throws IOException
	 *             when read error
	 */
	public final void read(Processor processor) throws IOException {
		try {
			checkSource();

			if (end) {
				return;
			}

			resetReadMark();

			String line = readBufferLine();
			long size = 1;
			while (line != null) {
				processor.process(line);
				size++;
				if (size > batchSize)
					break;
				line = readBufferLine();
			}

			if (StringUtils.isEmpty(line)) {
				end = true;
				close();
			} else {
				setReadMark();
			}
		} catch (IOException e) {
			close();
			throw e;
		}
	}

	/**
	 * check whether the reader is initilized
	 * 
	 * @throws IOException
	 */
	protected void checkSource() throws IOException {
		if (reader == null) {
			throw new IOException("Reader not initialized!");
		}

	}

	protected void setReadMark() throws IOException {
		try {
			reader.mark(10);
		} catch (IOException e) {
			if (!"mark/reset not supported".equals(e.getMessage()))
				throw e;
		}
	}

	protected void resetReadMark() throws IOException {
		try {
			reader.reset();
		} catch (IOException e) {
			if (!"Stream not marked".equals(e.getMessage()) && !"Resetting to invalid mark".equals(e.getMessage())
					&& !"mark/reset not supported".equals(e.getMessage()))
				throw e;
		}
	}

	public final List<String> readBatch() throws IOException {
		final List<String> list = new ArrayList<String>();
		read(new Processor() {
			public void process(String line) {
				list.add(line);
			}
		});
		if (list.size() == 0)
			return null;
		return list;
	}

	public void setBatchSize(long batchSize) throws IOException {
		if (batchSize < 0)
			throw new IOException("Batch size must be great than 0!");
		this.batchSize = batchSize;
	}

}
