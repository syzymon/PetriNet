package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CriticalState<T> extends State<T> {
    public CriticalState(Map<T, Integer> initial, boolean concurrency) {
        super(initial);
        var map = concurrency ? new ConcurrentHashMap<>(weights) : new HashMap<>(weights);
        weights = filterZeros(map);
    }

    public void setWeightsAfterTransition(CriticalState<T> temporaryState) {
        this.weights = temporaryState.weights;
    }

    @Override
    public Map<T, Integer> getWeights() {
        return new HashMap<>(weights);
    }

    @Override
    public CriticalState<T> next(Transition<T> transition) {
        performStateTransition(weights, transition);
        weights = filterZeros(weights);
        return this;
    }


    public Transition<T> executeFire(Collection<Transition<T>> transitions) {
        var temporaryState = new CriticalState<>(weights, false);

        Transition<T> allowedTransition = transitions.stream()
                .filter(this::isTransitionAllowed)
                .findFirst()
                .orElseThrow(AssertionError::new);

        temporaryState = temporaryState.next(allowedTransition);

        setWeightsAfterTransition(temporaryState);

        return allowedTransition;
    }
}
