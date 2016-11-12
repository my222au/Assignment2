package dv606.my222au.assignment2.mp3player;

public class Track {

    private final String artist;
    private final String album;
    private final String name;
    private final String path;

    private Track next = null;

    public Track(String artist, String album, String name, String path) {
        this.artist = artist;
        this.album = album;
        this.name = name;
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setNext(Track track) {
        next = track;
    }

    public Track getNext() {
        return next;
    }

    @Override
    public String toString() {
        String result = null;
        if (path != null)
            result = path;

        if (name != null)
            result = name;

        if (artist != null)
            result = artist + " - " + result;

        if (album != null)
            result = result + " (" + album + ")";

        return result;
    }
}
