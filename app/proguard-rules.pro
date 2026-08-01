# ProGuard rules for DramaFy
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Kotlinx Serialization
-keep,includedescriptorclasses class com.sanzzaza.dramafy.**$$serializer { *; }
-keepclassmembers class com.sanzzaza.dramafy.** { *** Companion; }
-keepclasseswithmembers class com.sanzzaza.dramafy.** { kotlinx.serialization.KSerializer serializer(...); }

# Retrofit / OkHttp
-keepattributes Signature, Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Coroutines
-keepclassmembernames class kotlinx.** { volatile <fields>; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.** { *; }
