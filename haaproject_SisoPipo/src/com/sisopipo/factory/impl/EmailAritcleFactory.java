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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.FetchProfile;
import c4j.mail.MailReceiver;
import c4j.mail.MailSessionUtil;
import c4j.mail.MailReceiver.MailMessage;
import com.sisopipo.content.Article;
import com.sisopipo.factory.ArticleFactory;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class EmailAritcleFactory implements ArticleFactory {

	private static final String CATEGORY_DEFAULT = "Default";

	public List<Article> generete() throws Exception {
		List<Article> articles = new ArrayList<Article>();

		MailReceiver receiver = new MailReceiver(MailSessionUtil.getDefaultStoreConnector());
		FetchProfile profile = new FetchProfile();
		profile.add(FetchProfile.Item.ENVELOPE);
		List<MailMessage> messages = receiver.receive(true, profile);
		for (MailMessage mailMessage : messages) {
			String editor = mailMessage.getFrom();
			String subject = mailMessage.getSubject();
			String content = mailMessage.getBodyText();
			Date sentDate = mailMessage.getSendDate();
			String category = CATEGORY_DEFAULT;
			Matcher matcher = Pattern.compile("\\[[^\\[\\]]+\\]").matcher(subject);
			if (matcher.find()) {
				int start = matcher.start();
				if (start == 0) {
					String group = matcher.group();
					subject = subject.substring(group.length());
					category = group.substring(1, group.length() - 1);
				}
			}
			Article article = new Article();
			article.setDate(sentDate);
			article.setEditor(editor);
			article.setContent(content);
			article.setSubject(subject);
			article.setCategory(category);

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
			System.out.println(article.getCategory());
		}
		System.out.println("over");
	}

}
