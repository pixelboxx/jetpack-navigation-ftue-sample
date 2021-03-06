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
package com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.R
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.application.AuthenticationManager
import com.zhuinden.jetpacknavigationdaggersavedstatehandleftueexperiment.core.navigation.NavigationDispatcher

class ProfileViewModel @AssistedInject constructor(
    private val authenticationManager: AuthenticationManager,
    @Assisted private val navigationDispatcher: NavigationDispatcher
): ViewModel() {
    @AssistedInject.Factory
    interface Factory {
        fun create(navigationDispatcher: NavigationDispatcher): ProfileViewModel
    }

    val activationCheck: LiveData<Unit> = object: LiveData<Unit>(Unit) {
        override fun onActive() {
            if (!authenticationManager.isAuthenticated()) {
                navigationDispatcher.emit { navController, context ->
                    navController.navigate(R.id.logged_in_to_logged_out)
                }
            }
        }
    }
}