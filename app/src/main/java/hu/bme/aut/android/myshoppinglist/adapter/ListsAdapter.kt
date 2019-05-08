package hu.bme.aut.android.myshoppinglist.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import hu.bme.aut.android.myshoppinglist.R
import hu.bme.aut.android.myshoppinglist.data.ShoppingList
import kotlinx.android.synthetic.main.card_slist.view.*

class ListsAdapter(private val context: Context) : RecyclerView.Adapter<ListsAdapter.ViewHolder>() {

    private val postList: MutableList<ShoppingList> = mutableListOf()
    private var lastPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tvName
        val tvDate: TextView = itemView.tvDate
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.card_slist, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val tmpPost = postList[position]
        viewHolder.tvName.text = tmpPost.sname
        viewHolder.tvDate.text = tmpPost.date

        setAnimation(viewHolder.itemView, position)
    }

    override fun getItemCount() = postList.size

    fun addPost(slist: ShoppingList?) {
        slist ?: return

        postList.add(slist)
        notifyDataSetChanged()
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

}