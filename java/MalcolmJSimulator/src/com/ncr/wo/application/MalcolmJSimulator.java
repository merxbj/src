package com.ncr.wo.application;

import com.ncr.wo.core.ConnectionInfo;
import com.ncr.wo.core.MqConnectionProxy;
import com.ncr.wo.core.OrderDispatcher;
import com.ncr.wo.core.OrderStatusWatcher;

public class MalcolmJSimulator {

    public static void main(String[] args) {
        
        CommandLine cl = CommandLine.parse(args);
        MqConnectionProxy proxy = new MqConnectionProxy(new ConnectionInfo(cl));
        OrderStatusWatcher watcher = new OrderStatusWatcher(proxy, cl.getDestinationStore(), cl.getTimeout());
        OrderDispatcher dispatcher = new OrderDispatcher(proxy, cl.getDestinationStore(), cl.getExpiry(), watcher);

        try {

            proxy.connect();
            watcher.startReceiving();
            dispatcher.dispatchOrders(cl.getOrderTemplatePath(), cl.getOrderSequenceBegin(), cl.getOrderCount());
            watcher.waitForExit();
            
        } catch (Exception jmsex) {
            FailureHandler.handleFailure(jmsex);
        } finally {
            
        }
        
        System.out.println("DONE.");
    }
} // end class
