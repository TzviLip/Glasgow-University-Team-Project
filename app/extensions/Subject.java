package extensions;

import akka.actor.ActorRef;

public interface Subject {

    public void registerObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObserver(ActorRef out);

}
