package com.example.vktests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_background_preview.view.*
import android.graphics.drawable.GradientDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class BackgroundPreviewsAdapter(val onItemCLickListener: ((Int) -> Unit)) :
    RecyclerView.Adapter<BackgroundPreviewsAdapter.BackgroundPreviewViewHolder>() {

    private var prevSelectedPosition = -1

    private val backroundPreviewResources = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundPreviewViewHolder {
        return BackgroundPreviewViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_background_preview, parent, false)
        )
    }

    override fun getItemCount() = backroundPreviewResources.size

    override fun onBindViewHolder(holder: BackgroundPreviewViewHolder, position: Int) {

        holder.bind(backroundPreviewResources[position], false)

    }

    override fun onBindViewHolder(holder: BackgroundPreviewViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.bind(backroundPreviewResources[position], (payloads[0] as? Boolean) ?: false)
        }
    }

    fun setBackgroundColors(backgroundResources: List<Int>) {
        backroundPreviewResources.clear()
        backroundPreviewResources.addAll(backgroundResources)
        notifyDataSetChanged()
    }

    fun setSelectedBackgroundPreview(position: Int) {

        if (position == prevSelectedPosition) return

        if (prevSelectedPosition >= 0)
            notifyItemChanged(prevSelectedPosition, false)

        notifyItemChanged(position, true)
        prevSelectedPosition = position
    }

    @DrawableRes
    fun getBackgroundRes(clickedPosition: Int) = backroundPreviewResources[clickedPosition]

    inner class BackgroundPreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(@DrawableRes drawableResource: Int, isSelected: Boolean) {

            itemView.setOnClickListener { onItemCLickListener(adapterPosition) }

            Glide.with(itemView)
                .load(drawableResource)
                .transform(CenterCrop(), RoundedCorners(10))
                .into(itemView.backroundPreview)

            if (!isSelected) {
                itemView.imagePreviewContainer.foreground = null
            } else {
                itemView.imagePreviewContainer.foreground = GradientDrawable().apply {
                    setStroke(10, ContextCompat.getColor(itemView.context, R.color.blue_main))
                    cornerRadii = floatArrayOf(6f, 6f, 6f, 6f, 6f, 6f, 6f, 6f)
                }
            }
        }
    }
}