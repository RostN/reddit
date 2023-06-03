package com.example.finalattestation.subreddit

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalattestation.R
import com.example.finalattestation.api.Response
import com.example.finalattestation.api.SubredditPagingAdapter
import com.example.finalattestation.SubredditViewModel
import com.example.finalattestation.databinding.FragmentSubredditBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SubredditFragment : Fragment() {
    private var _binding: FragmentSubredditBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabAdapter: SubredditTabAdapter
    var searchRequest = ""
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubredditBinding.inflate(inflater, container, false)
        //Установка адаптера
        binding.recycler.adapter = subredditAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subredditContext = requireContext()

        binding.searchLine.setOnFocusChangeListener { v, b ->
            binding.searchLine.hint=getString(R.string.subreddit_search_line_hint_2)
        }

        //Строка ввода
        binding.searchLine.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            binding.searchLine.hint = getString(R.string.subreddit_search_line_hint_2)
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                searchRequest = "${binding.searchLine.text}"
                v.hideKeyboard()
                changeView()
                startSearch(searchRequest)
                binding.searchLine.isFocusable = false
                binding.searchLine.text.clear()
                binding.searchLine.hint =
                    getString(R.string.subreddit_search_line_hint_result) + searchRequest
                return@OnKeyListener true
            } else false
        })

        //Кнопка закрыть окно поиска
        binding.closeSearch.setOnClickListener {
            changeView()
            binding.searchLine.hint = getString(R.string.subreddit_search_line_hint)
        }

        //Фрагменты для таба
        val fragmentList = listOf(
            SubredditFragmentNew.newInstance(),
            SubredditFragmentPopular.newInstance()
        )

        //Заголовки для табов
        val fragmentTitle = listOf(
            getString(R.string.subreddit_tab_new),
            getString(R.string.subreddit_tab_popular)
        )

        //Установка адаптера для вьюпейджера
        tabAdapter = SubredditTabAdapter(this, fragmentList)
        binding.viewPager.adapter = tabAdapter

        //Установка заголовков и синхронизация пейджера с табом
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = fragmentTitle[position]
        }.attach()
    }

    //Запуск поиска
    private fun startSearch(request: String) {
        try {
            //Установка типа загрузки
            viewModel.id = request
            viewModel.mode = 6
            //Загрузка данных в адаптер
            viewModel.pagedSubreddit.onEach {
                subredditAdapter.submitData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        } catch (e: Exception) {
            println("Error mode 6\nSubredditFragment: $e")
        }
    }

    //Изменение видимости частей экрана
    private fun changeView() {
        binding.tabsControl.isVisible = !binding.tabsControl.isVisible
        binding.searchControl.isVisible = !binding.searchControl.isVisible
    }

    //Скрытие клавиатуры
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    //Функция нажатия на элемент
    private fun onItemClick(item: Response) {
        bundle.putString("Title", item.data.title)
        bundle.putString("subredditID", item.data.display_name)
        bundle.putBoolean("Subscriber", item.data.user_is_subscriber)
        findNavController().navigate(R.id.subredditDetails, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}