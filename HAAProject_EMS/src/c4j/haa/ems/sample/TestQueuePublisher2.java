/**
 * $Revision: 1.0 $
 * $Author: Eric Yang $
 * $Date: Feb 2, 2010 5:05:08 PM $
 *
 * Author: Eric Yang
 * Date  : Feb 2, 2010 5:05:08 PM
 *
 */
package c4j.haa.ems.sample;

import java.util.Date;

import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.tibco.tibjms.TibjmsQueueConnectionFactory;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class TestQueuePublisher2 {

	String serverUrl = "tcp://192.168.5.185:7222";

	String userName = "admin";

	String password = "";

	String queueName = "CLUSTER_TEST";

	public void test() {
		try {
			TibjmsQueueConnectionFactory factory = new TibjmsQueueConnectionFactory(serverUrl);
			QueueConnection connection = factory.createQueueConnection(userName, password);
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(queueName);
			MessageProducer producer = session.createProducer(queue);
			TextMessage message = session.createTextMessage();
			message.setText("hello from 185" + new Date().toString());
			producer.send(message);
			connection.close();
			System.out.println("over");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestQueuePublisher2().test();
	}
}
