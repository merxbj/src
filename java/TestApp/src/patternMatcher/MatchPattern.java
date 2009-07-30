package patternMatcher;

import java.util.regex.*;

public class MatchPattern {

    public static void main(String[] args) {

        Pattern pat = Pattern.compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
        Matcher match = pat.matcher("192.168.0.256");

        if (match.matches()) {
            System.out.println(match.group());
        }

    }

}
