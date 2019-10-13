package com.rahulrav

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.rahulrav.IssueRegistry.Companion.BadConfigurationProviderIssue
import com.rahulrav.IssueRegistry.Companion.NoisyIssue
import com.rahulrav.IssueRegistry.Companion.RemoveWorkManagerIntializerIssue
import com.rahulrav.Stubs.ANDROID_APP_CLASS
import com.rahulrav.Stubs.APP_IMPLEMENTS_CONFIGURATION_PROVIDER
import com.rahulrav.Stubs.EMPTY_MANIFEST
import com.rahulrav.Stubs.MANIFEST_WITH_INITIALIZER
import com.rahulrav.Stubs.MANIFEST_WITH_NO_INITIALIZER
import com.rahulrav.Stubs.OTHER_CLASS_IMPLEMENTS_CONFIGURATION_PROVIDER
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
}
