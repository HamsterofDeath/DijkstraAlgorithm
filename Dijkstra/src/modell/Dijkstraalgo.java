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

    public void setCost(int cost) {
      this.cost = cost;
    }
  }

  public void addConnection(Node n, Node z, int cost) {
    n.getCosts().add(new Connection(z, cost));
  }

  public Connection searchConnection(Node n, Node z) {
    if (n.costs == null) {
      return null;
    } else {
      for (Connection s : n.getCosts()) {
        if (s.node.equals(z)) {
          return s;
        } else {
          return null;
        }
      }
    }
  }

  public void deleteConnection(Node n, Node z) {
    Connection c = searchConnection(n, z);
    if (c != null) {
      n.getCosts().remove(c);
    }
  }

  public void changeCostOfConnection(Node n, Node z, int newCost) {
    Connection c = searchConnection(n, z);
    if (c != null) {
      for (Connection s : n.getCosts()) {
        if (s.equals(c)) {
          s.setCost(newCost);
        }
      }
    }
  }

  public static void main(String[] args) {
    Dijkstraalgo path = new Dijkstraalgo();
  }
}
