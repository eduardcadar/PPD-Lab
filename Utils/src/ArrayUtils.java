public class ArrayUtils {
    public static boolean SameElements(double[] a, double[] b) {
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if (a[i] - b[i] > 0.0001)
                return false;
        return true;
    }
}
