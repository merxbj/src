/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

/**
 *
 * @author merxbj
 */
public class Application {
    public static void main(String[] args) throws Exception {
        Parser parser = new Parser();
        ProfilingResults results = parser.parse(args[0]);
        results.toXml(args[1]);
    }
}
