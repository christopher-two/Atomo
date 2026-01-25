package org.override.atomo.feature.profile.domain

object ProfileValidator {

    private val USERNAME_REGEX = Regex("^[a-z0-9_-]+$")

    fun isValidUsername(username: String): Boolean {
        return username.isNotEmpty() && USERNAME_REGEX.matches(username)
    }

    fun formatUsername(username: String): String {
        return username.lowercase()
    }

    fun isValidUrl(url: String): Boolean {
        return try {
            val validUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            java.net.URL(validUrl).toURI()
            true
        } catch (e: Exception) {
            false
        }
    }
}
