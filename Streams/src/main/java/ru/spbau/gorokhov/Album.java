package ru.spbau.gorokhov;

import java.util.Arrays;
import java.util.List;
/**
 * Created by wackloner on 14.11.2016.
 */
public class Album {

    private final String name;
    private final List<Track> tracks;
    private final Artist artist;

    public Album(Artist artist, String name, Track... tracks) {
        this.name = name;
        this.tracks = Arrays.asList(tracks);
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public Artist getArtist() {
        return artist;
    }
}