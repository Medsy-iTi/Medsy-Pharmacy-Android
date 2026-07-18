package com.medsy.presentation.auth.approval

import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.medsy.presentation.R
import com.medsy.presentation.common.PlaceholderScaffold

@Composable
fun ApprovalRoot(
    status: ApprovalScreenStatus,
    openLogin: () -> Unit,
) {
    val title = when (status) {
        ApprovalScreenStatus.Pending -> R.string.approval_pending_title
        ApprovalScreenStatus.Rejected -> R.string.approval_rejected_title
        ApprovalScreenStatus.Suspended -> R.string.approval_suspended_title
    }
    val supporting = when (status) {
        ApprovalScreenStatus.Pending -> R.string.approval_pending_supporting
        ApprovalScreenStatus.Rejected -> R.string.approval_rejected_supporting
        ApprovalScreenStatus.Suspended -> R.string.approval_suspended_supporting
    }
    ApprovalScreen(title, supporting, openLogin)
}

@Composable
private fun ApprovalScreen(
    @StringRes title: Int,
    @StringRes supporting: Int,
    openLogin: () -> Unit,
) {
    PlaceholderScaffold(
        title = stringResource(title),
        supportingText = stringResource(supporting),
    ) {
        Button(onClick = openLogin) {
            Text(stringResource(R.string.approval_back_to_login))
        }
    }
}

enum class ApprovalScreenStatus {
    Pending,
    Rejected,
    Suspended,
}
