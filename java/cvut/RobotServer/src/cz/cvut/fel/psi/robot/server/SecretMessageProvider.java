/*
 * SecretMessageProvider
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

package cz.cvut.fel.psi.robot.server;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SecretMessageProvider {

    private static List<String> messages = Arrays.asList(new String[] {
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
