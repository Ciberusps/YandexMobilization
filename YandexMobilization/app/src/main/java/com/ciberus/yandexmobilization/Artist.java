package com.ciberus.yandexmobilization;

import android.media.Image;

import java.util.ArrayList;

/**
 * Created by Ciber on 20.04.2016.
 */

// Класс "Исполнитель" для удобного хранения и доступа к
// данным об исполнителе
public class Artist {
    protected int id;
    protected String name;
    protected ArrayList<String> genres;
    protected int tracks;
    protected int albums;
    protected String link;
    protected String description;
    protected Covers covers;

    public Artist(int id, String name, ArrayList<String> genres, int tracks, int albums, String link, String description, Covers covers) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.covers = covers;
    }

    public Artist(){};

    //Вспомогательный класс Covers
    public static class Covers {
        protected String linkSmallCover;
        protected String linkBigCover;
        protected Image smallCover;
        protected Image bigCover;

        public Covers(String linkSmallCover, String linkBigCover)
        {
            this.linkSmallCover = linkSmallCover;
            this.linkBigCover = linkBigCover;
        }
    }
}
