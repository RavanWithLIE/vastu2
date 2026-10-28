package com.mycompany.myapp

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class MonthEventsResponse(
    val year: Int,
    val month: Int,
    val totalEvents: Int,
    val days: Map<String, List<CalendarEvent>>
)

data class CalendarEvent(
    val eventId: String,
    val type: String,
    val typeName: String,
    val title: String,
    val description: String?,
    val startAt: String,
    val endAt: String,
    val status: String,
    val metadata: Map<String, JsonElement>?
)