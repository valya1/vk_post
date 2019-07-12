package ru.miha.vk_post

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
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
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
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
import ru.miha.vk_post.test_views.StickerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_root.*
import ru.miha.vk_post.data.TextStyle
import ru.miha.vk_post.data.TrashResource
import ru.miha.vk_post.data.UIResource
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class RootActivity : AppCompatActivity(), StickerListDialogFragment.Listener, StickerView.OnMoveListener {

    companion object {
        const val TRASH_DISMISS = "trash_dismiss"
        const val STICKER_DIALOG = "sticker_dialog"
        const val PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var mUIResource: UIResource

    private var mBackgroundPreviewsAdapter: BackgroundPreviewsAdapter? = null
    private val mUiHandler = Handler()
    private val mRunnables = mutableMapOf<String, Runnable>()

    private val textStyles = listOf(
        TextStyle(null, R.color.black),
        TextStyle(R.color.white, R.color.black),
        TextStyle(R.color.white_semitransparent, R.color.white)
    )
    private var textStylesIterator = textStyles.iterator()

    private var mCustomTextStyleApplied = false
    private var mIsTrashAppearingOrVisible = false

    private var saveDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        mBackgroundPreviewsAdapter = BackgroundPreviewsAdapter(
            UIResource(
                previewDrawableRes = R.drawable.gray,
                originalDrawableRes = R.drawable.white,
                trashResource = TrashResource.CustomTrash(
                    R.drawable.ic_fab_trash_blue_circled,
                    R.drawable.ic_fab_trash_blue_released_circled
                ),
                defaultTextColor = R.color.black
            ),
            UIResource(previewDrawableRes = R.drawable.blue, defaultTextColor = R.color.white),
            UIResource(previewDrawableRes = R.drawable.green, defaultTextColor = R.color.white),
            UIResource(previewDrawableRes = R.drawable.yellow_orange, defaultTextColor = R.color.white),
            UIResource(previewDrawableRes = R.drawable.purple, defaultTextColor = R.color.white),
            UIResource(previewDrawableRes = R.drawable.beach, defaultTextColor = R.color.white),
            UIResource(previewDrawableRes = R.drawable.stars, defaultTextColor = R.color.white)
        ) { clickedPosition ->

            rvBackroundPreviews?.scrollToPosition(clickedPosition)
            mBackgroundPreviewsAdapter?.setSelectedBackgroundPreview(clickedPosition)
            mBackgroundPreviewsAdapter?.getUIResource(clickedPosition)?.let(::setUIResources)
        }.also {
            setUIResources(it.getUIResource(0))
        }

        rvBackroundPreviews.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        rvBackroundPreviews.adapter = mBackgroundPreviewsAdapter

        btnChangeTextStyle.setOnClickListener {
            if (!textStylesIterator.hasNext())
                textStylesIterator = textStyles.iterator()
            applyTextStyle(textStylesIterator.next())
        }

        btnAddSticker.setOnClickListener {

            if (supportFragmentManager.findFragmentByTag(STICKER_DIALOG) == null) {
                textPost.hideKeyboard()
                StickerListDialogFragment.newInstance(24).show(supportFragmentManager, STICKER_DIALOG)
            }
        }

        btnSave.setOnClickListener {
            startImageSaving()
        }
    }

    private fun applyTextStyle(textStyle: TextStyle) {
        with(textPost) {
            setTextColor(ContextCompat.getColor(this@RootActivity, textStyle.textColor))
            setBackgroundColorFotText(textStyle.backgroundColor)
            mCustomTextStyleApplied = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDisposable?.dispose()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImage()
        }
    }

    override fun onStickerClicked(position: Int) {
        addStickerOnScreen("file:///android_asset/Stickers/${position + 1}.png")
    }

    override fun onStickerMove(sticker: StickerView, dx: Float, dy: Float) {

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

        if (mIsTrashAppearingOrVisible) {
            with(imageTrash) {
                if (stickerCenterX in x..(x + width) && stickerCenterY in y..(y + width)) {
                    imageTrash.setImageResource(mUIResource.trashResource.releasedTrashDrawableRes)
                    sticker.scheduleUniqueDelayedAction(delay = 700) {
                        animateStickerDismissWithCallback { containerContent.removeView(this) }
                    }

                } else {
                    imageTrash.setImageResource(mUIResource.trashResource.defaultTrashDrawableRes)
                    sticker.cancelUniqueDelayedAction()
                }
            }
        }
    }

    fun setUIResources(resource: UIResource) {
        mUIResource = resource
        imageBackround?.setImageResource(resource.originalDrawableRes)
        if (!mCustomTextStyleApplied)
            textPost.setTextColor(ContextCompat.getColor(this, resource.defaultTextColor))
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
        with(containerContent) {
            textPost.isCursorVisible = false
            layout(left, top, right, bottom)
            val image = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            draw(canvas)
            createImageFile(image)
        }
    }

    fun createImageFile(image: Bitmap) {
        saveDisposable = Observable.fromCallable {
            var out: FileOutputStream? = null
            val path =
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/vkPostLite/"
            val fileName = "IMG_${getCurrentDate()}.jpg"

            try {
                File("$path/")
                    .run {
                        mkdirs()
                        out = FileOutputStream(File(this, fileName))
                        image.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
            } finally {
                out?.flush()
                out?.close()
            }
            path + fileName
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBar.isVisible = true }
            .doOnTerminate {
                progressBar.isVisible = false
                textPost.isCursorVisible = true
            }
            .subscribe(
                { filePath ->
                    Toast.makeText(this, "Картинка добавлена: $filePath", Toast.LENGTH_LONG).show()
                },
                { throwable ->
                    Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
                }
            )
    }


    fun getCurrentDate(): String {
        return Calendar.getInstance().run {
            SimpleDateFormat("yyyyMdd_HHmmss", Locale.getDefault()).format(time)
        }

    }

    fun View.animateStickerDismissWithCallback(duration: Long = 150, onAnimationCompleteBlock: View.() -> Unit) =
        animate()
            .alpha(0.0f)
            .scaleX(0.0f)
            .scaleY(0.0f)
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

    fun View.hideKeyboard() {
        if (isFocused) {
            clearFocus()
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            try {
                imm?.hideSoftInputFromWindow(this.windowToken, 0)
            } catch (t: Throwable) {
            }
        }
    }
}