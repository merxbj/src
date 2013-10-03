package com.ncr.wo.application;

import com.ncr.wo.core.ConnectionInfo;
import com.ncr.wo.core.MqConnectionProxy;
import com.ncr.wo.core.OrderDispatcher;

public class MalcolmJSimulator {

    public static void main(String[] args) {
        
        CommandLine cl = CommandLine.parse(args);
        MqConnectionProxy proxy = new MqConnectionProxy(new ConnectionInfo(cl));
        OrderDispatcher dispatcher = new OrderDispatcher(proxy, cl.getDestinationStore(), cl.getTimeout());

        try {

            proxy.connect();
            dispatcher.dispatchOrders(cl.getOrderTemplatePath(), cl.getOrderSequenceBegin(), cl.getOrderCount());
            
        } catch (Exception jmsex) {
            FailureHandler.handleFailure(jmsex);
        } finally {
            
        }
        
        System.out.println("SUCCESS");
    }
} // end class
