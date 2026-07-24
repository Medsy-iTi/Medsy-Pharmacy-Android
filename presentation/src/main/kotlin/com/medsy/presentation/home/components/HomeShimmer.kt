package com.medsy.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medsy.designsystem.components.MedsyShimmer
import com.medsy.designsystem.components.MedsyShimmerPlaceholder

@Composable
fun HomeShimmer() {
    MedsyShimmer(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            MedsyShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            MedsyShimmerPlaceholder(
                modifier = Modifier
                    .size(width = 120.dp, height = 24.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier.size(width = 140.dp, height = 24.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                MedsyShimmerPlaceholder(
                    modifier = Modifier.size(width = 60.dp, height = 20.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            repeat(3) {
                MedsyShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}
