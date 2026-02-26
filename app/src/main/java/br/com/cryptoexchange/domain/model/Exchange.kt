package br.com.cryptoexchange.domain.model

import java.util.Date

data class Exchange(
    val id: Int,
    val name: String,
    val slug: String,
    val logoUrl: String?,
    val description: String?,
    val websiteUrl: String?,
    val dateLaunched: Date?,
    val spotVolumeUsd: Double?,
    val makerFee: Double?,
    val takerFee: Double?,
    val weeklyVisits: Int?,
    val spot: Int?
)
