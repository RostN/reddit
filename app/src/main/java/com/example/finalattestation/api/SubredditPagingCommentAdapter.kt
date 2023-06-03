package com.example.finalattestation.api

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.finalattestation.databinding.SubredditCommentSimpleBinding
import com.example.finalattestation.formatter
import com.example.finalattestation.subredditAuthor

class SubredditPagingCommentAdapter(
    private val authorPostClick: (Response) -> Unit,
    private val authorCommentClick: (Response) -> Unit,
    private val subredditNameClick: (Response) -> Unit,
) :
    PagingDataAdapter<Response, MyViewHolderThird>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderThird {
        return MyViewHolderThird(
            SubredditCommentSimpleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolderThird, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            pic.load(item?.data?.link_url)
            subredditName.text = item?.data?.subreddit
            titlePost.text = item?.data?.link_title
            postAuthor.text = "$subredditAuthor ${item?.data?.link_author}"
            commentAuthor.text = "$subredditAuthor ${item?.data?.author}"
            bodyComment.text = item?.data?.body
            dateComment.text = formatter.format(item?.data?.created!! * 1000)
            if (item?.data?.link_author == null) postAuthor.isVisible = false
        }

        //Открыть сабреддит
        holder.binding.subredditName.setOnClickListener {
            subredditNameClick(item!!)
        }

        //Открыть страничку автора поста
        holder.binding.postAuthor.setOnClickListener {
            authorPostClick(item!!)
        }

        //Открыть страничку автора комментария
        holder.binding.commentAuthor.setOnClickListener {
            authorCommentClick(item!!)
        }
    }
}

class MyViewHolderThird(val binding: SubredditCommentSimpleBinding) :
    RecyclerView.ViewHolder(binding.root)