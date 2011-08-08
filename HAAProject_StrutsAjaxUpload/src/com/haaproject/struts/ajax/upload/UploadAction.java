/**
 * Created By: Comwave Project Team
 * Created Date: Aug 8, 2011 5:44:39 PM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class UploadAction extends ActionSupport {

	private String fileId;
	private String fileName;

	private File fileData;

	public String execute() throws Exception {
		fileId = "1";
		fileName = "a.txt";
		return SUCCESS;
	}

	public File getFileData() {
		return fileData;
	}

	public void setFileData(File fileData) {
		this.fileData = fileData;
	}

	public String getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}
}
