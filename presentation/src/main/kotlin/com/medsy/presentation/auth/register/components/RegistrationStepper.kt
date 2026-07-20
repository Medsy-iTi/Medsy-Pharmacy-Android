package com.medsy.presentation.auth.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medsy.presentation.R

@Composable
fun RegistrationStepper(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        R.string.auth_step_personal_data to Icons.Default.Person,
        R.string.auth_step_pharmacy_info to Icons.Default.Assignment,
        R.string.auth_step_documents to Icons.Default.Security,
        R.string.auth_step_review to Icons.Default.Check
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            val stepNumber = index + 1
            val isActive = stepNumber <= currentStep
            val isCurrent = stepNumber == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.CenterStart)
                                .padding(end = 20.dp),
                            thickness = 2.dp,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    if (index < steps.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.CenterEnd)
                                .padding(start = 20.dp),
                            thickness = 2.dp,
                            color = if (stepNumber < currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface
                            )
                            .then(
                                if (!isActive) Modifier.background(
                                    MaterialTheme.colorScheme.outlineVariant.copy(
                                        alpha = 0.5f
                                    )
                                )
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.second,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Text(
                    text = stringResource(step.first),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 12.sp
                    ),
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
