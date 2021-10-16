package modell;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Dijkstraalgo {

  // little explanation of the classes: each point is a node. Each node has a connection that
  // contains the node it is connected with and the cost. There is also a path that contains the
  // ziel node and a list of all the nodes that need to be traversed to each the ziel along with the
  // total cost.

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

    @Override
    public String toString() {
      return "Path{" + "node=" + node.name + ", path=" + path + ", gesamtCost=" + gesamtCost + '}';
    }

    public List<Node> getPath() {
      return path;
    }

    Node node;
    List<Node> path = new ArrayList<>();
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
    List<Node> nochNichtbearbeitet = new ArrayList<>(graph);
    paths.add(initializePath(start));

    // Step 1: take the start and create paths for its neighbors
    nochNichtbearbeitet.remove(start);
    paths = createPathsNeighbors(start, paths);

    // Step 2: compare all the paths whose nodes have not been bearbeitet and take the min
    while (nochNichtbearbeitet.size() > 0) {
      Path minPath = minCostPaths(paths, nochNichtbearbeitet);
      // the case that the path to the goal is already found, we don't need to investigate the rest
      if (minPath.node.equals(ziel)) {
        return minPath;
      }
      nochNichtbearbeitet.remove(minPath.node);
      paths = createPathsNeighbors(minPath.node, paths);
    }
    return pathToZiel(paths, ziel);
  }

  private Path initializePath(Node start) {
    Path starting = new Path(start);
    starting.getPath().add(start);
    starting.setGesamtCost(0);
    return starting;
  }

  private Path pathToZiel(List<Path> paths, Node ziel) {
    for (Path a : paths) {
      if (a.node.equals(ziel)) {
        return a;
      }
    }
    return null;
  }

  private Path minCostPaths(List<Path> paths, List<Node> nochNichtbearbeitet) {
    Path minPath = null;
    int cost = -1;
    for (int i = 0; i < paths.size(); i++) {
      if (nochNichtbearbeitet.contains(paths.get(i).node)) {
        if (cost == -1) {
          cost = paths.get(i).gesamtCost;
          minPath = paths.get(i);
        } else {
          if (paths.get(i).gesamtCost < cost) {
            cost = paths.get(i).gesamtCost;
            minPath = paths.get(i);
          }
        }
      }
    }
    return minPath;
  }

  private List<Path> createPathsNeighbors(Node start, List<Path> paths) {
    List<Path> optimisedPaths = new ArrayList<>(paths);
    // first extract the neighbors
    List<Connection> neighbors = start.getConnections();
    // now we check the paths to these connections and select the path with minimal cost
    // case 1: the path already exists
    for (Connection c : neighbors) {
      Path pathToStart = pathForNode(start, paths);
      if (pathForConnectionExists(c.node, paths)) {
        Path pathForConn = pathForNode(c.node, paths);
        if (pathForConn.gesamtCost > (pathToStart.gesamtCost + c.getCost())) {
          optimisedPaths = optimisePaths(optimisedPaths, pathForConn, pathToStart, c.getCost());
        }
      } else {
        optimisedPaths = createAnewPath(optimisedPaths, c, pathToStart);
      }
    }
    return optimisedPaths;
  }

  private List<Path> createAnewPath(List<Path> optimisedPaths, Connection c, Path pathToStart) {
    List<Path> opti = new ArrayList<>(optimisedPaths);
    Path toC = new Path(c.node);
    toC.setPath(pathToStart.getPath());
    toC.getPath().add(c.node);
    toC.setGesamtCost(pathToStart.gesamtCost + c.cost);
    opti.add(toC);
    return opti;
  }

  private boolean pathForConnectionExists(Node node, List<Path> paths) {
    for (Path a : paths) {
      if (a.node.equals(node)) {
        return true;
      }
    }
    return false;
  }

  private Path pathForNode(Node start, List<Path> paths) {
    for (Path a : paths) {
      if (a.node.equals(start)) {
        return a;
      }
    }
    return null;
  }

  // I replace the pathForConn with pathtoStart + cost of the last connection
  private List<Path> optimisePaths(
      List<Path> optimisedPaths, Path pathForConn, Path pathToStart, int cost) {
    List<Path> opti = new ArrayList<>(optimisedPaths);
    for (Path a : opti) {
      if (a.equals(pathForConn)) {
        a.setPath(pathToStart.getPath());
        a.getPath().add(a.node);
        a.gesamtCost = pathToStart.gesamtCost + cost;
      }
    }
    return opti;
  }

  public static void main(String[] args) {
    Dijkstraalgo graph = createDemoGraph();
    graph.drawConnections();
    Path path = graph.myDijkstra(graph.getNode("a"), graph.getNode("f"));
    System.out.println(path);
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
