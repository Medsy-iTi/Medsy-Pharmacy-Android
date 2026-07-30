package com.medsy.presentation.orders.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun RequestsShimmer(modifier: Modifier = Modifier) {
    MedsyShimmer(modifier = modifier) {
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(5) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                MedsyShimmerPlaceholder(
                                    modifier = Modifier
                                        .height(20.dp)
                                        .width(100.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                MedsyShimmerPlaceholder(
                                    modifier = Modifier
                                        .height(16.dp)
                                        .width(60.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }
                            MedsyShimmerPlaceholder(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(80.dp),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        MedsyShimmerPlaceholder(
                            modifier = Modifier
                                .height(48.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

            }
        }
    }
}
