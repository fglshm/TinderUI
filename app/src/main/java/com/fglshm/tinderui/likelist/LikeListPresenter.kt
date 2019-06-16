package com.fglshm.tinderui.likelist

import com.fglshm.tinderui.model.Person
import io.realm.RealmResults

class LikeListPresenter(
    private val view: LikeListContract.View
) : LikeListContract.Presenter, LikeListContract.InteractorOutput {

    private val interactor by lazy { LikeListInteractor(this) }

    override fun onViewCreated() {
        view.showProgress()
        interactor.fetchPersons()
    }

    override fun onFetch(what: RealmResults<Person>) {
        view.hideProgress()
        view.addPersons(what)
    }

    override fun onEmptyResult() {
        view.hideProgress()
        view.showEmptyResult()
    }
}