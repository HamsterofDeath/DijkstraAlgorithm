package modell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dijkstraalgo {

    // little explanation of the classes: each point is a node. Each node has a connection that
    // contains the node it is connected with and the cost. There is also a path that contains the
    // ziel node and a list of all the nodes that need to be traversed to each the ziel along
    // with the
    // total cost.

    List<Node> graph = new ArrayList<>();

    public Node getNode(final String name) {
        return graph.stream().filter(node -> name.equalsIgnoreCase(node.name)).findAny()
                .orElseThrow();
    }

    public static class Node {
        public Node(String name) {
            this.name = name;
        }

        public List<Connection> getConnections() {
            return connections;
        }

        public final String           name;
        public final List<Connection> connections = new ArrayList<>();

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof final Node node)) return false;
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

    public record Connection(Node node, int cost) {

        @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (!(o instanceof final Connection that)) return false;
                return cost == that.cost && Objects.equals(node, that.node);
            }

        @Override
            public String toString() {
                return node.name + " cost=" + cost;
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


}
