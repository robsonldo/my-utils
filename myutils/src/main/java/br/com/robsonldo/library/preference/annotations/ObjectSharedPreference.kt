package br.com.robsonldo.library.preference.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class ObjectSharedPreference(val value: String, val initialize: Boolean = false)