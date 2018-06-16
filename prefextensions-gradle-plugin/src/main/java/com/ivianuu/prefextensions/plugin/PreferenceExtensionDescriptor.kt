package com.ivianuu.prefextensions.plugin

/**
 * @author Manuel Wrage (IVIanuu)
 */
data class PreferenceExtensionDescriptor(
    val packageName: String,
    val fileName: String,
    val preferences: Set<PreferenceDescriptor>,
    val generatePrefContainer: Boolean
)