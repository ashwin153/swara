package com.swara.music.io;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
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

    private static final Predicate<MidiEvent> MIDI_KEY = evt ->
        evt.getMessage() instanceof MetaMessage && ((MetaMessage) evt.getMessage()).getType() == 0x59;

    private static final Predicate<MidiEvent> MIDI_BPM = evt ->
        evt.getMessage() instanceof MetaMessage && ((MetaMessage) evt.getMessage()).getType() == 0x51;

    private static final Predicate<MidiEvent> MIDI_TIME = evt ->
        evt.getMessage() instanceof MetaMessage && ((MetaMessage) evt.getMessage()).getType() == 0x58;

    private static final Predicate<MidiEvent> MIDI_PROG = evt ->
        evt.getMessage() instanceof ShortMessage && ((ShortMessage) evt.getMessage()).getCommand() == 0xC0;

    private static final Predicate<MidiEvent> MIDI_NOTE = evt -> evt.getMessage() instanceof ShortMessage && (
        ((ShortMessage) evt.getMessage()).getCommand() == ShortMessage.NOTE_ON ||
        ((ShortMessage) evt.getMessage()).getCommand() == ShortMessage.NOTE_OFF
    );

    @Override
    public Song read(InputStream in) throws Exception {
        // Read a MIDI sequence from the input stream and flatten all tracks.
        final Sequence sequence = MidiSystem.getSequence(in);
        Preconditions.checkArgument(sequence.getDivisionType() == Sequence.PPQ);
        final List<MidiEvent> events = Arrays.stream(sequence.getTracks())
            .flatMap(track -> IntStream.range(0, track.size()).mapToObj(track::get))
            .sorted((a, b) -> Long.compare(a.getTick(), b.getTick()))
            .collect(Collectors.toList());

        // Split the events whenever a new fragment begins.
        final TreeMap<Long, List<MidiEvent>> headers = events.stream()
            .filter(MIDI_KEY.or(MIDI_BPM).or(MIDI_TIME).or(MIDI_PROG))
            .collect(Collectors.groupingBy(MidiEvent::getTick, TreeMap::new, Collectors.toList()));

        // Iteratively construct a song.
        final Song.Builder song = new Song.Builder();
        final Key.Builder key = new Key.Builder();
        final Tempo.Builder tempo = new Tempo.Builder();
        final Map<Integer, Integer> programs = new HashMap<>();

        headers.keySet().forEach(start -> {
            final Fragment.Builder fragment = new Fragment.Builder();
            final List<MidiEvent> header = headers.get(start);

            // Read key information.
            header.stream().filter(MIDI_KEY).findAny()
                .map(i -> ((MetaMessage) i.getMessage()).getData())
                .ifPresent(i -> {
                    key.withSignature(i[0]);
                    key.withType(i[1]);
                });

            // Read tempo information.
            header.stream().filter(MIDI_BPM).findAny()
                .map(i -> ((MetaMessage) i.getMessage()).getData())
                .map(i -> 60_000_000 / ((i[0] << 16) + (i[1] << 8) + i[2]))
                .ifPresent(tempo::withBpm);

            header.stream().filter(MIDI_TIME).findAny()
                .map(i -> ((MetaMessage) i.getMessage()).getData())
                .map(i -> new Fraction(i[0], (int) Math.pow(2, i[1])))
                .ifPresent(tempo::withSignature);

            // Read program change information.
            header.stream().filter(MIDI_PROG)
                .map(i -> (ShortMessage) i.getMessage())
                .forEach(msg -> programs.put(msg.getChannel(), msg.getData1()));

            // Parse the fragment channel information.
            events.stream().filter(MIDI_NOTE.and(i -> headers.floorKey(i.getTick()).equals(start)))
                .collect(Collectors.groupingBy(i -> ((ShortMessage) i.getMessage()).getChannel()))
                .forEach((channel, list) -> {
                    final Phrase.Builder phrase = new Phrase.Builder();
                    final Map<Integer, ShortMessage> notes = new HashMap<>();

                    // Group note on and note off events by tick.
                    final TreeMap<Long, List<MidiEvent>> ticks = list.stream().collect(
                        Collectors.groupingBy(MidiEvent::getTick, TreeMap::new, Collectors.toList())
                    );

                    ticks.forEach((tick, group) -> {
                        // Articulate all notes (if any) in the current chord.
                        final Long last = ticks.lowerKey(tick);
                        final long diff = tick - (last == null ? start : last);

                        if (diff > 0) {
                            phrase.withChord(new Chord.Builder()
                                .withDuration(new Fraction(diff / (4.0 * sequence.getResolution())))
                                .withNotes(notes.values().stream()
                                    .map(i -> new Note.Builder()
                                        .withPitch(i.getData1() % 12)
                                        .withOctave(i.getData1() / 12)
                                        .build())
                                    .collect(Collectors.toSet()))
                                .withVolume((int) notes.values().stream()
                                    .mapToInt(ShortMessage::getData2)
                                    .average()
                                    .orElse(0))
                                .build());
                        }

                        // Add all new events and remove all old events.
                        for (MidiEvent event : group) {
                            final ShortMessage msg = (ShortMessage) event.getMessage();
                            notes.merge(msg.getData1(), msg, (v1, v2) -> null);
                        }
                    });

                    fragment.withPhrase(channel, phrase
                        .withProgram(programs.get(channel))
                        .build());
                });

            // Build the fragment and add it to the song.
            song.withFragment(fragment
                .withKey(key.build())
                .withTempo(tempo.build())
                .build());
        });

        // Assemble and return the parsed song.
        return song.build();
    }

}
