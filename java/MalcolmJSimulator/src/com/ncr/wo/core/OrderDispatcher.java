/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr.wo.core;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author jm185267
 */
public class OrderDispatcher {
    
    private OrderSender sender;
    private OrderStatusWatcher watcher;
    
    public OrderDispatcher(MqConnectionProxy proxy, String destinationStore, long orderImportExpiryInMs, OrderStatusWatcher watcher) {
        this.sender = new OrderSender(proxy, destinationStore, orderImportExpiryInMs);
        this.watcher = watcher;
    }

    public void dispatchOrders(String orderTemplatePath, int sequenceBegin, int count) throws Exception {
        String orderTemplate = loadOrderTemplate(orderTemplatePath);
        for (int i = 0; i < count; i++) {
            int orderSequenceNumber = sequenceBegin + i;
            String order = String.format(orderTemplate, orderSequenceNumber);
            sender.send(order);
            watcher.watch(orderSequenceNumber);
            System.out.printf("[TOSTORE] Dispatched order %d out of %d with sequenceId = %d\n", i + 1, count, orderSequenceNumber);
        }
    }

    private String loadOrderTemplate(String orderTemplatePath) throws Exception {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(orderTemplatePath));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return builder.toString();
    }
}
