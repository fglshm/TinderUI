package com.fglshm.tinderui.likelist

import com.fglshm.tinderui.application.App
import com.fglshm.tinderui.model.Person

class LikeListInteractor(
    private val output: LikeListContract.InteractorOutput
) : LikeListContract.Interactor {

    override fun fetchPersons() {
        App.realm?.executeTransaction {
            val persons = it.where(Person::class.java).findAll()
            if (persons.size != 0) {
                output.onFetch(persons)
            } else {
                output.onEmptyResult()
            }
        }
    }

}