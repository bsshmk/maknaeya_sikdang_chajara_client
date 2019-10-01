package com.mksoft.maknaeya_sikdang_chajara.ui_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mksoft.maknaeya_sikdang_chajara.R
import com.mksoft.maknaeya_sikdang_chajara.model.Review
import kotlinx.android.synthetic.main.review_view.view.*
import java.util.*

class ReviewListAdapter : RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder>(){

    private var reviewList:List<Review> = Collections.emptyList()

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListAdapter.ReviewViewHolder {
        return ReviewViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    fun updateReviewList(reviewList: List<Review>){
        this.reviewList = reviewList
        notifyDataSetChanged()
    }
    inner class ReviewViewHolder(parent:ViewGroup):RecyclerView.ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.review_view, parent, false)){
        private val nameAndRate = itemView.review_view_nameAndRate_TextView
        private val contents = itemView.review_view_contents_TextView
        fun bind(review:Review){
            nameAndRate.text = review.writer_id+" / "+ review.review_score
            contents.text = review.review_contents
        }
    }


}