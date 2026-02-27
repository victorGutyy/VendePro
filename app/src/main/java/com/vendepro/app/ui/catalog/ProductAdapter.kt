package com.vendepro.app.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vendepro.app.R
import com.vendepro.app.data.model.Product
import com.vendepro.app.databinding.ItemProductCardBinding
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductAdapter(
    private val onShare: (Product) -> Unit,
    private val onDelete: (Product) -> Unit,
    private val onSharePersonal: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProductViewHolder(ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ProductViewHolder(private val b: ItemProductCardBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(p: Product) {
            b.tvProductName.text = p.productName
            b.tvBusiness.text    = p.businessName
            b.tvSeller.text      = p.sellerName
            b.tvContact.text     = p.contactNumber
            b.tvPrice.text       = NumberFormat.getCurrencyInstance(Locale("es","CO")).format(p.price)
            b.tvDate.text        = SimpleDateFormat("dd/MM/yy hh:mm a", Locale("es","CO")).format(Date(p.uploadedAt))

            val file = File(p.imagePath)
            if (file.exists())
                Glide.with(b.root).load(file).centerCrop().placeholder(R.drawable.ic_product_placeholder).into(b.ivProduct)
            else
                b.ivProduct.setImageResource(R.drawable.ic_product_placeholder)

            b.btnShare.setOnClickListener         { onShare(p) }
            b.btnSharePersonal.setOnClickListener { onSharePersonal(p) }
            b.btnDelete.setOnClickListener        { onDelete(p) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(a: Product, b: Product) = a.id == b.id
            override fun areContentsTheSame(a: Product, b: Product) = a == b
        }
    }
}
