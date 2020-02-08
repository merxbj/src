/*
 * Robot
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package robot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class Robot {

    private Robot() {
    }

    public static void main(final String[] args) {

        CommandLine cl = CommandLine.parse(args);
        if (cl == null) {
            CommandLine.printUsage();
            return;
        }

        ProgressLoggerFactory.setLogLevel(cl.getLogLevel());

        FileTransporter transporter = new FileTransporter();

        if (cl.getFirmwareFileName().equals("")) {
            transporter.download(cl.getHostname(), cl.getPort(), cl.getDownloadFileName());
        } else {
            transporter.upload(cl.getHostname(), cl.getPort(), cl.getFirmwareFileName());
        }

    }

    public static class CommandLine {

        private static final int DEFAULT_SERVER_PORT = 3999;
        private static final String DEFAULT_DOWNLOAD_FILE_NAME = "foto.png";
        private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.Verboose;

        public static void printUsage() {
            System.out.println("\n\nUSAGE:");
            System.out.println("\n\tRobot [OPTION]... HOSTNAME [FIRMWARE]");
            System.out.println("\nDESCRIPTION:");
            System.out.println("\tPsiTP4 initiates an UDP based connection with a robot");
            System.out.println("\tlocated on a distant planet in order to download a nice");
            System.out.println("\tsightseeing photo or to upload new firmware.");
            System.out.println("\nOPTIONS:");
            System.out.println("\n\t--port=PORT");
            System.out.println("\t\tExplicitely specify the remote PORT to which the robot");
            System.out.println("\t\tis listening. Otherwise the default value 3999 is assumed.");
            System.out.println("\n\t--download-file-name=FILE");
            System.out.println("\t\tExplicitely specify the local FILE name of downloadded photo.");
            System.out.println("\t\tOtherwise the default value photo.png is assumed.");
            System.out.println("\n\t--log-level=LEVEL");
            System.out.println("\t\tExplicitely specify this client logging LEVEL.");
            System.out.println("\t\tOtherwise the default value Verboose is assumed.");
            System.out.println("\n\t\t\t Verboose\tLogging everything.");
            System.out.println("\n\t\t\t Normal\t\tLogging excludes the in/out traffic.");
            System.out.println("\n\t\t\t Simple\t\tLogging excludes the in/out traffic");
            System.out.println("\t\t\t\t\tand the window slides.");
        }
        private InetAddress hostname;
        private String firmwareFileName;
        private int port;
        private LogLevel logLevel;
        private String downloadFileName;

        public static CommandLine parse(final String[] args) {
            CommandLine cl = new CommandLine();

            try {
                int argi = 0;
                while (argi < args.length) {
                    if (args[argi].startsWith("--port")) {
                        cl.port = Integer.parseInt(args[argi].split("=")[1]);
                        System.out.printf("Overriding the default port with %d\n", cl.port);
                    } else if (args[argi].startsWith("--download-file-name")) {
                        cl.downloadFileName = args[argi].split("=")[1];
                        System.out.printf("Overriding the default download file name with %s\n", cl.downloadFileName);
                    } else if (args[argi].startsWith("--log-level")) {
                        cl.logLevel = LogLevel.fromString(args[argi].split("=")[1]);
                        System.out.printf("Overriding the default log level with %s\n", cl.logLevel.toString());
                    } else if (args[argi].startsWith("--help")) {
                        throw new RuntimeException(); // just to quickly move ahead to print the usage
                    } else if (args[argi].startsWith("-")) {
                        throw new RuntimeException(); // just to quickly move ahead to print the usage
                    } else {
                        break; // no more options - go ahead with regular parameters
                    }
                    argi++;
                }

                cl.hostname = InetAddress.getByName(args[argi++]);
                cl.firmwareFileName = args.length == argi ? "" : args[argi];
            } catch (Exception ex) {
                cl = null;
            }

            return cl;
        }

        public CommandLine() {
            hostname = null;
            firmwareFileName = "";
            port = DEFAULT_SERVER_PORT;
            logLevel = DEFAULT_LOG_LEVEL;
            downloadFileName = DEFAULT_DOWNLOAD_FILE_NAME;
        }

        public final InetAddress getHostname() {
            return hostname;
        }

        public final String getFirmwareFileName() {
            return firmwareFileName;
        }

        public final LogLevel getLogLevel() {
            return logLevel;
        }

        public final int getPort() {
            return port;
        }

        public final String getDownloadFileName() {
            return downloadFileName;
        }

        /**
         * Formats the given <code>Exception</code> in a unified manner.
         *
         * @param ex <code>Exception</code> to be formatted.
         * @return The <code>Exception</code> formatted <code>String</code> representation.
         */
        public static String formatException(final Throwable ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(ex.toString());
            sb.append("\n");
            for (StackTraceElement ste : ex.getStackTrace()) {
                sb.append("\t at ");
                sb.append(ste.toString());
                sb.append("\n");
            }

            Throwable innerException = ex.getCause();
            while (innerException != null) {
                sb.append("\t caused by ");
                sb.append(formatException(innerException));
                sb.append("\n");
                innerException = innerException.getCause();
            }

            return sb.toString();
        }

        public enum LogLevel {

            Verboose, Normal, Simple;

            public static LogLevel fromString(final String rawLogLevel) {
                if (Verboose.toString().startsWith(rawLogLevel)) {
                    return Verboose;
                } else if (Normal.toString().startsWith(rawLogLevel)) {
                    return Normal;
                } else if (Simple.toString().startsWith(rawLogLevel)) {
                    return Simple;
                }

                throw new RuntimeException(String.format("Incorrect logging level specification: %s", rawLogLevel));
            }
        }
    }

    public static class NormalProgressLogger extends VerbooseProgressLogger {

        @Override
        public void onDataGramReceived(final Packet packet) {
        }

        @Override
        public void onDataGramSent(final Packet packet) {
        }
    }

    public static interface ProgressLogger {

        void onWindowSlide(long bytes);

        void onDataGramReceived(final Packet packet);

        void onDataGramSent(final Packet packet);

        void onConnectionOpen(final Connection con);

        void onConnectionClose(final Connection con);

        void onStateEntered(final State entered);

        void onStateExited(final State exited);

        void onConnectionReset(final Connection con);
    }

    public static final class ProgressLoggerFactory {

        private static CommandLine.LogLevel logLevel;
        private static EnumMap<CommandLine.LogLevel, ProgressLogger> loggerCache;

        static {
            loggerCache = new EnumMap<CommandLine.LogLevel, ProgressLogger>(CommandLine.LogLevel.class);
        }

        private ProgressLoggerFactory() {
        }

        public static void setLogLevel(final CommandLine.LogLevel newLogLevel) {
            logLevel = newLogLevel;
        }

        public static ProgressLogger getLogger() {

            if (loggerCache.containsKey(logLevel)) {
                return loggerCache.get(logLevel);
            }

            ProgressLogger newLogger = null;
            switch (logLevel) {
                case Verboose:
                    newLogger = new VerbooseProgressLogger();
                    break;
                case Normal:
                    newLogger = new NormalProgressLogger();
                    break;
                case Simple:
                    newLogger = new SimpleProgressLogger();
                    break;
                default:
                    throw new RuntimeException("Unexpected logging level! Cannnot create logger!");
            }

            loggerCache.put(logLevel, newLogger);

            return newLogger;
        }
    }

    public static class SimpleProgressLogger extends NormalProgressLogger {

        @Override
        public void onWindowSlide(final long bytes) {
        }
    }

    public static class VerbooseProgressLogger implements ProgressLogger {

        private long transferredBytes;

        public VerbooseProgressLogger() {
            this.transferredBytes = 0;
        }

        @Override
        public void onConnectionClose(final Connection con) {
            System.out.println(String.format("Connection (%s) state change: CLOSED", con));
        }

        @Override
        public void onConnectionOpen(final Connection con) {
            System.out.println(String.format("Connection (%s) state change: OPENED", con));
        }

        @Override
        public void onWindowSlide(long bytes) {
            this.transferredBytes += bytes;
            System.out.println(String.format("Sliding window: Just slided %d bytes. Slided %d in total.", bytes, transferredBytes));
        }

        @Override
        public void onDataGramReceived(final Packet packet) {
            System.out.println(String.format("\trcv: %s", packet));
        }

        @Override
        public void onDataGramSent(final Packet packet) {
            System.out.println(String.format("\tsnd: %s", packet));
        }

        @Override
        public void onStateEntered(final State entered) {
            System.out.println(String.format("State machine notification: Entered %s.", entered));
        }

        @Override
        public void onStateExited(final State exited) {
            System.out.println(String.format("State machine notification: Exited %s.", exited));
        }

        @Override
        public void onConnectionReset(final Connection con) {
            System.out.println(String.format("Connection (%s) state change: RESET", con));
        }
    }

    public static interface Connection<P extends Packet> {

        void open() throws TransmissionException;

        void close() throws TransmissionException;

        void reset() throws TransmissionException;

        void send(P packet) throws TransmissionException;

        P receive() throws TransmissionException;

        boolean isConnected();
    }

    public static class FileTransporter {

        private TransmissionFactory transmissionFactory;

        public FileTransporter() {
            this.transmissionFactory = TransmissionFactoryFactory.newTransmissionFactory();
        }

        public void download(InetAddress hostname, int port, String localFileName) {
            Connection connection = transmissionFactory.newConnection(hostname, port);
            TransmissionStateMachine machine = transmissionFactory.newTransmissionStateMachine(connection);
            machine.download(localFileName);
        }

        public void upload(InetAddress hostname, int port, String firmwareFileName) {
            Connection connection = transmissionFactory.newConnection(hostname, port);
            TransmissionStateMachine machine = transmissionFactory.newTransmissionStateMachine(connection);
            machine.upload(firmwareFileName);
        }
    }

    public static interface Packet {

        void deserialize(byte[] data, int length) throws Exception;

        byte[] serialize() throws Exception;
    }

    public static abstract class SlidingWindow {

        public static final short SLIDING_WINDOW_MAX_SIZE = 2048;
        protected UnsignedShort begin;
        protected UnsignedShort end;
        protected UnsignedShort size;
        protected ProgressLogger progressLogger;

        public SlidingWindow(UnsignedShort begin) {
            this.size = new UnsignedShort(SLIDING_WINDOW_MAX_SIZE);
            this.begin = begin;
            this.end = begin;  // the abstract window is by default closed
            this.progressLogger = ProgressLoggerFactory.getLogger();
        }

        public UnsignedShort getBegin() {
            return new UnsignedShort(begin);
        }

        public UnsignedShort getEnd() {
            return new UnsignedShort(end);
        }

        public UnsignedShort getSize() {
            return new UnsignedShort(size);
        }

        public final class Boundraies {

            private UnsignedShort begin;
            private UnsignedShort end;
            private UnsignedShort offset;

            public Boundraies(UnsignedShort begin, UnsignedShort end) {
                this(begin, end, new UnsignedShort(0));
            }

            public Boundraies(UnsignedShort begin, UnsignedShort end, UnsignedShort offset) {
                this.begin = new UnsignedShort(begin);
                this.end = new UnsignedShort(end);
                this.offset = new UnsignedShort(offset);
            }

            public Boundraies normalize() {
                UnsignedShort _offset = begin.lessThanOrEquals(end) ? new UnsignedShort(0) : size;
                UnsignedShort _begin = begin.add(_offset);
                UnsignedShort _end = end.add(_offset);
                return new Boundraies(_begin, _end, _offset);
            }

            public UnsignedShort getBegin() {
                return new UnsignedShort(begin);
            }

            public void setBegin(UnsignedShort begin) {
                this.begin = new UnsignedShort(begin);
            }

            public UnsignedShort getEnd() {
                return new UnsignedShort(end);
            }

            public void setEnd(UnsignedShort end) {
                this.end = new UnsignedShort(end);
            }

            public UnsignedShort getOffset() {
                return new UnsignedShort(offset);
            }

            public void setOffset(UnsignedShort offset) {
                this.offset = new UnsignedShort(offset);
            }
        }
    }

    public static interface TransmissionFactory {

        Connection newConnection(InetAddress hostname, int port);

        TransmissionStateMachine newTransmissionStateMachine(Connection connection);
    }

    public static class TransmissionFactoryFactory {

        private TransmissionFactoryFactory() {
        }

        public static TransmissionFactory newTransmissionFactory() {
            return new PTCPTransmissionFactory();
        }
    }

    public static class UnsignedShort implements Comparable<UnsignedShort> {

        private short value;

        public UnsignedShort() {
            this((short) 0);
        }

        public UnsignedShort(short number) {
            this.value = number;
        }

        public UnsignedShort(int number) {
            this.value = (short) number;
        }

        public UnsignedShort(final UnsignedShort copy) {
            this.value = copy.value;
        }

        @Override
        public int compareTo(UnsignedShort other) {
            Integer t = this.normalizeToInteger();
            Integer o = other.normalizeToInteger();
            return t.compareTo(o);
        }

        public int normalizeToInteger() {
            return ((int) value) & 0xffff;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final UnsignedShort other = (UnsignedShort) obj;
            if (this.value != other.value) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.value;
            return hash;
        }

        @Override
        public String toString() {
            return String.format("%d", normalizeToInteger());
        }

        public short getShortValue() {
            return value;
        }

        public UnsignedShort add(UnsignedShort other) {
            return new UnsignedShort(value + other.value);
        }

        public UnsignedShort add(int other) {
            return new UnsignedShort(value + other);
        }

        public UnsignedShort add(short other) {
            return new UnsignedShort(value + other);
        }

        public UnsignedShort substract(UnsignedShort other) {
            return new UnsignedShort(value - other.value);
        }

        public UnsignedShort substract(int other) {
            return new UnsignedShort(value - other);
        }

        public UnsignedShort substract(short other) {
            return new UnsignedShort(value - other);
        }

        public boolean greaterThan(UnsignedShort other) {
            return (this.compareTo(other) == 1);
        }

        public boolean lessThan(UnsignedShort other) {
            return this.compareTo(other) == -1;
        }

        public boolean greaterThanOrEquals(UnsignedShort other) {
            return ((this.compareTo(other) == 1) || (this.compareTo(other) == 0));
        }

        public boolean lessThanOrEquals(UnsignedShort other) {
            return ((this.compareTo(other) == -1) || (this.compareTo(other) == 0));
        }
    }

    public static class DeserializationException extends TransmissionException {

        public DeserializationException(Throwable cause) {
            super(cause);
        }

        public DeserializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public DeserializationException(String message) {
            super(message);
        }

        public DeserializationException() {
        }
    }

    public static class SerializationException extends TransmissionException {

        public SerializationException(Throwable cause) {
            super(cause);
        }

        public SerializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public SerializationException(String message) {
            super(message);
        }

        public SerializationException() {
        }
    }

    public static class TransmissionException extends Exception {

        public TransmissionException() {
        }

        public TransmissionException(String msg) {
            super(msg);
        }

        public TransmissionException(Throwable cause) {
            super(cause);
        }

        public TransmissionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class PTCPConnection implements Connection<PTCPPacket> {

        private InetAddress hostname;
        private int port;
        private DatagramSocket socket;
        private int id;
        private PTCPConnectionType type;
        private boolean connecting;
        private ProgressLogger progressLogger;
        private UnsignedShort lastSentSeq;
        private int sameSeqSentCount;

        public PTCPConnection(InetAddress hostname, int port) {
            this.hostname = hostname;
            this.port = port;
            this.id = 0;
            this.type = PTCPConnectionType.UNDETERMINED;
            this.progressLogger = ProgressLoggerFactory.getLogger();
            this.lastSentSeq = new UnsignedShort(0);
            this.sameSeqSentCount = 0;
        }

        @Override
        public void open() throws PTCPException {

            if (isConnected()) {
                System.out.println("Attempted to open already opened connection. Nothing has been done.");
                return;
            }

            setConnecting(true);

            try {

                this.socket = new DatagramSocket();
                PTCPOpenConnectionPacket openRequest = new PTCPOpenConnectionPacket(type);
                PTCPPacket openResponse = null;

                boolean connectionEstablished = false;
                while (!connectionEstablished) {
                    this.send(openRequest);
                    openResponse = this.receive();
                    if (gotValidOpenResponse(openRequest, openResponse)) {
                        connectionEstablished = true;
                    }
                }

                if (!connectionEstablished) {
                    throw new PTCPException("Failed to establish a new connection!");
                }

                this.id = openResponse.getCon();
                progressLogger.onConnectionOpen(this);

            } catch (PTCPException ex) {
                throw ex;
            } catch (SocketException ex) {
                throw new PTCPException("Unable to bind the socket!", ex);
            } catch (IOException ex) {
                throw new PTCPException("IOException occured - rcv/snd failed on socket.", ex);
            } catch (Exception ex) {
                throw new PTCPException("Unexpected exception occured!", ex);
            } finally {
                setConnecting(false);
            }

        }

        @Override
        public void close() throws PTCPException {
            socket.close();
            progressLogger.onConnectionClose(this);
        }

        @Override
        public void reset() throws PTCPException {

            PTCPResetPacket resetPacket = new PTCPResetPacket(this.id);
            this.send(resetPacket);

            progressLogger.onConnectionReset(this);
        }

        @Override
        public void send(PTCPPacket packet) throws PTCPException {
            if (isConnected()) {

                if (isConnecting() || (type == PTCPConnectionType.UPLOAD)) {
                    if (sameSeqSentTooManyTimes(packet.getSeq())) {
                        throw new PTCPProtocolException("Tried to send the same sequence number too many times! Thats enough!");
                    }
                }

                try {
                    packet.setCon(this.id);
                    byte[] snd = packet.serialize();
                    DatagramPacket toSend = new DatagramPacket(snd, snd.length, hostname, port);
                    socket.send(toSend);
                    progressLogger.onDataGramSent(packet);
                } catch (IOException ex) {
                    throw new PTCPException("IOException occured - send failed on socket.", ex);
                } catch (SerializationException ex) {
                    throw new PTCPException("Invalid packet format - cannot serialize!", ex);
                }
            } else {
                throw new PTCPException("Tried to send() when !isConnected()!");
            }
        }

        @Override
        public PTCPPacket receive() throws PTCPException {
            if (isConnected()) {
                try {
                    byte[] rcv = new byte[PTCPConstants.PACKET_MAX_SIZE];
                    DatagramPacket response = new DatagramPacket(rcv, rcv.length);
                    socket.setSoTimeout(PTCPConstants.RECEIVE_TIMEOUT_MILI);
                    socket.receive(response);

                    PTCPPacket received = new PTCPPacket();
                    received.deserialize(response.getData(), response.getLength());

                    progressLogger.onDataGramReceived(received);

                    if ((id != 0) && (received.getCon() != id)) {
                        throw new PTCPProtocolException("Received packet addressed to different connection id.");
                    }
                    return received;
                } catch (SocketTimeoutException ste) {
                    return null;
                } catch (IOException ex) {
                    throw new PTCPException("IOException occured - send failed on socket.", ex);
                } catch (DeserializationException ex) {
                    throw new PTCPException("Invalid binary data - cannot deserialize.", ex);
                }
            } else {
                throw new PTCPException("Tried to receive() when !isConnected()!");
            }
        }

        @Override
        public boolean isConnected() {
            return (isConnecting() || (this.id != 0));
        }

        public void setConnectionType(PTCPConnectionType type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public boolean isConnecting() {
            return connecting;
        }

        private void setConnecting(boolean connectiong) {
            this.connecting = connectiong;
        }

        /**
         * 
         * @param openRequest
         * @param openResponse
         * @return 
         */
        private boolean gotValidOpenResponse(PTCPOpenConnectionPacket openRequest, PTCPPacket openResponse) {
            if ((openRequest != null) && (openResponse != null)) {
                try {
                    if (!openResponse.getFlag().equals(PTCPFlag.SYN)) {
                        System.out.println("Server responded with invalid flags! Expected SYN, got " + openResponse.getFlag().toString());
                        return false;
                    }

                    if (!openResponse.getAck().equals(new UnsignedShort(0)) || !openResponse.getSeq().equals(new UnsignedShort(0))) {
                        System.out.printf("Server responded with invalid ACK or SEQ value! Expected 0 and 0, got %s and %s\n", openResponse.getAck(), openResponse.getSeq());
                        return false;
                    }

                    if (!PTCPConnectionType.fromDataArray(openResponse.getData()).equals(type)) {
                        System.out.println("Server responded with SYN packet with command that does not match the requested command.");
                        return false;
                    }

                    if (openResponse.getCon() == 0) {
                        System.out.println("Server responded with SYN packet with zero connection id.");
                        return false;
                    }

                    return true;
                } catch (Exception ex) {
                    System.out.println(CommandLine.formatException(ex));
                }
            }

            return false;
        }

        @Override
        public String toString() {
            return String.format("0x%08X", id);
        }

        private boolean sameSeqSentTooManyTimes(UnsignedShort seq) {
            if (!lastSentSeq.equals(seq)) {
                lastSentSeq = new UnsignedShort(seq);
                sameSeqSentCount = 0;
                return false;
            } else {
                return (++sameSeqSentCount == PTCPConstants.SAME_SEQ_SENT_MAX_COUNT);
            }
        }
    }

    public static enum PTCPConnectionType {

        UNDETERMINED,
        DOWNLOAD,
        UPLOAD;

        public byte[] toDataArray() throws SerializationException {
            byte[] dataArray = new byte[1];
            switch (this) {
                case DOWNLOAD:
                    dataArray[0] = 0x1;
                    break;
                case UPLOAD:
                    dataArray[0] = 0x2;
                    break;
                default:
                    throw new SerializationException("Unexpected connection type information.");
            }
            return dataArray;
        }

        public static PTCPConnectionType fromDataArray(byte[] dataArray) throws DeserializationException {
            if (dataArray.length > 1) {
                throw new DeserializationException("Unexpected length of pseudo-data holding the connection type information.");
            }

            switch (dataArray[0]) {
                case 0x1:
                    return PTCPConnectionType.DOWNLOAD;
                case 0x2:
                    return PTCPConnectionType.UPLOAD;
                default:
                    throw new DeserializationException("Unexpected pseudo-data value holding the connection type information.");
            }
        }
    }

    public static class PTCPConstants {

        public static final int RECEIVE_TIMEOUT_MILI = 100;
        public static final long UPLOAD_WINDOW_SLIDE_TIMEOUT_NANO = 100 * 1000000L;
        public static final long SAME_ACK_RECEIVED_MAX_COUNT = 3;
        public static final int SAME_SEQ_SENT_MAX_COUNT = 20;
        public static final int PACKET_HEADER_SIZE = 9;
        public static final int PACKET_MAX_DATA_SIZE = 256;
        public static final int PACKET_MAX_SIZE = PACKET_HEADER_SIZE + PACKET_MAX_DATA_SIZE;
    }

    public static class PTCPDataUploadPacket extends PTCPPacket {

        public PTCPDataUploadPacket(UnsignedShort seq, byte[] data, int len) {
            setSeq(seq);
            setData(Arrays.copyOf(data, len));
        }
    }

    public static class PTCPFinishedPacket extends PTCPPacket {

        public PTCPFinishedPacket(UnsignedShort seq, UnsignedShort ack) {
            setFlag(PTCPFlag.FIN);
            setSeq(seq);
            setAck(ack);
        }
    }

    public static enum PTCPFlag {

        NONE,
        SYN,
        FIN,
        RST;

        public static PTCPFlag deserialize(InputStream stream) throws DeserializationException {
            DataInputStream in = new DataInputStream(stream);
            try {
                byte raw = (byte) (in.readByte() & 7); // mask out 5 leading values - they are not signficant
                switch (raw) {
                    case 0:
                        return NONE;
                    case 1:
                        return SYN;
                    case 2:
                        return FIN;
                    case 4:
                        return RST;
                    default:
                        throw new DeserializationException("Flags are mixed together! It is allowed to have only one flag set!");
                }
            } catch (IOException ex) {
                throw new DeserializationException("IOException thrown while deserializing the flag!", ex);
            }
        }

        public void serialize(OutputStream stream) throws SerializationException {
            DataOutputStream out = new DataOutputStream(stream);
            try {
                switch (this) {
                    case NONE:
                        out.write(0);
                        break;
                    case SYN:
                        out.write(1);
                        break;
                    case RST:
                        out.write(4);
                        break;
                    case FIN:
                        out.write(2);
                        break;
                    default:
                        throw new SerializationException("Unusable flag used! Cannot serialize!");
                }
            } catch (IOException ex) {
                throw new SerializationException("IOException thrown while serializing the flag!", ex);
            }
        }
    }

    public static class PTCPInboundSlidingWindow extends SlidingWindow {

        protected SortedMap<UnsignedShort, byte[]> currentWindow;
        protected OutputStream data;

        public PTCPInboundSlidingWindow() {
            super(new UnsignedShort(0));
            this.end = begin.add(size);
            this.currentWindow = new TreeMap<UnsignedShort, byte[]>();
            this.data = null;
        }

        public void init(UnsignedShort begin, OutputStream data) {
            this.begin = begin;
            this.data = data;
        }

        public boolean accept(byte[] chunk, UnsignedShort seq) {
            if (fitsToWindow(seq)) {
                currentWindow.put(seq, chunk);
                return true;
            }
            return false;
        }

        public boolean slideWindow() throws PTCPException {
            try {
                long slided = 0;

                while (currentWindow.containsKey(this.begin)) {
                    byte[] chunk = currentWindow.remove(this.begin);
                    data.write(chunk);
                    begin = begin.add(chunk.length);
                    end = end.add(chunk.length);
                    slided += chunk.length;
                }

                if (slided > 0) {
                    progressLogger.onWindowSlide(slided);
                    return true;
                }
            } catch (IOException ex) {
                throw new PTCPException("Unable to write the confirmed data to the given stream!", ex);
            }

            return false;
        }

        protected boolean fitsToWindow(UnsignedShort seq) {
            /*
             * The additive constant will move the renge round the overflow-circle
             * to ensure the invariant that begin < end
             */
            UnsignedShort offset = begin.lessThan(end) ? new UnsignedShort(0) : size;
            UnsignedShort _begin = begin.add(offset);
            UnsignedShort _end = end.add(offset);
            UnsignedShort _seq = seq.add(offset);

            return (_seq.greaterThanOrEquals(_begin) && _seq.lessThan(_end));
        }

        public void finish() {
            try {
                if (data != null) {
                    data.flush();
                    data.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public static class PTCPOpenConnectionPacket extends PTCPPacket {

        public PTCPOpenConnectionPacket(PTCPConnectionType type) throws SerializationException {
            this.setCon(0);
            this.setSeq(new UnsignedShort(0));
            this.setAck(new UnsignedShort(0));
            this.setFlag(PTCPFlag.SYN);
            this.setData(type.toDataArray());
        }
    }

    public static class PTCPOutboundSlidingWindow extends SlidingWindow implements Iterable<PTCPPacket> {

        private TreeMap<UnsignedShort, PTCPPacket> currentWindowCache;
        private InputStream data;

        public PTCPOutboundSlidingWindow() {
            super(new UnsignedShort(0));
            currentWindowCache = new TreeMap<UnsignedShort, PTCPPacket>();
        }

        public void init(InputStream data) throws PTCPException {
            this.data = data;
            refill();
        }

        public boolean acknowledged(UnsignedShort ack) throws PTCPException {
            if (fitsToWindow(ack)) {
                if (slideWindow(ack)) {
                    refill();
                    return true;
                }
            }

            return false;
        }

        private boolean slideWindow(UnsignedShort ack) {
            List<PTCPPacket> ackedPackets = new ArrayList<PTCPPacket>(SLIDING_WINDOW_MAX_SIZE / PTCPConstants.PACKET_MAX_DATA_SIZE);

            Boundraies boundaries = new Boundraies(begin, end).normalize();
            while (boundaries.getBegin().lessThan(ack.add(boundaries.getOffset()))) {
                PTCPPacket ackedPacket = currentWindowCache.remove(begin);
                begin = begin.add(ackedPacket.getData().length);
                ackedPackets.add(ackedPacket);
                boundaries = new Boundraies(begin, end).normalize();
            }

            if (ackedPackets.size() > 0) {
                long totalAckedSize = 0;
                for (PTCPPacket ackedPacket : ackedPackets) {
                    totalAckedSize += ackedPacket.getData().length;
                }
                progressLogger.onWindowSlide(totalAckedSize);
                return true;
            }

            return false;
        }

        private void refill() throws PTCPException {
            try {
                while ((data.available() > 0) && !isWindowFilled()) {
                    byte[] chunk = new byte[PTCPConstants.PACKET_MAX_DATA_SIZE];
                    int len = data.read(chunk);
                    UnsignedShort seq = end;
                    PTCPDataUploadPacket packet = new PTCPDataUploadPacket(seq, chunk, len);
                    currentWindowCache.put(seq, packet);
                    end = end.add(len);
                }
            } catch (IOException ex) {
                throw new PTCPException("Unable to read an additional data from the given stream!", ex);
            }
        }

        @Override
        public Iterator<PTCPPacket> iterator() {
            return currentWindowCache.values().iterator();
        }

        public PTCPPacket getPacketBySequence(UnsignedShort seq) {
            return currentWindowCache.get(seq);
        }

        public boolean isEmpty() {
            boolean isEmpty = true;
            try {
                isEmpty = (currentWindowCache.isEmpty() && (data.available() == 0));
            } catch (IOException ex) {
            }
            return isEmpty;
        }

        protected boolean fitsToWindow(UnsignedShort ack) {
            /*
             * The additive constant will move the renge round the overflow-circle
             * to ensure the invariant that begin < end
             */
            UnsignedShort offset = begin.lessThan(end) ? new UnsignedShort(0) : size;
            UnsignedShort _begin = begin.add(offset);
            UnsignedShort _end = end.add(offset);
            UnsignedShort _ack = ack.add(offset);

            return (_ack.greaterThan(_begin) && _ack.lessThanOrEquals(_end));
        }

        private boolean isWindowFilled() {
            /*
             * The following cast ensures unsigned comparsion of signed numbers.
             * The additive constant will move the renge round the overflow-circle
             * to ensure the invariant that begin < end
             */
            UnsignedShort offset = begin.lessThan(end) ? new UnsignedShort(0) : size;
            UnsignedShort _begin = begin.add(offset);
            UnsignedShort _end = end.add(offset);

            return ((_end.substract(_begin)).equals(size));
        }

        public void finish() {
            try {
                if (data != null) {
                    data.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public static class PTCPPacket implements Packet {

        private int con;
        private UnsignedShort seq;
        private UnsignedShort ack;
        private PTCPFlag flag;
        private byte[] data;

        public PTCPPacket() {
            this.con = 0;
            this.seq = new UnsignedShort(0);
            this.ack = new UnsignedShort(0);
            this.flag = PTCPFlag.NONE;
            this.data = new byte[0];
        }

        @Override
        public void deserialize(byte[] data, int length) throws DeserializationException {

            DataInputStream stream = new DataInputStream(new ByteArrayInputStream(data, 0, length));

            try {
                this.con = stream.readInt();
                this.seq = new UnsignedShort(stream.readShort());
                this.ack = new UnsignedShort(stream.readShort());
                this.flag = PTCPFlag.deserialize(stream);
                this.data = new byte[stream.available()];
                stream.read(this.data);
            } catch (IOException ex) {
                throw new DeserializationException("IOException thrown while deserializing!", ex);
            } finally {
                try {
                    stream.close();
                } catch (IOException ex) {
                }
            }
        }

        @Override
        public byte[] serialize() throws SerializationException {

            byte[] bytes = new byte[9 + this.data.length];
            ByteArrayOutputStream bytesStream = new ByteArrayOutputStream(bytes.length);
            DataOutputStream stream = new DataOutputStream(bytesStream);

            try {
                stream.writeInt(this.con);
                stream.writeShort(this.seq.getShortValue());
                stream.writeShort(this.ack.getShortValue());
                this.flag.serialize(stream);
                stream.write(this.data);
                bytes = bytesStream.toByteArray();
            } catch (IOException ex) {
                throw new SerializationException("IOException thrown while serializing!", ex);
            } finally {
                try {
                    stream.close();
                } catch (IOException ex) {
                }
            }

            return bytes;
        }

        public UnsignedShort getAck() {
            return ack;
        }

        public void setAck(UnsignedShort ack) {
            this.ack = ack;
        }

        public int getCon() {
            return con;
        }

        public void setCon(int con) {
            this.con = con;
        }

        public PTCPFlag getFlag() {
            return flag;
        }

        public void setFlag(PTCPFlag flag) {
            this.flag = flag;
        }

        public UnsignedShort getSeq() {
            return seq;
        }

        public void setSeq(UnsignedShort seq) {
            this.seq = seq;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            if (data.length > PTCPConstants.PACKET_MAX_DATA_SIZE) {
                throw new RuntimeException("Unexpected data size exceeding the MAX_DATA_SIZE!");
            }

            this.data = data;
        }

        @Override
        public String toString() {
            return String.format("con=0x%08X, seq=%s, ack=%s, flg=%s, sze=%d", con, seq, ack, flag, data.length);
        }
    }

    public static class PTCPResetPacket extends PTCPPacket {

        public PTCPResetPacket(int con) {
            setCon(con);
            setFlag(PTCPFlag.RST);
        }
    }

    public static class PTCPResponsePacket extends PTCPPacket {

        public PTCPResponsePacket(UnsignedShort ack) {
            setAck(ack);
        }
    }

    public static class PTCPTransmissionFactory implements TransmissionFactory {

        public PTCPTransmissionFactory() {
        }

        @Override
        public Connection newConnection(InetAddress hostname, int port) {
            return new PTCPConnection(hostname, port);
        }

        @Override
        public TransmissionStateMachine newTransmissionStateMachine(Connection connection) {
            PTCPConnection con = (PTCPConnection) connection;
            if (con == null) {
                throw new RuntimeException("Invalid Connection supplied. Expected PTCPConnection. Programmer error?");
            }
            return new PTCPTransmissionStateMachine(con);
        }
    }

    public static class PTCPConnectionResetException extends PTCPException {

        public PTCPConnectionResetException() {
            super("Connection forcibly hung up by the remote side! Transmission failed!");
        }
    }

    public static class PTCPException extends TransmissionException {

        public PTCPException(Throwable cause) {
            super(cause);
        }

        public PTCPException(String message, Throwable cause) {
            super(message, cause);
        }

        public PTCPException(String message) {
            super(message);
        }

        public PTCPException() {
        }
    }

    public static class PTCPProtocolException extends PTCPException {

        public PTCPProtocolException(String message) {
            super(message);
        }
    }

    public static class Context {

        private State currentState;
        private Connection connection;

        public Context(Connection connection) {
            this.connection = connection;
        }

        public State getCurrentState() {
            return currentState;
        }

        public void setCurrentState(State currentState) {
            this.currentState = currentState;
        }

        public StateTransitionStatus doStateTransition(State newState) {
            if (newState == null) {
                throw new RuntimeException("Attempted to transition to a null state!");
            }

            currentState = newState;
            currentState.setConnection(connection);

            return StateTransitionStatus.Continue;
        }
    }

    public static abstract class State<C extends Connection> {

        protected C connection;

        public C getConnection() {
            return connection;
        }

        public void setConnection(C connection) {
            this.connection = connection;
        }

        public abstract StateTransitionStatus process(Context context);

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    public static enum StateTransitionStatus {

        Continue,
        Finished,
        Aborted
    }

    public static abstract class TransmissionStateMachine {

        protected Context context;
        protected State firstState;
        protected ProgressLogger progressLogger;

        public TransmissionStateMachine(Connection connection) {
            this.context = new Context(connection);
            this.progressLogger = ProgressLoggerFactory.getLogger();
        }

        public void download(String localFileName) {
            if (configureDownload(localFileName)) {
                run();
            }
        }

        public void upload(String firmwareFileName) {
            if (configureUpload(firmwareFileName)) {
                run();
            }
        }

        public void run() {
            StateTransitionStatus transitionStatus = context.doStateTransition(firstState);
            while (transitionStatus == StateTransitionStatus.Continue) {
                State currentState = context.getCurrentState();
                progressLogger.onStateEntered(currentState);
                transitionStatus = currentState.process(context);
                progressLogger.onStateExited(currentState);
            }
        }

        public abstract boolean configureDownload(String localFileName);

        public abstract boolean configureUpload(String firmwareFileName);
    }

    public static class FileDownloadState extends PTCPState {

        private String localFileName;
        private PTCPInboundSlidingWindow window;

        public FileDownloadState(String localFileName) {
            this.localFileName = localFileName;
            this.window = new PTCPInboundSlidingWindow();
        }

        @Override
        public StateTransitionStatus process(Context context) {
            try {

                window.init(new UnsignedShort(0), openFileStream());

                PTCPPacket received = connection.receive();
                while ((received == null) || (received.getFlag() != PTCPFlag.FIN)) {

                    if (received != null) {
                        checkFlags(received.getFlag());

                        if (window.accept(received.getData(), received.getSeq())) {
                            window.slideWindow();
                        }

                        PTCPPacket response = new PTCPResponsePacket(window.getBegin());
                        response.setSeq(received.getAck());
                        connection.send(response);
                    }

                    received = connection.receive();
                }

                return context.doStateTransition(new RemoteSideDisconnectingState(received, window.getBegin()));

            } catch (PTCPProtocolException pex) {
                System.out.println(CommandLine.formatException(pex));
                return context.doStateTransition(new TransmissionAbortedState());
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            } finally {
                window.finish();
            }

            return context.doStateTransition(new TransmissionFailedState());
        }

        private void checkFlags(PTCPFlag ptcpFlag) throws PTCPException {
            if (ptcpFlag.equals(PTCPFlag.RST)) {
                throw new PTCPConnectionResetException();
            } else if (!ptcpFlag.equals(PTCPFlag.NONE)) {
                throw new PTCPProtocolException("Protocol failure! Got unepxected flag during transmission...");
            }
        }

        private OutputStream openFileStream() throws PTCPException {
            try {
                FileOutputStream stream = new FileOutputStream(localFileName);
                return stream;
            } catch (FileNotFoundException ex) {
                throw new PTCPException("Unable to open the download file for writing!", ex);
            }
        }
    }

    public static class FileUploadFinishedState extends PTCPState {

        UnsignedShort finishingSequence;

        public FileUploadFinishedState(UnsignedShort finishingSequence) {
            this.finishingSequence = finishingSequence;
        }

        @Override
        public StateTransitionStatus process(Context context) {

            try {

                boolean remoteSideFinished = false;
                while (!remoteSideFinished) {
                    connection.send(new PTCPFinishedPacket(finishingSequence, new UnsignedShort(0)));
                    PTCPPacket packet = connection.receive();
                    remoteSideFinished = isValidFinishPacket(packet);
                }
                return context.doStateTransition(new TransmissionSuccessfulState());

            } catch (PTCPProtocolException pex) {
                System.out.println(CommandLine.formatException(pex));
                return context.doStateTransition(new TransmissionAbortedState());
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            }

            return context.doStateTransition(new TransmissionFailedState());

        }

        private boolean isValidFinishPacket(PTCPPacket packet) {
            return ((packet != null)
                    && (packet.getSeq().equals(new UnsignedShort(0)))
                    && (packet.getAck().equals(finishingSequence))
                    && (packet.getFlag().equals(PTCPFlag.FIN)
                    && (packet.getData().length == 0)));
        }
    }

    public static class FileUploadState extends PTCPState {

        private String firmwareFileName;
        private UnsignedShort lastReceivedAck = new UnsignedShort(0);
        private int sameAckReceivedCount = 0;
        private PTCPOutboundSlidingWindow window;

        public FileUploadState(String firmwareFileName) {
            this.firmwareFileName = firmwareFileName;
            this.window = new PTCPOutboundSlidingWindow();
        }

        @Override
        public StateTransitionStatus process(Context context) {

            try {
                window.init(openFileStream());

                while (!window.isEmpty()) {
                    sendCurrentWindow();
                    acceptAcknoledgements();
                }

                return context.doStateTransition(new FileUploadFinishedState(window.getEnd()));
            } catch (PTCPProtocolException pex) {
                System.out.println(CommandLine.formatException(pex));
                return context.doStateTransition(new TransmissionAbortedState());
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            } finally {
                window.finish();
            }

            return context.doStateTransition(new TransmissionFailedState());
        }

        private void checkFlags(PTCPFlag ptcpFlag) throws PTCPException {
            if (ptcpFlag.equals(PTCPFlag.RST)) {
                throw new PTCPConnectionResetException();
            } else if (!ptcpFlag.equals(PTCPFlag.NONE)) {
                throw new PTCPException("Protocol failure! Got unepxected flag during transmission...");
            }
        }

        private InputStream openFileStream() throws PTCPException {
            try {
                return new FileInputStream(firmwareFileName);
            } catch (FileNotFoundException ex) {
                throw new PTCPException("Unable to open the firmware file!", ex);
            }
        }

        private void sendCurrentWindow() throws PTCPException {
            // let's fill up the pipeline
            for (PTCPPacket dataPacket : window) {
                connection.send(dataPacket);
            }
        }

        private void acceptAcknoledgements() throws PTCPException {
            long waitingForSlideBeginTime = System.nanoTime();

            boolean windowSlided = false;
            boolean timeoutExpired = false;
            while (!windowSlided && !timeoutExpired) {
                windowSlided = acceptIncomingPacket();
                timeoutExpired = (System.nanoTime() - waitingForSlideBeginTime) >= PTCPConstants.UPLOAD_WINDOW_SLIDE_TIMEOUT_NANO;
            }
        }

        private boolean sameAckReceivedTooManyTimes(UnsignedShort ack, long maxTimes) {
            if (!lastReceivedAck.equals(ack)) {
                lastReceivedAck = ack;
                sameAckReceivedCount = 0;
                return false;
            } else {
                return (++sameAckReceivedCount == maxTimes);
            }
        }

        private boolean acceptIncomingPacket() throws PTCPException {
            PTCPPacket ackPacket = connection.receive();
            if (ackPacket != null) {
                checkFlags(ackPacket.getFlag());
                if (sameAckReceivedTooManyTimes(ackPacket.getAck(), PTCPConstants.SAME_ACK_RECEIVED_MAX_COUNT)) {
                    sendNextPacket(ackPacket.getAck());
                }
                return window.acknowledged(ackPacket.getAck());
            }
            return false;
        }

        private void sendNextPacket(UnsignedShort ack) throws PTCPException {
            PTCPPacket packet = window.getPacketBySequence(ack);
            if (packet != null) {
                connection.send(packet);
            }
        }
    }

    public static abstract class PTCPState extends State<PTCPConnection> {
    }

    public static class PTCPTransmissionStateMachine extends TransmissionStateMachine {

        protected PTCPConnection connection;

        public PTCPTransmissionStateMachine(PTCPConnection connection) {
            super(connection);
            this.connection = connection;
        }

        @Override
        public boolean configureDownload(String localFileName) {
            connection.setConnectionType(PTCPConnectionType.DOWNLOAD);
            firstState = new WaitingForConnectionState(new FileDownloadState(localFileName));

            return true;
        }

        @Override
        public boolean configureUpload(String firmwareFileName) {
            connection.setConnectionType(PTCPConnectionType.UPLOAD);
            firstState = new WaitingForConnectionState(new FileUploadState(firmwareFileName));

            return true;
        }
    }

    public static class RemoteSideDisconnectingState extends PTCPState {

        private PTCPPacket finishedPacket;
        private UnsignedShort expectedFinSeq;

        public RemoteSideDisconnectingState(PTCPPacket finishedPacket, UnsignedShort expectedFinSeq) {
            this.finishedPacket = finishedPacket;
            this.expectedFinSeq = new UnsignedShort(expectedFinSeq);
        }

        @Override
        public StateTransitionStatus process(Context context) {
            try {
                if (!haveValidFinishPacket()) {
                    connection.reset();
                }

                connection.send(new PTCPFinishedPacket(finishedPacket.getAck(), finishedPacket.getSeq()));
                return context.doStateTransition(new TransmissionSuccessfulState());
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            }

            return context.doStateTransition(new TransmissionFailedState());
        }

        private boolean haveValidFinishPacket() {
            return ((finishedPacket.getSeq().equals(expectedFinSeq))
                    && (finishedPacket.getAck().equals(new UnsignedShort(0))));
        }
    }

    public static class TransmissionFailedState extends PTCPState {

        @Override
        public StateTransitionStatus process(Context context) {
            try {
                connection.reset();
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            } finally {
                try {
                    connection.close();
                } catch (PTCPException pex) {
                    System.out.println(CommandLine.formatException(pex));
                }
            }
            return StateTransitionStatus.Aborted;
        }
    }

    public static class TransmissionSuccessfulState extends PTCPState {

        @Override
        public StateTransitionStatus process(Context context) {
            try {
                connection.close();
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            }
            return StateTransitionStatus.Finished;
        }
    }

    public static class WaitingForConnectionState extends PTCPState {

        private State transmissionState;

        public WaitingForConnectionState(State transmissionState) {
            this.transmissionState = transmissionState;
        }

        @Override
        public StateTransitionStatus process(Context context) {
            try {
                connection.open();
                return context.doStateTransition(transmissionState);
            } catch (PTCPProtocolException pe) {
                System.out.println(CommandLine.formatException(pe));
                return context.doStateTransition(new TransmissionAbortedState());
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            }

            return context.doStateTransition(new TransmissionFailedState());
        }
    }

    public static class TransmissionAbortedState extends PTCPState {

        @Override
        public StateTransitionStatus process(Context context) {
            try {
                connection.reset();
            } catch (PTCPException ex) {
                System.out.println(CommandLine.formatException(ex));
            } finally {
                try {
                    connection.close();
                } catch (PTCPException pex) {
                    System.out.println(CommandLine.formatException(pex));
                }
            }
            return context.doStateTransition(new TransmissionFailedState());
        }
    }
}
