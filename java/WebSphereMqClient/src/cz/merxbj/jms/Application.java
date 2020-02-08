package cz.merxbj.jms;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import java.util.Calendar;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class Application {

    private static String host = "corpesbdev1.sheetz.com";
    private static int port = 2550;
    private static String channel = "MALCOLM.NCR.SVRCONN";
    private static String queueManagerName = "malcolm.ncr.qm";
    private static String destinationName = "TOSTORE";
    private static boolean isTopic = false;
    private static int timeout = 5000; // in ms or 15 seconds
    // System exit status value (assume unset value to be 1)
    private static int status = 1;

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        // Parse the arguments
        parseArgs(args);

        // Variables
        Connection connection = null;
        Session session = null;
        Destination destination;
        MessageConsumer consumer = null;

        try {
            // Create a connection factory
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            JmsConnectionFactory cf = ff.createConnectionFactory();

            // Set the properties
            cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
            cf.setIntProperty(WMQConstants.WMQ_PORT, port);
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManagerName);

            long start = Calendar.getInstance().getTimeInMillis();
            System.out.println("About to create the connection ...");
            // Create JMS objects
            connection = cf.createConnection();
            long end = Calendar.getInstance().getTimeInMillis();
            System.out.println("Connection created in " + (end - start) / 1000 + "s and " + (end - start) % 1000 + "ms");
            
            System.out.println("About to create the connection ...");
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            if (isTopic) {
                destination = session.createTopic(destinationName);
            } else {
                destination = session.createQueue(destinationName);
            }
            consumer = session.createConsumer(destination);

            // Start the connection
            connection.start();

            // And, receive the message
            Message message = consumer.receive(timeout);
            if (message != null) {
                System.out.println("Received message:\n" + message);
            } else {
                System.out.println("No message received!\n");
                recordFailure(null);
            }

            recordSuccess();
        } catch (JMSException jmsex) {
            recordFailure(jmsex);
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
                } catch (JMSException jmsex) {
                    System.out.println("Consumer could not be closed.");
                    recordFailure(jmsex);
                }
            }

            if (session != null) {
                try {
                    session.close();
                } catch (JMSException jmsex) {
                    System.out.println("Session could not be closed.");
                    recordFailure(jmsex);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException jmsex) {
                    System.out.println("Connection could not be closed.");
                    recordFailure(jmsex);
                }
            }
        }
        System.exit(status);
    } // end main()

    /**
     * Process a JMSException and any associated inner exceptions.
     *
     * @param jmsex
     */
    private static void processJMSException(JMSException jmsex) {
        System.out.println(jmsex);
        Throwable innerException = jmsex.getLinkedException();
        if (innerException != null) {
            System.out.println("Inner exception(s):");
        }
        while (innerException != null) {
            System.out.println(innerException);
            innerException = innerException.getCause();
        }
    }

    /**
     * Record this run as successful.
     */
    private static void recordSuccess() {
        System.out.println("SUCCESS");
        status = 0;
    }

    /**
     * Record this run as failure.
     *
     * @param ex
     */
    private static void recordFailure(Exception ex) {
        if (ex != null) {
            if (ex instanceof JMSException) {
                processJMSException((JMSException) ex);
            } else {
                System.out.println(ex);
            }
        }
        System.out.println("FAILURE");
        status = -1;

    }

    /**
     * Parse user supplied arguments.
     *
     * @param args
     */
    private static void parseArgs(String[] args) {
        try {
            int length = args.length;
            if (length == 0) {
                return;
                //throw new IllegalArgumentException("No arguments! Mandatory arguments must be specified.");
            }
            if ((length % 2) != 0) {
                throw new IllegalArgumentException("Incorrect number of arguments!");
            }

            int i = 0;

            while (i < length) {
                if ((args[i]).charAt(0) != '-') {
                    throw new IllegalArgumentException("Expected a '-' character next: " + args[i]);
                }

                char opt = (args[i]).toLowerCase().charAt(1);

                switch (opt) {
                    case 'h':
                        host = args[++i];
                        break;
                    case 'p':
                        port = Integer.parseInt(args[++i]);
                        break;
                    case 'l':
                        channel = args[++i];
                        break;
                    case 'm':
                        queueManagerName = args[++i];
                        break;
                    case 'd':
                        destinationName = args[++i];
                        break;
                    default: {
                        throw new IllegalArgumentException("Unknown argument: " + opt);
                    }
                }

                ++i;
            }

            if (queueManagerName == null) {
                throw new IllegalArgumentException("A queueManager name must be specified.");
            }

            if (destinationName == null) {
                throw new IllegalArgumentException("A destination name must be specified.");
            }

            // Whether the destination is a queue or a topic. Apply a simple check.
            if (destinationName.startsWith("topic://")) {
                isTopic = true;
            } else {
                // Otherwise, let's assume it is a queue.
                isTopic = false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printUsage();
            System.exit(-1);
        }
    }

    /**
     * Display usage help.
     */
    private static void printUsage() {
        System.out.println("\nUsage:");
        System.out
                .println("JmsConsumer -m queueManagerName -d destinationName [-h host -p port -l channel]");
    }
} // end class
