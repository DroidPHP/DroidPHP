package org.opendroidphp.app.common.parser;


public class ParserTokens {

    public static final String NGINX_SERVER_PORT = "listen";
    public static final String NGINX_SERVER_NAME = "server_name";
    public static final String NGINX_SERVER_LOCATION = "root";

    public static final String LIGHTTPD_SERVER_PORT = "port";
    public static final String LIGHTTPD_SERVER_NAME = "server_name";
    public static final String LIGHTTPD_SERVER_LOCATION = "document-root";

    public static final String REGEX_LIGTTPD = "^\\s*(server|dir-listing)\\.(port|document-root|errorlog|activate)\\s*=\\s*([^#;]*)";

    public static final String REGEX_NGINX = "^\\s*(listen|server_name|root)\\s([^#;]*)";
}
