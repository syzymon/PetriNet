package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class State<T> {
    Map<T, Integer> weights;

    State(Map<T, Integer> initial) {
        weights = initial;
    }

    State(State<T> toCopy) {
        this(toCopy.weights);
    }

    static <T> Map<T, Integer> filterZeros(Map<T, Integer> initial) {
        return initial.entrySet().stream()
                .filter(map -> map.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    boolean isTransitionAllowed(Transition<T> transition) {
        return (transition.getInputEdgesEntryStream()
                .allMatch(edgeEntry -> getPlaceWeight(edgeEntry.getKey()) >= edgeEntry.getValue())

                &&

                transition.getInhibitorsStream()
                        .allMatch(forbiddenPlace -> getPlaceWeight(forbiddenPlace) == 0));
    }

    boolean isAnyTransitionAllowed(Collection<Transition<T>> transitions) {
        return transitions.stream().anyMatch(this::isTransitionAllowed);
    }

    void performStateTransition(Map<T, Integer> stateMap, Transition<T> transition) {
        assert isTransitionAllowed(transition);
        transition.getInputEdgesEntryStream()
                .forEach(edgeEntry ->
                        stateMap.put(edgeEntry.getKey(), stateMap.get(edgeEntry.getKey()) - edgeEntry.getValue())
                );

        transition.getResetEdgesStream()
                .forEach(stateMap::remove);

        transition.getOutputEdgesEntryStream().
                forEach(edgeEntry ->
                        stateMap.put(
                                edgeEntry.getKey(),
                                stateMap.getOrDefault(edgeEntry.getKey(), 0) + edgeEntry.getValue()
                        )
                );
    }


    public State<T> next(Transition<T> transition) {
        Map<T, Integer> result = new HashMap<>(weights);
        performStateTransition(result, transition);
        return new State<>(filterZeros(result));
    }

    private int getPlaceWeight(T place) {
        return weights.getOrDefault(place, 0);
    }

    public Map<T, Integer> getWeights() {
        return weights;
    }
}
