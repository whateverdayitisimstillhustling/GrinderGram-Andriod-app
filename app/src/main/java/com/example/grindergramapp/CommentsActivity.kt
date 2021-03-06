package com.example.grindergramapp

import Adapter.CommentAdapter
import Model.Post
import Model.User
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Comment

class CommentsActivity : AppCompatActivity() {

    private var postId =""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter  : CommentAdapter?=null
    private var commentList : MutableList<Model.Comment>? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent =intent
        postId =intent.getStringExtra("postId")
        publisherId =intent.getStringExtra("publisherId")
        firebaseUser =FirebaseAuth.getInstance().currentUser

        var recyclerView : RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comments)
        val linearLayoutManger = LinearLayoutManager(this)
        linearLayoutManger.reverseLayout =true
        recyclerView.layoutManager = linearLayoutManger


        commentList =ArrayList()
        commentAdapter = CommentAdapter(this,commentList)
        recyclerView.adapter = commentAdapter


        userInfo()
        readComments()
        getpostImage()

        post_comments.setOnClickListener(View.OnClickListener {
            if(add_comment!!.text.toString()=="")
            {
                Toast.makeText(this@CommentsActivity , "Please,Write your first comment.",Toast.LENGTH_LONG).show()
            }
            else
            {
                addComments()
            }
        })

    }

    private fun addComments() {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId!!)
        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"]= add_comment!!.text.toString()
        commentsMap["publisher"]= firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)

        addNotification()

        add_comment!!.text.clear()

    }


    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_comments)

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun getpostImage() {
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postId!!).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists())
                {
                    val image = p0.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(post_image_comments)

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun readComments()
    {
        val commentRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        commentRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    commentList!!.clear()
                    for(snapshot in p0.children)
                    {
                        val comment = snapshot.getValue(Model.Comment::class.java)
                        commentList!!.add(comment!!)
                    }
                    commentAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }

        })
    }


    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference
            .child("Notifications")
            .child(publisherId!!)
        val notiMap = HashMap<String , Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["comment"] = "commented " + add_comment!!.text.toString()
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
    }
}
