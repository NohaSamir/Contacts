package com.example.contacts.database

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Contact(
    var name: String = "",
    @PrimaryKey
    var mobile: String? = ""
) : RealmObject(), Parcelable {

    companion object DummyContacts {

        fun getDummyContacts(): List<Contact> {

            val contact1 = Contact("Noha", "01222222222")
            val contact2 = Contact("Mona", "01222225555")

            return mutableListOf(contact1, contact2);
        }
    }
}