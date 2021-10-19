package com.app.couchvibes.ui.event_manager.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.couchvibes.R
import com.app.couchvibes.data.ViewType
import com.app.couchvibes.data.models.EventModel
import com.app.couchvibes.databinding.ListItemEventOrgnisedBinding
import com.app.couchvibes.utils.Utils
import com.app.couchvibes.utils.helper.DateHelper
import com.app.couchvibes.utils.listeners.OnActionListener

class EventOrganiseByMeAdapter constructor(
    private val context: Context,
    private val items: MutableList<EventModel>,
    private val onActionListener: OnActionListener<EventModel>,
) : RecyclerView.Adapter<EventOrganiseByMeAdapter.ViewHolder>() {

    private var isLoadingAdded = false

    class ViewHolder : RecyclerView.ViewHolder {

        var binding: ListItemEventOrgnisedBinding? = null

        constructor(view: ListItemEventOrgnisedBinding) : super(view.root) {
            binding = view
        }

        constructor(itemView: View) : super(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val type = ViewType.parseByValue(viewType)
        return if (type?.value == ViewType.ITEM.value)
            ViewHolder(ListItemEventOrgnisedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading_horizontal, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]

        if (holder.binding != null) {

            holder.binding!!.apply {

                Utils.displayImage(model.bannerImgUrl, ivBannerImage, 0)
                tvEventTitle.text = model.title

                val date = DateHelper.isoToDate(model.date)
                tvDateEvent.text = DateHelper.customFormat(date)

                val time = DateHelper.isoToDate(model.startTime!!)
                tvEventStartTime.text = DateHelper.getTime(time)

                if (model.eventLocations != null) {
                    rlLocationEvent.visibility = View.VISIBLE
                    tvLocation.text = model.eventLocations!![0].address
                } else {
                    rlLocationEvent.visibility = View.INVISIBLE
                }
            }

            holder.itemView.setOnClickListener {
                onActionListener.notify(model, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size - 1 && isLoadingAdded)
            ViewType.LOADING.value
        else
            ViewType.ITEM.value
    }

    fun add(model: EventModel) {
        items.add(model)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(mcList: List<EventModel>) {
        for (model in mcList) {
            add(model)
        }
    }

    fun remove(model: EventModel) {
        val position: Int = items.indexOf(model)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(EventModel())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = items.size - 1
        if (position < 0) {
            return
        }
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun getItem(position: Int): EventModel {
        return items[position]
    }
}