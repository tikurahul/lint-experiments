package com.rahulrav

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestFile
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
            """.trimIndent()).indented()

    val WORK_MANAGER_CONFIGURATION_INTERFACE: TestFile = kotlin(
            "androidx/work/Configuration.kt",
            """
                 package androidx.work
                 interface Configuration {
                   interface Provider {
                     fun getWorkManagerConfiguration(): Configuration
                   } 
                 }  
            """.trimIndent())
            .indented().within("src")

    val ANDROID_APP_CLASS: TestFile = kotlin(
            "android/app/Application.kt",
            """
                package android.app
                open class Application {
                  fun onCreate() {
                      
                  }
                }
            """.trimIndent())
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
            """.trimIndent())
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
            """.trimIndent())
            .indented().within("src")

}
