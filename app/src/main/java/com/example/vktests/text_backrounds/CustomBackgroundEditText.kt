package com.example.vktests.text_backrounds

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spanned
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.withTranslation
import android.graphics.*
import com.example.vktests.R


class CustomBackgroundEditText : AppCompatEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var customBackgroundTextHelper: CustomBackgroundTextHelper? = null

    private var paint = Paint().apply {
        color = ContextCompat.getColor(context,  R.color.blue_text_bg_semi_transparent)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }

    override fun onDraw(canvas: Canvas) {

        if (customBackgroundTextHelper != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                customBackgroundTextHelper?.draw(canvas, paint, text as Spanned, layout)
            }
        }

        super.onDraw(canvas)
    }

    fun setBackgroundTextDrawable(drawable: Drawable?) {
        if (drawable != null) {
            customBackgroundTextHelper = CustomBackgroundTextHelper(
                horizontalPadding = 10,
                verticalPadding = 10,
                backgroundDrawable = drawable
            )
        } else {
            customBackgroundTextHelper = null
        }
        invalidate()
    }

    fun setBackroundColor(@ColorRes colorId: Int) {
        paint = Paint().apply {
            color = ContextCompat.getColor(this@CustomBackgroundEditText.context, colorId)
            isAntiAlias = true
        }

    }
}