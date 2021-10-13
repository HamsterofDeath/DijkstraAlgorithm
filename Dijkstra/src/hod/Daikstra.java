package hod;

import modell.Dijkstraalgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Daikstra {
    private Dijkstraalgo.Node start;
    private Dijkstraalgo.Node goal;

    public Daikstra(final Dijkstraalgo.Node start, final Dijkstraalgo.Node goal) {
        this.start = start;
        this.goal = goal;
        new EnhancedNode(start);
    }

    public List<EnhancedNode> pathTo(Dijkstraalgo.Node goal) {
        var cursor = goal;
        var path = new ArrayList<EnhancedNode>();
        while (!cursor.equals(start)) {
            var enhanced = node2ShortestPathEnd.get(cursor);
            path.add(enhanced);
            cursor = enhanced.cameFrom.basedOn;
        }
        Collections.reverse(path);

        return path;


    }

    private Map<Dijkstraalgo.Node, EnhancedNode> node2ShortestPathEnd = new HashMap<>();

    public class EnhancedNode {
        public final  Dijkstraalgo.Node       basedOn;
        public        int                     totalCost = 0;
        public        int                     baseCost  = 0;
        public        EnhancedNode            cameFrom;
        private final List<Dijkstraalgo.Node> seen      = new ArrayList<>();

        public EnhancedNode(final Dijkstraalgo.Node node) {
            this.basedOn = node;
            if (node2ShortestPathEnd.containsKey(basedOn)) {
                throw new IllegalStateException();
            }
            node2ShortestPathEnd.put(basedOn, this);

            for (Dijkstraalgo.Cost cost : node.costs) {
                if (node2ShortestPathEnd.containsKey(cost.node)) {
                    final EnhancedNode knownNode = node2ShortestPathEnd.get(cost.node);
                    knownNode.testNewPath(cost);
                } else {
                    final EnhancedNode nodeToConsider = new EnhancedNode(cost.node);
                    nodeToConsider.init(this, totalCost + cost.cost);
                    node2ShortestPathEnd.put(cost.node, nodeToConsider);
                }
            }
        }

        private void testNewPath(final Dijkstraalgo.Cost alternative) {
            if (alternative.cost + baseCost < totalCost) {
                var source = node2ShortestPathEnd.get(cameFrom.basedOn);
                final EnhancedNode nodeToConsider = new EnhancedNode(alternative.node);
                nodeToConsider.init(source, baseCost + alternative.cost);
                node2ShortestPathEnd.put(basedOn, nodeToConsider);
            }
        }

        private void init(final EnhancedNode cameFrom, final int totalCost) {
            this.cameFrom = cameFrom;
            this.baseCost = cameFrom.totalCost;
            this.totalCost = totalCost;
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

    }

}
