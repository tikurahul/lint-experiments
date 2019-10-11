@file:Suppress("UnstableApiUsage")

package com.rahulrav

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.w3c.dom.Element

class RemoveWorkManagerInitializerDetector : Detector(), XmlScanner, SourceCodeScanner {

    private var analyzed = false
    private var removedInitializer = false
    private var location: Location? = null
    private var appImplementsProvider = false

    override fun getApplicableElements() = listOf("manifest")
    override fun applicableSuperClasses() = listOf("androidx.work.Configuration.Provider")

    override fun visitElement(context: XmlContext, element: Element) {
        if (!analyzed) {
            analyzed = true
            val document = context.client.getMergedManifest(context.project)
            val application = document?.getElementsByTagName("application")?.item(0)
            val provider = document?.getElementsByTagName("provider")?.item(0)

            if (provider != null) {
                location = context.getLocation(provider)
                val name = provider.attributes.getNamedItemNS(ANDROID_NS, ATTRIBUTE_NAME).textContent
                val remove = provider.attributes.getNamedItemNS(TOOLS_NS, ATTRIBUTE_NODE).textContent
                if (name == "androidx.work.impl.WorkManagerInitializer" && remove == "remove") {
                    removedInitializer = true
                }
            } else if (application != null) {
                location = context.getLocation(application)
            } else {
                location = Location.create(context.file)
            }
        }
    }

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (context.evaluator.extendsClass(
                    declaration.javaPsi,
                    "android.app.Application",
                    false)) {
            appImplementsProvider = true
        }
    }

    override fun afterCheckRootProject(context: Context) {
        val location = location ?: Location.create(context.file)
        if (appImplementsProvider) {
            if (!removedInitializer) {
                context.report(
                    issue = IssueRegistry.RemoveWorkManagerIntializerIssue,
                    location = location,
                    message = IssueRegistry.RemoveWorkManagerIntializerDescription
                )
            }
        }
    }

    companion object {
        private const val ANDROID_NS = "http://schemas.android.com/apk/res/android"
        private const val TOOLS_NS = "http://schemas.android.com/tools"

        private const val ATTRIBUTE_NAME = "name"
        private const val ATTRIBUTE_NODE = "node"
    }
}
