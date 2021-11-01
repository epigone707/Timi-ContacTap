/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.recyclersample.data

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/* Handles operations on flowersLiveData and holds details about it. */
class DataSource(resources: Resources) {
    private val initialProfileList = profileList(resources)
    private val profilesLiveData = MutableLiveData(initialProfileList)

    /* Adds profile to liveData and posts value. */
    fun addProfile(profile: Profile) {
        val currentList = profilesLiveData.value
        if (currentList == null) {
            profilesLiveData.postValue(listOf(profile))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, profile)
            profilesLiveData.postValue(updatedList)
        }
    }

    /* Removes flower from liveData and posts value. */
    fun removeProfile(profile: Profile) {
        val currentList = profilesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(profile)
            profilesLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getProfileForId(id: Long): Profile? {
        profilesLiveData.value?.let { profiles ->
            return profiles.firstOrNull{ it.id == id}
        }
        return null
    }

    fun getProfileList(): LiveData<List<Profile>> {
        return profilesLiveData
    }

    /* Returns a random flower asset for flowers that are added.
    fun getRandomFlowerImageAsset(): Int? {
        val randomNumber = (initialProfileList.indices).random()
        return initialProfileList[randomNumber].image
    }
    */

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}