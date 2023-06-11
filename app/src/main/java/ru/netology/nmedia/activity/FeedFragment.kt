package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.DataModel
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val appAuth: AppAuth by inject()

    private val dataModel: DataModel by activityViewModel()
    private val viewModel: PostViewModel by activityViewModel()

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onPostClick(post: Post) {
                val postClikedId = post.id
                dataModel.postIdMessage.value = postClikedId
                findNavController().navigate(R.id.action_feedFragment_to_postFragment)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val text = post.content
                val bundle = Bundle()
                bundle.putString("editedText", text)
                findNavController().navigate(R.id.action_feedFragment_to_editPostFragment, bundle)
            }

            override fun onLike(post: Post) {
                if (post.likedByMe) viewModel.unLikeById(post) else viewModel.likeById(post)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onDelete(post: Post) {
                viewModel.deleteById(post.id)
            }

            override fun onPlayVideo(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                startActivity(intent)
            }

            override fun onShowPhoto(post: Post) {
                val likes = post.likes.toString()
                val id = post.id
                val isLikedByMe = post.likedByMe
                val url = post.attachment!!.url
                val bundle = Bundle()
                bundle.putString("likes", likes)
                bundle.putBoolean("likedByMe", isLikedByMe)
                bundle.putString("url", url)
                bundle.putLong("id", id)
                findNavController().navigate(R.id.action_feedFragment_to_photo, bundle)
            }
        })

        binding.list.adapter = adapter
        binding.list.itemAnimator = null // эта вставка должна помочь с  проблемой мерцания

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.connectionLost.isVisible = state.connectionError
        }

        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data.posts)
            binding.emptyText.isVisible = data.empty
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.state.observe(viewLifecycleOwner, { state ->
                binding.swipeRefresh.isRefreshing = state.refreshing
            })
            viewModel.refreshPosts()
        }

        //Add post button
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.newerPostLoad.show()
            }
        }

        binding.newerPostLoad.setOnClickListener {
            binding.newerPostLoad.hide()
            binding.list.smoothScrollToPosition(0)
            viewModel.refreshPosts()
        }

        var menuProvider: MenuProvider? = null

        authViewModel.state.observe(viewLifecycleOwner) { authState ->
            menuProvider?.let { requireActivity().removeMenuProvider(it) }
            //main_menu
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)

                    //make visible group by authorize
                    menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                    menu.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                    return when (menuItem.itemId) {
                        R.id.signOut -> {
                            appAuth.clear()
                            //HW
                            true
                        }
                        R.id.signIn -> {
                            appAuth.setAuth(5, "x-token")

                            findNavController().navigate(R.id.action_feedFragment_to_signIn)
                            true
                        }
                        R.id.signUp -> {
                            appAuth.setAuth(5, "x-token")
                            //HW
                            true
                        }
                        else -> false
                    }

                }

            }.apply { menuProvider = this }, viewLifecycleOwner)
        }

        return binding.root
    }
}
