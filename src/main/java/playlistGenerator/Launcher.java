package playlistGenerator;

import playlistGenerator.controllers.ScanController;
import playlistGenerator.tools.genreJSON;

import java.util.List;
import java.util.Map;

public class Launcher {

    public static void main(String[] args) throws Exception {
        ScanController scanController = new ScanController();

//        scanController.scan();
       //scanController.query();
        genreJSON p = new genreJSON();
        Map<String, List<String>> res =  p.parse();

        //System.out.println(res);
    }


}
