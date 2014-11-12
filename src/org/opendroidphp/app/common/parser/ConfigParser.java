package org.opendroidphp.app.common.parser;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser implements Parser {

    private HashMap<String, String> keyValue = new HashMap<String, String>();

    public static ConfigParser factory(BufferedReader stream) {
        Pattern[] pattern = {Pattern.compile(ParserTokens.REGEX_LIGTTPD), Pattern.compile(ParserTokens.REGEX_NGINX)};
        ConfigParser parser = new ConfigParser();
        try {
            parser.findByRegex(pattern, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parser;
    }

    protected HashMap<String, String> findByRegex(final Pattern[] pattern, BufferedReader stream) throws Exception {
        if (pattern.length < 2) {
            throw new Exception("Pattern must not be less then 2");
        }
        String line;
        Matcher matcher;
        HashMap<String, String> keyValue = new HashMap<String, String>();
        boolean keyFound = false;
        while ((line = stream.readLine()) != null) {
            matcher = pattern[0].matcher(line);
            if (matcher.find() && (keyFound = true)) {
                keyValue.put(matcher.group(2), matcher.group(3).replaceAll("\"", "").trim());
            } else if (!keyFound) {
                matcher = pattern[1].matcher(line);
                if (matcher.find()) {
                    keyValue.put(matcher.group(1), matcher.group(2).replaceAll("\"", "").trim());
                }
            }
        }
        return this.keyValue = keyValue;
    }

    @Override
    public HashMap<String, String> getKeyValue() {
        return keyValue;
    }
}