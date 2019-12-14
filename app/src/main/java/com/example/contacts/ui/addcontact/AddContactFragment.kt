package com.example.contacts.ui.addcontact

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.contacts.R
import com.example.contacts.database.contactsDatabase
import com.example.contacts.viewmodel.ContactsViewModel
import com.example.contacts.viewmodel.ContactsViewModelFactory
import kotlinx.android.synthetic.main.fragment_add_contact.*


class AddContactFragment : Fragment() {

    private val viewModel: ContactsViewModel by lazy {
        ViewModelProviders.of(this, ContactsViewModelFactory(contactsDatabase))
            .get(ContactsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_24dp)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_contact, container, false)

        viewModel.contactExist.observe(this, Observer {
            showAlertDialog()
        })


        viewModel.validName.observe(this, Observer {
            if (it == false) nameEditText.error = getString(R.string.required)
        })

        viewModel.validMobile.observe(this, Observer {
            if (it == false) phoneEditText.error = getString(R.string.required)
        })

        viewModel.navigateToMainList.observe(this, Observer {
            if (it == true) {
                this.findNavController()
                    .navigate(R.id.action_addContactFragment_to_contactsFragment)
                viewModel.doneNavigating()
            }
        })

        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_save -> {
                viewModel.addContact(nameEditText.text.toString(), phoneEditText.text.toString())
                closeKeyboard()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun closeKeyboard() {
        val imm: InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)

    }

    private fun showAlertDialog() {
        context?.let {
            // Initialize a new instance of
            val builder = AlertDialog.Builder(it)

            // Set the alert dialog title
            builder.setTitle(getString(R.string.contact_already_exist))

            // Display a message on alert dialog
            builder.setMessage(getString(R.string.are_you_want_update))

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                // Do something when user press the positive button
                viewModel.updateContact(nameEditText.text.toString(), phoneEditText.text.toString())
            }

            // Display a negative button on alert dialog
            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }

    }

}
