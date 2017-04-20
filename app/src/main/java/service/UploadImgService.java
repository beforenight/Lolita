package service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * File Name UploadImgService
 * Created by Administrator
 * Created date on 2017/4/14 18:31.
 */

public class UploadImgService extends IntentService
{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadImgService(final String name)
    {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent)
    {

    }
}
