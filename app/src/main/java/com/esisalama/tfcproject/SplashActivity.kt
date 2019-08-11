package com.esisalama.tfcproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.InvocationHandler

class SplashActivity :AppCompatActivity(){
    private val splashTime =  3000L
    private lateinit var myHandler: Handler

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        myHandler = Handler()

        myHandler.postDelayed({
            goToMainActivity()
        },splashTime)
    }


}