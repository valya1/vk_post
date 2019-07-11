package com.example.vktests

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.vktests.data.UIResource
import com.example.vktests.data.TrashResource
import com.example.vktests.test_views.StickerView
import kotlinx.android.synthetic.main.activity_root.*
import java.io.File
import java.io.FileOutputStream


class RootActivity : AppCompatActivity(), StickerListDialogFragment.Listener, StickerView.OnMoveListener {

    private var mBackgroundPreviewsAdapter: BackgroundPreviewsAdapter? = null
    private val mUiHandler = Handler()
    private val mRunnables = mutableMapOf<String, Runnable>()

    private lateinit var mUIResource: UIResource

    private var mIsTrashAppearingOrVisible = false


    companion object {
        const val TRASH_DISMISS = "trash_dismiss"

        const val PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val testDrawable = ContextCompat.getDrawable(this, R.drawable.white_rect_semitransparent)

        mBackgroundPreviewsAdapter = BackgroundPreviewsAdapter(
            UIResource(
                previewDrawableRes = R.drawable.gray,
                originalDrawableRes = R.drawable.white,
                trashResource = TrashResource.CustomTrash(
                    R.drawable.ic_fab_trash_blue_circled,
                    R.drawable.ic_fab_trash_blue_released_circled
                )
            ),
            UIResource(R.drawable.blue),
            UIResource(R.drawable.green),
            UIResource(R.drawable.yellow_orange),
            UIResource(R.drawable.purple),
            UIResource(R.drawable.beach),
            UIResource(R.drawable.stars)
        ) { clickedPosition ->

            rvBackroundPreviews?.scrollToPosition(clickedPosition)
            mBackgroundPreviewsAdapter?.setSelectedBackgroundPreview(clickedPosition)
            mBackgroundPreviewsAdapter?.getUIResource(clickedPosition)?.let(::setUIResource)
        }
            .also {
                setUIResource(it.getUIResource(0))
            }

        rvBackroundPreviews.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        rvBackroundPreviews.adapter = mBackgroundPreviewsAdapter

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

        btnSave.setOnClickListener {
            startImageSaving()
        }

    }

    override fun onStickerClicked(position: Int) {
        addStickerOnScreen("file:///android_asset/Stickers/${position + 1}.png")
    }

    override fun onMove(sticker: StickerView, dx: Float, dy: Float) {

        val parentHeight = containerContent.height

        val stickerCenterX = sticker.x + sticker.width / 2
        val stickerCenterY = sticker.y + sticker.height / 2
        val trashCenterY = imageTrash.y + imageTrash.height / 2

        if (dy > 5f && stickerCenterY < trashCenterY && trashCenterY - stickerCenterY <= parentHeight * 0.4) {
            imageTrash.cancelUniqueDelayedAction(tag = TRASH_DISMISS)

            if (!mIsTrashAppearingOrVisible) {
                imageTrash.showWithScaleAnimation(startAction = { mIsTrashAppearingOrVisible = true })
            }
        } else {
            imageTrash.scheduleUniqueDelayedAction(tag = TRASH_DISMISS) {
                hideWithScaleAnimation(
                    endAction = { mIsTrashAppearingOrVisible = false },
                    finishVisibility = View.INVISIBLE
                )
            }
        }

        if (imageTrash.isVisible) {
            with(imageTrash) {
                if (stickerCenterX in x..(x + width) && stickerCenterY in y..(y + width)) {
                    imageTrash.setImageResource(mUIResource.trashResource.releasedTrashDrawableRes)
                    sticker.scheduleUniqueDelayedAction(delay = 700) {
                        animateAlphaDismissWithCallback { containerContent.removeView(this) }
                    }

                } else {
                    imageTrash.setImageResource(mUIResource.trashResource.defaultTrashDrawableRes)
                    sticker.cancelUniqueDelayedAction()
                }
            }
        }
    }

    fun setUIResource(resource: UIResource) {
        mUIResource = resource
        imageBackround?.setImageResource(resource.originalDrawableRes)
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

    fun startImageSaving() {

        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            saveImage()
        }
    }

    fun saveImage() {

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        with(containerContent) {
            layout(left, top, right, bottom)
            val image = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            draw(canvas)

            var out: FileOutputStream? = null

            try {
                File("$path/vkPost/")
                    .run {
                        mkdirs()
                        val imageFile = File(this, "vk_post.jpg")
                        out = FileOutputStream(imageFile)
                        image.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
            } finally {
                out?.flush()
                out?.close()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImage()
        }

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
        tag: String = this.tag as String, delay: Long = 1300, action: View.() -> Unit
    ) {
        (tag as? String)?.run {
            if (!mRunnables.containsKey(tag)) {
                mRunnables[tag] = Runnable { action() }.also { handler.postDelayed(it, delay) }
            }
        }
    }

    fun View.cancelUniqueDelayedAction(tag: String = this.tag as String) {
        if (mRunnables.containsKey(tag)) {
            mRunnables[tag]?.run(handler::removeCallbacks)
            mRunnables.remove(tag)
        }
    }
}