package com.sky.chessplay.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OnlineEngine

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineEngine
