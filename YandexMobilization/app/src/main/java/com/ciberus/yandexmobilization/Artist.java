package com.ciberus.yandexmobilization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    protected Cover cover;

    public Artist(int id, String name, ArrayList<String> genres, int tracks, int albums, String link, String description, Cover cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }


    public Artist(){};

    //Вспомогательный класс Covers
    public static class Cover {
        protected String small;
        protected String big;
        /*protected Image smallCover;
        protected Image bigCover;
*/
        public Cover(String small, String big)
        {
            this.small = small;
            this.big = big;
        }
    }

    public static Artist deserialize(JSONObject json) throws JSONException {

        JSONArray genresJSON = json.getJSONArray("genres");

        ArrayList<String> genres = new ArrayList<String>();
        if (genresJSON != null) {
            int len = genresJSON.length();
            for (int i=0;i<len;i++){
                genres.add(genresJSON.get(i).toString());
            }
        }

        JSONObject cover = json.getJSONObject("cover");

        return new Artist(
                json.optInt("id"),
                json.optString("name"),
                genres,
                json.optInt("tracks"),
                json.optInt("albums"),
                json.optString("link"),
                json.optString("description"),
                new Cover(cover.optString("small"),
                        cover.optString("big")));
    }
}
