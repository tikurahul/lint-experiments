package com.rahulrav

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestFiles.manifest
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rahulrav.IssueRegistry.Companion.BadConfigurationProviderIssue
import com.rahulrav.IssueRegistry.Companion.NoisyIssue
import org.junit.Test

class DetectorTest {
  @Test
  fun testNoisyDetector() {
    lint()
        .files(
            manifest(
                """
                    <manifest package="test">
                    </manifest>
                    """.trimIndent()
            ).indented()
        )
        .allowMissingSdk()
        .issues(NoisyIssue)
        .run()
        .expect(
            """
                AndroidManifest.xml: Information: This is a noisy issue. Feel free to ignore for now. [NoisyIssueId]
                0 errors, 0 warnings
                """.trimIndent()
        )
  }

  @Test
  fun testWrongConfigurationProvider_correctImplementation() {
    lint().files(
        kotlin("androidx/work/Configuration.kt", """
                package androidx.work
                interface Configuration {
                  interface Provider {
                      fun getWorkManagerConfiguration(): Configuration
                  } 
                }             
              """.trimIndent()
        ).indented().within("src"),
        kotlin("android/app/Application.kt", """
                package android.app

                open class Application {
                  fun onCreate() {
                    
                  }
                }
              """.trimIndent()
        ).indented().within("src"),
        kotlin("com/example/App.kt", """
                package com.example
                
                import android.app.Application
                import androidx.work.Configuration
                
                class Config : Configuration
                
                class App : Configuration.Provider, Application() {
                  override fun onCreate() {
                  }
                  override fun getWorkManagerConfiguration(): Configuration = Config()
                }
              """.trimIndent()
        ).indented().within("src")
    )
        .issues(BadConfigurationProviderIssue)
        .run()
        .expect("No warnings.")
  }

  @Test
  fun testWrongConfigurationProvider_incorrectImplementation() {
    lint().files(
        kotlin("androidx/work/Configuration.kt", """
                package androidx.work
                interface Configuration {
                  interface Provider {
                      fun getWorkManagerConfiguration(): Configuration
                  } 
                }             
              """.trimIndent()
        ).indented().within("src"),
        kotlin("android/app/Application.kt", """
                package android.app

                open class Application {
                  fun onCreate() {
                    
                  }
                }
              """.trimIndent()
        ).indented().within("src"),
        kotlin("com/example/App.kt", """
                package com.example

                import androidx.work.Configuration
                
                class Config : Configuration
                
                class App : Configuration.Provider {
                  override fun onCreate() {
                  }
                  override fun getWorkManagerConfiguration(): Configuration = Config()
                }
              """.trimIndent()
        ).indented().within("src")
    )
        .issues(BadConfigurationProviderIssue)
        .run()
        .expect("""
          src/com/example/App.kt: Error: Only an android.app.Application can implement androidx.work.Configuration.Provider [BadConfigurationProviderId]
          1 errors, 0 warnings
        """.trimIndent())
  }
}
