/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr.wo.core;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author jm185267
 */
public class OrderStatusWatcher {
    
    private MqConnectionProxy proxy;
    private String watchedStore;
    private HashMap<Integer, Boolean> watched = new HashMap<>();
    private final Object receiveSync = new Object();
    private final Object watchSync = new Object();
    XPathExpression sequenceIdXPath;
    XPathExpression statusXPath;

    public OrderStatusWatcher(MqConnectionProxy proxy, String watchedStore) {
        this.proxy = proxy;
        this.watchedStore = watchedStore;
        
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
            
            sequenceIdXPath = xp.compile("/OrderBridgeResponse/@SequenceId");
            statusXPath = xp.compile("/OrderBridgeResponse/Status");
        } catch (XPathExpressionException xpee) {
            throw new RuntimeException("Failed to instantiate ORderStatusWatcher", xpee);
        }
        
    }
    
    public void watch(int sequenceId, long timeoutInMs) {
        watched.put(sequenceId, Boolean.FALSE);
    }
    
    public void startReceiving() throws JMSException, InterruptedException {
        while (true) {
            TextMessage msg;
            while ((msg = proxy.receive()) != null) {
                handleMessage(msg);
            }
            synchronized (receiveSync) {
                receiveSync.wait(1000);
            }
        }
    }
    
    public void startWatching() {
        
    }

    private void handleMessage(TextMessage msg) throws JMSException {
        String sendingStore = msg.getStringProperty("storeno");
        if (sendingStore.equals(watchedStore)) {
            try {
                InputSource is = new InputSource(new StringReader(msg.getText()));
                String sequenceId = sequenceIdXPath.evaluate(is);
                String status = statusXPath.evaluate(is);
                handleAcknowledgement(sequenceId, status);
            } catch (XPathExpressionException ex) {
                // just ignore for now
            }
        }
    }

    private void handleAcknowledgement(String sequenceId, String status) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
