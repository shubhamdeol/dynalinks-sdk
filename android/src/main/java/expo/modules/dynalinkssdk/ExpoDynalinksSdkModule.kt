package expo.modules.dynalinkssdk

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.Promise
import com.dynalinks.sdk.Dynalinks
import com.dynalinks.sdk.DeepLinkResult
import com.dynalinks.sdk.LinkData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpoDynalinksSdkModule : Module() {
  private var isDynalinksConfigured = false

  override fun definition() = ModuleDefinition {
    Name("ExpoDynalinksSdk")

    Events("onDeferredDeepLink")

    AsyncFunction("configureDynalinks") { apiKey: String, promise: Promise ->
      configureAndCheckDeepLink(apiKey, promise)
    }
  }

  private fun configureAndCheckDeepLink(apiKey: String, promise: Promise) {
    val context = appContext.reactContext ?: run {
      promise.resolve(false)
      return
    }

    if (!isDynalinksConfigured) {
      try {
        Dynalinks.configure(
          context = context,
          clientAPIKey = apiKey
        )
        isDynalinksConfigured = true
        println("[Dynalinks] SDK configured successfully")
      } catch (e: Exception) {
        println("[Dynalinks] Failed to configure SDK: ${e.message}")
        promise.resolve(false)
        return
      }
    }

    // Check for deferred deep link in a coroutine
    CoroutineScope(Dispatchers.Main).launch {
      try {
        val result = Dynalinks.checkForDeferredDeepLink()
        val eventData = convertDeepLinkResultToMap(result)
        println("[Dynalinks] Deferred deep link result: $eventData")
        sendEvent("onDeferredDeepLink", eventData)
      } catch (e: Exception) {
        println("[Dynalinks] Error checking deferred deep link: ${e.message}")
        sendEvent("onDeferredDeepLink", mapOf("matched" to false))
      }
    }

    promise.resolve(true)
  }

  private fun convertDeepLinkResultToMap(result: DeepLinkResult): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>(
      "matched" to result.matched
    )

    result.confidence?.let {
      map["confidence"] = it.name.lowercase()
    }

    result.matchScore?.let {
      map["match_score"] = it
    }

    result.link?.let { linkData ->
      map["link"] = convertLinkDataToMap(linkData)
    }

    return map
  }

  private fun convertLinkDataToMap(linkData: LinkData): Map<String, Any?> {
    return mapOf(
      "id" to linkData.id,
      "name" to linkData.name,
      "path" to linkData.path,
      "shortened_path" to linkData.shortenedPath,
      "url" to linkData.url,
      "full_url" to linkData.fullUrl,
      "deep_link_value" to linkData.deepLinkValue,
      "android_fallback_url" to linkData.androidFallbackUrl,
      "ios_fallback_url" to linkData.iosFallbackUrl,
      "enable_forced_redirect" to linkData.enableForcedRedirect,
      "social_title" to linkData.socialTitle,
      "social_description" to linkData.socialDescription,
      "social_image_url" to linkData.socialImageUrl,
      "clicks" to linkData.clicks
    ).filterValues { it != null }
  }
}
