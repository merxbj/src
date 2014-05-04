/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr.wo.core;

import java.io.StringReader;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author jm185267
 */
public class OrderStatusWatcher {

    private MqConnectionProxy proxy;
    private String watchedStore;
    private ConcurrentHashMap<Integer, String> watched = new ConcurrentHashMap<Integer, String>();
    private final Object receiveSync = new Object();
    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private Thread receiveThread;
    private ExecutorService watchThreadService = Executors.newCachedThreadPool();
    private final Object watchSync = new Object();
    private final long timeOutInMillis;
    private XPathExpression rootXPath;
    private XPathExpression sequenceIdXPath;
    private XPathExpression statusXPath;

    public OrderStatusWatcher(MqConnectionProxy proxy, String watchedStore, long timeOutInMillis) {
        this.proxy = proxy;
        this.watchedStore = watchedStore;
        this.timeOutInMillis = timeOutInMillis;

        try {
            XPath xp = XPathFactory.newInstance().newXPath();
            xp.setNamespaceContext(new NamespaceContext() {
                @Override
                public String getNamespaceURI(String prefix) {
                    if (prefix.equals("ob")) {
                        return "http://radiantsystems.com/OrderBridge";
                    }
                    return "";
                }

                @Override
                public String getPrefix(String namespaceURI) {
                    if (namespaceURI.equals("http://radiantsystems.com/OrderBridge")) {
                        return "ob";
                    }
                    return "";
                }

                @Override
                public Iterator getPrefixes(String namespaceURI) {
                    return null;
                }
            });

            sequenceIdXPath = xp.compile("/ob:OrderBridgeResponse/@SequenceId");
            statusXPath = xp.compile("/ob:OrderBridgeResponse/Status");
            rootXPath = xp.compile("/");
        } catch (XPathExpressionException xpee) {
            throw new RuntimeException("Failed to instantiate OrderStatusWatcher", xpee);
        }

    }
    
    public void waitForExit() {
        
        try {
            watchThreadService.awaitTermination(timeOutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            // whatever, just leave
        }
        
        shutdown.set(true);
        synchronized (receiveSync) {
            receiveSync.notifyAll();
        }
        synchronized (watchSync) {
            watchSync.notifyAll();
        }
        try {
            receiveThread.join(timeOutInMillis);
            watchThreadService.shutdownNow();
        } catch (Exception ex) {
            // whatever, just leave
        }
    }

    public void startReceiving() throws JMSException, InterruptedException {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiveThread();
            }
        });
        receiveThread.start();
    }

    private void receiveThread() {
        System.out.println("[TOSTOREACK] Receiving acknowledgements started ...");
        try {
            boolean over = false;
            while (!over) {
                
                over = shutdown.get();
                if (!over) {
                    TextMessage msg = proxy.receive();
                    if (msg != null) {
                        handleMessage(msg);
                    } else {
                        synchronized (receiveSync) {
                            receiveSync.wait(500);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            System.out.println("[TOSTOREACK] Receiving acknowledgements stopped ...");
        }
    }

    private void handleMessage(TextMessage msg) throws JMSException {
        String sendingStore = msg.getStringProperty("storeno");
        if (sendingStore.equals(watchedStore)) {
            try {
                InputSource is = new InputSource(new StringReader(msg.getText()));
                Node root = (Node) rootXPath.evaluate(is, XPathConstants.NODE);
                String sequenceId = sequenceIdXPath.evaluate(root);
                String status = statusXPath.evaluate(root);
                handleAcknowledgement(sequenceId, status);
            } catch (XPathExpressionException ex) {
                System.out.println(ex);
            }
        }
    }
    
    private void handleAcknowledgement(String sequenceIdString, String status) {
        int sequenceId = Integer.parseInt(sequenceIdString);
        watched.put(sequenceId, status);
    }

    public void watch(final int sequenceId) {
        watchThreadService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    watched.put(sequenceId, OrderStatus.SENT);
                    synchronized (watchSync) {
                        watchSync.wait(timeOutInMillis);
                        if (shutdown.get()) {
                            System.out.printf("[TOSTOREACK] Stopped waiting for order %d to be acknowledged due to shutdown signal.", sequenceId);
                            System.out.println("");
                            return;
                        }
                        final String finalStatus = watched.get(sequenceId);
                        if (finalStatus.equals(OrderStatus.SENT)) {
                            System.out.printf("[TOSTOREACK] Timed out waiting for the order %d to be acknowledged.", sequenceId);
                            System.out.println("");
                        } else {
                            System.out.printf("[TOSTOREACK] Received order %d acknowledgement: %s", sequenceId, finalStatus);
                            System.out.println("");
                        }
                    }
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    
    private class OrderStatus {
        public static final String SENT = "Sent";
        public static final String SUCCESS = "Success";
        public static final String DENIED = "Denied";
    }

}
