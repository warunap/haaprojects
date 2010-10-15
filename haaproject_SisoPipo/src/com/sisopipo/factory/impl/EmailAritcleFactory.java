/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 7, 2010 12:42:31 AM $
 *
 * Author: Geln Yang
 * Date  : Oct 7, 2010 12:42:31 AM
 *
 */
package com.sisopipo.factory.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.FetchProfile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import c4j.mail.MailReceiver;
import c4j.mail.MailSessionUtil;
import c4j.mail.MailReceiver.MailMessage;
import c4j.string.StringUtil;
import com.sisopipo.content.Article;
import com.sisopipo.factory.ArticleFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class EmailAritcleFactory implements ArticleFactory {

	public static final Log logger = LogFactory.getLog(EmailAritcleFactory.class);

	private static final String CATEGORY_DEFAULT = "Default";

	private static final String yyyyMMdd = "yyyy/MM/dd";

	private static final String HHmmss = null;

	public List<Article> generete() throws Exception {
		List<Article> articles = new ArrayList<Article>();

		MailReceiver receiver = new MailReceiver(MailSessionUtil.getDefaultStoreConnector());
		FetchProfile profile = new FetchProfile();
		profile.add(FetchProfile.Item.ENVELOPE);
		List<MailMessage> messages = receiver.receive(true, profile);
		for (MailMessage mailMessage : messages) {
			String subject = mailMessage.getSubject();
			String editor = mailMessage.getFromMail();
			String content = mailMessage.getBodyText();
			Date sentDate = mailMessage.getSendDate();
			String tags = CATEGORY_DEFAULT;

			logger.info("New Article:" + subject);

			Article article = new Article();
			Matcher matcher = Pattern.compile("\\[[^\\[\\]]+\\]").matcher(subject);
			if (matcher.find()) {
				int start = matcher.start();
				if (start == 0) {
					String group = matcher.group();

					String keys = group.substring(1, group.length() - 1);
					String[] arr = keys.split("#");
					if (arr.length != 3) {
						logger.error("Error key include in subject:" + subject);
						continue;
					}
					/* set the action */
					article.setAction(arr[0]);

					/* set the date */
					if (StringUtil.isNotBlankOrNull(arr[1])) {
						try {
							String time = arr[1] + new SimpleDateFormat(HHmmss).format(new Date());
							Date d = new SimpleDateFormat(yyyyMMdd + HHmmss).parse(time);
							sentDate = d;
						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
					}

					/* set the tags */
					if (StringUtil.isNotBlankOrNull(arr[2])) {
						tags = arr[2];
					}

					/* get the subject */
					subject = subject.substring(group.length());
				}
			}

			article.setDate(sentDate);
			article.setEditor(editor);
			article.setContent(content);
			article.setSubject(subject);
			article.setTags(tags);

			articles.add(article);
		}
		receiver.close();
		return articles;
	}

	public static void main(String[] args) throws Exception {
		List<Article> articles = new EmailAritcleFactory().generete();
		for (Article article : articles) {
			System.out.println(article.getSubject());
			System.out.println(article.getDate());
			System.out.println(article.getTags());
		}
		System.out.println("over");
	}

}
