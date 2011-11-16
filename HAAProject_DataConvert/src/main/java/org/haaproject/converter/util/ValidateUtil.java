/**
 * Created Date: Nov 16, 2011 4:32:33 PM
 */
package org.haaproject.converter.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.haaproject.converter.dom.Component;
import org.haaproject.converter.dom.Container;
import org.haaproject.converter.dom.Line;
import org.haaproject.converter.exception.ParseException;
import org.haaproject.converter.reader.IndexReader;
import org.haaproject.converter.reader.IndexStreamReader;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ValidateUtil {

	public static void validateContent(Component component, String fileContent) throws IOException, ParseException {
		StringReader sReader = new StringReader(fileContent);
		BufferedReader reader = new BufferedReader(sReader);
		validate(component, reader);
	}

	public static void validateFile(Component component, String filePath) throws IOException, ParseException {
		FileInputStream inputStream = new FileInputStream(filePath);
		try {
			validate(component, inputStream);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
	}

	public static void validate(Component component, InputStream inputStream) throws IOException, ParseException {
		try {
			if (!component.getConverter().isHasLineFlag()) {
				IndexReader indexReader = new IndexStreamReader(inputStream, component);
				if (!validate(component, indexReader))
					throw new ParseException("[line:" + indexReader.lineNum() + "]unexpected line,invalid startKey!");
				return;
			} else {
				InputStreamReader inr = new InputStreamReader(inputStream, component.getConverter().getCharset());
				try {
					BufferedReader reader = new BufferedReader(inr);
					validate(component, reader);
				} finally {
					try {
						inr.close();
					} catch (Exception e) {
					}
				}
			}
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
			}
		}
	}

	public static void validate(Component component, BufferedReader bufferedReader) throws IOException, ParseException {
		IndexReader indexReader = new IndexReader(bufferedReader, component);
		if (!validate(component, indexReader))
			throw new ParseException("[line:" + indexReader.lineNum() + "]unexpected line,invalid startKey!");
	}

	private static boolean validate(Component component, IndexReader reader) throws IOException, ParseException {
		boolean foundStartKey = onceValidate(component, reader);

		if (component.isShowMany() || component.isShowNoneMany()) {
			String lineContent = reader.popTopLine();
			if (lineContent == null)
				return true;

			/* loop component */
			while (component.isBelongToMe(lineContent)) {
				foundStartKey = onceValidate(component, reader);
				lineContent = reader.popTopLine();
				if (lineContent == null)
					break;
			}
		}
		return foundStartKey;
	}

	private static boolean onceValidate(Component component, IndexReader reader) throws IOException, ParseException {
		List<Container> children = component.getChildren();
		boolean foundStartKey = true;// 最后一次读取的行是否有找到匹配的StartKey
		for (int i = 0; i < children.size(); i++) {
			Container child = children.get(i);
			if (child instanceof Line) {
				Line line = (Line) child;

				/* return if line hasn't start key */
				if (!line.isHasStartKey())
					return true;

				foundStartKey = validate(line, reader);
			} else if (child instanceof Component) {
				Component c = (Component) child;
				foundStartKey = validate(c, reader);
			}
		}
		return foundStartKey;
	}

	private static boolean validate(Line line, IndexReader reader) throws IOException, ParseException {
		String content = reader.readLine();
		if (content == null) {
			if (line.isShowMany() || line.isShowOnce()) {
				throw new ParseException("[line:" + reader.lineNum() + "]Null line! expect line[startKey:"
						+ line.getStartKey() + "]!");
			}
			reader.back();
			return true;
		}
		if (!line.isBelongToMe(content)) {
			if (line.isShowMany() || line.isShowOnce())
				throw new ParseException("[line:" + reader.lineNum() + "] Unexpected line! invalid startKey!");
			else {
				reader.back();
				return false;
			}
		}

		validateContentLength(line, content, reader);

		if (line.isShowMany() || line.isShowNoneMany()) {
			String newLine = reader.readLine();
			if (newLine != null) {
				while (line.isBelongToMe(newLine)) {
					validateContentLength(line, newLine, reader);

					newLine = reader.readLine();
					if (newLine == null) {
						/* go back a line */
						reader.back();
						return true;
					}
				}
			}
			/* go back a line */
			reader.back();
		}
		String lastLine = reader.readLine();
		reader.back();
		return "".equalsIgnoreCase(lastLine) || lastLine == null || line.isBelongToMe(lastLine);
	}

	private static void validateContentLength(Line line, String content, IndexReader reader) throws ParseException {
		try {
			line.validateLength(content);
		} catch (ParseException e) {
			throw new ParseException("[index:" + reader.lineNum() + "]" + e.getMessage(), e);
		}
	}
}
