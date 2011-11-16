/**
 * $Revision: 1.1 $
 * $Author: geln_yang $
 * $Date: 2010/05/31 14:14:35 $
 *
 * Author: Eric Yang
 * Date  : May 31, 2010 8:12:43 PM
 *
 */
package org.haaproject.converter.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.haaproject.converter.dom.Component;
import org.haaproject.converter.dom.Container;
import org.haaproject.converter.dom.Line;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.reader.BatchReader;
import org.haaproject.converter.reader.ReadStatus;


/**
 * @Author Eric Yang
 * @version 1.0
 */
public class ParseUtil {

	
	private static final Log LOG = LogFactory.getLog(ParseUtil.class);
	
	@SuppressWarnings("unchecked")
	public static void setValue(String key, Object target, Object value) throws ParseException {
		try {
			if (target instanceof List) {
				List t = (List) target;
				t.add(value);
			} else {
				OgnlUtil.setValue(key, target, value);
			}
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	/**
	 * NOTICE:The batch reader can't be closed.
	 */
	public static Object batchParse(BatchReader batchReader) throws ParseException {
		try {
			List<String> list = batchReader.readBatch();
			if (list == null || list.size() <= 0) {
				LOG.debug("No data readed!");
				return null;
			}
			ReadStatus readStatus = batchReader.getReadStatus();
			parse(readStatus);
			return readStatus.getReadData();
		} catch (ParseException e) {
			throw e;
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	private static void parse(ReadStatus readStatus) throws ParseException {
		Container container = readStatus.getContainer();// get the container first(it will be changed)
		parseOnce(readStatus);

		if (readStatus.isBatchOver()) {
			return;
		}

		Container next = container.getNext();
		if (next != null) {
			parseFrom(readStatus, next);
		}

		parseParent(readStatus, container);
	}

	private static void parseParent(ReadStatus readStatus, Container container) throws ParseException {
		if (readStatus.isBatchOver()) {
			return;
		}

		Component parent = (Component) container.getParent();
		if (parent != null) {
			/* if the last one,try to re-parse from parent if parent's SHOW-MANY. */
			if ((parent.isShowMany() || parent.isShowNoneMany()) && parent.isBelongToMe(readStatus.getCurrBatchLine())) {
				readStatus.setContainer(parent);// start parse from parent
				parse(readStatus);
			} else if (parent.getNext() != null) {
				readStatus.setContainer(parent.getNext());// start parse from parent's Next
				parse(readStatus);
			}
		}

	}

	private static void parseOnce(ReadStatus readStatus) throws ParseException {
		Container c = readStatus.getContainer();
		parseOnceContainer(readStatus, c);
	}

	private static void parseFrom(ReadStatus readStatus, Container c) throws ParseException {
		parseOnceContainer(readStatus, c);
		Container next = c.getNext();
		if (next != null) {
			while (next != null && !readStatus.isBatchOver()) {
				parseOnceContainer(readStatus, next);
				next = next.getNext();
			}
		}
	}

	private static void parseOnceContainer(ReadStatus readStatus, Container c) throws ParseException {
		readStatus.setContainer(c);
		if (c instanceof Line) {
			parseLine(readStatus, (Line) c);
		} else if (c instanceof Component) {
			parseComponet(readStatus, (Component) c);
		}
	}

	private static void parseComponet(ReadStatus readStatus, Component component) throws ParseException {
		String currBatchLine = readStatus.getCurrBatchLine();
		if (!component.isBelongToMe(currBatchLine)) {
			if (component.isShowMany() || component.isShowOnce())
				throw new ParseException("[index:" + readStatus.lineNum() + "] Unexpected line! expect line!\r\n[line:"
						+ currBatchLine + "]");
			else {
				return;
			}
		}

		/* if the last one,try to re-parse from parent if it's SHOW-MANY. */
		if ((component.isShowMany() || component.isShowNoneMany())) {
			
			while (component.isBelongToMe(currBatchLine)) {
				parseOnceComponent(readStatus, component);
				if (readStatus.isBatchOver()) {
					return;
				}
				currBatchLine = readStatus.getCurrBatchLine();
			}
		} else {
			parseOnceComponent(readStatus, component);
		}
	}

	private static void parseOnceComponent(ReadStatus readStatus, Component component) throws ParseException {
		Container container = component.getChildren().get(0);
		readStatus.setContainer(container);
		parseFrom(readStatus, container);
	}

	private static void parseLine(ReadStatus readStatus, Line line) throws ParseException {
		if (readStatus.isBatchOver()) {
			if (line.isShowMany() || line.isShowOnce())
				throw new ParseException("[index:" + readStatus.lineNum() + "]Null line! expect line[startKey:"
						+ line.getStartKey() + "]!");
			else {
				return;
			}
		}

		String lineContent = readStatus.getCurrBatchLine();
		if (!line.isBelongToMe(lineContent)) {
			if (line.isShowMany() || line.isShowOnce())
				throw new ParseException("[index:" + readStatus.lineNum() + "] Unexpected line! expect line[startKey:"
						+ line.getStartKey() + "]!\r\n[line:" + lineContent + "]");
			else {
				return;
			}
		}

		if (line.isShowMany() || line.isShowNoneMany()) {
			while (line.isBelongToMe(lineContent)) {
				parseOnceLine(line, lineContent, readStatus);
				if (readStatus.isBatchOver()) {
					return;
				}
				lineContent = readStatus.getCurrBatchLine();
			}
		} else {
			parseOnceLine(line, lineContent, readStatus);
		}

	}

	private static void parseOnceLine(Line line, String content, ReadStatus readStatus) throws ParseException {
		/* increase index after parsing */
		readStatus.increaseLineNum();
		Object data = readStatus.getCurrContainerData();
		line.parse(data, content, readStatus.lineNum());
	}
}
