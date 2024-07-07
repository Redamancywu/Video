package com.techme.jetpack.ext

import android.content.ComponentName
import android.os.Bundle
import androidx.navigation.*
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import com.techme.jetpack.MainActivity
import com.techme.jetpack.plugin.runtime.NavDestination
import com.techme.jetpack.plugin.runtime.NavRegistry

fun NavController.switchTab(route: String, args: Bundle? = null) {
    val destId = route.hashCode()
    val list = backQueue.filter {
        it.destination.id == destId
    }

    if (list.isEmpty()) {
        navigateTo(route, args)
    } else {
        navigateBack(route, false, false)
    }
}

fun NavController.navigateTo(route: String, args: Bundle? = null, navOptions: NavOptions? = null) {
    navigate(route.hashCode(), args, navOptions)

}

fun NavController.navigateBack(
    route: String,
    inclusive: Boolean = false,
    saveState: Boolean = false
) {
    popBackStack(route.hashCode(), inclusive, saveState)
}

fun MainActivity.injectNavGraph(controller: NavController) {
    // 1. 构建navGraph路由表对象
    val provider = controller.navigatorProvider
    val graphNavigator = provider.get<NavGraphNavigator>("navigation")
    val navGraph = graphNavigator.createDestination()

    val iterator = NavRegistry.get().listIterator()
    while (iterator.hasNext()) {
        val navData = iterator.next()
        when (navData.type) {
            NavDestination.NavType.Fragment -> {
                val navigator = provider.get<FragmentNavigator>("fragment")
                val destination = navigator.createDestination();
                destination.id = navData.route.hashCode()
                destination.setClassName(navData.className)
                navGraph.addDestination(destination)
            }
            NavDestination.NavType.Activity -> {
                val navigator = provider.get<ActivityNavigator>("activity")
                val destination = navigator.createDestination();
                destination.id = navData.route.hashCode()
                destination.setComponentName(
                    ComponentName(
                        packageName,
                        navData.className
                    )
                )
                navGraph.addDestination(destination)
            }
            NavDestination.NavType.Dialog -> {
                val navigator = provider.get<DialogFragmentNavigator>("dialog")
                val destination = navigator.createDestination();
                destination.id = navData.route.hashCode()
                destination.setClassName(navData.className)
                navGraph.addDestination(destination)
            }
            else -> {
                throw java.lang.IllegalStateException("cant create NavGraph,because unknown ${navData.type}")
            }
        }

        if (navData.asStarter) {
            navGraph.setStartDestination(navData.route.hashCode())
        }
    }

    controller.setGraph(navGraph, null)
}