package com.example.contacts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.contacts.database.Contact
import com.example.contacts.database.ContactDatabase
import kotlinx.coroutines.*

class ContactsViewModel(private val database: ContactDatabase) : ViewModel() {

    private val _contacts = MutableLiveData<List<Contact>>()

    val contacts: MutableLiveData<List<Contact>> by lazy {
        getAllContacts()
        return@lazy _contacts
    }

    //*******************************************************

    private val _contactExist = MutableLiveData<Boolean?>()

    val contactExist: LiveData<Boolean?>
        get() = _contactExist

    //*******************************************************

    private val _navigateToMainList = MutableLiveData<Boolean?>()

    val navigateToMainList: LiveData<Boolean?>
        get() = _navigateToMainList

    //*******************************************************

    private val _validName = MutableLiveData<Boolean?>()

    val validName: LiveData<Boolean?>
        get() = _validName

    private val _validMobile = MutableLiveData<Boolean?>()

    val validMobile: LiveData<Boolean?>
        get() = _validMobile

    //*******************************************************

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     */
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    //*****************************************************

    fun getAllContacts() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                _contacts.postValue(database.getAllContacts())
            }
        }
    }


    fun addContact(name: String, mobile: String) {

        if (checkIfValidContact(name, mobile)) {
            uiScope.launch {

                val exist = checkIfExist(mobile)

                if (!exist) {
                    withContext(Dispatchers.IO) {
                        database.insert(Contact(name, mobile))
                        _navigateToMainList.postValue(true)
                    }

                } else {
                    _contactExist.value = true
                }
            }
        }
    }

    fun updateContact(name: String, mobile: String) {
        if (checkIfValidContact(name, mobile)) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    database.update(Contact(name, mobile))
                }
                _navigateToMainList.value = true
            }
        }
    }

    private suspend fun checkIfExist(mobile: String): Boolean {
        return withContext(Dispatchers.IO) {
            val contact = database.get(mobile)
            contact != null
        }
    }

    private fun checkIfValidContact(name: String, mobile: String): Boolean {

        _validName.value = name.isNotEmpty()

        _validMobile.value = mobile.isNotEmpty()

        return _validName.value as Boolean && _validMobile.value as Boolean
    }

    fun search(text: String) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                _contacts.postValue(database.search(text))
            }

        }
    }

    fun delete(contact: Contact) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                contact.mobile?.let { database.delete(it) }
                _navigateToMainList.postValue(true)
            }
        }
    }

    fun doneNavigating() {
        _navigateToMainList.value = null
    }

    override fun onCleared() {
        super.onCleared()

        database.close()
        viewModelJob.cancel()

    }
}
