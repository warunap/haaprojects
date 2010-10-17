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

	/** Article subject */
	private String subject;

	/** Article editor */
	private String editor;

	/** Tags splitted by comma */
	private String tags;

	/** Article content */
	private String content;

	/** Publish date */
	private Date date;

	private String action;

	/** Article attachments,include images,flash */
	private List<File> attachments;

	private String path;

	public boolean toRemove() {
		return "delete".equalsIgnoreCase(action) || "remove".equalsIgnoreCase(action);
	}

	public boolean toAdd() {
		return !toRemove();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String title) {
		this.subject = title == null ? null : title.trim();

	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
