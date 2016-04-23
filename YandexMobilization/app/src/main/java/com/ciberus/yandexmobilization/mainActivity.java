package com.ciberus.yandexmobilization;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class mainActivity extends AppCompatActivity {

    public static ArrayList<Artist> artists;
    public static String LOG_TAG = "my_log";

    private ArtistAdapter adapter;
    private ListView lv_Artist;
    private ImageLoader imageLoader;
    private String requestArtistsURL = "http://cache-default03e.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private RequestQueue requestQueue; //Очередь запросов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализируем UIL
        //Создаем дефолтные опции отображения изображения
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.unknown)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();

        // Создаем глобальную конфигурацию и инициализируем UIL с помощью конфига
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheSize(50 * 1024 * 1024) // 50 MB
                .defaultDisplayImageOptions(options)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        lv_Artist = (ListView)findViewById(R.id.list_artists);

        requestQueue = Volley.newRequestQueue(this);

        //Создаем кэшируемый запрос "Исполнителей"
        CacheRequest jsonArtistsRequest = new CacheRequest(0, requestArtistsURL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            final String jsonString = new String(response.data,
                                    /*HttpHeaderParser.parseCharset(response.headers)*/Charset.defaultCharset()); //По дефолту берется неправильная кодировка из заголовков


                            artists = new ArrayList<Artist>();

                            JSONArray artistsJSON = new JSONArray(jsonString);
                            JSONObject curArtistJSON;

                            for (int i = 0; i < artistsJSON.length(); i++) {
                                try {

                                    curArtistJSON = artistsJSON.getJSONObject(i);
                                    artists.add(Artist.deserialize(curArtistJSON));

                                } catch (JSONException e) {
                                    Log.e(LOG_TAG, "Unexpected JSON exception", e);
                                }
                            }

                            adapter = new ArtistAdapter(mainActivity.this, artists);
                            lv_Artist.setAdapter(adapter);

                            Log.d(LOG_TAG, Integer.toString(artists.size()));

                        } catch (/*UnsupportedEncodingException |*/ JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                //Если отсутствует интернет показываем уведомление
                new Response.ErrorListener() {
                    @Override
                    public void  onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Не удалось подключиться к интернету!\n\n" + error,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
        );

        requestQueue.add(jsonArtistsRequest); //Отправляем запрос "Исполнителей" в очередь

        //Обработчик нажатия на итем списка
        lv_Artist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


    //Класс для создания кэшируемых запросов
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
}
