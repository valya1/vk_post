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
import com.example.vktests.data.UIResource


class BackgroundPreviewsAdapter(vararg backrounds: UIResource, val onItemCLickListener: ((Int) -> Unit)) :
    RecyclerView.Adapter<BackgroundPreviewsAdapter.BackgroundPreviewViewHolder>() {

    private var selectedPosition = 0
    private val backroundResources: MutableList<UIResource> = backrounds.toMutableList()

    init {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundPreviewViewHolder {
        return BackgroundPreviewViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_background_preview, parent, false)
        )
    }

    override fun getItemCount() = backroundResources.size

    override fun onBindViewHolder(holder: BackgroundPreviewViewHolder, position: Int) {
        holder.bind(backroundResources[position].previewDrawableRes, position == selectedPosition)
    }


    override fun onBindViewHolder(holder: BackgroundPreviewViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.bind(backroundResources[position].previewDrawableRes, payloads[0] as Boolean)
        }
    }


    fun setSelectedBackgroundPreview(position: Int) {

        if (position == selectedPosition) return

        val prevPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(prevPosition, false)
        notifyItemChanged(selectedPosition, true)
    }

    fun getUIResource(position: Int) = backroundResources[position]

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