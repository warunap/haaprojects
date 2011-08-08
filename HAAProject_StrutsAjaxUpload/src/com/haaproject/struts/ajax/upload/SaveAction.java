/**
 * Created By: Comwave Project Team
 * Created Date: Aug 8, 2011 5:44:39 PM
 */
package com.haaproject.struts.ajax.upload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class SaveAction extends ActionSupport {

	private static Log LOG = LogFactory.getLog(SaveAction.class);
	private String subject;
	private String content;

	public String execute() throws Exception {
		LOG.info("save action");
		LOG.info("subject:" + subject);
		LOG.info("subject:" + content);
		return SUCCESS;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
