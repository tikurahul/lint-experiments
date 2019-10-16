@file:Suppress("UnstableApiUsage")

package com.rahulrav

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

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
            val providers = document?.getElementsByTagName("provider")
            val provider = providers.find { node ->
                val name = node.attributes.getNamedItemNS(ANDROID_NS, ATTRIBUTE_NAME)?.textContent
                name == "androidx.work.impl.WorkManagerInitializer"
            }
            if (provider != null) {
                location = context.getLocation(provider)
                val remove = provider.attributes.getNamedItemNS(TOOLS_NS, ATTRIBUTE_NODE)
                if (remove?.textContent == "remove") {
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

        fun NodeList?.find(fn: (node: Node) -> Boolean): Node? {
            if (this == null) {
                return null
            } else {
                for (i in 0 until this.length) {
                    val node = this.item(i)
                    if (fn(node)) {
                        return node
                    }
                }
                return null
            }
        }
    }
}
