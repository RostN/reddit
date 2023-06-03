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
import com.example.finalattestation.R
import com.example.finalattestation.api.Response
import com.example.finalattestation.SubredditViewModel
import com.example.finalattestation.databinding.FragmentFavoriteSubredditAllBinding
import com.example.finalattestation.api.SubredditPagingAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoriteFragmentSubredditAll : Fragment() {

    private var _binding: FragmentFavoriteSubredditAllBinding? = null
    private val binding get() = _binding!!
    val bundle = Bundle()

    private val subredditAdapter =
        SubredditPagingAdapter { Responce -> onItemClick(Responce) }

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
        _binding = FragmentFavoriteSubredditAllBinding.inflate(inflater, container, false)
        //Установка адаптера
        binding.recycler.adapter = subredditAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            //Установка типа загрузки
            viewModel.mode = 3
            //Загрузка данных в адаптер
            viewModel.pagedSubreddit.onEach {
                subredditAdapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } catch (e: Exception) {
            println("Error mode 3\nFavoriteFragmentSubredditAll: $e")
        }
        binding.Title.isVisible = subredditAdapter.itemCount == 0
    }

    //Функция нажатия на элемент
    private fun onItemClick(item: Response) {
        bundle.putString("Title", item.data.title)
        bundle.putString("subredditID", item.data.display_name)
        bundle.putBoolean("Subscriber", item.data.user_is_subscriber)
        findNavController().navigate(R.id.subredditDetails, bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragmentSubredditAll()
    }
}