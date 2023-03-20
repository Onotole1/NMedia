package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPhotoBinding


class PhotoFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(inflater, container, false)

        val url = "http://10.0.2.2:9999/media/${requireArguments().getString("url")}"

        Glide.with(binding.photo)
            .load(url)
            .placeholder(R.drawable.baseline_image_search_24)
            .error(R.drawable.ic_baseline_error_24)
            .into(binding.photo)

        return binding.root
    }
}