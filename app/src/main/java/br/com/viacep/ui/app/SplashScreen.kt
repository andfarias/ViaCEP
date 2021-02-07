package br.com.viacep.ui.app

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import br.com.viacep.MainActivity
import br.com.viacep.R
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class SplashScreen : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        Handler().postDelayed({
            startActivity(intentFor<MainActivity>().newTask().clearTask())
        }, 1000)
    }

}