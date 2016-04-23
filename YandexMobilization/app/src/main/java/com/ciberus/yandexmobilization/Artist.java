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

    public enum AlbumsAndTracksSeparator {
        dot,
        comma
    }

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

    public Artist() {} //Конструктор без параметров

    //Возвращает список жанров через запятую
    public String takeGenresList()
    {
        String genres = "";

        for (int i = 0; i < this.genres.size(); i++)
        {
            genres += this.genres.get(i);
            if (i < this.genres.size()-1)
                genres += ", ";
        }

        return genres;
    }

    //Возвращет колв-ов альбомов и треков через разделитель
    public String takeAlbumsAndTracksList(AlbumsAndTracksSeparator separator)
    {
        String albumsAndTracks = this.albums + " ";

        /*if (this.albums == 1 || this.albums == 21)
            albumsAndTracks+= " альбом";
        else if ((this.albums >= 2 && this.albums <= 4))
            albumsAndTracks+= " альбома";
        else if (this.albums >=5 && this.albums <= 20)
            albumsAndTracks+= " альбомов";
*/
        if ((this.albums % 10 == 1) && (this.albums % 100 != 11))
            albumsAndTracks += "альбом";
        else if ((this.albums % 10 >= 2) && (this.albums % 10 <= 4) && ((this.albums % 100 < 10) || (this.albums % 100 >= 20)))
            albumsAndTracks += "альбома";
        else
            albumsAndTracks += "альбомов";

        albumsAndTracks+= ((separator == AlbumsAndTracksSeparator.comma) ? ", " : " • ") + this.tracks + " ";

        if ((this.tracks % 10 == 1) && (this.tracks % 100 != 11))
            albumsAndTracks += "песня";
        else if ((this.tracks % 10 >= 2) && (this.tracks % 10 <= 4) && ((this.tracks % 100 < 10) || (this.tracks % 100 >= 20)))
            albumsAndTracks += "песни";
        else
            albumsAndTracks += "песен";

        return albumsAndTracks;
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

        JSONObject coverJSON = json.getJSONObject("cover");

        return new Artist(
                json.optInt("id"),
                json.optString("name"),
                genres,
                json.optInt("tracks"),
                json.optInt("albums"),
                json.optString("link"),
                json.optString("description"),
                new Cover(coverJSON.optString("small"),
                        coverJSON.optString("big")));
    }
}
