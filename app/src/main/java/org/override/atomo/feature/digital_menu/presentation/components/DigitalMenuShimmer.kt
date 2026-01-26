package org.override.atomo.feature.digital_menu.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerCircle
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun DigitalMenuShimmer() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Sections / Categories
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            ) {
                repeat(4) {
                    ShimmerLine(modifier = Modifier.width(80.dp).height(32.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
        
        // Menu Items
        items(5) {
            AtomoCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).height(100.dp)
            ) {
                 Row(
                     modifier = Modifier.padding(16.dp),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     // Image placeholder
                     ShimmerCircle(modifier = Modifier.size(70.dp))
                     
                     Spacer(modifier = Modifier.width(16.dp))
                     
                     Column {
                         ShimmerLine(modifier = Modifier.width(150.dp).height(18.dp))
                         Spacer(modifier = Modifier.height(8.dp))
                         ShimmerLine(modifier = Modifier.fillMaxWidth().height(14.dp))
                         Spacer(modifier = Modifier.height(8.dp))
                         ShimmerLine(modifier = Modifier.width(60.dp).height(16.dp))
                     }
                 }
            }
        }
    }
}
