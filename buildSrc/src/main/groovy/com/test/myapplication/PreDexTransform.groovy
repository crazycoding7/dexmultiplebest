package com.test.myapplication

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class PreDexTransform extends Transform {

    Project project
    // 添加构造，为了方便从plugin中拿到project对象，待会有用
    PreDexTransform(Project project) {
        this.project = project
        println "PreDexTransform  .."
        // 获取到hack module的debug目录，也就是Antilazy.class所在的目录
        def libPath = project.project(':hack').buildDir.absolutePath.concat("/intermediates/javac/debug/classes")

        println "获取hack 文件  .." + libPath
        Inject.appendClassPath(libPath) // 加载AntilazyLoad.class
        Inject.appendClassPath("/Users/alex/Library/Android/sdk/platforms/android-29/android.jar") // 加载环境

    }

    @Override
    String getName() {
        return "preDex"
    }

    // 指定input的类型
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transfrom的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {

        // inputs就是输入文件的集合
        // outputProvider可以获取outputs的路径
        // Transfrom的inputs有两种类型，一种是目录，一种是jar包，要分开遍历

        println ".......开始转换"
        // 遍历transfrom的inputs
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                Inject.injectDir(directoryInput.file.absolutePath)

                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                println ".......dest。。。。。。。。。。。。。" + dest

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput ->


                //TODO 注入代码
                String jarPath = jarInput.file.absolutePath;
                String projectName = project.rootProject.name;
                if (jarPath.endsWith("classes.jar")
                        && jarPath.contains("exploded-aar\\" + projectName)
                        // hotpatch module是用来加载dex，无需注入代码
                        && !jarPath.contains("exploded-aar\\" + projectName + "\\hotpatch")) {
                    Inject.injectJar(jarPath)
                }

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }


    }
}