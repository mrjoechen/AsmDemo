package com.chenqiao.plugin

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

class TestTransform extends Transform{


    @Override
    String getName() {
        return "TestTransformPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(@NonNull TransformInvocation transformInvocation) {
        println '--------------- LifecyclePlugin visit start --------------- '
        def startTime = System.currentTimeMillis()
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        //删除之前的输出
        if (outputProvider != null)
            outputProvider.deleteAll()
        //遍历inputs
        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider)
//                println 'directoryInput： '+directoryInput
//                println 'outputProvider： '+outputProvider
            }

        }
        def cost = (System.currentTimeMillis() - startTime) / 1000

        println '--------------- TestTransform visit end --------------- '
        println "TestTransform cost ： $cost s"
    }

    /**
     * 处理文件目录下的class文件
     */
    static void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name

                if (checkTestClassFile(name)) {
                    println '----------- deal with "Test class" file <' + name + '> -----------'
                    ClassReader classReader1 = new ClassReader(file.bytes)
                    ClassWriter classWriter1 = new ClassWriter(classReader1, ClassWriter.COMPUTE_FRAMES)
                    ClassVisitor cv = new TestClassVisitor(classWriter1)
                    classReader1.accept(cv, 0)
                    byte[] code = classWriter1.toByteArray()
                    FileOutputStream fos = new FileOutputStream(
                            file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()

                    FileOutputStream stream = new FileOutputStream(new File("/Users/chenqiao/AndroidStudioProjects/AsmDemo/plugin/src/main/groovy/com/chenqiao/plugin/Test.class"))
                    stream.write(code)
                }
            }
        }
        //处理完输入文件之后，要把输出给下一个任务
        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }


    /**
     * 检查class文件是否需要处理
     * @param fileName
     * @return
     */

    static boolean checkTestClassFile(String name) {
        println name
        //只处理需要的class文件
        return (name.endsWith(".class") && !name.startsWith("R\$")
                && !"R.class".equals(name) && !"BuildConfig.class".equals(name)
                && "TestClass.class".equals(name))
    }
}