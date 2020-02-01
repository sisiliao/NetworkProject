package socs.network.node;

public class ConnectionThread implements Runnable {
    Link link;
    Router router1;
    Router router2;

    public ConnectionThread(Link link, Router router1, Router router2) {
        this.link = link;
        this.router1 = router1;
        this.router2 = router2;
    }

    public void run() {

    }
}
