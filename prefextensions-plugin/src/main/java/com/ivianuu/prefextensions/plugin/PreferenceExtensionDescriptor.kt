package com.ivianuu.prefextensions.plugin

import com.squareup.kotlinpoet.*
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
data class PreferenceExtensionDescriptor(
    val packageName: String,
    val fileName: String,
    val preferences: Set<PreferenceDescriptor>
)