package petrinet;

import java.util.*;
import java.util.concurrent.Semaphore;

public class FireManager<T> {

    private List<Collection<Transition<T>>> queue;
    private Map<Collection<Transition<T>>, Semaphore> mutices;
    private final CriticalState<T> state;
    private boolean sectionOccupied;

    public FireManager(CriticalState<T> state) {
        queue = Collections.synchronizedList(new ArrayList<>());
        mutices = new HashMap<>();
        sectionOccupied = false;
        this.state = state;
    }

    public synchronized Semaphore wantToEnter(Collection<Transition<T>> transitions) {
        int initialMutexValue = sectionOccupied || !state.isAnyTransitionAllowed(transitions) ? 0 : 1;
        Semaphore mutex = new Semaphore(initialMutexValue, true);
        if(initialMutexValue == 0)
            queue.add(transitions);
        mutices.put(transitions, mutex);
        return mutex;
    }

    public synchronized void enterSection() {
        assert !sectionOccupied;
        sectionOccupied = true;
    }

    public synchronized void leaveSection(Collection<Transition<T>> transitions) {
        mutices.remove(transitions);

        assert sectionOccupied;
        sectionOccupied = false;

        getFirstAllowedThreadCollection().ifPresent(this::wakeUpWaitingThread);
        //wakeup some mutex!
    }

    private void wakeUpWaitingThread(Collection<Transition<T>> threadCollection) {
        queue.remove(threadCollection);
        mutices.get(threadCollection).release();
    }

    private Optional<Collection<Transition<T>>> getFirstAllowedThreadCollection() {
        return queue.stream()
                .filter(state::isAnyTransitionAllowed)
                .findFirst();
    }

    public void removeInterruptedThreadCollection(Collection<Transition<T>> threadCollection) {
        queue.remove(threadCollection);
        mutices.remove(threadCollection);
    }
}
