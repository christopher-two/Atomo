package org.override.atomo.data.mapper

import org.override.atomo.data.local.entity.PortfolioEntity
import org.override.atomo.data.local.entity.PortfolioItemEntity
import org.override.atomo.data.remote.dto.PortfolioDto
import org.override.atomo.data.remote.dto.PortfolioItemDto
import org.override.atomo.domain.model.Portfolio
import org.override.atomo.domain.model.PortfolioItem

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

fun PortfolioItem.toDto(): PortfolioItemDto = PortfolioItemDto(
    id = id,
    portfolioId = portfolioId,
    title = title,
    description = description,
    imageUrl = imageUrl,
    projectUrl = projectUrl,
    sortOrder = sortOrder
)
