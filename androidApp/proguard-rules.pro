# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Ktor / OkHttp internals
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Keep Koin
-keep class org.koin.** { *; }

# Keep Coil
-keep class coil.** { *; }

# Keep app entry points
-keep class com.closetmixer.android.** { *; }
