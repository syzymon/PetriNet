package petrinet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class PetriNet<T> {

    private CriticalState<T> state;
    private FireManager<T> guard;

    private boolean fair;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        state = new CriticalState<>(initial);
        this.fair = fair;
        guard = new FireManager<>(state);
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        StateTraversal<T> dfs = new StateTraversal<>(new State<>(state), transitions);
        return dfs.computeReachableStates();
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        Semaphore mutex = guard.wantToEnter(transitions);
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            guard.removeInterruptedThreadCollection(transitions);
            throw e;
        }

        guard.enterSection();

        Transition<T> result = state.executeFire(transitions);

        guard.leaveSection(transitions);

        return result;
    }
}