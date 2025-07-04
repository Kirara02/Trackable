package com.uniguard.trackable.domain.uhf.model

data class InventoryTag(
    val epc: String,
    val mem: String = "",
    val count: Int = 1,
    val rssi: Int = 0
)