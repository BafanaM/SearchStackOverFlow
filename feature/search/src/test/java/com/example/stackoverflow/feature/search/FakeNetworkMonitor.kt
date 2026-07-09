package com.example.stackoverflow.feature.search

import com.example.stackoverflow.core.network.connectivity.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeNetworkMonitor(initiallyOnline: Boolean = true) : NetworkMonitor {

    private val _isOnline = MutableStateFlow(initiallyOnline)
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun setOnline(online: Boolean) {
        _isOnline.value = online
    }
}
