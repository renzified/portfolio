package com.rsalanga.roadmap.shared.domain.common

sealed interface AppResult<out D, out E : DomainError> {
    data class Success<out D>(val data: D) : AppResult<D, Nothing>
    data class Failure<out E : DomainError>(val error: E) : AppResult<Nothing, E>
}
