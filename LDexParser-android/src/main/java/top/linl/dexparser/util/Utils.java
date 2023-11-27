package top.linl.dexparser.util;


/**
 * @author suzhelan
 * time 2023.9.30
 */
public class Utils {


    public static byte[] copyArrays(byte[] src, int start, int length) {
        byte[] copyArrays = new byte[length];
        System.arraycopy(src, start, copyArrays, 0, length);
        return copyArrays;
    }

    /**
     * 将16进制的int分析成数组
     */
    public static int[] SplitHexInt(int decnum) {
        int[] result = new int[2];
        if (decnum < 0x0100) {
            result[1] = decnum;
            return result;
        }
        result[0] = decnum / 256;
        result[1] = decnum % 256;
        return result;
    }

    /**
     * 排除常用类
     */
    public static boolean isCommonlyUsedClass(String name) {
        return name.startsWith("Ljava") || name.startsWith("Landroid") || name.startsWith("Lkotlin") || name.startsWith("Lcom/android") || name.startsWith("Lcom/google") || name.startsWith("Lcom/microsoft") || name.startsWith("Ldalvik");
    }

    public static class MTimer {
        private final long startTime = System.currentTimeMillis();

        public String get() {
            return (System.currentTimeMillis() - startTime) + "ms";
        }
    }
}
