package com.example.mao.ipcc2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.mao.ipcc2.Classes.album;
import com.example.mao.ipcc2.Classes.albumList;
import com.example.mao.ipcc2.Classes.picture;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private TableLayout tab;
    private TableLayout tab2;
    private TableRow r;

    private albumList albums;
    private album toFirebase;

    private int in = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context cnx = this;

        albums = new albumList();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        /**
         * This statement reads the user_photos permition from Facebook
         */
        loginButton.setReadPermissions(Arrays.asList("user_photos"));
        tab = (TableLayout) findViewById(R.id.myTable);
        r = new TableRow(this);

        callbackManager = CallbackManager.Factory.create();
        /**T
         * his method is to send a request to facebook to get the User Album (id , picture , photos (id,picture))
         * It parses the returned JSON file
         * All Files an AlbumList object with it
         *
         */

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                ////////////////////////////////////////////////////////////////////////////
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + AccessToken.getCurrentAccessToken().getUserId(),
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {

                                try {
                                    JSONObject joMain = response.getJSONObject(); //convert GraphResponse response to JSONObject
                                    JSONObject jMain = joMain.getJSONObject("albums"); //find albums JSONObject from
                                    JSONArray jArray = jMain.getJSONArray("data"); //find JSONArray from albums

                                    for (int i = 0; i < jArray.length(); i++) { // loop over the albums JSONArray to get all albums
                                        album alb;

                                        JSONObject dataArray = jArray.getJSONObject(i);
                                        String albumurl = dataArray.getJSONObject("picture").getJSONObject("data").getString("url");// storing the album picture url
                                        alb = new album(dataArray.getInt("id"), albumurl);// create new album


                                        if (dataArray.has("photos")) { // make sure that the album is not empty of pictures
                                            // its possible to edit the condition to not add empty albums
                                            JSONObject jPhotoObj = dataArray.optJSONObject("photos");


                                            JSONArray jPhotoArray = jPhotoObj.getJSONArray("data");

                                            for (int j = 0; j < jPhotoArray.length(); j++) { //loop over the photos JSONArray to get all photos
                                                JSONObject jPhotos = jPhotoArray.getJSONObject(j);
                                                String photoUrl = jPhotos.getString("picture");
                                                int photoId = jPhotos.getInt("id");
                                                alb.addPict(new picture(photoId, photoUrl));// adding photos to their album
                                            }

                                        }

                                        albums.addAlbum(alb);// add the created album "with the inner photo list" to an album list


                                    }

                                    new DownloadImageTask(cnx, albums, tab).execute(); // call the function that downloads the pictures and display them

                                } catch (JSONException e) {
                                    e.printStackTrace();


                                }

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "albums{id,picture{url},photos{id,picture}}");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                //mytxt.setText("Canseled\n");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * The OnClick Listener can be triggered by 4 types f buttons
     *  1. A User album ImageButton
     *  2. A user Photo ImageButton
     *  3. Goback button
     *  4. send to firebase buttom
     * @param v
     */
    @Override
    public void onClick(View v) {
        /**
         * This condition is for the Goback button
         * The button can be added to the toolbar
         */
        if(v.getId()== R.id.backBtn){//Cheking for the Go back button first
            //Can be added on ToolBar AS well

            setContentView(R.layout.activity_main);
            loginButton = (LoginButton) findViewById(R.id.login_button);
            tab = (TableLayout) findViewById(R.id.myTable);
            displayBitnamps(albums.getBitnmaps(),albums,null);
            return;
        }
        else if(v.getId() == R.id.toFirebase){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This method is not implemented Yet");
            AlertDialog alert = builder.create();
            alert.show();
            // Here we can use Facebook ID to authenticat to Firebase and send URL strings to the database
            return;
        }
        /**
         * This condition is for the Album ImageButton and the Photo ImageButton
         * It sets A new Layout and calls DownloadImageTask to fill this layout with images
         */
        if (v.getTag() instanceof album) {// We check if the Album button or the picture button is clicked
            album selectedAlbum = (album) v.getTag(); // we get the object of the clicked button
            setContentView(R.layout.picture_layout);// we start the photos layout
            tab2 = (TableLayout) findViewById(R.id.tab2);
            toFirebase = new album(0, "null"); // We initialise the list for the picture to be sent to FIREBASE
            if(selectedAlbum.getBitnamps() == null) // We check if we already downloaded the pictures to not do it again
                new DownloadImageTask(this, selectedAlbum, tab2).execute();
            else
                displayBitnamps(selectedAlbum.getBitnamps(),null,selectedAlbum);

        } else {
            picture pic = (picture) v.getTag();

            // We add or remove the selected photo from the list to be sent to Firebase
            if (toFirebase.isThere(pic)) {

                toFirebase.removePict(pic);
                v.setBackgroundColor(2);
            } else {
                toFirebase.addPict(pic);
                v.setClickable(true);
                v.setBackgroundColor(1);
            }
        }
    }

    /**
     *This method Creates a set of Image Buttons from a Bitnamp Array and add them to the TableLayout
     * @param result is the Bitnamp Array with all the pictures of Albums or album.Photos
     * @param albums the AlbumList from wish we generate the Bitnamp array, Can be NULL if its photos not albums pictures
     * @param albumN the Album from wish we generated the Bitnamp array of its photos, Can be NULL if its user Albums not user photos pictures
     */
    public void displayBitnamps (ArrayList<Bitmap> result,albumList albums  ,album albumN ){
        int screenSize;
        in = 1;

        if (result.size() < 5)
            screenSize = 1;
        else
            screenSize = result.size() / 4;

        in = 0;
        for (int i = 0; i < screenSize; i++) {

            for (int j = 0; j < 4; j++) {
                if (i + j == result.size())
                    break;
                ImageButton myImage = new ImageButton(this);
                myImage.setImageBitmap(result.get(in));

                if (albums != null)
                    //myImage.setTag(in, this.albums.serachAlbum(i + j));
                    myImage.setTag(albums.serachAlbum(in));
                else
                    myImage.setTag(albumN.getPictures().get(in));

                myImage.setOnClickListener(MainActivity.this);
                r.addView(myImage);
                in++;

            }
            if (albums != null)
                tab.addView(r);
            else {
                //tab2 = (TableLayout) findViewById(R.id.tab2);
                tab2.addView(r);
            }
            r = new TableRow(this);
        }
    }

    /**
     * This Class is running on the background to get Bitnamp images from Links
     * The Links can be use albums , or user Photos
     * and it calls the displayBitnamps with the bitnamps list resulted
     */
    public class DownloadImageTask extends AsyncTask<String, Void, ArrayList<Bitmap>> {
        ImageButton bmImage;
        Context cnx;
        albumList albums = null;
        album albumN = null;
        ArrayList<Bitmap> bitnamps;


        public DownloadImageTask(Context cnx, Object albums, TableLayout tabs) {
            this.bmImage = new ImageButton(cnx);
            this.cnx = cnx;
            //this.tab = tabs;
            if (albums instanceof albumList) { // checking if we are working with albums or photos
                this.albums = (albumList) albums;
            } else
                this.albumN = (album) albums;
            bitnamps = new ArrayList<>();
        }

        protected ArrayList<Bitmap> doInBackground(String... urls) {
            String urldisplay;
            int sizea ;
            if (albums != null) // getting the picture link from our object
                sizea = albums.sizeOf();
            else
                sizea = albumN.sizeOf();


            for (int i = 0; i < sizea; i++) {// loop over the albums or albums.photos to get all pictures
                if (albums != null) // getting the picture link from our object
                    urldisplay = albums.serachAlbum(i).getLink();
                else
                    urldisplay = albumN.getPictures().get(i).getLink();

                try {
                    InputStream in = new java.net.URL(urldisplay).openStream(); // getting the picture
                    bitnamps.add(BitmapFactory.decodeStream(in));// storing the picture in a bitnamp array
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }

            return bitnamps;// returning the bitnamp array to be displayed
        }

        /**
         * After downloading all the Bitnamp images
         * The onPostExecute calls displayBitnamps to display them
         * @param result
         */
        protected void onPostExecute(ArrayList<Bitmap> result) {
            if(albums != null)
                albums.setBitnmaps(result);
            else
                albumN.setBitnamps(result);
            MainActivity.this.displayBitnamps(result,albums,albumN);// pass the bitnamps array to be displayed

        }
    }
}
