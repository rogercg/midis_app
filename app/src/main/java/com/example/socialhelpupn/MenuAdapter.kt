package com.example.socialhelpupn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MenuAdapter(private val context: Context, private val textos: Array<String>, private val imagenes: IntArray) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = textos.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.grid_single, parent, false)
        val textView: TextView = view.findViewById(R.id.grid_text)
        val imageView: ImageView = view.findViewById(R.id.grid_image)
        textView.text = textos[position]
        imageView.setImageResource(imagenes[position])
        return view
    }
}

