package top.linl.dexparser.bean.ids;

import top.linl.dexparser.DexParser;

/**
 * 表示方法签名
 */
public class DexProtoId {
    /**
     * 方法签名简写 指向string_idx
     */
    int shorty_idx;
    /**
     * 方法返回类型  指向type_idx
     */
    int return_type_idx;
    /**
     * 方法参数列表偏移量 如果不为0则指向type_list格式
     * 参阅 https://source.android.google.cn/docs/core/runtime/dex-format?hl=zh-cn#type-list
     * 实现 {@link DexMethodId#getMethodParams(DexParser)}
     */
    int parameters_off;


    public DexProtoId(int shorty_idx, int return_type_idx, int parameters_off) {
        this.shorty_idx = shorty_idx;
        this.return_type_idx = return_type_idx;
        this.parameters_off = parameters_off;
    }
}
