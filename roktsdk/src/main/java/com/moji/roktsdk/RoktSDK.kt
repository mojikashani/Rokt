package com.moji.roktsdk

import android.content.Context
import android.os.AsyncTask
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.util.*


/**
 * Created by moji on 30/3/18.
 */
class RoktSDK(private val context: Context, private val listener : RoktSDKListener) {
    companion object {
        const val SHARED_PREFS_FILE = "shared_prefs_file"
        const val KEY_NUMBER_LIST = "key_number_list"
    }

    fun addNumber(number : Double){
        SubmitNumberTask().execute(number)

    }

    fun askForAverage() {
        GetAverageTask().execute()
    }

    private inner class GetAverageTask : AsyncTask<Unit, Int, AsyncTaskResult>() {
        override fun doInBackground(vararg args: Unit): AsyncTaskResult {

            var result :AsyncTaskResult = AsyncTaskResult(0.0, null)
            try {
                val connection :HttpURLConnection  = (URL("https://roktcdn1.akamaized.net/store/test/android/prestored_scores.json")).openConnection() as HttpURLConnection
                connection.requestMethod ="GET"
                connection.connect()
                val input : InputStream = connection.getInputStream()
                val r = BufferedReader(InputStreamReader(input) as Reader?)
                val inputString = r.readLine()
                val newInputString = inputString.subSequence(1,inputString.length-1)
                val  numberList = newInputString.split(",")

                val prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
                val set : MutableSet<String> = prefs.getStringSet(KEY_NUMBER_LIST, mutableSetOf())
                var average = 0.0
                var summation = 0.0
                if (set.isNotEmpty()) {
                    for (number in set) {
                        summation += number.toDouble()
                    }
                }
                for(number in numberList){
                    summation+=number.toDouble()
                }

                average = summation / (set.size+numberList.size)
                result = AsyncTaskResult(average, null)
            }catch (ex : Exception){
                result = AsyncTaskResult(0.0, ex)
            }finally {
                return result
            }
        }

        override fun onPostExecute(result: AsyncTaskResult) {
            result.error?.let {
                listener.onError(result.error)
            } ?: run {
                listener.onFetchAverageNumber(result.average)
            }
        }
    }

    private inner class SubmitNumberTask : AsyncTask<Double, Int, AsyncTaskResult>() {
        override fun doInBackground(vararg args: Double?): AsyncTaskResult {
            var result :AsyncTaskResult = AsyncTaskResult(0.0, null)
            try {
                if(args.isEmpty()){
                    throw Exception("No Argument is passed to the class")
                }
                val number = args[0]
                val prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
                var set : MutableSet<String> = prefs.getStringSet(KEY_NUMBER_LIST, mutableSetOf())
                set.add(number.toString())
                val editor = prefs.edit()
                editor.putStringSet(KEY_NUMBER_LIST, set)
                editor.commit()
                result = AsyncTaskResult(0.0, null)
            } catch (ex: Exception) {
                result = AsyncTaskResult(0.0, ex)
            } finally {
                return result
            }
        }

        override fun onPostExecute(result: AsyncTaskResult) {
            result.error?.let {
                listener.onError(result.error)
            } ?: run {
                listener.onNumberSubmitted()
            }
        }
    }
}