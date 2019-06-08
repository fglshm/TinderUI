package com.fglshm.tinderui.util

import android.content.Context
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity

object Utils {

    object Size {

        fun getScreenWidth(context: AppCompatActivity): Int {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        fun getScreenHeight(context: AppCompatActivity): Int {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

        fun getScale(context: AppCompatActivity): Int {
            return context.resources.displayMetrics.density.toInt()
        }

    }

}