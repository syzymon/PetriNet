package multiplicator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.Collection;

public class Runner implements Runnable
{
    private Collection<Transition<Main.Place>> trans;
    private PetriNet<Main.Place> net;

    Runner(Collection<Transition<Main.Place>> trans, PetriNet<Main.Place> net)
    {
        this.trans = trans;
        this.net = net;
    }

    @Override
    public void run()
    {
        while(true){
            try
            {
                net.fire(trans);
            }
            catch (InterruptedException e){
                Thread t = Thread.currentThread();
                t.interrupt();
                break;
            }
        }
    }
}
