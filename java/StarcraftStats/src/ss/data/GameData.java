/*
 * GameData
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

package ss.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import org.w3c.dom.Document;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ss.application.InvalidDataFileException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class GameData {

    private static final String schemaPath;
    private static final XPath xpath;
    private static final SimpleDateFormat dateFormat;
    private static final DocumentBuilderFactory dbf;
    private Hashtable<String, Player> players;
    private Hashtable<String, Team> teams;
    private List<Game> games;

    static {
        schemaPath = "Starcraft2.xsd";
        xpath = XPathFactory.newInstance().newXPath();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dbf = DocumentBuilderFactory.newInstance();
    }

    public GameData(String dataFilePath) throws InvalidDataFileException {
        try {
            loadData(new FileInputStream(dataFilePath));
        } catch (FileNotFoundException ex) {
            throw new InvalidDataFileException(String.format("Given file not found: %s!", dataFilePath));
        } catch (Exception ex) {
            ss.application.CommandLine.handleException(ex);
            throw new InvalidDataFileException(String.format("Invalid data file format: %s!", ex.getMessage()));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Player p : players.values()) {
            builder.append("Player: ").append(p).append("\n");
        }
        for (Team t : teams.values()) {
            builder.append("Team: ").append(t).append("\n");
        }
        for (Game g : games) {
            builder.append("Game: ").append(g).append("\n");
        }
        return builder.toString();
    }

    private void loadData(FileInputStream fileInputStream) throws Exception {
        Document data = dbf.newDocumentBuilder().parse(fileInputStream);
        if (validate(new DOMSource(data))) {
            load(data);
        }
    }

    private boolean validate(DOMSource data) throws InvalidDataFileException {
        try {
            Validator xsdSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(schemaPath)).newValidator();
            xsdSchema.validate(data);
            return true;
        } catch (Exception ex) {
            throw new InvalidDataFileException(String.format("XSD Validation failed: %s", ex.getMessage()));
        }
    }

    private void load(Document data) throws Exception {
        loadPlayers((NodeList) xpath.evaluate("/Starcraft/Players/Player", data, XPathConstants.NODESET));
        loadTeams((NodeList) xpath.evaluate("/Starcraft/Teams/Team", data, XPathConstants.NODESET));
        loadGames((NodeList) xpath.evaluate("/Starcraft/Games/Game", data, XPathConstants.NODESET));
    }

    private void loadPlayers(NodeList xmlPlayers) {
        players = new Hashtable<String, Player>(xmlPlayers.getLength());
        for (int i = 0; i < xmlPlayers.getLength(); i++) {
            Node xmlPlayer = xmlPlayers.item(i);
            Player player = loadPlayer(xmlPlayer);
            players.put(player.getId(), player);
        }
    }

    private void loadTeams(NodeList xmlTeams) {
        teams = new Hashtable<String, Team>(xmlTeams.getLength());
        for (int i = 0; i < xmlTeams.getLength(); i++) {
            Node xmlTeam = xmlTeams.item(i);
            Team team = loadTeam(xmlTeam);
            teams.put(team.getId(), team);
        }
    }

    private void loadGames(NodeList xmlGames) throws ParseException {
        games = new ArrayList<Game>(xmlGames.getLength());
        for (int i = 0; i < xmlGames.getLength(); i++) {
            Node xmlGame = xmlGames.item(i);
            Game game = loadGame(xmlGame);
            games.add(game);
        }
    }

    private Player loadPlayer(Node xmlPlayer) {
        String id = xmlPlayer.getAttributes().getNamedItem("id").getNodeValue();
        String name = xmlPlayer.getAttributes().getNamedItem("name").getNodeValue();
        return new Player(id, name);
    }

    private Team loadTeam(Node xmlTeam) {
        String id = xmlTeam.getAttributes().getNamedItem("id").getNodeValue();
        TeamType type = TeamType.lookup(xmlTeam.getAttributes().getNamedItem("type").getNodeValue());
        boolean isRandom = Boolean.parseBoolean(xmlTeam.getAttributes().getNamedItem("type").getNodeValue());
        LeagueName league = LeagueName.lookup(xmlTeam.getAttributes().getNamedItem("league").getNodeValue());
        Team team = new Team(id, type, isRandom, league);
        loadMembers(team);
        return team;
    }

    private Game loadGame(Node xmlGame) throws ParseException {
        Date date = dateFormat.parse(xmlGame.getAttributes().getNamedItem("date").getNodeValue());
        GameResult result = GameResult.lookup(xmlGame.getAttributes().getNamedItem("result").getNodeValue());
        int points = Integer.parseInt(xmlGame.getAttributes().getNamedItem("points").getNodeValue());
        Team teamA = teams.get(xmlGame.getChildNodes().item(0).getAttributes().getNamedItem("id").getNodeValue());
        Team teamB = teams.get(xmlGame.getChildNodes().item(1).getAttributes().getNamedItem("id").getNodeValue());
        Game game = new Game(date, result, points, teamA, teamB);
        loadRaceSelection(game, xmlGame);
        return game;
    }

    private void loadRaceSelection(Game game, Node xmlGame) {
        Collection<Player> teamAPlayers = game.getTeamA().getMembers();
        Collection<Player> teamBPlayers = game.getTeamB().getMembers();
        String[] teamARaceSelection = xmlGame.getChildNodes().item(0).getAttributes().getNamedItem("races").getNodeValue().split(":");
        String[] teamBRaceSelection = xmlGame.getChildNodes().item(1).getAttributes().getNamedItem("races").getNodeValue().split(":");

        int i = 0;
        for (Player player : teamAPlayers) {
            game.getRaceSelection().put(player, Race.lookup(teamARaceSelection[i++]));
        }

        i = 0;
        for (Player player : teamBPlayers) {
            game.getRaceSelection().put(player, Race.lookup(teamBRaceSelection[i++]));
        }
    }

    private void loadMembers(Team team) {
        for (String playerId : team.getId().split(":")) {
            team.getMembers().add(players.get(playerId));
        }
    }
}
