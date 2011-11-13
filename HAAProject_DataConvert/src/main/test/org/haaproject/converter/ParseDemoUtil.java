/**
 * Created Date: Nov 11, 2011 2:42:22 PM
 */
package org.haaproject.converter;

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
 * @author Geln Yang
 * @version 1.0
 */
public class ParseDemoUtil {
	private static final Log LOG = LogFactory.getLog(ParseDemoUtil.class);

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
			while (next != null) {
				/*same level*/
				readStatus.setContainer(next);
				parseOnce(readStatus);
				next = next.getNext();
			}
		}

		parseParent(readStatus, container);
	}

	private static void parseParent(ReadStatus readStatus, Container container) throws ParseException {
		if (readStatus.isBatchOver()) {
			return;
		}

		Component parent = (Component) container.getParent();
		if (parent != null) {
			/*if the last one,try to re-parse from parent if parent's SHOW-MANY.*/
			if ((parent.isShowMany() || parent.isShowNoneMany())
					&& parent.isBelongToMe(readStatus.getCurrBatchLine())) {
				readStatus.setContainer(parent);// start parse from parent
				parse(readStatus);
			} else if (parent.getNext() != null) {
				readStatus.setContainer(parent.getNext());// start parse from parent's Next
				parse(readStatus);
			}
		}

	}

	private static void parseOnce(ReadStatus readStatus) throws ParseException {
		Container container = readStatus.getContainer();
		if (container instanceof Line) {
			Line line = (Line) container;
			parseLine(readStatus, line);
		} else if (container instanceof Component) {
			Component component = (Component) container;
			parseComponet(readStatus, component);
		}

	}

	private static void parseComponet(ReadStatus readStatus, Component component) throws ParseException {
		String currBatchLine = readStatus.getCurrBatchLine();
		if (!component.isBelongToMe(currBatchLine)) {
			if (component.isShowMany() || component.isShowOnce())
				throw new ParseException("[index:" + readStatus.lineNum()
						+ "] Unexpected line! expect line!\r\n[line:" + currBatchLine + "]");
			else {
				return;
			}
		}

		/*if the last one,try to re-parse from parent if it's SHOW-MANY.*/
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
		if (container instanceof Component) {
			parseComponet(readStatus, (Component) container);
		} else if (container instanceof Line) {
			parseLine(readStatus, (Line) container);
		}

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
				throw new ParseException("[index:" + readStatus.lineNum()
						+ "] Unexpected line! expect line[startKey:" + line.getStartKey() + "]!\r\n[line:"
						+ lineContent + "]");
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