package Adapter

import Fragments.ProfileFragment
import Model.User
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.MainActivity
import com.example.grindergramapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(private var mConetext : Context,
                  private var mUser :List<User> ,
                  private  var isFragment : Boolean = false): RecyclerView.Adapter<UserAdapter.ViewHolder>()
{

    private var firebasseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mConetext).inflate(R.layout.users_item_layout,parent,false)
        return  UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int)
    {
        val user = mUser[position]
        holder.userNameTextView.text= user.getUsername()
        holder.userFullnameView.text= user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.ProfileimageView)

        checkFollowingStatus(user.getUid(),holder.FollowButon)




        holder.itemView.setOnClickListener ( View.OnClickListener {
           if(isFragment)
           {
               val pref = mConetext.getSharedPreferences("PRESS",Context.MODE_PRIVATE).edit()
               pref.putString("profileId", user.getUid())
               pref.apply()
               (mConetext as FragmentActivity).supportFragmentManager.beginTransaction()
                   .replace(R.id.fragment_container,ProfileFragment()).commit()
           }
            else
           {
               val intent = Intent(mConetext, MainActivity::class.java)
               intent.putExtra("publisherId",user.getUid())
               mConetext.startActivity(intent)
           }
        } )




        holder.FollowButon.setOnClickListener {
            if(holder.FollowButon.text.toString()=="Follow")
            {
                firebasseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebasseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }

                addNotification(user.getUid())
            }
            else
            {
                firebasseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebasseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }



    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var userNameTextView : TextView =itemView.findViewById(R .id.user_name_search)
        var userFullnameView : TextView =itemView.findViewById(R.id.user_full_name_search)
        var ProfileimageView : CircleImageView =itemView.findViewById(R.id.user_profile_image_search)
        var FollowButon : Button =itemView.findViewById(R.id.follow_btn_search)
    }


    private fun checkFollowingStatus(uid: String, followButon: Button)
    {
       val followingRef = firebasseUser?.uid.let { it1 ->
           FirebaseDatabase.getInstance().reference
               .child("Follow").child(it1.toString())
               .child("Following")
       }

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
            if(dataSnapshot.child(uid).exists())
            {
                followButon.text = "Following"
            }
            else
            {
            followButon.text ="Follow"
            }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun addNotification(userId:String)
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference
            .child("Notifications")
            .child(userId)
        val notiMap = HashMap<String , Any>()
        notiMap["userid"] = firebasseUser!!.uid
        notiMap["comment"] = "started following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)
    }
}