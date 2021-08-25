package ru.skillbranch.skillarticles.data.adapters

import ru.skillbranch.skillarticles.data.local.User
import java.util.*


class UserJsonAdapter : JsonAdapter<User> {

    private var user: User = User(generateId(), generateName())

    private fun generateId() = UUID.randomUUID().toString()

    private fun generateName(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }




    override fun getDeserializeObj(jsonObject: String): User {
        val list = jsonObject.apply {
            drop(8)
            dropLast(1)
        }.split(",").toList().apply {
            forEach { str->
                str.dropWhile { it == ':' }.trim()
            }
        }

        return User(
            id = list[0],
            name = list[1],
            avatar = list[3],
            rating = list[4].toInt(),
            respect = list[5].toInt(),
            about = list[6]
        )
    }

    override fun getSerializeObj(): String {
        return String.format(
            "User = {id: %s, name: %s, avatar: %s, rating: %d, respect: %d, about: %s}",
            user.id, user.name, user.avatar, user.rating, user.respect, user.about
        )
    }


}