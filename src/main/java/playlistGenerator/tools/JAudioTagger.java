package playlistGenerator.tools;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import playlistGenerator.functionalInterfaces.Parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JAudioTagger implements Parser {

    private final Map<String, String> tags;
    private final List<FieldKey> tagKeys;

    public JAudioTagger(List<FieldKey> tagKeys) {
        this.tags = new HashMap<>();
        this.tagKeys = tagKeys;
    }

    @Override
    public Map<String, String> parse(File file) throws ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, TagException {

        AudioFile audioFile = AudioFileIO.read(file);
        
        Tag id3Tag = audioFile.getTag();
        Mp4Tag mp4Tag = (Mp4Tag) audioFile.getTag();

        for(FieldKey tag : tagKeys) {
            tags.put(tag.toString(), nonEmpty(mp4Tag.getFirst(tag), id3Tag.getFirst(tag)));
        }

        return tags;
    }

    private String nonEmpty(String s1, String s2) {
        if(s1.isEmpty()) {
            return s2;
        }
        return s1;
    }
}
