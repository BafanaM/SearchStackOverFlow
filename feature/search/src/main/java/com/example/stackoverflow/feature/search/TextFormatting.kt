package com.example.stackoverflow.feature.search

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("MMM d ''yy").withZone(ZoneId.systemDefault())

internal fun formatAskedDate(epochSeconds: Long): String =
    dateFormatter.format(Instant.ofEpochSecond(epochSeconds))
