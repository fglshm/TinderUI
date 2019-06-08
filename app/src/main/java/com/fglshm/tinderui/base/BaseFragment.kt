package com.fglshm.tinderui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fglshm.tinderui.util.Utils

abstract class BaseFragment : Fragment() {

    abstract val logTag: String
    abstract fun getLayout(): Int
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    protected var scale = 0
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        screenWidth = Utils.Size.getScreenWidth(mContext as AppCompatActivity)
        screenHeight = Utils.Size.getScreenHeight(mContext as AppCompatActivity)
        scale = Utils.Size.getScale(mContext as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            getLayout(),
            container,
            false
        )
    }

    fun showLog(text: Any?) = Log.d(logTag, text?.toString() ?: "ERROR: text is null")

}