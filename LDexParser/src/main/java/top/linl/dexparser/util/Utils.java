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
}
