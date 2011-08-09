/**
 * Created By: Comwave Project Team
 * Created Date: Aug 9, 2011 11:56:54 AM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class AjaxUploadIntercepter extends AbstractInterceptor {

	private static Log LOG = LogFactory.getLog(AjaxUploadIntercepter.class);

	public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext ac = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);

		try {
			List<String> ajaxFileNames = findAjaxFileUploadParameters(request);
			if (ajaxFileNames != null && ajaxFileNames.size() > 0) {
				for (String ajaxFileName : ajaxFileNames) {
					processAjaxFile(ac, request, ajaxFileName);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return invocation.invoke();
	}

	/**
	 * @param ac
	 * @param request
	 * @param ajaxFileName
	 */
	private void processAjaxFile(ActionContext ac, HttpServletRequest request, String ajaxFileName) {
		Object val = request.getParameterMap().get(ajaxFileName);
		String[] fileIdArr;
		if (val instanceof Object[]) {
			fileIdArr = (String[]) val;
		} else {
			fileIdArr = ((String) val).split(",");
		}
		if (fileIdArr != null && fileIdArr.length > 0) {
			List<File> files = new ArrayList<File>();
			for (String fileId : fileIdArr) {
				fileId = fileId.trim();
				File file = getFile(ac, request, fileId);
				if (file != null) {
					files.add(file);
				} else {
					LOG.warn("Can't find file:" + fileId);
				}
			}

			Map<String, Object> params = ac.getParameters();
			String ajaxFileNamePrefix = AjaxUploadConfig.getAjaxFileNamePrefix();
			String fileName = ajaxFileName.substring(ajaxFileNamePrefix.length());
			if (files.size() > 0) {
				params.put(fileName, files.toArray(new File[files.size()]));
			} else {
				params.put(fileName, new File[0]);
			}
		}
	}

	/**
	 * @param ac
	 * @param request
	 * @param fileId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private File getFile(ActionContext ac, HttpServletRequest request, String fileId) {
		Map<String, Object> session = ac.getSession();
		Map<String, String> fileMap = (Map<String, String>) session.get(AjaxUploadConfig.SESSION_FILE_MAP_KEY);
		if (fileMap != null) {
			String fileName = fileMap.get(fileId);
			if (StringUtils.isNotEmpty(fileName)) {
				File file = new File(AjaxUploadConfig.getUploadDir() + fileName);
				return file;
			}
		}
		return null;
	}

	/**
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> findAjaxFileUploadParameters(HttpServletRequest request) {
		String ajaxFileNamePrefix = AjaxUploadConfig.getAjaxFileNamePrefix();
		List<String> params = new ArrayList<String>();
		Enumeration parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String elem = (String) parameterNames.nextElement();
			if (elem.startsWith(ajaxFileNamePrefix)) {
				params.add(elem);
			}
		}
		return params;
	}
}
