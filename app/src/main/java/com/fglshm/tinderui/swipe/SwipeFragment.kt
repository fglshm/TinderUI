package com.fglshm.tinderui.swipe

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.fglshm.tinderui.data.Data
import android.view.animation.AlphaAnimation
import com.fglshm.tinderui.application.App
import com.fglshm.tinderui.model.Person

class SwipeFragment : BaseFragment() {

    interface OnLikeListButtonClickListener {
        fun onLikeListButtonClick()
    }

    override val logTag: String = SwipeFragment::class.java.simpleName
    override fun getLayout(): Int = R.layout.fragment_swipe

    var listener: OnLikeListButtonClickListener? = null

    private val imageList = Data.imageList
    private val nameList = Data.nameList
    private val descList = Data.descList

    private lateinit var atomicInteger: AtomicInteger

    private val cardContainer by lazy { card_container }
    private val likeCard by lazy { card_like_fragment_swipe }
    private val dislikeCard by lazy { card_dislike_fragment_swipe }
    private val likeListButton by lazy { image_button_like_list }
    private val reloadButton by lazy { image_button_reload }
    private val progressBar by lazy { progress }

    private var direction: Int = 0
    private var initX: Float = 0F
    private var initY: Float = 0F
    private var diffX: Float = 0F
    private var diffY: Float = 0F

    private var topCard: CardView? = null
    private var topPerson: Person? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitialCards()
        setClickListener()
    }

    private fun setInitialCards() {
        atomicInteger = AtomicInteger()
        repeat(3) {
            val card = createCardView()
            cardContainer.addView(card, 0)
            val animation = AlphaAnimation(0f, 1f)
            animation.duration = 300
            card.startAnimation(animation)
        }
    }

    private fun setClickListener() {
        likeCard.setOnClickListener {
            if (cardContainer.childCount == 0) return@setOnClickListener
            setCardState()
            saveLikePerson(topPerson)
            handleButton(true)
        }
        dislikeCard.setOnClickListener {
            if (cardContainer.childCount == 0) return@setOnClickListener
            setCardState()
            handleButton(false)
        }
        likeListButton.setOnClickListener {
            listener?.onLikeListButtonClick()
        }
        reloadButton.setOnClickListener {
            resetLikeList()
            it.alpha = 0.0F
            it.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            progressBar.animate().apply {
                duration = 250
                alpha(1.0F)
            }.start()
            Handler().postDelayed({
                run {
                    progressBar.alpha = 0.0F
                    progressBar.visibility = View.GONE
                    setInitialCards()
                }
            }, 1000)
        }
    }

    private fun setCardState() {
        likeCard.isEnabled = false
        dislikeCard.isEnabled = false
        Handler().postDelayed({
            run {
                likeCard.isEnabled = true
                dislikeCard.isEnabled = true
            }
        }, 1000)
    }

    private fun resetLikeList() {
        val persons = App.realm?.where(Person::class.java)?.findAll()
        App.realm?.executeTransaction {
            persons?.deleteAllFromRealm()
            Log.d("App", "[ ALL DELETED ]")
        }
    }

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

    /**
     * save first point of touch
     */
    private fun handleActionDown(event: MotionEvent) {
        initX = event.rawX
        initY = event.rawY
    }

    /**
     * translate card according to user gesture
     */
    private fun handleActionMove(event: MotionEvent, card: View) {
        diffX = event.rawX.minus(initX)
        diffY = event.rawY.minus(initY)
        val scale = if (Math.abs(diffX) < 350) {
            0.95.plus(0.05 * Math.abs(diffX) / 350)
        } else {
            1.0
        }
        val nextCard: View? = cardContainer.getChildAt(cardContainer.childCount.minus(2))
        nextCard?.scaleX = scale.toFloat()
        nextCard?.scaleY = scale.toFloat()
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

    /**
     * the action when user stops gesture
     */
    private fun handleActionUp(card: View) {
        if (Math.abs(diffX) > 350) {
            card.animate().apply {
                duration = 100
                translationX(1500F * direction)
            }.setListener(object : Animator.AnimatorListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onAnimationEnd(animation: Animator?) {
                    if (direction > 0) {
                        saveLikePerson(topPerson)
                    }
                    if (cardContainer.childCount > 1) {
                        val nextCard = cardContainer.getChildAt(cardContainer.childCount.minus(2)) as? CardView
                        topCard = nextCard
                        topPerson = Person(
                            topCard?.text_name_card_item?.text.toString(),
                            topCard?.text_description_card_item?.text.toString()
                        )
                        nextCard?.setOnTouchListener(cardTouchListener)
                    }
                    cardContainer.removeViewAt(cardContainer.childCount.minus(1))
                    if (atomicInteger.get() != imageList.size) {
                        cardContainer.addView(createCardView(), 0)
                    }
                    if (cardContainer.childCount == 0) {
                        reloadButton.visibility = View.VISIBLE
                        reloadButton.animate().apply {
                            alpha(1.0F)
                            duration = 250
                        }.setStartDelay(250).start()
                    }
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

    private fun handleButton(isLike: Boolean) {
        val topCard = cardContainer.getChildAt(cardContainer.childCount.minus(1))
        val animatedTextView = if (isLike) topCard.text_like_card_item else topCard.text_dislike_card_item
        animatedTextView.animate().apply {
            duration = 250
            alpha(1F)
        }.setListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                topCard.animate().apply {
                    duration = 200
                    translationX(if (isLike) 1500F else -1500F)
                }.setListener(object : Animator.AnimatorListener {
                    @SuppressLint("ClickableViewAccessibility")
                    override fun onAnimationEnd(animation: Animator?) {
                        if (cardContainer.childCount > 1) {
                            val nextCard = cardContainer.getChildAt(cardContainer.childCount.minus(2)) as? CardView
                            this@SwipeFragment.topCard = nextCard
                            topPerson = Person(
                                nextCard?.text_name_card_item?.text.toString(),
                                nextCard?.text_description_card_item?.text.toString()
                            )
                            nextCard?.setOnTouchListener(cardTouchListener)
                        }
                        cardContainer.removeViewAt(cardContainer.childCount.minus(1))
                        if (atomicInteger.get() != imageList.size) {
                            cardContainer.addView(createCardView(), 0)
                        }
                        if (cardContainer.childCount == 0) {
                            reloadButton.visibility = View.VISIBLE
                            reloadButton.animate().apply {
                                alpha(1.0F)
                                duration = 250
                            }.setStartDelay(250).start()
                        }
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        if (cardContainer.childCount == 1) return
                        val nextCard = cardContainer.getChildAt(cardContainer.childCount.minus(2))
                        nextCard.animate().apply {
                            duration = 200
                            scaleX(1.0F)
                            scaleY(1.0F)
                        }.start()
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                }).setStartDelay(250).start()
            }

            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        }).start()
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
            Glide.with(mContext).load(imageList[atomicInteger.get() % 11])
                .apply(RequestOptions().override(imageWidth, imageHeight)).centerCrop().into(this)
        }
        val desc = descList[atomicInteger.get() % 11]
        val name = nameList[atomicInteger.getAndIncrement() % 11]
        card.text_description_card_item.text = null
        card.text_name_card_item.text = null
        card.text_description_card_item.text = desc
        card.text_name_card_item.text = name

        // this is for achieving scale animation when top car is gone.
        if (atomicInteger.get() != 1) {
            card.scaleX = 0.95F
            card.scaleY = 0.95F
        } else {
            card.setOnTouchListener(cardTouchListener)
            topCard = card
            topPerson = Person(name, desc)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val cardButtonWidth = likeCard.layoutParams.width
        val peopleImageWidth = likeListButton.layoutParams.width
        params.marginStart = 16.times(scale)
        params.marginEnd = 16.times(scale)
        params.bottomMargin = cardButtonWidth.plus(32.times(scale))
        params.topMargin = peopleImageWidth.plus(32.times(scale))
        card.layoutParams = params
        return card
    }

    private fun saveLikePerson(person: Person?) {
        App.realm?.executeTransaction { r ->
            person?.let {
                r.copyToRealm(person)
            }
        }
    }

}