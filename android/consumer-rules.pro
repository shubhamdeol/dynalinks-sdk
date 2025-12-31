# Dynalinks SDK - Keep all classes and Kotlin serialization metadata
-keep class com.dynalinks.sdk.** { *; }
-keepclassmembers class com.dynalinks.sdk.** { *; }

# Keep Kotlin serialization for Dynalinks internal classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.dynalinks.sdk.**$$serializer { *; }
-keepclassmembers class com.dynalinks.sdk.** {
    *** Companion;
}
-keepclasseswithmembers class com.dynalinks.sdk.** {
    kotlinx.serialization.KSerializer serializer(...);
}
