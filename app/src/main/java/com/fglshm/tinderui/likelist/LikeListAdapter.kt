package com.fglshm.tinderui.likelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fglshm.tinderui.R
import com.fglshm.tinderui.data.Data
import com.fglshm.tinderui.model.Person
import com.fglshm.tinderui.viewholder.ItemViewHolder
import io.realm.RealmResults
import kotlinx.android.synthetic.main.recyclerview_like_list_item.view.*

class LikeListAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    private val persons = mutableListOf<Person?>()
    var isEmpty = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        if (isEmpty) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_empty_result, parent, false)
            return ItemViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_like_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = if (isEmpty) 1 else persons.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (isEmpty) return
        val person = persons[position]
        holder.itemView.apply {
            val index = Data.nameList.indexOf(person?.name)
            val image = Data.imageList[index]
            text_name.text = person?.name
            text_description.text = person?.description
            Glide.with(this).load(image).into(image_person)
        }
    }

    fun addEmptyRow() {
        persons.add(null)
        notifyItemInserted(itemCount)
    }

    fun addAll(what: RealmResults<Person>) {
        persons.addAll(what)
        notifyDataSetChanged()
    }

}