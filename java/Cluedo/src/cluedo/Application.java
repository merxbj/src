/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cluedo;

import cluedo.simulation.*;
import cluedo.engine.*;
import java.io.FileInputStream;

/**
 *
 * @author eTeR
 */
public class Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        CluedoGame game = CluedoGameFactory.parse(new FileInputStream(args[0]));
        CluedoEngine engine = new CluedoEngine();
        CluedoGameInterpreter interpreter = new CluedoGameInterpreter(game, engine);
        engine.run();
    }
}
