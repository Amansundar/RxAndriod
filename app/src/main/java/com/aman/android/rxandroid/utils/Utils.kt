package com.aman.android.rxandroid.utils

import com.aman.android.rxandroid.model.ApiUser
import com.aman.android.rxandroid.model.User
import java.util.ArrayList

class Utils {
    fun convertApiUserListToUserList(apiUserList: List<ApiUser>): List<User> {
        val userList = ArrayList<User>()

        for (apiUser in apiUserList) {
            val user = User()
            user.login = apiUser.login
            user.url = apiUser.url
            userList.add(user)
        }

        return userList
    }

    fun getApiUserList(): List<ApiUser> {
        val apiUserList = ArrayList<ApiUser>()

        val apiUserOne = ApiUser("login1", 1, "url")
        apiUserList.add(apiUserOne)

        val apiUserTwo = ApiUser("login2", 2, "url")
        apiUserList.add(apiUserTwo)

        val apiUserThree = ApiUser("login3", 3, "url")
        apiUserList.add(apiUserThree)

        return apiUserList
    }

    fun filterUserWhoLovesBoth(kotlinFans: List<User>, javaFans: List<User>): List<User> {
        val userWhoLovesBoth = ArrayList<User>()
        for (kotlinFan in kotlinFans) {
            javaFans
                    .filter { kotlinFan.id == it.id }
                    .forEach { userWhoLovesBoth.add(kotlinFan) }
        }
        return userWhoLovesBoth
    }

    fun getUserListWhoLovesKotlin(): List<User> {
        val userList = ArrayList<User>()

        val userOne = User()
        userOne.id = 1
        userOne.login = "Bala"
        userOne.url = "K"
        userList.add(userOne)

        val userTwo = User()
        userTwo.id = 2
        userTwo.login = "Ankit"
        userTwo.url = "Kumar"
        userList.add(userTwo)

        return userList
    }

    fun getUserListWhoLovesJava(): List<User> {
        val userList = ArrayList<User>()

        val userOne = User()
        userOne.id = 1
        userOne.login = "Bala"
        userOne.url = "K"
        userList.add(userOne)

        val userTwo = User()
        userTwo.id = 3
        userTwo.login = "Sandeep"
        userTwo.url = "Kumar"
        userList.add(userTwo)

        return userList
    }
}