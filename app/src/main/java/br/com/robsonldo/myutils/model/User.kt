package br.com.robsonldo.myutils.model

import br.com.robsonldo.library.preference.PreferenceObject
import br.com.robsonldo.library.preference.annotations.ObjectSharedPreference

@ObjectSharedPreference("my_user")
class User : PreferenceObject() {

    var name: String? = null
    var lastNate: String? = null
}