package ru.skillbranch.skillarticles.data.adapters

import ru.skillbranch.skillarticles.data.local.User


class UserJsonAdapter : JsonAdapter<User> {


    private fun List<String?>.dropColons(): List<String> {
        val newList = arrayListOf<String>()
        for (element in this) {
            val newElem = element?.dropWhile { it != ' ' }
            if (newElem != null) {
                newList.add(newElem.trim())
            }
        }

        return newList
    }

    
    override fun fromJson(json: String): User? {
        val list = json
            .drop(8)
            .dropLast(1)
            .split(",")
            .dropColons()



        return User(
            id = list[0],
            name = list[1],
            avatar = list[2],
            rating = list[3].toInt(),
            respect = list[4].toInt(),
            about = list[5]
        )
    }

    override fun toJson(obj: User?): String {
        return String.format(
            "User = {id: %s,name: %s,avatar: %s,rating: %d,respect: %d,about: %s}",
            obj?.id, obj?.name, obj?.avatar, obj?.rating, obj?.respect, obj?.about
        )
    }
}

