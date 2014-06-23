package playlistGenerator.tools;

import java.io.File;

@FunctionalInterface
public interface Parser<T> {

    T parse(File file);
}
