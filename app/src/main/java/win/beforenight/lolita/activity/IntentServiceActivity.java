package win.beforenight.lolita.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import win.beforenight.lolita.R;

public class IntentServiceActivity extends AppCompatActivity
{
    private TextView contentTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service);

        contentTextView = (TextView) findViewById(R.id.contentTextView);

        OkHttpClient mClient = new OkHttpClient();

        Request.Builder builder = new Request.Builder()
                .url("https://www.baidu.com/");
        builder.method("GET", null);

        final Request request = builder.build();

        Call newCall = mClient.newCall(request);

        newCall.enqueue(new Callback()
        {
            @Override
            public void onFailure(final Call call, final IOException e)
            {
                contentTextView.setText("请求失败!");
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException
            {

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        contentTextView.setText(response.body().toString());

                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

    }
}
