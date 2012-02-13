/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ncr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map.Entry;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jmerxbauer
 */
public class OptionUsageSearcher {
    
    public static void main(String[] args) throws Exception {
        
        String sourceRoot = args[0];
        String posOptionDefinitionsFile = args[1];
        String posOtionNumbersToCheckCsv = args[2];
        
        Map<Integer, String> posOptionDefines = getPosOptionDefines(posOptionDefinitionsFile);
        Map<String, Integer> posOptionValues = inverse(posOptionDefines);
        
        List<Integer> undefinedOptions = new ArrayList<Integer>();
        List<String> optionsToCheck = getOptionsToCheck(posOptionDefines, posOtionNumbersToCheckCsv, undefinedOptions);
        
        OptionDefineSearcher searcher = new OptionDefineSearcher(optionsToCheck, undefinedOptions);
        Files.walkFileTree(Paths.get(sourceRoot), searcher);

        printResults(searcher.getResults(), posOptionValues);
    }

    private static List<String> getOptionsToCheck(Map<Integer, String> posOptionDefines, String posOtionNumbersToCheckCsv, List<Integer> undefined) throws Exception {
        List<String> posOptionNumbersToCheckCsvLines = Files.readAllLines(Paths.get(posOtionNumbersToCheckCsv), Charset.defaultCharset());
        List<String> optionDefsToCheck = new ArrayList<String>(posOptionNumbersToCheckCsvLines.size());

        for (String posOptionNumbersToCheckCsvLine : posOptionNumbersToCheckCsvLines) {
            String controlNumberString = posOptionNumbersToCheckCsvLine.split(",")[0];
            
            int controlNumber = 0;
            try {
                controlNumber = Integer.parseInt(controlNumberString);
            } catch (NumberFormatException ex) {
            }
            
            boolean bIsValidControlNumber = (controlNumber > 0);
            if (bIsValidControlNumber) {
                String controlDefine = posOptionDefines.get(controlNumber);
                if (controlDefine != null) {
                    optionDefsToCheck.add(controlDefine);
                } else {
                    System.out.println("Undefined option number " + controlNumber);
                    undefined.add(controlNumber);
                }
            }
        }
        
        return optionDefsToCheck;
    }

    private static void printResults(Map<String, Set<Path>> results, Map<String, Integer> defines) {
        for (Entry<String, Set<Path>> entry : results.entrySet()) {
            System.out.printf("%s = %d in %d files\n", entry.getKey(), defines.get(entry.getKey()), entry.getValue().size());
            for (Path path : entry.getValue()) {
                System.out.printf("%s\n", path.toString());
            }
            System.out.println();
        }
    }

    private static Map<Integer, String> getPosOptionDefines(String posOptionDefinitionsFile) throws Exception {
        List<String> posOptionDefinitionsFileLines = Files.readAllLines(Paths.get(posOptionDefinitionsFile), Charset.defaultCharset());
        Pattern pattern = Pattern.compile("^\\#define\\s+(OPT_\\w+)\\s+(\\d+).*");

        Map<Integer, String> posOptionDefines = new HashMap<Integer, String>();
        for (String posOptionDefinitionsFileLine : posOptionDefinitionsFileLines) {
            try {
                Matcher match = pattern.matcher(posOptionDefinitionsFileLine);
                if (match.matches()) {
                    int controlNumber = Integer.parseInt(match.group(2));
                    String controlDefine = match.group(1);
                    posOptionDefines.put(controlNumber, controlDefine);
                }
            } catch (Exception ex) {
                System.out.printf("Failed to parse line: %s\n", posOptionDefinitionsFileLine);
            }
        }
        
        return posOptionDefines;
    }

    private static <V, K> Map<V, K> inverse(Map<K, V> map) {
        Map<V, K> inverse = new HashMap<V, K>();
        for (Entry<K, V> entry : map.entrySet()) {
            inverse.put(entry.getValue(), entry.getKey());
        }
        return inverse;
    }
    
    private static class OptionDefineSearcher extends SimpleFileVisitor<Path>
    {
        private List<String> posOptionDefs;
        private final PathMatcher matcher;
        private Map<String, Set<Path>> results;
        private List<String> optionDefineFiles;
        List<Integer> undefined;

        public OptionDefineSearcher(List<String> posOptionDefs, List<Integer> undefined) {
            this.posOptionDefs = posOptionDefs;
            this.matcher = FileSystems.getDefault().getPathMatcher("glob:*.{cpp,h,c,cs,xml,xsl,xsd,wxs,rvs,idl,i,sql,txt,pas,wxi,bat,vbs,xslt,dsp,csproj,rptx,vcproj,sln}");
            this.results = new HashMap<String, Set<Path>>();
            this.undefined = undefined;
            this.optionDefineFiles = new ArrayList<String>();
            optionDefineFiles.add("posoptions.h");
            optionDefineFiles.add("posoptiondef.pas");
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (!matcher.matches(file.getFileName())) {
                return super.visitFile(file, attrs);
            }
            
            if (optionDefineFiles.contains(file.getFileName().toString().toLowerCase())) {
                return handleOptionDefineFiles(file, attrs);
            }
            
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
                while (reader.ready()) {
                    String line = reader.readLine();
                    for (String posOptionDef : posOptionDefs) {
                        if (line.contains(posOptionDef)) {
                            if (!results.containsKey(posOptionDef)) {
                                results.put(posOptionDef, new TreeSet<Path>());
                            }
                            Set<Path> files = results.get(posOptionDef);
                            files.add(file);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.printf("Error occured while analyzing file %s\n", file.toString());
            }
            
            return FileVisitResult.CONTINUE;
        }

        public Map<String, Set<Path>> getResults() {
            return results;
        }

        private FileVisitResult handleOptionDefineFiles(Path file, BasicFileAttributes attrs) throws IOException {
            
            BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
            while (reader.ready()) {
                String line = reader.readLine();
                for (Integer posOptionNum : undefined) {
                    if (line.contains(posOptionNum.toString())) {
                        if (!results.containsKey(posOptionNum.toString())) {
                            results.put(posOptionNum.toString(), new TreeSet<Path>());
                        }
                        Set<Path> files = results.get(posOptionNum.toString());
                        files.add(file);
                    }
                }
            }

            return FileVisitResult.CONTINUE;
        }
    }
    
}
