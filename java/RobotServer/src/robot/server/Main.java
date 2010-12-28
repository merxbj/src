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
package robot.server;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.util.Map.Entry;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {

    public static void main(String[] args) {

        CommandLine params = CommandLine.parse(args);

        RobotServer server = new RobotServer(params);
        server.run();

    }

    public static class ClientRequestFactory {

        private String address;
        private final static List<String> validRequestNames;
        private final static HashMap<String, Request> prototypes;

        static {
            prototypes = new HashMap<String, Request>();
            prototypes.put("KROK", new RequestStep());
            prototypes.put("VLEVO", new RequestTurnLeft());
            prototypes.put("ZVEDNI", new RequestPickUp());
            prototypes.put("OPRAVIT", new RequestRepair());
            prototypes.put("NABIT", new RequestRecharge());

            validRequestNames = Arrays.asList(new String[]{"KROK", "VLEVO", "ZVEDNI", "OPRAVIT", "NABIT"});
        }

        public ClientRequestFactory(String address) {
            this.address = address;
        }

        public Request parseRequest(String rawRequest) {

            if (!rawRequest.startsWith(address)) {
                return new RequestUnknown();
            }

            try {

                String requestStringOnly = rawRequest.substring(address.length() + 1); // strip out the address
                List<String> tokens = Arrays.asList(requestStringOnly.split(" "));

                Request prototype = prototypes.get(tokens.get(0));
                if (prototype == null) {
                    return new RequestUnknown();
                }

                Request request = prototype.clone();
                request.setAdress(address);
                if (request.parseParamsFromTcp(StringUtils.join(tokens.subList(1, tokens.size()), " "))) {
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

        private Robot robot;

        public ClientRequestProcessor(Robot robot) {
            this.robot = robot;
        }

        public Response processPickUp() {
            try {
                String secretMessage = robot.pickUp();
                return new ResponseSuccess(secretMessage);
            } catch (RobotCannotPickUpException ex) {
                return new ResponseCannotPickUp();
            }
        }

        public Response processRecharge() {
            try {
                RobotServerInfo info = robot.recharge();
                return new ResponseOk(info.getBattery().level, info.getPosition().x, info.getPosition().y);
            } catch (RobotCrumbledException ex) {
                return new ResponseCrumbled();
            } catch (RobotDamagedException ex) {
                return new ResponseDamage(ex.getDamagedBlock());
            }
        }

        public Response processRepair(int blockToRepair) {
            try {
                RobotServerInfo info = robot.repair(blockToRepair);
                return new ResponseOk(info.getBattery().level, info.getPosition().x, info.getPosition().y);
            } catch (RobotNoDamageException ex) {
                return new ResponseNoDamage();
            }
        }

        public Response processStep() {
            try {
                RobotServerInfo info = robot.doStep();
                return new ResponseOk(info.getBattery().level, info.getPosition().x, info.getPosition().y);
            } catch (RobotCrashedException ex) {
                return new ResponseCrash();
            } catch (RobotBatteryEmptyException ex) {
                return new ResponseBatteryEmpty();
            } catch (RobotCrumbledException ex) {
                return new ResponseCrumbled();
            } catch (RobotDamagedException ex) {
                return new ResponseDamage(ex.getDamagedBlock());
            }
        }

        public Response processTurnLeft() {
            try {
                RobotServerInfo info = robot.turnLeft();
                return new ResponseOk(info.getBattery().level, info.getPosition().x, info.getPosition().y);
            } catch (RobotBatteryEmptyException ex) {
                return new ResponseBatteryEmpty();
            }
        }

        public Response processUnknown() {
            return new ResponseUnknownRequest();
        }

        public String getExpectedAddress() {
            return robot.getName();
        }
    }

    public static class CommandLine {

        private int portNumber;

        public static CommandLine parse(String[] args) {
            CommandLine cl = new CommandLine();

            try {
                cl.setPortNumber(Integer.parseInt(args[0]));
            } catch (Exception ex) {
                throw new RuntimeException("The one and only argument must be the port number in closed interval <3500,3800>!", ex);
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
    }

    public static class Robot {

        private String name;
        private RobotServerInfo info;
        private RobotState currentState;

        public Robot(String name) {
            this.name = name;

            /**
             * Generate the robot starting position and direction
             */
            int bat = 100;
            int x = (int) Math.ceil(Math.random() * 32) - (Math.min(Math.abs(RobotServerInfo.MAX_X), Math.abs(RobotServerInfo.MIN_X) - 1));
            int y = (int) Math.ceil(Math.random() * 32) - (Math.min(Math.abs(RobotServerInfo.MAX_Y), Math.abs(RobotServerInfo.MIN_Y) - 1));
            Direction direction = Direction.values()[(int) Math.floor(Math.random() * 4)];

            this.info = new RobotServerInfo(bat, x, y, direction);
            this.currentState = new RobotOkState();
        }

        public RobotServerInfo doStep() throws RobotCrashedException, RobotBatteryEmptyException, RobotCrumbledException, RobotDamagedException {
            currentState.doStep(this);
            return info;
        }

        public RobotServerInfo turnLeft() throws RobotBatteryEmptyException {
            currentState.turnLeft(this);
            return info;
        }

        public RobotServerInfo repair(int blockToRepair) throws RobotNoDamageException {
            currentState.repair(this, blockToRepair);
            return info;
        }

        public String pickUp() throws RobotCannotPickUpException {
            return currentState.pickUp(this);
        }

        public RobotServerInfo recharge() throws RobotCrumbledException, RobotDamagedException {
            currentState.recharge(this);
            return info;
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
    }

    public static class RobotClientProcess implements Runnable {

        private Socket clientSocket;
        private InputStream in;
        private OutputStream out;
        private ClientRequestFactory requestFactory;
        private ClientRequestProcessor requestProcessor;
        private Robot robot;
        private Logger log;

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
                        log.logMessage("Request message %s from the client %s!", rawRequest, clientSocket.getInetAddress());

                        Request request = requestFactory.parseRequest(rawRequest);
                        Response response = request.process(requestProcessor);
                        log.logMessage("Response message %s for the client %s!", response.formatForTcp(), clientSocket.getInetAddress());

                        sendResponseToSocket(response);
                        quit = response.isEndGame();
                    } catch (IOException ex) {
                        clientSocket.close();
                        log.logMessage("Connection lost.");
                        quit = true;
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
            this.robot = new Robot(RobotNameProvider.provideName());
            this.requestFactory = new ClientRequestFactory(robot.getName());
            this.requestProcessor = new ClientRequestProcessor(robot);
            this.log = FileLogger.getLogger(this.robot.getName());
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

    public static class RobotDamagedState implements RobotState {

        private int damagedBlock;

        public RobotDamagedState(int damagedBlock) {
            assert ((damagedBlock > 0) && (damagedBlock < 10));
            this.damagedBlock = damagedBlock;
        }

        public void doStep(Robot robot) throws RobotCrumbledException {
            throw new RobotCrumbledException();
        }

        public void turnLeft(Robot robot) throws RobotBatteryEmptyException {
            robot.getInfo().turn();

            robot.getInfo().getBattery().level -= 10;
            if (robot.getInfo().getBattery().level <= 0) {
                throw new RobotBatteryEmptyException();
            }
        }

        public void repair(Robot robot, int blockToRepair) throws RobotNoDamageException {
            if (damagedBlock != blockToRepair) {
                throw new RobotNoDamageException();
            }
            robot.getInfo().setStepsSoFar(0);
            robot.setCurrentState(new RobotOkState());
        }

        public String pickUp(Robot robot) throws RobotCannotPickUpException {
            if (robot.getInfo().getPosition().x != 0 || robot.getInfo().getPosition().y != 0) {
                throw new RobotCannotPickUpException();
            }

            return robot.getSecretMessage();
        }

        public void recharge(Robot robot) throws RobotCrumbledException, RobotDamagedException {
            throw new RobotCrumbledException();
        }
    }

    public static class RobotNameProvider {

        public static final List<String> names;
        public static HashMap<String, Boolean> reservations;

        static {
            names = Arrays.asList(new String[]{
                        "Jardo", "Pepo", "Miso", "Roberte", "Karle",
                        "Lojzo", "Vaclave", "Tomasi", "Robocope", "Optime",
                        "Iron Mane", "Bumblebee", "Martinku", "Chlupatoure Obecny",
                        "Edwarde", "Bello", "Jacobe", "Jaspere"});
            reservations = new HashMap<String, Boolean>();
        }

        public static synchronized String provideName() {

            int pick = (int) Math.floor(Math.random() * names.size());
            String name = names.get(pick);
            while (reservations.containsKey(name) && reservations.get(name)) {
                pick = (int) Math.floor(Math.random() * names.size());
                name = names.get(pick);
            }
            reservations.put(name, Boolean.TRUE);
            return name;
        }

        public static synchronized void freeName(String name) {
            reservations.put(name, Boolean.FALSE);
        }
    }

    public static class RobotOkState implements RobotState {

        public void doStep(Robot robot) throws RobotCrashedException, RobotBatteryEmptyException, RobotDamagedException {

            RobotServerInfo info = robot.getInfo();

            boolean robotDamaged = Math.ceil(Math.random() * 10) <= (info.getStepsSoFar() % 10);
            if (robotDamaged) {
                int damagedBlock = damageRobot(robot);
                throw new RobotDamagedException(damagedBlock);
            }

            info.getBattery().level -= 10;
            if (info.getBattery().level <= 0) {
                throw new RobotBatteryEmptyException();
            }

            try {
                info.move();
                info.setStepsSoFar(info.getStepsSoFar() + 1);
            } catch (RobotOutOfFieldException ex) {
                throw new RobotCrashedException(ex);
            }
        }

        public void turnLeft(Robot robot) throws RobotBatteryEmptyException {
            robot.getInfo().turn();

            robot.getInfo().getBattery().level -= 10;
            if (robot.getInfo().getBattery().level <= 0) {
                throw new RobotBatteryEmptyException();
            }
        }

        public void repair(Robot robot, int blockToRepair) throws RobotNoDamageException {
            throw new RobotNoDamageException();
        }

        public String pickUp(Robot robot) throws RobotCannotPickUpException {
            if (robot.getInfo().getPosition().x != 0 || robot.getInfo().getPosition().y != 0) {
                throw new RobotCannotPickUpException();
            }

            return robot.getSecretMessage();
        }

        public void recharge(Robot robot) throws RobotCrumbledException, RobotDamagedException {
            boolean robotDamaged = (Math.random() < 0.5);
            if (robotDamaged) {
                int damagedBlock = damageRobot(robot);
                robot.getInfo().getBattery().level = 1;
                throw new RobotDamagedException(damagedBlock);
            }
            robot.getInfo().getBattery().level = 100;
        }

        private int damageRobot(Robot robot) {
            int damagedBlock = (int) Math.ceil(Math.random() * 8) + 1;
            robot.setCurrentState(new RobotDamagedState(damagedBlock));
            return damagedBlock;
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
        private static final int MAX_X = 17;
        private static final int MAX_Y = 17;
        private static final int MIN_X = -17;
        private static final int MIN_Y = -17;

        public RobotServerInfo(int battery, int x, int y, Direction direction) {
            super(battery, x, y, direction);
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

        public void doStep(Robot robot) throws RobotCrashedException, RobotBatteryEmptyException, RobotCrumbledException, RobotDamagedException;

        public void turnLeft(Robot robot) throws RobotBatteryEmptyException;

        public String pickUp(Robot robot) throws RobotCannotPickUpException;

        public void repair(Robot robot, int blockToRepair) throws RobotNoDamageException;

        public void recharge(Robot robot) throws RobotCrumbledException, RobotDamagedException;
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

    public static class Logger {

        protected String name;
        protected static HashMap<String, Logger> loggers;

        static {
            loggers = new HashMap<String, Logger>();
        }

        public static synchronized Logger getLogger(String robot) {
            if (loggers.containsKey(robot)) {
                return loggers.get(robot);
            } else {
                Logger log = new Logger(robot);
                loggers.put(robot, log);
                return log;
            }
        }

        protected Logger(String name) {
            this.name = name;
        }

        protected void initialize() {
        }

        public synchronized void logRequest(Request request) {
            log(String.format("Received request %s addressed to %s!", request.getClass().getName(), request.getAdress()));
        }

        public synchronized void logResponse(Response response) {
            log(String.format("Sent response %s. This %s close the connection!", response.getClass().getName(), response.isEndGame() ? "will" : "will not"));
        }

        public synchronized void logException(Throwable exception) {
            log(formatException(exception));
        }

        public synchronized void logMessage(String message, Object... args) {
            log(String.format(message, args));
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

        protected String formatMessage(String message) {
            Date date = Calendar.getInstance().getTime();
            return String.format("%25s | [%s] | %s", date, name, message);
        }

        protected void log(String message) {
            System.out.println(formatMessage(message));
        }
    }

    public static class FileLogger extends Logger {

        private File file;

        public static synchronized Logger getLogger(String name) {
            Logger log = null;
            if (loggers.containsKey(name)) {
                log = loggers.get(name);
            } else {
                log = new FileLogger(name);
                loggers.put(name, log);
            }
            log.initialize();
            return log;
        }

        public FileLogger(String name) {
            super(name);
        }

        @Override
        protected void initialize() {
            super.initialize();
            file = new File(String.format("%s.log", name));
            int counter = 1;
            while (file.exists()) {
                file = new File(String.format("%s#%d.log", name, ++counter));
            }
        }

        @Override
        protected void log(String message) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file, true));
                writer.append(formatMessage(message));
                writer.newLine();
            } catch (IOException ex) {
                super.log(message);
            } finally {
                try {
                    writer.close();
                } catch (Exception ex) {
                }
            }
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
            super(cause);
        }

        public RobotUnknownResponseException(String message, Throwable cause) {
            super(message, cause);
        }

        public RobotUnknownResponseException(String message) {
            super(message);
        }

        public RobotUnknownResponseException() {
            super();
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
            return new StringBuilder("571 ").append("NENI PORUCHA ").append("\r\n").toString();
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
            return new StringBuilder("250 OK ").append(String.format("(%d,%d,%d)", getRemainingBattery(), getX(), getY())).append("\r\n").toString();
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
            return new StringBuilder("500 ").append("NEZNAMY PRIKAZ").append("\r\n").toString();
        }

        @Override
        public boolean isEndGame() {
            return false;
        }

        public void handle(ResponseHandler handler) throws RobotException {
            throw new RobotException(new RobotUnknownRequestException());
        }
    }
}
