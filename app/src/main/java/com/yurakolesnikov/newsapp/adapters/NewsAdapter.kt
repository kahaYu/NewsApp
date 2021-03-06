package com.yurakolesnikov.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yurakolesnikov.newsapp.R
import com.yurakolesnikov.newsapp.databinding.ItemArticlePreviewBinding
import com.yurakolesnikov.newsapp.models.Article
import com.yurakolesnikov.newsapp.utils.formatDate

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    // Pass binding object to have access to the views
    inner class ArticleViewHolder(itemView: View, val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(itemView) {
    }

    // Enhance work of RV, implement basic animation with help of DiffUtil
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // Compute the difference of two lists in the background
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_article_preview,
            parent, false
        )
        val binding = ItemArticlePreviewBinding.bind(view)
        return ArticleViewHolder(view, binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        Glide.with(holder.itemView).load(article.urlToImage).into(holder.binding.ivArticleImage)
        holder.binding.apply {
            tvSource?.text = article.source?.name
            tvTitle.text = article.title
            tvDescription?.text = article.description
            tvPublishedAt.text = article.formattedPublishedAt // Change appearance of date
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(article) } // Make ViewHolder clickable
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}
