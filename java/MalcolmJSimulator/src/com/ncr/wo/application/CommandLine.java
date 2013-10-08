/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr.wo.application;

/**
 *
 * @author jm185267
 */
public class CommandLine {

    private String host = "153.86.210.181";
    private int port = 2550;
    private String channel = "NCR.SVRCONN.DEV";
    private String queueManagerName = "MALCOLM.NCR.QM";
    private String destinationName = "MERXBJ_TOSTORE";
    private String destinationAckName = "MERXBJ_TOSTOREACK";
    private long timeout = 10000;
    private long expiry = 5000;
    private int orderCount = 1;
    private int orderSequenceBegin = 1;
    private String orderTemplatePath;
    private String destinationStore;

    public static CommandLine parse(String[] args) {
        CommandLine cl = new CommandLine();
        try {
            int length = args.length;
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
                        cl.host = args[++i];
                        break;
                    case 'p':
                        cl.port = Integer.parseInt(args[++i]);
                        break;
                    case 'l':
                        cl.channel = args[++i];
                        break;
                    case 'm':
                        cl.queueManagerName = args[++i];
                        break;
                    case 'd':
                        cl.destinationName = args[++i];
                        break;
                    case 'a':
                        cl.destinationAckName = args[++i];
                        break;
                    case 't':
                        cl.timeout = Integer.parseInt(args[++i]);
                        break;
                    case 'o':
                        cl.orderTemplatePath = args[++i];
                        break;
                    case 'c':
                        cl.orderCount = Integer.parseInt(args[++i]);
                        break;
                    case 'b':
                        cl.orderSequenceBegin = Integer.parseInt(args[++i]);
                        break;
                    case 's':
                        cl.destinationStore = args[++i];
                        break;
                    case 'e':
                        cl.expiry = Integer.parseInt(args[++i]);
                        break;
                    default: {
                        throw new IllegalArgumentException("Unknown argument: " + opt);
                    }
                }

                ++i;
            }

            if (cl.orderTemplatePath == null) {
                throw new IllegalArgumentException(
                        "A order template path must be specified.");
            }
            
            if (cl.destinationStore == null) {
                throw new IllegalArgumentException(
                        "A destination store number must be specified.");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            printUsage();
            System.exit(-1);
        }

        return cl;
    }

    private static void printUsage() {
        System.out.println("\nUsage:\n");
            System.out.println("\tMalcolmJSimulator -o orderTemplatePath -s destinationStore");
            System.out.println("\t[-c orderCount]");
            System.out.println("\t[-b orderSequenceBegin]");
            System.out.println("\t[-m queueManagerName]");
            System.out.println("\t[-d destinationName]");
            System.out.println("\t[-a destinationAckName]");
            System.out.println("\t[-h host]");
            System.out.println("\t[-p port]");
            System.out.println("\t[-l channel]");
            System.out.println("\t[-t timeout]");
            System.out.println("\t[-e messageExpiry]");
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

    public long getTimeout() {
        return timeout;
    }

    public long getExpiry() {
        return expiry;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public String getOrderTemplatePath() {
        return orderTemplatePath;
    }

    public String getDestinationStore() {
        return destinationStore;
    }

    public int getOrderSequenceBegin() {
        return orderSequenceBegin;
    }
}
