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
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.ui.contactinfo.Contact

/* Returns initial list of flowers. */
fun contactList(resources: Resources): List<Contact> {
    return listOf(
        Contact(
            id = 1,
            firstName = "Umich contact",
            lastName = "temp"
        ),
        Contact(
            id = 2,
            firstName = "family contact",
            lastName = "temp"
        ) ,
        Contact(
            id = 3,
            firstName = "internship contact",
            lastName = "temp"
        )
    )
}