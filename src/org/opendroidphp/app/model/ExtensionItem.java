package org.opendroidphp.app.model;

public class ExtensionItem {

    private String name;
    private String summery;
    private String shellScript;
    private String downloadUrl;
    private String fileName;
    private String installPath;

    public ExtensionItem() {
    }

    public ExtensionItem(String name, String summery, String shellScript, String downloadUrl, String fileName, String installPath) {
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

    public ExtensionItem setName(String name) {
        this.name = name;
        return this;
    }


    public String getFileName() {
        return this.fileName;
    }

    public ExtensionItem setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getSummery() {
        return this.summery;
    }

    public ExtensionItem setSummery(String summery) {
        this.summery = summery;
        return this;
    }

    public String getShellScript() {
        return this.shellScript;
    }

    public ExtensionItem setShellScript(String shellScript) {
        this.shellScript = shellScript;
        return this;
    }

    public String getInstallPath() {
        return this.installPath;
    }

    public ExtensionItem setInstallPath(String installPath) {
        this.installPath = installPath;
        return this;
    }

    public String getDownloadUrl() {

        return this.downloadUrl;
    }

    public ExtensionItem setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }
}