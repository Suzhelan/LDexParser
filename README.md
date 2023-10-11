# LDexParser
Android Dex parser
Features currently supported 
· Find the string that occurs within a method
· Waiting for more features to be perfected
Usage:
Add Dependencies:

implementation 'com.github.Suzhelan:LDexParser:v0.17'

And then in the code:
DexFinder finder = DexFinder.builder(ClassLoader,new File(apkPath));

I'm working on perfecting the documentation and optimizing this.
