package org.opendroidphp.app.common.utils;

public class Extension {

    private String name;
    private String summery;
    private String shellScript;
    private String downloadUrl;
    private String fileName;
    private String installPath;


    public Extension(String name, String summery, String shellScript, String downloadUrl, String fileName, String installPath) {
        this.name = name;
        this.summery = summery;
        this.shellScript = shellScript;
        this.downloadUrl = downloadUrl;
        this.fileName = fileName;
        this.installPath = installPath;
    }

    public String getName() {
        return this.name;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getSummery() {
        return this.summery;
    }

    public String getShellScript() {
        return this.shellScript;
    }

    public String getInstallPath() {
        return this.installPath;
    }

    public String getDownloadUrl() {

        return this.downloadUrl;
    }
}