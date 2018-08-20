package com.android.example.news.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.news.R;
import com.android.example.news.api.model.Article;
import com.android.example.news.databinding.NewsListItemBinding;
import com.android.example.news.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the news list
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> implements Filterable {

    private List<Article> mDataSet;
    private OnSelectedNewsListener mOnSelectedNewsListener;
    private ValueFilter mValueFilter;
    private List<Article> mFilterList;

    @Override
    public Filter getFilter() {
        if (mValueFilter == null) {
            mValueFilter = new ValueFilter();
        }
        return mValueFilter;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mThumbnail;
        TextView mTitle;
        TextView mAuthors;
        TextView mDate;
        View mItemView;

        ViewHolder(NewsListItemBinding binding) {
            super(binding.getRoot());
            mItemView = binding.getRoot();
            mThumbnail = binding.imgThumbnail;
            mTitle = binding.textTitle;
            mAuthors = binding.textAuthors;
            mDate = binding.textDate;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    NewsListAdapter(List<Article> dataSet, OnSelectedNewsListener onSelectedNewsListener) {
        mDataSet = dataSet;
        mOnSelectedNewsListener = onSelectedNewsListener;
        mFilterList = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public NewsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
        // create a new view
        NewsListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.news_list_item, parent, false);
        return new ViewHolder(binding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Article article = mDataSet.get(position);
        new ImageUtils().loadImage(article.getMedia().get(0).getMediaMetaData().get(7).getUrl(), holder.mThumbnail);
        holder.mTitle.setText(article.getTitle());
        holder.mAuthors.setText(article.getAuthors());
        holder.mDate.setText(article.getDate());
        // set listener on entire row
        holder.mItemView.setOnClickListener(view -> mOnSelectedNewsListener.onNewsSelected(article));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Helper interface for detecting row selection
     */
    public interface OnSelectedNewsListener {
        void onNewsSelected(Article article);
    }

    /**
     * Helper class for filtering the news list
     */
    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<Article> filteredList = new ArrayList<>();
                if (mFilterList != null) {
                    for (Article article : mFilterList) {
                        // if the filter query is in the title then add the article to te filtered list
                        if (article.getTitle().contains(constraint)) {
                            filteredList.add(article);
                        }
                    }
                }
                // new filtered list
                results.count = filteredList.size();
                results.values = filteredList;
            } else {
                // entire list with no filtering
                results.count = mFilterList.size();
                results.values = mFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mDataSet = (List<Article>) results.values;
            notifyDataSetChanged();
        }

    }
}
