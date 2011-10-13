/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package profiling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author jmerxbauer
 */
public class ProfillingOutputParser {
    
    public static void main(String[] args) throws Exception {
        
        Map<String, Map<Integer, Long>> profilingData = new HashMap<String, Map<Integer, Long>>();
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("d:\\temp\\hess\\pos_freezing\\profiling.txt"));
            
            while (reader.ready()) {
                String line = reader.readLine();
                String[] rawProfilingData = line.split("\\|")[1].split("-");

                rawProfilingData[0] = rawProfilingData[0].trim();
                rawProfilingData[1] = rawProfilingData[1].trim();
                rawProfilingData[2] = rawProfilingData[2].trim();

                String module = rawProfilingData[0].substring(1, rawProfilingData[0].length() - 1);
                String rawEvent = rawProfilingData[1].substring(1, rawProfilingData[1].length() - 1);
                int event = Integer.parseInt(rawEvent);
                String rawTimeAndUnit = rawProfilingData[2].substring(1, rawProfilingData[2].length() - 1);
                long time = Long.parseLong(rawTimeAndUnit.split(" ")[0]);
                
                if (!profilingData.containsKey(module)) {
                    Map<Integer, Long> eventData = new HashMap<Integer, Long>();
                    eventData.put(event, time);
                    profilingData.put(module, eventData);
                } else {
                    Map<Integer, Long> eventData = profilingData.get(module);
                    if (!eventData.containsKey(event)) {
                        eventData.put(event, time);
                    } else {
                        long totalTime = eventData.get(event) + time;
                        eventData.put(event, totalTime);
                    }
                }
            }
            
            for (Entry<String, Map<Integer, Long>> entry : profilingData.entrySet()) {
                for (Entry<Integer, Long> event : entry.getValue().entrySet()) {
                    System.out.printf("%s,%d,%d\n", entry.getKey(), event.getKey(), event.getValue());
                }
            }
            
        } catch (Exception ex) {
            throw ex;
        } finally {
            reader.close();
        }
        
    }
}
