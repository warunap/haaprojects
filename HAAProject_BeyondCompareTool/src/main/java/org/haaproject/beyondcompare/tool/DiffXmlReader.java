package org.haaproject.beyondcompare.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DiffXmlReader {

	private static final Log logger = LogFactory.getLog(DiffXmlReader.class);

	public static final String TAG_FOLDER_COMP = "foldercomp";
	public static final String TAG_FILE_COMP = "filecomp";
	public static final String TAG_LTPATH = "ltpath";
	public static final String TAG_RTPATH = "rtpath";
	public static final String TAG_LT = "lt";
	public static final String TAG_NAME = "name";

	public static List<String> readDiffList(String diffXmlFile) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(diffXmlFile));
		return readDiffList(document);
	}

	public static List<String> readDiffList(Document document) throws Exception {
		List<String> fileList = new ArrayList<String>();

		Element rootElement = document.getRootElement();
		Element rootFolder = rootElement.element(TAG_FOLDER_COMP);
		Element ltpathElement = rootFolder.element(TAG_LTPATH);
		Element rtpathElement = rootFolder.element(TAG_RTPATH);

		String ltpath = ltpathElement.getTextTrim() + File.separator;
		String rtpath = rtpathElement.getTextTrim() + File.separator;

		logger.info("Left Foloder:" + ltpath);
		logger.info("Right Foloder:" + rtpath);

		readFromFolderCompareElement(fileList, rootFolder, ltpath);
		return fileList;
	}

	@SuppressWarnings("unchecked")
	private static void readFromFolderCompareElement(List<String> fileList, Element element, String parentPath) {
		String baseFolder = parentPath;
		String subFolderName = getLtName(element);
		if (subFolderName != null) {
			baseFolder += subFolderName + File.separator;
		}

		Iterator<Element> fileElements = element.elementIterator(TAG_FILE_COMP);
		if (fileElements != null) {
			while (fileElements.hasNext()) {
				Element fileElement = fileElements.next();
				String fileName = getLtName(fileElement);
				String filePath = baseFolder + fileName;
				logger.info(filePath);
				fileList.add(filePath);
			}
		}

		Iterator<Element> subFolderElements = element.elementIterator(TAG_FOLDER_COMP);
		if (subFolderElements != null) {
			while (subFolderElements.hasNext()) {
				Element next = subFolderElements.next();
				readFromFolderCompareElement(fileList, next, baseFolder);
			}
		}
	}

	private static String getLtName(Element element) {
		Element lt = element.element(TAG_LT);
		if (lt != null) {
			Element name = lt.element(TAG_NAME);
			return name.getTextTrim();
		}
		return null;
	}

}
