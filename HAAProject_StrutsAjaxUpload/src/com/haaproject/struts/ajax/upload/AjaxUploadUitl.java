/**
 * Created Date: Aug 10, 2011 2:30:56 PM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class AjaxUploadUitl {
	private static Log LOG = LogFactory.getLog(AjaxUploadUitl.class);

	public static final String SESSION_FILE_MAP_KEY = "_session_file_map";
	public static final String AJAX_FILE_NAME_PREFIX = "ajaxupload_";

	@SuppressWarnings("unchecked")
	public static Map<String, String> getSessionFileMap() {
		Map<String, Object> session = ActionContext.getContext().getSession();
		Map<String, String> fileMap = (Map<String, String>) session.get(SESSION_FILE_MAP_KEY);
		if (fileMap == null) {
			fileMap = new HashMap<String, String>();
			session.put(SESSION_FILE_MAP_KEY, fileMap);
		}
		return fileMap;
	}

	public static List<File> getFilesByFieldName(String fieldName) {
		return getFiles(AJAX_FILE_NAME_PREFIX + fieldName);
	}

	public static Map<String, File> getFileMapByFieldName(String fieldName) {
		return getFileMap(AJAX_FILE_NAME_PREFIX + fieldName);
	}

	public static List<File> getFiles(String ajaxFileName) {
		String[] fileIdArr = getFileIdArr(ajaxFileName);
		if (fileIdArr != null && fileIdArr.length > 0) {
			List<File> files = new ArrayList<File>(fileIdArr.length);

			for (String fileId : fileIdArr) {
				fileId = fileId.trim();
				File file = getFile(fileId);
				if (file != null) {
					files.add(file);
				} else {
					LOG.warn("Can't find file:" + fileId);
				}
			}
			return files;
		}
		return null;
	}

	public static Map<String, File> getFileMap(String ajaxFileName) {
		String[] fileIdArr = getFileIdArr(ajaxFileName);
		if (fileIdArr != null && fileIdArr.length > 0) {
			Map<String, File> files = new HashMap<String, File>(fileIdArr.length);

			for (String fileId : fileIdArr) {
				fileId = fileId.trim();
				File file = getFile(fileId);
				if (file != null) {
					files.put(fileId, file);
				} else {
					LOG.warn("Can't find file:" + fileId);
				}
			}
			return files;
		}
		return null;
	}

	/**
	 * @param ajaxFileName
	 * @return
	 */
	private static String[] getFileIdArr(String ajaxFileName) {
		ServletRequest request = ServletActionContext.getRequest();
		Object val = request.getParameterMap().get(ajaxFileName);
		String[] fileIdArr = null;
		if (val != null) {
			if (val instanceof Object[]) {
				fileIdArr = (String[]) val;
			} else {
				fileIdArr = ((String) val).split(",");
			}
		}
		return fileIdArr;
	}

	@SuppressWarnings("unchecked")
	public static File getFile(String fileId) {
		Map<String, Object> session = ActionContext.getContext().getSession();
		Map<String, String> fileMap = (Map<String, String>) session.get(SESSION_FILE_MAP_KEY);
		if (fileMap != null) {
			String filePath = fileMap.get(fileId);
			if (StringUtils.isNotEmpty(filePath)) {
				File file = new File(filePath);
				return file;
			}
		}
		return null;
	}
}
