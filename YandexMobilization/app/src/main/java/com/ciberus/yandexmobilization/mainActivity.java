package com.ciberus.yandexmobilization;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class mainActivity extends AppCompatActivity {

    /*protected enum Genres {
        pop,
        dance,
        electronics
    }*/

    public static ArrayList<Artist> artists;
    ArtistAdapter adapter;
    GridView gvMain;
    ImageLoader imageLoader;
    String requestArtistsURL = "http://cache-default03e.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";

    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.unknown)
                .resetViewBeforeLoading(true)  // default
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .build();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheSize(50 * 1024 * 1024) // 50 MB
                .defaultDisplayImageOptions(options)
                //.imageDecoder(new NutraBaseImageDecoder(true))
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        setContentView(R.layout.activity_main);

        gvMain = (GridView)findViewById(R.id.gvMain);

        new ParseTask().execute();

        gvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Отправляем id изображения в класс aboutArtistActivity:
                Intent aboutArtistActivityIntent = new Intent(getApplicationContext(), aboutArtistActivity.class);
                //Передаем необходимый index:
                aboutArtistActivityIntent.putExtra("id", position);
                startActivity(aboutArtistActivityIntent);
            }
        });
        //test();
    }


    //Асинхронно делаем запрос и парсим полученный JSON
    private class ParseTask extends AsyncTask<Void, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL(requestArtistsURL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONArray artistsJSON = null;
            String secondName = "";

            try {
                artistsJSON = new JSONArray(strJson);

                artists = new ArrayList<Artist>();

                JSONObject curArtistJSON;
                Artist curArtist;

                JSONArray curArtistGenresJSON;
                ArrayList<String> tempArtistGenres;
                JSONObject curArtistCoverJSON;
                Artist.Covers tempArtistCovers;

                for(int i = 0; i < artistsJSON.length(); i++)
                {
                    curArtistJSON = artistsJSON.getJSONObject(i);
                    //tempArtistGenres = new String[artistJSON.getJSONArray("Genres").length()];
                    curArtist = new Artist();
                    if (!curArtistJSON.isNull("id")) curArtist.id = curArtistJSON.getInt("id");
                    if (!curArtistJSON.isNull("name")) curArtist.name = curArtistJSON.getString("name");
                    if (!curArtistJSON.isNull("tracks")) curArtist.tracks = curArtistJSON.getInt("tracks");
                    if (!curArtistJSON.isNull("albums")) curArtist.albums = curArtistJSON.getInt("albums");
                    if (!curArtistJSON.isNull("link")) curArtist.link = curArtistJSON.getString("link");
                    if (!curArtistJSON.isNull("description")) curArtist.description = curArtistJSON.getString("description");

                    if (curArtistJSON.getJSONArray("genres") != null) {
                        tempArtistGenres = new ArrayList<String>();
                        curArtistGenresJSON = curArtistJSON.getJSONArray("genres");
                        for(int j = 0; j < curArtistGenresJSON.length(); j++){
                            tempArtistGenres.add(curArtistGenresJSON.getString(j));
                        }
                        curArtist.genres = tempArtistGenres;
                    }

                    if (curArtistJSON.getJSONObject("cover") != null)
                    {
                        curArtistCoverJSON = curArtistJSON.getJSONObject("cover");
                        tempArtistCovers = new Artist.Covers(
                                curArtistCoverJSON.getString("small"),
                                curArtistCoverJSON.getString("big")
                        );
                        curArtist.covers = tempArtistCovers;
                    }

                    /*curArtist = new Artist(
                            curArtistJSON.getInt("id"),
                            curArtistJSON.getString("name"),
                            tempArtistGenres,
                            curArtistJSON.getInt("tracks"),
                            curArtistJSON.getInt("albums"),
                            curArtistJSON.getString("link"),
                            curArtistJSON.getString("description"),
                            tempArtistCovers
                    );*/

                    artists.add(curArtist);
                }

                /*// 1. достаем инфо о втором друге - индекс 1
                JSONObject secondFriend = friends.getJSONObject(1);
                secondName = secondFriend.getString("name");
                Log.d(LOG_TAG, "Второе имя: " + secondName);

                // 2. перебираем и выводим контакты каждого друга
                for (int i = 0; i < friends.length(); i++) {
                    JSONObject friend = friends.getJSONObject(i);

                    JSONObject contacts = friend.getJSONObject("contacts");

                    String phone = contacts.getString("mobile");
                    String email = contacts.getString("email");
                    String skype = contacts.getString("skype");

                    Log.d(LOG_TAG, "phone: " + phone);
                    Log.d(LOG_TAG, "email: " + email);
                    Log.d(LOG_TAG, "skype: " + skype);
                }*/




            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter = new ArtistAdapter(mainActivity.this, artists);

            Log.d(LOG_TAG, Integer.toString(artists.size()));

            gvMain.setAdapter(adapter);
        }
    }

    protected void test()
    {
        /*ArrayList<String> genre = new ArrayList<String>();

            genre.add("Genre");
            genre.add("Genre2");
        */
        /*String[] genre = new String[2];
        genre[0] = "Genre1";
        genre[1] = "Genre2";

        artists = new ArrayList<Artist>();
        artists.add(new Artist(0, "Name1", genre, 10, 15, "link1", "description1"));
        artists.add(new Artist(1, "Name2", genre, 10, 15, "link2", "description2"));
        artists.add(new Artist(2, "Name3", genre, 10, 15, "link1", "description1"));
        artists.add(new Artist(3, "Name4", genre, 10, 15, "link2", "description2"));
*/
        adapter = new ArtistAdapter(this, artists);
      //  adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.tvText, artists);
        gvMain.setAdapter(adapter);
    }
}
