# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}

# KotlinX Serialization
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
    @kotlinx.serialization.Serializer <init>(...);
}
-keep,allowobfuscation,allowshrinking class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Ktor
-keep class io.ktor.** { *; }
-keep class io.ktor.client.** { *; }
-keep class io.ktor.client.engine.okhttp.** { *; }

# Supabase
-keep class io.github.jan.supabase.** { *; }

# Google Play Services (Identity/Auth)
-keep class com.google.android.gms.auth.api.identity.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.android.AndroidExceptionPreHandler {
    <init>();
}

# Missing classes on Android (Safe to ignore for Ktor/Supabase)
-dontwarn java.lang.management.**
-dontwarn javax.naming.**
-dontwarn javax.xml.**
-dontwarn org.slf4j.**