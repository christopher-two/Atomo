package org.override.atomo.feature.dashboard.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerCircle
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun DashboardShimmer() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    ShimmerLine(modifier = Modifier.width(150.dp).height(24.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerLine(modifier = Modifier.width(100.dp).height(16.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                ShimmerCircle(modifier = Modifier.size(40.dp))
            }
        }
        
        // Modules (simulate 3-4 modules)
        items(4) {
            AtomoCard(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
               Row(
                   modifier = Modifier.padding(16.dp),
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   ShimmerCircle(modifier = Modifier.size(60.dp))
                   Spacer(modifier = Modifier.width(16.dp))
                   Column {
                       ShimmerLine(modifier = Modifier.width(120.dp).height(20.dp))
                       Spacer(modifier = Modifier.height(8.dp))
                       ShimmerLine(modifier = Modifier.width(200.dp).height(14.dp))
                       Spacer(modifier = Modifier.height(4.dp))
                       ShimmerLine(modifier = Modifier.width(100.dp).height(14.dp))
                   }
               }
            }
        }
    }
}
