package edu.umich.yanfuguo.contactap.model

data class Contact (

    var name: String = "",
    var imageUrl: String? = null,
    var personalEmail: String = "",
    var businessEmail: String = "",
    var personalPhone: String = "",
    var businessPhone: String = "",
    var otherPhone: String = "",
    var bio: String = "",
    var instagram: String = "",
    var snapchat: String = "",
    var twitter: String = "",
    var linkedIn: String = "",
    var hobbies: String = "",
    var other: String = "",

//    // profile id
//    // this field is useful only if this Contact object is used for storing a connection
//    var profileId: String = "",
)
