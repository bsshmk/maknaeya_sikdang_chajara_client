package com.mksoft.maknaeya_sikdang_chajara.ui_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mksoft.maknaeya_sikdang_chajara.R
import com.mksoft.maknaeya_sikdang_chajara.databinding.ReviewViewBinding
import com.mksoft.maknaeya_sikdang_chajara.model.Review
import com.mksoft.maknaeya_sikdang_chajara.viewmodel.ReviewViewModel
import kotlinx.android.synthetic.main.review_view.view.*
import java.util.*

class ReviewListAdapter : RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder>(){

    private var reviewList:List<Review> = Collections.emptyList()

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListAdapter.ReviewViewHolder {
        val binding: ReviewViewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.review_view, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    fun updateReviewList(reviewList: List<Review>){
        this.reviewList = reviewList
        notifyDataSetChanged()
    }
    inner class ReviewViewHolder(private val binding:ReviewViewBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = ReviewViewModel()
        fun bind(review:Review){
            viewModel.bind(review)
            binding.viewModel = viewModel
        }
    }


}