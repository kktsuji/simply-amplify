package com.example.simplyamplify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.util.Log
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.fragment.app.FragmentActivity
import com.amazonaws.mobile.client.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AWSMobileClient.getInstance().initialize(applicationContext, object : Callback<UserStateDetails> {
            override fun onResult(userStateDetails: UserStateDetails) {
                when (userStateDetails.userState) {
                    UserState.SIGNED_IN -> runOnUiThread {
                        val textView = findViewById(R.id.myTextView) as TextView
                        textView.text = "Logged IN"
                    }
                    UserState.SIGNED_OUT -> runOnUiThread {
                        val textView = findViewById(R.id.myTextView) as TextView
                        textView.text = "Logged OUT"

                        AWSMobileClient.getInstance().showSignIn(
                            this@MainActivity,
                            object : Callback<UserStateDetails> {
                                override fun onResult(result: UserStateDetails) {
                                    //Log.d(FragmentActivity.TAG, "onResult: " + result.userState)
                                }

                                override fun onError(e: Exception) {
                                    //Log.e(FragmentActivity.TAG, "onError: ", e)
                                }
                            })
                    }
                    else -> AWSMobileClient.getInstance().signOut()
                }
            }

            override fun onError(e: Exception) {
                Log.e("INIT", e.toString())
            }
        })
    }
}
