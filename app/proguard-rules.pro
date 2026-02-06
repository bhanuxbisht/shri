# Keep serialization models
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** { @kotlinx.serialization.Serializable *; }
-dontwarn kotlinx.serialization.**
