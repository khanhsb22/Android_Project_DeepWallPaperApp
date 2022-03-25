package khanhle.imageapp.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import khanhle.imageapp.R

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splass_screen)

        Handler().postDelayed(object : Runnable {
            override fun run() {
                startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                finish()
            }

        }, SPLASH_TIME)

    }

}