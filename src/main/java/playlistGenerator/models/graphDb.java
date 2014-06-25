package playlistGenerator.models;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.tooling.GlobalGraphOperations;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class graphDb {

    private static final String DB_PATH = "src/main/resources/neo4j-test-db";
    public static final String INDEX_FIELD = "filename";

    private GraphDatabaseService graphDb;
    private Label label;
    private ExecutionEngine engine;

    private static enum RelTypes implements RelationshipType {
        SIMILAR
    }

    public graphDb() {
        init();
    }

    private void init() {
        //deleteFileOrDirectory(new File(DB_PATH));
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

        registerShutdownHook(graphDb);

        label = () -> "Track";

        engine = new ExecutionEngine(graphDb);

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

            currentNode.setProperty("id", track.getId());
            currentNode.setProperty("features", track.getFeatureVector());
            currentNode.setProperty("features_length", track.getFeatureVectorLength());
            currentNode.setProperty("filename", track.getFilename());
            currentNode.setProperty("filepath", track.getFilepath());

            for(Map.Entry<String, String> tag : track.getTags().entrySet()) {
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

    public boolean containsFileName(String filename) {
        try (Transaction ignored = graphDb.beginTx()) {

            try (ResourceIterator<Node> tracks =
                         graphDb.findNodesByLabelAndProperty(label, "filename", filename).iterator()) {
                return tracks.hasNext();
            }

        }
    }

    public ExecutionResult getNearest(String filename, int limit) {

        String query = "MATCH (t1:label {filename:{filename}})-[s:SIMILAR]-(t2:label) " +
                "WITH t2, s.cosine AS sim " +
                "ORDER BY sim DESC " +
                "LIMIT {l} " +
                "RETURN t2.filename AS Neighbor, sim AS Similarity";

        Map<String, Object> params = new HashMap<>();
        params.put("filename", filename);
        params.put("l", limit);

        ExecutionResult result;
        try (Transaction ignored = graphDb.beginTx()) {
            result = engine.execute(query, params);
        }

        return result;
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
