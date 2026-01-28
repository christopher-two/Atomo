/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.domain.model

/**
 * Represents a professional portfolio showcasing work items.
 *
 * @property id Unique identifier for the portfolio.
 * @property userId The ID of the user.
 * @property title The title of the portfolio.
 * @property description Optional description.
 * @property isVisible Whether the portfolio is public.
 * @property templateId The ID of the UI template.
 * @property primaryColor The primary color hex code.
 * @property fontFamily The font family name.
 * @property createdAt Timestamp of creation.
 * @property items List of portfolio items (projects).
 */
data class Portfolio(
    val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val isVisible: Boolean,
    val templateId: String,
    val primaryColor: String,
    val fontFamily: String,
    val createdAt: Long,
    val items: List<PortfolioItem> = emptyList()
)

/**
 * Represents a single project or item in the portfolio.
 *
 * @property id Unique identifier.
 * @property portfolioId The portfolio this item belongs to.
 * @property title Project title.
 * @property description Optional detailed description of the project.
 * @property imageUrl Optional cover image URL.
 * @property projectUrl Optional URL to the live project or repo.
 * @property sortOrder Display order.
 * @property createdAt Creation timestamp.
 */
data class PortfolioItem(
    val id: String,
    val portfolioId: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val projectUrl: String?,
    val sortOrder: Int,
    val createdAt: Long
)
