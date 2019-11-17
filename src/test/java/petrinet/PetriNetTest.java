package petrinet;

import org.junit.Test;
import static org.junit.Assert.*;


import java.util.*;

import static org.junit.Assert.*;

public class PetriNetTest {
    @Test
    public void reachable() {

        var initialWeights = Map.of(
                'a', 5,
                'b', 0,
                'z', 7,
                'd', 2,
                'e', 0
        );

        PetriNet<Character> net = new PetriNet<>(initialWeights, true);

        assertEquals(net.reachable(new ArrayList<>()), new HashSet<>(List.of(State.filterZeros(initialWeights))));
    }
}