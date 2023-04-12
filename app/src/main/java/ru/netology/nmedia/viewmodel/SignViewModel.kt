package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.FeedModelState

import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.net.SocketTimeoutException

class SignViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())


    private val _state = MutableLiveData(AuthModelState())
    val state: LiveData<AuthModelState>
        get() = _state

    fun signIn(login: String, pass: String) = viewModelScope.launch {
//        val response = repository.signIn()
//        response.token?.let { AppAuth.getInstance().setAuth(response.id, response.token) }
        try {
            val response = repository.signIn(login, pass)
            response.token?.let { AppAuth.getInstance().setAuth(response.id, response.token) }
            _state.value = AuthModelState(successfulRequest = true)
        } catch (e: ApiException) {
            _state.value = AuthModelState(loginAndPassError = true)
        } catch (e: Exception) {
            _state.value = AuthModelState(connectionError = true)
        }
    }

}