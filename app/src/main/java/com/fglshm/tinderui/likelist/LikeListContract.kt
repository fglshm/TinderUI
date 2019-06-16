package com.fglshm.tinderui.likelist

import com.fglshm.tinderui.model.Person
import io.realm.RealmResults


interface LikeListContract {

    interface View {
        fun showProgress()
        fun hideProgress()
        fun showEmptyResult()
        fun addPersons(what: RealmResults<Person>)
    }

    interface Presenter {
        fun onViewCreated()
    }

    interface Interactor {
        fun fetchPersons()
    }

    interface InteractorOutput {
        fun onFetch(what: RealmResults<Person>)
        fun onEmptyResult()
    }

}