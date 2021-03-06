package br.com.robsonldo.myutils.preference

import br.com.robsonldo.myutils.preference.annotations.ObjectSharedPreference

abstract class PreferenceObject {

    fun saveLocal(): Boolean {
        synchronized(this) {
            val objectSharedPreference =
                this.javaClass.getAnnotation(ObjectSharedPreference::class.java)

            objectSharedPreference ?: return false
            PreferenceManager.getInstance().add(objectSharedPreference, this)
            return true
        }
    }

    fun removeLocal(): Boolean {
        synchronized(this) {
            val objectSharedPreference =
                this.javaClass.getAnnotation(ObjectSharedPreference::class.java)

            objectSharedPreference ?: return false
            PreferenceManager.getInstance().remove(objectSharedPreference, this.javaClass)
            return true
        }
    }
}