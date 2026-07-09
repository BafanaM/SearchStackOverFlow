package com.example.stackoverflow.feature.detail

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("MMM d ''yy 'at' HH:mm").withZone(ZoneId.systemDefault())

internal fun formatDateTime(epochSeconds: Long): String =
    dateFormatter.format(Instant.ofEpochSecond(epochSeconds))
