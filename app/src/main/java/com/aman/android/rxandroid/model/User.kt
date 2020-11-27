package com.aman.android.rxandroid.model

class User() {
    var id: Int = 0
    var login: String = ""
    var url: String = ""
    var isFollowing: Boolean = false


    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", url='" + url + '\'' +
                ", isFollowing=" + isFollowing +
                '}'
    }
}