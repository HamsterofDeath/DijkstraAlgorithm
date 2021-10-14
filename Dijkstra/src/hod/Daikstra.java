package hod;

import modell.Dijkstraalgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Daikstra {
    private final Dijkstraalgo.Node start;

    public Daikstra(final Dijkstraalgo.Node start) {
        this.start = start;
        new EnhancedNode(start, 0, null);
    }

    public List<EnhancedNode> pathTo(Dijkstraalgo.Node goal) {
        var cursor = goal;
        var path = new ArrayList<EnhancedNode>();
        while (cursor != null) {
            var enhanced = node2ShortestPathEnd.get(cursor);
            if (cursor != start) {
                cursor = enhanced.cameFrom.basedOn;
            } else {
                cursor = null;
            }
            path.add(enhanced);

        }
        Collections.reverse(path);

        return path;


    }

    private final Map<Dijkstraalgo.Node, EnhancedNode> node2ShortestPathEnd = new HashMap<>();

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
    }

    public static void main(String[] args) {
        var g = Dijkstraalgo.createDemoGraph();
        var daik = new Daikstra(g.getNode("A"));
        var path = daik.pathTo(g.getNode("F"));
        System.out.println(path);
    }

}
