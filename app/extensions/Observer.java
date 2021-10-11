package extensions;

import akka.actor.ActorRef;

public interface Observer {

    public void update(ActorRef out);
}
