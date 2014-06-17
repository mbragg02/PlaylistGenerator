package playlistGenerator;

import playlistGenerator.models.Track;
import playlistGenerator.tools.MP4Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public final static String musicPath = "/Users/mbragg/Music/iTunes/iTunes Media/Music";
    public static final String M4A = ".m4a";
    private List<File> files;
    private List<Track> tracks;
    private int trackIDs;

    public static void main(String[] args) throws IOException {

        Main main = new Main();
        main.launcher();
    }


    private void launcher() throws IOException {

        files = new ArrayList<>();
        tracks = new ArrayList<>();
        trackIDs = 0;

        fileScanner();

        MP4Parser parser = new MP4Parser();

        for (File file : files) {
            Track track = new Track(trackIDs, file.getName(), file.getAbsolutePath());
            trackIDs++;
            track.setMeta(parser.getMeta(file.getAbsolutePath()));
            tracks.add(track);
        }


        for(Track track : tracks) {
            System.out.println(track.getTrackMeta().getTitle());
        }


    }

    private void fileScanner() {

        try {
            Files.walk(new File(musicPath).toPath())
                    .filter(p -> p.getFileName().toString().endsWith(M4A))
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .forEach(p -> files.add(p.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
