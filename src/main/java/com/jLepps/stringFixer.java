package com.jLepps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class stringFixer {

    public String fixString(String string){
        String regex = "['\":(),.]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()){
            string = escapeSpecialCharacters(string);
        }
        return string;
    }

    private String escapeSpecialCharacters(String input) {
        return input.replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace(":", "\\:")
                .replace(",", "\\,")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace(".", "\\.");
    }
}
