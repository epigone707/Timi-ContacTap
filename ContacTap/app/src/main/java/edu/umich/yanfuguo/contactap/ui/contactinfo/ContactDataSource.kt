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
import edu.umich.yanfuguo.contactap.ui.contactinfo.Contact

/* Handles operations on flowersLiveData and holds details about it. */
class ContactDataSource(resources: Resources) {
    private val initialContactList = contactList(resources)
    private val contactsLiveData = MutableLiveData(initialContactList)

    /* Adds contact to liveData and posts value. */
    fun addContact(contact: Contact) {
        val currentList = contactsLiveData.value
        if (currentList == null) {
            contactsLiveData.postValue(listOf(contact))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, contact)
            contactsLiveData.postValue(updatedList)
        }
    }

    /* Removes flower from liveData and posts value. */
    fun removeContact(contact: Contact) {
        val currentList = contactsLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(contact)
            contactsLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getContactForId(id: Long): Contact? {
        contactsLiveData.value?.let { contacts ->
            return contacts.firstOrNull{ it.id == id}
        }
        return null
    }

    fun getContactList(): LiveData<List<Contact>> {
        return contactsLiveData
    }

    /* Returns a random flower asset for flowers that are added.
    fun getRandomFlowerImageAsset(): Int? {
        val randomNumber = (initialContactList.indices).random()
        return initialContactList[randomNumber].image
    }
    */

    companion object {
        private var INSTANCE: ContactDataSource? = null

        fun getDataSource(resources: Resources): ContactDataSource {
            return synchronized(ContactDataSource::class) {
                val newInstance = INSTANCE ?: ContactDataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}