# Keep serialization models
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** { @kotlinx.serialization.Serializable *; }
-keep class com.seva.scripture.data.seed.** { *; }
-dontwarn kotlinx.serialization.**
