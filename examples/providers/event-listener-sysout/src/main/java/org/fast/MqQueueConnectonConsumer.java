package org.fast;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqQueueConnectonConsumer {
	private Connection connection;
	private ConnectionFactory connectionFactory;
	private String queueName;
	private static Logger LOG = LoggerFactory.getLogger(MqQueueConnectonConsumer.class);

	public MqQueueConnectonConsumer(String username, String password, String url, String queueName) {
		// ConnectionFactory ：连接工厂，JMS 用它创建连接
		this.connectionFactory = new ActiveMQConnectionFactory(username, password, url);
		this.queueName = queueName;
		// Connection ：JMS 客户端到JMS Provider 的连接
		try {
			this.connection = connectionFactory.createConnection();
			// Connection 启动
			this.connection.start();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MqQueueConnectonConsumer retrieve() {
		close();
		try {
			connection = connectionFactory.createConnection();
			// Connection 启动
			connection.start();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this;
	}

	public MqQueueConnectonConsumer close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		
		return this;
	}

	public MqQueueConnectonConsumer message(Serializable message) {
		try {
			LOG.info("Message: {}", message);
			// Session： 一个发送或接收消息的线程
			Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			// Queue ：消息的目的地;消息发送给谁.
			Queue destination = session.createQueue(queueName);
			// MessageProducer：消息发送者
			MessageProducer producer = session.createProducer(destination);
			// 设置持久化，此处学习，实际根据项目决定
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			// 构造消息，此处写死，项目就是参数，或者方法获取
			sendMessage(session, producer, message);
			session.commit();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			try {
				Thread.sleep(1000 * 5);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			retrieve();
			message(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				Thread.sleep(1000 * 5);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			retrieve();
			message(message);
		}
		
		return this;
	}

	/**
	 * 
	 * @param session
	 * @param producer
	 * @throws Exception
	 */
	public static void sendMessage(Session session, MessageProducer producer, Serializable message) throws Exception {
		ObjectMessage orderMess = session.createObjectMessage(message);
		System.out.println("向ActiveMq:发送订单信息：" + "ActiveMq 发送的Topic消息");
		producer.send(orderMess);
	}

}
