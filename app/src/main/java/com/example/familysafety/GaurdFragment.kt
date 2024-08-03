package com.example.familysafety

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.Manifest

import android.nfc.cardemulation.CardEmulation
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.familysafety.databinding.FragmentGaurdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GaurdFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = GaurdFragment()
    }


    lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    lateinit var binding: FragmentGaurdBinding
    private val CALL_PERMISSION_REQUEST_CODE = 100
    private val SMS_PERMISSION_REQUEST_CODE = 101
    val emergencyContacts =  mutableListOf("+15551234567", "09752107631", "551234567")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGaurdBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pinkcard: CardView = view.findViewById(R.id.pink_card)

        val sendinvite: Button = view.findViewById(R.id.send_invite)
        val inviteName: EditText = view.findViewById(R.id.invite_name)
        val inviteCon: EditText = view.findViewById(R.id.invite_con)
        val inviteRel: EditText = view.findViewById(R.id.invite_rel)
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Contacts")



        sendinvite.setOnClickListener {
            val name = inviteName.text.toString()
            val number = inviteCon.text.toString()
            val address = inviteRel.text.toString()

            sendInvite()
            val userDetails = HashMap<String, String>()
            userDetails["Name"] = name
            userDetails["Contact"] = number
            userDetails["Relation"] = address
            val key = ref.push().key
            if (key != null) {
                ref.child(key).setValue(userDetails)
                    .addOnCompleteListener { registrationTask ->
                        if (registrationTask.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "User registered successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "User registration unsuccessful!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }}

        pinkcard.setOnClickListener {
            // List of emergency contacts
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED) {
                sendEmergencySMS()
            } else {
                // Request SMS permission
                requestPermissions(arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_REQUEST_CODE)
            }
        }


        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, send SMS
                    sendEmergencySMS()
                } else {
                    // Permission denied, show a message or handle accordingly
                    Toast.makeText(requireContext(), "SMS permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        val greencard: CardView = view.findViewById(R.id.green_card)
        greencard.setOnClickListener {
            // Make a call to the emergency number (e.g., 123456789)
            val phoneNumber = "100"
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))

            // Check for permission before making the call
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            } else {
                // Request CALL_PHONE permission if not granted
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_REQUEST_CODE)
            }
        }



    }

    private fun sendEmergencySMS() {

        // List of emergency contacts


        // Message to be sent
        val message = "This is an emergency message. My location is Manipal Institute Of Technology. Please help!"

        // Iterate through each contact and send SMS
        for (contact in emergencyContacts) {
            try {
                // Get the default instance of SmsManager
                val smsManager = SmsManager.getDefault()

                // Send SMS to the contact
                smsManager.sendTextMessage(contact, null, message, null, null)

                Toast.makeText(requireContext(), "Emergency SMS sent successfully", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Failed to send emergency SMS", Toast.LENGTH_SHORT).show()
                ex.printStackTrace()
            }
        }

    }

    private fun sendInvite() {
        val inviteName = binding.inviteName.text.toString()
        val inviteContact = binding.inviteCon.text.toString()
        val inviteRelation = binding.inviteRel.text.toString()

        emergencyContacts.add(inviteContact)
        Toast.makeText(requireContext(), "Contact added", Toast.LENGTH_SHORT).show()
        val firestore = Firebase.firestore

        val data = hashMapOf(
            "name" to inviteName,
            "contact" to inviteContact,
            "relation" to inviteRelation
        )

        val senderMail = FirebaseAuth.getInstance().currentUser?.email.toString()

        firestore.collection("invites")
            .document(senderMail)
            .set(data)
            .addOnSuccessListener {
                Log.d("SendInvite", "Invite sent successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("SendInvite", "Error sending invite", exception)
            }
    }
}