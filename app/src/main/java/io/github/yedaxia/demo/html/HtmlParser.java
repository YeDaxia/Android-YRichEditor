package io.github.yedaxia.demo.html;

import android.text.Html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.ArrayList;
import java.util.List;

import io.github.yedaxia.demo.util.NumberUtils;
import io.github.yedaxia.demo.util.StringUtils;

/**
 * html标签解析
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/12.
 */

public class HtmlParser {

    /**
     *
     * @param htmlContent
     * @return
     */
    public static List<IHtmlElement> parse(String htmlContent) {
        if (StringUtils.isEmpty(htmlContent)) {
            return null;
        }
        Document doc = Jsoup.parseBodyFragment(htmlContent);
        List<Node> childNodeList = doc.body().childNodes();
        if (childNodeList == null || childNodeList.isEmpty()) {
            return null;
        }
        final int size = childNodeList.size();
        List<IHtmlElement> elList = new ArrayList<>();
        for (int pos = 0; pos != size; pos++) {
            Node childNode = childNodeList.get(pos);
            String tagName = childNode.nodeName();
            if (tagName.equalsIgnoreCase("h")) {
                elList.add(new PElement(Html.fromHtml(((Element) childNode).html())));
            } else if(tagName.equalsIgnoreCase("h1")){
                elList.add(new HElement(((Element) childNode).html()));
            }else if (tagName.equalsIgnoreCase("img")) {
                String src = childNode.attr("src");
                String width = childNode.attr("width");
                String height = childNode.attr("height");
                elList.add(new ImgElement(src, NumberUtils.parseInt(width, 0), NumberUtils.parseInt(height, 0)));
            } else {
                if (childNode instanceof Element) {
                    elList.add(new PElement(Html.fromHtml(((Element) childNode).html())));
                } else {
                    elList.add(new PElement(htmlContent));
                }
            }
        }
        return elList;
    }

}
