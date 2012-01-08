/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author eTeR
 */
public class Robot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CommandLine cl = CommandLine.parse(args);
        if (cl == null) {
            throw new RuntimeException(String.format("Invalid parameters supplied!"));
        }
        
        if (cl.runAsServer()) {
            RobotServer rs = new RobotServer(cl);
            rs.run();
        } else {
            RobotServerConnection connection = new RobotServerConnection(cl.getAddress(), cl.getPortNumber());
            AutomaticRobot robot = new AutomaticRobot(new SmartRobot(new ClientRobot(connection)));

            String secret = robot.findSecret();        
            System.out.println(secret);
        }
    }

    public static class CommandLine {

        private int portNumber;
        private InetAddress address;

        private CommandLine() {
            portNumber = 0;
            address = null;
        }

        public static CommandLine parse(String[] args) {
            CommandLine cl = new CommandLine();

            try {
                if (args.length == 1) {
                    cl.setPortNumber(Integer.parseInt(args[0]));
                } else if (args.length == 2) {
                    cl.setAddress(InetAddress.getByName(args[0]));
                    cl.setPortNumber(Integer.parseInt(args[1]));
                }
            } catch (Exception ex) {
                throw new RuntimeException(String.format("Invalid parameters supplied!"), ex);
            }

            if (cl.getPortNumber() < 3000 || cl.getPortNumber() > 3999) {
                throw new RuntimeException("The port number must be in closed interval <3000,3999>!");
            }

            return cl;
        }

        public int getPortNumber() {
            return portNumber;
        }

        public void setPortNumber(int portNumber) {
            this.portNumber = portNumber;
        }

        public InetAddress getAddress() {
            return address;
        }

        public void setAddress(InetAddress address) {
            this.address = address;
        }
        
        public boolean runAsServer() {
            return (address == null);
        }
    }

    public static enum Direction {

        North, West, South, East, Unknown;
        protected static final List<Direction> directionRotationOrder;
        protected static final EnumMap<Direction, Vector> dirToVec;

        static {
            directionRotationOrder = Arrays.asList(new Direction[]{Direction.North, Direction.West, Direction.South, Direction.East});
            dirToVec = new EnumMap<Direction, Vector>(Direction.class);
            dirToVec.put(Direction.North, new Vector(0, 1));
            dirToVec.put(Direction.East, new Vector(1, 0));
            dirToVec.put(Direction.South, new Vector(0, -1));
            dirToVec.put(Direction.West, new Vector(-1, 0));
            dirToVec.put(Direction.Unknown, new Vector(0, 0));
        }

        public static Direction getNextDirection(Direction current) {
            int directionIndex = directionRotationOrder.indexOf(current);
            return directionRotationOrder.get((directionIndex + 1) % 4);
        }

        public static Vector toVector(Direction direction) {
            return dirToVec.get(direction);
        }

        public static Direction fromVector(Vector vector) {
            for (Entry<Direction, Vector> e : dirToVec.entrySet()) {
                if (e.getValue().equals(vector)) {
                    return e.getKey();
                }
            }
            return null;
        }
    }

    public static interface Handlable {

        public void handle(ResponseHandler handler) throws RobotException;
    }

    public static class Position extends Vector {

        public Position(int x, int y) {
            super(x, y);
        }
    }

    public interface Processable {

        public Response process(RequestProcessor processor);
    }

    public static class RobotInfo {

        protected Position position;
        protected Direction direction;

        public RobotInfo() {
            this(0, 0, Direction.Unknown);
        }

        public RobotInfo(int x, int y, Direction direction) {
            this.position = new Position(x, y);
            this.direction = direction;
        }

        public Position getPosition() {
            return this.position;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction newDirection) {
            this.direction = newDirection;
        }
    }

    public static class StringUtils {

        /**
         * 
         * C&P from http://snippets.dzone.com/posts/show/91 (ygmarchi on Apr 16, 2010 at 10:42)
         * @param s
         * @param delimiter
         * @return
         */
        public static String join(List<? extends CharSequence> s, String delimiter) {
            int capacity = 0;
            int delimLength = delimiter.length();
            Iterator<? extends CharSequence> iter = s.iterator();
            if (iter.hasNext()) {
                capacity += iter.next().length() + delimLength;
            }

            StringBuilder buffer = new StringBuilder(capacity);
            iter = s.iterator();
            if (iter.hasNext()) {
                buffer.append(iter.next());
                while (iter.hasNext()) {
                    buffer.append(delimiter);
                    buffer.append(iter.next());
                }
            }
            return buffer.toString();
        }
    }

    public static interface TcpFormatable {

        public String formatForTcp();

        public boolean parseParamsFromTcp(String params);
    }

    public static class Vector {

        public int x;
        public int y;

        public Vector(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector substract(Vector other) {
            return new Vector(this.x - other.x, this.y - other.y);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Vector other = (Vector) obj;
            if (this.x != other.x) {
                return false;
            }
            if (this.y != other.y) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + this.x;
            hash = 89 * hash + this.y;
            return hash;
        }

        @Override
        public String toString() {
            return String.format("(%3d,%3d)", x, y);
        }
    }

    public static class MissbehavedRequestProcessorException extends RuntimeException {

        public MissbehavedRequestProcessorException(String message) {
            super(message);
        }
    }

    public static class RobotCannotPickUpException extends RobotException {

        public RobotCannotPickUpException(Throwable cause) {
            super(cause);
        }

        public RobotCannotPickUpException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotCannotPickUpException(String message) {
            super(message);
        }

        public RobotCannotPickUpException() {
        }
    }

    public static class RobotCrashedException extends RobotException {

        public RobotCrashedException(Throwable cause) {
            super(cause);
        }

        public RobotCrashedException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotCrashedException(String message) {
            super(message);
        }

        public RobotCrashedException() {
        }
    }

    public static class RobotCrumbledException extends RobotException {

        public RobotCrumbledException(Throwable cause) {
            super(cause);
        }

        public RobotCrumbledException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotCrumbledException(String message) {
            super(message);
        }

        public RobotCrumbledException() {
        }
    }

    public static class RobotException extends Exception {

        public RobotException(Throwable cause) {
            super(cause);
        }

        public RobotException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotException(String message) {
            super(message);
        }

        public RobotException() {
        }
    }

    public static class RobotProcessorDamagedException extends RobotException {

        private int damagedProcessor;

        public RobotProcessorDamagedException(Throwable cause, int damagedProcessor) {
            super(cause);
            this.damagedProcessor = damagedProcessor;
        }

        public RobotProcessorDamagedException(String message, Throwable cause, int damagedProcessor) {
            super(message, cause);
            this.damagedProcessor = damagedProcessor;
        }

        public RobotProcessorDamagedException(String message, int damagedProcessor) {
            super(message);
            this.damagedProcessor = damagedProcessor;
        }

        public RobotProcessorDamagedException(int damagedProcessor) {
            this.damagedProcessor = damagedProcessor;
        }

        public int getDamagedProcessor() {
            return damagedProcessor;
        }

        public void setDamagedProcessor(int damagedProcessor) {
            this.damagedProcessor = damagedProcessor;
        }
    }

    public static class RobotProcessorOkException extends RobotException {

        public RobotProcessorOkException(Throwable cause) {
            super(cause);
        }

        public RobotProcessorOkException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotProcessorOkException(String message) {
            super(message);
        }

        public RobotProcessorOkException() {
        }
    }

    public static class RobotUnknownRequestException extends RobotException {

        public RobotUnknownRequestException(Throwable cause) {
            super(cause);
        }

        public RobotUnknownRequestException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotUnknownRequestException(String message) {
            super(message);
        }

        public RobotUnknownRequestException() {
        }
    }

    public static class RobotUnknownResponseException extends RobotException {

        public RobotUnknownResponseException(Throwable cause) {
        }

        public RobotUnknownResponseException(String message, Throwable cause) {
        }

        public RobotUnknownResponseException(String message) {
        }

        public RobotUnknownResponseException() {
        }
    }

    public static class SocketUtils {

        public static String readStringFromStream(InputStream stream) throws IOException {

            InputStreamReader in = new InputStreamReader(stream, "US-ASCII");
            StringBuilder builder = new StringBuilder();
            try {
                while (true) {
                    int i = in.read();
                    if (i == -1) {
                        throw new IOException("Unexpected end of stream reached!");
                    }

                    char ch = (char) i;
                    if (ch == '\r') {
                        ch = (char) in.read();
                        if (ch == '\n') {
                            break;
                        } else {
                            return "";
                        }
                    }
                    builder.append(ch);
                }
            } catch (EOFException ex) {
                return "";
            }

            return builder.toString();
        }

        public static void sendStringToStream(String data, OutputStream stream) throws IOException {
            stream.write(data.getBytes());
        }
    }

    public static abstract class Request implements TcpFormatable, Processable, Cloneable {

        protected String address;
        protected List<Response> supportedResponses;

        public Request() {
            this("/nobody/");
        }

        public Request(String address) {
            this.address = address;
        }

        @Override
        public Request clone() {
            Request clone = null;
            try {
                clone = (Request) super.clone();
            } catch (CloneNotSupportedException ex) {
            }
            return clone;
        }

        @Override
        public Response process(RequestProcessor processor) {

            Response response = route(processor);
            if (!isResponseSupported(response)) {
                throw new MissbehavedRequestProcessorException(
                        String.format("Unsupported response %s on request %s!",
                        response.getClass().getName(),
                        this.getClass().getName()));
            }

            return response;
        }

        @Override
        public boolean parseParamsFromTcp(String params) {
            return true;
        }

        public String getAdress() {
            return address;
        }

        public void setAdress(String adress) {
            this.address = adress;
        }

        protected boolean isResponseSupported(Response response) {

            if (supportedResponses == null) {
                supportedResponses = getSupportedResponses();
            }

            for (Response supportedResponse : supportedResponses) {
                if (supportedResponse.getClass().equals(response.getClass())) {
                    return true;
                }
            }
            return false;
        }

        protected abstract Response route(RequestProcessor processor);

        protected abstract List<Response> getSupportedResponses();
    }

    public static class RequestPickUp extends Request {

        public RequestPickUp() {
        }

        public RequestPickUp(String address) {
            super(address);
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" ZVEDNI").append("\r\n").toString();
        }

        @Override
        public Response route(RequestProcessor processor) {
            return processor.processPickUp();
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseSuccess(), new ResponseCannotPickUp()});
        }
    }

    public static interface RequestProcessor {

        public Response processStep();

        public Response processTurnLeft();

        public Response processPickUp();

        public Response processProcessorRepair(int processorToRepair);

        public Response processUnknown();

        public String getExpectedAddress();
    }

    public static class RequestRepair extends Request {

        private int processorToRepair;

        public RequestRepair(String address, int processorToRepair) {
            super(address);
            this.processorToRepair = processorToRepair;
        }

        public RequestRepair(int processorToRepair) {
            this("", processorToRepair);
        }

        public RequestRepair() {
            this("", 0);
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" OPRAVIT ").append(processorToRepair).append("\r\n").toString();
        }

        @Override
        public Response route(RequestProcessor processor) {
            return processor.processProcessorRepair(processorToRepair);
        }

        @Override
        public boolean parseParamsFromTcp(String params) {
            try {
                this.processorToRepair = Integer.parseInt(params);
                return ((processorToRepair > 0) && (processorToRepair < 10));
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseOk(), new ResponseProcessorOk()});
        }
    }

    public static class RequestStep extends Request {

        public RequestStep(String address) {
            super(address);
        }

        public RequestStep() {
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" KROK").append("\r\n").toString();
        }

        @Override
        public Response route(RequestProcessor processor) {
            return processor.processStep();
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseOk(), new ResponseCrash(),
                        new ResponseProcessorDamaged(), new ResponseCrumbled()});
        }
    }

    public static class RequestTurnLeft extends Request {

        public RequestTurnLeft(String address) {
            super(address);
        }

        public RequestTurnLeft() {
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" VLEVO").append("\r\n").toString();
        }

        @Override
        public Response route(RequestProcessor processor) {
            return processor.processTurnLeft();
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseOk()});
        }
    }

    public static class RequestUnknown extends Request {

        public RequestUnknown(String address) {
            super(address);
        }

        public RequestUnknown() {
        }

        @Override
        public String formatForTcp() {
            throw new UnsupportedOperationException("Does not make sense.");
        }

        @Override
        public Response route(RequestProcessor processor) {
            return processor.processUnknown();
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseUnknownRequest()});
        }
    }

    public static abstract class Response implements TcpFormatable, Handlable, Cloneable {

        public abstract boolean isEndGame();

        @Override
        public boolean parseParamsFromTcp(String params) {
            return true;
        }

        @Override
        public Response clone() {
            Response clone = null;
            try {
                clone = (Response) super.clone();
            } catch (CloneNotSupportedException ex) {
            }
            return clone;
        }
    }

    public static class ResponseCannotPickUp extends Response {

        @Override
        public String formatForTcp() {
            return new StringBuilder("550 ").append("NELZE ZVEDNOUT ZNACKU").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleCannotPickUp();
        }
    }

    public static class ResponseCrash extends Response {

        @Override
        public String formatForTcp() {
            return new StringBuilder("530 ").append("HAVARIE").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleCrash();
        }
    }

    public static class ResponseCrumbled extends Response {

        @Override
        public String formatForTcp() {
            return new StringBuilder("572 ").append("ROBOT SE ROZPADL").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotCrumbledException {
            handler.handleCrumbled();
        }
    }

    public static interface ResponseHandler {

        public void handleOk(int x, int y);

        public void handleIdentification(String address);

        public void handleSuccess(String secretMessage);

        public void handleUnknownRequest() throws RobotUnknownRequestException;

        public void handleCrash() throws RobotCrashedException;

        public void handleCannotPickUp() throws RobotCannotPickUpException;

        public void handleProcessorDamaged(int damagedProcessor) throws RobotProcessorDamagedException;

        public void handleProcessorOk() throws RobotProcessorOkException;

        public void handleCrumbled() throws RobotCrumbledException;
    }

    public static class ResponseIdentification extends Response {

        protected String address;
        protected static final String idFormatString = "Ahoj kliente! Oslovuj mne %s.";

        public ResponseIdentification(String address) {
            this.address = address;
        }

        public ResponseIdentification() {
            this("");
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder("200 ").append(String.format(idFormatString, getAddress())).append("\r\n").toString();
        }

        public String getAddress() {
            return address;
        }

        @Override
        public boolean parseParamsFromTcp(String params) {
            Pattern pattern = Pattern.compile("Oslovuj mne [^.\n\r$]+[.\n\r$]");
            Matcher match = pattern.matcher(params);
            if (match.find()) {
                List<String> tokens = Arrays.asList(match.group().split(" "));
                String almostAddress = StringUtils.join(tokens.subList(2, tokens.size()), " ");
                pattern = Pattern.compile("[^.\n\r]+");
                match = pattern.matcher(almostAddress);
                if (match.find()) {
                    this.address = match.group();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isEndGame() {
            return false;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleIdentification(address);
        }
    }

    public static class ResponseOk extends Response {

        protected int x;
        protected int y;

        public ResponseOk(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public ResponseOk() {
            this(-1, -1);
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder("240 OK ").append(String.format("(%d,%d)", getX(), getY())).append("\r\n").toString();
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean isEndGame() {
            return false;
        }

        @Override
        public boolean parseParamsFromTcp(String params) {
            Pattern pattern = Pattern.compile("\\(.+\\)");
            Matcher match = pattern.matcher(params);
            if (match.find()) {
                String temp = match.group();
                String status = temp.substring(1, temp.length() - 1);
                String[] tokens = status.split(",");
                if (tokens.length == 2) {
                    try {
                        this.x = Integer.parseInt(tokens[0]);
                        this.y = Integer.parseInt(tokens[1]);
                        return true;
                    } catch (Exception ex) {
                    }
                }
            }
            return false;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleOk(x, y);
        }
    }

    public static class ResponseProcessorDamaged extends Response {

        protected int damagedProcessor;

        public ResponseProcessorDamaged(int damagedProcessor) {
            this.damagedProcessor = damagedProcessor;
        }

        public ResponseProcessorDamaged() {
            this(-1);
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder("580 ").append(String.format("SELHANI PROCESORU %d", getDamagedProcessor())).append("\r\n").toString();
        }

        public int getDamagedProcessor() {
            return damagedProcessor;
        }

        public void setDamagedProcessor(int damagedProcessor) {
            this.damagedProcessor = damagedProcessor;
        }

        @Override
        public boolean isEndGame() {
            return false;
        }

        @Override
        public boolean parseParamsFromTcp(String params) {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher match = pattern.matcher(params);
            if (match.find()) {
                try {
                    this.damagedProcessor = Integer.parseInt(match.group());
                    return true;
                } catch (Exception ex) {
                }
            }
            return false;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleProcessorDamaged(damagedProcessor);
        }
    }

    public static class ResponseProcessorOk extends Response {

        @Override
        public String formatForTcp() {
            return new StringBuilder("571 ").append("PROCESOR FUNGUJE").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleProcessorOk();
        }
    }

    public static class ResponseSuccess extends Response {

        protected String secretString;

        public ResponseSuccess(String secretString) {
            this.secretString = secretString;
        }

        public ResponseSuccess() {
            this("Neinicializovane tajemstvi - programator si nepral zadne sdelit!");
        }

        @Override
        public String formatForTcp() {
            return new StringBuilder("210 ").append("USPECH ").append(secretString).append("\r\n").toString();
        }

        public String getSecretString() {
            return secretString;
        }

        public void setSecretString(String secretString) {
            this.secretString = secretString;
        }

        @Override
        public boolean parseParamsFromTcp(String params) {
            List<String> tokens = Arrays.asList(params.split(" "));
            if (tokens.size() > 1) {
                this.secretString = StringUtils.join(tokens.subList(1, tokens.size()), " ");
                return true;
            }
            return false;
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleSuccess(secretString);
        }
    }

    public static class ResponseUnknown extends Response {

        @Override
        public boolean isEndGame() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String formatForTcp() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            throw new RobotUnknownResponseException();
        }
    }

    public static class ResponseUnknownRequest extends Response {

        @Override
        public String formatForTcp() {
            return new StringBuilder("500 ").append("NEZNAMY PRIKAZ").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return false;
        }

        @Override
        public void handle(ResponseHandler handler) throws RobotException {
            throw new RobotException(new RobotUnknownRequestException());
        }
    }

    public static class ClientRequestFactory {

        private String address;
        private final static Pattern validRequestPattern;
        private final static HashMap<String, Request> prototypes;

        static {
            prototypes = new HashMap<String, Request>();
            prototypes.put("KROK", new RequestStep());
            prototypes.put("VLEVO", new RequestTurnLeft());
            prototypes.put("ZVEDNI", new RequestPickUp());
            prototypes.put("OPRAVIT", new RequestRepair());

            validRequestPattern = Pattern.compile("(.+) (KROK|VLEVO|ZVEDNI|OPRAVIT|NABIT) ?(\\d)?");
        }

        public ClientRequestFactory(String address) {
            this.address = address;
        }

        public Request parseRequest(String rawRequest) throws InvalidAddressException {

            Matcher match = validRequestPattern.matcher(rawRequest);
            if (!match.find()) {
                return new RequestUnknown();
            }

            try {

                Request prototype = prototypes.get(match.group(2));
                if (prototype == null) {
                    return new RequestUnknown();
                }

                if (!address.equals(match.group(1))) {
                    throw new InvalidAddressException(match.group(1), address, rawRequest);
                }

                Request request = prototype.clone();
                request.setAdress(address);
                if (request.parseParamsFromTcp(match.groupCount() == 3 ? match.group(3) : "")) {
                    return request;
                } else {
                    return new RequestUnknown();
                }


            } catch (Exception ex) {
                return new RequestUnknown();
            }
        }
    }

    public static class ClientRequestProcessor implements RequestProcessor {

        private ServerRobot robot;

        public ClientRequestProcessor(ServerRobot robot) {
            this.robot = robot;
        }

        @Override
        public Response processPickUp() {
            try {
                String secretMessage = robot.pickUp();
                return new ResponseSuccess(secretMessage);
            } catch (RobotCannotPickUpException ex) {
                return new ResponseCannotPickUp();
            }
        }

        @Override
        public Response processProcessorRepair(int processorToRepair) {
            try {
                RobotServerInfo info = robot.repair(processorToRepair);
                return new ResponseOk(info.getPosition().x, info.getPosition().y);
            } catch (RobotProcessorOkException ex) {
                return new ResponseProcessorOk();
            }
        }

        @Override
        public Response processStep() {
            try {
                RobotServerInfo info = robot.doStep();
                return new ResponseOk(info.getPosition().x, info.getPosition().y);
            } catch (RobotCrashedException ex) {
                return new ResponseCrash();
            } catch (RobotCrumbledException ex) {
                return new ResponseCrumbled();
            } catch (RobotProcessorDamagedException ex) {
                return new ResponseProcessorDamaged(ex.getDamagedProcessor());
            }
        }

        @Override
        public Response processTurnLeft() {
            RobotServerInfo info = robot.turnLeft();
            return new ResponseOk(info.getPosition().x, info.getPosition().y);
        }

        @Override
        public Response processUnknown() {
            return new ResponseUnknownRequest();
        }

        @Override
        public String getExpectedAddress() {
            return robot.getName();
        }
    }

    public static class ServerRobot implements Comparable<ServerRobot> {

        private String name;
        private RobotServerInfo info;
        private RobotState currentState;

        public ServerRobot(String name) {
            this.name = name;

            /**
             * Generate the robot starting position and direction
             * TODO: Move this initialization inside the RobotServerInfo
             */
            int x = (int) Math.floor(Math.random() * 43) - (Math.min(Math.abs(RobotServerInfo.MAX_X), Math.abs(RobotServerInfo.MIN_X) - 1));
            int y = (int) Math.floor(Math.random() * 43) - (Math.min(Math.abs(RobotServerInfo.MAX_Y), Math.abs(RobotServerInfo.MIN_Y) - 1));

            Direction direction = Direction.values()[(int) Math.floor(Math.random() * 4)];

            this.info = new RobotServerInfo(x, y, direction);
            this.currentState = new RobotOkState();
        }

        public RobotServerInfo doStep() throws RobotCrashedException, RobotCrumbledException, RobotProcessorDamagedException {
            currentState.doStep(this);
            return info;
        }

        public RobotServerInfo turnLeft() {
            currentState.turnLeft(this);
            return info;
        }

        public RobotServerInfo repair(int processorToRepair) throws RobotProcessorOkException {
            currentState.repair(this, processorToRepair);
            return info;
        }

        public String pickUp() throws RobotCannotPickUpException {
            return currentState.pickUp(this);
        }

        public String getName() {
            return name;
        }

        public String getSecretMessage() {
            return SecretMessageProvider.getRandomSecretMessage();
        }

        public RobotState getCurrentState() {
            return currentState;
        }

        public void setCurrentState(RobotState currentState) {
            this.currentState = currentState;
        }

        public RobotServerInfo getInfo() {
            return this.info;
        }

        @Override
        public int compareTo(ServerRobot t) {
            return this.name.compareTo(t.name);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ServerRobot other = (ServerRobot) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }
    }

    public static class RobotClientProcess implements Runnable {

        private Socket clientSocket;
        private InputStream in;
        private OutputStream out;
        private ClientRequestFactory requestFactory;
        private ClientRequestProcessor requestProcessor;
        private ServerRobot robot;
        private Logger log;

        @Override
        public void run() {

            try {

                this.in = clientSocket.getInputStream();
                this.out = clientSocket.getOutputStream();
                log.logMessage("Client connected! Going to send him the robot identification address %s!", robot.getName());
                sendResponseToSocket(new ResponseIdentification(robot.getName()));

                boolean quit = false;
                while (!quit) {

                    try {
                        String rawRequest = readRequestFromSocket();
                        log.logMessage("Read request message %s from the client %s!", rawRequest, clientSocket.getInetAddress());

                        Request request = requestFactory.parseRequest(rawRequest);
                        log.logRequest(request);

                        Response response = request.process(requestProcessor);
                        log.logResponse(response);

                        sendResponseToSocket(response);
                        quit = response.isEndGame();
                    } catch (IOException ex) {
                        clientSocket.close();
                        log.logMessage("Connection lost.");
                        quit = true;
                    } catch (InvalidAddressException iaex) {
                        log.logException(iaex);
                    }
                }

            } catch (Exception ex) {
                log.logException(ex);
            } finally {
                try {
                    goodBye();
                    if (clientSocket != null) {
                        in.close();
                        out.close();
                        clientSocket.close();
                    }
                } catch (Exception ex) {
                    log.logException(ex);
                }
            }
        }

        public RobotClientProcess(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.robot = new ServerRobot(RobotNameProvider.provideName());
            this.requestFactory = new ClientRequestFactory(robot.getName());
            this.requestProcessor = new ClientRequestProcessor(robot);
            this.log = Logger.getLogger(this.robot);
        }

        private String readRequestFromSocket() throws IOException {
            return SocketUtils.readStringFromStream(in);
        }

        private void sendResponseToSocket(Response response) throws IOException {
            SocketUtils.sendStringToStream(response.formatForTcp(), out);
        }

        private void goodBye() {
            RobotNameProvider.freeName(robot.getName());
        }
    }

    public static class RobotNameProvider {

        public static List<String> names;
        public static HashMap<String, Boolean> reservations;

        static {
            names = Arrays.asList(new String[]{
                        "Jardo", "Pepo", "Miso", "Roberte", "Karle",
                        "Lojzo", "Vaclave", "Tomasi", "Robocope", "Optime",
                        "Iron Mane", "Bumblebee", "Martinku", "Chlupatoure Obecny",
                        "Edwarde", "Bello", "Jacobe", "Jaspere"});

            reservations = new HashMap<String, Boolean>();
        }

        public static String provideName() {
            int pick = (int) Math.floor(Math.random() * names.size());
            String name = names.get(pick);
            while (reservations.containsKey(name) && reservations.get(name)) {
                pick = (int) Math.floor(Math.random() * names.size());
                name = names.get(pick);
            }
            reservations.put(name, Boolean.TRUE);
            return name;
        }

        public static void freeName(String name) {
            reservations.put(name, Boolean.FALSE);
        }
    }

    public static class RobotOkState implements RobotState {

        @Override
        public void doStep(ServerRobot robot) throws RobotCrashedException, RobotProcessorDamagedException {
            RobotServerInfo info = robot.getInfo();

            boolean robotDamaged = Math.ceil(Math.random() * 10) <= (info.getStepsSoFar() % 10);
            if (robotDamaged) {
                int damagedProcessor = damageRobotProcessor(robot);
                throw new RobotProcessorDamagedException(damagedProcessor);
            }

            try {
                info.move();
                info.setStepsSoFar(info.getStepsSoFar() + 1);
            } catch (RobotOutOfFieldException ex) {
                throw new RobotCrashedException(ex);
            }
        }

        @Override
        public void turnLeft(ServerRobot robot) {
            robot.getInfo().turn();
        }

        @Override
        public void repair(ServerRobot robot, int processorToRepair) throws RobotProcessorOkException {
            throw new RobotProcessorOkException();
        }

        @Override
        public String pickUp(ServerRobot robot) throws RobotCannotPickUpException {
            if (robot.getInfo().getPosition().x != 0 || robot.getInfo().getPosition().y != 0) {
                throw new RobotCannotPickUpException();
            }

            return robot.getSecretMessage();
        }

        private int damageRobotProcessor(ServerRobot robot) {
            int damagedProcessor = (int) Math.ceil(Math.random() * 8) + 1;
            robot.setCurrentState(new RobotProcessorDamagedState(damagedProcessor));
            return damagedProcessor;
        }
    }

    public static class RobotProcessorDamagedState implements RobotState {

        private int damagedProcessor;

        public RobotProcessorDamagedState(int damagedProcessor) {
            assert ((damagedProcessor > 0) && (damagedProcessor < 10));
            this.damagedProcessor = damagedProcessor;
        }

        @Override
        public void doStep(ServerRobot robot) throws RobotCrumbledException {
            throw new RobotCrumbledException();
        }

        @Override
        public void turnLeft(ServerRobot robot) {
            robot.getInfo().turn();
        }

        @Override
        public void repair(ServerRobot robot, int processorToRepair) throws RobotProcessorOkException {
            if (damagedProcessor != processorToRepair) {
                throw new RobotProcessorOkException();
            }
            robot.getInfo().setStepsSoFar(0);
            robot.setCurrentState(new RobotOkState());
        }

        @Override
        public String pickUp(ServerRobot robot) throws RobotCannotPickUpException {
            if (robot.getInfo().getPosition().x != 0 || robot.getInfo().getPosition().y != 0) {
                throw new RobotCannotPickUpException();
            }

            return robot.getSecretMessage();
        }
    }

    public static class RobotServer {

        private int listeiningPort;

        public RobotServer(CommandLine params) {
            this.listeiningPort = params.getPortNumber();
        }

        public void run() {
            try {
                boolean quit = false;
                ServerSocket ss = new ServerSocket(listeiningPort);

                while (!quit) {
                    System.out.println("Listening ...");
                    final Socket sock = ss.accept();
                    Thread t = new Thread(new RobotClientProcess(sock));
                    t.start();
                }

                ss.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static class RobotServerInfo extends RobotInfo {

        private int stepsSoFar;
        public static final int MAX_X = 22;
        public static final int MAX_Y = 22;
        public static final int MIN_X = -22;
        public static final int MIN_Y = -22;

        public RobotServerInfo(int x, int y, Direction direction) {
            super(x, y, direction);
            this.stepsSoFar = 0;
        }

        public void move() throws RobotOutOfFieldException {
            Vector vec = Direction.toVector(direction);
            Position pos = getPosition();
            pos.x = pos.x + vec.x;
            pos.y = pos.y + vec.y;
            if (pos.x < MIN_X || pos.x > MAX_X || pos.y < MIN_Y || pos.y > MAX_Y) {
                throw new RobotOutOfFieldException(pos.x, pos.y);
            }
        }

        public void turn() {
            direction = Direction.getNextDirection(direction);
        }

        public int getStepsSoFar() {
            return stepsSoFar;
        }

        public void setStepsSoFar(int stepsSoFar) {
            this.stepsSoFar = stepsSoFar;
        }
    }

    public static interface RobotState {

        public void doStep(ServerRobot robot) throws RobotCrashedException, RobotCrumbledException, RobotProcessorDamagedException;

        public void turnLeft(ServerRobot robot);

        public String pickUp(ServerRobot robot) throws RobotCannotPickUpException;

        public void repair(ServerRobot robot, int processorToRepair) throws RobotProcessorOkException;
    }

    public static class SecretMessageProvider {

        private static List<String> messages = Arrays.asList(new String[]{
                    "This is so secret that I would have to kill you if I tell it.",
                    "You really thought that this is secret, huh?",
                    "My secret is that I have no secrets!",
                    "Tell me your secret at first!",
                    "Edward dates Bella as well as Robert Pattison dates Christine Steward!"
                });

        public static synchronized String getRandomSecretMessage() {
            return messages.get((int) Math.floor(Math.random() * messages.size()));
        }
    }

    public static class InvalidAddressException extends Exception {

        private String requestedAddress;
        private String expectedAddress;
        private String receivedRequest;

        public InvalidAddressException(String requested, String expected, String receivedRequest) {
            this.requestedAddress = requested;
            this.expectedAddress = expected;
            this.receivedRequest = receivedRequest;
        }

        public String getRequestedAddress() {
            return this.requestedAddress;
        }

        public String getExpectedAddress() {
            return expectedAddress;
        }

        public String getReceivedRequest() {
            return receivedRequest;
        }
    }

    public static class RobotOutOfFieldException extends Exception {

        private int x;
        private int y;

        public RobotOutOfFieldException(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static class Logger {

        protected ServerRobot robot;
        protected static HashMap<ServerRobot, Logger> loggers;

        static {
            loggers = new HashMap<ServerRobot, Logger>();
        }

        public static synchronized Logger getLogger(ServerRobot robot) {
            if (loggers.containsKey(robot)) {
                return loggers.get(robot);
            } else {
                Logger log = new Logger(robot);
                loggers.put(robot, log);
                return log;
            }
        }

        private Logger(ServerRobot robot) {
            this.robot = robot;
        }

        public void logRequest(Request request) {
            log(String.format("Received request %s addressed to %s!", request.getClass().getSimpleName(), request.getAdress()));
        }

        public void logResponse(Response response) {
            log(String.format("Sent response %s. This %s close the connection!", response.getClass().getSimpleName(), response.isEndGame() ? "will" : "will not"));
        }

        public void logException(Throwable exception) {
            log(formatException(exception));
        }

        public void logMessage(String message, Object... args) {
            log(String.format(message, args));
        }

        private void log(String message) {
            System.out.println(String.format("[%s] | %s | %s | %s", robot.getName(), robot.getInfo().getPosition(), robot.getInfo().getDirection(), message));
        }

        /**
         * Formats the given <code>Exception</code> in a unified manner.
         *
         * @param ex <code>Exception</code> to be formatted.
         * @return The <code>Exception</code> formatted <code>String</code> representation.
         */
        private String formatException(Throwable ex) {
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
    }

    public static class UnexpectedException extends RuntimeException {

        public UnexpectedException(Throwable cause) {
        }

        public UnexpectedException(String message, Throwable cause) {
        }

        public UnexpectedException(String message) {
        }

        public UnexpectedException() {
        }
    }

    public static class UnexpectedResponseException extends RuntimeException {

        public UnexpectedResponseException(Throwable cause) {
        }

        public UnexpectedResponseException(String message, Throwable cause) {
        }

        public UnexpectedResponseException(String message) {
        }

        public UnexpectedResponseException() {
        }
    }

    public static class AutomaticRobot {

        SmartRobot robot;

        public AutomaticRobot(SmartRobot robot) {
            this.robot = robot;
        }

        public String findSecret() {
            RobotInfo info = robot.initialize();

            int xPos = info.getPosition().x;
            while (xPos != 0) {
                if (xPos > 0) {
                    robot.stepLeft();
                    xPos--;
                } else {
                    robot.stepRight();
                    xPos++;
                }
            }

            int yPos = info.getPosition().y;
            while (yPos != 0) {
                if (yPos > 0) {
                    robot.stepDown();
                    yPos--;
                } else {
                    robot.stepUp();
                    yPos++;
                }
            }

            return robot.pickUp();
        }
    }

    public static class ClientRobot {

        private RobotServerConnection server;
        private ServerResponseHandler handler;
        private String name;
        private String secretMessage;
        private RobotInfo info;

        public ClientRobot(RobotServerConnection server) {
            this.server = server;
            this.handler = new ServerResponseHandler(this);
            this.info = new RobotInfo();
        }

        public void initialize() {
            try {
                Response res = this.server.connect();
                res.handle(handler);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            } catch (RobotException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public RobotInfo doStep() throws RobotCrashedException, RobotCrumbledException, RobotProcessorDamagedException {
            try {
                Response res = server.processRequest(new RequestStep(name));
                res.handle(handler);
                return info;
            } catch (RobotCrumbledException ex) {
                throw ex;
            } catch (RobotCrashedException ex) {
                throw ex;
            } catch (RobotProcessorDamagedException ex) {
                throw ex;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public RobotInfo turnLeft() {
            try {
                Response res = server.processRequest(new RequestTurnLeft(name));
                res.handle(handler);
                return this.info;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public String pickUp() throws RobotCannotPickUpException {
            try {
                Response res = server.processRequest(new RequestPickUp(name));
                res.handle(handler);
                return secretMessage; // secret message filled by the handler
            } catch (RobotCannotPickUpException ex) {
                throw ex;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public void repair(int processorToRepair) throws RobotProcessorOkException {
            try {
                Response res = server.processRequest(new RequestRepair(name, processorToRepair));
                res.handle(handler);
            } catch (RobotProcessorOkException ex) {
                throw ex;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public String getSecretMessage() {
            return secretMessage;
        }

        public void setSecretMessage(String secretMessage) {
            this.secretMessage = secretMessage;
        }

        public Position getPos() {
            Position pos = this.info.getPosition();
            return new Position(pos.x, pos.y);
        }

        public void setPos(Position position) {
            Position pos = this.info.getPosition();
            pos.x = position.x;
            pos.y = position.y;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public RobotServerConnection getServer() {
            return server;
        }

        public Direction getDirection() {
            return this.info.getDirection();
        }

        public RobotInfo getInfo() {
            return info;
        }

        public void setDirection(Direction direction) {
            this.info.setDirection(direction);
        }
    }

    public static class RobotServerConnection {

        private InetAddress address;
        private int port;
        private Socket socket;
        private InputStream in;
        private OutputStream out;
        private ServerResponseFactory factory;

        public RobotServerConnection(InetAddress address, int port) {
            this.address = address;
            this.port = port;
            this.factory = new ServerResponseFactory();
        }

        public Response connect() throws IOException {
            this.socket = new Socket(address, port);
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
            String rawResponse = SocketUtils.readStringFromStream(in);
            return factory.parseResponse(rawResponse);
        }

        public void disconnect() {
            try {
                this.in.close();
                this.out.close();
                this.socket.close();
            } catch (Exception ex) {
            }
        }

        public Response processRequest(Request req) throws IOException {
            String strReq = req.formatForTcp();
            SocketUtils.sendStringToStream(strReq, out);
            System.out.printf("Sent: %s", strReq);
            String rawResponse = SocketUtils.readStringFromStream(in);
            System.out.printf("Received: %s\n\n", rawResponse);
            return factory.parseResponse(rawResponse);
        }
    }

    public static class ServerResponseFactory {

        private final static HashMap<String, Response> prototypes;

        static {
            prototypes = new HashMap<String, Response>();
            prototypes.put("200", new ResponseIdentification());
            prototypes.put("210", new ResponseSuccess());
            prototypes.put("240", new ResponseOk());
            prototypes.put("500", new ResponseUnknownRequest());
            prototypes.put("530", new ResponseCrash());
            prototypes.put("550", new ResponseCannotPickUp());
            prototypes.put("580", new ResponseProcessorDamaged());
            prototypes.put("571", new ResponseProcessorOk());
            prototypes.put("572", new ResponseCrumbled());
        }

        public Response parseResponse(String rawResponse) {

            try {

                List<String> tokens = Arrays.asList(rawResponse.split(" "));

                Response prototype = prototypes.get(tokens.get(0));
                if (prototype == null) {
                    return new ResponseUnknown();
                }

                Response response = prototype.clone();
                if (response.parseParamsFromTcp(StringUtils.join(tokens.subList(1, tokens.size()), " "))) {
                    return response;
                } else {
                    return new ResponseUnknown();
                }

            } catch (Exception ex) {
                return new ResponseUnknown();
            }
        }
    }

    public static class ServerResponseHandler implements ResponseHandler {

        private ClientRobot robot;

        public ServerResponseHandler(ClientRobot robot) {
            this.robot = robot;
        }

        @Override
        public void handleCannotPickUp() throws RobotCannotPickUpException {
            throw new RobotCannotPickUpException("Pick up command issued without robot standing on 0,0!");
        }

        @Override
        public void handleCrash() throws RobotCrashedException {
            throw new RobotCrashedException("Robot paced out from the field!");
        }

        @Override
        public void handleCrumbled() throws RobotCrumbledException {
            throw new RobotCrumbledException("The robot attempted to perform such operation which made him crumbled!");
        }

        @Override
        public void handleProcessorDamaged(int damagedProcessor) throws RobotProcessorDamagedException {
            throw new RobotProcessorDamagedException("The robot has damaged processor!", damagedProcessor);
        }

        @Override
        public void handleIdentification(String address) {
            this.robot.setName(address);
        }

        @Override
        public void handleProcessorOk() throws RobotProcessorOkException {
            throw new RobotProcessorOkException("Repair command issued on processor that has no damage!");
        }

        @Override
        public void handleOk(int x, int y) {
            this.robot.setPos(new Position(x, y));
        }

        @Override
        public void handleSuccess(String secretMessage) {
            this.robot.setSecretMessage(secretMessage);
        }

        @Override
        public void handleUnknownRequest() throws RobotUnknownRequestException {
            throw new RobotUnknownRequestException("Unknown request sent by client! Programmer error?");
        }
    }

    public static class SmartRobot {

        private ClientRobot robot;

        public SmartRobot(ClientRobot robot) {
            this.robot = robot;
        }

        public RobotInfo initialize() {
            this.robot.initialize();
            turn();
            Position initialPosition = this.robot.getPos();
            step();
            Position afterFirstStep = this.robot.getPos();
            Direction direction = determineDirection(initialPosition, afterFirstStep);
            this.robot.setDirection(direction);
            return this.robot.getInfo();
        }

        public void stepUp() {
            turn(Direction.North);
            step();
        }

        public void stepLeft() {
            turn(Direction.West);
            step();
        }

        public void stepRight() {
            turn(Direction.East);
            step();
        }

        public void stepDown() {
            turn(Direction.South);
            step();
        }

        public String pickUp() {
            try {
                return robot.pickUp();
            } catch (RobotCannotPickUpException ex) {
                throw new UnexpectedException("Robot tried to pick up the secret message on place where it isn't allowed! Connection lost!", ex);
            }
        }

        private void turn(Direction direction) {
            Direction currentDir = robot.getDirection();
            while (!currentDir.equals(direction)) {
                turn();
                currentDir = Direction.getNextDirection(currentDir);
            }
            robot.setDirection(currentDir);
        }

        private void turn() {
            robot.turnLeft();
        }

        private void step() {
            RobotInfo info = null;
            do {
                try {
                    info = robot.doStep();
                } catch (RobotCrashedException ex) {
                    throw new UnexpectedException("Robot stepped out of the field! Connection lost!", ex);
                } catch (RobotCrumbledException ex) {
                    throw new UnexpectedException("Robot crumbled while trying to do a step! Connection lost!", ex);
                } catch (RobotProcessorDamagedException ex) {
                    repair(ex.getDamagedProcessor());
                }
            } while (info == null);
        }

        private void repair(int processorToRepair) {
            boolean repaired = false;
            do {
                try {
                    robot.repair(processorToRepair);
                    repaired = true;
                } catch (RobotProcessorOkException ex) {
                    throw new UnexpectedException("Attempted to repair processor, which is not damaged! Unable to recover!", ex);
                }
            } while (!repaired);
        }

        private Direction determineDirection(Position from, Position to) {
            Direction dir = Direction.fromVector(to.substract(from));
            if (dir == null || dir.equals(Direction.Unknown)) {
                throw new UnexpectedException("Unable to determine the robot direction! Unable to recover!");
            }
            return dir;
        }
    }
}
