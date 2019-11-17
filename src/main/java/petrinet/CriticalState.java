package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CriticalState<T> extends State<T> {
    public CriticalState(Map<T, Integer> initial) {
        super(initial);
        weights = filterZeros(new HashMap<>(this.weights));
    }

    public CriticalState(CriticalState<T> toCopy) {
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


    public Transition<T> executeFire(Collection<Transition<T>> transitions) {
        var temporaryState = new CriticalState<>(weights);

        Transition<T> allowedTransition = transitions.stream()
                .filter(this::isTransitionAllowed)
                .findFirst()
                .orElseThrow(AssertionError::new);

        temporaryState = temporaryState.next(allowedTransition);

        setWeightsAfterTransition(temporaryState);

        return allowedTransition;
    }
}
