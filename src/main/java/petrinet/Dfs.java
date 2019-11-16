package petrinet;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dfs<T> {
    private Set<State<T>> visited;
    private Collection<Transition<T>> transitions;

    public Dfs(State<T> source, Collection<Transition<T>> transitions) {
        this.transitions = transitions;
        visited = new HashSet<>(List.of(source));
    }

    public Set<Map<T, Integer>> getReachableStates() {
        dfs(visited.iterator().next());
        return visited.stream().map(State::getWeights).collect(Collectors.toSet());
    }

/*    private Stream<Dfs<T>> getStates(State<T> currentState) {
        return Stream.concat(
                Stream.of(this),
                transitions.stream()
                        .filter(currentState::isTransitionAllowed).
                        map(currentState::next)
                        .filter(Predicate.not(visited::contains)).
                        peek(state -> visited.add(state))
                        .flatMap(this::getStates)
        );
    }*/

    private void dfs(State<T> currentState) {
        transitions.stream()
                .filter(currentState::isTransitionAllowed).
                map(currentState::next)
                .filter(Predicate.not(visited::contains)).
                peek(state -> visited.add(state)).forEach(this::dfs);
    }
}
