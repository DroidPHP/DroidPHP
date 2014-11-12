package org.opendroidphp.app.common.parser;


import java.util.HashMap;

public class Finder {

    private HashMap<String, String> keyValue;

    public Finder(HashMap<String, String> keyValue) {
        this.keyValue = keyValue;
    }

    public String find(String keyName) {
        return keyValue.containsKey(keyName) ? keyValue.get(keyName) : "";
    }

    public void setKeyValue(String key, String value) {
        keyValue.put(key, value);
    }

    public boolean hasKey(String keyName) {
        return keyValue.containsKey(keyName);
    }

    public boolean isValid() {
        return (keyValue.containsKey(ParserTokens.NGINX_SERVER_NAME)
                || keyValue.containsKey(ParserTokens.NGINX_SERVER_PORT))
                || (keyValue.containsKey(ParserTokens.LIGHTTPD_SERVER_LOCATION)
                || keyValue.containsKey(ParserTokens.LIGHTTPD_SERVER_PORT));
    }

    public String getAddress() {
        return keyValue.containsKey(ParserTokens.NGINX_SERVER_NAME) ?
                keyValue.get(ParserTokens.NGINX_SERVER_NAME) : (
                keyValue.containsKey(ParserTokens.LIGHTTPD_SERVER_NAME) ?
                        keyValue.get(ParserTokens.LIGHTTPD_SERVER_NAME) : "localhost");
    }

    public String getPort() {
        return keyValue.containsKey(ParserTokens.NGINX_SERVER_PORT) ?
                keyValue.get(ParserTokens.NGINX_SERVER_PORT) : (
                keyValue.containsKey(ParserTokens.LIGHTTPD_SERVER_PORT) ?
                        keyValue.get(ParserTokens.LIGHTTPD_SERVER_PORT) : "80");
    }

    public String getRoot() {
        return keyValue.containsKey(ParserTokens.NGINX_SERVER_LOCATION) ?
                keyValue.get(ParserTokens.NGINX_SERVER_LOCATION) : (
                keyValue.containsKey(ParserTokens.LIGHTTPD_SERVER_LOCATION) ?
                        keyValue.get(ParserTokens.LIGHTTPD_SERVER_LOCATION) : "");
    }
}
