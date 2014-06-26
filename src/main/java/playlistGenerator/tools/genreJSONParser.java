package playlistGenerator.tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class genreJSONParser {

    private JSONParser parser;
    private Map<String, List<String>> allGenres;

    public genreJSONParser() {
        parser = new JSONParser();
        allGenres = new HashMap<>();
    }

    public Map<String, List<String>> parse(String filename) {

        try {

            URL genresURL = getClass().getResource(filename);

            Object obj = parser.parse(new FileReader(genresURL.getFile()));
            JSONObject genres = (JSONObject) obj;

            for (Object o : genres.entrySet()) {
                Map.Entry entry = (Map.Entry) o;

                List<String> subGenresList = new ArrayList<>();

                JSONArray subGenres = (JSONArray) entry.getValue();

                for (Object sub : subGenres) {
                    subGenresList.add((String) sub);
                }
                allGenres.put(entry.getKey().toString(), subGenresList);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return allGenres;
    }









    // Decoder v2
//    public Map<String, List<String>> parse() {
//        JSONParser parser = new JSONParser();
//        Map<String, List<String>> genreMap = new HashMap<>();
//
//        try {
//            Object obj = parser.parse(new FileReader("/Users/mbragg/IdeaProjects/PlaylistGenerator/src/main/resources/genresv2.json"));
//            JSONObject music = (JSONObject) obj;
//
//            for (Object musicObj : music.entrySet()) {
//                Map.Entry entry = (Map.Entry) musicObj;
//                Map val = (Map) entry.getValue();
//
//                List<String> subGenresList = new ArrayList<>();
//                JSONArray subGenres = (JSONArray) val.get("subgenres");
//                for (Object o : subGenres) {
//                    subGenresList.add(o.toString());
//                }
//                genreMap.put(val.get("name").toString(), subGenresList);
//            }
//
//        } catch (ParseException | IOException e) {
//            e.printStackTrace();
//        }
//
//        return genreMap;
//    }


//    public void encode() {
//
//        //JsonParser parser = new JsonParser();
//        Map<String, List<String>> genres = parse();
//
//        JSONObject outer = new JSONObject();
//        int count = 0;
//        for(Map.Entry<String, List<String>> x : genres.entrySet()) {
//            JSONObject master = new JSONObject();
//
//
//            JSONArray list = new JSONArray();
//
//            for(String t : x.getValue()) {
//                list.add(t);
//            }
//            //System.out.println(list);
//            outer.put(x.getKey(), list);
//            //master.put("name", x.getKey());
//
//            //System.out.println(master);
//           // outer.put(count++, master );
//        }
//
//        System.out.println(outer);
//
//    }


}


///////// DECODER v1 //////////////////////////////////
//
//    public Map<String, List<String>> parse() {
//        JSONParser parser = new JSONParser();
//        Map<String, List<String>> genreMap = new HashMap<>();
//        List<String> genreKeys = new ArrayList<>();
//
//        try {
//
//            Object obj = parser.parse(new FileReader("/Users/mbragg/IdeaProjects/PlaylistGenerator/src/main/resources/genres.json"));
//            JSONObject music = (JSONObject) obj;
//            JSONObject genres= (JSONObject) music.get("subgenres");
//
//            for(Object s : genres.keySet()) {
//                genreKeys.add((String) s);
//            }
//
//            for(String key : genreKeys) {
//
//                JSONObject genre = (JSONObject) genres.get(key);
//                JSONObject subGenres = (JSONObject) genre.get("subgenres");
//
//                //System.out.println(genre.get("name"));
//                List<String> subGenresList = new ArrayList<>();
//
//                if(subGenres != null) {
//                    for (Object o : subGenres.entrySet()) {
//                        Map.Entry entry = (Map.Entry) o;
//                        Map val = (Map) entry.getValue();
//                        subGenresList.add(val.get("name").toString());
//                      //  System.out.println(val.get("name"));
//                    }
//                }
//
//                genreMap.put(genre.get("name").toString(), subGenresList);
//
//             //   System.out.println("###########################");
//            }
//
//        } catch (ParseException | IOException e) {
//            e.printStackTrace();
//        }
//        return genreMap;
//    }



