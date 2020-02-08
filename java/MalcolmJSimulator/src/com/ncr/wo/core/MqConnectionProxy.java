/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr.wo.core;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ncr.wo.application.FailureHandler;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author jm185267
 */
public class MqConnectionProxy {
    
    private ConnectionInfo ci;
    private Connection connection = null;
    private Session session = null;
    private MessageConsumer consumer = null;
    private MessageProducer producer = null;

    public MqConnectionProxy(ConnectionInfo ci) {
        this.ci = ci;
    }
    
    public void connect() throws JMSException {
        JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
        JmsConnectionFactory cf = ff.createConnectionFactory();

        // Set the properties
        cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, ci.getHost());
        cf.setIntProperty(WMQConstants.WMQ_PORT, ci.getPort());
        cf.setStringProperty(WMQConstants.WMQ_CHANNEL, ci.getChannel());
        cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
        cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, ci.getQueueManagerName());

        // Create JMS objects
        connection = cf.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Destination destination = session.createQueue(ci.getDestinationName());
        producer = session.createProducer(destination);
        
        Destination destinationAck = session.createQueue(ci.getDestinationAckName());
        consumer = session.createConsumer(destinationAck);

        // Start the connection
        connection.start();
    }
    
    public TextMessage createTextMessage() throws JMSException {
        return session.createTextMessage();
    }
    
    public void send(TextMessage message, long messageExpiryInMs) throws JMSException {
        producer.send(message, DeliveryMode.NON_PERSISTENT, 1, messageExpiryInMs);
    }
    
    public TextMessage receive() throws JMSException {
        Message msg = consumer.receiveNoWait();
        return (msg instanceof TextMessage) ? (TextMessage) msg : null;
    }
    
    public void close() {
        if (consumer != null) {
                try {
                    consumer.close();
                } catch (JMSException jmsex) {
                    System.out.println("Consumer could not be closed.");
                    FailureHandler.handleFailure(jmsex);
                }
            }

            if (session != null) {
                try {
                    session.close();
                } catch (JMSException jmsex) {
                    System.out.println("Session could not be closed.");
                    FailureHandler.handleFailure(jmsex);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException jmsex) {
                    System.out.println("Connection could not be closed.");
                    FailureHandler.handleFailure(jmsex);
                }
            }
    }
}
