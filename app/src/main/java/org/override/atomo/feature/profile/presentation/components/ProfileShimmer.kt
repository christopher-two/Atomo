package org.override.atomo.feature.profile.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.override.atomo.core.ui.components.AtomoCard
import org.override.atomo.core.ui.components.ShimmerCircle
import org.override.atomo.core.ui.components.ShimmerLine

@Composable
fun ProfileShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Avatar
        ShimmerCircle(modifier = Modifier.size(120.dp))
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Name and Username
        ShimmerLine(modifier = Modifier.height(28.dp).width(200.dp))
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerLine(modifier = Modifier.height(16.dp).width(120.dp))
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info Cards (Bio, etc)
        AtomoCard(
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    ShimmerLine(modifier = Modifier.width(80.dp).height(14.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    ShimmerLine(modifier = Modifier.fillMaxWidth().height(14.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerLine(modifier = Modifier.width(200.dp).height(14.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
         // Social Links
        AtomoCard(
            modifier = Modifier.fillMaxWidth().height(80.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column {
                    ShimmerLine(modifier = Modifier.width(100.dp).height(14.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    ShimmerLine(modifier = Modifier.width(150.dp).height(14.dp))
                }
            }
        }
    }
}
