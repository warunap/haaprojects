package com.haaproject.readfileweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class ReadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(ReadFileServlet.class);

	private static final Object DIR_KEY = "base.dir";

	private static String baseDir;

	static {
		try {
			InputStream is = ReadFileServlet.class.getClassLoader().getResourceAsStream("/read_file_web.properties");
			Properties properties = new Properties();
			properties.load(is);
			baseDir = (String) properties.get(DIR_KEY);
			logger.info(DIR_KEY + "=" + baseDir);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String pathInfo = req.getPathInfo();
		logger.info("RequestPath:" + pathInfo);
		String filePath = baseDir + pathInfo;
		File file = new File(filePath);
		if (!file.exists()) {
			resp.setContentType("text/plain");
			String message = "Can't find file " + filePath;
			logger.warn(message);
			out.print(message);
			return;
		}
		logger.info("FilePath:" + filePath);
		String contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file);
		resp.setContentType(contentType);
		FileInputStream is = new FileInputStream(file);
		int c = -1;
		while ((c = is.read()) != -1) {
			out.write(c);
		}
	}

}
