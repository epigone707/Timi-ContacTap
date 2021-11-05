package edu.umich.yanfuguo.contactap.model

data class Contact (
    var photo: String? = null,
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var insta: String = "",
    var snap: String = "",
    var twitter: String = "",
    var linkedin: String = "",
    var other: String = ""
)
