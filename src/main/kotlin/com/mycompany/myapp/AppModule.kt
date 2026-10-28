package com.mycompany.myapp

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson{
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://p7y66xn5lh.execute-api.ap-south-1.amazonaws.com/prod/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideEventsApi(retrofit: Retrofit): EventsApi{
        return retrofit.create(EventsApi::class.java)
    }
}