package modell;

import java.util.ArrayList;
import java.util.List;

public class Dijkstraalgo {

  public static class Node {
    public Node(String name) {
      this.name = name;
    }

    public List<Connection> getCosts() {
      return costs;
    }

    String name;
    List<Connection> costs = new ArrayList<>();
  }

  public static class Connection {
    public Connection(Node node, int cost) {
      this.node = node;
      this.cost = cost;
    }

    Node node;
    int cost;
  }

  public void addConnection(Node n, Node z, int cost){
    n.getCosts().add(new Connection(z, cost));
  }

  public void searchConnection(Node n, Node z){
    n.getCosts().add(new Connection(z, cost));
  }

  public void deleteConnection(Node n, Node z){
    //if it exists
    n.getCosts().add(new Connection(z, cost));
  }

  public void changeCostOfConnection(Node n, Node z){
    //if it exists
    n.getCosts().add(new Connection(z, cost));
  }

  public static void main(String[] args) {
    Dijkstraalgo path = new Dijkstraalgo();


  }
}
