# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ptinkosinarmedia/Library/Android/sdk/tools/proguard/proguard-android.txt
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

#handled by ant build itself, no need to specify
#-injars      bin/classes
#-injars      libs
#-outjars     bin/classes-processed.jar
#-libraryjars /usr/local/java/android-sdk/platforms/android-9/android.jar

-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#volley proguard
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#end volley proguard

#glide proguard
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
#end glide proguard

#butterknife proguard
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#end butterknife proguard

#pugnotification&picasso proguard
-dontwarn com.squareup.okhttp.**
#end pugnotification&picasso proguard

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
##---------------End: proguard configuration for Gson  ----------

#crashlytic proguard
-keepattributes SourceFile,LineNumberTable,*Annotation*
-keep public class * extends java.lang.Exception
#end crashlytic proguard

# GreenDAO proguard
# Source: http://greendao-orm.com/documentation/technical-faq
#
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

#greenDAO v.3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**
#end GreenDAO proguard

#slidingMenu
-keep public class com.jeremyfeinstein.slidingmenu.lib {*;}
-keep public class com.jeremyfeinstein.slidingmenu.lib.app {*;}
#end slidingMenu

#okhttp3
-keepattributes Signature
-keepattributes Annotation
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-keep interface okhttp3.* { *; }
-dontwarn okio.*
#end okhttp3

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn org.apache.commons.**

-dontnote com.tonicartos.widget.stickygridheaders.**

-dontwarn com.jeremyfeinstein.slidingmenu.**

#need to keep these because it is referenced by GSON, can use serializedname annotation too
#-keep class hendrawd.ganteng.movieinfo.network.response.** {*;}

#class kept because it's extends of android View
-dontnote hendrawd.ganteng.movieinfo.view.**
-dontnote uk.co.senab.photoview.**
-dontnote se.emilsjolander.stickylistheaders.**
-dontnote com.soundcloud.android.crop.**
-dontnote com.jeremyfeinstein.slidingmenu.lib.**
-dontnote com.github.clans.fab.FloatingActionMenu
-dontnote com.android.volley.toolbox.NetworkImageView
#end class kept because it's extends of android view

#android support proguard
-dontwarn android.support.**
-dontnote android.support.**
#-keep class android.support.v7.** { *; }
#-keep interface android.support.v7.** { *; }
#end android support proguard

#gak jelas ini buat apa, padahal gak pake facebook, walau didisable gpp harusnya, ntar cek lagi
-dontnote com.google.android.gms.internal.**
-dontwarn com.google.android.gms.internal.**
#contoh notenya:
#Note: com.google.android.gms.internal.zzlf: can't find dynamically referenced class com.facebook.Session
#Note: com.google.android.gms.internal.zzlf$1: can't find dynamically referenced class com.facebook.login.LoginResult