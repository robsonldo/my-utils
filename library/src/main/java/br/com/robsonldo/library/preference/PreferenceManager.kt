package br.com.robsonldo.library.preference

import android.content.Context
import android.content.SharedPreferences
import br.com.robsonldo.library.preference.annotations.ObjectSharedPreference
import br.com.robsonldo.library.preference.interfaces.IPreferenceManager
import com.google.gson.Gson
import java.lang.reflect.Field

class PreferenceManager private constructor() {

    private val gson = Gson()

    companion object {
        private var instance: PreferenceManager? = null
        private lateinit var context: Context
        private lateinit var iPreferenceManager: IPreferenceManager<*>
        private lateinit var preference: SharedPreferences

        fun getInstance(): PreferenceManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) instance = PreferenceManager()
                }
            }

            return instance!!
        }

        fun init(context: Context, IPreferenceManager: IPreferenceManager<*>) {
            this.context = context
            this.iPreferenceManager = IPreferenceManager

            preference = this.context.getSharedPreferences(IPreferenceManager.preferenceName(),
                Context.MODE_PRIVATE)

            getInstance().load()
        }
    }

    fun load() {
        fieldValidForEach { field ->
            val objectSharedPreference = field.type.getAnnotation(ObjectSharedPreference::class.java)

            val any = if (objectSharedPreference!!.initialize) {
                field.type.newInstance() as PreferenceObject
            } else null

            field.set(iPreferenceManager, getDefined(objectSharedPreference, field.type, any))
        }
    }

    fun <T: PreferenceObject> getDefined(objectSharedPreference: ObjectSharedPreference,
                                         clazz: Class<*>, obj: T? = null): T? {

        return get(objectSharedPreference.value, clazz) ?: obj
    }

    fun <T: PreferenceObject> add(objectSharedPreference: ObjectSharedPreference, obj: T): Boolean {
        return add(objectSharedPreference.value, obj)
    }

    fun <T: PreferenceObject> remove(objectSharedPreference: ObjectSharedPreference,
                                     clazz: Class<T>): Boolean {

        return remove(objectSharedPreference.value, !objectSharedPreference.initialize,
            clazz.newInstance())
    }

    private fun <T: PreferenceObject> add(key: String, obj: T): Boolean {
        val editor = preference.edit()
        val json: String = gson.toJson(obj)

        editor.putString(key, json)
        attachObjectReference(obj, false)

        return editor.commit()
    }

    private fun <T: PreferenceObject> remove(key: String, removeAttachObject: Boolean,
                                             obj: T): Boolean {

        val editor = preference.edit()
        editor.remove(key)

        attachObjectReference(obj, removeAttachObject)
        return editor.commit()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> get(key: String, clazz: Class<*>): T? {
        val json: String? = preference.getString(key, "")
        if (json == null || json == "") return null

        return gson.fromJson(json, clazz) as T
    }

    private fun <T: PreferenceObject> attachObjectReference(obj: T, isRemove: Boolean) {
        fieldValidForEach { field ->
            if (field.type.isInstance(obj)) {
                field.set(iPreferenceManager, if (isRemove) null else obj)
            }
        }
    }

    private inline fun fieldValidForEach(action: (field: Field) -> Unit) {
        val fields = iPreferenceManager.javaClass.declaredFields
        loop@ for (field in fields) {
            when {
                field.type.getAnnotation(ObjectSharedPreference::class.java) == null -> continue@loop
                else -> {
                    field.isAccessible = true
                    action(field)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: IPreferenceManager<*>> i(clazz: Class<T>) = iPreferenceManager as T
    fun getContext() = context
}