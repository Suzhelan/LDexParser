package top.linl.dexparser.bean.ids;

import top.linl.dexparser.DexParser;
import top.linl.dexparser.util.Utils;

/**
 * @author suzhelan
 * time 2023.9.5
 */
public class DexStringId {

    /**
     * 字节长度
     */
    public int string_byte_length;
    /**
     * 在dex的索引偏移
     * uint
     */
    public int string_data_off;


    public DexStringId(int string_data_off, int string_byte_length) {
        this.string_data_off = string_data_off;
        this.string_byte_length = string_byte_length;
    }


    public String getString(DexParser parser) {
        byte[] string_data = Utils.copyArrays(parser.dexData, string_data_off + 1, string_byte_length);
        return new String(string_data);
    }

}
