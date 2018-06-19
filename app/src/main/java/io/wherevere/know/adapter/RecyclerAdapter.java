package io.wherevere.know.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.wherevere.know.R;
import io.wherevere.know.entity.Article;

/**
 * @author wherevere
 * @version 1.0
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {

    private Context mContext;
    private List<Article> mDataList = new ArrayList<>();

    public RecyclerAdapter(Context context, List<Article> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.loadData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView item_title;
        TextView item_author;
        TextView item_type;
        TextView item_time;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
            item_author = itemView.findViewById(R.id.item_author);
            item_type = itemView.findViewById(R.id.item_type);
            item_time = itemView.findViewById(R.id.item_time);
        }

        void loadData(Article article){
            item_title.setText(article.getTitle());
            item_author.setText(article.getAuthor());
            item_type.setText(article.getChapterName());
            item_time.setText(article.getNiceDate());
        }
    }

    public void refreshList(List<Article> articleList) {
        if (articleList != null && !articleList.isEmpty()) {
            mDataList.clear();
            mDataList.addAll(articleList);
        }
    }

    public void loadmoreList(List<Article> articleList) {
        if (articleList != null && !articleList.isEmpty()) {
            mDataList.addAll(articleList);
        }
    }
}
