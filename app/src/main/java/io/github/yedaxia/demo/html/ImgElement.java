package io.github.yedaxia.demo.html;

/**
 * 图片元素
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/12.
 */

public class ImgElement implements IHtmlElement{

    private String imgUrl;

    private int width;

    private int height;

    public ImgElement(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ImgElement(String imgUrl, int width, int height) {
        this.imgUrl = imgUrl;
        this.width = width;
        this.height = height;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
