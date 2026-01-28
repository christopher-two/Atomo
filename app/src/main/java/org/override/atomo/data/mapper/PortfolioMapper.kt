/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.data.mapper

import org.override.atomo.data.local.entity.PortfolioEntity
import org.override.atomo.data.local.entity.PortfolioItemEntity
import org.override.atomo.data.remote.dto.PortfolioDto
import org.override.atomo.data.remote.dto.PortfolioItemDto
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem

/** Maps PortfolioEntity to Portfolio domain model. */
fun PortfolioEntity.toDomain(): Portfolio = Portfolio(
    id = id,
    userId = userId,
    title = title,
    description = description,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps Portfolio domain model to PortfolioEntity. */
fun Portfolio.toEntity(): PortfolioEntity = PortfolioEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt
)

/** Maps PortfolioDto to PortfolioEntity. */
fun PortfolioDto.toEntity(): PortfolioEntity = PortfolioEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps Portfolio domain model to PortfolioDto. */
fun Portfolio.toDto(): PortfolioDto = PortfolioDto(
    id = id,
    userId = userId,
    title = title,
    description = description,
    isVisible = isVisible,
    templateId = templateId,
    primaryColor = primaryColor,
    fontFamily = fontFamily
)

/** Maps PortfolioItemEntity to PortfolioItem domain model. */
fun PortfolioItemEntity.toDomain(): PortfolioItem = PortfolioItem(
    id = id,
    portfolioId = portfolioId,
    title = title,
    description = description,
    imageUrl = imageUrl,
    projectUrl = projectUrl,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps PortfolioItem domain model to PortfolioItemEntity. */
fun PortfolioItem.toEntity(): PortfolioItemEntity = PortfolioItemEntity(
    id = id,
    portfolioId = portfolioId,
    title = title,
    description = description,
    imageUrl = imageUrl,
    projectUrl = projectUrl,
    sortOrder = sortOrder,
    createdAt = createdAt
)

/** Maps PortfolioItemDto to PortfolioItemEntity. */
fun PortfolioItemDto.toEntity(): PortfolioItemEntity = PortfolioItemEntity(
    id = id,
    portfolioId = portfolioId,
    title = title,
    description = description,
    imageUrl = imageUrl,
    projectUrl = projectUrl,
    sortOrder = sortOrder,
    createdAt = createdAt?.let { parseTimestamp(it) } ?: System.currentTimeMillis()
)

/** Maps PortfolioItem domain model to PortfolioItemDto. */
fun PortfolioItem.toDto(): PortfolioItemDto = PortfolioItemDto(
    id = id,
    portfolioId = portfolioId,
    title = title,
    description = description,
    imageUrl = imageUrl,
    projectUrl = projectUrl,
    sortOrder = sortOrder
)
