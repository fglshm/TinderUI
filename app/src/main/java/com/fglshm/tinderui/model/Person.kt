package com.fglshm.tinderui.model

import io.realm.RealmObject
import io.realm.annotations.Required


open class Person(
    @Required
    open var name: String = "",
    @Required
    open var description: String = ""
) : RealmObject() {
    override fun toString(): String = "name: $name description: $description"
}
