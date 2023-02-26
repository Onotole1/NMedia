package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import java.net.ConnectException

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    shares = 0,
    videoUrl = null,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun refreshPosts() {
        _data.value = FeedModel(refreshing = true)
        repository.getAllAsync((object  : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        }))
    }

    fun loadPosts() {
       _data.value = FeedModel(loading = true)
        repository.getAllAsync((object  : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        }))
    }

    fun likeById(post: Post) {
        repository.likeByIdAsync(post, object : PostRepository.Callback<Post> {
            override fun onError(e: Exception) {
                _data.postValue(
                    if (e is ConnectException) FeedModel(error = true) else FeedModel(connectionError = true)
                )
            }

            override fun onSuccess(post: Post) {
                _data.postValue(
                    FeedModel(posts =
                    _data.value!!.posts.map {
                        if (post.id == it.id)
                        {post.copy(likedByMe = post.likedByMe, likes = post.likes) }
                        else {
                            it
                        }
                    })
                )
            }

        })
    }

    fun unLikeById(post: Post) {
        repository.unLikeByIdAsync(post, object : PostRepository.Callback<Post> {
            override fun onError(e: Exception) {
                _data.postValue(
                    if (e is ConnectException) FeedModel(error = true) else FeedModel(connectionError = true)
                )
            }

            override fun onSuccess(post: Post) {
                _data.postValue(
                    FeedModel(posts =
                    _data.value!!.posts.map {
                        if (post.id == it.id)
                        {post.copy(likedByMe = post.likedByMe, likes = post.likes) }
                        else {
                            it
                        }
                    })
                )
            }

        })
    }

    fun deleteById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.deleteByIdAsync(id, object : PostRepository.CallbackUnit<Unit> {
            override fun onError(e: Exception) {
                _data.postValue(
                    if (e is ConnectException) FeedModel(error = true) else FeedModel(connectionError = true)
                )
            }

            override fun onSuccess() {
                try {
                    _data.postValue(
                        _data.value?.copy(posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id })
                    )

                } catch (e: IOException) {
                    _data.postValue(_data.value?.copy(posts = old))
                }
            }

        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.Callback<Post> {
                override fun onError(e: Exception) {
                    _data.postValue(
                        if (e is ConnectException) FeedModel(error = true) else FeedModel(connectionError = true)
                    )
                }

                override fun onSuccess(post: Post) {
                    _data.postValue((FeedModel()))
                    _postCreated.postValue(Unit)
                    loadPosts()
                }
            })
        }
        edited.value = empty
    }



    fun shareById(id: Long) = repository.shareById(id)


    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        var text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

//    fun getById(id: Long) {
//        repository.getByIdAsync(id, object : PostRepository.GetByIdCallback {
//            override fun onError(e: Exception) {
//                _data.postValue(FeedModel(error = true))
//            }
//
//            override fun onSuccess(post: Post) {
//                _data.postValue()
//            }
//
//        })
//    }
}