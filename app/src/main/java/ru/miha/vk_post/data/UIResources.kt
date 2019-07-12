package ru.miha.vk_post.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ru.miha.vk_post.R

data class UIResource(
    @DrawableRes val previewDrawableRes: Int,
    @DrawableRes val originalDrawableRes: Int = previewDrawableRes,
    @ColorRes val defaultTextColor: Int,
    val trashResource: TrashResource = TrashResource.DefaultTrash
)

sealed class TrashResource(
    @DrawableRes open val defaultTrashDrawableRes: Int,
    @DrawableRes open val releasedTrashDrawableRes: Int
) {

    object DefaultTrash : TrashResource(R.drawable.ic_fab_trash, R.drawable.ic_fab_trash_released)


    data class CustomTrash(override val defaultTrashDrawableRes: Int, override val releasedTrashDrawableRes: Int) :
        TrashResource(defaultTrashDrawableRes, releasedTrashDrawableRes)
}


class TextStyle(@ColorRes val backgroundColor: Int?, @ColorRes val textColor: Int)