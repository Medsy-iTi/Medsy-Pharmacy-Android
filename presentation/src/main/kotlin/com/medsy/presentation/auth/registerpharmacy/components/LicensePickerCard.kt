package com.medsy.presentation.auth.registerpharmacy.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.medsy.presentation.R

@Composable
internal fun LicensePickerCard(
    licenseName: String?,
    licenseSizeKb: Int?,
    @StringRes licenseErrorRes: Int?,
    onLicenseSelected: (
        displayName: String,
        mimeType: String?,
        bytes: ByteArray,
    ) -> Unit,
    onLicenseSelectionFailed: (messageRes: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasLicense = licenseName != null
    val contentResolver = LocalContext.current.contentResolver
    val licensePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        when (val result = contentResolver.readSelectedLicense(uri)) {
            LicenseReadResult.ReadError -> onLicenseSelectionFailed(
                R.string.pharmacy_registration_error_read_pdf,
            )

            LicenseReadResult.TooLarge -> onLicenseSelectionFailed(
                R.string.pharmacy_registration_error_pdf_too_large,
            )

            is LicenseReadResult.Success -> onLicenseSelected(
                result.displayName,
                result.mimeType,
                result.bytes,
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.pharmacy_registration_license_pdf_title),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text = stringResource(R.string.pharmacy_registration_license_pdf_limit),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (hasLicense) {
                Text(
                    text = stringResource(
                        R.string.pharmacy_registration_license_selected,
                        licenseName.orEmpty(),
                        licenseSizeKb ?: 0,
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }

            licenseErrorRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                    ),
                )
            }

            OutlinedButton(
                onClick = { licensePicker.launch(arrayOf(PDF_MEDIA_TYPE)) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.UploadFile,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        if (hasLicense) {
                            R.string.pharmacy_registration_replace_license
                        } else {
                            R.string.pharmacy_registration_select_license
                        },
                    ),
                )
            }
        }
    }
}
