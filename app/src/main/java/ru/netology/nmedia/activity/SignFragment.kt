package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.SignViewModel

class SignFragment : Fragment() {

    private val viewModel: SignViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignBinding.inflate(inflater, container, false)

        binding.signInButton.setOnClickListener {
            if (binding.username.text.isNullOrBlank() || binding.password.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.empty_login),
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                viewModel.signIn(binding.username.text.toString(), binding.password.text.toString())
                AndroidUtils.hideKeyboard(requireView())
                //findNavController().navigateUp()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.connectionError) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.server_not_connected),
                    Toast.LENGTH_LONG
                )
                    .show()
            } else if (state.loginAndPassError) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.login_pass_error),
                    Toast.LENGTH_LONG
                )
                    .show()
            } else if (state.successfulRequest) {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

}