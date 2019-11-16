package petrinet;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class StateTest {

    @Test
    public void filterZeros() {
        Map<Character, Integer> m = Map.of(
                'a', 5,
                'b', 0,
                'z', 7,
                'd', 2,
                'e', 0
        );

        assertEquals(State.filterZeros(m), Map.of(
                'a', 5,
                'z', 7,
                'd', 2
        ));
    }
}