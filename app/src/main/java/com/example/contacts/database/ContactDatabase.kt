package com.example.contacts.database

val contactsDatabase: ContactDatabase by lazy {
    ContactsDatabaseImpl()
}

interface ContactDatabase {

    fun insert(contact: Contact)

    fun update(contact: Contact)

    fun search(text: String): MutableList<Contact>?

    fun get(mobile: String): Contact?

    fun delete(mobile: String)

    fun clear()

    fun close()

    fun getAllContacts(): MutableList<Contact>?

}

