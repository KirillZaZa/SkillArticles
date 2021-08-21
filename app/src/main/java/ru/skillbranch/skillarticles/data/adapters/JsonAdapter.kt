package ru.skillbranch.skillarticles.data.adapters

import java.io.Serializable

interface JsonAdapter<T> : Serializable {

    companion object{
        const val version = 1L
    }


    fun getDeserializeObj(jsonObject: String): T

    fun getSerializeObj(): String



}