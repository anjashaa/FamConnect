package com.example.familysafety

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class homeFragment : Fragment() {
    lateinit var inviteAdapter: InviteAdapter
    lateinit var mContext: Context
    private val listContacts: ArrayList<ContactModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listMembers= listOf<MemberModel>(
            MemberModel(
                "Anjasha ",
                "Flat no.304,Building E2 Rosewood,Bangalore ",
                "90%",
                "395"
            ),
            MemberModel(
                "Mayuri",
                "10th building,  onetown road, Mangalore ",
                "80%",
                "62"
            ),
            MemberModel(
                "Ritunjaya",
                "11th building, 4th floor, coin circle road, manipal ",
                "70%",
                "7"
            ),
            MemberModel(
                "Anishka",
                "12th  yodidham road, Banglore ",
                "69%",
                "395"
            ),
            MemberModel(
                "Anshita",
                "12th  sevenup road,vanastalipuram, Mangalore ",
                "59%",
                "62"
            ),
        )
        val adapter = MemberAdapter(listMembers)
        val recycler=requireView().findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager=LinearLayoutManager(requireContext())
        recycler.adapter=adapter


        val inviteAdapter = InviteAdapter( fetchContact())
        /*CoroutineScope(Dispatchers.IO).launch {
            Log.d("FetchContact89", "fetchContacts: coroutine start")

            insertDatabaseContacts(fetchContacts())

            Log.d("FetchContact89", "fetchContacts: coroutine end ${listContacts.size}")
        }*/

        val inviteRecycler=requireView().findViewById<RecyclerView>(R.id.recycler_invite)
        inviteRecycler.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        inviteRecycler.adapter=inviteAdapter

        val threedots =requireView().findViewById<ImageView>(R.id.icon_three_dots)
        threedots.setOnClickListener{

            FirebaseAuth.getInstance().signOut()
        }

    }

    @SuppressLint("Range")
    private fun fetchContact(): ArrayList<ContactModel> {
val cr=requireActivity().contentResolver
        val cursor =cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)
        val listContacts: ArrayList<ContactModel> = ArrayList()

        if(cursor!=null&&cursor.count>0){
            while(cursor!=null&& cursor.moveToNext()){
                val id =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                if(hasPhoneNumber> 0){
                    val pCr = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        ""
                    )
                    if(pCr!=null &&  pCr.count>0){
                        while(pCr!=null&& pCr.moveToNext()){
                            val phoneNum =pCr.getString(pCr.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            listContacts.add(ContactModel(name,phoneNum))
                        }
                        pCr.close()
                    }
                }
            }
            if(cursor!=null){
                cursor.close()
            }
        }
        return listContacts
    }

    companion object {

        @JvmStatic
        fun newInstance() = homeFragment()
    }
}