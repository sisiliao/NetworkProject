package socs.network.node;

public class RouterDescription {

  //used to socket communication
  String processIPAddress;
  short processPortNumber;
  //used to identify the router in the simulated network space
  String simulatedIPAddress;
  //status of the router
  RouterStatus status;

  public String getProcessIPAddress() {
    return processIPAddress;
  }

  public short getProcessPortNumber() {
    return processPortNumber;
  }

  public String getSimulatedIPAddress() {
    return simulatedIPAddress;
  }

  public RouterStatus getStatus() {
    return status;
  }
}
