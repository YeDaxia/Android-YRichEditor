package io.github.yedaxia.demo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/5/20.
 */

public class SpHelper {

    private static final String SP_FILE_NAME = "edit_content_cache";

    private static final String KEY_CONTENT = "key_content";

    private SharedPreferences mSp;

    public SpHelper(Context context) {
        mSp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void saveContent(String content){
        mSp.edit().putString(KEY_CONTENT, content).apply();
    }

    public String getContent(){
        return mSp.getString(KEY_CONTENT,"");
    }
}
