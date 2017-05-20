package io.github.yedaxia.demo.html;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/20.
 */

public class HElement implements IHtmlElement{

    private CharSequence text;

    public HElement(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

}
