package main.java.org.music;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.List;

public class synth {

    public static final int NOTE_LENGTH = 500;

    public static void main(String[] args) {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel[] mc = synthesizer.getChannels();
            Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();

            synthesizer.loadInstrument(instruments[90]);

            List<Integer> d = Arrays.asList(62, 66, 69);
            List<Integer> a = Arrays.asList(57, 61, 64);
            List<Integer> g = Arrays.asList(55, 59, 62);
            List<List<Integer>> chords = Arrays.asList(d, d, a, g, d, d);

            List<Integer> duration = Arrays.asList(1, 3, 2, 2, 1, 3);
            int dur = 0;

            int repeat = 5;
            while (repeat > 0){
                for (List<Integer> notes : chords) {
                    for (Integer note : notes) {
                        mc[0].noteOn(note, 600);
                    }
                    Thread.sleep(NOTE_LENGTH * duration.get(dur));
                    for (Integer note : notes) {
                        mc[0].noteOff(note, 600);
                    }
                    dur++;
                }
                dur = 0;
                repeat--;
            }

            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();

            // hook up sequencer to synth
            Transmitter transmitter = sequencer.getTransmitter();
            Receiver receiver = synthesizer.getReceiver();
            transmitter.setReceiver(receiver);

            // create sequence to play
            Sequence sequence = new Sequence(Sequence.PPQ, 16);
            Track track = sequence.createTrack();

            // register instrument
            int instrument = 0;
            ShortMessage sm = new ShortMessage();
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);
            track.add(new MidiEvent(sm, 0));

            // add notes to play
            int programTime = 0;
            for (int i=0; i < 20; i++) {

                ShortMessage keyOn = new ShortMessage();
                keyOn.setMessage(ShortMessage.NOTE_ON, 0, 0, 64);
                ShortMessage keyOff = new ShortMessage();
                keyOff.setMessage(ShortMessage.NOTE_OFF, 0, 0, 64);

                track.add(new MidiEvent(keyOn, programTime + 2));
//                track.add(new MidiEvent(keyOff, programTime + 14));

                programTime += 16;
            }

            // play sequence
            sequencer.setSequence(sequence);
            sequencer.setTempoInBPM(120);
            sequencer.start();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
