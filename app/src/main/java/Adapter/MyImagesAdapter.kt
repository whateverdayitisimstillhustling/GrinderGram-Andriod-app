package Adapter

import Model.Post
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.grindergramapp.R
import com.google.firebase.database.core.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.images_item_layout.*

class MyImagesAdapter(private val mContext :Context , mPost :List<Post>):RecyclerView.Adapter<MyImagesAdapter.ViewHolder?>()
{


    private var mPost :List<Post>?=null

    init {
        this.mPost = mPost
    }

    inner class ViewHolder(@NonNull itemView:android.view.View):RecyclerView.ViewHolder(itemView)
    {
        var postImage :ImageView
        init {
            postImage = itemView.findViewById(R.id.post_image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
     val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
val post :Post = mPost!![position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
    }
}