package com.example.simplyamplify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.amazonaws.mobile.client.*
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.amplify.generated.graphql.CreateUserMutation
import type.CreateUserInput
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response


class MainActivity : AppCompatActivity() {

    // graphql
    private var mAWSAppSyncClient: AWSAppSyncClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ログイン画面
        initializeAWSMobileClient()

        // サインアウトボタンの定義
        val buttonSignout = findViewById(R.id.buttonSignout) as Button
        buttonSignout.setOnClickListener{
            this.runMutation()
            AWSMobileClient.getInstance().signOut()
            //AWSMobileClient.getInstance().signOut(SignOutOptions.builder().signOutGlobally(true).build())
            initializeAWSMobileClient()
            //Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show()
        }

        // graphql
        mAWSAppSyncClient = AWSAppSyncClient.builder()
            //.context(getApplicationContext())
            .context(applicationContext)
            //.awsConfiguration(AWSConfiguration(getApplicationContext()))
            .awsConfiguration(AWSConfiguration(applicationContext))
            .build()
    }

    // ログイン初期化
    fun initializeAWSMobileClient(){
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

    // dbへデータ追加
    fun runMutation() {
        val createUserInput =
            CreateUserInput.builder()
                .name("John")
                .build()

        mAWSAppSyncClient!!.mutate(CreateUserMutation.builder().input(createUserInput).build())
            .enqueue(mutationCallback)
    }

    // コールバックの定義
    private val mutationCallback = object: GraphQLCall.Callback<CreateUserMutation.Data>() {

        override fun onResponse(response: Response<CreateUserMutation.Data>) {
            //Log.i("Results", "Added Todo")
            Log.i("Results", response.data().toString())
            //Toast.makeText(applicationContext, "Good", Toast.LENGTH_SHORT).show()
        }

        override fun onFailure(e: ApolloException) {
            Log.e("Error", e.toString())
        }
    }


}
