package ru.miha.vk_post.text_backrounds

import android.os.Build
import android.text.Layout

private const val DEFAULT_LINESPACING_EXTRA = 0f
private const val DEFAULT_LINESPACING_MULTIPLIER = 1f

fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val lastLineSpacingNotAdded = Build.VERSION.SDK_INT >= 19
    val isLastLine = line == lineCount - 1

    val lineBottomWithoutSpacing: Int
    val lineSpacingExtra = spacingAdd
    val lineSpacingMultiplier = spacingMultiplier
    val hasLineSpacing = lineSpacingExtra != DEFAULT_LINESPACING_EXTRA
            || lineSpacingMultiplier != DEFAULT_LINESPACING_MULTIPLIER

    if (!hasLineSpacing || isLastLine && lastLineSpacingNotAdded) {
        lineBottomWithoutSpacing = lineBottom
    } else {
        val extra: Float
        if (lineSpacingMultiplier.compareTo(DEFAULT_LINESPACING_MULTIPLIER) != 0) {
            val lineHeight = getLineHeight(line)
            extra = lineHeight - (lineHeight - lineSpacingExtra) / lineSpacingMultiplier
        } else {
            extra = lineSpacingExtra
        }

        lineBottomWithoutSpacing = (lineBottom - extra).toInt()
    }

    return lineBottomWithoutSpacing
}

fun Layout.getLineHeight(line: Int): Int {
    return getLineTop(line + 1) - getLineTop(line)
}

fun Layout.getLineTopWithoutPadding(line: Int): Int {
    var lineTop = getLineTop(line)
    if (line == 0) {
        lineTop -= topPadding
    }
    return lineTop
}

fun Layout.getLineBottomWithoutPadding(line: Int): Int {
    var lineBottom = getLineBottomWithoutSpacing(line)
    if (line == lineCount - 1) {
        lineBottom -= bottomPadding
    }
    return lineBottom
}