# General Settings
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.**

# Keep standard attributes for debugging and libraries
-keepattributes SourceFile,LineNumberTable,Exceptions,InnerClasses,Signature,Deprecated,EnclosingMethod,Annotation,*Annotation*

# Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.jvm.internal.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.android.AndroidExceptionPreHandler {
    <init>();
}

# ----------------------------------------------------------------------------
# Libraries Specific Rules
# ----------------------------------------------------------------------------

# 1. KotlinX Serialization
# Prevent obfuscation of serializable fields. This is CRITICAL for JSON mapping and Supabase queries.
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
    @kotlinx.serialization.Serializer <init>(...);
}
# Keep the classes and their members if they are Serializable. 
# Do NOT allow obfuscation of names as Supabase/Postgrest might rely on them for query building.
-keep @kotlinx.serialization.Serializable class * {
    *;
}

# 2. Koin (Dependency Injection)
# Keep Koin internal classes
-keep class org.koin.** { *; }
-keep class * extends org.koin.core.module.Module
# Keep ViewModels constructors for reflection instantiation
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
# If you use `viewModelOf(::MyViewModel)`, Koin needs to see the constructor.

# 3. Ktor
# Keep Ktor engines and client details
-keep class io.ktor.** { *; }
-keep class io.ktor.client.** { *; }
-keep class io.ktor.client.engine.okhttp.** { *; }
# Specific for Coil+Ktor or other integrations
-dontwarn io.ktor.utils.io.requires.**

# 4. Supabase
# Keep Supabase SDK classes. 
# Explicitly keeping the auth and realtime classes is usually safer.
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# 5. Room
-disablebundleresourcefiles
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keepclassmembers class * {
    @androidx.room.Entity <fields>;
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.Ignore <fields>;
    @androidx.room.ForeignKey <fields>;
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
    @androidx.room.Transaction <methods>;
}

# 6. Compose
# Generally R8 handles this, but keep these just in case of reflection usage in custom widgets.
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# 7. Coil
-keep class coil.** { *; }
-dontwarn coil.**
-keep class com.google.accompanist.drawablepainter.** { *; }

# 8. Google Play Services / Authentication
-keep class com.google.android.gms.auth.api.identity.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class androidx.credentials.** { *; }
-dontwarn com.google.android.gms.**

# 9. App Specific Data Classes
# If you have data classes not marked with @Serializable but used in reflection/JSON
# Add them here or modify the Serializable rule above.
# For now, we assume all DTOs are @Serializable.

# 10. FileKit
-keep class io.github.vinceglb.filekit.** { *; }

# 11. Compressor
-keep class id.zelory.compressor.** { *; }