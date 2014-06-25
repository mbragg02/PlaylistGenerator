package playlistGenerator.functionalInterfaces;

import java.io.File;
import java.util.Map;

public interface Parser {

    Map<String, String> parse(File file) throws Exception;
}
