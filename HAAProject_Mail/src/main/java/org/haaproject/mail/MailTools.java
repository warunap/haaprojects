/**
 * Created Date: Jun 16, 2011 2:44:43 PM
 */
package org.haaproject.mail;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class MailTools {

	private static final Log logger = LogFactory.getLog(MailTools.class);

	/**
	 * 从所有img标签src指向的图片地址获得图片内容，将其转换为邮件内嵌的图片插入到邮件中, 这样保证在不同的网络中以及离线的情况下仍然可以查看包含图片的完整邮件内容
	 */
	public static MimeMultipart buildMimePartWithInlineImages(String content) throws MessagingException {
		MimeMultipart multipart = new MimeMultipart();
		addHTMLMailBody(multipart, content);

		/* set to releated for binding the HTML content with the inline attachments by Content-ID */
		multipart.setSubType("related");

		return multipart;
	}

	/**
	 * Find all image url in the html content and fetch the images as inline-attachments.
	 */
	private static void addHTMLMailBody(Multipart multipart, String content) throws MessagingException {
		Pattern pattern = Pattern.compile("<img[ ]+src=\"([^\">]+)\"", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		Set<String> images = new HashSet<String>();
		while (matcher.find()) {
			String img = matcher.group(1);
			logger.debug("find image:" + img);
			images.add(img);
		}
		List<MimeBodyPart> attachments = new ArrayList<MimeBodyPart>();
		for (String imageUrl : images) {
			try {
				MimeBodyPart attachPart = buildInlineAttachFromUrl(imageUrl);
				content = content.replace(imageUrl, "cid:" + attachPart.getContentID());
				attachments.add(attachPart);
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
		}

		/* the html content must be the first body part */
		MimeBodyPart htmlBodyPart = new MimeBodyPart();
		htmlBodyPart.setText(content, "UTF-8");
		htmlBodyPart.setHeader("Content-type", "text/html;charset=\"UTF-8\"");

		multipart.addBodyPart(htmlBodyPart);

		for (MimeBodyPart mimeBodyPart : attachments) {
			multipart.addBodyPart(mimeBodyPart);
		}
	}

	public static MimeBodyPart buildInlineAttachFromUrl(String url) throws MessagingException {
		File file = new File(url);
		String name = file.getName();
		String contentType = null;
		byte[] result = null;
		try {
			java.net.URL u = new java.net.URL(url);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
			conn.connect();
			contentType = conn.getContentType();
			InputStream is = conn.getInputStream();
			BufferedInputStream bin = null;
			try {
				bin = new BufferedInputStream(is);
				ByteArrayOutputStream bo = new ByteArrayOutputStream(bin.available());
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = bin.read(buffer, 0, 1024)) > 0) {
					bo.write(buffer, 0, len);
				}
				result = bo.toByteArray();
			} finally {
				try {
					if (bin != null)
						bin.close();
					is.close();
					conn.disconnect();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		MimeBodyPart attachPart = new MimeBodyPart();
		attachPart.setFileName(name);
		String cid = UUID.randomUUID().toString().replace("-", "");
		attachPart.setHeader("Content-ID", cid);
		attachPart.setHeader("Content-Type", contentType);
		attachPart.setDataHandler(new DataHandler(new ByteArrayDataSource(result, contentType)));
		return attachPart;
	}

}
