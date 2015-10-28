package com.splash.model;

/**
 * Created by hardik on 20/08/15.
 */
public class Item {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean checked;
    public Item(String paramTitle,boolean paramChecked){
        title=paramTitle;
        checked=paramChecked;
    }
}
