package com.uniguard.trackable.presentation.navigation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class BottomBar(
    val route: String,
    @DrawableRes val icon: Int,
    @DrawableRes val iconFocused: Int,
    @StringRes val titleResId: Int
)

