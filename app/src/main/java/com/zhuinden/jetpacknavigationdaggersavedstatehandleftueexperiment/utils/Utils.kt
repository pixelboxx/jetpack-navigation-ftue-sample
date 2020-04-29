/*
 * Copyright 2020 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.savedstate.SavedStateRegistryOwner

fun View.onClick(clickListener: (View) -> Unit) {
    setOnClickListener(clickListener)
}

inline fun EditText.onTextChanged(crossinline textChangeListener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            textChangeListener(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}

inline fun <reified T : Activity> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

fun Unit.safe() = Unit

fun Any.safe() = Unit

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

inline fun <T : View> T.showIf(condition: (T) -> Boolean): T {
    if (condition(this)) {
        show()
    } else {
        hide()
    }

    return this
}

inline fun <T : View> T.hideIf(condition: (T) -> Boolean): T {
    if (condition(this)) {
        hide()
    } else {
        show()
    }

    return this
}

inline fun <reified T : ViewModel> SavedStateRegistryOwner.createAbstractSavedStateViewModelFactory(
    arguments: Bundle,
    crossinline creator: (SavedStateHandle) -> T
): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            @Suppress("UNCHECKED_CAST")
            return creator(handle) as T
        }
    }
}

inline fun <reified T : ViewModel> Fragment.navGraphViewModel(
    @IdRes navGraphId: Int,
    crossinline creator: (SavedStateHandle) -> T
): Lazy<T> {
    // Wrapped in lazy to not search the NavController each time we want the backStackEntry
    val backStackEntry by lazy { findNavController().getBackStackEntry(navGraphId) }

    return createViewModelLazy(T::class, storeProducer = {
        backStackEntry.viewModelStore
    }, factoryProducer = {
        backStackEntry.createAbstractSavedStateViewModelFactory(
            arguments = arguments ?: Bundle(), creator = creator
        )
    })
}
