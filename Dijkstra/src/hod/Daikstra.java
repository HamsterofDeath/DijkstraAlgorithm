package hod;

import modell.Dijkstraalgo;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Daikstra {
    private final Dijkstraalgo.Node                    start;
    private final Map<Dijkstraalgo.Node, EnhancedNode> node2ShortestPathEnd = new HashMap<>();
    private final Map<Dijkstraalgo.Node, SolutionNode> node2ShortestPathEndV2 = new HashMap<>();

    private Daikstra(final Dijkstraalgo.Node start, final boolean v2) {
        this.start = start;
        if (v2) {
            node2ShortestPathEndV2.putAll(new DaikV2(start).getSolutions());
        } else {
            new EnhancedNode(start, 0, null);
            node2ShortestPathEnd.forEach((node, enhanced) -> node2ShortestPathEndV2.put(node, enhanced.toSimple()));
            node2ShortestPathEnd.clear();

        }
    }

    public static Daikstra createDaikstra(final Dijkstraalgo.Node start, final boolean v2) {
        return new Daikstra(start, v2);
    }

    public List<SolutionNode> pathTo(Dijkstraalgo.Node goal) {
        var cursor = goal;
        ArrayList<SolutionNode> path = new ArrayList<>();
        while (cursor != null) {
            var enhanced = node2ShortestPathEndV2.get(cursor);
            if (!start.equals(cursor)) {
                cursor = enhanced.cameFrom();
            } else {
                cursor = null;
            }
            path.add(enhanced);

        }
        Collections.reverse(path);

        return path;
    }


    public class EnhancedNode {
        public final Dijkstraalgo.Node basedOn;
        public final int               costToReach;
        public       EnhancedNode      cameFrom;

        @Override
        public String toString() {
            return "EnhancedNode{" +
                   "basedOn=" + basedOn +
                   ", totalCost=" + costToReach +
                   '}';
        }

        public EnhancedNode(final Dijkstraalgo.Node me, final int costToReach,
                            final EnhancedNode previous) {
            this.basedOn = me;
            this.cameFrom = previous;
            if (node2ShortestPathEnd.containsKey(basedOn)) {
                throw new IllegalStateException();
            }
            node2ShortestPathEnd.put(basedOn, this);
            this.costToReach = costToReach;

            for (Dijkstraalgo.Connection connection : me.connections) {
                if (node2ShortestPathEnd.containsKey(connection.getNode())) {
                    final EnhancedNode knownNode = node2ShortestPathEnd.get(connection.getNode());
                    knownNode.testNewPath(connection, this);
                } else {
                    final EnhancedNode
                            nodeToConsider =
                            new EnhancedNode(connection.getNode(),
                                             costToReach + connection.getCost(), this);
                    node2ShortestPathEnd.put(nodeToConsider.basedOn, nodeToConsider);
                }
            }
        }

        private void testNewPath(final Dijkstraalgo.Connection alternativePath,
                                 final EnhancedNode alternativeSource) {
            final int alternativeCost = alternativePath.getCost() + alternativeSource.costToReach;
            if (alternativeCost < costToReach) {
                node2ShortestPathEnd.remove(alternativePath.getNode());
                final EnhancedNode
                        nodeToConsider =
                        new EnhancedNode(alternativePath.getNode(), alternativeCost,
                                         alternativeSource);
                node2ShortestPathEnd.put(basedOn, nodeToConsider);
            }
        }


        public List<EnhancedNode> buildPath() {
            var path = new ArrayList<EnhancedNode>();
            var pointer = this;
            while (pointer != null) {
                path.add(pointer);
                pointer = pointer.cameFrom;
            }
            Collections.reverse(path);
            return path;
        }

        public String getName() {
            return basedOn.name;
        }

        public SolutionNode toSimple() {
            return new SolutionNode(basedOn, costToReach, cameFrom==null?null:cameFrom.basedOn);
        }
    }

    public static void main(String[] args) {
        var g = Dijkstraalgo.createDemoGraph();
        var daik = createDaikstra(g.getNode("A"), true);
        var path = daik.pathTo(g.getNode("F"));
        System.out.println(path);
    }

}

record SolutionNode(Dijkstraalgo.Node node, int cost, Dijkstraalgo.Node cameFrom) {

};

class DaikV2 {

    private record TempNode(Dijkstraalgo.Node node, Dijkstraalgo.Node previous,
                            int costUntilHere) {

    }

    private final Queue<TempNode>
                                                   open  =
            new PriorityQueue<>(Comparator.comparingInt(o -> o.costUntilHere));
    private final Map<Dijkstraalgo.Node, TempNode> paths = new HashMap<>();
    private final Map<Dijkstraalgo.Node, SolutionNode> solutions = new HashMap<>();
    private final Set<Dijkstraalgo.Node> seen = new HashSet<>();

    public DaikV2(final Dijkstraalgo.Node start) {
        final TempNode origin = new TempNode(start, null, 0);
        open.add(origin);
        doAllTheLogic();
    }

    private void doAllTheLogic() {
        while (!open.isEmpty()) {
            var here = open.remove();
            seen.remove(here.node);
            paths.put(here.node, here);
            var todo = here.node
                    .getConnections()
                    .stream()
                    .flatMap(connection ->
                         {
                             final Dijkstraalgo.Node target = connection.getNode();
                             final int totalCost = here.costUntilHere() + connection.getCost();
                             if (paths.containsKey(target)) {
                                var known = paths.get(target);
                                 if (known.costUntilHere <= totalCost) {
                                    return Optional.<TempNode>empty().stream();
                                 } else {
                                     final Dijkstraalgo.Node base = here.node();
                                     seen.add(base);
                                     return Optional.of(new TempNode(target, base, totalCost)).stream();
                                 }
                             } else {
                                 final Dijkstraalgo.Node base = here.node();
                                 var addMe = new TempNode(target, base, totalCost);
                                 if (!seen.contains(target)) {
                                     seen.add(target);
                                     return Optional.of(addMe).stream();
                                 } else {
                                     return Optional.<TempNode>empty().stream();
                                 }
                             }
                         }
                    );
            final List<TempNode> addUs = todo.toList();
            open.addAll(addUs);
        }
        paths.forEach((node, tempNode) -> solutions.put(node, new SolutionNode(tempNode.node, tempNode.costUntilHere(),tempNode.previous())));
        paths.clear();
    }

    public Map<Dijkstraalgo.Node, SolutionNode> getSolutions() {
        return solutions;
    }
}
