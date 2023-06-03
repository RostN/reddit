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
import com.example.finalattestation.api.SubredditPagingPostAdapter
import com.example.finalattestation.databinding.FragmentFavoriteSubredditSavedBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoriteFragmentSubredditSaved : Fragment() {

    private var _binding: FragmentFavoriteSubredditSavedBinding? = null
    private val binding get() = _binding!!
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

        _binding = FragmentFavoriteSubredditSavedBinding.inflate(inflater, container, false)
        //Установка адаптера
        binding.recycler.adapter = subredditAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            viewModel.mode = 4
            viewModel.id = myUserName
            viewModel.type = "links"
            viewModel.pagedSubreddit.onEach {
                subredditAdapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } catch (e: Exception) {
            println("Error mode 4\nFavoriteFragmentSubredditSaved: $e")
        }
        binding.Title.isVisible = subredditAdapter.itemCount == 0
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
        fun newInstance() = FavoriteFragmentSubredditSaved()
    }
}