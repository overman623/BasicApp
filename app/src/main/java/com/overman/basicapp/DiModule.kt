package com.overman.basicapp

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private val retrofit = provideRetrofit(providesOkHttpClient(providesHttpLoggingInterceptor()))

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideTodoDatabase(@ApplicationContext context: Context) : TodoDatabase {
        return Room.databaseBuilder(
                context,
                TodoDatabase::class.java,
                TodoDatabase.DATABASE_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun provideTodoDAO(todoDB: TodoDatabase): TodoDAO {
        return todoDB.todoDAO()
    }

    @Provides
    fun provideTodoRemoteService(retrofit: Retrofit): TodoRemoteService = retrofit.create(TodoRemoteService::class.java)

    @Provides
    fun provideRemoteTodoRepository() : TodoRemoteRepository {
        return TodoRemoteRepositoryImpl(provideTodoRemoteService(retrofit))
    }

    @Provides
    fun provideLocalTodoRepository(todoDAO: TodoDAO) : TodoLocalRepository {
        return TodoLocalRepositoryImpl(todoDAO)
    }

}