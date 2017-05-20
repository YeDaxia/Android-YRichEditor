package io.github.yedaxia.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class AbsRVAdapter<M> extends RecyclerView.Adapter {

    private List<M> data;
    private LayoutInflater inflater;


    public AbsRVAdapter(Context context, List<M> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }


    public M getItem(int position) {
        if (data == null || position < 0 || position >= data.size()) {
            return null;
        } else {
            return data.get(position);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IRViewHolder viewHolder = createViewHolder(viewType);
        View itemView = viewHolder.getItemView(inflater, parent);
        viewHolder.findViews(itemView, viewType);
        return new RWrapViewHolder(itemView, viewHolder);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        M model = data.get(position);
        ((RWrapViewHolder) holder).bindViewData(position, model, viewType);
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    @Override
    public int getItemViewType(int position) {
        M model = data.get(position);
        return getItemViewType(position, model);
    }

    public List<M> getListData(){
        return data;
    }


    protected abstract int getItemViewType(int position, M model);

    protected abstract IRViewHolder createViewHolder(int viewType);


    public static class RWrapViewHolder extends RecyclerView.ViewHolder implements IRViewHolder {

        private IRViewHolder proxyViewHolder;


        public RWrapViewHolder(View itemView, IRViewHolder viewHolder) {
            super(itemView);
            this.proxyViewHolder = viewHolder;
        }


        @Override
        public void findViews(View viewContainer, int viewType) {
            proxyViewHolder.findViews(viewContainer, viewType);
        }


        @Override
        public void bindViewData(int position, Object model, int viewType) {
            proxyViewHolder.bindViewData(position, model, viewType);
        }

        @Override
        public View getItemView(LayoutInflater inflater, ViewGroup parentView) {
            return null;
        }

        public IRViewHolder getViewHolder() {
            return proxyViewHolder;
        }
    }
}