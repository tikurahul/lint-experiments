package com.rahulrav

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rahulrav.IssueRegistry.Companion.BadConfigurationProviderIssue
import com.rahulrav.IssueRegistry.Companion.NoisyIssue
import com.rahulrav.IssueRegistry.Companion.RemoveWorkManagerIntializerIssue
import com.rahulrav.Stubs.ANDROID_APP_CLASS
import com.rahulrav.Stubs.ANDROID_LOG_JAVA
import com.rahulrav.Stubs.APP_IMPLEMENTS_CONFIGURATION_PROVIDER
import com.rahulrav.Stubs.EMPTY_MANIFEST
import com.rahulrav.Stubs.EXPERIMENTAL_KT
import com.rahulrav.Stubs.LOG_WTF_JAVA
import com.rahulrav.Stubs.LOG_WTF_KT
import com.rahulrav.Stubs.MANIFEST_WITH_INITIALIZER
import com.rahulrav.Stubs.MANIFEST_WITH_NO_INITIALIZER
import com.rahulrav.Stubs.OTHER_CLASS_IMPLEMENTS_CONFIGURATION_PROVIDER
import com.rahulrav.Stubs.TIME_TRAVEL_EXPERIMENT_KT
import com.rahulrav.Stubs.TIME_TRAVEL_PROVIDER_KT
import com.rahulrav.Stubs.USE_TIME_TRAVEL_EXPERIMENT_FROM_JAVA
import com.rahulrav.Stubs.WORK_MANAGER_CONFIGURATION_INTERFACE
import org.junit.Test

class DetectorTest {
    @Test
    fun testNoisyDetector() {
        lint().files(EMPTY_MANIFEST)
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
    fun testBadConfigurationProviderDetector_success() {
        lint().files(
                WORK_MANAGER_CONFIGURATION_INTERFACE,
                ANDROID_APP_CLASS,
                APP_IMPLEMENTS_CONFIGURATION_PROVIDER)
                .allowMissingSdk()
                .issues(BadConfigurationProviderIssue)
                .run()
                .expect("No warnings.")
    }

    @Test
    fun testBadConfigurationProviderDetector_failure() {
        lint().files(
                WORK_MANAGER_CONFIGURATION_INTERFACE,
                ANDROID_APP_CLASS,
                OTHER_CLASS_IMPLEMENTS_CONFIGURATION_PROVIDER)
                .allowMissingSdk()
                .issues(BadConfigurationProviderIssue)
                .run()
                .expect(
                        """
                        project0: Error: Only an android.app.Application can implement androidx.work.Configuration.Provider [BadConfigurationProviderId]
                        1 errors, 0 warnings
                        """.trimIndent()
                )
    }

    @Test
    fun testRemoveWorkManagerInitializerDetector_success() {
        lint().files(
                WORK_MANAGER_CONFIGURATION_INTERFACE,
                ANDROID_APP_CLASS,
                MANIFEST_WITH_NO_INITIALIZER,
                APP_IMPLEMENTS_CONFIGURATION_PROVIDER)
                .allowMissingSdk()
                .issues(RemoveWorkManagerIntializerIssue)
                .run()
                .expect("No warnings.")
    }

    @Test
    fun testRemoveWorkManagerInitializerDetector_failure_emptyManifest() {
        lint().files(
                EMPTY_MANIFEST,
                WORK_MANAGER_CONFIGURATION_INTERFACE,
                ANDROID_APP_CLASS,
                APP_IMPLEMENTS_CONFIGURATION_PROVIDER)
                .allowMissingSdk()
                .issues(RemoveWorkManagerIntializerIssue)
                .run()
                .expect(
                        """
                        AndroidManifest.xml:4: Error: If an android.app.Application implements androidx.work.Configuration.Provider, 
                        the default androidx.work.impl.WorkManagerInitializer needs to be removed from tne
                        AndroidManifest.xml file. [RemoveWorkManagerIntializerId]
                            <application>
                            ^
                        1 errors, 0 warnings
                        """.trimIndent()
                )
    }

    @Test
    fun testRemoveWorkManagerInitializerDetector_failure_manifestWithInitializer() {
        lint().files(
                MANIFEST_WITH_INITIALIZER,
                WORK_MANAGER_CONFIGURATION_INTERFACE,
                ANDROID_APP_CLASS,
                APP_IMPLEMENTS_CONFIGURATION_PROVIDER)
                .allowMissingSdk()
                .issues(RemoveWorkManagerIntializerIssue)
                .run()
                .expect(
                        """
                        AndroidManifest.xml:5: Error: If an android.app.Application implements androidx.work.Configuration.Provider, 
                        the default androidx.work.impl.WorkManagerInitializer needs to be removed from tne
                        AndroidManifest.xml file. [RemoveWorkManagerIntializerId]
                                 <provider
                                 ^
                        1 errors, 0 warnings
                      """.trimIndent()
                )
    }

    @Test
    fun testExperimentalDetector() {
        val input = arrayOf(
                EXPERIMENTAL_KT,
                TIME_TRAVEL_EXPERIMENT_KT,
                TIME_TRAVEL_PROVIDER_KT,
                USE_TIME_TRAVEL_EXPERIMENT_FROM_JAVA
        )

        /* ktlint-disable max-line-length */
        val expected = """
src/com/rahulrav/app/UseTimeTravelExperimentFromJava.java:18: Error: This declaration is experimental and its usage should be marked with
'@com.rahulrav.app.TimeTravelExperiment' or '@UseExperimental(markerClass = com.rahulrav.app.TimeTravelExperiment.class)' [UnsafeExperimentalUsageError]
        new TimeTravelProvider().setTime(-1);
        ~~~~~~~~~~~~~~~~~~~~~~~~
src/com/rahulrav/app/UseTimeTravelExperimentFromJava.java:18: Error: This declaration is experimental and its usage should be marked with
'@com.rahulrav.app.TimeTravelExperiment' or '@UseExperimental(markerClass = com.rahulrav.app.TimeTravelExperiment.class)' [UnsafeExperimentalUsageError]
        new TimeTravelProvider().setTime(-1);
                                 ~~~~~~~
2 errors, 0 warnings
        """
        /* ktlint-enable max-line-length */

        lint().files(*input)
                .allowMissingSdk()
                .issues(ExperimentalDetector.ISSUE)
                .run()
                .expect(expected.trimIndent())
    }

    @Test
    fun testLogWtfDetector() {
        /* ktlint-disable max-line-length */
        val expected = """
src/com/rahulrav/app/WhatATerribleFailure.kt:7: Error: Usage of Log.wtf() is prohibited [LogWtfUsageError]
        Log.wtf(clazz.name, message)
            ~~~~~~~~~~~~~~~~~~~~~~~~
1 errors, 0 warnings
            """.trimIndent()
        /* ktlint-enable max-line-length */

        lint().files(
                ANDROID_LOG_JAVA,
                LOG_WTF_KT)
                .allowMissingSdk() // The one SDK class that we need has been added manually!
                .issues(LogWtfDetector.ISSUE)
                .run()
                .expect(expected.trimIndent())
    }
}
