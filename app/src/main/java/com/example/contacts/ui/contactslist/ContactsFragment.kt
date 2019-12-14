package com.example.contacts.ui.contactslist

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.contacts.R
import com.example.contacts.database.Contact
import com.example.contacts.database.contactsDatabase
import com.example.contacts.viewmodel.ContactsViewModel
import com.example.contacts.viewmodel.ContactsViewModelFactory
import kotlinx.android.synthetic.main.contacts_fragment.*

//ToDo App Icon

class ContactsFragment : Fragment(), ContactsAdapter.Interaction {

    private val viewModel: ContactsViewModel by lazy {
        ViewModelProviders.of(this, ContactsViewModelFactory(contactsDatabase))
            .get(ContactsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)



        return inflater.inflate(R.layout.contacts_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ContactsAdapter(this)
        recycler.adapter = adapter

        viewModel.contacts.observe(this, Observer {
            it?.let { it1 -> adapter.submitList(it1) }
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_contactsFragment_to_addContactFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)

        val searchView: SearchView = menu.findItem(R.id.action_search)
            .actionView as SearchView

        setupSearchView(searchView)

    }

    override fun onItemSelected(position: Int, item: Contact) {
        findNavController().navigate(
            ContactsFragmentDirections.actionContactsFragmentToContactDetailsFragment(item)
        )
    }

    private fun setupSearchView(searchView: SearchView) {

        val queryTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    // This is your adapter that will be filtered
                    if (newText == null || newText.isEmpty())
                        viewModel.getAllContacts()
                    else if (newText.isNotEmpty())
                        newText.let { viewModel.search(it) }

                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    // **Here you can get the value "query" which is entered in the search box.**
                    return true
                }
            }
        searchView.setOnQueryTextListener(queryTextListener)
    }


}
