package com.ciberus.yandexmobilization;

import android.app.Activity;
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

    private ImageLoader imageLoader;
    private ImageView ivCoverBig;
    private TextView tvGenres;
    private TextView tvAlbumsAndSongs;
    private TextView tvBiography;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_artist);

        Intent intent = getIntent();

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

        int position = intent.getExtras().getInt("id"); //Узнаем id отправленный из главного активити
        Artist artist = mainActivity.artists.get(position);

        //Находим необходимые view по id
        ivCoverBig = ((ImageView) findViewById(R.id.image_cover_big));
        tvGenres = ((TextView) findViewById(R.id.text_genres));
        tvAlbumsAndSongs = ((TextView) findViewById(R.id.text_albums_and_tracks));
        tvBiography = ((TextView) findViewById(R.id.text_biography));

        //Выделить в отдельную функцию
        String genres = "";
        for (int i = 0; i < artist.genres.size(); i++)
        {
            genres += artist.genres.get(i);
            if (i < artist.genres.size()-1)
                genres += ", ";
        }

        //Заполняем активити
        setTitle(artist.name);
        if (ivCoverBig != null) imageLoader.displayImage(artist.cover.big, ivCoverBig);
        if (ivCoverBig != null) tvGenres.setText(genres);
        if (ivCoverBig != null) tvAlbumsAndSongs.setText(artist.takeAlbumsAndTracksList(Artist.AlbumsAndTracksSeparator.dot));
        if (ivCoverBig != null) tvBiography.setText(artist.description);
    }
}
