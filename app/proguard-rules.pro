# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Mukesh\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

    -dontskipnonpubliclibraryclasses
    -dontobfuscate
    -forceprocessing
    -optimizationpasses 5

    -keep class * extends android.app.Activity
    -keep class android.support.** { *; }

    -dontwarn retrofit2.**
    -keep class retrofit2.** { *; }
    -keepattributes Signature
    -keepattributes Exceptions
    -keepattributes Annotation
    -dontwarn okio.**

    -assumenosideeffects class android.util.Log {
        public static *** d(...);
        public static *** v(...);
    }

    -dontwarn android.support.**
    -dontwarn com.google.android.gms.**

    -keepattributes Signature

    # For using GSON @Expose annotation
    -keepattributes *Annotation*

    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
