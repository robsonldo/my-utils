package br.com.robsonldo.samplemyutils.model

import br.com.robsonldo.myutils.preference.PreferenceObject
import br.com.robsonldo.myutils.preference.annotations.ObjectSharedPreference

@ObjectSharedPreference("my_user")
class User : PreferenceObject() {

    var name: String? = null
    var lastNate: String? = null
}