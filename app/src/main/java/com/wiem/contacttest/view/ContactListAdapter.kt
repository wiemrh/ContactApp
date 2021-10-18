package com.wiem.contacttest.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.wiem.contacttest.R
import com.wiem.contacttest.core.helper.PermissionHelper
import com.wiem.contacttest.data.entity.ContactEntity


class ContactListAdapter(private val context: Context, private val activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemArrayList: List<ContactEntity> = ArrayList()
    private var onItemClick: OnItemClickListener? = null

    // Allows to remember the last item shown on screen
    private var lastPosition = -1

    fun setData(setList: List<ContactEntity>) {
        itemArrayList = setList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact, parent, false)
        return SubCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = itemArrayList[position]

        (holder as SubCategoryViewHolder).tvName.text = data.name
        holder.tvPhone.text = data.phone

        holder.ivCall.setOnClickListener {
            if (PermissionHelper.checkCallPermission(context, activity)) {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:" + data.phone)
                context.startActivity(callIntent)
            }
        }

        holder.container.setOnClickListener {
            onItemClick?.onItemClick(position, data)
        }

        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return itemArrayList.size
    }

    private inner class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val container: ConstraintLayout = itemView.findViewById(R.id.container)
        val ivCall: ImageView = itemView.findViewById(R.id.ivCall)
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var tvPhone: TextView = itemView.findViewById(R.id.tvPhone)

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: ContactEntity)
    }

    /**
     * Here is the key method to apply the animation
     */
    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(
                context,
                android.R.anim.slide_in_left
            )
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}