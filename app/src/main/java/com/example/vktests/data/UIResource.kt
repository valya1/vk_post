package com.example.vktests.data

import androidx.annotation.DrawableRes
import com.example.vktests.R

data class UIResource(
    @DrawableRes val previewDrawableRes: Int,
    @DrawableRes val originalDrawableRes: Int = previewDrawableRes,
    val trashResource: TrashResource = TrashResource.DefaultTrash
)

sealed class TrashResource(
    @DrawableRes open val defaultTrashDrawableRes: Int,
    @DrawableRes open  val releasedTrashDrawableRes: Int
) {

    object DefaultTrash : TrashResource(R.drawable.ic_fab_trash, R.drawable.ic_fab_trash_released)


    data class CustomTrash(override val defaultTrashDrawableRes: Int, override val releasedTrashDrawableRes: Int) :
        TrashResource(defaultTrashDrawableRes, releasedTrashDrawableRes)
}