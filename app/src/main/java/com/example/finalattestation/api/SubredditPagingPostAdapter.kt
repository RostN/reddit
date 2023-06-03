package com.example.finalattestation.api

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.finalattestation.databinding.SubredditPostSimpleBinding
import com.example.finalattestation.sharedText
import com.example.finalattestation.subreddit.subredditContext
import com.example.finalattestation.subredditAuthor

class SubredditPagingPostAdapter(
    private val onClick: (Response) -> Unit,
    private val authorClick: (Response) -> Unit
) :
    PagingDataAdapter<Response, MyViewHolderSecond>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderSecond {
        return MyViewHolderSecond(
            SubredditPostSimpleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolderSecond, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            var link = ""
            if (item?.data?.media_metadata.toString() !== "null") {
                link = item?.data?.media_metadata.toString()
                    .substringAfter("s={").substringAfter("u=")
                    .substringBefore("}").replace("&amp;", "&")
            } else {
                link = item?.data?.preview.toString()
                    .substringAfter("url=").substringBefore(",")
                    .replace("&amp;", "&")
            }

            pic.load(link)
            commentsCount.text = " ${item?.data?.num_comments}"
            description.text = item?.data?.selftext
            author.text = "$subredditAuthor ${item?.data?.author}"
            title.text = item?.data?.title
        }

        //Открыть страничку автора
        holder.binding.author.setOnClickListener {
            authorClick(item!!)
        }

        //Кнопка поделиться
        holder.binding.shareBtn.setOnClickListener {
            val sharedLink = "https://reddit.com${item?.data?.permalink}"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, sharedLink)
            sendIntent.type = "text/plain"
            startActivity(
                subredditContext,
                Intent.createChooser(sendIntent, sharedText),
                null
            )
        }

        holder.binding.root.setOnClickListener {
            item?.let { onClick(item) }
        }
    }
}

class MyViewHolderSecond(val binding: SubredditPostSimpleBinding) :
    RecyclerView.ViewHolder(binding.root)