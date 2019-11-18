package petrinet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class PetriNet<T> {

    private final CriticalState<T> state;
    private final FireManager<T> guard;

    @SuppressWarnings("unused")
    public PetriNet(Map<T, Integer> initial, boolean fair) {
        state = new CriticalState<>(initial, true);
        guard = new FireManager<>(state);
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        StateTraversal<T> dfs = new StateTraversal<>(new State<>(state), transitions);
        return dfs.computeReachableStates();
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        transitions = new ArrayList<>(transitions);
        Semaphore mutex = guard.wantToEnter(transitions);

        try {
            mutex.acquire();

            Transition<T> result = state.executeFire(transitions);

            guard.leaveSection();

            return result;
        } catch (InterruptedException e) {
            if (!guard.isCurrentThread(transitions))
                guard.invalidateWaiting(transitions);

            guard.leaveSection();
            throw e;
        }
    }
}