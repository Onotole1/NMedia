package ru.netology.nmedia.db

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dbModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDb::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory {
        val appDb = get<AppDb>()
        appDb.postDao()
    }
}