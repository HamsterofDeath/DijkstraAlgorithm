package modell;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Dijkstraalgo {

  List<Node> graph = new ArrayList<>();

  public Node getNode(final String name) {
    return graph.stream().filter(node -> name.equalsIgnoreCase(node.name)).findAny().orElseThrow();
  }

  public static class Node {
    public Node(String name) {
      this.name = name;
    }

    public List<Connection> getConnections() {
      return connections;
    }

    public final String name;
    public final List<Connection> connections = new ArrayList<>();

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof Node)) return false;
      final Node node = (Node) o;
      return Objects.equals(name, node.name);
    }

    @Override
    public String toString() {
      return "Node{" + "name='" + name + '\'' + '}';
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }
  }

  public static class Connection {
    public Connection(Node node, int cost) {
      this.node = node;
      this.cost = cost;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof Connection)) return false;
      final Connection that = (Connection) o;
      return cost == that.cost && Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
      return Objects.hash(node, cost);
    }

    private final Node node;

    public Node getNode() {
      return node;
    }

    public int getCost() {
      return cost;
    }

    private int cost;

    public void setCost(int cost) {
      this.cost = cost;
    }

    @Override
    public String toString() {
      return node.name + " cost=" + cost;
    }
  }

  public class Path {
    // the node is the goal, and the list is the path from start to goal

    public Path(Node node) {
      this.node = node;
    }

    public List<Node> getPath() {
      return path;
    }

    Node node;
    List<Node> path;
    int gesamtCost;

    public void setPath(List<Node> path) {
      this.path = path;
    }

    public void setGesamtCost(int gesamtCost) {
      this.gesamtCost = gesamtCost;
    }
  }

  public Node createNode(String n) {
    Node node = new Node(n);
    graph.add(node);
    return node;
  }

  public void addConnection(Node n, Node z, int cost) {
    final List<Connection> addFirstConnection = n.getConnections();
    final Connection con1 = new Connection(z, cost);
    if (!addFirstConnection.contains(con1)) {
      addFirstConnection.add(con1);
    }
    final List<Connection> addSecondConnection = z.getConnections();
    final Connection con2 = new Connection(n, cost);
    if (!addSecondConnection.contains(con2)) {
      addSecondConnection.add(con2);
    }
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

  private BufferedImage getImage(String filename) {
    // This time, you can use an InputStream to load
    try {
      // Grab the InputStream for the image.
      InputStream in = getClass().getResourceAsStream(filename);
      // Then read it.
      return ImageIO.read(in);
    } catch (IOException e) {
      System.out.println("The image was not loaded.");
      // System.exit(1);
    }
    return null;
  }

  public void drawConnections() {
    for (Node n : graph) {
      System.out.println(n.name + " ->" + n.connections);
    }
  }

  public void imageToNodes() {}

  public Path myDijkstra(Node start, Node ziel) {
    List<Path> paths = new ArrayList<>();
    List<Node> nichtBearbeitet = graph;

    if (start != null && ziel != null) {
      if (graph.contains(start) && graph.contains(ziel)) {
        for (Node n : graph) {
          if (n.equals(start)) {
            // remove the start node, as we don't need to visit it
            nichtBearbeitet.remove(start);
            // create paths for every single node except the start
            for (int i = 0; i < nichtBearbeitet.size(); i++) {
              paths.set(i, new Path(nichtBearbeitet.get(i)));
            }
            // update the paths for the neighbors of the start node
            for (int i = 0; i < n.connections.size(); i++) {
              for (Path a : paths) {
                if (start.connections.get(i).node.equals(a.node)) {
                  a.getPath().add(n);
                  a.setGesamtCost(n.connections.get(i).cost);
                }
              }
            }
            while (nichtBearbeitet.size() >= 1) {
              // compare the costs for the paths that haven't been optimized yet
              Node minNode = nodeWithMinCost(paths, nichtBearbeitet);
              // remove the chosen node, as its path is now optimal
              nichtBearbeitet.remove(minNode);
              for (int i = 0; i < minNode.connections.size(); i++) {
                for (Path a : paths) {
                  if (minNode.connections.get(i).node.equals(a.node)) {
                    //TODO: here I need to first compare the costs of the paths if bigger replace, if smaller leave alone
                    a.getPath().add(minNode);
                    a.setGesamtCost(minNode.connections.get(i).cost);
                  }
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

  private Node nodeWithMinCost(List<Path> paths, List<Node> nichtBearbeitet) {}

  public static void main(String[] args) {
    Dijkstraalgo graph = createDemoGraph();
    graph.drawConnections();
  }

  public static Dijkstraalgo createDemoGraph() {
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
    return graph;
  }
}
