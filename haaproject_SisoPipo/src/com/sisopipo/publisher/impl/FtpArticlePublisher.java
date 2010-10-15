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
import org.apache.commons.net.ftp.FTPClient;
import c4j.file.FileUtil;
import c4j.net.FTPWorker;
import c4j.net.FtpUtil;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class FtpArticlePublisher extends DirectoryArticlePublisher {

	private FTPWorker ftpclient;

	private String ftpurl;

	private int ftpport = FTPClient.DEFAULT_PORT;

	private String ftpuser;

	private String ftppasswd;

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	protected String getTargetDir(String relativeDir) {
		return baseDir + relativeDir;
	}

	protected void publishFile(File tempFile, String targetDir) throws IOException {
		FTPWorker ftpclient = getFtpClient();
		ftpclient.changeWorkingDirectory(targetDir);
		ftpclient.putFileToServer(tempFile.getAbsolutePath(), tempFile.getName());
		FileUtil.deleteFile(tempFile);
	}

	protected void removeFile(String filePath) throws IOException {
		boolean result = ftpclient.deleteFile(filePath);
		if (!result)
			logger.error("Failed to delete file " + filePath);
	}

	protected File getFile(String listFilePath) throws IOException {
		return getFtpClient().getFileToTmp(listFilePath);
	}

	protected boolean isFileExists(String listFilePath) throws IOException {
		return getFtpClient().existsFile(listFilePath);
	}

	protected void finishPublish() throws IOException {
		getFtpClient().close();
	}

	public FTPWorker getFtpClient() throws IOException {
		if (ftpclient == null) {
			ftpclient = FtpUtil.login(ftpurl, ftpport, ftpuser, ftppasswd);
		}
		return ftpclient;
	}

	public void setFtpurl(String ftpurl) {
		this.ftpurl = ftpurl;
	}

	public void setFtpport(int ftpport) {
		this.ftpport = ftpport;
	}

	public void setFtpuser(String ftpuser) {
		this.ftpuser = ftpuser;
	}

	public void setFtppasswd(String ftppasswd) {
		this.ftppasswd = ftppasswd;
	}

}
