package edu.umich.yanfuguo.contactap.ui.ProfileData

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recyclersample.data.DataSource
import com.example.recyclersample.data.Profile
import kotlin.random.Random

class ProfileListViewModel(val dataSource: DataSource) : ViewModel() {

    val profileLiveData = dataSource.getProfileList()

    /* If the name and description are present, create new Flower and add it to the datasource */
    fun insertProfile(profileFirstName: String, profileLastName: String) {
        if (profileFirstName == null || profileLastName == null) {
            return
        }

        val newProfile = Profile(
            Random.nextLong(),
            profileFirstName,
            profileLastName,
        )

        dataSource.addProfile(newProfile)
    }
}

class ProfileListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileListViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}