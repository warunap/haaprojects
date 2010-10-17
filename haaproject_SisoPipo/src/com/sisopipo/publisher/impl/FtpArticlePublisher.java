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

	private String ftpBaseDir;

	public void setFtpBaseDir(String ftpBaseDir) {
		this.ftpBaseDir = ftpBaseDir;
	}

	protected void initialLocalTargetDir(String localTargetDir, String relativeDir) throws IOException {
		if (new File(localTargetDir + LIST_FILE_NAME).exists())
			return;

		FTPWorker ftpclient = getFtpClient();
		String ftpTargeDir = ftpBaseDir + relativeDir;
		String listFilePath = ftpTargeDir + LIST_FILE_NAME;
		ftpclient.getFileToDir(listFilePath, localTargetDir);
	}

	protected String publishFile(File tempFile, String localTargetDir, String relativeDir) throws IOException {
		String localFilePath = super.publishFile(tempFile, localTargetDir, relativeDir);

		FTPWorker ftpclient = getFtpClient();
		String remoteFilePath = ftpBaseDir + relativeDir + tempFile.getName();
		ftpclient.changeWorkingDirectory(ftpBaseDir + relativeDir);
		ftpclient.put(localFilePath, remoteFilePath);
		return remoteFilePath;
	}

	protected void removeFile(String localTargetDir, String storedFileName, String relativeDir) throws IOException {
		super.removeFile(localTargetDir, storedFileName, relativeDir);

		String filePath = ftpBaseDir + relativeDir + storedFileName;
		boolean result = getFtpClient().deleteFile(filePath);
		if (!result)
			logger.error("Failed to delete file " + filePath);

	}

	public void finishPublish() throws IOException {
		getFtpClient().close();
		ftpclient = null;
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
