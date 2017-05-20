package io.github.yedaxia.richeditor;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.github.yedaxia.richedit.R;

/**
 * 标题输入框
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/20.
 */
public class HeadingEditText extends AppCompatEditText{

    private  int heading1TextSize;
    private  int heading2TextSize;
    private  int heading3TextSize;

    private int headingLevel;

    public HeadingEditText(Context context) {
        super(context);
        initTextSize();
    }

    public HeadingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextSize();
    }

    public HeadingEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextSize();
    }

    private void initTextSize(){
        Resources res = getResources();
        heading1TextSize = res.getDimensionPixelSize(R.dimen.rich_font_heading_1);
        heading2TextSize = res.getDimensionPixelSize(R.dimen.rich_font_heading_2);
        heading3TextSize = res.getDimensionPixelSize(R.dimen.rich_font_heading_3);
    }

    public void setLevel(@IRichEditor.HeadingLevel int level){
        this.headingLevel = level;
       switch (level){
           case IRichEditor.HEADING_1:
               setTextSize(TypedValue.COMPLEX_UNIT_PX, heading1TextSize);
               break;
           case IRichEditor.HEADING_2:
               setTextSize(TypedValue.COMPLEX_UNIT_PX, heading2TextSize);
               break;
           case IRichEditor.HEADING_3:
               setTextSize(TypedValue.COMPLEX_UNIT_PX, heading3TextSize);
               break;
           default:
               break;
       }
    }

    /**
     * 获取最终生成的html
     * @return
     */
    public String getHtml(){
        return String.format("<h%d>%s</h%d>", headingLevel, getText().toString(), headingLevel);
    }
}
