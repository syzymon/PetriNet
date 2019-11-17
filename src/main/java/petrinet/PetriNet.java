package petrinet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class PetriNet<T> {

    private CriticalState<T> state;
    private Semaphore mutex;

    private boolean fair;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        state = new CriticalState<>(initial);
        this.fair = fair;
        mutex = new Semaphore(1, true);
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        StateTraversal<T> dfs = new StateTraversal<>(new State<>(state), transitions);
        return dfs.computeReachableStates();
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
        while (true) {
            mutex.acquire();
            var allowedTransition = transitions.stream()
                    .filter(state::isTransitionAllowed)
                    .findFirst();

            if(allowedTransition.isPresent()) {
                var temporaryState = new CriticalState<T>(state);

                temporaryState = temporaryState.next(allowedTransition.get());

                state.setWeightsAfterTransition(temporaryState);

                mutex.release();
                return allowedTransition.get();
            }
        }
    }
}