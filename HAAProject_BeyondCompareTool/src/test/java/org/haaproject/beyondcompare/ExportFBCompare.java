package org.haaproject.beyondcompare;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.haaproject.beyondcompare.tool.DiffXmlReader;

public class ExportFBCompare {

	public static void exportDiffFiles(String diffXmlFile, String targetDir) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(diffXmlFile));
		Element rootElement = document.getRootElement();
		Element rootFolder = rootElement.element(DiffXmlReader.TAG_FOLDER_COMP);
		Element ltpathElement = rootFolder.element(DiffXmlReader.TAG_LTPATH);
		String baseDir = ltpathElement.getTextTrim() + File.separator;

		List<String> diffList = DiffXmlReader.readDiffList(document);

		for (String path : diffList) {
			if (path.indexOf(File.separator + "test") != -1 || path.indexOf(File.separator + "build") != -1) {
				continue;
			}

			File file = new File(path);
			String dir = file.getParent();
			String relativeDir = dir.substring(baseDir.length());

			String targetFolder = targetDir + relativeDir;
			File folder = new File(targetFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			FileUtils.copyFileToDirectory(file, folder);

		}
	}

	public static void main(String[] args) throws Exception {
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		exportDiffFiles("c:/report.xml", "D:/FB_PATCH/Project Code/FB-2008-01-23-SWIFT/" + date + File.separator);
		exportDiffFiles("c:/fb_openfunction_report.xml", "D:/FB_PATCH/Project Code/FB_OpenFunction_2008/" + date + File.separator);
	}
}
