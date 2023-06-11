package ru.netology.nmedia

import android.app.Application
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.netology.nmedia.api.apiModule
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.dbModule
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.repository.TokenRepositoryPreferences
import ru.netology.nmedia.viewmodel.SignViewModel
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.DataModel
import ru.netology.nmedia.viewmodel.PostViewModel

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(apiModule, dbModule, appModule)
        }
    }

    private val appModule = module {
        singleOf(::PostRepositoryImpl).bind<PostRepository>()
        factoryOf(::TokenRepositoryPreferences)
        factoryOf(GoogleApiAvailability::getInstance)
        factoryOf(FirebaseMessaging::getInstance)
        factoryOf(::AppAuth)

        viewModelOf(::AuthViewModel)
        viewModelOf(::DataModel)
        viewModelOf(::SignViewModel)
        viewModelOf(::PostViewModel)
    }
}