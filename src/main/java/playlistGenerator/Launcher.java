package playlistGenerator;

import playlistGenerator.controllers.ScanController;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher {

    public static void main(String[] args) throws Exception {


        ScanController scanController = new ScanController();
        Logger.getLogger("org.jaudiotagger").setLevel(Level.WARNING);

        scanController.launch();
    }
}
