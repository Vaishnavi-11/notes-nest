package com.example.notesnest.createnotes;

public class Note  {
    String title;
    String content;
    int color;

    Note(){
        this.title = "New Note";
    }

    Note(String title, String content,int color){
        this.title = title;
        this.content = content;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getColor() {
        return color;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
