package petrinet;

import java.util.*;
import java.util.concurrent.Semaphore;

public class FireManager<T> {

    private List<Collection<Transition<T>>> queue;
    private Map<Collection<Transition<T>>, Semaphore> mutices;
    private final CriticalState<T> state;
    //    private boolean sectionOccupied;
    private Collection<Transition<T>> currentThread;

    public FireManager(CriticalState<T> state) {
        queue = Collections.synchronizedList(new ArrayList<>());
        mutices = new HashMap<>();
//        sectionOccupied = false;
        currentThread = null;
        this.state = state;
    }

    public synchronized Semaphore wantToEnter(Collection<Transition<T>> threadCollection) {
        int initialMutexValue = (currentThread != null) || !state.isAnyTransitionAllowed(threadCollection) ? 0 : 1;
        Semaphore mutex = new Semaphore(initialMutexValue, true);
        if (initialMutexValue == 0)
            queue.add(threadCollection);
        else
            currentThread = threadCollection;
        mutices.put(threadCollection, mutex);
        return mutex;
    }

    public synchronized void leaveSection(Collection<Transition<T>> threadCollection) {
        mutices.remove(threadCollection);

        assert currentThread != null;

        getFirstAllowedThreadCollection().ifPresentOrElse(this::wakeUpWaitingThread, () -> {
            currentThread = null;
        });
    }

    public synchronized boolean isCurrentThread(Collection<Transition<T>> threadCollection) {
        return threadCollection == currentThread;
    }

    public synchronized void invalidateWaiting(Collection<Transition<T>> threadCollection) {
        queue.remove(threadCollection);
    }

    private void wakeUpWaitingThread(Collection<Transition<T>> threadCollection) {
        queue.remove(threadCollection);
        currentThread = threadCollection;
        mutices.get(threadCollection).release();
    }

    private Optional<Collection<Transition<T>>> getFirstAllowedThreadCollection() {
        return queue.stream()
                .filter(state::isAnyTransitionAllowed)
                .findFirst();
    }

}
