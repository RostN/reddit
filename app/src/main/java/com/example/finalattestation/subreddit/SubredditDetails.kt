package com.example.finalattestation.subreddit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalattestation.*
import com.example.finalattestation.api.Response
import com.example.finalattestation.api.SubredditPagingPostAdapter
import com.example.finalattestation.databinding.FragmentSubredditDetailsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("StaticFieldLeak")
lateinit var subredditContext: Context

class SubredditDetails : Fragment() {
    private var _binding: FragmentSubredditDetailsBinding? = null
    private val binding get() = _binding!!
    var subredditID = ""
    var subscriber = false
    val bundle = Bundle()

    private val subredditAdapter =
        SubredditPagingPostAdapter(
            onClick = { Responce -> onItemClick(Responce) },
            authorClick = { Responce -> onAuthorClick(Responce) }
        )

    private val viewModel: SubredditViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SubredditViewModel() as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubredditDetailsBinding.inflate(inflater, container, false)
        //Установка адаптера
        binding.recycler.adapter = subredditAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subredditID = arguments?.getString("subredditID").toString()
        subscriber = arguments?.getBoolean("Subscriber")!!
        binding.title.text = arguments?.getString("Title").toString()

        checkSubscribe(subscriber)

        try {
            viewModel.mode = 1
            viewModel.id = subredditID
            viewModel.pagedSubreddit.onEach {
                subredditAdapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } catch (e: Exception) {
            println("Error mode 1\nSubredditDetails: $e")
        }
        //Кнопка подписаться/отписаться
        binding.subscribeSubreddit.setOnClickListener {
            subOrUnsub(subredditID)
            subscriber = !subscriber
            checkSubscribe(subscriber)
        }

        //Кнопка назад
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    //Подписка или отписка
    private fun subOrUnsub(id: String) {
        var action = ""
        if (subscriber) action = "unsub" else action = "sub"
        MainActivity().subscribeOrUnsubscribe(action, id)
    }

    //Изменение иконки в зависимости от наличия подписки
    private fun checkSubscribe(get: Boolean) {
        if (get) {
            binding.subscribeSubreddit.setImageResource(R.drawable.icon_subscribed)
        } else binding.subscribeSubreddit.setImageResource(R.drawable.icon_subscribe)
    }

    //Нажатие на автора
    private fun onAuthorClick(item: Response) {
        bundle.putString("Redirect author", item.data.author)
        findNavController().navigate(R.id.navigation_notifications, bundle)
    }

    //Нажатие на пост
    private fun onItemClick(item: Response) {
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
        bundle.putString("postTitle", item.data.title)
        bundle.putString("postDescription", item.data.selftext)
        bundle.putString("postAuthor", item.data.author)
        bundle.putString("commentCount", item.data.num_comments.toString())
        bundle.putString("commentLink", item.data.permalink)
        bundle.putString("img", link)

        findNavController().navigate(R.id.subredditDetailPost, bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SubredditDetails()
    }
}