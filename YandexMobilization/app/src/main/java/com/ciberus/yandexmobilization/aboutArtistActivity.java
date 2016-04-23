package com.ciberus.yandexmobilization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class aboutArtistActivity extends AppCompatActivity {

    ImageLoader imageLoader;
    ImageView ivCoverBig;
    TextView tvGenres;
    TextView tvAlbumsAndSongs;
    TextView tvBiography;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_artist);

        Intent intent = getIntent();

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


        int position = intent.getExtras().getInt("id");
        Artist artist = mainActivity.artists.get(position);


        ivCoverBig = ((ImageView) findViewById(R.id.ivCoverBig));
        tvGenres = ((TextView) findViewById(R.id.tvGenres));
        tvAlbumsAndSongs = ((TextView) findViewById(R.id.tvAlbumsAndSongs));
        tvBiography = ((TextView) findViewById(R.id.tvBiography));


        //Выделить в отдельную функцию
        String genres = "";
        for (int i = 0; i < artist.genres.size(); i++)
        {
            genres += artist.genres.get(i);
            if (i < artist.genres.size()-1)
                genres += ", ";
        }

        if (ivCoverBig != null) imageLoader.displayImage(artist.cover.big, ivCoverBig);
        if (ivCoverBig != null) tvGenres.setText(genres);
        if (ivCoverBig != null) tvAlbumsAndSongs.setText(artist.albums + " альбомов • " + artist.tracks + " песен");
        if (ivCoverBig != null) tvBiography.setText(artist.description);

        //ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
    }
}
