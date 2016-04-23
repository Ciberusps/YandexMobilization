package com.ciberus.yandexmobilization;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CacheRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
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
    RequestQueue requestQueue;


    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        gvMain = (GridView)findViewById(R.id.gvMain);
        requestQueue = Volley.newRequestQueue(this);

        //Делаем кэшируемый запрос "Исполнителей"
        CacheRequest jsonArtistsRequest = new CacheRequest(0, requestArtistsURL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            final String jsonString = new String(response.data,
                                    /*HttpHeaderParser.parseCharset(response.headers)*/Charset.defaultCharset()); //По дефолту берется неправильная кодировка из заголовков

                            artists = new ArrayList<Artist>();

                            JSONArray artistsJSON = new JSONArray(jsonString);

                            JSONObject curArtistJSON;
/*
                            Artist curArtist;

                            JSONArray curArtistGenresJSON;
                            ArrayList<String> tempArtistGenres;
                            JSONObject curArtistCoverJSON;
                            //Artist.Covers tempArtistCovers;
*/
                            Artist artist = new Artist();

                            //C
                            for (int i = 0; i < artistsJSON.length(); i++) {
                                try {

                                    curArtistJSON = artistsJSON.getJSONObject(i);
                                    //tempArtistGenres = new String[artistJSON.getJSONArray("Genres").length()];
                                    artist = Artist.deserialize(curArtistJSON);

                                    /*curArtist = new Artist();
                                    if (!curArtistJSON.isNull("id"))
                                        curArtist.id = curArtistJSON.getInt("id");
                                    if (!curArtistJSON.isNull("name"))
                                        curArtist.name = curArtistJSON.getString("name");
                                    if (!curArtistJSON.isNull("tracks"))
                                        curArtist.tracks = curArtistJSON.getInt("tracks");
                                    if (!curArtistJSON.isNull("albums"))
                                        curArtist.albums = curArtistJSON.getInt("albums");
                                    if (!curArtistJSON.isNull("link"))
                                        curArtist.link = curArtistJSON.getString("link");
                                    if (!curArtistJSON.isNull("description"))
                                        curArtist.description = curArtistJSON.getString("description");

                                    if (curArtistJSON.getJSONArray("genres") != null) {
                                        tempArtistGenres = new ArrayList<String>();
                                        curArtistGenresJSON = curArtistJSON.getJSONArray("genres");
                                        for (int j = 0; j < curArtistGenresJSON.length(); j++) {
                                            tempArtistGenres.add(curArtistGenresJSON.getString(j));
                                        }
                                        curArtist.genres = tempArtistGenres;
                                    }

                                    if (curArtistJSON.getJSONObject("cover") != null) {
                                        curArtistCoverJSON = curArtistJSON.getJSONObject("cover");
                                        tempArtistCovers = new Artist.Covers(
                                                curArtistCoverJSON.getString("small"),
                                                curArtistCoverJSON.getString("big")
                                        );
                                        curArtist.covers = tempArtistCovers;
                                    }*/

                                    artists.add(artist);
                                } catch (JSONException e) {
                                    Log.e(LOG_TAG, "unexpected JSON exception", e);
                                    // Do something to recover ... or kill the app.
                                }
                            }

                            adapter = new ArtistAdapter(mainActivity.this, artists);

                            Log.d(LOG_TAG, Integer.toString(artists.size()));
                            //Log.d(LOG_TAG, jsonString);

                            gvMain.setAdapter(adapter);

                        } catch (/*UnsupportedEncodingException |*/ JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void  onErrorResponse(VolleyError error) {

                    }
                }
        );

        requestQueue.add(jsonArtistsRequest);


//        new ParseTask().execute();

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


    private class CacheRequest extends Request<NetworkResponse> {
        private final Response.Listener<NetworkResponse> mListener;
        private final Response.ErrorListener mErrorListener;

        public CacheRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
            if (cacheEntry == null) {
                cacheEntry = new Cache.Entry();
            }
            final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
            final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
            long now = System.currentTimeMillis();
            final long softExpire = now + cacheHitButRefreshed;
            final long ttl = now + cacheExpired;
            cacheEntry.data = response.data;
            cacheEntry.softTtl = softExpire;
            cacheEntry.ttl = ttl;
            String headerValue;
            headerValue = response.headers.get("Date");
            if (headerValue != null) {
                cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            headerValue = response.headers.get("Last-Modified");
            if (headerValue != null) {
                cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            cacheEntry.responseHeaders = response.headers;
            return Response.success(response, cacheEntry);
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        protected VolleyError parseNetworkError(VolleyError volleyError) {
            return super.parseNetworkError(volleyError);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }
    }






   /* //Асинхронно делаем запрос и парсим полученный JSON
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

                    *//*curArtist = new Artist(
                            curArtistJSON.getInt("id"),
                            curArtistJSON.getString("name"),
                            tempArtistGenres,
                            curArtistJSON.getInt("tracks"),
                            curArtistJSON.getInt("albums"),
                            curArtistJSON.getString("link"),
                            curArtistJSON.getString("description"),
                            tempArtistCovers
                    );*//*

                    artists.add(curArtist);
                }

                /*//*/ 1. достаем инфо о втором друге - индекс 1
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
                }*//*




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

        adapter = new ArtistAdapter(this, artists);
      //  adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.tvText, artists);
        gvMain.setAdapter(adapter);
    }*/
}
