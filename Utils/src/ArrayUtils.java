public class ArrayUtils {
    public static <T> boolean SameElements(T[] t1, T[] t2) {
        if (t1.length != t2.length)
            return false;
        for (int i = 0; i < t1.length; i++)
            if (!t1[i].equals(t2[i]))
                return false;
        return true;
    }

    public static boolean SameElements(double[] a, double[] b) {
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if (a[i] - b[i] > 0.0001)
                return false;
        return true;
    }
}
