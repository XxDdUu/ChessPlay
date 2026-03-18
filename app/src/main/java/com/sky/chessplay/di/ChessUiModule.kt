package com.sky.chessplay.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import model.service.ChessUiService
import model.service.DefaultChessUiService

@Module
@InstallIn(ViewModelComponent::class)
abstract class ChessUiModule {

    @Binds
    abstract fun bindChessUiService(
        impl: DefaultChessUiService
    ): ChessUiService
}
