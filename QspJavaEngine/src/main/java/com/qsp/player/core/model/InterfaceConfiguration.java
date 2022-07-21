package com.qsp.player.core.model;

public class InterfaceConfiguration {
    public boolean useHtml= true;
    public int fontSize;
    public int backColor;
    public int fontColor;
    public int linkColor;

    public void reset() {
        useHtml = true;
        fontSize = 0;
        backColor = 0;
        fontColor = 0;
        linkColor = 0;
    }
}
