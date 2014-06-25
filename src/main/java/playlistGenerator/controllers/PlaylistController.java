package playlistGenerator.controllers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PlaylistController {

    public static final String PLAYLIST_FILENAME = "playlist.m3u";
    private BufferedWriter writer;

    public void create(List<String> filePaths) throws IOException {

        createPlaylistFile();

        for(String filePath : filePaths) {
            try {
                writer.write(filePath);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writer.close();
    }

    private void createPlaylistFile() {
        try {

            writer = new BufferedWriter(new FileWriter(PLAYLIST_FILENAME));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
