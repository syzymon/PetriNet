package petrinet;

import java.util.*;

class StateTraversal<T> {
    private Set<Map<T, Integer>> visited;
    private Collection<Transition<T>> transitions;
    private Queue<State<T>> queue;

    StateTraversal(State<T> source, Collection<Transition<T>> transitions) {
        this.transitions = transitions;
        visited = new HashSet<>(List.of(source.getWeights()));
        queue = new LinkedList<>(List.of(source));
    }

    Set<Map<T, Integer>> computeReachableStates() {
        dfs();
        return visited;
    }

    private void dfs() {
        while(!queue.isEmpty()) {
            State<T> currentState = queue.poll();
            transitions.stream()
                    .filter(currentState::isTransitionAllowed)
                    .map(currentState::next)
                    .filter(state -> !visited.contains(state.getWeights()))
                    .peek(state -> visited.add(state.getWeights()))
                    .forEach(queue::add);
        }
    }
}
