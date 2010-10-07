/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 6, 2010 10:37:12 PM $
 *
 * Author: Geln Yang
 * Date  : Oct 6, 2010 10:37:12 PM
 *
 */
package com.sisopipo.content;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class Article implements Serializable {

	private String subject;

	private String editor;

	private String category;

	private String content;

	private Date date;

	private List<File> attachments;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String title) {
		this.subject = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<File> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<File> attachments) {
		this.attachments = attachments;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}
}
