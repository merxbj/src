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

package pal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {
    
    static HashMap<String, Target> targets = new HashMap<String, Target>();
    static StringBuilder output = new StringBuilder();

    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            make();
            System.out.println(String.format("Ellapsed time: %d", System.currentTimeMillis() - start));
        } catch (Exception ex) {
            System.out.println("ERROR");
        }
    }

    public static void make() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> rawTarget = new ArrayList<String>();

        Target root = null;
        String line = reader.readLine();
        while(line != null && !line.equals("")) {
            if ((rawTarget.size() > 0) && !line.startsWith("\t")) {
                Target t = new Target();
                t.parse(rawTarget);
                targets.put(t.name, t);
                rawTarget.clear();
                if (root == null) {
                    root = t; // consider first target as root
                }
            }
            rawTarget.add(line);
            line = reader.readLine();
        }

        root.make();
        System.out.println(output);
    }

    static class Target implements Comparable<Target> {
        String name;
        List<String> dependencies = new ArrayList<String>();
        List<String> work = new ArrayList<String>();
        boolean made = false;
        boolean waitingForDependencies = false;

        public Target() {
        }

        public Target(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(Target o) {
            return (this.name.compareTo(o.name));
        }

        public void parse(List<String> rawTarget) {
            String[] nameAndDependencies = rawTarget.get(0).split(":");
            this.name = nameAndDependencies[0].trim();
            if (nameAndDependencies.length == 2) {
                String[] dependenciesNames = nameAndDependencies[1].split(" ");
                for (String dependencyName : dependenciesNames) {
                    if ((dependencyName == null ? "" != null : !dependencyName.equals(""))) {
                        this.dependencies.add(dependencyName);
                    }
                }
            }
            for (int i = 1; i < rawTarget.size(); i++) {
                work.add(rawTarget.get(i).trim());
            }
            targets.put(name, this);
        }

        public void make() {
            if (waitingForDependencies) {
                throw new RuntimeException("Circular dependency!");
            }

            if (!made) {

                waitingForDependencies = true;
                for (String dependencyName : dependencies) {
                    Target dependency = targets.get(dependencyName);
                    dependency.make();
                }
                waitingForDependencies = false;

                for (String line : work) {
                    output.append(line).append("\n");
                }
                this.made = true;
            }
        }
    }
}
