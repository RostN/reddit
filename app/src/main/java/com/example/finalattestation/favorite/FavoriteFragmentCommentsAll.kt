package com.example.finalattestation.favorite

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
import com.example.finalattestation.*
import com.example.finalattestation.api.Response
import com.example.finalattestation.api.SubredditPagingCommentAdapter
import com.example.finalattestation.databinding.FragmentFavoriteCommentsAllBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoriteFragmentCommentsAll : Fragment() {

    private var _binding: FragmentFavoriteCommentsAllBinding? = null
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
        _binding = FragmentFavoriteCommentsAllBinding.inflate(inflater, container, false)
        //Установка адаптера
        binding.recycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            viewModel.mode = 5
            viewModel.id = myUserName
            viewModel.type = ""
            viewModel.pagedSubreddit.onEach {
                adapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } catch (e: Exception) {
            println("Error mode 5\nFavoriteFragmentSubredditSaved: $e")
        }
        binding.Title.isVisible = adapter.itemCount == 0
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
        fun newInstance() = FavoriteFragmentCommentsAll()
    }
}