/**
 * $Revision: 1.1 $
 * Created: 2007-2-8
 * $Date: 2013/04/03 09:15:08 $
 * 
 * Author: Keven Chen
 */
package org.haaproject.kerberos.util;

import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author Keven Chen
 * @version $Revision 1.1 $
 */

public class FreeMarkerTemplateManager {

	private static final String DEFAULT_CHARSET = "UTF-8";

	private Configuration cfg = new Configuration();

	private String templateDir;

	public FreeMarkerTemplateManager() {
		this(null);
	}

	public FreeMarkerTemplateManager(String templateDir) {
		this(templateDir, DEFAULT_CHARSET);
	}

	public FreeMarkerTemplateManager(String templateDir, String charset) {
		if (charset == null || charset.trim().length() == 0)
			charset = DEFAULT_CHARSET;
		cfg.setDefaultEncoding(charset);
		cfg.setTemplateLoader(new ClassPathTemplateLoader(FreeMarkerTemplateManager.class.getClassLoader()));

		this.templateDir = templateDir;
	}

	public Template getTemplate(String templateName) throws IOException {
		return getTemplate(templateDir, templateName);
	}

	public Template getTemplate(String templateDir, String templateName) throws IOException {
		return cfg.getTemplate(templateDir + templateName);
	}

	public String getTemplateDir() {
		return templateDir;
	}

	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}

	@SuppressWarnings("unchecked")
	public void setClassForTemplateLoading(Class clazz, String pathPrefix) {
		cfg.setClassForTemplateLoading(clazz, pathPrefix);
	}

	public void setClassLoader(ClassLoader loader) {
		cfg.setTemplateLoader(new ClassPathTemplateLoader(loader));
	}
}
