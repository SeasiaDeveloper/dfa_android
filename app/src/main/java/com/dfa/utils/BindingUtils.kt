package com.dfa.utils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

object BindingUtils {

    @JvmStatic
    fun shortText(s : String, size : Int) : String {
        var returnText = s
        if (s.length > size) {
            returnText = s.substring(0, size) + "..."
        }

        return returnText
    }




    @JvmStatic
    fun convertDateIntoDayMonthDate(date : String?, format : String) : String {
        // Display a date in day, month, year format
        var today = ""
        if (!TextUtils.isEmpty(date)) {
            val datee = Date.parse(date)

            when (format) {
                "DayMonthDate" -> {
                    val formatter = SimpleDateFormat("EEEE, MMMM dd")
                    today = formatter.format(datee)
                }

                "Day" -> {
                    val formatter = SimpleDateFormat("EEEE")
                    today = formatter.format(datee)
                }
                "Date" -> {
                    val formatter = SimpleDateFormat("dd")
                    today = formatter.format(datee)
                }
                "Month" -> {
                    val formatter = SimpleDateFormat("MMM")
                    today = formatter.format(datee)
                }
                "MonthDate" -> {
                    val formatter = SimpleDateFormat("MMM dd")
                    today = formatter.format(datee)
                }
            }
        }
        return today
    }


}