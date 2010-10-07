/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 7, 2010 1:07:52 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 7, 2010 1:07:52 AM
 *
 */
package com.sisopipo.publisher.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import c4j.encode.Encoder;
import c4j.file.FileUtil;
import c4j.xml.XmlUtil;
import com.sisopipo.content.Article;
import com.sisopipo.content.ArticleUtil;
import com.sisopipo.publisher.ArticlePublisher;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class DirectoryArticlePublisher implements ArticlePublisher {

	public static final Log logger = LogFactory.getLog(DirectoryArticlePublisher.class);

	private static final String LIST_FILE_NAME = "list.xml";

	protected String baseDir;

	public void pulisher(Article article) throws Exception {
		String content = article.getContent();
		String subject = article.getSubject();
		Date date = article.getDate();
		String relativeDir = getReleativeDir(date);
		String targetDir = getTargetDir(relativeDir);

		checkAndAddSubjectIntoList(article, targetDir);

		File tempFile = FileUtil.createTempFile(subject + ".html");
		FileUtils.writeStringToFile(tempFile, content);
		publishFile(tempFile, targetDir);

		finishPublish();
	}

	private String getReleativeDir(Date date) {
		return new SimpleDateFormat("yyyy/MM/dd/").format(date);
	}

	private void checkAndAddSubjectIntoList(Article article, String targetDir) throws Exception {
		String listFilePath = targetDir + LIST_FILE_NAME;
		boolean exists = isFileExists(listFilePath);
		Document document;
		File file;
		if (!exists) {
			document = ArticleUtil.createListDocument();
			file = FileUtil.createTempFile(LIST_FILE_NAME);
		} else {
			file = getFile(listFilePath);
			String content = FileUtils.readFileToString(file, Encoder.DEFAULT_CHARSET);
			document = XmlUtil.parse(content);
		}
		ArticleUtil.addItem(document, article);
		FileUtils.writeStringToFile(file, document.asXML());

		publishFile(file, targetDir);
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = FileUtil.initDirPath(baseDir);
	}

	protected String getTargetDir(String relativeDir) {
		return FileUtil.initDirPath(baseDir + relativeDir);
	}

	protected void publishFile(File tempFile, String targetDir) throws IOException {
		FileUtil.moveFile(tempFile, targetDir + tempFile.getName());
	}

	protected File getFile(String listFilePath) throws IOException {
		return new File(listFilePath);
	}

	protected boolean isFileExists(String listFilePath) throws IOException {
		return new File(listFilePath).exists();
	}

	protected void finishPublish() throws IOException {
	}

}
