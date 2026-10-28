package com.mycompany.myapp

import android.R.attr.fontWeight
import android.R.attr.text
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class MainActivity: ComponentActivity(){

    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme() {
                CalendarScreen(viewModel=viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel){
    var currentMonth by remember { mutableStateOf(YearMonth.now())}

    var selectedDate by remember { mutableStateOf<LocalDate?>(null)}

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(currentMonth){
        viewModel.loadMonth(currentMonth)
    }

    val monthResponse= uiState.response?.takeIf {
        it.year==currentMonth.year && it.month==currentMonth.monthValue
    }

    val monthEvents= monthResponse?.days ?:emptyMap()

    val selectedEvents=selectedDate?.let { monthEvents[it.toString()].orEmpty()}.orEmpty()

    Scaffold(containerColor = Color(0xFF7F8FC)) {
        padding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ){
            Text(
                text = "My Calendar",
                fontWeight = FontWeight.Bold,
                color=Color(0xFF172033)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Keep Track of your events",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = {
                        currentMonth=currentMonth.minusMonths(1)
                        selectedDate=null
                    }
                ) {
                    Text("<", fontSize = 36.sp)
                }

                Text(
                    text = currentMonth.month.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    ) + " ${currentMonth.year}",
                    fontSize = 20.sp,
                    fontWeight= FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        currentMonth=currentMonth.plusMonths(1)
                        selectedDate=null
                    }
                ) {
                    Text(">", fontSize = 36.sp)
                }
            }

            Spacer(Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape=RoundedCornerShape(20.dp),
                color=Color.White,
                shadowElevation = 2.dp
            ){
                Column(modifier = Modifier.padding(10.dp))
                {
                    Row(modifier = Modifier.fillMaxWidth()){
                        listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun").forEach{
                            day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight= FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    val firstDayoffset=currentMonth.atDay(1).dayOfWeek.value-1

                    val totalDays: Int = currentMonth.lengthOfMonth()

                    val totalCells= ((firstDayoffset + totalDays.toLong() + 6) / 7) * 7

                    val today=LocalDate.now()

                    for (weekStart in 0 until totalCells step 7){
                        Row(modifier = Modifier.fillMaxWidth()){
                            for (column in 0 until 7){
                                val cellIndex=weekStart+column
                                val dayNumber=cellIndex-firstDayoffset+1

                                if (dayNumber in 1..totalDays){
                                    val date=currentMonth.atDay(dayNumber.toInt())

                                    val dateKey=date.toString()

                                    val hasEvents=monthEvents[dateKey]?.isNotEmpty()==true

                                    val isSelected=selectedDate==date

                                    val isToday=date==today

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(58.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isSelected -> Color(0xFF2457E6)
                                                    else ->
                                                        Color.Transparent
                                                }
                                            )
                                            .clickable {
                                                selectedDate = date
                                            },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayNumber.toString(),
                                            color=when {
                                                isSelected-> Color.White
                                                isToday-> Color(0xFF2457E6)
                                                else -> Color(0xFF202638)
                                            },
                                            fontSize = 15.sp,
                                            fontWeight= if (isToday || isSelected){
                                                FontWeight.Bold
                                            } else{
                                                FontWeight.Normal
                                            }
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (hasEvents){
                                                        if (isSelected){
                                                            Color.White
                                                        } else {
                                                            Color(0xFF2457E6)
                                                        }
                                                    } else {
                                                        Color.Transparent
                                                    }
                                                )
                                            )

                                    }
                                } else {
                                    Spacer(Modifier.weight(1f).height(58.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            when {
                uiState.isLoading && monthResponse==null->{
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text("Loading events....")
                }

                uiState.error != null && monthResponse==null ->{
                    Text(
                        text=uiState.error?: "Something went Wrong",
                        color= MaterialTheme.colorScheme.error
                    )

                    Spacer(Modifier.height(8.dp))

                    Button(onClick = {
                        viewModel.loadMonth(currentMonth)
                    }) {
                        Text("Retry")
                    }
                }

                monthResponse !=null ->{
                    Text(
                        text = "${monthResponse.totalEvents} events this month",
                        color=Color.Gray,
                        fontSize = 14.sp
                    )
                }

                else ->{
                    Text(
                        text = "Loading calendar...",
                        color=Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Tap a date to view its Events",
                color=Color.Gray,
                fontSize = 13.sp
            )
        }
    }

    if (selectedDate!=null){
        ModalBottomSheet(
            onDismissRequest = {
                selectedDate=null
            },
            containerColor = Color.White,
            dragHandle = {
                BottomSheetDefaults.DragHandle()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(horizontal=20.dp)
            ){
                Text(
                    text = selectedDate!!.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy",
                        Locale.ENGLISH)
                    ),
                    fontSize = 20.sp,
                    fontWeight= FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${selectedEvents.size} Events",
                    color=Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(18.dp))

                if (monthResponse== null && uiState.isLoading){
                    CircularProgressIndicator()
                } else if (monthResponse == null && uiState.error != null){
                    Text(
                        text = uiState.error?:"Unable to load Events for the day",
                        color=Color.Gray
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(
                            items=selectedEvents,
                            key = { it.eventId}
                        ){
                            event->
                            EventCard(event=event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: CalendarEvent) {

    val eventColor = when (event.type.uppercase()) {
        "MEETING" -> Color(0xFF2457E6)
        "EXAM" -> Color(0xFFB45309)
        "HOLIDAY" -> Color(0xFF16803D)
        "MAINTENANCE" -> Color(0xFF7C3AED)
        else -> Color(0xFF64748B)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF7F8FC)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Event type
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = eventColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = event.typeName,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    color = eventColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = event.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202638)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${formatEventTime(event.startAt)} – ${
                    formatEventTime(event.endAt)
                }",
                color = Color(0xFF2457E6),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Status: ${event.status}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            if (!event.description.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))

                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            // Display arbitrary metadata fields
            event.metadata.orEmpty().forEach { (key, value) ->

                val displayValue = when {
                    value.isJsonNull -> null
                    value.isJsonPrimitive &&
                            value.asJsonPrimitive.isString ->
                        value.asString
                    else -> value.toString()
                }

                if (!displayValue.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "${key.toDisplayLabel()}: $displayValue",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

private fun formatEventTime(value: String): String {
    return try {
        val instant = Instant.parse(value)

        val formatter = DateTimeFormatter
            .ofPattern("h:mm a", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())

        formatter.format(instant)
    } catch (e: Exception) {
        value
    }
}

private fun String.toDisplayLabel(): String {
    return replace("_", " ")
        .replace(Regex("([a-z])([A-Z])"), "$1 $2")
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ENGLISH)
            else it.toString()
        }
}
