package br.com.robsonldo.samplemyutils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.robsonldo.myutils.animation.enums.TransitionAnimation
import br.com.robsonldo.myutils.preference.PreferenceManager
import br.com.robsonldo.myutils.utils.UtilsActivity
import br.com.robsonldo.myutils.view.EditTextEasyView
import br.com.robsonldo.samplemyutils.model.Setting
import br.com.robsonldo.samplemyutils.model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Or PreferenceManager.getInstance().i(MyPreference::class.java).user
        var user: User? = PreferenceManager.getInstance().iSelf().user
        var setting: Setting? = PreferenceManager.getInstance().iSelf().setting

        user?.removeLocal() ?: run {
            user = User().apply {
                name = "Robson"
                lastNate = "Oliveira"
                saveLocal()
            }
        }

        setting?.removeLocal() ?: run {
            setting = Setting().apply {
                acceptNotification = true
                saveLocal()
            }
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
        return EditTextEasyView.validateChields(root)
    }
}