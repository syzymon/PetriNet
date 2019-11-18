package multiplicator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Main {
    public enum Place {
        A, B,
        middleLeft, middleRight,
        bottomAcc, result
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt(), b = sc.nextInt();

        var placesInitialState = Map.of(
                Place.A, a,
                Place.B, b,
                Place.middleLeft, 1,
                Place.middleRight, 0,
                Place.bottomAcc, 0,
                Place.result, 0
        );

        Transition<Place> bottom = new Transition<>(
                Map.of(
                        Place.B, 1,
                        Place.middleLeft, 1
                ),
                Collections.emptyList(),
                List.of(Place.bottomAcc),
                Map.of(Place.middleRight, 1)
        );

        Transition<Place> top = new Transition<>(
                Map.of(Place.middleRight, 1),
                Collections.emptyList(),
                List.of(Place.A),
                Map.of(Place.middleLeft, 1)
        );

        Transition<Place> left = new Transition<>(
                Map.of(
                        Place.bottomAcc, 1,
                        Place.middleLeft, 1
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Map.of(
                        Place.A, 1,
                        Place.middleLeft, 1
                )
        );

        Transition<Place> right = new Transition<>(
                Map.of(
                        Place.A, 1,
                        Place.middleRight, 1
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                Map.of(
                        Place.result, 1,
                        Place.bottomAcc, 1,
                        Place.middleRight, 1
                )
        );

        Transition<Place> awaited = new Transition<>(
                Collections.emptyMap(),
                Collections.emptyList(),
                List.of(Place.B, Place.bottomAcc, Place.middleRight),
                Collections.emptyMap()
        );

        List<Transition<Place>>  transitions = new ArrayList<>(List.of(bottom, top, left, right));

        PetriNet<Place> net = new PetriNet<>(placesInitialState, true);

        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < 4; ++i) {
            Thread t = new Thread(new Runner(transitions, net));
            t.start();
            threads.add(t);
        }

        try {
            net.fire(List.of(awaited));
            System.out.println(net.getPlaceValue(Place.result));
            threads.forEach(Thread::interrupt);
        } catch(InterruptedException e) {
            System.out.println("Main thread interrupted");
        }
    }
}
