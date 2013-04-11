package de.halirutan.mathematica.codeInsight.completion;

import java.util.*;

/**
 * @author patrick (4/3/13)
 */
public class SymbolInformationProvider {

    private static HashMap<String, SymbolInformation> ourSymbols;

    private SymbolInformationProvider() { }

    private static void initialize() {

        final ResourceBundle info = ResourceBundle.getBundle("symbolInformation");

        ourSymbols = new HashMap<String, SymbolInformation>();

        final Enumeration<String> names = info.getKeys();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String parts[] = info.getString(name).split(";");

            boolean isFunction = false;
            int importance = 0;
            String attributes[]= {};
            String shortName = "";
            String pattern = "";
            String options[] = {};

            //extract whether it is a function
            if (parts.length > 0) {

                try {
                    importance = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    importance = 0;
                }
            }

            if (parts.length > 1) {
                attributes = parts[1].trim().split(" ");
            }

            if (parts.length > 2) {
                shortName = parts[2];
            }

            if (parts.length > 3) {
                pattern = parts[3].trim();
            }

            if (parts.length > 4) {
                options = parts[4].replace("{","").replace("}","").trim().split(" ");
            }

            if (!pattern.equals("")) {
                isFunction = true;
            }

            ourSymbols.put(name, new SymbolInformation(name, shortName, importance, pattern, isFunction, attributes, options));
        }

    }

    public static HashMap<String,SymbolInformation> getSymbolNames() {
        if (ourSymbols == null) {
            initialize();
            }
        return ourSymbols;
    }

    public static class SymbolInformation {
        public final String name;
        public final int importance;
        public final String shortName;
        public final String callPattern;
        public final boolean function;
        public final String attributes[];
        public final String options[];

        public SymbolInformation(String name, String shortName, int importance, String callPattern, boolean function, String[] attributes, String[] options) {
            this.name = name;
            this.shortName = shortName;
            this.importance = importance;
            this.callPattern = callPattern;
            this.function = function;
            this.attributes = attributes;
            this.options = options;
        }

        public SymbolInformation(String name) {
            this.name = name;
            shortName = name;
            importance = 0;
            callPattern = "";
            function = false;
            attributes = new String[0];
            options = new String[0];
        }


    }

}
