package com.fglshm.tinderui.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.fglshm.tinderui.model.Person
import io.realm.Realm

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null
        var realm: Realm? = null
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        Realm.init(applicationContext)
        realm = Realm.getDefaultInstance()

        val persons = App.realm?.where(Person::class.java)?.findAll()
        realm?.executeTransaction {
            persons?.deleteAllFromRealm()
            Log.d("App", "[ ALL DELETED ]")
        }
    }

}