package com.example.contacts.ui.contactdetails

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.contacts.R
import com.example.contacts.database.Contact
import com.example.contacts.database.contactsDatabase
import com.example.contacts.viewmodel.ContactsViewModel
import com.example.contacts.viewmodel.ContactsViewModelFactory
import kotlinx.android.synthetic.main.fragment_contact_details.*
import kotlinx.android.synthetic.main.item_telephone.*


class ContactDetailsFragment : Fragment() {

    private val CALL_REQUEST_CODE = 100

    private lateinit var mContact: Contact
    private val viewModel: ContactsViewModel by lazy {
        ViewModelProviders.of(this, ContactsViewModelFactory(contactsDatabase))
            .get(ContactsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact_details, container, false)

        val contact = ContactDetailsFragmentArgs.fromBundle(arguments!!)

        mContact = contact.contact

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = mContact.name
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)

        phoneTextView.text = mContact.mobile

        toolbar.setNavigationOnClickListener {
            findNavController().navigate(
                ContactDetailsFragmentDirections.actionContactDetailsFragmentToContactsFragment()
            )
        }

        callImageButton.setOnClickListener { call() }
        callLayout.setOnClickListener { call() }


        viewModel.navigateToMainList.observe(this, Observer {
            findNavController().navigate(ContactDetailsFragmentDirections.actionContactDetailsFragmentToContactsFragment())
        })

        deleteImageButton.setOnClickListener {
            viewModel.delete(mContact)
            viewModel.doneNavigating()
        }
    }

    private fun call() {

        context?.let {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    CALL_REQUEST_CODE
                )
            } else {

                startCalling()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCalling()
                } else {
                    Toast.makeText(
                        context,
                        "Enable phone permission to start calling",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun startCalling() {
        try {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + phoneTextView.getText().toString())
            startActivity(callIntent)
        } catch (activityException: ActivityNotFoundException) {
            Toast.makeText(context, "Call failed$activityException", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun parentActionBar(hide: Boolean) {
        if (activity is AppCompatActivity) {

            if (hide) (activity as AppCompatActivity).supportActionBar?.hide()
            else (activity as AppCompatActivity).supportActionBar?.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        parentActionBar(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActionBar(true)
    }
}
