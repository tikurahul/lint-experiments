package com.rahulrav

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.android.tools.lint.checks.infrastructure.TestFiles.manifest

object Stubs {
    val EMPTY_MANIFEST: TestFile = manifest(
            """
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                          xmlns:tools="http://schemas.android.com/tools"
                          package="com.example">
                    <application>
                    </application>
                </manifest>
            """.trimIndent()
    ).indented()

    val MANIFEST_WITH_NO_INITIALIZER: TestFile = manifest(
            """
               <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                  xmlns:tools="http://schemas.android.com/tools"
                  package="com.example">
                  <application>
                        <provider
                          android:name="androidx.work.impl.WorkManagerInitializer"
                          android:authorities="com.example.workmanager-init"
                          tools:node="remove"/>
                        
                  </application>
                </manifest>
            """).indented()

    val MANIFEST_WITH_INITIALIZER: TestFile = manifest(
            """
               <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                  xmlns:tools="http://schemas.android.com/tools"
                  package="com.example">
                  <application>
                        <provider
                          android:name="androidx.work.impl.WorkManagerInitializer"
                          android:authorities="com.example.workmanager-init"/>
                        
                  </application>
                </manifest>
            """).indented()

    val WORK_MANAGER_CONFIGURATION_INTERFACE: TestFile = kotlin(
            "androidx/work/Configuration.kt",
            """
                 package androidx.work
                 interface Configuration {
                   interface Provider {
                     fun getWorkManagerConfiguration(): Configuration
                   } 
                 }  
            """)
            .indented().within("src")

    val ANDROID_APP_CLASS: TestFile = kotlin(
            "android/app/Application.kt",
            """
                package android.app
                open class Application {
                  fun onCreate() {
                      
                  }
                }
            """)
            .indented().within("src")

    val APP_IMPLEMENTS_CONFIGURATION_PROVIDER: TestFile = kotlin(
            "com/example/App.kt",
            """
                package com.example
                
                import android.app.Application
                import androidx.work.Configuration
                
                class Config : Configuration
                
                class App : Configuration.Provider, Application() {
                  override fun onCreate() {
                  }
                  override fun getWorkManagerConfiguration(): Configuration = Config()
                }
            """)
            .indented().within("src")

    val OTHER_CLASS_IMPLEMENTS_CONFIGURATION_PROVIDER: TestFile = kotlin(
            "com/example/App.kt",
            """
                package com.example
                import androidx.work.Configuration
                
                class Config : Configuration
                
                class App : Configuration.Provider {
                  override fun onCreate() {
                  }
                  override fun getWorkManagerConfiguration(): Configuration = Config()
                }
            """)
            .indented().within("src")

    /**
     * [TestFile] containing Experimental.kt from the Kotlin standard library.
     *
     * This is a workaround for the Kotlin standard library used by the Lint test harness not
     * including the Experimental annotation by default.
     */
    val EXPERIMENTAL_KT: TestFile = kotlin(
            "kotlin/Experimental.kt",
            """
                package kotlin
    
                import kotlin.annotation.AnnotationRetention.BINARY
                import kotlin.annotation.AnnotationRetention.SOURCE
                import kotlin.annotation.AnnotationTarget.*
                import kotlin.internal.RequireKotlin
                import kotlin.internal.RequireKotlinVersionKind
                import kotlin.reflect.KClass
    
                @Target(ANNOTATION_CLASS)
                @Retention(BINARY)
                @SinceKotlin("1.2")
                @RequireKotlin("1.2.50", versionKind = RequireKotlinVersionKind.COMPILER_VERSION)
                @Suppress("ANNOTATION_CLASS_MEMBER")
                public annotation class Experimental(val level: Level = Level.ERROR) {
                    public enum class Level {
                        WARNING,
                        ERROR,
                    }
                }
    
                @Target(
                    CLASS, PROPERTY, LOCAL_VARIABLE, VALUE_PARAMETER, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, EXPRESSION, FILE, TYPEALIAS
                )
                @Retention(SOURCE)
                @SinceKotlin("1.2")
                @RequireKotlin("1.2.50", versionKind = RequireKotlinVersionKind.COMPILER_VERSION)
                public annotation class UseExperimental(
                    vararg val markerClass: KClass<out Annotation>
                )
    
                @Target(CLASS, PROPERTY, CONSTRUCTOR, FUNCTION, TYPEALIAS)
                @Retention(BINARY)
                internal annotation class WasExperimental(
                    vararg val markerClass: KClass<out Annotation>
                )
            """).indented().within("src")

    val TIME_TRAVEL_EXPERIMENT_KT: TestFile = kotlin(
            "com/rahulrav/app/TimeTravelExperiment.kt",
            """
                package com.rahulrav.app
                
                @Experimental
                @Retention(AnnotationRetention.BINARY)
                @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
                annotation class TimeTravelExperiment
            """).indented().within("src")

    val TIME_TRAVEL_PROVIDER_KT = kotlin(
            "com/rahulrav/app/TimeTravelProvider.kt",
            """
                package com.rahulrav.app
                
                @Suppress("unused")
                @TimeTravelExperiment
                class TimeTravelProvider {
                    var timeInternal: Long = 0
                
                    fun setTime(timestamp: Long) {
                        timeInternal = timestamp
                    }
                }
            """).indented().within("src")

    val USE_TIME_TRAVEL_EXPERIMENT_FROM_JAVA = java(
            "com/rahulrav/app/UseTimeTravelExperimentFromJava.java",
            """
                package com.rahulrav.app;
                
                import kotlin.UseExperimental;
                
                @SuppressWarnings("unused")
                class UseTimeTravelExperimentFromJava {
                    @TimeTravelExperiment
                    void setTimeToNow() {
                        new TimeTravelProvider().setTime(System.currentTimeMillis());
                    }
                
                    @UseExperimental(markerClass = TimeTravelExperiment.class)
                    void setTimeToEpoch() {
                        new TimeTravelProvider().setTime(0);
                    }
                
                    void violateTimeTravelAccords() {
                        new TimeTravelProvider().setTime(-1);
                    }
                }
            """).indented().within("src")

    /**
     * [TestFile] containing Log.java from the Android SDK.
     *
     * This is a hacky workaround for the Android SDK not being included on the Lint test harness
     * classpath. Ideally, we'd specify ANDROID_HOME as an environment variable.
     */
    val ANDROID_LOG_JAVA = java(
            """
                package android.util;
                
                public class Log {
                    public static void wtf(String tag, String msg) {
                        // Stub!
                    }
                }
            """.trimIndent())

    val LOG_WTF_KT = kotlin(
            "com/rahulrav/app/WhatATerribleFailure.kt",
            """
                package com.rahulrav.app
                
                import android.util.Log
                
                class WhatATerribleFailure {
                    fun <T> logAsWtf(clazz: Class<T>, message: String) {
                        Log.wtf(clazz.name, message)

                        wtf(message)
                    }
                
                    fun wtf(message: String) {
                        Log.d("TAG", message)
                    }
                }
            """).indented().within("src")

    val LOG_WTF_JAVA = java(
            "com/rahulrav/app/WhatATerribleFailureJava.java",
            """
                package com.rahulrav.app;
                
                import android.util.Log;
                
                class WhatATerribleFailureJava {
                    void logAsWtf(Class<?> clazz, String message) {
                        Log.wtf(clazz.getName(), message);
                
                        wtf(message);
                    }
                
                    void wtf(String message) {
                        Log.d("TAG", message);
                    }
                }
            """).indented().within("src")
}
