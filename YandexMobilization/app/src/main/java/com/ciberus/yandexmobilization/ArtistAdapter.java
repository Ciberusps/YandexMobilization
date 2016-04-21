package com.ciberus.yandexmobilization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ciber on 20.04.2016.
 */
public class ArtistAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Artist> objects;

    ArtistAdapter(Context context, ArrayList<Artist> artists) {
        ctx = context;
        objects = artists;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        Artist p = getArtist(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((ImageView) view.findViewById(R.id.ivArtistPreview)).setImageResource(R.color.colorPrimary); /*setImageURI(p.covers.smallCover);*/
        ((TextView) view.findViewById(R.id.tvName)).setText(p.name.toString());
        String genres = "";
        for (int i = 0; i < p.genres.size(); i++)
        {
            genres += p.genres.get(i);
            if (i < p.genres.size()-1)
                genres += ", ";
        }
        ((TextView) view.findViewById(R.id.tvGenres)).setText(genres);
        ((TextView) view.findViewById(R.id.tvAlbumsAndSongs)).setText(p.albums + " альбомов, " + p.tracks + " песен");
        //((ImageView) view.findViewById(R.id.ivImage)).setImageResource(p.image);

        //CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        // присваиваем чекбоксу обработчик
        //cbBuy.setOnCheckedChangeListener(myCheckChangList);
        // пишем позицию
        //cbBuy.setTag(position);
        // заполняем данными из товаров: в корзине или нет
        //cbBuy.setChecked(p.box);
        return view;
    }

    // товар по позиции
    Artist getArtist(int position) {
        return ((Artist) getItem(position));
    }
}
