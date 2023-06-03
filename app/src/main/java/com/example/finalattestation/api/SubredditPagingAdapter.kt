package com.example.finalattestation.api

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.finalattestation.MainActivity
import com.example.finalattestation.databinding.SubredditSimpleBinding
import com.example.finalattestation.subscribersText


class SubredditPagingAdapter(
    private val onClick: (Response) -> Unit
) :
    PagingDataAdapter<Response, MyViewHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            SubredditSimpleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            subredditTitle.text = item?.data?.title
            redditText.text = item?.data?.public_description
            subredditImage.load(item?.data?.header_img)
            subscribers.text = "$subscribersText: ${item?.data?.subscribers}"
            if (item?.data?.user_is_subscriber == true) {
                subscribedBtn.isVisible = true
                subscribeBtn.isVisible = false
            }
            if (item?.data?.user_is_subscriber == false) {
                subscribeBtn.isVisible = true
                subscribedBtn.isVisible = false
            }
            if (item?.data?.header_img ==null) subredditImage.isVisible=false
        }

        val changeBtn = {
            holder.binding.subscribedBtn.isVisible = !holder.binding.subscribedBtn.isVisible
            holder.binding.subscribeBtn.isVisible = !holder.binding.subscribeBtn.isVisible
        }

        //Отобразить содержимое
        holder.binding.subredditTitle.setOnClickListener {
            holder.binding.contentControl.isVisible = !holder.binding.contentControl.isVisible
        }

        //Подписаться
        holder.binding.subscribeBtn.setOnClickListener {
            println("Subscribe")
            item?.data?.user_is_subscriber = true
            changeBtn.invoke()
            MainActivity().subscribeOrUnsubscribe("sub", item?.data?.display_name!!)
        }

        //Отписаться
        holder.binding.subscribedBtn.setOnClickListener {
            println("Unsubscribe")
            item?.data?.user_is_subscriber = false
            changeBtn.invoke()
            MainActivity().subscribeOrUnsubscribe("unsub", item?.data?.display_name!!)
        }
        holder.binding.root.setOnClickListener {
            item?.let { onClick(item) }
        }
    }
}

//Дифутил
class DiffUtilCallback : DiffUtil.ItemCallback<Response>() {
    override fun areItemsTheSame(oldItem: Response, newItem: Response): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(
        oldItem: Response,
        newItem: Response
    ): Boolean =
        oldItem == newItem
}

class MyViewHolder(val binding: SubredditSimpleBinding) :
    RecyclerView.ViewHolder(binding.root)