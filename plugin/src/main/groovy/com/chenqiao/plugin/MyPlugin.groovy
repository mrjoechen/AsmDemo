package com.chenqiao.plugin

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


import static org.objectweb.asm.ClassReader.EXPAND_FRAMES


class MyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //registerTransform
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new LifecycleTransform())
//        android.registerTransform(new TestTransform())
    }

}