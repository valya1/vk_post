package com.example.vktests

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import kotlinx.android.synthetic.main.activity_root.*
import android.text.method.Touch.onTouchEvent
import com.example.vktests.touch_listeners.MoveListener
import com.almeros.android.multitouch.MoveGestureDetector
import com.example.vktests.touch_listeners.RotateListener
import com.almeros.android.multitouch.RotateGestureDetector
import com.example.vktests.touch_listeners.ScaleListener
import android.view.ScaleGestureDetector
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight
import com.example.vktests.test_views.StickerView2


class RootActivity : AppCompatActivity(), StickerListDialogFragment.Listener {

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


    fun addStickerOnScreen(src: String) {

        val sticker = StickerView2(this)
            .apply {
                scaleX = 0.0f
                scaleY = 0.0f
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
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