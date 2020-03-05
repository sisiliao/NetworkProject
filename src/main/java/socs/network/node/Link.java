package socs.network.node;

public class Link {

  RouterDescription rd1;
  RouterDescription rd2;
  short weight;

  public Link(RouterDescription r1, RouterDescription r2) {
    rd1 = r1;
    rd2 = r2;
  }

  public Link(RouterDescription r1, RouterDescription r2, short weight) {
    rd1 = r1;
    rd2 = r2;
    this.weight = weight;
  }
}
