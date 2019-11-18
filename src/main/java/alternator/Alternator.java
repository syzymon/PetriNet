package alternator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Alternator {
    private PetriNet<String> net;

    private final String[] mainNodes;
    private final String[] bufferNodes;
    private final String mutexNode = "mutex";
    private final String sectionNode = "section";

    private final Map<String, Transition<String>> enterTransitions;
    private final Map<String, Transition<String>> leaveTransitions;

    private String getBufferNodeName(String nodeName) {
        return Arrays.stream(bufferNodes)
                .filter(str -> str.startsWith(nodeName))
                .findFirst()
                .orElseThrow(ArrayIndexOutOfBoundsException::new);
    }

    private Transition<String> createEnterTransition(String threadNode) {
        String bufferNode = getBufferNodeName(threadNode);
        Map<String, Integer> inputs = Map.of(
                mutexNode, 1,
                threadNode, 1,
                bufferNode, 1
        );

        Map<String, Integer> outputs = new HashMap<>(Map.of(
                sectionNode, 1
        ));

        Collection<String> zeros = new ArrayList<>();
        Collection<String> inhibitors = new ArrayList<>();

        return new Transition<>(inputs, zeros, inhibitors, outputs);
    }

    private Transition<String> createLeaveTransition(String threadNode) {
        String bufferNode = getBufferNodeName(threadNode);

        Map<String, Integer> inputs = Map.of(
                sectionNode, 1
        );

        Map<String, Integer> outputs = new HashMap<>(Map.of(
                mutexNode, 1,
                threadNode, 1
        ));

        // Add outputs to buffers not equal to current (make them not previously visited).
        Arrays.stream(bufferNodes)
                .filter(str -> !str.equals(bufferNode))
                .forEach(nodeName -> outputs.put(nodeName, 1));

        Collection<String> inhibitors = new ArrayList<>(List.of(threadNode));
        Collection<String> zeros = new ArrayList<>(Arrays.asList(bufferNodes));

        return new Transition<>(inputs, zeros, inhibitors, outputs);
    }

    Alternator() {
        mainNodes = new String[]{"one", "two", "three"};
        bufferNodes = new String[]{"oneNotLast", "twoNotLast", "threeNotLast"};

        Map<String, Integer> initialWeights = Map.of(
                "one", 1,
                "two", 1,
                "three", 1,
                "oneNotLast", 1,
                "twoNotLast", 1,
                "threeNotLast", 1,
                "mutex", 1,
                "section", 0
        );

        net = new PetriNet<>(initialWeights, true);
        enterTransitions = Arrays.stream(mainNodes)
                .map(nodeName -> new AbstractMap.SimpleEntry<>(nodeName, createEnterTransition(nodeName)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        leaveTransitions = Arrays.stream(mainNodes)
                .map(nodeName -> new AbstractMap.SimpleEntry<>(nodeName, createLeaveTransition(nodeName)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    void test() {
        var allTransitions = Stream.concat(enterTransitions.values().stream(), leaveTransitions.values().stream()).collect(Collectors.toList());
        var reachable = net.reachable(allTransitions);

        System.err.println(reachable.size());

        System.err.println("Safety: " + reachable.stream().allMatch(weights -> weights.getOrDefault(sectionNode, 0) <= 1));

        Map<String, String> shortAliases = Map.of(
                "one", "A",
                "two", "B",
                "three", "C"
        );

        var threadList = Arrays.stream(mainNodes).map(nodeName -> new Thread(new Runner(
                net,
                new ArrayList<>(List.of(enterTransitions.get(nodeName))),
                new ArrayList<>(List.of(leaveTransitions.get(nodeName)))
        ), shortAliases.get(nodeName))).collect(Collectors.toList());

        threadList.forEach(Thread::start);

        try {
            Thread.sleep(30000);
            threadList.forEach(Thread::interrupt);
        } catch (InterruptedException ex) {
            System.out.println("Interrupted");
        }
    }
}
