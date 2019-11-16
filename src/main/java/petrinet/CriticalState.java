package petrinet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CriticalState<T> extends State<T> {
    public CriticalState(Map<T, Integer> initial) {
        super(initial);
        // TODO: concurrency or mutex when copying or nothing???
        weights = filterZeros(new ConcurrentHashMap<>(this.weights));
    }

    public CriticalState<T> next(Transition<T> transition) {
        performStateTransition(weights, transition);
        return this;
    }

    public Map<T, Integer> getWeights() {
        return new HashMap<>(weights);
    }
}
