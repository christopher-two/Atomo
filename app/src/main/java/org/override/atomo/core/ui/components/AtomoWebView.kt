package org.override.atomo.core.ui.components

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AtomoWebView(
    url: String,
    modifier: Modifier = Modifier,
    isJavaScriptEnabled: Boolean = true,
    isDomStorageEnabled: Boolean = true,
    onPageFinished: ((view: WebView?, url: String?) -> Unit)? = null,
    update: (WebView) -> Unit = {}
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = isJavaScriptEnabled
                settings.domStorageEnabled = isDomStorageEnabled
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onPageFinished?.invoke(view, url)
                    }
                }
                loadUrl(url)
            }
        },
        update = update,
        modifier = modifier
    )
}
