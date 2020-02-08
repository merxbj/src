/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.amaze;

import cz.sge.GameApplication;

/**
 *
 * @author merxbj
 */
public class MazeApp {

    public static void main(String[] args) {

        MazeGame game = new MazeGame();
        GameApplication.start(game);
    }

}
