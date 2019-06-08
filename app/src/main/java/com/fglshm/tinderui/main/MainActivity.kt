package com.fglshm.tinderui.main

import android.os.Bundle
import com.fglshm.tinderui.R
import com.fglshm.tinderui.base.BaseActivity
import com.fglshm.tinderui.extention.transact
import com.fglshm.tinderui.swipe.SwipeFragment

class MainActivity : BaseActivity() {

    override val logTag: String = MainActivity::class.java.simpleName
    override fun getLayout(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSwipeFragment()
    }

    private fun showSwipeFragment() {
        supportFragmentManager.transact {
            replace(R.id.container, SwipeFragment())
        }
    }

}

