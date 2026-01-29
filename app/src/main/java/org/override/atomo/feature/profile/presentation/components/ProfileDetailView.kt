/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.feature.profile.presentation.components

import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import coil3.compose.AsyncImage
import org.override.atomo.core.ui.components.MorphPolygonShape
import org.override.atomo.domain.model.Profile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileDetailView(
    profile: Profile,
    onEditClick: () -> Unit,
    onSyncClick: () -> Unit,
    onShareClick: () -> Unit, // New callback
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // No URL generation here anymore

    // 1. Configuración del Morphing
    val numVertices = 12

    val circlePolygon = remember {
        RoundedPolygon.circle(
            numVertices = numVertices,
            radius = 1f,     // CRÍTICO: Debe ser 1f
            centerX = 0f,    // CRÍTICO: Debe ser 0f
            centerY = 0f     // CRÍTICO: Debe ser 0f
        )
    }

    val cookiePolygon = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = numVertices,
            innerRadius = 0.5f,
            rounding = CornerRounding(radius = 0.2f),
            radius = 1f,     // CRÍTICO: Debe ser 1f
            centerX = 0f,    // CRÍTICO: Debe ser 0f
            centerY = 0f     // CRÍTICO: Debe ser 0f
        )
    }
    val morph = remember(circlePolygon, cookiePolygon) {
        Morph(circlePolygon, cookiePolygon)
    }

    // 2. Animación Infinita (Loop)
    val infiniteTransition = rememberInfiniteTransition(label = "morphLoop")
    // Usamos un tween más largo con una curva suave para que se sienta "expresivo"
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = padding.calculateLeftPadding(LocalLayoutDirection.current))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AVATAR CON MORPHING INFINITO
            Box(
                modifier = Modifier
                    .size(200.dp) // Tamaño total del contenedor del avatar
                    .padding(4.dp), // Un pequeño padding para que la forma no toque los bordes exactos
                contentAlignment = Alignment.Center
            ) {
                // Instanciamos la forma una vez por recomposición basada en el progreso
                val currentAnimatedShape = MorphPolygonShape(morph, progress)

                profile.avatarUrl?.let {
                    AsyncImage(
                        model = profile.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            // AQUI ESTABA LA CLAVE: Aplicar el clip correctamente calculado
                            .clip(currentAnimatedShape)
                            // Añadimos un fondo/borde sutil que también sigue la forma
                            .background(MaterialTheme.colorScheme.primaryContainer, currentAnimatedShape)
                            .padding(4.dp) // Padding interno entre el borde y la imagen
                            .clip(currentAnimatedShape), // Volvemos a recortar la imagen interna
                        contentScale = ContentScale.Crop,
                    )
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(currentAnimatedShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.displayName?.firstOrNull()?.uppercase() ?: "@",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Info Section (Sin cambios)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.displayName ?: "@${profile.username}",
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    ),
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (profile.displayName != null) {
                    Text(
                        text = "@${profile.username}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(Modifier.height(8.dp))

                val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                Text(
                    text = "Joined ${dateFormat.format(Date(profile.createdAt))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Link Sharing Actions (Sin cambios)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(onClick = onSyncClick) {
                    Icon(Icons.Default.Sync, contentDescription = "Sync Profile")
                }
                FilledIconButton(
                    onClick = onShareClick
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share Profile")
                }
            }

            // Social Links (Sin cambios)
            if (!profile.socialLinks.isNullOrEmpty()) {
                Text(
                    text = "Social Links",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                profile.socialLinks.forEach { (platform, url) ->
                    ListItem(
                        headlineContent = { Text(platform.replaceFirstChar { it.uppercase() }) },
                        supportingContent = { Text(url, maxLines = 1) },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(MaterialTheme.shapes.extraLarge),
                    )
                }
            }
        }
    }
}