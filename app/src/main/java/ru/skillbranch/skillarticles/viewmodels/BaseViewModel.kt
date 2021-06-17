package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.*

abstract class BaseViewModel<T>(initState: T) : ViewModel() {

    protected val notifications = MutableLiveData<Event<Notify>>()

    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }


    //not null current state
    protected val currentState
        get() = state.value!!


    /**
     *
     * Лямбда выражение принимает в качестве аргмента текущий стейт (состояние)
     * и она возвращает модифицированное состояние, которое присваивается текущему состоянию
     */
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }


    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    fun observerNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver { onNotify(it) })
    }


    @UiThread
    fun notify(content: Notify) {
        notifications.value = Event(content)
    }


    /**
     *
     * Функция принимает источник данных и лямбда выражение, обрабатывающее поступающие данные
     * Лямбда принимает новые данные и текущее состояние, изменяет его и возвращает
     * модифицированное состояние и устанавливается как текущее
     */
    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    class ViewModelFactory(private val params: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
                return ArticleViewModel(params) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }

}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false

    //Возвращает контент который еще не был обработан иначе null
    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {

    //В качестве аргумента принимает лямбда выражение-обработчик в которую передается необработанное
    //раннее событие получаемое в реализации метода Observer'a onChanged
    override fun onChanged(event: Event<E>?) {

        /**
         *
         * Если есть необработанное событие (контент) передай в качестве аргумента в лямбду onEventUnhandledContent
         */
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify(val message: String) {
    data class TextMessage(val msg: String) : Notify(msg)

    data class ActionMessage(
        val msg: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)?
    ) : Notify(msg)


    data class ErrorMessage(
        val msg: String,
        val errLabel: String,
        val errHandler: (() -> Unit)?
    ) : Notify(msg)
}

