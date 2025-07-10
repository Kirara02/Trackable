package com.uniguard.trackable.data.mapper

interface Mapper<F, T> {
    fun map(from: F): T
//    fun reverse(from: T): F
}