package khanhle.imageapp.repository

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import io.paperdb.Paper
import khanhle.imageapp.R

class DialogUpgradePremium {

    private var premium = false

    fun showDialog(context: Context) {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.upgrade_premium_dialog)

        Paper.init(context)
        premium = Paper.book().read("premium_state", false)

        var text_upgrade = dialog.findViewById(R.id.text_upgrade) as TextView
        var text_calcel_upgrade = dialog.findViewById(R.id.text_calcel_upgrade) as TextView

        text_calcel_upgrade.setOnClickListener {
            dialog.dismiss()
        }

        text_upgrade.setOnClickListener {
            if (!premium) {
                Paper.book().write("premium_state", true)
                Toast.makeText(
                    context,
                    "Now that the premium package has been upgraded, please restart the application to experience ! ",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "You have been premium user.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        dialog.show()

        var window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}