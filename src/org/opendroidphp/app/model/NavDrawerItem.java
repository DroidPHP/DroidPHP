package org.opendroidphp.app.model;

public class NavDrawerItem {

    private String title;
    private int icon = 0;

    public NavDrawerItem(String title) {
        this.title = title;
    }

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

}
