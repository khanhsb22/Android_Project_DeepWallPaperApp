package khanhle.imageapp.repository

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import khanhle.imageapp.R

class DialogCheckConnectInternet {

    fun showDialog(context: Context) {

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true) // true or false to close dialog when touch in screen
        dialog.setContentView(R.layout.connect_internet_dialog)

        var text_ok_internet_dialog = dialog.findViewById(R.id.text_ok_internet_dialog) as TextView

        text_ok_internet_dialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        var window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}