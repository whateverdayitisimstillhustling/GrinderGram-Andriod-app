package Fragments

import Adapter.NotificationAdapter
import Model.Notification
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.grindergramapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class NotificationsFragment : Fragment() {

    private var notificationList:List<Notification>?=null
    private var notificationAdapter :NotificationAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val recyclerView :RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_notifications)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        notificationList = ArrayList()

        notificationAdapter = NotificationAdapter(context!!, notificationList as ArrayList<Notification>)
        recyclerView.adapter = notificationAdapter

        readNotifications()

        return view
    }

    private fun readNotifications()
    {
    val notiRef = FirebaseDatabase.getInstance()
        .reference
        .child("Notifications")
        .child(FirebaseAuth.getInstance().currentUser!!.uid)

        notiRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    (notificationList as ArrayList<Notification>).clear()
                    for(snapshot in p0.children)
                    {
                        val notification= snapshot.getValue(Notification::class.java)
                        (notificationList as ArrayList<Notification>).add(notification!!)
                    }

                    Collections.reverse(notificationList)
                    notificationAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

}
