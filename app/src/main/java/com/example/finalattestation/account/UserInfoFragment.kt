package com.example.finalattestation.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.finalattestation.*
import com.example.finalattestation.databinding.FragmentUserInfoBinding
import com.example.finalattestation.api.SubredditPagingPostAdapter
import com.example.finalattestation.SubredditViewModel
import com.example.finalattestation.api.Repository
import com.example.finalattestation.api.Response
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class UserInfoFragment : Fragment() {
    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val subredditAdapter =
        SubredditPagingPostAdapter(
            onClick = { Responce -> onItemClick(Responce) },
            authorClick = { Responce -> onAuthorClick(Responce) }
        )
    val bundle = Bundle()

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
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        //Установка адаптера
        binding.recycler.adapter = subredditAdapter
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userName = arguments?.getString("Author").toString()

        //Информация о пользователе
        viewLifecycleOwner.lifecycleScope.launch {
            //Получение данных о пользователе и установка их в форму
            val repository = Repository().loadUsersAbout(userName)
            checkSubscribe(repository.subreddit.user_is_subscriber)
            //Если имя не прописано, скрываем его
            if (repository.subreddit.title != "") {
                binding.userTitle.text = repository.subreddit.title
            } else binding.userTitle.isVisible = false

            binding.userCreated.text =
                "${getString(R.string.users_info_created)} ${formatter.format(repository.created * 1000)}"
            binding.userSubscribers.text =
                "${getString(R.string.users_info_subscribers)} ${repository.subscribers}"
            binding.userKarma.text =
                "${getString(R.string.users_info_karma)} ${repository.total_karma}"
            binding.userName.text = "@$userName"
            val avatar = repository.icon_img.replace("&amp;", "&")
            binding.userAvatar.load(avatar) {
                transformations(CircleCropTransformation())
            }

            //Кнопка подписаться/отписаться
            binding.subOrUnsubBtn.setOnClickListener {
                val action = if (repository.subreddit.user_is_subscriber) "unsub" else "sub"
                repository.subreddit.user_is_subscriber = !repository.subreddit.user_is_subscriber
                MainActivity().subscribeOrUnsubscribe(action, "u_${userName}")
                checkSubscribe(repository.subreddit.user_is_subscriber)
            }

            if (repository.is_blocked) {
                val errorMessage = getString(R.string.users_info_banned)
                binding.subOrUnsubBtn.isEnabled = false
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }

            //Получение и загрузка сабреддитов пользователя
            try {
                //Установка типа загрузки
                viewModel.id = userName
                viewModel.mode = 2
                //Загрузка данных в адаптер
                viewModel.pagedSubreddit.onEach {
                    subredditAdapter.submitData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)
            } catch (e: Exception) {
                println("Error mode 2\nUserInfoFragment: $e")
            }
        }
    }

    //Изменение иконки и текста в зависимости от наличия подписки
    private fun checkSubscribe(get: Boolean) {
        if (get) {
            binding.buttonIcon.setImageResource(R.drawable.icon_subscribed)
            binding.textMover.text = getString(R.string.users_indo_subscribed)
            binding.subOrUnsubBtn.text = getString(R.string.users_indo_subscribed)
        } else {
            binding.buttonIcon.setImageResource(R.drawable.icon_subscribe)
            binding.textMover.text = getString(R.string.users_indo_subscribe)
            binding.subOrUnsubBtn.text = getString(R.string.users_indo_subscribe)
        }
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
        fun newInstance() = UserInfoFragment()
    }
}