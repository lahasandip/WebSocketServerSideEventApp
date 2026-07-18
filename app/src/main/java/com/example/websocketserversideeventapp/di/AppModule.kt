package com.example.websocketserversideeventapp.di

import com.example.websocketserversideeventapp.data.remote.BitcoinWebSocketService
import com.example.websocketserversideeventapp.data.remote.NetworkClient
import com.example.websocketserversideeventapp.data.remote.WikimediaApi
import com.example.websocketserversideeventapp.data.remote.WikimediaApiImpl
import com.example.websocketserversideeventapp.data.remote.WikimediaSseService
import com.example.websocketserversideeventapp.data.repository.BitcoinRepositoryImpl
import com.example.websocketserversideeventapp.data.repository.WikiEditRepositoryImpl
import com.example.websocketserversideeventapp.domain.repository.BitcoinRepository
import com.example.websocketserversideeventapp.domain.repository.WikiEditRepository
import com.example.websocketserversideeventapp.domain.usecase.BitcoinPriceUseCase
import com.example.websocketserversideeventapp.domain.usecase.WikimediaSseEventUseCase
import com.example.websocketserversideeventapp.presentation.viewmodel.BitcoinViewModel
import com.example.websocketserversideeventapp.presentation.viewmodel.WikiEditViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single { NetworkClient.okHttpClient }
    single { NetworkClient.json }
    singleOf(::WikimediaApiImpl) bind WikimediaApi::class
    singleOf(::WikimediaSseService)
    singleOf(::BitcoinWebSocketService)
}

val repositoryModule = module {
    singleOf(::WikiEditRepositoryImpl) bind WikiEditRepository::class
    singleOf(::BitcoinRepositoryImpl) bind BitcoinRepository::class
}

val useCaseModule = module {
    factoryOf(::WikimediaSseEventUseCase)
    factoryOf(::BitcoinPriceUseCase)
}

val viewModelModule = module {
    viewModelOf(::WikiEditViewModel)
    viewModelOf(::BitcoinViewModel)
}

val appModule = listOf(
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
