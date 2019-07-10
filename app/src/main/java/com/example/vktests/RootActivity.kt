package com.example.vktests

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.vktests.test_views.StickerView
import kotlinx.android.synthetic.main.activity_root.*


class RootActivity : AppCompatActivity(), StickerListDialogFragment.Listener, StickerView.OnMoveListener {

    private var mBackgroundPreviewsAdapter: BackgroundPreviewsAdapter? = null
    private val mUiHandler = Handler()
    private val mRunnable = mutableMapOf<String, Runnable>()

    private var mIsTrashAppearingOrVisible = false

    companion object {
        const val TRASH_DISMISS = "trash_dismiss"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val testDrawable = ContextCompat.getDrawable(this, R.drawable.white_rect_semitransparent)

        mBackgroundPreviewsAdapter = BackgroundPreviewsAdapter { clickedPosition ->

            rvBackroundPreviews?.scrollToPosition(clickedPosition)
            mBackgroundPreviewsAdapter?.setSelectedBackgroundPreview(clickedPosition)
            mBackgroundPreviewsAdapter?.getBackgroundRes(clickedPosition)?.let { backgroundRes ->
                bgImage.setImageDrawable(ContextCompat.getDrawable(this, backgroundRes))
            }
        }

        rvBackroundPreviews.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        rvBackroundPreviews.adapter = mBackgroundPreviewsAdapter

        mBackgroundPreviewsAdapter?.setBackgroundColors(
            listOf(
                R.drawable.blue, R.drawable.green, R.drawable.yellow_orange,
                R.drawable.purple, R.drawable.beach, R.drawable.stars
            )
        )

        btnChangeTextStyle.setOnClickListener {

            textPost.setBackgroundTextDrawable(testDrawable)

//            val spannableString = SpannableString(textPost.text)
//            if (!spansIterator.hasNext()) {
//                spansIterator = spans.iterator()
//            }
//            spannableString.setSpan(spansIterator.next(), 0, textPost.text.length, SPAN_EXCLUSIVE_EXCLUSIVE)
//            textPost.setText(spannableString)
        }

        btnAddSticker.setOnClickListener {
            StickerListDialogFragment.newInstance(24)
                .show(supportFragmentManager, "stickers_dialog")
        }
    }

    override fun onStickerClicked(position: Int) {
        addStickerOnScreen("file:///android_asset/Stickers/${position + 1}.png")
    }

    override fun onMove(sticker: StickerView, dx: Float, dy: Float) {

        val parentHeight = (trashSwitcher.parent as View).height
        val stickerCenterX = sticker.x + sticker.width / 2
        val stickerCenterY = sticker.y + sticker.height / 2
        val trashCenterY = trashSwitcher.y + trashSwitcher.height / 2

        if (dy > 5f && stickerCenterY < trashCenterY && trashCenterY - stickerCenterY <= parentHeight * 0.4) {
            trashSwitcher.cancelUniqueDelayedAction(tag = TRASH_DISMISS)

            if (!mIsTrashAppearingOrVisible) {
                trashSwitcher.showWithScaleAnimation(startAction = { mIsTrashAppearingOrVisible = true })
            }
        } else {
            trashSwitcher.scheduleUniqueDelayedAction(tag = TRASH_DISMISS) {
                hideWithScaleAnimation(
                    endAction = { mIsTrashAppearingOrVisible = false },
                    finishVisibility = View.INVISIBLE
                )
            }
        }

        if (trashSwitcher.isVisible) {
            with(trashSwitcher) {
                if (stickerCenterX in x..(x + width) && stickerCenterY in y..(y + width)) {
                    trashSwitcher.displayedChild = 1
                    sticker.scheduleUniqueDelayedAction(delay = 700) {
                        animateAlphaDismissWithCallback { containerContent.removeView(this) }
                    }

                } else {
                    trashSwitcher.displayedChild = 0
                    sticker.cancelUniqueDelayedAction()
                }
            }
        }
    }

    fun addStickerOnScreen(src: String) {

        val sticker = StickerView(this)
            .apply {
                tag = src
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
                onMoveListener = this@RootActivity
            }

        Glide.with(sticker)
            .load(src)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean = false

                override fun onResourceReady(
                    resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    with(sticker) {
                        mUiHandler.post {
                            setImageDrawable(resource)
                            containerContent.addView(this, 1)
                            showWithScaleAnimation()
                        }
                    }
                    return false
                }
            })
            .submit()
    }


    fun View.animateAlphaDismissWithCallback(duration: Long = 100, onAnimationCompleteBlock: View.() -> Unit) =
        animate()
            .alpha(0.0f)
            .setDuration(duration)
            .withEndAction { onAnimationCompleteBlock() }
            .start()

    fun View.showWithScaleAnimation(
        duration: Long = 100, startAction: (() -> Unit)? = null, endAction: (() -> Unit)? = null
    ) {
        scaleX = 0f
        scaleY = 0f
        animate()
            .withStartAction {
                visibility = VISIBLE
                startAction?.invoke()
            }
            .withEndAction {
                endAction?.invoke()
            }
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(duration)
            .start()
    }


    fun View.hideWithScaleAnimation(
        duration: Long = 100,
        finishVisibility: Int = View.GONE,
        startAction: (() -> Unit)? = null,
        endAction: (() -> Unit)? = null
    ) = animate()
        .withStartAction {
            visibility = VISIBLE
            startAction?.invoke()
        }
        .withEndAction {
            visibility = finishVisibility
            endAction?.invoke()
        }
        .scaleX(0.0f)
        .scaleY(0.0f)
        .setDuration(duration)
        .start()


    fun View.scheduleUniqueDelayedAction(
        tag: String = this.tag as String, delay: Long = 1000, action: View.() -> Unit
    ) {
        (tag as? String)?.run {
            if (!mRunnable.containsKey(tag)) {
                mRunnable[tag] = Runnable { action() }.also { handler.postDelayed(it, delay) }
            }
        }
    }

    fun View.cancelUniqueDelayedAction(tag: String = this.tag as String) {
        if (mRunnable.containsKey(tag)) {
            mRunnable[tag]?.run(handler::removeCallbacks)
            mRunnable.remove(tag)
        }
    }
}