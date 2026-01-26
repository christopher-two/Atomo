package org.override.atomo.feature.cv.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun CvShimmer() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(4) {
             AtomoCard(
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(bottom = 16.dp)
            ) {
                 Box(modifier = Modifier.padding(16.dp)) {
                    Column {
                         ShimmerLine(modifier = Modifier.width(180.dp).height(20.dp))
                         Spacer(modifier = Modifier.height(12.dp))
                         ShimmerLine(modifier = Modifier.width(120.dp).height(16.dp))
                         Spacer(modifier = Modifier.height(24.dp))
                         Row {
                             ShimmerLine(modifier = Modifier.width(80.dp).height(14.dp))
                             Spacer(modifier = Modifier.width(16.dp))
                             ShimmerLine(modifier = Modifier.width(80.dp).height(14.dp))
                         }
                    }
                }
            }
        }
    }
}
