/**
 * $Revision: 1.0 $
 * $Author: Geln Yang $
 * $Date: Oct 15, 2010 11:08:13 PM $
 *
 * Author: Geln Yang
 * Date  : Oct 15, 2010 11:08:13 PM
 *
 */
package com.sisopipo.publisher;

import com.sisopipo.publisher.impl.DirectoryArticlePublisher;
import com.sisopipo.publisher.impl.FtpArticlePublisher;

/**
 * @author Geln Yang
 * @version 1.0
 */
public class PublisherFactory {

	public static ArticlePublisher directoryFactory(String baseDir) {
		DirectoryArticlePublisher publisher = new DirectoryArticlePublisher();
		publisher.setLocalBaseDir(baseDir);
		return publisher;
	}

	public static ArticlePublisher ftpFactory(String ftpurl, int ftpport, String ftpuser, String ftppasswd, String ftpBaseDir, String localBaseDir) {
		FtpArticlePublisher publisher = new FtpArticlePublisher();
		publisher.setFtpurl(ftpurl);
		publisher.setFtpport(ftpport);
		publisher.setFtpuser(ftpuser);
		publisher.setFtppasswd(ftppasswd);
		publisher.setFtpBaseDir(ftpBaseDir);
		publisher.setLocalBaseDir(localBaseDir);
		return publisher;
	}

}
