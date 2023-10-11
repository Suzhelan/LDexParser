package top.linl.dexparser.util;


import java.io.*;
import java.text.DecimalFormat;

public class FileUtils {


    /**
     * 计算文件大小
     * fileUrl：D:/download/山花遍野.jpg
     * @return 1GB
     * */
    public static String getSize(long size) {
        int GB = 1024 * 1024 * 1024;//定义GB的计算常量
        int MB = 1024 * 1024;//定义MB的计算常量
        int KB = 1024;//定义KB的计算常量
        try {
            // 格式化小数
            DecimalFormat df = new DecimalFormat("0.00");
            String resultSize = "";
            if (size / GB >= 1) {
                //如果当前Byte的值大于等于1GB
                resultSize = df.format(size / (float) GB) + "GB";
            } else if (size / MB >= 1) {
                //如果当前Byte的值大于等于1MB
                resultSize = df.format(size / (float) MB) + "MB";
            } else if (size / KB >= 1) {
                //如果当前Byte的值大于等于1KB
                resultSize = df.format(size / (float) KB) + "KB";
            } else {
                resultSize = size + "B";
            }
            return resultSize;
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }



    public static byte[] readAllByte(InputStream stream, int size) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream in = null;
        try {
            bos = new ByteArrayOutputStream(size);
            in = new BufferedInputStream(stream);
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
