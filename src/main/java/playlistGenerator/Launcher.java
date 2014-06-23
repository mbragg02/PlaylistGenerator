package playlistGenerator;

import playlistGenerator.controllers.ScanController;

public class Launcher {

    public static void main(String[] args) throws Exception {
        ScanController scanController = new ScanController();
        scanController.launch();
    }
}
