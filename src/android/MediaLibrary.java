package com.firerunner.cordova;

/* some code is taken from
    https://github.com/exfm/iex-audio/blob/master/android/fm/ex/android/MediaStorePlugin.java
*/

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


public class MediaLibrary extends CordovaPlugin {

    private static final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");
    private String unknown;
    public static final String ACTION_IS_SUPPORTED = "isSupported";
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_GET_ALBUMS = "getAlbums";
    public static final String ACTION_GET_SONGS = "getSongs";
    public static final String ACTION_GET_ARTISTS = "getArtists";
    public static final String ACTION_GET_GENRES = "getGenres";

    public static final String ACTION_GET_ALBUMS_BY_ARTIST = "getAlbumsByArtist";
    public static final String ACTION_GET_ARTISTS_BY_GENRE = "getArtistsByGenre";
    public static final String ACTION_GET_SONGS_BY_ALBUM = "getSongsByAlbum";

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        JSONObject returnObj = new JSONObject();

        if(action.equals(ACTION_IS_SUPPORTED)){
            addProperty(returnObj, ACTION_IS_SUPPORTED, true);
        } else if(action.equals(ACTION_INITIALIZE)) {
            addProperty(returnObj, ACTION_INITIALIZE, true);
        } else if(action.equals(ACTION_GET_ARTISTS)) {
            addProperty(returnObj, "artists", getArtists());
        } else if(action.equals(ACTION_GET_ALBUMS_BY_ARTIST)) {
            addProperty(returnObj, "albums", getAlbumsByArtist(args.getInt(0)));
        } else if(action.equals(ACTION_GET_SONGS_BY_ALBUM)) {
            addProperty(returnObj, "songs", getSongsByAlbum(args.getInt(0)));
        } else if(action.equals(ACTION_GET_ALBUMS)) {
            addProperty(returnObj, "albums", getAlbums());
        } else if(action.equals(ACTION_GET_SONGS)) {
            addProperty(returnObj, "songs", getSongs());
        }

        callbackContext.success(returnObj);
        return true;
    }

    private JSONArray getArtists(){

        final JSONArray artists = new JSONArray();
        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Artists.ARTIST);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
                final int nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                final JSONObject artist = new JSONObject();
                try{
                    artist.put("id", cursor.getInt(idIdx));
                    artist.put("name", cursor.getString(nameIdx));
                    artists.put(artist);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        return artists;
    }

    private JSONArray getAlbumsByArtist(final int artistId){
        final JSONArray albums = new JSONArray();

        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                null,
                "artist_id = " + artistId,
                null,
                MediaStore.Audio.Albums.ALBUM);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
                final Integer albumId = cursor.getInt(idIdx);

                final int nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                final int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
                final int coverIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);

                final JSONObject album = new JSONObject();
                try{
                    album.put("id", albumId);
                    album.put("artist", cursor.getString(artistIdx));
                    album.put("title", cursor.getString(nameIdx));
                    album.put("image", cursor.getString(coverIdx));
                    albums.put(album);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        return albums;
    }

    private JSONArray getSongsByAlbum(final int albumId){
        final JSONArray songs = new JSONArray();
        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.ALBUM_ID + " = " + albumId,
                null,
                MediaStore.Audio.Media.TRACK);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                final int albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                final int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                final int artistIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
                final int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                final int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                final int albumIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                final int durationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                final JSONObject song = new JSONObject();
                try{
                    song.put("id", cursor.getInt(idIdx));
                    song.put("album", cursor.getString(albumIdx));
                    song.put("albumId", cursor.getString(albumIdIdx));
                    song.put("artist", cursor.getString(artistIdx));
                    song.put("artistId", cursor.getString(artistIdIdx));
                    song.put("title", cursor.getString(titleIdx));
                    song.put("url", cursor.getString(dataIdx));
                    song.put("duration", cursor.getString(durationIdx));
                    songs.put(song);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        return songs;
    }

    private JSONArray getAlbums(){

        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Albums.ALBUM);
        final JSONArray albums = new JSONArray();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
                final int nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                final int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
                final int coverIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART);

                final JSONObject album = new JSONObject();
                final Integer id = cursor.getInt(idIdx);
                try {
                    album.put("id", id);
                    album.put("artist", cursor.getString(artistIdx));
                    album.put("title", cursor.getString(nameIdx));
                    album.put("image", cursor.getString(coverIdx));

                    albums.put(album);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        return albums;
    }

    private JSONArray getSongs(){
        final JSONArray songs = new JSONArray();
        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.TITLE);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                final int albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                final int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                final int artistIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
                final int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                final int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                final int albumIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                final int durationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                final JSONObject song = new JSONObject();
                try{
                    song.put("id", cursor.getInt(idIdx));
                    song.put("album", cursor.getString(albumIdx));
                    song.put("albumId", cursor.getString(albumIdIdx));
                    song.put("artist", cursor.getString(artistIdx));
                    song.put("artistId", cursor.getString(artistIdIdx));
                    song.put("title", cursor.getString(titleIdx));
                    song.put("url", cursor.getString(dataIdx));
                    song.put("duration", cursor.getString(durationIdx));
                    songs.put(song);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        return songs;
    }

    private void addProperty(JSONObject obj, String key, Object value)
    {
        try {
            obj.put(key, value);
        }
        catch (JSONException e) { }

    }
}