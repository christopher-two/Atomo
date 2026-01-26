package org.override.atomo.feature.pay.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun PayShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
         // Current Plan Title
        ShimmerLine(modifier = Modifier.width(150.dp).height(20.dp))
        Spacer(modifier = Modifier.height(16.dp))
        
        // Current Plan Card
        AtomoCard(
            modifier = Modifier.fillMaxWidth().height(180.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                     ShimmerLine(modifier = Modifier.width(100.dp).height(24.dp))
                     Spacer(modifier = Modifier.height(16.dp))
                     ShimmerLine(modifier = Modifier.width(200.dp).height(16.dp))
                     Spacer(modifier = Modifier.height(8.dp))
                     ShimmerLine(modifier = Modifier.width(150.dp).height(16.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Upgrade Title
        ShimmerLine(modifier = Modifier.width(200.dp).height(28.dp))
        Spacer(modifier = Modifier.height(24.dp))
        
        // Other Plans
        repeat(2) {
            AtomoCard(
                modifier = Modifier.fillMaxWidth().height(150.dp)
            ) {
                 Box(modifier = Modifier.padding(16.dp)) {
                    Column {
                         ShimmerLine(modifier = Modifier.width(120.dp).height(24.dp))
                         Spacer(modifier = Modifier.height(16.dp))
                         ShimmerLine(modifier = Modifier.fillMaxWidth().height(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
