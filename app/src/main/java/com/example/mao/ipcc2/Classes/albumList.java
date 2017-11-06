package com.example.mao.ipcc2.Classes;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Mao on 11/6/2017.
 * This class is for albums List
 * is stores and manages a list of albums
 */

public class albumList {
    /**
     * @param albums is the user albums list
     * @param bitnmaps is the Downloaded bitnamp list from the albums urls
     *
     */
    private LinkedList<album> albums;
    private ArrayList<Bitmap> bitnmaps;


    public albumList (){
        albums = new LinkedList<>();
        bitnmaps = null;
    }


    ////////////Picture list management//////////
    public void addAlbum(album pict){
        albums.add(pict);
    }
    public void removeAlbum(album pict){
        albums.remove(pict);
    }
    public void removeAlbumIn(int pict){
        albums.remove(pict);
    }
    public void modifyAlbum(album from,album to){
        albums.set(albums.indexOf(from),to);
    }
    public void modifyAlbumIn(int from,album to){
        albums.set(from,to);
    }
    public album serachAlbum(int index){
        return albums.get(index);
    }
    public int indexOf(album index){
        return albums.indexOf(index);
    }
    public int sizeOf (){
        return albums.size();
    }
    /////////////////////////////////////////////


    public ArrayList<Bitmap> getBitnmaps() {
        return bitnmaps;
    }

    public void setBitnmaps(ArrayList<Bitmap> bitnmaps) {
        this.bitnmaps = bitnmaps;
    }
}
