/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import org.override.atomo.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AtomoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = false,
    seedColor: Color = Color(0xFFDAEDFF),
    content: @Composable () -> Unit
) {
    // Crear una FontFamily que incluya las variantes relevantes (normales y cursivas)
    val poppinsFamily = FontFamily(
        Font(R.font.poppins_thin, weight = FontWeight.Thin),
        Font(R.font.poppins_extra_light, weight = FontWeight.ExtraLight),
        Font(R.font.poppins_light, weight = FontWeight.Light),
        Font(R.font.poppins_regular, weight = FontWeight.Normal),
        Font(R.font.poppins_medium, weight = FontWeight.Medium),
        Font(R.font.poppins_semi_bold, weight = FontWeight.SemiBold),
        Font(R.font.poppins_bold, weight = FontWeight.Bold),
        Font(R.font.poppins_extra_bold, weight = FontWeight.ExtraBold),
        Font(R.font.poppins_black, weight = FontWeight.Black),

        // Italic variants
        Font(R.font.poppins_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(R.font.poppins_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic),
        Font(R.font.poppins_semi_bold_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
        Font(R.font.poppins_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
        Font(R.font.poppins_black_italic, weight = FontWeight.Black, style = FontStyle.Italic),
        Font(R.font.poppins_thin_italic, weight = FontWeight.Thin, style = FontStyle.Italic),
        Font(R.font.poppins_extra_bold_italic, weight = FontWeight.ExtraBold, style = FontStyle.Italic),
        Font(R.font.poppins_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
        Font(R.font.poppins_extra_light_italic, weight = FontWeight.ExtraLight, style = FontStyle.Italic)
    )

    val typography = Typography(
        displayLarge = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Light,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Light,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = poppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )

    val context = LocalContext.current

    if (useDynamicColor) {
       val colorScheme = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = MaterialTheme.shapes,
            motionScheme = MotionScheme.expressive(),
            content = {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxSize(),
                    content = content
                )
            }
        )
    } else {
        DynamicMaterialTheme(
            seedColor = seedColor,
            isDark = darkTheme,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
            style = PaletteStyle.Expressive,
            shapes = MaterialTheme.shapes,
            animate = true,
            animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
            typography = typography,
            content = {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxSize(),
                    content = content
                )
            },
        )
    }
}