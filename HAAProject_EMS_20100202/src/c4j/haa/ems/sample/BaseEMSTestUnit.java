/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Feb 2, 2010 5:11:56 PM $
 *
 * Author: Eric Yang
 * Date  : Feb 2, 2010 5:11:56 PM
 *
 */
package c4j.haa.ems.sample;

import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class BaseEMSTestUnit {

	String serverUrl = "tcp://192.168.5.59:7222";

	String userName = "admin";

	String password = "";

	String topicName = "test_queue";

	protected TopicConnection topicConnection;

	protected TopicConnection getTopicConnect() throws JMSException {
		TopicConnectionFactory factory = new com.tibco.tibjms.TibjmsTopicConnectionFactory(serverUrl);
		return factory.createTopicConnection(userName, password);
	}

	protected TopicSession getSession(TopicConnection connection) throws JMSException {
		return connection.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

}
