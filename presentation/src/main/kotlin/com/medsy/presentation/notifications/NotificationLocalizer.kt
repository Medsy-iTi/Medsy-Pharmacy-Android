package com.medsy.presentation.notifications

import android.content.Context
import com.medsy.presentation.R

object NotificationLocalizer {

    private const val CATEGORY_REQUEST_IN_AREA = "REQUEST_IN_AREA"
    private const val TITLE_NEW_REQUEST_NEARBY = "New request nearby"
    private const val TITLE_NEW_PATIENT_REQUEST = "New Patient Request"

    private const val BODY_SUBSTRING_NEW_REQUEST = "new request is available"
    private const val BODY_SUBSTRING_NEW_PRESCRIPTION = "new prescription request"

    fun getLocalizedTitle(context: Context, category: String, defaultTitle: String): String {
        return when (category) {
            CATEGORY_REQUEST_IN_AREA -> context.getString(R.string.new_patient_request_title)
            else -> {
                when (defaultTitle) {
                    TITLE_NEW_REQUEST_NEARBY, TITLE_NEW_PATIENT_REQUEST -> context.getString(R.string.new_patient_request_title)
                    else -> defaultTitle
                }
            }
        }
    }

    fun getLocalizedBody(context: Context, category: String, defaultBody: String): String {
        return when (category) {
            CATEGORY_REQUEST_IN_AREA -> context.getString(R.string.new_patient_request_body)
            else -> {
                if (defaultBody.contains(BODY_SUBSTRING_NEW_REQUEST, ignoreCase = true) ||
                    defaultBody.contains(BODY_SUBSTRING_NEW_PRESCRIPTION, ignoreCase = true)) {
                    context.getString(R.string.new_patient_request_body)
                } else {
                    defaultBody
                }
            }
        }
    }
}