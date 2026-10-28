package com.mycompany.myapp

import retrofit2.http.GET
import retrofit2.http.Query

interface EventsApi{

    @GET("events")
    suspend fun getEvents(
        @Query("year")year:Int,
        @Query("month")month:Int
    ): MonthEventsResponse

}