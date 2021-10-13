package modell;

import java.util.ArrayList;
import java.util.List;

public class Dijkstraalgo {

  List<Node> nodes = new ArrayList<>();

  public static class Node {
    public Node(String name) {
      this.name = name;
    }

    public final String name;
    public final List<Cost> costs = new ArrayList<>();
  }

  public static class Cost {
    public Cost(Node node, int cost) {
      this.node = node;
      this.cost = cost;
    }

    public final Node node;
    public final int cost;
  }

  public void addNode(){

  }


  public static void main(String[] args) {
    Dijkstraalgo path = new Dijkstraalgo();
  }
}
