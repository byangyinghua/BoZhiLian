package cn.com.data_plus.bozhilian.thread;

import android.os.AsyncTask;
import android.text.Html;

import cn.com.data_plus.bozhilian.ui.MainActivity;
import cn.com.data_plus.bozhilian.ui.TextFragment;
import cn.com.data_plus.bozhilian.util.FileUtil;
import cn.com.data_plus.bozhilian.util.LogUtil;

public class ReadLogAsync extends AsyncTask<Void, Void, String> {
    private MainActivity mActivity;

    public ReadLogAsync(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mActivity.showFragment(TextFragment.newInstance("LOG加载中..."));
    }

    @Override
    protected String doInBackground(Void... params) {
        return FileUtil.readFile(FileUtil.getFile(FileUtil.SUB_DIR_LOGS, LogUtil.getLogFileName()));
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //noinspection deprecation
        mActivity.showFragment(TextFragment.newInstance(Html.fromHtml(s)));
    }
}