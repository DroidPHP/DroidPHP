package org.opendroidphp.app.common.parser;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServerParser {

    public static final String NGINX_SERVER_PORT = "listen";
    public static final String NGINX_SERVER_NAME = "server_name";
    public static final String NGINX_SERVER_LOCATION = "root";

    public static final String LIGHTTPD_SERVER_PORT = "port";
    public static final String LIGHTTPD_SERVER_NAME = "server_name";
    public static final String LIGHTTPD_SERVER_LOCATION = "document-root";

//    public static final String LIGHTTPD_ERROR_LOG = "errorlog";
//    public static final String LIGHTTPD_DIRECTORY_INDEX = "activate";

    protected static final HashMap<String, String> keyValue = new HashMap<String, String>();

    public ServerParser(File file) {
        try {
            getKeyValueMap(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Map getKeyValueMap(FileReader fileReader) throws IOException {

        String LIGTTTPD_PATTERN = "^\\s*(server|dir-listing)\\.(port|document-root|errorlog|activate)\\s*=\\s*([^#;]*)";
        String NGINX_PATTERN = "^\\s*(listen|server_name|root)\\s([^#;]*)";

        BufferedReader reader = new BufferedReader(fileReader);

        Pattern lighttpdPattern = Pattern.compile(LIGTTTPD_PATTERN);
        Pattern nginxPattern = Pattern.compile(NGINX_PATTERN);

        String line;
        Matcher matcher;
        while ((line = reader.readLine()) != null) {
            matcher = lighttpdPattern.matcher(line);
            if (matcher.find()) {
                keyValue.put(matcher.group(2), matcher.group(3).replaceAll("\"", "").trim());
            }
            matcher = nginxPattern.matcher(line);
            if (matcher.find()) {
                keyValue.put(matcher.group(1), matcher.group(2).replaceAll("\"", "").trim());
            }
        }
        return keyValue;
    }

    public void setValue(File file, String hostName, String hostPort, String root) {

        HashMap<String, String> regexKeyValue = new HashMap<String, String>();

        regexKeyValue.put("^\\s*server\\.port\\s*=\\s*([^#;]*)", "server.port = \"" + hostPort + "\"");
        regexKeyValue.put("^\\s*server\\.document-root\\s*=\\s*([^#;]*)", "server.document-root = \"" + root + "\"");

//        regexKeyValue.put("^\\s*server\\.errorlog\\s*=\\s*([^#;]*)", "server.errorlog = \"/mnt/error.log\"");
//        regexKeyValue.put("^\\s*dir-listing\\.activate\\s*=\\s*([^#;]*)", "dir-listing.activate = \"false\"");

        regexKeyValue.put("^\\s*listen\\s([^#;]*)", "listen " + hostPort + "");
        regexKeyValue.put("^\\s*server_name\\s([^#;]*)", "server_name " + hostName + "");
        regexKeyValue.put("^\\s*root\\s([^#;]*)", "root " + root + "");

        try {
            String conf = replaceKeyValue(new FileReader(file.getPath()), regexKeyValue);
            FileUtils.writeStringToFile(file, conf, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String replaceKeyValue(FileReader fileReader, HashMap<String, String> regexKeyValue) throws IOException {
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            for (Map.Entry<String, String> entry : regexKeyValue.entrySet()) {
                line = line.replaceFirst(entry.getKey(), entry.getValue());
            }
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public String findByKey(String keyName) {
        return keyValue.get(keyName);
    }

    public boolean hasKey(String keyName) {
        return null != keyValue.get(keyName) ? true : false;
    }

    public boolean isValid() {
        return (keyValue.containsKey(NGINX_SERVER_NAME)
                || keyValue.containsKey(NGINX_SERVER_PORT))
                || (keyValue.containsKey(LIGHTTPD_SERVER_LOCATION)
                || keyValue.containsKey(LIGHTTPD_SERVER_PORT));
    }

    public String getAddress() {
        return keyValue.containsKey(NGINX_SERVER_NAME) ?
                keyValue.get(NGINX_SERVER_NAME) : (
                keyValue.containsKey(LIGHTTPD_SERVER_NAME) ?
                        keyValue.get(LIGHTTPD_SERVER_NAME) : "");
    }

    public String getPort() {

        return keyValue.containsKey(NGINX_SERVER_PORT) ?
                keyValue.get(NGINX_SERVER_PORT) : (
                keyValue.containsKey(LIGHTTPD_SERVER_PORT) ?
                        keyValue.get(LIGHTTPD_SERVER_PORT) : "");
    }

    public String getDocumentRoot() {
        return keyValue.containsKey(NGINX_SERVER_LOCATION) ?
                keyValue.get(NGINX_SERVER_LOCATION) : (
                keyValue.containsKey(LIGHTTPD_SERVER_LOCATION) ?
                        keyValue.get(LIGHTTPD_SERVER_LOCATION) : "");
    }

    public boolean isLighttpd() {
        return hasKey("server.document-root");
    }

    public boolean isNginx() {
        return hasKey("listen");
    }

    public String getType() {
        if (isNginx())
            return "nginx";
        else if (isLighttpd())
            return "lighttpd";
        return "";
    }
}
