package ru.skillbranch.skillarticles.data.delegates

import androidx.datastore.preferences.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import ru.skillbranch.skillarticles.data.PrefManager
import ru.skillbranch.skillarticles.data.adapters.JsonAdapter
import ru.skillbranch.skillarticles.data.local.User
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefObjDelegate<T>(
    private val adapter: JsonAdapter<T>,
    private val customKey: String? = null
) {


    operator fun provideDelegate(
        thisRef: PrefManager,
        prop: KProperty<*>
    ) : ReadWriteProperty<PrefManager, T>{
        return object: ReadWriteProperty<PrefManager, T>{
            private var obj: Any? = null

            val key = createKey(customKey ?: prop.name)

            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T {
                if(obj == null){
                    val flowValue = thisRef.dataStore.data
                        .map { prefs->
                            prefs[key] ?: adapter.getSerializeObj()
                        }
                    obj = runBlocking(Dispatchers.IO) {
                        flowValue.first()
                    }
                }


                return adapter.getDeserializeObj(obj!! as String)
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T) {
                obj = value

                thisRef.scope.launch {
                    thisRef.dataStore.edit { prefs->
                        prefs[key] = value
                    }
                }
            }

        }
    }



    @Suppress("UNCHECKED CAST")
    fun createKey(name: String): Preferences.Key<T> =
        stringPreferencesKey(name) as Preferences.Key<T>
}