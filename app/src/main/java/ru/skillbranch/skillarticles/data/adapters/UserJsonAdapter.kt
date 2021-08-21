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

    private fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T>{
        val list = mutableListOf<T>()
        for(element in this){
            if(predicate(element)){
                list.add(element)
            }
        }

        return list
    }


    override fun getDeserializeObj(jsonObject: String): User {
        val list = jsonObject.apply {
            drop(8)
            dropLast(1)
        }.toList().dropLastUntil { it == ':' }


        return User(
            id = list[0].toString(),
            name = list[1].toString(),
            avatar = list[3].toString(),
            rating = list[4].digitToInt(),
            respect = list[5].digitToInt(),
            about = list[6].toString()
        )
    }

    override fun getSerializeObj(): String {
        return String.format(
            "User = {id: %s, name: %s, avatar: %s, rating: %d, respect: %d, about: %s}",
            user.id, user.name, user.avatar, user.rating, user.respect, user.about
        )
    }


}