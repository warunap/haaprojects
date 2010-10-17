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

/**
 * @author Geln Yang
 * @version 1.0
 */
public class DirectoryArticlePublisher extends AbstractArticlePublisher {

	public static final Log logger = LogFactory.getLog(DirectoryArticlePublisher.class);

	protected static final String LIST_FILE_NAME = "list.xml";

	private static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

	private static String PRE_FILE_NAME = "";

	protected String localBaseDir;

	protected void publishArticle(Article article) throws Exception {
		String content = article.getContent();
		Date date = article.getDate();
		String relativeDir = getReleativeDir(date);
		String localTargetDir = getLocalTargetDir(relativeDir);

		initialLocalTargetDir(localTargetDir, relativeDir);
		String targetFileName = getUniqueName();
		article.setPath(targetFileName);

		checkAndAddSubjectIntoList(article, localTargetDir, relativeDir);

		File tempFile = FileUtil.createTempFile(targetFileName);
		FileUtils.writeStringToFile(tempFile, content, Encoder.DEFAULT_CHARSET);
		publishFile(tempFile, localTargetDir, relativeDir);
	}

	private String getUniqueName() {
		String name;
		do {
			name = new SimpleDateFormat(yyyyMMddHHmmssSSS).format(new Date()) + ".html";
		} while (name.equals(PRE_FILE_NAME));
		PRE_FILE_NAME = name;
		return name;
	}

	/* used for sub class */
	protected void initialLocalTargetDir(String localTargetDir, String relativeDir) throws IOException {
	}

	protected void removeArticle(Article article) throws Exception {
		Date date = article.getDate();
		String relativeDir = getReleativeDir(date);
		String localTargetDir = getLocalTargetDir(relativeDir);

		String listFilePath = localTargetDir + LIST_FILE_NAME;
		File file = getFile(listFilePath);
		if (file == null) {
			logger.warn("Not exists file " + listFilePath);
			return;
		}
		String content = FileUtils.readFileToString(file, Encoder.DEFAULT_CHARSET);
		Document document = XmlUtil.parse(content);

		String storedFileName = ArticleUtil.removeItem(document, article);

		FileUtils.writeStringToFile(file, document.asXML(), Encoder.DEFAULT_CHARSET);
		publishFile(file, localTargetDir, relativeDir);

		removeFile(localTargetDir, storedFileName, relativeDir);
	}

	private void checkAndAddSubjectIntoList(Article article, String localTargetDir, String relativeDir) throws Exception {
		String listFilePath = localTargetDir + LIST_FILE_NAME;
		File file = getFile(listFilePath);
		Document document;
		if (file == null) {
			document = ArticleUtil.createListDocument();
			file = FileUtil.createTempFile(LIST_FILE_NAME);
		} else {
			String content = FileUtils.readFileToString(file, Encoder.DEFAULT_CHARSET);
			document = XmlUtil.parse(content);
		}
		ArticleUtil.addItem(document, article);
		FileUtils.writeStringToFile(file, document.asXML(), Encoder.DEFAULT_CHARSET);

		publishFile(file, localTargetDir, relativeDir);
	}

	public void setLocalBaseDir(String baseDir) {
		this.localBaseDir = FileUtil.initDirPath(baseDir);
	}

	protected String getLocalTargetDir(String relativeDir) {
		return FileUtil.initDirPath(localBaseDir + relativeDir);
	}

	protected String publishFile(File tempFile, String localTargetDir, String relativeDir) throws IOException {
		String sourceDir = FileUtil.getFileDir(tempFile.getAbsolutePath());
		localTargetDir = new File(localTargetDir).getAbsolutePath();
		if (!sourceDir.endsWith(File.separator))
			sourceDir += File.separator;
		if (!localTargetDir.endsWith(File.separator))
			localTargetDir += File.separator;
		/* judge the taget directory is the same as that the temporary file in */
		if (!localTargetDir.equals(sourceDir))
			FileUtil.moveFile(tempFile, localTargetDir + tempFile.getName());

		return localTargetDir + tempFile.getName();
	}

	protected void removeFile(String localTargetDir, String storedFileName, String relativeDir) throws IOException {
		FileUtil.deleteFile(localTargetDir + storedFileName);
	}

	protected String getReleativeDir(Date date) {
		return new SimpleDateFormat("yyyy/MM/dd/").format(date);
	}

	protected File getFile(String listFilePath) throws IOException {
		File file = new File(listFilePath);
		if (!file.exists())
			return null;
		return file;
	}

	public void finishPublish() throws IOException {
	}

}
