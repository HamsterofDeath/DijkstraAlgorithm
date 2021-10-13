package modell;

import java.util.ArrayList;
import java.util.List;

public class Dijkstraalgo {

  List<Node> graph = new ArrayList<>();

  public static class Node {
    public Node(String name) {
      this.name = name;
    }

    public List<Connection> getConnections() {
      return connections;
    }

    String name;
    List<Connection> connections = new ArrayList<>();
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

    @Override
    public String toString() {
      return node.name +
              " cost=" + cost;
    }
  }

  public Node createNode(String n) {
    Node node = new Node(n);
    graph.add(node);
    return node;
  }

  public void addConnection(Node n, Node z, int cost) {
    n.getConnections().add(new Connection(z, cost));
    z.getConnections().add(new Connection(n, cost));
  }

  public Connection searchConnection(Node n, Node z) {
    for (Connection s : n.getConnections()) {
      if (s.node.equals(z)) {
        return s;
      }
    }
    return null;
  }

  public void deleteConnection(Node n, Node z) {
    Connection c1 = searchConnection(n, z);
    Connection c2 = searchConnection(z, n);
    if (c1 != null) {
      n.getConnections().remove(c1);
      z.getConnections().remove(c2);
    }
  }

  public void changeCostOfConnection(Node n, Node z, int newCost) {
    Connection c1 = searchConnection(n, z);
    Connection c2 = searchConnection(z, n);
    if (c1 != null) {
      for (Connection s : n.getConnections()) {
        if (s.equals(c1)) {
          s.setCost(newCost);
        }
      }
      for (Connection s : z.getConnections()) {
        if (s.equals(c2)) {
          s.setCost(newCost);
        }
      }
    }
  }

  public void drawConnections() {
    for (Node n : graph) {
      System.out.println(n.name + " ->" + n.connections);
    }
  }

  public static void main(String[] args) {
    Dijkstraalgo graph = new Dijkstraalgo();

    Node a = graph.createNode("a");
    Node b = graph.createNode("b");
    Node c = graph.createNode("c");
    Node d = graph.createNode("d");
    Node e = graph.createNode("e");
    Node f = graph.createNode("f");

    graph.addConnection(a, b, 9);
    graph.addConnection(a, d, 5);
    graph.addConnection(b, c, 3);
    graph.addConnection(b, e, 1);
    graph.addConnection(d, e, 2);
    graph.addConnection(e, f, 8);
    graph.addConnection(f, c, 3);
    graph.drawConnections();
  }
}
