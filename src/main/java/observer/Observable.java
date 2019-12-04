package observer;

import java.io.IOException;
import java.util.LinkedList;

public abstract class Observable {
    private LinkedList<Observer> list;

    public Observable(){
        list = new LinkedList<>();
    }

    public void notifyObs() throws IOException {
        for(Observer obs:list){
            obs.update();
        }
    }

    public void addObs(Observer obs){
        list.add(obs);
    }

    public void removeObs(Observer obs){
        list.remove(obs);
    }


}
