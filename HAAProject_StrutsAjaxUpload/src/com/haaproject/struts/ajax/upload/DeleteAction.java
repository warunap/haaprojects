/**
 * Created By: Comwave Project Team
 * Created Date: Aug 8, 2011 5:44:39 PM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class DeleteAction extends ActionSupport {

	private static Log LOG = LogFactory.getLog(DeleteAction.class);
	private String fileId;
	private String fileName;
	private String resultCode;
	private String resultMessage;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		LOG.info("delete file:" + fileId);
		Map<String, Object> session = ActionContext.getContext().getSession();
		Map<String, String> fileMap = (Map<String, String>) session.get(AjaxUploadConfig.SESSION_FILE_MAP_KEY);
		if (fileMap != null) {
			String filePath = fileMap.get(fileId);
			if (StringUtils.isNotEmpty(filePath)) {
				File file = new File(filePath);
				fileName = file.getName();
				if (file.exists()) {
					FileUtils.forceDelete(file);
				} else {
					LOG.warn(filePath + " not exists!");
				}
				fileMap.remove(fileId);
			}
		}

		resultCode = "S";
		resultMessage = "success";
		return SUCCESS;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public String getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}
}
