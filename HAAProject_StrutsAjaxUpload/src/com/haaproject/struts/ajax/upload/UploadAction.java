/**
 * Created By: Comwave Project Team
 * Created Date: Aug 8, 2011 5:44:39 PM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class UploadAction extends ActionSupport {

	private static Map<String, String> globalFileMap = new HashMap<String, String>();

	private String fileId;
	private String fileName;

	private File ajaxFile;
	private String ajaxFileFileName;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		fileName = ajaxFileFileName;
		String sessionid = ServletActionContext.getRequest().getSession().getId();
		if (ajaxFile != null) {
			File destFile = new File(AjaxUploadConfig.getUploadDir() + sessionid + "_" + ajaxFileFileName);
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
				Map<String, Object> session = ActionContext.getContext().getSession();
				Map<String, String> fileMap = (Map<String, String>) session.get(AjaxUploadConfig.SESSION_FILE_MAP_KEY);
				if (fileMap == null) {
					fileMap = new HashMap<String, String>();
					session.put(AjaxUploadConfig.SESSION_FILE_MAP_KEY, fileMap);
				}
				fileMap.put(fileId, filePath);
			}
			globalFileMap.remove(fileId);
		}

		return SUCCESS;
	}

	public String getAjaxFileFileName() {
		return ajaxFileFileName;
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
