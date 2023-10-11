package top.linl.dexparser;

import top.linl.dexparser.bean.ids.DexMethodId;
import top.linl.dexparser.bean.ids.DexTypeId;
import top.linl.dexparser.util.DexTypeUtils;
import top.linl.dexparser.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class DexFinder {

    public static int mThreadSize = 5;
    private final ArrayList<DexParser> dexParsersList = new ArrayList<>();

    private final ExecutorService dexInitTask = Executors.newFixedThreadPool(mThreadSize);
    private final ZipFile apkZipFile;

    private CountDownLatch allTaskOver;

    /**
     * start build dexfinder
     *
     * @param classLoader host apk classloader
     * @param apkFile apkPath
     */
    public static DexFinder builder(ClassLoader classLoader, File apkFile) throws Exception {
        return new DexFinder(classLoader, apkFile);
    }

    /**
     * start build dexfinder
     *
     * @param classLoader host apk classloader
     * @param apkFile apkPath
     * @param threadSize Number of threads If the crash during initialization can be reduced to less than 5, this may be caused by high heap memory usage
     */
    public static DexFinder builder(ClassLoader classLoader, File apkFile, int threadSize) throws Exception {
        mThreadSize = threadSize;
        return new DexFinder(classLoader, apkFile);
    }

    private DexFinder(ClassLoader loader, File apkFile) throws Exception {
        DexTypeUtils.setClassLoader(loader);
        apkZipFile = new ZipFile(apkFile);
        ZipEntry entry; // 每一个压缩实体
        InputStream inputStream = Files.newInputStream(apkFile.toPath());
        ZipInputStream zipInput = new ZipInputStream(inputStream);
        //遍历压缩包中的文件
        while ((entry = zipInput.getNextEntry()) != null) { // 得到一个压缩实体
            if (!entry.isDirectory() && entry.getName().endsWith(".dex")) {
                ZipEntry finalEntry = entry;
                dexInitTask.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InputStream stream = apkZipFile.getInputStream(finalEntry);
                            byte[] dexData = FileUtils.readAllByte(stream, (int) finalEntry.getSize());
                            stream.close();
                            DexParser dexParser = new DexParser(dexData);
                            dexParser.entry = finalEntry;
                            dexParser.init();
                            dexParsersList.add(dexParser);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
        dexInitTask.shutdown();
        while (true) {
            if (dexInitTask.isTerminated()) {
                System.out.println("init end");
                break;
            }
        }
        zipInput.close();
    }

    public ArrayList<Method> findMethodString(String str) throws Exception {
        ArrayList<Method> result = new ArrayList<>();
        ExecutorService findTaskList = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(dexParsersList.size());
        for (DexParser dexParser : dexParsersList) {
            findTaskList.submit(() -> {
                InputStream stream;
                try {
                    stream = apkZipFile.getInputStream(dexParser.entry);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dexParser.setDexData(FileUtils.readAllByte(stream, (int) dexParser.entry.getSize()));
                for (DexMethodId dexMethodId : dexParser.dexMethodIdsList) {
                    if (dexMethodId.getUsedStringList() == null) continue;
                    for (Integer integer : dexMethodId.getUsedStringList()) {
                        String method_string = dexParser.dexStringIdsList[integer].getString(dexParser);
                        if (method_string.contains(str)) {
                            try {
                                //get method info
                                String methodName = dexParser.dexStringIdsList[dexMethodId.name_idx].getString(dexParser);
                                String declareClass = dexParser.dexStringIdsList[dexParser.dexTypeIdsList[dexMethodId.class_ids].descriptor_idx].getString(dexParser);
                                DexTypeId[] methodParams = dexMethodId.getMethodParams(dexParser);
                                Class<?> clz = DexTypeUtils.findClass(declareClass);
                                Class<?>[] params = new Class[methodParams.length];
                                for (int i = 0; i < params.length; i++) {
                                    String className = dexParser.dexStringIdsList[methodParams[i].descriptor_idx].getString(dexParser);
                                    params[i] = DexTypeUtils.findClass(className);
                                }
                                Method method = clz.getDeclaredMethod(methodName, params);
                                result.add(method);
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                dexParser.closeDexData();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();//计数器等待线程池执行完成
        findTaskList.shutdownNow();//防止跑到这一行 mian方法仍然在运行
        return result;
    }

    public ArrayList<String> testFindMethodString(String str) throws Exception {
        ArrayList<String> result = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(mThreadSize);
        CountDownLatch countDownLatch = new CountDownLatch(dexParsersList.size());
        for (DexParser dexParser : dexParsersList) {
            executorService.submit(() -> {
                InputStream stream;
                try {
                    stream = apkZipFile.getInputStream(dexParser.entry);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dexParser.setDexData(FileUtils.readAllByte(stream, (int) dexParser.entry.getSize()));
                Loop:
                for (DexMethodId dexMethodId : dexParser.dexMethodIdsList) {
                    if (dexMethodId.getUsedStringList() == null) continue;
                    for (Integer integer : dexMethodId.getUsedStringList()) {
                        String method_string = dexParser.dexStringIdsList[integer].getString(dexParser);
                        if (method_string.contains(str)) {
                            String methodName = dexParser.dexStringIdsList[dexMethodId.name_idx].getString(dexParser);
                            String declareClass = dexParser.dexStringIdsList[dexParser.dexTypeIdsList[dexMethodId.class_ids].descriptor_idx].getString(dexParser);
                            DexTypeId[] methodParams = dexMethodId.getMethodParams(dexParser);
                            result.add(declareClass + methodName);
                            continue Loop;
                        }
                    }
                }
                dexParser.closeDexData();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdownNow();//防止跑到这一行 mian方法仍然在运行
        return result;
    }

    public static class FindMethodTask implements Runnable {

        @Override
        public void run() {

        }
    }
}
