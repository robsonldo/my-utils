package br.com.robsonldo.samplemyutils.preference

import br.com.robsonldo.myutils.preference.interfaces.IPreferenceManager
import br.com.robsonldo.samplemyutils.model.Setting
import br.com.robsonldo.samplemyutils.model.User

class MyPreference : IPreferenceManager<MyPreference> {

    var user: User? = null
    var setting: Setting? = null

    override fun preferenceName() = "My_Preference_=))"
}