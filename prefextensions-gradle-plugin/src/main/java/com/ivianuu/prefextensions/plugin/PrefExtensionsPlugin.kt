package com.ivianuu.prefextensions.plugin

import com.android.build.gradle.*
import com.android.build.gradle.api.BaseVariant
import com.google.common.base.CaseFormat
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * @author Manuel Wrage (IVIanuu)
 */
class PrefExtensionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.all {
            when (it) {
                is FeaturePlugin -> {
                    project.extensions.getByType(FeatureExtension::class.java).run {
                        generatePreferenceExtensions(project, featureVariants)
                        generatePreferenceExtensions(project, libraryVariants)
                    }
                }
                is LibraryPlugin -> {
                    project.extensions.getByType(LibraryExtension::class.java).run {
                        generatePreferenceExtensions(project, libraryVariants)
                    }
                }
                is AppPlugin -> {
                    project.extensions.getByType(AppExtension::class.java).run {
                        generatePreferenceExtensions(project, applicationVariants)
                    }
                }
            }
        }
    }

    private fun generatePreferenceExtensions(
        project: Project,
        variants: DomainObjectSet<out BaseVariant>
    ) {
        println("generate for $project")

        variants.all { variant ->
            println("generate for variant $variant")

            val outputDir = project.buildDir.resolve(
                "generated/source/prefextensions/${variant.dirName}/")

            try {
                outputDir.deleteRecursively()
            } catch (e: Exception) {
            }

            val task = project.tasks.create(
                "generate${variant.name.capitalize()}PreferenceExtensions")

            task.outputs.dir(outputDir)
            task.outputs.upToDateWhen { false }

            variant.registerJavaGeneratingTask(task, outputDir)

            variant.outputs.all { output ->
                val processResources = output.processResources
                task.dependsOn(processResources)

                task.doLast {
                    val xmlFolder = File(project.projectDir.absolutePath + "/src/main/res/xml")

                    val xmlFiles = xmlFolder.listFiles()?.toList() ?: return@doLast

                    xmlFiles
                        .map {
                            val fileName = CaseFormat.LOWER_UNDERSCORE
                                .to(CaseFormat.UPPER_CAMEL,
                                    it.name.replace(".xml", "")) + "Ext"

                            val packageName =
                                "com.ivianuu.prefextensions.${it.name.replace(".xml", "")}"

                            PreferenceExtensionDescriptor(
                                packageName,
                                fileName,
                                parsePreferenceXml(it),
                                false
                            )
                        }
                        .filter { it.preferences.isNotEmpty() }
                        .map(::PreferenceExtensionGenerator)
                        .map(PreferenceExtensionGenerator::generate)
                        .forEach { it.writeTo(outputDir) }
                }
            }
        }
    }

    private fun parsePreferenceXml(file: File): Set<PreferenceDescriptor> {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val document = try {
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            documentBuilder.parse(file)
        } catch (e: Exception) {
            null
        } ?: return emptySet()

        val prefs = mutableSetOf<PreferenceDescriptor>()
        parsePreferences(document, prefs)
        return prefs
    }

    private fun parsePreferences(node: Node, prefs: MutableSet<PreferenceDescriptor>) {
        for (i in 0 until node.childNodes.length) {
            val childNote = node.childNodes.item(i)
            val pref = parsePreference(childNote)
            if (pref != null) {
                prefs.add(pref)
            }

            parsePreferences(childNote, prefs)
        }
    }

    private fun parsePreference(node: Node): PreferenceDescriptor? {
        return try {
            val nodeName = node.nodeName
            val prefName = PreferencesMap.get(nodeName) ?: return null
            val keyAttr = node.attributes?.getNamedItem("android:key")?.nodeValue
                    ?: return null
            PreferenceDescriptor(prefName, keyAttr)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}