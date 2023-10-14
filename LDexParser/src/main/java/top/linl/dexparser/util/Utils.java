package top.linl.dexparser.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author suzhelan
 * time 2023.9.30
 */
public class Utils {

    public static void outMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage(); //椎内存使用情况
        long usedMemorySize = memoryUsage.getUsed(); //已使用的内存
        System.out.printf(" 已用内存%s", usedMemorySize / (1024 * 1024) + "M");
    }

    public static byte[] copyArrays(byte[] src, int start, int length) {
        byte[] copyArrays = new byte[length];
        System.arraycopy(src, start, copyArrays, 0, length);
        return copyArrays;
    }

    public static class MTimer {
        private final long startTime = System.currentTimeMillis();

        public String get() {
            return (System.currentTimeMillis() - startTime) + "ms";
        }
    }
}
