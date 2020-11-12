package com.aman.android.rxandroid.model

public class User() {
    var id: Int = 0
    var login: String = ""
    var url: String = ""
    var isFollowing: Boolean = false

    @JvmOverloads constructor(githubUser: ApiUser) : this() {
        this.id = githubUser.id
        this.login = githubUser.login
        this.url = githubUser.url
    }


    override fun toString(): String {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", url='" + url + '\'' +
                ", isFollowing=" + isFollowing +
                '}'
    }
}