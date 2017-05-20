package io.github.yedaxia.demo.html;

/**
 * 段落元素
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/12.
 */

public class PElement implements IHtmlElement{

    private CharSequence text;

    public PElement(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }
}
