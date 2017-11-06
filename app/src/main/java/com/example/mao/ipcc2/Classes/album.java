package com.example.mao.ipcc2.Classes;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by AbdelMaoula on 11/6/2017.
 * Album class is for a user album to store and manage the album data and the photos inside this album
 */

public class album {
    /**
     *
     * @param id is The Albums ID
     * @param link is the album picture URL
     * @param pictures is a list of pictures inside this album
     * @param bitnamps is a bitnamp array to store the user photos bitmaps downloaded
     */
    private long id;
    private String link;
    private LinkedList<picture> pictures;
    private ArrayList<Bitmap> bitnamps;


    public album(long id, String link) {
        this.id = id;
        this.link = link;
        this.pictures = new LinkedList<>();
        bitnamps = null;
    }

    ////////////Picture list management//////////
    public void addPict(picture pict){
        pictures.add(pict);
    }
    public void removePict(picture pict){
        pictures.remove(pict);
    }
    public void removePictIn(int pict){
        pictures.remove(pict);
    }
    public void modifyPict(picture from,picture to){
        pictures.set(pictures.indexOf(from),to);
    }
    public void modifyPictIn(int from,picture to){
        pictures.set(from,to);
    }
    public picture serachPict(int index){
        return pictures.get(index);
    }
    public int indexOf(picture index){
        return pictures.indexOf(index);
    }
    public int sizeOf (){
        return pictures.size();
    }
    public boolean isThere (picture pic){
        return pictures.contains(pic);
    }
    /////////////////////////////////////////////

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LinkedList<picture> getPictures() {
        return pictures;
    }

    public ArrayList<Bitmap> getBitnamps() {
        return bitnamps;
    }

    public void setBitnamps(ArrayList<Bitmap> bitnamps) {
        this.bitnamps = bitnamps;
    }

    public void setPictures(LinkedList<picture> pictures) {
        this.pictures = pictures;
    }
}
