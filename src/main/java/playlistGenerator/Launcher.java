package playlistGenerator;

import playlistGenerator.controllers.FileScanController;

public class Launcher {

    public static void main(String[] args) throws Exception {
        FileScanController fileScanController = new FileScanController();

        fileScanController.scan();
//       scanController.query();



//        genreJSON p = new genreJSON();
//        Map<String, List<String>> res =  p.parse();

        //System.out.println(res);
    }


}
