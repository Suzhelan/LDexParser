# LDexParser
Android Dex parser
用于Xposed查找被混淆的方法 目前只写了查找方法出现的的字符串  
更推荐使用 性能更好 更方便使用的[dexkit](https://github.com/LuckyPray/DexKit)  
写此项目仅用于自用与学习 该项目在所有方面都有待优化 我也正在持续优化
可参阅文章学习   
- [知乎-DEX 文件格式解析](https://zhuanlan.zhihu.com/p/66800634)
- [Google Dex文件格式](https://source.android.google.cn/docs/core/runtime/dex-format?hl=zh-cn)
- [Google Dex指令集相关-Dalvik 字节码](https://source.android.google.cn/docs/core/runtime/dalvik-bytecode?hl=zh-cn)
---
基本实现 第一步
```java
                //先初始化 参数一是目标应用类加载器 参数二是目标类路径
                DexFinder dexFinder = new DexFinder.Builder(ClassUtils.getHostLoader(), HookEnv.getHostApkPath())
                        .setCachePath(PathTool.getModuleDataPath() + "/MethodFinderCache")//设置运行缓存路径 将使用本地内存代表堆内存 这样可以在解析大且多的dex时造成堆溢出
                        .setOnProgress(new DexFinder.OnProgress() {
                            //重写此方法以监听dex解析数量情况 可以用作通知View解析进度
                    @Override
                    public void init(int dexSize) {
                        new Handler(Looper.getMainLooper()).post(() -> loadingDialog.progressBar.setMax(dexSize));
                    }

                    @Override
                    public void parse(int progress, String dexName) {
                        new Handler(Looper.getMainLooper()).post(() -> loadingDialog.progressBar.setProgress(progress));
                    }
                }).build();//调用build方法后会开始解析 目测300MB的qq解析时间在20s内
```
第二步 分析查找  
我们可以通过jadx或者mt管理器来分析出方法内具体做了什么  
比如下面是目标apk的实现代码
```java
public class a {
    private final String s;
    private String b() {
        return PathTool.getDataPath() + "/data/simple/" + s;
    }
}
```  
很多地方 比如类名方法已经经过混淆 使得我们难以hook以实现自适配  
下面看如何查找方法内出现的的字符串
```java
    //查找方法内包含"/data/simple/"字符串的方法
    ArrayList<Method> methodList = dexFinder.findMethodString("/data/simple/");
    //findMethodString返回的是一个ArrayList查找所有符合的方法 查找不到不会为null而是为size == 0
```
