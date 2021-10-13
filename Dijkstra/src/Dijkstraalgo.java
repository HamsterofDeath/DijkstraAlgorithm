import java.util.List;

public class Dijkstraalgo {

  public class Node {
    String name;
    List<Cost> costs;
  }

  public class Cost {
    Node node;
    int cost;
  }
}
