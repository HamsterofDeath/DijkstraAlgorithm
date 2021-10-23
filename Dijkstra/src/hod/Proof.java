package hod;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Proof {
    public static void main(String[] args) {
        var samples = 100;
        var size = 5;
        var max = 100;
        for (int i = 0; i < samples; i++) {
            var data = random(size, 100);
            var high = avg(data);
            var low = root(data);
            System.out.println(Arrays.toString(data) + " = "+high +" >= "+low);
        }

    }

    public static double avg(double[] ns) {
        return Arrays.stream(ns).sum() / ns.length;
    }

    public static double root(double[] ns) {
        return Arrays.stream(ns)
                       .map(n -> Math.pow(n, 1.0 / ns.length)).reduce((n, n2) -> n * n2)
                       .orElseThrow() / ns.length;
    }

    public static double[] random(int size, int max) {
        final double[] ret = new double[size];
        for (int i = 0, retLength = ret.length; i < retLength; i++) {
            ret[i] = Math.random() * max;
        }
        Arrays.sort(ret);
        return ret;
    }
}
