package com.medsy.data.local.auth

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.medsy.domain.auth.model.AccountRole
import com.medsy.domain.auth.model.PharmacyAccount
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.model.PharmacySession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val mutableSession = MutableStateFlow(readSessionInternal())
    val session = mutableSession.asStateFlow()

    fun save(value: PharmacySession) {
        preferences.edit {
            putString(KEY_ACCESS_TOKEN, value.accessToken)
            putString(KEY_REFRESH_TOKEN, value.refreshToken)
            putLong(KEY_ACCOUNT_ID, value.account.id)
            putString(KEY_DISPLAY_NAME, value.account.displayName)
            putString(KEY_ROLE, value.account.role.name)
            putString(KEY_APPROVAL_STATUS, value.account.approvalStatus.name)
        }
        mutableSession.value = value
    }

    fun updateTokens(accessToken: String, refreshToken: String) {
        val current = mutableSession.value ?: return
        save(current.copy(accessToken = accessToken, refreshToken = refreshToken))
    }

    fun clear() {
        preferences.edit { clear() }
        mutableSession.value = null
    }

    fun accessToken(): String? = preferences.getString(KEY_ACCESS_TOKEN, null)
    fun refreshToken(): String? = preferences.getString(KEY_REFRESH_TOKEN, null)

    private fun readSessionInternal(): PharmacySession? {
        val accessToken = accessToken() ?: return null
        val refreshToken = refreshToken() ?: return null
        val accountId = preferences.getLong(KEY_ACCOUNT_ID, -1L).takeIf { it >= 0 } ?: return null
        val displayName = preferences.getString(KEY_DISPLAY_NAME, null) ?: return null
        val status = preferences.getString(KEY_APPROVAL_STATUS, null)
            ?.let { runCatching { PharmacyApprovalStatus.valueOf(it) }.getOrNull() }
            ?: return null

        return PharmacySession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            account = PharmacyAccount(
                id = accountId,
                displayName = displayName,
                role = AccountRole.PHARMACIST,
                approvalStatus = status,
            ),
        )
    }

    private companion object {
        const val PREFERENCES_NAME = "medsy_pharmacy_auth"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ACCOUNT_ID = "account_id"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_ROLE = "role"
        const val KEY_APPROVAL_STATUS = "approval_status"
    }
}
