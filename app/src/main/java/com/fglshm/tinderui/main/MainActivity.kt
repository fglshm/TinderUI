package com.fglshm.tinderui.main

import android.os.Bundle
import android.util.Log
import com.fglshm.tinderui.R
import com.fglshm.tinderui.base.BaseActivity
import com.fglshm.tinderui.extension.transact
import com.fglshm.tinderui.likelist.LikeListFragment
import com.fglshm.tinderui.swipe.SwipeFragment

class MainActivity : BaseActivity(), SwipeFragment.OnLikeListButtonClickListener, MainContract.View {

    override val logTag: String = MainActivity::class.java.simpleName
    override fun getLayout(): Int = R.layout.activity_main

    private val containerRes = R.id.container
    private lateinit var swipeFragment: SwipeFragment
    private lateinit var likeListFragment: LikeListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showSwipeFragment()
    }

    private fun showSwipeFragment() {
        swipeFragment = SwipeFragment()
        swipeFragment.listener = this
        supportFragmentManager.transact {
            replace(containerRes, swipeFragment)
        }
    }

    override fun onLikeListButtonClick() {
        showLikeListFragment()
    }

    private fun showLikeListFragment() {
        likeListFragment = LikeListFragment()
        supportFragmentManager.transact {
            supportFragmentManager.transact {
                setCustomAnimations(R.anim.bottom_to_top, R.anim.stay, R.anim.stay, R.anim.top_to_bottom)
                add(containerRes, likeListFragment)
                addToBackStack(null)
            }
        }
    }

    override fun popCurrentFragment() {
        supportFragmentManager.popBackStack()
    }

}

