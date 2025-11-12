-keepattributes Signature,InnerClasses,EnclosingMethod,Exceptions,*Annotation*,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations

-keep class kotlin.Metadata { *; }


-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.KotlinExtensions
-dontwarn kotlin.Unit


-keep class **$$serializer { *; }

-keepclassmembers class **$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class kotlinx.serialization.** { *; }

-keepclassmembers enum * { **[] $VALUES; public *; }


-keep @androidx.room.Dao interface * { *; }

-keep class **Database_Impl { *; }

-keep class * extends androidx.room.RoomDatabase { *; }

-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}

-dontwarn androidx.compose.**
-dontwarn androidx.activity.compose.**