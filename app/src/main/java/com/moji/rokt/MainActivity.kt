package com.moji.rokt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.moji.roktsdk.RoktSDK
import com.moji.roktsdk.RoktSDKListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RoktSDKListener {

    private val roktSDK = RoktSDK(this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onSubmitClicked(view : View){
        val numberToSubmit = editNumber.text.toString()

        if(numberToSubmit.isNotEmpty()) {
            roktSDK.addNumber(numberToSubmit.toDouble())
        }
    }

    fun onGetAverageClicked(view : View){
        roktSDK.askForAverage()
    }

    override fun onNumberSubmitted() {
        Toast.makeText(applicationContext, getString(R.string.message_number_submitted), Toast.LENGTH_LONG).show()
    }

    override fun onError(error: Throwable) {
        Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
    }

    override fun onFetchAverageNumber(average: Double) {
        txtDisplay.text = getString(R.string.message_average_display, average.toString())
    }

}
