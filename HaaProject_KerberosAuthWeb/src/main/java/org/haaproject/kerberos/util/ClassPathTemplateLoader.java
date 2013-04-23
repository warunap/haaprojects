/**
 * Created: 2007-2-1
 * Modified: 2007-2-1
 * 
 * Author: Keven Chen
 */
package org.haaproject.kerberos.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import freemarker.cache.TemplateLoader;

/**
 * @author Keven Chen
 * @version $Revision 1.0 $
 */
public class ClassPathTemplateLoader implements TemplateLoader {

	private ClassLoader loader;

	public ClassPathTemplateLoader(ClassLoader loader) {
		this.loader = loader;
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource != null) {
			((InputStream) templateSource).close();
		}
	}

	public Object findTemplateSource(String name) throws IOException {
		ClassLoader loader = this.loader;
		if (loader == null) {
			loader = Thread.currentThread().getContextClassLoader();
		}
		InputStream in = null;
		in = loader.getResourceAsStream(name);
		if (in == null)
			in = ClassLoader.getSystemResourceAsStream(name);
		if (in == null) {
			ClassLoader parent = loader.getParent();
			while (in == null && parent != null) {
				in = parent.getResourceAsStream(name);
				parent = parent.getParent();
			}
		}

		return in;
	}

	public long getLastModified(final Object templateSource) {
		return 0;
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource != null) {
			return new InputStreamReader((InputStream) templateSource, encoding);
		}
		return null;
	}

}
