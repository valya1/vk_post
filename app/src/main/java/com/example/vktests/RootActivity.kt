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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.vktests.test_views.StickerView2
import kotlinx.android.synthetic.main.activity_root.*


class RootActivity : AppCompatActivity(), StickerListDialogFragment.Listener, StickerView2.OnMoveListener {

    private var backgoundPreviewsAdapter: BackgroundPreviewsAdapter? = null
    private val uiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)


        val testDrawable = ContextCompat.getDrawable(this, R.drawable.white_rect_semitransparent)

//        postText.addTextChangedListener(object: TextWatcher{
//            override fun afterTextChanged(s: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//        })

        backgoundPreviewsAdapter = BackgroundPreviewsAdapter { clickedPosition ->

            rvBackroundPreviews?.scrollToPosition(clickedPosition)
            backgoundPreviewsAdapter?.setSelectedBackgroundPreview(clickedPosition)
            backgoundPreviewsAdapter?.getBackgroundRes(clickedPosition)?.let { backgroundRes ->
                bgImage.setImageDrawable(ContextCompat.getDrawable(this, backgroundRes))
            }
        }

        rvBackroundPreviews.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        rvBackroundPreviews.adapter = backgoundPreviewsAdapter

        backgoundPreviewsAdapter?.setBackgroundColors(
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


    private val trashDismissRunnable = Runnable {
        trashSwitcher.visibility = View.GONE
        isTrashAnimatingOrVisible = false
    }

    private var isTrashAnimatingOrVisible = false

    override fun onMove(sticker: StickerView2, dx: Float, dy: Float, pointerX: Float, pointerY: Float) {

        val parentHeight = (trashSwitcher.parent as View).height

        val stickerCenterX = sticker.x + sticker.width / 2
        val stickerCenterY = sticker.y + sticker.height / 2
        val trashCenterX = trashSwitcher.x + trashSwitcher.width / 2
        val trashCenterY = trashSwitcher.y + trashSwitcher.height / 2

        if (dy > 0 && stickerCenterY < trashCenterY && trashCenterY - stickerCenterY <= parentHeight * 0.4) {
            uiHandler.removeCallbacks(trashDismissRunnable)
            if (!isTrashAnimatingOrVisible) {
                trashSwitcher
                    .apply {
                        visibility = VISIBLE
                        scaleX = 0f
                        scaleY = 0f
                    }
                    .animate()
                    .withStartAction {
                        isTrashAnimatingOrVisible = true
                    }
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
        } else {
            uiHandler.postDelayed(trashDismissRunnable, 2000)
        }


        with(trashSwitcher) {

            if (stickerCenterX in x..(x + width) && stickerCenterY in y..(y + width)) {
                trashSwitcher.displayedChild = 1
            } else {
                trashSwitcher.displayedChild = 0
            }
        }
    }


    fun addStickerOnScreen(src: String) {

        val sticker = StickerView2(this)
            .apply {
                scaleX = 0.0f
                scaleY = 0.0f
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
                        uiHandler.post {
                            setImageDrawable(resource)
                            containerContent.addView(this, 1)
                            animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start()
                        }
                    }
                    return false
                }
            })
            .submit()
    }
}