package com.mycompany.myapp

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor(private val eventsApi: EventsApi){

    suspend fun getEvents(year:Int,month:Int): MonthEventsResponse{
        return eventsApi.getEvents(year,month)
    }
}