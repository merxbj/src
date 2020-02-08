/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr.wo.core;

import com.ncr.wo.application.CommandLine;

/**
 *
 * @author jm185267
 */
public class ConnectionInfo {
    private String host = "153.86.210.181";
    private int port = 2550;
    private String channel = "NCR.SVRCONN.DEV";
    private String queueManagerName = "MALCOLM.NCR.QM";
    private String destinationName = "MERXBJ_TOSTORE";
    private String destinationAckName = "MERXBJ_TOSTOREACK";

    public ConnectionInfo(CommandLine cl) {
        this.host = cl.getHost();
        this.port = cl.getPort();
        this.channel = cl.getChannel();
        this.queueManagerName = cl.getQueueManagerName();
        this.destinationName = cl.getDestinationName();
        this.destinationAckName = cl.getDestinationAckName();
    }
    
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getChannel() {
        return channel;
    }

    public String getQueueManagerName() {
        return queueManagerName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getDestinationAckName() {
        return destinationAckName;
    }
}
