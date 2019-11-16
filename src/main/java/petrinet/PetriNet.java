package petrinet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PetriNet<T> {

    private CriticalState<T> state;
    private boolean fair;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        state = new CriticalState<>(initial);
        this.fair = fair;
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        // TODO: surround by mutex!!!
        Dfs<T> dfs = new Dfs<>(new State<>(state), transitions);
        return dfs.getReachableStates();
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        return null;
    }
}