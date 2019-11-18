package petrinet;

import java.util.*;
import java.util.concurrent.Semaphore;

class FireManager<T> {

    private List<AbstractMap.SimpleEntry<Collection<Transition<T>>, Semaphore>> queue;
    private final CriticalState<T> state;
    private Collection<Transition<T>> currentThread;

    FireManager(CriticalState<T> state) {
        queue = Collections.synchronizedList(new ArrayList<>());
        currentThread = null;
        this.state = state;
    }

    synchronized Semaphore wantToEnter(Collection<Transition<T>> threadCollection) {
        int initialMutexValue = (currentThread != null) || !state.isAnyTransitionAllowed(threadCollection) ? 0 : 1;
        Semaphore mutex = new Semaphore(initialMutexValue, true);
        if (initialMutexValue == 0)
            queue.add(new AbstractMap.SimpleEntry<>(threadCollection, mutex));
        else
            currentThread = threadCollection;

        return mutex;
    }

    synchronized boolean isCurrentThread(Collection<Transition<T>> threadCollection) {
        return threadCollection == currentThread;
    }

    synchronized void invalidateWaiting(Collection<Transition<T>> threadCollection) {
        var entry = queue.stream()
                .filter(e -> e.getKey() == threadCollection)
                .findFirst().orElseThrow(IndexOutOfBoundsException::new);

        queue.remove(entry);
    }

    synchronized void leaveSection() {
        getFirstAllowedThreadCollection().ifPresentOrElse(this::wakeUpWaitingThread, () -> currentThread = null);
    }

    private void wakeUpWaitingThread(Collection<Transition<T>> threadCollection) {
        var entry = queue.stream()
                .filter(e -> e.getKey() == threadCollection)
                .findFirst().orElseThrow(IndexOutOfBoundsException::new);

        queue.remove(entry);

        currentThread = threadCollection;
        entry.getValue().release();
    }

    private Optional<Collection<Transition<T>>> getFirstAllowedThreadCollection() {
        return queue.stream()
                .filter(e -> state.isAnyTransitionAllowed(e.getKey()))
                .map(AbstractMap.SimpleEntry::getKey)
                .findFirst();
    }

}
