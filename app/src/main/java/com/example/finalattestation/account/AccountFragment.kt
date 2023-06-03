package com.example.finalattestation.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.finalattestation.*
import com.example.finalattestation.api.retrofit
import com.example.finalattestation.auth.AuthViewModel
import com.example.finalattestation.auth.LoginActivity
import com.example.finalattestation.auth.launchAndCollectIn
import com.example.finalattestation.databinding.FragmentAccountBinding
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val bundle = Bundle()

    private val viewModelExit: AuthViewModel by viewModels()
    private val logoutResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModelExit.webLogoutComplete()
        } else {
            // логаут отменен
            // делаем complete тк github не редиректит после логаута и пользователь закрывает CCT
            viewModelExit.webLogoutComplete()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Если был редирект на пользователя, открыть страничку пользователя
        val redirect = arguments?.getString("Redirect author").toString()
        if (redirect != "null") {
            bundle.putString("Author", redirect)
            parentFragmentManager.popBackStack()
            findNavController().navigate(R.id.userInfoFragment, bundle)
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                val repository = retrofit.loadMyAccount()

                //Установка данных
                val link = repository.icon_img.replace("&amp;", "&")
                binding.avatar.load(link) {
                    transformations(CircleCropTransformation())
                }
                binding.FullName.text = repository.subreddit.title
                if (repository.subreddit.title == "") binding.FullName.isVisible = false
                binding.nickname.text = "@${repository.name}"
                binding.karma.text =
                    "${getString(R.string.users_info_karma)} ${repository.total_karma}"
                binding.subscribers.text =
                    "${getString(R.string.users_info_subscribers)} ${repository.subreddit.subscribers}"
                binding.created.text = "${getString(R.string.users_info_created)} " +
                        formatter.format(repository.created * 1000)

                //Кнопка очистить сохраненные
                binding.clearBtn.setOnClickListener {
                    var round = 1
                    viewLifecycleOwner.lifecycleScope.launch {
                        while (round > 0) {
                            val clearRepository =
                                retrofit.loadSaved(username = myUserName).data.children
                            if (clearRepository.isNotEmpty()) {
                                round = if (clearRepository.size == 25) 1 else 0
                                clearRepository.onEach {
                                    retrofit.clearSaved(name = it.data.name)
                                }
                            } else round = -1

                            if (round == -1) {
                                val emptyList = getString(R.string.clear_btn_no_data)
                                Toast.makeText(requireContext(), emptyList, Toast.LENGTH_LONG)
                                    .show()
                            }
                            if (round == 0) {
                                val done = getString(R.string.clear_btn_done)
                                Toast.makeText(requireContext(), done, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                //Кнопка выйти
                binding.exitBtn.setOnClickListener {
                    viewModelExit.logout()
                }
            }

            viewModelExit.logoutPageFlow.launchAndCollectIn(viewLifecycleOwner) {
                logoutResponse.launch(it)
            }

            viewModelExit.logoutCompletedFlow.launchAndCollectIn(viewLifecycleOwner) {
                val intent = Intent(activity, LoginActivity::class.java)
                activity?.finish()
                editor = sharedPreferences.edit()
                editor.putString("TOKEN", "")
                editor.apply()
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}