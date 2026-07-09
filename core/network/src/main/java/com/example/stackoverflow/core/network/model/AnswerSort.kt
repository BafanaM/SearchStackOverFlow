package com.example.stackoverflow.core.network.model

enum class AnswerSort(val apiSort: String, val apiOrder: String, val label: String) {
    ACTIVE(apiSort = "activity", apiOrder = "desc", label = "Active"),
    OLDEST(apiSort = "creation", apiOrder = "asc", label = "Oldest"),
    VOTES(apiSort = "votes", apiOrder = "desc", label = "Votes"),
}
