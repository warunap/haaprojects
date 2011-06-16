package org.haaproject.mail.test;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.haaproject.mail.HaaMailConfig;
import org.haaproject.mail.MailTools;

public class ImageAttachTest {

	private static final Log logger = LogFactory.getLog(ImageAttachTest.class);

	public static void main(String[] args) throws Exception {
		String subject = "[" + System.currentTimeMillis() + "]Meeting Request Using JavaMail";
		String content = "<html><body>HTML : IMAGE MAIL<BR>";
		content += "<img src=\"http://sisopipo.com/images/logo.gif\"/><br/>";
		content += "<img src=\"http://sisopipo.com/images/article.jpg\"/><br/>";
		content += "</body></html>";

		logger.info(subject);
		logger.info(content);

		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", HaaMailConfig.getConfig("mail.smtp.host"));

		Session session = Session.getDefaultInstance(props);

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(HaaMailConfig.getConfig("mail.from")));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(HaaMailConfig.getConfig("mail.recipient")));
		message.setSubject(subject);

		MimeMultipart multipart = MailTools.buildMimePartWithInlineImages(content);

		message.setContent(multipart);
		Transport.send(message);

		logger.info("send mail over");

	}

}
