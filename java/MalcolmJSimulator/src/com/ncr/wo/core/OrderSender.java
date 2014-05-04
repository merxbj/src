/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr.wo.core;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 *
 * @author jm185267
 */
class OrderSender {
    
    private long orderImportExpiryInMs;
    private MqConnectionProxy proxy;
    private String destinationStore;

    public OrderSender(MqConnectionProxy proxy, String destinationStore, long orderImportExpiryInMs) {
        this.proxy = proxy;
        this.destinationStore = destinationStore;
        this.orderImportExpiryInMs = orderImportExpiryInMs;
    }
    
    public void send(String order) throws JMSException {
        TextMessage msg = proxy.createTextMessage();
        msg.setText(order);
        msg.setStringProperty("storeno", destinationStore);
        msg.setStringProperty("TargetFunctionName", "receiveOnlineOrder");
        proxy.send(msg, orderImportExpiryInMs);
    }
}
