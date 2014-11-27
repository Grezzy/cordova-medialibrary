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
    public static final String ACTION_GET_SONG_FILES = "getSongFiles";
    public static final String ACTION_GET_ARTISTS = "getArtists";
    public static final String ACTION_GET_GENRES = "getGenres";
    public static final String ACTION_GET_PLAYLISTS = "getPlaylists";

    public static final String ACTION_GET_ALBUMS_BY_ARTIST = "getAlbumsByArtist";
    public static final String ACTION_GET_ARTISTS_BY_GENRE = "getArtistsByGenre";
    public static final String ACTION_GET_SONGS_BY_ALBUM = "getSongsByAlbum";
    public static final String ACTION_GET_SONGS_BY_PLAYLIST = "getSongsByPlaylist";

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    JSONObject returnObj = new JSONObject();

                    if (action.equals(ACTION_IS_SUPPORTED)) {
                        addProperty(returnObj, ACTION_IS_SUPPORTED, true);
                    } else if (action.equals(ACTION_INITIALIZE)) {
                        addProperty(returnObj, "songs", getSongFiles());
                    } else if (action.equals(ACTION_GET_ARTISTS)) {
                        addProperty(returnObj, "artists", getArtists());
                    } else if (action.equals(ACTION_GET_ALBUMS_BY_ARTIST)) {
                        addProperty(returnObj, "albums", getAlbumsByArtist(args.getInt(0)));
                    } else if (action.equals(ACTION_GET_SONGS_BY_ALBUM)) {
                        addProperty(returnObj, "songs", getSongsByAlbum(args.getInt(0)));
                    } else if (action.equals(ACTION_GET_ALBUMS)) {
                        addProperty(returnObj, "albums", getAlbums());
                    } else if (action.equals(ACTION_GET_SONGS)) {
                        addProperty(returnObj, "songs", getSongs());
                    } else if (action.equals(ACTION_GET_PLAYLISTS)) {
                        addProperty(returnObj, "playlists", getPlaylists());
                    } else if (action.equals(ACTION_GET_SONGS_BY_PLAYLIST)) {
                        addProperty(returnObj, "songs", getSongsByPlaylist(args.getInt(0)));
                    } else if (action.equals(ACTION_GET_SONG_FILES)) {
                        addProperty(returnObj, "songs", getSongFiles());
                    }

                    callbackContext.success(returnObj);
                }
            catch (JSONException e) {
                JSONObject returnObj = new JSONObject();
                addProperty(returnObj, "message", e.toString());
                callbackContext.error(returnObj);
            }
        }
        });
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

    private JSONArray getPlaylists() {

        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Playlists.NAME);
        final JSONArray playlists = new JSONArray();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
                final int nameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);

                final JSONObject playlist = new JSONObject();
                final Integer id = cursor.getInt(idIdx);
                try {
                    playlist.put("id", id);
                    playlist.put("title", cursor.getString(nameIdx));

                    playlists.put(playlist);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        return playlists;
    }

    private JSONArray getSongsByPlaylist(final int playlistId){
        final JSONArray songs = new JSONArray();
        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                null,
                MediaStore.Audio.Playlists.Members.IS_MUSIC + " != 0 ",
                null,
                MediaStore.Audio.Playlists.Members.TRACK);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID);
                final int albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM);
                final int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST);
                final int artistIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ARTIST_ID);
                final int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.TITLE);
                final int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DATA);
                final int albumIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.ALBUM_ID);
                final int durationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.DURATION);
                final int playlistIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.PLAYLIST_ID);
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
                    song.put("playlistId", cursor.getString(playlistIdIdx));
                    songs.put(song);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        return songs;
    }

    private JSONArray getSongFiles(){
        final JSONArray songs = new JSONArray();
        final ContentResolver resolver = this.cordova.getActivity().getContentResolver();
        final Cursor cursor = resolver.query(MediaStore.Files.getContentUri("external"),
                null,
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO,
                null,
                MediaStore.Files.FileColumns.DISPLAY_NAME);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final int idIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                final int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                final int parentIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT);
                final int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                final int sizeIdx = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
                final JSONObject song = new JSONObject();
                try{
                    song.put("id", cursor.getInt(idIdx));
                    song.put("title", cursor.getString(titleIdx));
                    song.put("url", cursor.getString(dataIdx));
                    song.put("size", cursor.getString(sizeIdx));
                    song.put("parent", cursor.getString(parentIdx));
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