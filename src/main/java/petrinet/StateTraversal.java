package petrinet;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StateTraversal<T> {
    private Set<State<T>> visited;
    private Collection<Transition<T>> transitions;

    public StateTraversal(State<T> source, Collection<Transition<T>> transitions) {
        this.transitions = transitions;
        visited = new HashSet<>(List.of(source));
    }

    public Set<Map<T, Integer>> computeReachableStates() {
        dfs(visited.iterator().next());
        return visited.stream()
                .map(State::getWeights)
                .collect(Collectors.toSet());
    }

    private void dfs(State<T> currentState) {
        transitions.stream()
                .filter(currentState::isTransitionAllowed)
                .map(currentState::next)
                .filter(Predicate.not(visited::contains))
                .peek(visited::add)
                .forEach(this::dfs);
    }
}
