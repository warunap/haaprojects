/**
 * Created Date: Aug 9, 2011 11:56:54 AM
 */
package com.haaproject.struts.ajax.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;

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
		String fileName = ajaxFileName.substring(AjaxUploadUitl.AJAX_FILE_NAME_PREFIX.length());
		List<File> files = AjaxUploadUitl.getFiles(ajaxFileName);
		if (files != null && files.size() > 0) {
			int fileCount = files.size();
			List<String> acceptedContentTypes = new ArrayList<String>(fileCount);
			List<String> acceptedFileNames = new ArrayList<String>(fileCount);
			String contentTypeName = fileName + "ContentType";
			String fileNameName = fileName + "FileName";
			
			for (File file : files) {
				String name = file.getName();
				if (name.indexOf("_") > 0) {
					name = name.substring(name.indexOf("_") + 1);
				}
				acceptedFileNames.add(name);
				acceptedContentTypes.add(FileTypeMap.getDefaultFileTypeMap().getContentType(file));
			}

			Map<String, Object> params = ac.getParameters();
			if (files.size() > 0) {
				params.put(fileName, files.toArray(new File[fileCount]));
				params.put(contentTypeName, acceptedContentTypes.toArray(new String[fileCount]));
				params.put(fileNameName, acceptedFileNames.toArray(new String[fileCount]));
			} else {
				params.put(fileName, new File[0]);
			}
		}
	}

	/**
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> findAjaxFileUploadParameters(HttpServletRequest request) {
		List<String> params = new ArrayList<String>();
		Enumeration parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String elem = (String) parameterNames.nextElement();
			if (elem.startsWith(AjaxUploadUitl.AJAX_FILE_NAME_PREFIX)) {
				params.add(elem);
			}
		}
		return params;
	}
}
