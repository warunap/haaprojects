/**
 * Created Date: Jun 2, 2011 10:20:11 AM
 */
package org.haaproject.poitest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.RevisionMarkAuthorTable;
import org.apache.poi.xslf.XSLFSlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class OfficeTools {

	private static final Log logger = LogFactory.getLog(OfficeTools.class);

	public static void getWordRevisions(String officePath) throws IOException {
		String path = officePath.toLowerCase();
		if (path.endsWith("doc")) {
			HWPFDocument document = new HWPFDocument(new FileInputStream(new File(officePath)));
			RevisionMarkAuthorTable table = document.getRevisionMarkAuthorTable();
			for (int i = 0; i < table.getSize(); i++) {
				logger.info("author:" + table.getAuthor(i));
			}

			for (String revision : table.getEntries()) {
				logger.info("-->" + revision);
			}
		} else {
			XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(path));
			List<IBodyElement> bodyElements = docx.getBodyElements();
			for (IBodyElement bodyElement : bodyElements) {
				IBody part = bodyElement.getPart();
			}
		}
	}

	public static int getPageCount(String filePath) {
		String lowerFilePath = filePath.toLowerCase();
		try {
			if (lowerFilePath.endsWith(".xls")) {
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(lowerFilePath));
				Integer sheetNums = workbook.getNumberOfSheets();
				if (sheetNums > 0) {
					return workbook.getSheetAt(0).getRowBreaks().length + 1;
				}
			} else if (lowerFilePath.endsWith(".xlsx")) {
				XSSFWorkbook xwb = new XSSFWorkbook(lowerFilePath);
				Integer sheetNums = xwb.getNumberOfSheets();
				if (sheetNums > 0) {
					return xwb.getSheetAt(0).getRowBreaks().length + 1;
				}
			} else if (lowerFilePath.endsWith(".docx")) {
				XWPFDocument docx = new XWPFDocument(POIXMLDocument.openPackage(lowerFilePath));
				return docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
			} else if (lowerFilePath.endsWith(".doc")) {
				HWPFDocument wordDoc = new HWPFDocument(new FileInputStream(lowerFilePath));
				return wordDoc.getSummaryInformation().getPageCount();
			} else if (lowerFilePath.endsWith(".ppt")) {
				HSLFSlideShow document = new HSLFSlideShow(new FileInputStream(lowerFilePath));
				SlideShow slideShow = new SlideShow(document);
				return slideShow.getSlides().length;
			} else if (lowerFilePath.endsWith(".pptx")) {
				XSLFSlideShow xdocument = new XSLFSlideShow(lowerFilePath);
				XMLSlideShow xslideShow = new XMLSlideShow(xdocument);
				return xslideShow.getSlides().length;
			} else {
				return 1;
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return 1;
		}
		return 1;
	}
}
