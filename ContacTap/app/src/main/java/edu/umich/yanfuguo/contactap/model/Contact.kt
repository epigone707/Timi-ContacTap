package edu.umich.yanfuguo.contactap.model

data class Contact (
    var photo: String? = null,
    var name: String = "",
    var personal_email: String = "",
    var business_email: String = "",
    var personal_phone: String = "",
    var business_phone: String = "",
    var other_phone: String = "",
    var bio: String = "",
    var insta: String = "",
    var snap: String = "",
    var twitter: String = "",
    var linkedin: String = "",
    var hobbies: String = "",
    var otherinfo: String = ""
)
