package com.example.finalattestation.subreddit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.finalattestation.R
import com.example.finalattestation.api.Response
import com.example.finalattestation.api.SubredditPagingCommentAdapter
import com.example.finalattestation.SubredditViewModel
import com.example.finalattestation.databinding.FragmentSubredditDetailPostBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SubredditDetailPost : Fragment() {

    private var _binding: FragmentSubredditDetailPostBinding? = null
    private val binding get() = _binding!!
    val bundle = Bundle()

    private val adapter = SubredditPagingCommentAdapter(
        authorPostClick = { Responce -> onPostAuthorClick(Responce) },
        authorCommentClick = { Responce -> onCommentAuthorClick(Responce) },
        subredditNameClick = { Responce -> onSubredditClick(Responce) }
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
//        _binding = FragmentSubredditDetailPostBinding.inflate(inflater, container, false)
        _binding = FragmentSubredditDetailPostBinding.inflate(inflater)

        //Установка адаптера
        binding.recycler.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("postTitle").toString()
        val description = arguments?.getString("postDescription").toString()
        val link = arguments?.getString("img").toString()
        val author = arguments?.getString("postAuthor").toString()
        val commentCount = " ${arguments?.getString("commentCount")}"
        val commentLink = arguments?.getString("commentLink").toString()
        binding.author.text = "${getString(R.string.subreddit_author)} $author"
        binding.pic.load(link)
        binding.commentsCount.text = commentCount
        binding.titleToolbar.text = title
        binding.description.text = description
        binding.postName.text = title

        if (description == "null") binding.description.isVisible = false
        if (link == "null") binding.pic.isVisible = false

        binding.author.setOnClickListener {
            bundle.putString("Redirect author", author)
            findNavController().navigate(R.id.navigation_notifications, bundle)
        }

        try {
            viewModel.mode = 7
            viewModel.type = commentLink.drop(3)
            viewModel.pagedSubreddit.onEach {
                adapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } catch (e: Exception) {
            println("Error mode 7\nSubredditDetailPost: $e")
        }

        //Кнопка назад
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
    }

    //Клик на имя автора поста
    private fun onPostAuthorClick(item: Response) {
        bundle.putString("Redirect author", item.data.link_author)
        findNavController().navigate(R.id.navigation_notifications, bundle)
    }

    //Клик на имя автора комментария
    private fun onCommentAuthorClick(item: Response) {
        bundle.putString("Redirect author", item.data.author)
        findNavController().navigate(R.id.navigation_notifications, bundle)
    }

    //Клин на название сабреддита
    private fun onSubredditClick(item: Response) {
        bundle.putString("Title", item.data.subreddit)
        bundle.putString("subredditID", item.data.subreddit)
        bundle.putBoolean("Subscriber", item.data.user_is_subscriber)
        findNavController().navigate(R.id.subredditDetails, bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SubredditDetailPost()
    }
}