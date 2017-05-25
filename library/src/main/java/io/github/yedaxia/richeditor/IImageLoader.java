package io.github.yedaxia.richeditor;

import android.net.Uri;
import android.widget.ImageView;

/**
 * 加载图片
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/25.
 */

public interface IImageLoader {

    /**
     * 加载图片接口
     * @param imageView
     * @param uri
     */
    void loadIntoImageView(ImageView imageView, Uri uri);
}
