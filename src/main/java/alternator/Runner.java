package alternator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.Collection;

public class Runner implements Runnable {
    private Collection<Transition<String>> enterTransitions;
    private Collection<Transition<String>> leaveTransitions;
    private PetriNet<String> net;

    Runner(PetriNet<String> net, Collection<Transition<String>> enterTransitions, Collection<Transition<String>> leaveTransitions) {
        this.net = net;
        this.enterTransitions = enterTransitions;
        this.leaveTransitions = leaveTransitions;
    }

    private void printLetterDot() {
        System.out.print(Thread.currentThread().getName());
        System.out.print('.');
    }

    @Override
    public void run() {
        while (true) {
            try {
                net.fire(enterTransitions);

                printLetterDot();

                net.fire(leaveTransitions);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
