package com.swara.music.io;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

import com.google.common.base.Preconditions;
import com.swara.music.data.Chord;
import com.swara.music.data.Fragment;
import com.swara.music.data.Key;
import com.swara.music.data.Note;
import com.swara.music.data.Phrase;
import com.swara.music.data.Song;
import com.swara.music.data.Tempo;

import org.apache.commons.math3.fraction.Fraction;

/**
 * Reads a {@link Song} from a MIDI {@link javax.sound.midi.Sequence}.
 */
public class MidiSongReader implements SongReader {

    @Override
    public Song read(InputStream in) throws Exception {
        // Read a MIDI sequence from the input stream.
        final Sequence seq = MidiSystem.getSequence(in);
        Preconditions.checkArgument(seq.getDivisionType() == Sequence.PPQ);

        // Group all the relevant MIDI meta events by fragment (key = 0x02, tempo = 0x03, time = 0x04).
        final TreeMap<Long, List<MidiEvent>> fragments = Arrays.stream(seq.getTracks())
            .flatMap(track -> IntStream.range(0, track.size()).mapToObj(track::get))
            .filter(i -> i.getMessage() instanceof MetaMessage)
            .filter(i -> {
                final int type = ((MetaMessage) i.getMessage()).getType();
                return type == 0x51 || type == 0x58 || type == 0x59;
            }).collect(Collectors.groupingBy(MidiEvent::getTick, TreeMap::new, Collectors.toList()));

        // Add all the MIDI channel events to the correct group.
        Arrays.stream(seq.getTracks())
            .flatMap(track -> IntStream.range(0, track.size()).mapToObj(track::get))
            .filter(i -> i.getMessage() instanceof ShortMessage)
            .forEach(i -> fragments.floorEntry(i.getTick()).getValue().add(i));

        // Iteratively construct a song from the specified fragments. By default, the key, tempo,
        // and program mappings are the same as the previous fragment.
        final Song.Builder song = new Song.Builder();
        final Key.Builder key = new Key.Builder();
        final Tempo.Builder tempo = new Tempo.Builder();
        final Map<Integer, Integer> programs = new HashMap<>();

        fragments.entrySet().forEach(entry -> {
            final long start = entry.getKey();
            final List<MidiEvent> list = entry.getValue();
            final Fragment.Builder fragment = new Fragment.Builder();

            // Extract key and tempo information; if the information is not present, then it is
            // assumed to be the same as the previous fragment (or default values).
            for (int i = 0; i < list.size() && list.get(i).getMessage() instanceof MetaMessage; i++) {
                final MetaMessage msg = (MetaMessage) list.get(i).getMessage();
                if (msg.getType() == 0x59) {
                    key.withSignature(msg.getData()[0]);
                    key.withType(msg.getData()[1]);
                } else if (msg.getType() == 0x51) {
                    final int mspqn = (msg.getData()[0] << 16) + (msg.getData()[1] << 8) + (msg.getData()[2]);
                    tempo.withBpm(60_000_000 / mspqn);
                } else if (msg.getType() == 0x58) {
                    tempo.withSignature(msg.getData()[0], (int) Math.pow(2, msg.getData()[1]));
                }
            }

            // Add the key and tempo information to the fragment.
            fragment.withKey(key.build());
            fragment.withTempo(tempo.build());

            // Separate the MIDI events for the fragment by channel.
            final Map<Integer, List<MidiEvent>> channels = list.stream()
                .filter(i -> i.getMessage() instanceof ShortMessage)
                .collect(Collectors.groupingBy(i -> ((ShortMessage) i.getMessage()).getChannel()));

            for (Integer channel : channels.keySet()) {
                // Group events by tick.
                final Phrase.Builder phrase = new Phrase.Builder();
                final Map<Integer, Integer> keys = new HashMap<>();
                final TreeMap<Long, List<MidiEvent>> ticks = channels.get(channel).stream()
                    .collect(Collectors.groupingBy(MidiEvent::getTick, TreeMap::new, Collectors.toList()));

                for (Long tick : ticks.keySet()) {
                    // Articulate all notes (if any) in the current chord.
                    if (tick > start) {
                        final Long last = ticks.lowerKey(tick);
                        final long diff = tick - (last == null ? start : last);

                        phrase.withChord(new Chord.Builder()
                            .withDuration(new Fraction(diff / (4.0 * seq.getResolution())))
                            .withNotes(keys.entrySet().stream()
                                .map(e -> new Note.Builder()
                                    .withPitch(e.getKey() % 12)
                                    .withOctave(e.getKey() / 12)
                                    .withVolume(e.getValue())
                                    .build())
                                .collect(Collectors.toSet()))
                            .build()
                        );
                    }

                    // Add any new notes and remove any old notes.
                    for (MidiEvent event : ticks.get(tick)) {
                        final ShortMessage msg = (ShortMessage) event.getMessage();
                        if (msg.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                            programs.put(msg.getChannel(), msg.getData1());
                        } else if (msg.getCommand() == ShortMessage.NOTE_ON && msg.getData2() > 0) {
                            keys.put(msg.getData1(), msg.getData2());
                        } else if (msg.getCommand() == ShortMessage.NOTE_OFF || (msg.getCommand() == ShortMessage.NOTE_ON && msg.getData2() == 0)) {
                            keys.remove(msg.getData1());
                        }
                    }
                }

                // Add the program to the phrase.
                phrase.withProgram(programs.get(channel));

                // Add the phrase to the fragment.
                fragment.withPhrase(channel, phrase.build());
            }

            // Add the fragment to the song.
            song.withFragment(fragment.build());
        });

        return song.build();
    }

}
