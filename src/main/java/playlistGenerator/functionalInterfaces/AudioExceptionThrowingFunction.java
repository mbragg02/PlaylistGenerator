package playlistGenerator.functionalInterfaces;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

@FunctionalInterface
public interface AudioExceptionThrowingFunction{

    AudioInputStream apply(File input) throws UnsupportedAudioFileException, IOException;
}
