package petrinet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CriticalState<T> extends State<T> {
    public CriticalState(Map<T, Integer> initial) {
        super(initial);
        weights = filterZeros(new HashMap<>(this.weights));
    }

    public CriticalState(CriticalState<T> toCopy) {
        // TODO: concurrency or mutex when copying or nothing???
        super(toCopy.weights);
    }

    public void setWeightsAfterTransition(CriticalState<T> temporaryState) {
        this.weights = temporaryState.weights;
    }

    public Map<T, Integer> getWeights() {
        return new HashMap<>(weights);
    }

    public CriticalState<T> next(Transition<T> transition) {
        performStateTransition(weights, transition);
        weights = filterZeros(weights);
        return this;
    }
}
