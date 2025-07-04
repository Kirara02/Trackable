package com.uniguard.trackable.domain.scanner.model

data class ScanResult(
    val rawData: String,
    val length: Int,
    val hex: String,
    val printable: String,
    val extraStr: String
)