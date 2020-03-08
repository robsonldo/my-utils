package br.com.robsonldo.myutils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.robsonldo.myutils.animation.enums.TransitionAnimation
import br.com.robsonldo.myutils.preference.PreferenceManager
import br.com.robsonldo.myutils.utils.UtilsActivity
import br.com.robsonldo.myutils.view.EditTextEasyView
import br.com.robsonldo.myutils.model.Setting
import br.com.robsonldo.myutils.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Or PreferenceManager.getInstance().i(MyPreference::class.java).user
        var user: User? = PreferenceManager.getInstance().iSelf().user
        var setting: Setting? = PreferenceManager.getInstance().iSelf().setting

        if (user == null) {
            user = User().apply {
                name = "Robson"
                lastNate = "Oliveira"
                saveLocal()
            }
        } else {
            user.removeLocal()
        }

        if (setting == null) {
            setting = Setting().apply {
                acceptNotification = true
                saveLocal()
            }
        } else {
            setting.removeLocal()
        }

        enter.setOnClickListener {
            if (validate()) {
                UtilsActivity.goToActivity(
                    activity = this, destination = MainActivity::class.java,
                    animation = TransitionAnimation.FROM_LEFT_TO_RIGHT, isFinish = true
                )
            }
        }
    }

    private fun validate(): Boolean {
        return EditTextEasyView.validateChields(findViewById(R.id.root))
    }
}