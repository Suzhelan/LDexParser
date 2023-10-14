package top.linl;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import top.linl.dexparser.DexFinder;

public class Main {
    public static void main(String[] args) {
        System.out.println(System.getProperty("file.encoding"));
        System.out.println("开始解析DEX");

        File file = new File("resources/base.apk");
        try {
            long l1 = System.currentTimeMillis();
            DexFinder finder = new DexFinder.Builder(Main.class.getClassLoader(), file.getPath())
                    .setCachePath(new File("resources").getAbsolutePath())
                    .build();
            long startTime = System.currentTimeMillis();
            System.out.println("初始化耗时 " + (startTime - l1) + "ms");
            System.out.println(finder.testFindMethodString("记录下"));
            System.out.println(finder.testFindMethodString("请求"));
            long endTime = System.currentTimeMillis();
            finder.close();
            System.out.println("耗时 " + (endTime - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage(); //椎内存使用情况
        long usedMemorySize = memoryUsage.getUsed(); //已使用的内存
        System.out.printf(" 已用内存%s", usedMemorySize / (1024 * 1024) + "M");

    }
}
