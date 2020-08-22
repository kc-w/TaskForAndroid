package com.example.taskforandroid.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;


import com.example.taskforandroid.common.FilesUtils;
import com.example.taskforandroid.common.Utils;

import java.util.List;



//图文混排显示类
public class RichEditorNew extends RichEditor {
    private OnTextChangeNewListener mTextChangeListener;
    private OnConsoleMessageListener mOnConsoleMessageListener;
    /**
     * 用于在ontextchange中执行操作标识避免循环
     */
    private boolean isUnableTextChange = false;
    private boolean isNeedSetNewLineAfter = false;

    public final static String FILE_TAG = "/rich_editor";

    public RichEditorNew(Context context) {
        super(context);

    }

    public RichEditorNew(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RichEditorNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }



    public void getCurrChooseParams() {
        exec("javascript:RE.getSelectedNode();");
    }




    public interface OnTextChangeNewListener {
        void onTextChange(String text);
    }

    public interface OnConsoleMessageListener {
        void onTextChange(String message, int lineNumber, String sourceID);
    }



    //插入html
    public void insertHtml(String html) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertHTML('" + html + "');");
    }

    //插入换行
    public void setNewLine() {
        isNeedSetNewLineAfter = false;
        isUnableTextChange = true;
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertHTML('<br></br>');");
    }

    public void setHint(String placeholder) {
        setPlaceholder(placeholder);
    }

    public void setHintColor(String placeholderColor) {
        exec("javascript:RE.setPlaceholderColor('" + placeholderColor + "');");
    }

    public void setNeedSetNewLineAfter(boolean needSetNewLineAfter) {
        isNeedSetNewLineAfter = needSetNewLineAfter;
    }

    public boolean isNeedSetNewLineAfter() {
        return isNeedSetNewLineAfter;
    }

    //插入文件下载链接
    public void insertFileWithDown(String downloadUrl, String title) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }

        String fileName;
        try {
            String[] split = downloadUrl.split("/");
            fileName = split[split.length - 1];
        } catch (Exception e) {
            fileName = "rich" + System.currentTimeMillis();
        }

        title += fileName;
        insertHtml("<a href=\"" + downloadUrl + "\" download=\"" + fileName + "\">" + title + "</a><br><br>");
    }


    //插入图片
    public void insertImage(String url, String alt, String style) {
        alt = "picvision";
        style = "margin-top:10px;width:100%";

        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertImage('" + url + "', '" + alt + "', '" + style + "');");
    }

    //插入音频
    public void insertAudio(String audioUrl, String style) {
        //controls进度控制
        style = "margin-top:10px;width:100%";

        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertAudio('" + audioUrl + "', '" + style + "');");
    }

    //插入视频
    public void insertVideo(String videoUrl, String style, String posterUrl,Context context) {
        //增加进度控制视频显示第一帧
        style = "margin-top:10px;width:100%;background:#000";

        if (TextUtils.isEmpty(posterUrl)) {
            //根据视频地址截取视频的缩略图
            Bitmap videoBitmap = FilesUtils.getVideoBitmap(videoUrl);
            if (videoBitmap != null) {
                //保存视频第一帧的url地址
                String videoBitmapUrl = FilesUtils.saveBitmap(context,videoBitmap);
                if (!TextUtils.isEmpty(videoBitmapUrl)) {
                    posterUrl = videoBitmapUrl;
                }
            }

        }

        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertVideo('" + videoUrl + "', '" + style + "', '" + posterUrl + "');");
    }


    // 获取html本地的地址 方便上传的时候转为在线的地址
    public List<String> getAllSrcAndHref() {
        return Utils.getHtmlSrcOrHrefList(getHtml());
    }

    public void clearLocalRichEditorCache() {
        FilesUtils.clearLocalRichEditorCache();
    }

}
