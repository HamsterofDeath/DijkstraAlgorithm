package modell;

import java.util.ArrayList;
import java.util.List;

public class Dijkstraalgo {

  List<Node> nodes = new ArrayList<>();

  public static class Node {
    public Node(String name, List<Cost> costs) {
      this.name = name;
      this.costs = costs;
    }

    String name;
    List<Cost> costs;
  }

  public static class Cost {
    public Cost(Node node, int cost) {
      this.node = node;
      this.cost = cost;
    }

    Node node;
    int cost;
  }

  public void addNode(){

  }


  public static void main(String[] args) {
    Dijkstraalgo path = new Dijkstraalgo();
  }
}
