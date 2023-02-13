package ru.netology.nmedia.adapter

import android.net.Uri
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var currentPost: Post

    fun bind(post: Post) {
        currentPost = post
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            //likeCount.text = changeNumber(post.likes)
            like.isChecked = post.likedByMe
            like.text = changeNumber(post.likes)
            share.text = changeNumber(post.shares)

//            like.setImageResource(
//                if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24dp
//            )

            if (post.videoUrl != null) {
                videoLayout.visibility = View.VISIBLE
                videoView.apply {
                    setVideoURI(Uri.parse(post.videoUrl))
                    requestFocus()
                    start()
                }
            } else {
                videoLayout.visibility = View.GONE
            }

            cardPost.setOnClickListener {
                onInteractionListener.onPostClick(post)
            }

            videoLayout.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onDelete(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }


    //This function change number for format of app
    fun changeNumber(count: Int): String {
        val numberFirstToStr: Int
        val numberSecondToStr: Int
        if ((count >= 1_000) && (count < 10_000)) {
            numberFirstToStr = count / 1_000
            numberSecondToStr = ((count % 1_000) / 100)
            if (numberSecondToStr == 0) {
                return "$numberFirstToStr" + "K"
            } else return "$numberFirstToStr.$numberSecondToStr" + "K"
        } else if ((count >= 10_000) && (count < 1_000_000)) {
            numberFirstToStr = count / 1_000
            return "$numberFirstToStr" + "K"
        } else if (count >= 1_000_000) {
            numberFirstToStr = count / 1_000_000
            numberSecondToStr = ((count % 1_000_000) / 100_000)
            if (numberSecondToStr == 0) {
                return "$numberFirstToStr" + "M"
            } else return "$numberFirstToStr.$numberSecondToStr" + "M"
        } else {
            return "$count"
        }
    }
}