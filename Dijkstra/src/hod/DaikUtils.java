package hod;

import modell.Dijkstraalgo;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class DaikUtils {

    public static final String mazeUrl = "https://i.ibb.co/zJ95xjw/amazeing.png";

    public static Dijkstraalgo fromImage(BufferedImage img) {
        var graph = new Dijkstraalgo();
        var nodes = new HashMap<String, Dijkstraalgo.Node>();
        var w = img.getWidth();
        var h = img.getHeight();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                var color = img.getRGB(x, y);
                if (color == Color.WHITE.getRGB()) {
                    var nodeName = x + "/" + y;
                    nodes.put(nodeName, graph.createNode(nodeName));
                }
            }
        }

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                var nodeName = x + "/" + y;
                if (nodes.containsKey(nodeName)) {
                    var center = nodes.get(nodeName);
                    for (int x2 = -1; x2 <= 1; x2++) {
                        for (int y2 = -1; y2 <= 1; y2++) {
                            if (x2 != 0 || y2 != 0) {
                                var otherX = x + x2;
                                var otherY = y + y2;
                                var otherNodeName = otherX + "/" + otherY;
                                if (nodes.containsKey(otherNodeName)) {
                                    var other = nodes.get(otherNodeName);
                                    var cost = (int) Math.round(Math.sqrt(x2 * x2 + y2 * y2) * 100);
                                    graph.addConnection(center, other, cost);
                                }
                            }
                        }
                    }
                }
            }
        }

        return graph;

    }

    public static BufferedImage loadImage(String path) {
        try {
            URL url = new URL(path);
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        var startX = 0;
        var startY = 0;
        var targetX = 115;
        var targetY = 105;
        drawSolution(startX, startY, targetX, targetY, mazeUrl);
    }

    private static void drawSolution(final int startX, final int startY, final int targetX,
                                     final int targetY, final String urlToMaze) {
        var image = loadImage(urlToMaze);
        var sample = fromImage(image);
        final Dijkstraalgo.Node upperLeft = sample.getNode(startX + "/" + startY);
        final Dijkstraalgo.Node lowerRight = sample.getNode(targetX + "/" + targetY);
        var daik = Daikstra.createDaikstra(upperLeft, true);
        var path = daik.pathTo(lowerRight);
        System.out.println("Cost: "+path.get(path.size()-1).cost());
        for (SolutionNode node : path) {
            var name = node.node().name;
            var split = name.split("/");
            var x = Integer.parseInt(split[0]);
            var y = Integer.parseInt(split[1]);
            image.setRGB(x,y, Color.red.getRGB());
        }
        try {
            final File result = new File("solution.png");
            result.delete();
            ImageIO.write(image, "PNG", result);
            System.out.println(result.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
