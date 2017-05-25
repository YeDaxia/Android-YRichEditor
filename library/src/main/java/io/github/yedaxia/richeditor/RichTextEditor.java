package io.github.yedaxia.richeditor;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.commonsware.cwac.richedit.Effect;
import com.commonsware.cwac.richedit.RichEditText;
import com.commonsware.cwac.richtextutils.SpanTagRoster;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;
import com.github.yedaxia.richedit.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.List;


/**
 * 富文本编辑器
 */
public class RichTextEditor extends ScrollView implements IRichEditor{

    private LinearLayout rootLayout;
    private LayoutInflater inflater;
    private OnKeyListener keyListener; // 所有EditText的软键盘监听器
    private OnClickListener btnListener; // 图片右上角红叉按钮监听器
    private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
    private EditText currentFocusEdit; // 最近被聚焦的EditText

    private IUploadEngine mUploadEngine;
    private RichEditText.OnSelectionChangedListener selectChangeListener;
    private IImageLoader mImageLoader;

    private final RichEditText.OnSelectionChangedListener wrapSelectChangeListener = new RichEditText.OnSelectionChangedListener() {
        @Override
        public void onSelectionChanged(int start, int end, List<Effect<?>> effects) {
            if(selectChangeListener != null){
                selectChangeListener.onSelectionChanged(start, end, effects);
            }
        }
    };

    public RichTextEditor(Context context) {
        this(context, null);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int editorPaddingLeft;
        int editorPaddingRight;
        int editorPaddingTop;
        int editorPaddingBottom;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RichTextEditor);
        editorPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.RichTextEditor_rich_paddingLeft,0);
        editorPaddingRight = typedArray.getDimensionPixelSize(R.styleable.RichTextEditor_rich_paddingRight,0);
        editorPaddingTop = typedArray.getDimensionPixelSize(R.styleable.RichTextEditor_rich_paddingTop, 0);
        editorPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.RichTextEditor_rich_paddingBottom, 0);
        typedArray.recycle();

        inflater = LayoutInflater.from(context);

        // 1. 初始化rootLayout
        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        //rootLayout.setBackgroundColor(Color.WHITE);
        setupLayoutTransitions();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rootLayout.setPadding(editorPaddingLeft, editorPaddingTop, editorPaddingRight, editorPaddingBottom);//设置间距，防止生成图片时文字太靠边，不能用margin，否则有黑边
        addView(rootLayout, layoutParams);

        // 2. 初始化键盘退格监听
        // 主要用来处理点击回删按钮时，view的一些列合并操作
        keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    EditText edit = (EditText) v;
                    onBackspacePress(edit);
                }
                return false;
            }
        };

        // 3. 图片叉掉处理
        btnListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout parentView = (RelativeLayout) v.getParent();
                onImageCloseClick(parentView);
            }
        };

        focusListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentFocusEdit = (EditText) v;
                }
            }
        };

        RichEditText firstEdit = createPEditText();
        rootLayout.addView(firstEdit);
        currentFocusEdit = firstEdit;
    }

    /**
     * 设置图片上传引擎
     * @param uploadEngine
     */
    public void setUploadEngine(IUploadEngine uploadEngine){
        this.mUploadEngine = uploadEngine;
    }

    /**
     * 设置图片加载器
     * @param imageLoader
     */
    public void setImageLoader(IImageLoader imageLoader){
        this.mImageLoader = imageLoader;
    }

    /**
     * 初始化transition动画
     */
    private void setupLayoutTransitions() {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(300);
        rootLayout.setLayoutTransition(transition);
    }

    /**
     * 处理软键盘backSpace回退事件
     *
     * @param editTxt 光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
        if (startSelection == 0) {
            int editIndex = rootLayout.indexOfChild(editTxt);
            View preView = rootLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
            // 则返回的是null
            if (null != preView) {
                if (preView instanceof RichImageLayout) {
                    // 光标EditText的上一个view对应的是图片
                    onImageCloseClick(preView);
                } else if (preView instanceof EditText) {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    Editable bottomText = editTxt.getText();
                    EditText preEdit = (EditText) preView;
                    rootLayout.removeView(editTxt);
                    // 文本合并
                    final int cursorIndex = preEdit.length();
                    preEdit.getText().append(bottomText);
                    preEdit.setSelection(cursorIndex);
                    preEdit.requestFocus();
                    currentFocusEdit = preEdit;
                }
            }
        }
    }

    /**
     * 处理图片叉掉的点击事件
     *
     * @param view 整个image对应的relativeLayout view
     * @type 删除类型 0代表backspace删除 1代表按红叉按钮删除
     */
    private void onImageCloseClick(View view) {
        EditImageView editImageView = (EditImageView)view.findViewById(R.id.edit_imageView);
        editImageView.cancelUpload();
        rootLayout.removeView(view);
    }

    /**
     * 看是否所有图片都上传成功，如果失败的试着重新上传。
     * @return
     */
    public int tryIfSuccessAndReUpload(){
        int viewCount = rootLayout.getChildCount();
        int result = IUploadEngine.STATUS_UPLOAD_SUCCESS;
        for(int i = 0; i != viewCount ; ++i){
            View childView = rootLayout.getChildAt(i);
            if(childView instanceof RichImageLayout){
                EditImageView editImageView = (EditImageView)childView.findViewById(R.id.edit_imageView);
                int uploadStatus = editImageView.getUploadStatus();
                if(uploadStatus == IUploadEngine.STATUS_UPLOAD_FAIL){
                    editImageView.doUpload();
                    result = uploadStatus;
                }else if(uploadStatus != IUploadEngine.STATUS_UPLOAD_SUCCESS){
                    result = uploadStatus;
                }
            }
        }
        return result;
    }

    @Override
    public void setHtmlContent(String htmlContent) {
        if(htmlContent == null){
            return;
        }
        Document doc = Jsoup.parseBodyFragment(htmlContent);
        List<Node> childNodeList = doc.body().childNodes();
        if(childNodeList == null || childNodeList.isEmpty()){
            return;
        }
        rootLayout.removeAllViews();
        final int size = childNodeList.size();
        for(int pos = 0 ; pos != size ; pos++){
            Node childNode = childNodeList.get(pos);
            String tagName = childNode.nodeName();
            if(tagName.equalsIgnoreCase("p")){
                addPEditTextAtIndex(pos, Html.fromHtml(((Element)childNode).html()));
            }else if(tagName.equalsIgnoreCase("h1")){
                addHEditTextAtIndex(HEADING_1, pos, ((Element)childNode).html());
            }else if(tagName.equalsIgnoreCase("img")){
                Uri imgUri = Uri.parse(childNode.attr("src"));
                addImageViewAtIndex(pos, imgUri);
            }else{
                addPEditTextAtIndex(pos, childNode.outerHtml());
            }
        }
        if(rootLayout.getChildAt(size - 1) instanceof RichImageLayout){
            addPEditTextAtIndex(size,"");
        }
    }

    @Override
    public void insertImage(Uri imgUri) {

        final CharSequence lastFocusText = currentFocusEdit.getText();
        final int cursorIndex = currentFocusEdit.getSelectionStart();
        final int lastEditIndex = rootLayout.indexOfChild(currentFocusEdit);

        final int focusTextLen = lastFocusText.length();

        //光标在文本最前面
        if (cursorIndex == 0) {
            addImageViewAtIndex(lastEditIndex, imgUri);
        } else if(cursorIndex  == focusTextLen){ //光标在最后

            //光标是在最后一个编辑框
            if(rootLayout.getChildCount() - 1 == lastEditIndex){
                currentFocusEdit = addPEditTextAtIndex(lastEditIndex + 1, "");
                currentFocusEdit.requestFocus();
            }

            addImageViewAtIndex(lastEditIndex + 1, imgUri);
        }else { //光标在中间

            final CharSequence leftText = lastFocusText.subSequence(0, cursorIndex);
            // 光标左边文本
            currentFocusEdit.setText(leftText);

            final CharSequence rightText = lastFocusText.subSequence(cursorIndex, focusTextLen);
            // 光标右边文本
            currentFocusEdit = addPEditTextAtIndex(lastEditIndex + 1, rightText);

            addImageViewAtIndex(lastEditIndex + 1, imgUri);
            currentFocusEdit.requestFocus();
        }
    }

    @Override
    public void insertHeading(@HeadingLevel int level) {
        final CharSequence lastFocusText = currentFocusEdit.getText();
        final int cursorIndex = currentFocusEdit.getSelectionStart();
        final int lastEditIndex = rootLayout.indexOfChild(currentFocusEdit);

        final int focusTextLen = lastFocusText.length();

        //光标在文本最前面
        if (cursorIndex == 0) {
            currentFocusEdit = addHEditTextAtIndex(level, lastEditIndex, "");
        } else if(cursorIndex  == focusTextLen){ //光标在最后
            currentFocusEdit = addHEditTextAtIndex(level, lastEditIndex + 1, "");
        }else { //光标在中间

            final CharSequence leftText = lastFocusText.subSequence(0, cursorIndex);
            // 光标左边文本
            currentFocusEdit.setText(leftText);
            addPEditTextAtIndex(lastEditIndex + 1 , "");

            final CharSequence rightText = lastFocusText.subSequence(cursorIndex, focusTextLen);
            // 光标右边文本
            addHEditTextAtIndex(level, lastEditIndex + 2, rightText);
        }

        currentFocusEdit.requestFocus();
    }

    @Override
    public void insertHyperlink(String text, String link) {
        if(currentFocusEdit instanceof RichEditText){
            final int cursorIndex  = currentFocusEdit.getSelectionEnd();
            currentFocusEdit.getText().append(text);
            currentFocusEdit.setSelection(cursorIndex, cursorIndex + text.length());
            ((RichEditText)currentFocusEdit).applyEffect(RichEditText.URL,link);
        }
    }

    @Override
    public void insertParagraph() {
        final CharSequence lastFocusText = currentFocusEdit.getText();
        final int cursorIndex = currentFocusEdit.getSelectionStart();
        final int lastEditIndex = rootLayout.indexOfChild(currentFocusEdit);

        final int focusTextLen = lastFocusText.length();

        //光标在文本最前面
        if (cursorIndex == 0) {
            currentFocusEdit = addPEditTextAtIndex(lastEditIndex, "");
        } else if(cursorIndex  == focusTextLen){ //光标在最后
            currentFocusEdit = addPEditTextAtIndex(lastEditIndex + 1, "");
        }else { //光标在中间

            final CharSequence leftText = lastFocusText.subSequence(0, cursorIndex);
            // 光标左边文本
            currentFocusEdit.setText(leftText);
            currentFocusEdit = addPEditTextAtIndex(lastEditIndex + 1 , "");

            final CharSequence rightText = lastFocusText.subSequence(cursorIndex, focusTextLen);
            // 光标右边文本
            addPEditTextAtIndex(lastEditIndex + 2, rightText);
        }

        currentFocusEdit.requestFocus();
    }

    @Override
    public void toggleBoldSelectText() {
        if(currentFocusEdit instanceof RichEditText){
            ((RichEditText)currentFocusEdit).toggleEffect(RichEditText.BOLD);
        }
    }

    @Override
    public String getHtmlContent() {
        final int count = rootLayout.getChildCount();
        StringBuilder contentBuilder = new StringBuilder();
        SpannedXhtmlGenerator xhtmlGenerator = new SpannedXhtmlGenerator(new SpanTagRoster());

        for(int i = 0 ; i != count ; i++){
            View childView = rootLayout.getChildAt(i);
            if(childView instanceof RichEditText){
                String pHtml = xhtmlGenerator.toXhtml(((RichEditText)childView).getText());
                contentBuilder.append(String.format("<p>%s</p>", pHtml));
            }else if(childView instanceof RichImageLayout){
                EditImageView eImageView = (EditImageView)childView.findViewById(R.id.edit_imageView);
                contentBuilder.append(eImageView.getHtml());
            }else if(childView instanceof HeadingEditText){
                contentBuilder.append(((HeadingEditText)childView).getHtml());
            }
        }
        return contentBuilder.toString();
    }

    @Override
    public void clearContent() {
        rootLayout.removeAllViews();
    }

    /**
     * 设置监听光标变化
     * @param selectChangeListener
     */
    public void setOnSelectChangeListener(RichEditText.OnSelectionChangedListener selectChangeListener) {
        this.selectChangeListener = selectChangeListener;
    }

    /**
     * 在特定位置插入段落编辑框
     *
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    private RichEditText addPEditTextAtIndex(final int index, final CharSequence editStr) {
        RichEditText newEditText = createPEditText();
        newEditText.setText(editStr);
        rootLayout.addView(newEditText, index);
        return newEditText;
    }

    /**
     * 在特定位置添加ImageView
     */
    private void addImageViewAtIndex(final int index, Uri imgUri) {
        final RelativeLayout imageLayout = createImageLayout();
        EditImageView imageView = (EditImageView) imageLayout.findViewById(R.id.edit_imageView);
        imageView.setImageLoader(mImageLoader);
        imageView.setUploadEngine(mUploadEngine);
        imageView.setImageAndUpload(imgUri);
        rootLayout.addView(imageLayout, index);
    }

    /**
     * 在特定位置插入标题编辑框
     * @param index
     * @param editStr
     * @return
     */
    private EditText addHEditTextAtIndex(@HeadingLevel int headingLevel, final int index, final CharSequence editStr){
        HeadingEditText newEditText = createHEditText();
        newEditText.setText(editStr);
        newEditText.setLevel(headingLevel);
        rootLayout.addView(newEditText, index);
        return newEditText;
    }

    /**
     * 生成标题文本输入框
     * @return
     */
    private HeadingEditText createHEditText(){
        HeadingEditText editText = (HeadingEditText)inflater.inflate(R.layout.richedit_heading, this, false);
        editText.setOnKeyListener(keyListener);
        editText.setOnFocusChangeListener(focusListener);
        return editText;
    }

    /**
     * 生成段落文本输入框
     */
    private RichEditText createPEditText() {
        RichEditText editText = (RichEditText) inflater.inflate(R.layout.richedit_paragraph, this, false);
        editText.setOnSelectionChangedListener(wrapSelectChangeListener);
        editText.setOnKeyListener(keyListener);
        editText.setOnFocusChangeListener(focusListener);
        return editText;
    }

    /**
     * 生成图片View
     */
    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.richedit_imageview, this, false);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setTag(layout.getTag());
        closeView.setOnClickListener(btnListener);
        return layout;
    }
}
