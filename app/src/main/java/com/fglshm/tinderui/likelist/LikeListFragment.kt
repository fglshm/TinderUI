package com.fglshm.tinderui.likelist

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.fglshm.tinderui.R
import com.fglshm.tinderui.base.BaseFragment
import com.fglshm.tinderui.extension.setInvisible
import com.fglshm.tinderui.extension.setVisible
import com.fglshm.tinderui.main.MainActivity
import com.fglshm.tinderui.model.Person
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_like_list.*

class LikeListFragment : BaseFragment(), LikeListContract.View {

    override val logTag: String = LikeListFragment::class.java.simpleName
    override fun getLayout(): Int = R.layout.fragment_like_list

    private val backgroundView by lazy { view_bg }
    private val behavior by lazy { BottomSheetBehavior.from(bottom_sheet) }
    private val backButton by lazy { image_button_back_fragment_like_list }
    private val recyclerView by lazy { recycler_fragment_like_list }
    private val mAdapter by lazy { LikeListAdapter() }
    private val progressBar by lazy { progress }

    private val presenter by lazy { LikeListPresenter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setListener()
        setCallback()
        presenter.onViewCreated()
    }

    private fun init() {
        recyclerView.adapter = mAdapter
        behavior.peekHeight = screenHeight
    }

    private fun setCallback() {
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

    private fun setListener() {
        backButton.setOnClickListener {
            (mContext as MainActivity).popCurrentFragment()
        }
    }


    override fun showProgress() {
        progressBar.setVisible()
    }

    override fun hideProgress() {
        progressBar.setInvisible()
    }

    override fun addPersons(what: RealmResults<Person>) {
        mAdapter.addAll(what)
    }

    override fun showEmptyResult() {
        mAdapter.isEmpty = true
        mAdapter.addEmptyRow()
    }
}