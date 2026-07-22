package com.medsy.domain.auth.usecase

import javax.inject.Inject

class ValidateEgyptPhoneUseCase @Inject constructor() {
    operator fun invoke(phone: String): Boolean {
        val regex = Regex("^01[0125][0-9]{8}$")
        return regex.matches(phone)
    }
}
