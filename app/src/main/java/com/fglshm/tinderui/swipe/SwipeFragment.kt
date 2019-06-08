package com.fglshm.tinderui.swipe

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fglshm.tinderui.R
import com.fglshm.tinderui.base.BaseFragment
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.android.synthetic.main.fragment_swipe.*
import java.util.concurrent.atomic.AtomicInteger
import android.widget.LinearLayout

class SwipeFragment : BaseFragment() {

    private val imageList = listOf(
        R.drawable.person1,
        R.drawable.person2,
        R.drawable.person4,
        R.drawable.person5,
        R.drawable.person6,
        R.drawable.person7,
        R.drawable.person9,
        R.drawable.person10,
        R.drawable.person11
    )

    private val nameList = listOf(
        "Debbie Miller Wilson",
        "Darlene N. Riley",
        "Natasha Morgan Christian",
        "Eloise Erickson Dawkins",
        "Fatma Lucia Holbrook",
        "Tin Gill Stanford",
        "Marissa Al Brady",
        "Sunny Davidson Curran",
        "Mina Morrison Briones"
    )

    private val descList = listOf(
        "Hello, my name is Debbie Miller Wilson",
        "Hello, my name is Darlene N. Riley",
        "Hello, my name is Natasha Morgan Christian",
        "Hello, my name is Eloise Erickson Dawkins",
        "Hello, my name is Fatma Lucia Holbrook",
        "Hello, my name is Tin Gill Stanford",
        "Hello, my name is Marissa Al Brady",
        "Hello, my name is Sunny Davidson Curran",
        "Hello, my name is Mina Morrison Briones"
    )

    private val atomicInteger = AtomicInteger()

    override val logTag: String = SwipeFragment::class.java.simpleName
    override fun getLayout(): Int = R.layout.fragment_swipe

    private val cardContainer by lazy { card_container }
    private val likeCard by lazy { card_like_fragment_swipe }
    private val dislikeCard by lazy { card_dislike_fragment_swipe }

    private var direction: Int = 0
    private var initX: Float = 0F
    private var initY: Float = 0F
    private var diffX: Float = 0F
    private var diffY: Float = 0F

    private val cardTouchListener = View.OnTouchListener { card, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleActionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                handleActionMove(event, card)
            }
            MotionEvent.ACTION_UP -> {
                handleActionUp(card)
            }
        }
        true
    }

    private fun handleActionDown(event: MotionEvent) {
        initX = event.rawX
        initY = event.rawY
    }

    private fun handleActionMove(event: MotionEvent, card: View) {
        diffX = event.rawX.minus(initX)
        diffY = event.rawY.minus(initY)
        val scale = if (Math.abs(diffX) < 350) {
            0.95.plus(0.05 * Math.abs(diffX) / 350)
        } else {
            1.0
        }
        val nextCard = cardContainer.getChildAt(cardContainer.childCount.minus(2))
        nextCard.scaleX = scale.toFloat()
        nextCard.scaleY = scale.toFloat()
        direction = if (diffX > 0) 1 else -1
        card.translationX = diffX
        card.translationY = diffY
        val alpha = Math.abs(diffX / 350)
        val angle = if (direction > 0) { // to right
            card.text_like_card_item.alpha = alpha
            -Math.PI * 2F * direction * diffX / 180
        } else { // to left
            card.text_dislike_card_item.alpha = alpha
            Math.PI * 2F * direction * diffX / 180
        }
        card.rotation = angle.toFloat()
    }

    private fun handleActionUp(card: View) {
        if (Math.abs(diffX) > 350) {
            card.animate().apply {
                duration = 100
                translationX(1500F * direction)
            }.setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    cardContainer.removeViewAt(cardContainer.childCount.minus(1))
                    cardContainer.getChildAt(cardContainer.childCount.minus(1)).setOnTouchListener(cardTouchListener)
                    cardContainer.addView(createCardView(), 0)
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            }).start()
        } else {
            card.animate().apply {
                duration = 400
                translationX(0F)
                translationY(0F)
                rotation(0F)
                interpolator = OvershootInterpolator(1.75F)
            }.start()
            card.text_like_card_item.animate().apply {
                duration = 200
                alpha(0F)
            }
            card.text_dislike_card_item.animate().apply {
                duration = 200
                alpha(0F)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCards()
        setClickListener()
    }

    private fun setCards() {
        repeat(3) {
            cardContainer.addView(createCardView(), 0)
        }
    }

    private fun setClickListener() {
        likeCard.setOnClickListener {
            val topCard = cardContainer.getChildAt(cardContainer.childCount.minus(1))
            topCard.text_like_card_item.animate().apply {
                duration = 200
                alpha(1F)
            }.setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    topCard.animate().apply {
                        duration = 200
                        translationX(1500F)
                    }.setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            cardContainer.removeViewAt(cardContainer.childCount.minus(1))
                            cardContainer.getChildAt(cardContainer.childCount.minus(1))
                                .setOnTouchListener(cardTouchListener)
                            cardContainer.addView(createCardView(), 0)
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {
                            val nextCard = cardContainer.getChildAt(cardContainer.childCount.minus(2))
                            nextCard.animate().apply {
                                duration = 200
                                scaleX(1.0F)
                                scaleY(1.0F)
                            }.start()
                        }
                    }).start()
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            }).start()
        }
        dislikeCard.setOnClickListener {
            val topCard = cardContainer.getChildAt(cardContainer.childCount.minus(1))
            topCard.text_dislike_card_item.animate().apply {
                duration = 200
                alpha(1F)
            }.setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    topCard.animate().apply {
                        duration = 200
                        translationX(-1500F)
                    }.setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            cardContainer.removeViewAt(cardContainer.childCount.minus(1))
                            cardContainer.getChildAt(cardContainer.childCount.minus(1))
                                .setOnTouchListener(cardTouchListener)
                            cardContainer.addView(createCardView(), 0)
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {
                            val nextCard = cardContainer.getChildAt(cardContainer.childCount.minus(2))
                            nextCard.animate().apply {
                                duration = 200
                                scaleX(1.0F)
                                scaleY(1.0F)
                            }.start()
                        }
                    }).start()
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            }).start()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    private fun createCardView(): View {
        val card = LayoutInflater.from(mContext).inflate(R.layout.card_item, null) as CardView
        val imageWidth = screenWidth
        val imageHeight = screenHeight
        card.image_profile_card_item.apply {
            requestLayout()
            layoutParams.width = imageWidth
            layoutParams.height = imageHeight
            Glide.with(mContext).load(imageList[atomicInteger.get() % 9])
                .apply(RequestOptions().override(imageWidth, imageHeight)).centerCrop().into(this)
        }
        card.text_description_card_item.text = descList[atomicInteger.get() % 9]
        card.text_name_card_item.text = nameList[atomicInteger.getAndIncrement() % 9]
        if (atomicInteger.get() != 1) {
            card.scaleX = 0.95F
            card.scaleY = 0.95F
        } else {
            card.setOnTouchListener(cardTouchListener)
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val cardButtonWidth = likeCard.layoutParams.width
        params.marginStart = 16.times(scale)
        params.marginEnd = 16.times(scale)
        params.bottomMargin = cardButtonWidth.times(2)
        params.topMargin = 16.times(scale)
        card.layoutParams = params
        return card
    }

}