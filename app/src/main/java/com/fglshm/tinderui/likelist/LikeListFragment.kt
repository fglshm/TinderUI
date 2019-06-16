package com.fglshm.tinderui.likelist

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.fglshm.tinderui.R
import com.fglshm.tinderui.base.BaseFragment
import com.fglshm.tinderui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_like_list.*

class LikeListFragment : BaseFragment() {

    override val logTag: String = LikeListFragment::class.java.simpleName
    override fun getLayout(): Int = R.layout.fragment_like_list

    private val bottomSheetView by lazy { bottom_sheet }
    private val backgroundView by lazy { view_bg }
    private val behavior by lazy { BottomSheetBehavior.from(bottom_sheet) }
    private val backButton by lazy { image_button_back_fragment_like_list }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        behavior.peekHeight = screenHeight

        backButton.setOnClickListener {
            (mContext as MainActivity).popCurrentFragment()
        }
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, slide: Float) {
                val alpha = 1.minus(Math.abs(slide)) * 9 / 10
                backgroundView.alpha = alpha
                showLog(backgroundView.alpha)
            }

            override fun onStateChanged(p0: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        (mContext as MainActivity).popCurrentFragment()
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        showLog("STATE_DRAGGING")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        showLog("STATE_EXPANDED")
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        showLog("STATE_HALF_EXPANDED")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        showLog("STATE_COLLAPSED")
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        showLog("STATE_SETTLING")
                    }
                    else -> {

                    }
                }
            }
        })
    }


}