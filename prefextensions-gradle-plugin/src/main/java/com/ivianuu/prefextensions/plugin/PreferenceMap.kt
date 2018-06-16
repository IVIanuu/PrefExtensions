package com.ivianuu.prefextensions.plugin

import com.squareup.kotlinpoet.ClassName

/**
 * @author Manuel Wrage (IVIanuu)
 */
object PreferencesMap {

    private val TYPE_MAP = mapOf(
        "CheckBoxPreference" to ClassName("android.support.v7.preference",
            "CheckBoxPreference"),
        "EditTextPreference" to ClassName("android.support.v7.preference",
            "EditTextPreference"),
        "ListPreference" to ClassName("android.support.v7.preference",
            "ListPreference"),
        "MultiSelectListPreference" to ClassName("android.support.v14.preference",
            "MultiSelectListPreference"),
        "PreferenceCategory" to ClassName("android.support.v7.preference",
            "PreferenceCategory"),
        "Preference" to ClassName("android.support.v7.preference",
            "Preference"),
        "SwitchPreference" to ClassName("android.support.v14.preference",
            "SwitchPreference")
    )

    fun get(type: String): ClassName? {
        return TYPE_MAP[type] ?: try {
            ClassName.bestGuess(type)
        } catch (e: Exception) {
            null
        }
    }

}