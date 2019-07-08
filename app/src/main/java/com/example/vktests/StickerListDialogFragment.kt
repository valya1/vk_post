package com.example.vktests

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_sticker_list_dialog.*
import kotlinx.android.synthetic.main.fragment_sticker_list_dialog_item.*
import kotlinx.android.synthetic.main.fragment_sticker_list_dialog_item.view.stickerImageView

const val ARG_ITEM_COUNT = "item_count"

class StickerListDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onStart() {
        super.onStart()

        val view = view
        val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)

        view?.post {
            val parent = view.parent as View
            val params = parent.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
            val behavior = params.behavior

            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            val maxHeight = (view.measuredHeight * 0.6).toInt()
            bottomSheet?.layoutParams?.height = maxHeight
            bottomSheetBehavior!!.peekHeight = maxHeight
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sticker_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        list.layoutManager = GridLayoutManager(context, 4)
        list.adapter = StickerAdapter(arguments?.getInt(ARG_ITEM_COUNT) ?: 0)

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((list.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() == 0)
                    divider.visibility = View.GONE
                else
                    divider.visibility = View.VISIBLE
            }
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onStickerClicked(position: Int)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_sticker_list_dialog_item, parent, false)) {

        init {
            itemView.stickerImageView.setOnClickListener {
                mListener?.let {
                    it.onStickerClicked(adapterPosition)
                    dismiss()
                }
            }
        }
    }

    private inner class StickerAdapter internal constructor(private val mItemCount: Int) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(holder.itemView.context)
                .load("file:///android_asset/Stickers/${position + 1}.png")
                .into(holder.itemView.stickerImageView)
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    companion object {
        fun newInstance(itemCount: Int): StickerListDialogFragment =
            StickerListDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }
    }
}
