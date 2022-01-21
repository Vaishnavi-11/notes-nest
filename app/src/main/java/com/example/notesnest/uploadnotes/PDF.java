package com.example.notesnest.uploadnotes;

public class PDF {
    public String name;
    public String url;

    public PDF() {
    }

    public PDF(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setPdfName(String pdfName) {
        this.name = pdfName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}