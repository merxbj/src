/*
 * Main
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
package robot.client;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.regex.*;
import java.util.Map.Entry;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {

    public static void main(String[] args) {

        final CommandLine cl = CommandLine.parse(args);

        try {

            RobotServerConnection connection = new RobotServerConnection(cl.getAddress(), cl.getPortNumber());
            AutomaticRobot robot = new AutomaticRobot(new SmartRobot(new Robot(connection)));
            System.out.println(robot.findSecret());

        } catch (Exception ex) {
            System.out.println(formatException(ex));
        }

    }

    public static class CommandLine {

        private int portNumber;
        private InetAddress address;

        public static CommandLine parse(String[] args) {
            CommandLine cl = new CommandLine();

            try {
                cl.setAddress(InetAddress.getByName(args[0]));
                cl.setPortNumber(Integer.parseInt(args[1]));
            } catch (Exception ex) {
                throw new RuntimeException("Client parameters are valid ip adress (DNS name) and port number <3500,3800>!", ex);
            }

            if (cl.getPortNumber() < 3500 || cl.getPortNumber() > 3800) {
                throw new RuntimeException("The port number must be in closed interval <3500,3800>!");
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
    }

    public static class Robot {

        private RobotServerConnection server;
        private ServerResponseHandler handler;
        private String name;
        private String secretMessage;
        private RobotInfo info;

        public Robot(RobotServerConnection server) {
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

        public void doStep() throws RobotCrashedException, RobotCrumbledException, RobotBatteryEmptyException, RobotDamagedException {
            try {
                Response res = server.processRequest(new RequestStep(name));
                res.handle(handler);
            } catch (RobotCrumbledException ex) {
                throw ex;
            } catch (RobotCrashedException ex) {
                throw ex;
            } catch (RobotBatteryEmptyException ex) {
                throw ex;
            } catch (RobotDamagedException ex) {
                throw ex;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public void turnLeft() throws RobotBatteryEmptyException {
            try {
                Response res = server.processRequest(new RequestTurnLeft(name));
                res.handle(handler);
            } catch (RobotBatteryEmptyException ex) {
                throw ex;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public void pickUp() throws RobotCannotPickUpException {
            try {
                Response res = server.processRequest(new RequestPickUp(name));
                res.handle(handler);
            } catch (RobotCannotPickUpException ex) {
                throw ex;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public void repair(int blockToRepair) throws RobotNoDamageException {
            try {
                Response res = server.processRequest(new RequestRepair(name, blockToRepair));
                res.handle(handler);
            } catch (RobotNoDamageException ex) {
                throw ex;
            } catch (RobotException ex) {
                throw new UnexpectedResponseException(ex);
            } catch (IOException ex) {
                throw new UnexpectedException(ex);
            }
        }

        public void recharge() throws RobotDamagedException, RobotCrumbledException {
            try {
                Response res = server.processRequest(new RequestRecharge(name));
                res.handle(handler);
            } catch (RobotDamagedException ex) {
                throw ex;
            } catch (RobotCrumbledException ex) {
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

        public Battery getBattery() {
            return new Battery(this.info.getBattery().level);
        }

        public void setBattery(Battery battery) {
            this.info.getBattery().level = battery.level;
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
            SocketUtils.sendStringToStream(req.formatForTcp(), out);
            String rawResponse = SocketUtils.readStringFromStream(in);
            return factory.parseResponse(rawResponse);
        }
    }

    public static class ServerResponseFactory {

        private final static HashMap<String, Response> prototypes;

        static {
            prototypes = new HashMap<String, Response>();
            prototypes.put("220", new ResponseIdentification());
            prototypes.put("221", new ResponseSuccess());
            prototypes.put("250", new ResponseOk());
            prototypes.put("500", new ResponseUnknownRequest());
            prototypes.put("530", new ResponseCrash());
            prototypes.put("540", new ResponseBatteryEmpty());
            prototypes.put("550", new ResponseCannotPickUp());
            prototypes.put("570", new ResponseDamage());
            prototypes.put("571", new ResponseNoDamage());
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

        private Robot robot;

        public ServerResponseHandler(Robot robot) {
            this.robot = robot;
        }

        public void handleBatteryEmpty() throws RobotBatteryEmptyException {
            this.robot.getServer().disconnect();
            throw new RobotBatteryEmptyException("The robot run out of battery!");
        }

        public void handleCannotPickUp() throws RobotCannotPickUpException {
            throw new RobotCannotPickUpException("Pick up command issued without robot standing on 0,0!");
        }

        public void handleCrash() throws RobotCrashedException {
            throw new RobotCrashedException("Robot paced out from the field!");
        }

        public void handleCrumbled() throws RobotCrumbledException {
            throw new RobotCrumbledException("The robot attempted to perform such operation which made him crumbled!");
        }

        public void handleDamage(int damagedBlock) throws RobotDamagedException {
            throw new RobotDamagedException("The robot has damaged block!", damagedBlock);
        }

        public void handleIdentification(String address) {
            this.robot.setName(address);
        }

        public void handleNoDamage() throws RobotNoDamageException {
            throw new RobotNoDamageException("Repair command issued on block that has no damage!");
        }

        public void handleOk(int battery, int x, int y) {
            this.robot.setBattery(new Battery(battery));
            this.robot.setPos(new Position(x, y));
        }

        public void handleSuccess(String secretMessage) {
            this.robot.setSecretMessage(secretMessage);
        }

        public void handleUnknownRequest() throws RobotUnknownRequestException {
            throw new RobotUnknownRequestException("Unknown request sent by client! Programmer error?");
        }
    }

    public static class SmartRobot {

        private Robot robot;

        public SmartRobot(Robot robot) {
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
                robot.pickUp();
                return robot.getSecretMessage();
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
            robot.setDirection(direction);
        }

        private void turn() {
            do {
                try {
                    batteryCheck();
                    robot.turnLeft();
                    return;
                } catch (RobotBatteryEmptyException ex) {
                    throw new UnexpectedException("Robot run out of battery while trying to turn left! Connection lost!", ex);
                }
            } while (true);
        }

        private void step() {
            do {
                try {
                    batteryCheck();
                    robot.doStep();
                    return;
                } catch (RobotBatteryEmptyException ex) {
                    throw new UnexpectedException("Robot run out of batter while trying to do a step! Connection lost!", ex);
                } catch (RobotCrashedException ex) {
                    throw new UnexpectedException("Robot stepped out of the field! Connection lost!", ex);
                } catch (RobotCrumbledException ex) {
                    throw new UnexpectedException("Robot crumbled while trying to do a step! Connection lost!", ex);
                } catch (RobotDamagedException ex) {
                    repair(ex.getDamagedBlock());
                }
            } while (true);
        }

        private void recharge() {
            boolean recharged = false;
            do {
                try {
                    robot.recharge();
                    recharged = true;
                } catch (RobotDamagedException ex) {
                    repair(ex.getDamagedBlock());
                } catch (RobotCrumbledException ex) {
                    throw new UnexpectedException("Robot crumbled during recharging! Connection lost!", ex);
                }
            } while (!recharged);
        }

        private void repair(int blockToRepair) {
            boolean repaired = false;
            do {
                try {
                    robot.repair(blockToRepair);
                    repaired = true;
                } catch (RobotNoDamageException ex) {
                    throw new UnexpectedException("Attempted to repair block, which is not damaged! Unable to recover!", ex);
                }
            } while (!repaired);
        }

        private void batteryCheck() {
            if (robot.getBattery().level <= 10) {
                recharge();
            }
        }

        private Direction determineDirection(Position from, Position to) {
            Direction dir = Direction.fromVector(to.substract(from));
            if (dir == null || dir.equals(Direction.Unknown)) {
                throw new UnexpectedException("Unable to determine the robot direction! Unable to recover!");
            }
            return dir;
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

    public static class UnexpectedException extends RuntimeException {

        public UnexpectedException(Throwable cause) {
            super(cause);
        }

        public UnexpectedException(String message, Throwable cause) {
            super(message, cause);
        }

        public UnexpectedException(String message) {
            super(message);
        }

        public UnexpectedException() {
            super();
        }
    }

    public static class UnexpectedResponseException extends RuntimeException {

        public UnexpectedResponseException(Throwable cause) {
            super(cause);
        }

        public UnexpectedResponseException(String message, Throwable cause) {
            super(message, cause);
        }

        public UnexpectedResponseException(String message) {
            super(message);
        }

        public UnexpectedResponseException() {
            super();
        }
    }

    public static class Battery {

        public int level;

        public Battery(int level) {
            this.level = level;
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

    public static interface Processable {

        public Response process(RequestProcessor processor);
    }

    public static class RobotInfo {

        protected Battery battery;
        protected Position position;
        protected Direction direction;

        public RobotInfo() {
            this(100, 0, 0, Direction.Unknown); // a new robot comes fully charged
        }

        public RobotInfo(int battery, int x, int y, Direction direction) {
            this.battery = new Battery(battery);
            this.position = new Position(x, y);
            this.direction = direction;
        }

        public Battery getBattery() {
            return this.battery;
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
    }

    public static class MissbehavedRequestProcessorException extends RuntimeException {

        public MissbehavedRequestProcessorException(String message) {
            super(message);
        }
    }

    public static class RobotBatteryEmptyException extends RobotException {

        public RobotBatteryEmptyException(Throwable cause) {
            super(cause);
        }

        public RobotBatteryEmptyException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotBatteryEmptyException(String message) {
            super(message);
        }

        public RobotBatteryEmptyException() {
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

    public static class RobotDamagedException extends RobotException {

        private int damagedBlock;

        public RobotDamagedException(Throwable cause, int damagerBlock) {
            super(cause);
            this.damagedBlock = damagerBlock;
        }

        public RobotDamagedException(String message, Throwable cause, int damagerBlock) {
            super(message, cause);
            this.damagedBlock = damagerBlock;
        }

        public RobotDamagedException(String message, int damagerBlock) {
            super(message);
            this.damagedBlock = damagerBlock;
        }

        public RobotDamagedException(int damageBlock) {
            this.damagedBlock = damageBlock;
        }

        public int getDamagedBlock() {
            return damagedBlock;
        }

        public void setDamagedBlock(int damagedBlock) {
            this.damagedBlock = damagedBlock;
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

    public static class RobotNoDamageException extends RobotException {

        public RobotNoDamageException(Throwable cause) {
            super(cause);
        }

        public RobotNoDamageException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotNoDamageException(String message) {
            super(message);
        }

        public RobotNoDamageException() {
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
                        throw new IOException("Unexpected end of stram reached!");
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
            } catch (IOException ex) {
                stream.close();
                throw ex;
            }

            return builder.toString();
        }

        public static void sendStringToStream(String data, OutputStream stream) throws IOException {
            try {
                stream.write(data.getBytes());
            } catch (IOException ex) {
                stream.close();
                throw ex;
            }
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

        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" ZVEDNI").append("\r\n").toString();
        }

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

        public Response processRepair(int blockToRepair);

        public Response processRecharge();

        public Response processUnknown();

        public String getExpectedAddress();
    }

    public static class RequestRecharge extends Request {

        public RequestRecharge(String address) {
            super(address);
        }

        public RequestRecharge() {
        }

        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" NABIT").append("\r\n").toString();
        }

        public Response route(RequestProcessor processor) {
            return processor.processRecharge();
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseOk(), new ResponseCrumbled(), new ResponseDamage()});
        }
    }

    public static class RequestRepair extends Request {

        private int blockToRepair;

        public RequestRepair(String address, int blockToRepair) {
            super(address);
            this.blockToRepair = blockToRepair;
        }

        public RequestRepair(int blockToRepair) {
            this("", blockToRepair);
        }

        public RequestRepair() {
            this("", 0);
        }

        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" OPRAVIT ").append(blockToRepair).append("\r\n").toString();
        }

        public Response route(RequestProcessor processor) {
            return processor.processRepair(blockToRepair);
        }

        @Override
        public boolean parseParamsFromTcp(String params) {
            String[] tokens = params.split(" ");
            if (tokens.length == 1) {
                try {
                    this.blockToRepair = Integer.parseInt(tokens[0]);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
            return false;
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseOk(), new ResponseNoDamage()});
        }
    }

    public static class RequestStep extends Request {

        public RequestStep(String address) {
            super(address);
        }

        public RequestStep() {
        }

        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" KROK").append("\r\n").toString();
        }

        public Response route(RequestProcessor processor) {
            return processor.processStep();
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseOk(), new ResponseCrash(),
                        new ResponseBatteryEmpty(), new ResponseDamage(), new ResponseCrumbled()});
        }
    }

    public static class RequestTurnLeft extends Request {

        public RequestTurnLeft(String address) {
            super(address);
        }

        public RequestTurnLeft() {
        }

        public String formatForTcp() {
            return new StringBuilder(getAdress()).append(" VLEVO").append("\r\n").toString();
        }

        public Response route(RequestProcessor processor) {
            return processor.processTurnLeft();
        }

        @Override
        protected List<Response> getSupportedResponses() {
            return Arrays.asList(new Response[]{new ResponseOk(), new ResponseBatteryEmpty()});
        }
    }

    public static class RequestUnknown extends Request {

        public RequestUnknown(String address) {
            super(address);
        }

        public RequestUnknown() {
        }

        public String formatForTcp() {
            throw new UnsupportedOperationException("Does not make sense.");
        }

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

    public static class ResponseBatteryEmpty extends Response {

        public String formatForTcp() {
            return new StringBuilder("540 ").append("BATERIE PRAZDA").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleBatteryEmpty();
        }
    }

    public static class ResponseCannotPickUp extends Response {

        public String formatForTcp() {
            return new StringBuilder("550 ").append("NELZE ZVEDNOUT ZNACKU").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleCannotPickUp();
        }
    }

    public static class ResponseCrash extends Response {

        public String formatForTcp() {
            return new StringBuilder("530 ").append("HAVARIE").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleCrash();
        }
    }

    public static class ResponseCrumbled extends Response {

        public String formatForTcp() {
            return new StringBuilder("572 ").append("ROBOT SE ROZPADL").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return true;
        }

        public void handle(ResponseHandler handler) throws RobotCrumbledException {
            handler.handleCrumbled();
        }
    }

    public static class ResponseDamage extends Response {

        protected int damagedBlock;

        public ResponseDamage(int damagedBlock) {
            this.damagedBlock = damagedBlock;
        }

        public ResponseDamage() {
            this(-1);
        }

        public String formatForTcp() {
            return new StringBuilder("570 ").append(String.format("PORUCHA BLOK %d", getDamagedBlock())).append("\r\n").toString();
        }

        public int getDamagedBlock() {
            return damagedBlock;
        }

        public void setDamagedBlock(int damagedBlock) {
            this.damagedBlock = damagedBlock;
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
                    this.damagedBlock = Integer.parseInt(match.group());
                    return true;
                } catch (Exception ex) {
                }
            }
            return false;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleDamage(damagedBlock);
        }
    }

    public static interface ResponseHandler {

        public void handleOk(int battery, int x, int y);

        public void handleIdentification(String address);

        public void handleSuccess(String secretMessage);

        public void handleUnknownRequest() throws RobotUnknownRequestException;

        public void handleCrash() throws RobotCrashedException;

        public void handleBatteryEmpty() throws RobotBatteryEmptyException;

        public void handleCannotPickUp() throws RobotCannotPickUpException;

        public void handleDamage(int damagedBlock) throws RobotDamagedException;

        public void handleNoDamage() throws RobotNoDamageException;

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

        public String formatForTcp() {
            return new StringBuilder("220 ").append(String.format(idFormatString, getAddress())).append("\r\n").toString();
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

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleIdentification(address);
        }
    }

    public static class ResponseNoDamage extends Response {

        public String formatForTcp() {
            return new StringBuilder("571 ").append("NENI PORUCHA").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return false;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleNoDamage();
        }
    }

    public static class ResponseOk extends Response {

        protected final static String robotDataFormat = "(%d,%d,%d)";
        protected int remainingBattery;
        protected int x;
        protected int y;

        public ResponseOk(int remainingBattery, int x, int y) {
            this.remainingBattery = remainingBattery;
            this.x = x;
            this.y = y;
        }

        public ResponseOk() {
            this(-1, -1, -1);
        }

        public String formatForTcp() {
            return new StringBuilder("250 ").append("OK ").append(String.format(robotDataFormat, getRemainingBattery(), getX(), getY())).append("\r\n").toString();
        }

        public int getRemainingBattery() {
            return remainingBattery;
        }

        public void setRemainingBattery(int remainingBattery) {
            this.remainingBattery = remainingBattery;
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
                if (tokens.length == 3) {
                    try {
                        this.remainingBattery = Integer.parseInt(tokens[0]);
                        this.x = Integer.parseInt(tokens[1]);
                        this.y = Integer.parseInt(tokens[2]);
                        return true;
                    } catch (Exception ex) {
                    }
                }
            }
            return false;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleOk(remainingBattery, x, y);
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

        public String formatForTcp() {
            return new StringBuilder("221 ").append("USPECH ").append(secretString).append("\r\n").toString();
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

        public void handle(ResponseHandler handler) throws RobotException {
            handler.handleSuccess(secretString);
        }
    }

    public static class ResponseUnknown extends Response {

        @Override
        public boolean isEndGame() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public String formatForTcp() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void handle(ResponseHandler handler) throws RobotException {
            throw new RobotUnknownResponseException();
        }
    }

    public static class ResponseUnknownRequest extends Response {

        public String formatForTcp() {
            return new StringBuilder("500 ").append("NEZNAMY PRIKAZ ").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return false;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            throw new RobotException(new RobotUnknownRequestException());
        }
    }

    /**
     * Formats the given <code>Exception</code> in a unified manner.
     *
     * @param ex <code>Exception</code> to be formatted.
     * @return The <code>Exception</code> formatted <code>String</code> representation.
     */
    private static String formatException(Throwable ex) {
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
