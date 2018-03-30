package com.moji.roktsdk

/**
 * Created by moji on 30/3/18.
 */
interface RoktSDKListener {
    fun onNumberSubmitted()
    fun onError(error : Throwable)
    fun onFetchAverageNumber(average : Double)
}