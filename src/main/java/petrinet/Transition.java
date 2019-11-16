package petrinet;

import java.util.*;
import java.util.stream.Stream;


public class Transition<T> {

    private Map<T, Integer> input, output;
    private Collection<T> reset, inhibitor;

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        // TODO: synchronized?
        this.input = new HashMap<>(input);
        this.output = new HashMap<>(output);
        this.reset = new ArrayList<>(reset);
        this.inhibitor = new ArrayList<>(inhibitor);
    }

    public Stream<Map.Entry<T, Integer>> getInputEdgesEntryStream() {
        return input.entrySet().stream();
    }

    public Stream<Map.Entry<T, Integer>> getOutputEdgesEntryStream() {
        return output.entrySet().stream();
    }

    public Stream<T> getResetEdgesStream() {
        return reset.stream();
    }

    public Stream<T> getInhibitorsStream() {
        return inhibitor.stream();
    }
}
