package com.test.myapplication

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 入口类，字节码操作class文件,在构造函数添加 "System.out.println(com.example.aidl.hack.AntilazyLoad.class);"
 */
class MyApplicationPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // 实现插件
        println "hello, this is test plugin! 我是插件"
        project.task('plugin-task') {
            println "hello, this is a test task!..."
        }

        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new PreDexTransform(project))
    }
}