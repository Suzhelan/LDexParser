package top.linl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import top.linl.dexparser.util.Utils;

public class Simple {
    public static List<Object> objects = new ArrayList<>();
    public static void main(String[] args) throws Exception {

        String name = new int[0].getClass().getName();
        System.out.println(name);
        Simple.class.getClassLoader().loadClass(name);
        Utils.MTimer m1 = new Utils.MTimer();
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);//大多数情况下，请保持kryo.setReferences(true)

        Output output = new Output(new FileOutputStream(new File("resources/test")));
        for (int i = 0; i < 500 * 10000; i++) {
            Test test = new Test();
            test.index = i;
            test.ides = i;
            test.shh[0] = new shh();
            kryo.writeObject(output, test);
        }
        output.close();
        System.out.println(m1.get());

        Utils.MTimer m2 = new Utils.MTimer();
        Writer writer = new FileWriter(new File("resources/test.txt"));
        BufferedWriter buf = new BufferedWriter(writer);
        for (int i = 0; i < 500 * 10000; i++) {
            Test test = new Test();
            test.index = i;
            test.ides = i;
            test.shh[0] = new shh();
            buf.write(JSONArray.toJSONString(test));
        }
        buf.close();
        writer.close();
        System.out.println(m2.get());

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage(); //椎内存使用情况
        long usedMemorySize = memoryUsage.getUsed(); //已使用的内存
        System.out.printf(" 已用内存%s", usedMemorySize/(1024*1024)+"M");

//        System.out.println(objects.size());
    }

    static class Test {
        public int index;
        public int ides;
        public shh[] shh = new shh[1];
    }

    static class shh {
        int pp = 9;
    }

}
