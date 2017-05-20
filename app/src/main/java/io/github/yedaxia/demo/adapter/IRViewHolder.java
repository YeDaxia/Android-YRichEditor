package io.github.yedaxia.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/2/27
 */

public interface IRViewHolder<M> {

    View getItemView(LayoutInflater inflater, ViewGroup parentView);

    /**
     * 调用findViewById绑定View对象
     * @param viewContainer
     * @param viewType
     */
    void findViews(View viewContainer, int viewType);

    /**
     * 绑定和View相关联的数据
     */
    void bindViewData(int position, M model, int viewType);
}
