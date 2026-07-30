package com.medsy.presentation.orderdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun RequestDetailsShimmer(
    modifier: Modifier = Modifier,
) {
    MedsyShimmer(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Info Card Shimmer
            MedsyShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Section Header Shimmer
            MedsyShimmerPlaceholder(
                modifier = Modifier
                    .width(140.dp)
                    .height(22.dp)
            )

            // Medicines List Shimmer (2 items)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Checkbox placeholder
                        MedsyShimmerPlaceholder(
                            modifier = Modifier.size(24.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // Image placeholder
                        MedsyShimmerPlaceholder(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        // Text column
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MedsyShimmerPlaceholder(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(18.dp)
                            )
                            MedsyShimmerPlaceholder(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .height(14.dp)
                            )
                        }
                    }
                }
            }

            // Prescription Section title
            MedsyShimmerPlaceholder(
                modifier = Modifier
                    .width(110.dp)
                    .height(20.dp)
            )

            // Prescription Box Shimmer
            MedsyShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Total Summary row shimmer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .width(80.dp)
                        .height(18.dp)
                )
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .width(100.dp)
                        .height(22.dp)
                )
            }

            // Buttons shimmer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                )
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                )
            }

        }
    }
}
