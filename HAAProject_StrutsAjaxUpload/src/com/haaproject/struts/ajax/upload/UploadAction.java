/**
 * Created Date: Aug 8, 2011 5:44:39 PM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class UploadAction extends ActionSupport {

	private static Log LOG = LogFactory.getLog(UploadAction.class);

	private static Map<String, String> globalFileMap = new HashMap<String, String>();

	private String fileId;
	private String fileName;

	private File ajaxFile;
	private String ajaxFileFileName;

	private String uploadDir;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		fileName = ajaxFileFileName;
		String sessionid = ServletActionContext.getRequest().getSession().getId();
		if (ajaxFile != null) {
			File destFile = new File(uploadDir + sessionid + "_" + ajaxFileFileName);
			if (destFile.exists()) {
				FileUtils.forceDelete(destFile);
			}
			FileUtils.moveFile(ajaxFile, destFile);
			fileId = UUID.randomUUID().toString().replace("-", "");
			globalFileMap.put(fileId, destFile.getName());
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String recordToSession() throws Exception {
		if (StringUtils.isNotEmpty(fileId)) {
			String filePath = globalFileMap.get(fileId);
			if (StringUtils.isNotEmpty(filePath)) {
				Map<String, String> fileMap = AjaxUploadUitl.getSessionFileMap();
				fileMap.put(fileId, filePath);
			}
			globalFileMap.remove(fileId);
		}

		return SUCCESS;
	}

	public String getAjaxFileFileName() {
		return ajaxFileFileName;
	}

	public void setUploadDir(String uploadDir) {
		if (!uploadDir.endsWith(File.separator)) {
			uploadDir += File.separator;
		}
		try {
			FileUtils.forceMkdir(new File(uploadDir));
		} catch (IOException e) {
			LOG.warn(e.getMessage());
		}
		this.uploadDir = uploadDir;

	}

	public void setAjaxFileFileName(String ajaxFileFileName) {
		this.ajaxFileFileName = ajaxFileFileName;
	}

	public File getAjaxFile() {
		return ajaxFile;
	}

	public void setAjaxFile(File fileData) {
		this.ajaxFile = fileData;
	}

	public String getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
