package com.medsy.presentation.common.util

import androidx.annotation.StringRes
import com.medsy.domain.common.MedsyError
import com.medsy.presentation.R

@StringRes
fun MedsyError.toMessageRes(): Int = when (this) {
    MedsyError.Remote.NoInternet -> R.string.error_no_internet
    MedsyError.Remote.RequestTimeout -> R.string.error_request_timeout
    MedsyError.Remote.Serialization -> R.string.error_serialization
    MedsyError.Remote.EmptyResponse -> R.string.error_empty_response
    MedsyError.Remote.Unknown -> R.string.error_unknown
    is MedsyError.Remote.Http -> when (statusCode) {
        400 -> R.string.auth_error_invalid_credentials
        401 -> R.string.auth_error_authentication_required
        403 -> R.string.auth_error_pharmacists_only
        else -> R.string.error_server
    }

    MedsyError.Auth.INVALID_CREDENTIALS -> R.string.auth_error_invalid_credentials
    MedsyError.Auth.INVALID_ROLE -> R.string.auth_error_pharmacists_only
    MedsyError.Auth.NO_PHARMACY -> R.string.auth_error_no_pharmacy

    MedsyError.Validation.REQUIRED_FIELDS -> R.string.auth_error_required_field
    MedsyError.Validation.INVALID_PHONE_NUMBER -> R.string.auth_error_invalid_phone
    MedsyError.Validation.INVALID_LOCATION -> R.string.error_invalid_location
    MedsyError.Validation.INVALID_LICENSE_DOCUMENT -> R.string.error_invalid_document
    MedsyError.Validation.INVALID_OTP -> R.string.auth_error_invalid_otp

    MedsyError.Local.MEDIA,
    MedsyError.Local.UNKNOWN -> R.string.error_unknown

}
