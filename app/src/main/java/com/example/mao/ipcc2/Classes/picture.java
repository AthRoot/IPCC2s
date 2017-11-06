package com.example.mao.ipcc2.Classes;

/**
 * Created by Mao on 11/3/2017.
 */

/**
 * This class is to store and manage a user photo
 */
public class picture {
    private String link;
    private long id;

    /**
     *
     * @param id is the pictures id
     * @param link is the pictures URL
     */
    public picture(long id,String link) {
        this.link = link;
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
