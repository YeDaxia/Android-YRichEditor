package io.github.yedaxia.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.github.yedaxia.demo.R;
import io.github.yedaxia.demo.html.HElement;
import io.github.yedaxia.demo.html.IHtmlElement;
import io.github.yedaxia.demo.html.ImgElement;
import io.github.yedaxia.demo.html.PElement;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/20.
 */

public class HtmlAdapter extends AbsRVAdapter<IHtmlElement> {

    private static final int ITEM_IMAGE = 1;
    private static final int ITEM_PARAGRAPH = 2;
    private static final int ITEM_HEADING = 3;


    private Context context;

    public HtmlAdapter(Context context, List<IHtmlElement> data) {
        super(context, data);
        this.context = context;
    }

    @Override
    protected int getItemViewType(int position, IHtmlElement model) {
        if(model instanceof ImgElement){
            return ITEM_IMAGE;
        }else if(model instanceof HElement){
            return ITEM_HEADING;
        }else{
            return ITEM_PARAGRAPH;
        }
    }

    @Override
    protected IRViewHolder createViewHolder(int viewType) {
        if(viewType == ITEM_IMAGE){
            return new VHHtmlImg();
        }else if(viewType == ITEM_HEADING){
            return new VHHtmlHeading();
        }else{
            return new VHHtmlParagraph();
        }
    }

    class VHHtmlHeading implements IRViewHolder<IHtmlElement> {

        TextView tvHtmlHeading;

        @Override
        public View getItemView(LayoutInflater inflater, ViewGroup parentView) {
            return inflater.inflate(R.layout.item_html_heading, parentView, false);
        }

        @Override
        public void findViews(View viewContainer, int viewType) {
            tvHtmlHeading = (TextView) viewContainer.findViewById(R.id.tv_html_heading);
        }

        @Override
        public void bindViewData(int position, IHtmlElement model, int viewType) {
            tvHtmlHeading.setText(((HElement)model).getText());
        }
    }


    class VHHtmlImg implements IRViewHolder<IHtmlElement> {

        ImageView ivHtmlImg;

        @Override
        public View getItemView(LayoutInflater inflater, ViewGroup parentView) {
            return inflater.inflate(R.layout.item_html_img, parentView, false);
        }

        @Override
        public void findViews(View viewContainer, int viewType) {
            ivHtmlImg = (ImageView) viewContainer.findViewById(R.id.iv_html_img);
        }

        @Override
        public void bindViewData(int position, IHtmlElement model, int viewType) {
            Glide.with(context).load(((ImgElement)model).getImgUrl()).dontAnimate().into(ivHtmlImg);
        }
    }


    class VHHtmlParagraph implements IRViewHolder<IHtmlElement> {

        TextView tvHtmlParagraph;

        @Override
        public View getItemView(LayoutInflater inflater, ViewGroup parentView) {
            return inflater.inflate(R.layout.item_html_paragraph, parentView, false);
        }

        @Override
        public void findViews(View viewContainer, int viewType) {
            tvHtmlParagraph = (TextView)viewContainer.findViewById(R.id.tv_html_paragraph);
        }

        @Override
        public void bindViewData(int position, IHtmlElement model, int viewType) {
           tvHtmlParagraph.setText(((PElement)model).getText());
        }
    }

}
