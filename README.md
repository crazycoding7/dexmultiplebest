# Android Dex分包热修复实践

### 一、原理

- 原理是Hook了ClassLoader.pathList.dexElements[]。因为ClassLoader的findClass是通过遍历dexElements[]中的dex来寻找类的。当然为了支持4.x的机型，需要打包的时候进行插桩。

- 越靠前的Dex优先被系统使用，基于类级别的修复

- 兼容性

  在Android4.3、5.0、9.0、10.0都已经测试通过。

- 包含技术

  JVM原理、Gradle插件开发(本地发布)、字节码操作技术javassist、DexClassLoader。

  

### 二、运行环境

1. 项目目录

   | 模块     | 文件                         | 说明                                                         |
   | -------- | ---------------------------- | ------------------------------------------------------------ |
   | app      |                              |                                                              |
   |          | Cat                          | 热修复类，动态修改喵咪的叫声                                 |
   |          | MainActivity                 | 入口类                                                       |
   |          | Assert-hack.jar              | hack模块的jar,app启动时需要加载，防止CLASS_ISPREVERIFIED问题！ |
   |          | Assert-patch_dex.jar         | 热补丁备份                                                   |
   |          | build.gradle                 | 引入字节码插件，编译时自动注入`apply plugin: 'com.plugin.gradle'` |
   |          | MyApplicationPlugin          | 启动时分别加载hack.jar，和本地SdCard中的热补丁。             |
   | buildSrc |                              | 编译插件                                                     |
   |          |                              | ` ./uploadArchives`命令可以发布插件到本地release目录。第三方项目通过 `classpath 'com.plugin.demo:plugin-test:1.0.0'  apply plugin: 'com.plugin.gradle'`直接使用。 |
   |          | com.plugin.gradle.properties | 插件对外名称，里面指定入口类。                               |
   |          | MyApplicationPlugin          | 入口类                                                       |
   |          | PreDexTransform              | 字节码操作类，需要配置hack、android环境，遍历对应class文件，然后添加在构造函数中添加`"System.out.println(com.example.aidl.hack.AntilazyLoad.class);"`. |
   | hack     |                              | 提供class注入类                                              |
   |          | AntilazyLoad                 | 编译时注入该类，防止安装时候Class被打CLASS_ISPREVERIFIED标签而导致报错。 |
   | release  |                              | 本地插件发布目录，可以通过`maven { url uri('./release') }`本地查找到。 |

2. Gradle环境

   `classpath "com.android.tools.build:gradle:3.5.2"`

   `distributionUrl=https\://services.gradle.org/distributions/gradle-5.6.4-all.zip`

   注意：如果再高的环境，会出现 `buildsrc Module`名称被系统环境保留问题，无法在项目中运行buildsrc插件代码。

3. class打包命令

   ```shell
   // 进入编译后的com目录
   jar -cvf patch.jar com
   dx --dex --output=patch_dex.jar patch.jar
   ```

4. 注意

   - 如果hack模块没有build文件

     ```shell
     cd hack
     gradle assembleDebug # 手动编译
     ```

   - 项目报错

     注释掉build.gradle插件模块，即`//apply plugin: 'com.plugin.gradle'`。然后在运行。也可以在Application中关闭热加载代码，让项目先跑起来。

### 三、效果图

热修复前：

<img src="./images/old.jpeg" style="zoom:30%;" />

热修复后：

<img src="./images/new.jpeg" style="zoom:30%;" />