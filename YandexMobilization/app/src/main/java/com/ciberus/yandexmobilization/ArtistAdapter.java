package com.ciberus.yandexmobilization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Ciber on 20.04.2016.
 */
//Адаптер для "Исполнителей"
public class ArtistAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Artist> objects;
    ImageLoader imageLoader;

    ArtistAdapter(Context context, ArrayList<Artist> artists) {
        ctx = context;
        objects = artists;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_artist, parent, false);
        }

        Artist artist = getArtist(position);

        // Заполняем View в пункте списка данными из массива "Исполнителей": картинка, имя, жанры, альбомы и треки
        imageLoader.displayImage(artist.cover.small, ((ImageView) view.findViewById(R.id.image_artist_preview))); //Загружаем и кешируем картинку с помощью библиотеки UIL

        ((TextView) view.findViewById(R.id.text_name)).setText(artist.name.toString());

        ((TextView) view.findViewById(R.id.text_genres)).setText(artist.takeGenresList());

        ((TextView) view.findViewById(R.id.text_albums_and_tracks)).setText(artist.takeAlbumsAndTracksList(Artist.AlbumsAndTracksSeparator.comma));

        return view;
    }

    // Артист по позиции
    Artist getArtist(int position) {
        return ((Artist) getItem(position));
    }
}
