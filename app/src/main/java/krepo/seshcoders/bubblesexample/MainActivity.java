package krepo.seshcoders.bubblesexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import krepo.seshcoders.messengerbubbles.BubbleLayout;
import krepo.seshcoders.messengerbubbles.BubblesManager;
import krepo.seshcoders.messengerbubbles.MessCloudView;

public class MainActivity extends AppCompatActivity {
    // TODO: 02.01.2020 gravity in the bubble badge when stick to wall
    // TODO: 02.01.2020 stick to wall with cloud view bubble stays in the middle
    // TODO: 02.01.2020 foreground service instead of this one


    //consts
    private static final int PERMISSIONS_REQUEST_CODE = 1231;
    private static final String TAG = "MainActivity";

    //views, android objects
    private BubbleLayout bubbleView;
    private BubblesManager bubblesManager;
    private Context mContext;
    private TextView messageBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        bubblesManager = new BubblesManager.Builder(mContext)
                .setTrashLayout(R.layout.component_bubble_trash_layout)
                .build();
        bubblesManager.initialize();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
        } else {
            initializeBubbles();
            new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bubbleView.displayMessage("witaaaaaaaaaaaaaaaaaaa mikola nie istnieje");

                        }
                    });
                }
            }, 8, TimeUnit.SECONDS);
        }
    }


    private void initializeBubbles() {
        bubbleView = (BubbleLayout) LayoutInflater
                .from(MainActivity.this).inflate(R.layout.component_bubble_layout, null, false);
        bubbleView.setShouldStickToWall(true);
        messageBadge = bubbleView.findViewById(R.id.badge);
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageBadge.getLayoutParams();

        bubbleView.setOnBubbleStickToWallListener(new BubbleLayout.OnBubbleStickToWallListener() {
            @Override
            public void onBubbleStickToWall(MessCloudView.BubbleCurrentWall wall) {
                Log.d(TAG, "onBubbleStickToWall: " + wall);
                if (wall == MessCloudView.BubbleCurrentWall.LEFT) {
                    //left wall, messageBadge layout grevity right
                    params.removeRule(RelativeLayout.ALIGN_START);
                    params.addRule(RelativeLayout.ALIGN_END, R.id.avatar);
                    messageBadge.setLayoutParams(params);
                } else {
                    //right wall, messageBadge layout grevity left
                    params.removeRule(RelativeLayout.ALIGN_END);
                    params.addRule(RelativeLayout.ALIGN_START, R.id.avatar);
                    messageBadge.setLayoutParams(params);
                }
            }
        });
        bubblesManager.addBubble(bubbleView, getScreenWidth(), getScreenHeight()/2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Toast.makeText(mContext, "permissions changed", Toast.LENGTH_SHORT).show();
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
