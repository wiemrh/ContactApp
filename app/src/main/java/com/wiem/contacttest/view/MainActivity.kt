package com.wiem.contacttest.view


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wiem.contacttest.R
import com.wiem.contacttest.core.BaseActivity
import com.wiem.contacttest.data.database.ContactDatabase
import com.wiem.contacttest.data.repository.ContactRepository
import com.wiem.contacttest.model.UIState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn

@FlowPreview
class MainActivity : BaseActivity() {

    private val viewModel: ContactViewModel by viewModels {
        ContactViewModelFactory(application, ContactRepository(ContactDatabase.invoke(this)))
    }
    private lateinit var contactListAdapter: ContactListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.hideKeyboard()

        setAdapter()
        loadContact()
        requestContactPermission()
        handleClick()

    }

    private fun loadContact() {

        lifecycleScope.launchWhenCreated {
            viewModel.uiState
                .debounce(300)
                .flowOn(Dispatchers.Main)
                .collectLatest { state ->
                    when (state) {
                        is UIState.Loading -> {
                            progress.visibility = View.VISIBLE
                            rvContact.visibility = View.GONE
                            errorLayout.visibility = View.GONE
                        }
                        is UIState.Success -> {
                            progress.visibility = View.GONE
                            rvContact.visibility = View.VISIBLE
                            errorLayout.visibility = View.GONE
                            contactListAdapter.setData(
                                state.data
                            )
                        }
                        is UIState.Failed -> {
                            progress.visibility = View.GONE
                            rvContact.visibility = View.GONE
                            errorLayout.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }

    private fun handleClick() {
        addContact.setOnClickListener {
            saveContact()
        }
        search_contact_edt.doAfterTextChanged {
            viewModel.loadContact(it.toString())
        }
    }

    private fun setAdapter() {
        contactListAdapter = ContactListAdapter(this, this@MainActivity)
        rvContact.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = contactListAdapter
            this.setHasFixedSize(true)
            this.adapter?.notifyDataSetChanged()
            this.scheduleLayoutAnimation()
        }
    }

    private fun saveContact() {
        val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                errorLayout.visibility = View.GONE
                viewModel.init()
            } else {
                errorLayout.visibility = View.VISIBLE
                progress.visibility = View.GONE
            }
        }
    }

    private fun requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            &&
            checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                PERMISSIONS_REQUEST_READ_WRITE_CONTACTS
            )
        } else {
            viewModel.init()
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_READ_WRITE_CONTACTS = 100
    }
}

