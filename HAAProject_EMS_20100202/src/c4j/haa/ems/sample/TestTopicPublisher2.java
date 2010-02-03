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

import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

/**
 * @author Eric Yang
 * @version 1.0
 */
public class TestTopicPublisher2 extends BaseEMSTestUnit {

	public void test() {
		try {
			setTopicName("helloboy");
			setServerUrl("tcp://192.168.5.60:7222");
			TopicConnection topicConnect = getTopicConnect();
			TopicSession session = getSession(topicConnect);
			Topic topic = session.createTopic(topicName);

			TopicPublisher publisher = session.createPublisher(topic);

			javax.jms.TextMessage message = session.createTextMessage();
			message.setText("hello eric");
			publisher.publish(message);
			topicConnect.close();
			System.out.println("over");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestTopicPublisher2().test();
	}
}
