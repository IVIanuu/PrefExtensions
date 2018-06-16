package com.ivianuu.prefextensions.plugin

import com.squareup.kotlinpoet.ClassName

/**
 * @author Manuel Wrage (IVIanuu)
 */
data class PreferenceDescriptor(
    val className: ClassName,
    val key: String
)

