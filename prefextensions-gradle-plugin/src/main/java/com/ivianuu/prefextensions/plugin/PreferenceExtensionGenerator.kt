package com.ivianuu.prefextensions.plugin

import com.squareup.kotlinpoet.*

/**
 * @author Manuel Wrage (IVIanuu)
 */
class PreferenceExtensionGenerator(private val descriptor: PreferenceExtensionDescriptor) {

    fun generate(): FileSpec {
        val file =
            FileSpec.builder(descriptor.packageName, descriptor.fileName)

        descriptor.preferences.forEach {
            file.addProperty(preferenceProperty(it, PREFERENCE_FRAGMENT))
            if (descriptor.generatePrefContainer) {
                file.addProperty(preferenceProperty(it, PREFERENCE_CONTAINER))
            }
        }

        return file.build()
    }

    private fun preferenceProperty(
        preference: PreferenceDescriptor,
        receiver: ClassName
    ): PropertySpec {
        return PropertySpec.builder(preference.key, preference.className)
            .receiver(receiver)
            .getter(
                FunSpec.getterBuilder()
                    .addCode(
                        CodeBlock.builder()
                            .apply {
                                if (receiver == PREFERENCE_CONTAINER) {
                                    addStatement("return preferenceScreen.findPreference(\"${preference.key}\") as %T",
                                        preference.className)
                                } else {
                                    addStatement("return findPreference(\"${preference.key}\") as %T",
                                        preference.className)
                                }
                            }
                            .build()
                    )
                    .build()
            )
            .build()
    }

}