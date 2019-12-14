package com.example.contacts.database

import io.realm.Realm
import io.realm.RealmResults


class ContactsDatabaseImpl : ContactDatabase {

    /**
     * Realm objects can only be accessed on the thread they were created in App Widget.
     * and so we initialize it inside every method to avoid this exception
     */

    /*
    private val realm: Realm by lazy {
         Realm.getDefaultInstance()
     }

    private val query: RealmQuery<Contact> by lazy {
        Realm.getDefaultInstance().where<Contact>(Contact::class.java)
    }*/

    override fun insert(contact: Contact) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(contact)
        realm.commitTransaction()
    }

    override fun update(contact: Contact) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(contact)
        realm.commitTransaction()
    }

    override fun search(text: String): MutableList<Contact>? {
        val realm = Realm.getDefaultInstance()
        val query = realm.where<Contact>(Contact::class.java)
        val realmResults: RealmResults<Contact> = query
            .contains("name", text)
            .or()
            .contains("mobile", text)
            .findAll()

        return realm.copyFromRealm(realmResults)
    }

    override fun get(mobile: String): Contact? {
        val query = Realm.getDefaultInstance().where<Contact>(Contact::class.java)
        return query.equalTo("mobile", mobile).findFirst()

    }

    override fun delete(mobile: String) {
        val realm = Realm.getDefaultInstance()
        val query = realm.where<Contact>(Contact::class.java)
        val realmResults = query.equalTo("mobile", mobile).findFirst()
        realm.beginTransaction()
        realmResults?.deleteFromRealm()
        realm.commitTransaction()
    }

    override fun clear() {
        val query = Realm.getDefaultInstance().where<Contact>(Contact::class.java)
        val realmResults: RealmResults<Contact> = query.findAll()
        realmResults.deleteAllFromRealm()
    }

    override fun getAllContacts(): MutableList<Contact>? {
        val realm = Realm.getDefaultInstance()
        val query = realm.where<Contact>(Contact::class.java)
        val realmResults: RealmResults<Contact> = query.findAll()
        return realm.copyFromRealm(realmResults)
    }

    override fun close() {
        val realm = Realm.getDefaultInstance()
        realm.close()
    }
}