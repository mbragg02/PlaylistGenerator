package playlistGenerator.models;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import playlistGenerator.tools.genreJSONParser;

import java.io.File;
import java.util.*;

public class graphDb {

    private static final String DB_PATH = "src/main/resources/neo4j-test-db";
    public static final String INDEX_FIELD = "filename";

    private GraphDatabaseService graphDb;
    private final Label label;
    private ExecutionEngine engine;
    private final Map<String, List<String>> genres;

    private static enum RelTypes implements RelationshipType {
        SIMILAR
    }

    public graphDb() {
        genres = new genreJSONParser().parse("genres.json");
        label = () -> "Track";
        initialize();
    }

    private void initialize() {
        //deleteFileOrDirectory(new File(DB_PATH));
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        engine = new ExecutionEngine(graphDb);
        registerShutdownHook(graphDb);
        configIndex();
    }

    private void configIndex() {

        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = graphDb.schema();

            Iterator<IndexDefinition> indexDefinitionIterator = schema.getIndexes(label).iterator();

            // Only if there is no index made already
            if (!indexDefinitionIterator.hasNext()) {
                schema.indexFor(label)
                        .on(INDEX_FIELD)
                        .create();
            }
            tx.success();
        }
    }

    public void addNode(Track track) {
        try (Transaction tx = graphDb.beginTx()) {

            Node currentNode = graphDb.createNode(label);

            currentNode.setProperty("features", track.getFeatureVector());
            currentNode.setProperty("features_length", track.getFeatureVectorLength());
            currentNode.setProperty("filename", track.getFilename());
            currentNode.setProperty("filepath", track.getFilePath());
            currentNode.setProperty("genreCategory",getGenreCategory(track.getTags().get("GENRE")));


            for (Map.Entry<String, String> tag : track.getTags().entrySet()) {
                currentNode.setProperty(tag.getKey(), tag.getValue());
            }

            ResourceIterable<Node> nodes = GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(label);

            for (Node node : nodes) {
                if (node.getId() != currentNode.getId()) {
                    Relationship relationship = currentNode.createRelationshipTo(node, RelTypes.SIMILAR);
                    relationship.setProperty("cosine", cosine(currentNode, node));
                }
            }
            tx.success();
        }
    }

    private String getGenreCategory(String subGenre) {

        for (Map.Entry<String, List<String>> entry : genres.entrySet()) {
            String key = entry.getKey().toLowerCase();

            if(subGenre.equalsIgnoreCase(key)) return key;
            for(String s : entry.getValue()) {

                if(subGenre.equalsIgnoreCase(s.toLowerCase())) return key;
            }

        }
        return "";
    }

    public boolean containsFileName(String filename) {
        try (Transaction ignored = graphDb.beginTx()) {

            try (ResourceIterator<Node> tracks =
                         graphDb.findNodesByLabelAndProperty(label, "filename", filename).iterator()) {
                return tracks.hasNext();
            }

        }
    }

    public List<String> getNearest(String filename, int limit) {

        try (Transaction tx = graphDb.beginTx()) {
            String query = "MATCH (t1:Track {filename:{filename}})-[s:SIMILAR]-(t2:Track) " +
                    "WITH t2, s.cosine AS sim " +
                    "ORDER BY sim DESC " +
                    "LIMIT {l} " +
                    "RETURN t2 AS Neighbour, sim AS Similarity";

            String queryWithGenre = "MATCH (t1:Track {filename:{filename}})-[s:SIMILAR]-(t2:Track) " +
                    "WHERE t1.genreCategory = t2.genreCategory " +
                    "WITH t2, s.cosine AS sim " +
                    "ORDER BY sim DESC " +
                    "LIMIT {l} " +
                    "RETURN t2 AS Neighbour, sim AS Similarity";

            Map<String, Object> params = new HashMap<>();
            params.put("filename", filename);
            params.put("l", limit);

            ExecutionResult result;
            try (Transaction ignored = graphDb.beginTx()) {
                result = engine.execute(queryWithGenre, params);
            }

            List<String> filePaths = new ArrayList<>();

            Iterator<Node> t = result.columnAs("Neighbour");

            // Add the query track to the playlist result list 1st
            filePaths.add(getQueryTrackFilePath(filename));

            // Add the query result filePaths next
            for (Node node : IteratorUtil.asIterable(t)) {
                filePaths.add((String) node.getProperty("filepath"));
            }
            return filePaths;
        }

    }

    private String getQueryTrackFilePath(String queryFileName) {
        try (Transaction tx = graphDb.beginTx()) {

            String query = "MATCH (t:Track) WHERE t.filename = {query} RETURN t.filepath AS query";

            Map<String, Object> params = new HashMap<>();
            params.put("query", queryFileName);
            ExecutionResult result;

            try (Transaction ignored = graphDb.beginTx()) {
                result = engine.execute(query, params);
            }

            Iterator<String> t = result.columnAs("query");
            return t.next();
        }

    }

    private double cosine(Node x, Node y) {
        double[] xFeatures = (double[]) x.getProperty("features");
        double[] yFeatures = (double[]) y.getProperty("features");
        return dotProduct(xFeatures, yFeatures) / ((double) x.getProperty("features_length") * (double) y.getProperty("features_length"));
    }

    private double dotProduct(double[] x, double[] y) {

        int minLength = x.length <= y.length ? x.length : y.length;

        double dotProduct = 0.0;
        for (int i = 0; i < minLength; i++) {
            dotProduct = dotProduct + (x[i] * y[i]);
        }
        return dotProduct;
    }

    public void shutDown() {
        System.out.println();
        System.out.println("Shutting down database ...");
        graphDb.shutdown();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    private static void deleteFileOrDirectory(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    deleteFileOrDirectory(child);
                }
            }
            file.delete();
        }
    }

    //    void removeData() {
//        try (Transaction tx = graphDb.beginTx()) {
//            currentNode.getSingleRelationship(RelTypes.SIMILAR, Direction.OUTGOING).delete();
//            currentNode.delete();
//            tx.success();
//        }
//    }

}
