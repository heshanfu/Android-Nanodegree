package fyi.jackson.activejournal;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import fyi.jackson.activejournal.data.AppViewModel;
import fyi.jackson.activejournal.data.entities.Activity;
import fyi.jackson.activejournal.data.entities.Content;
import fyi.jackson.activejournal.data.entities.Position;
import fyi.jackson.activejournal.dummy.Data;
import fyi.jackson.activejournal.dummy.Point;
import fyi.jackson.activejournal.fragment.ActivityListFragment;
import fyi.jackson.activejournal.fragment.ImportActivityFragment;
import fyi.jackson.activejournal.fragment.RecordingFragment;
import fyi.jackson.activejournal.worker.ThumbnailWorker;

public class ActivityMain extends AppCompatActivity {

    public static final String TAG = ActivityMain.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 9468;

    AppViewModel viewModel;

    Snackbar snackbar;
    FrameLayout bottomFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomFrame = findViewById(R.id.frame_bottom_layer);

        fragmentTransaction(R.id.frame_top_layer, RecordingFragment.newInstance());
        fragmentTransaction(R.id.frame_bottom_layer, ActivityListFragment.newInstance());

        viewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        viewModel.getActivities().observe(this, new Observer<List<Activity>>() {
            @Override
            public void onChanged(@Nullable List<Activity> activities) {
                updateThumbnails(activities);
            }
        });

        checkForPermissions();

    }

    private void checkForPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        boolean permissionGranted = true;

        for (int i = 0; i < permissions.length; i++) {
            permissionGranted &= ContextCompat.checkSelfPermission(this,
                    permissions[i]) == PackageManager.PERMISSION_GRANTED;
        }


        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this,
                    permissions, PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    private void updateThumbnails(List<Activity> activities) {
        for (Activity activity : activities) {
            if (activity.getThumbnail() == null) {
                androidx.work.Data activityData = new androidx.work.Data.Builder()
                        .putLong(ThumbnailWorker.KEY_ACTIVITY_ID, activity.getActivityId())
                        .build();

                OneTimeWorkRequest thumbnailRequest =
                        new OneTimeWorkRequest.Builder(ThumbnailWorker.class)
                                .setInputData(activityData)
                                .build();

                WorkManager.getInstance().enqueue(thumbnailRequest);

                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                performFileSearch();
//                fragmentTransaction(R.id.frame_bottom_layer, ImportActivityFragment.newInstance());
                return true;
            case R.id.action_settings:
                bulkInsert();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fragmentTransaction(int parentId, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(parentId, fragment)
                .commit();
    }

    private void updateSnackbar(Integer value) {
        if (snackbar == null) {
            snackbar = Snackbar.make(
                    bottomFrame,
                    "Beep boop",
                    Snackbar.LENGTH_SHORT);
        }
        snackbar.setText(value + " positions in database.");
        snackbar.show();
    }

    private void bulkInsert() {
        List<Point> points = Data.getSpinnakerSailing();
        List<Point> points1 = Data.getClingmansDome();
        List<Position> positions = new ArrayList<>();
        List<Position> positions1 = new ArrayList<>();

        long activityId = System.currentTimeMillis();
        long activityId1 = activityId + 10;

        for (Point p : points) {
            Position pos = new Position();
            pos.setActivityId(activityId);
            pos.setLegId(1);
            pos.setLat(p.lat);
            pos.setLng(p.lng);
            pos.setTs(p.ts);
            pos.setAcc(p.acc);
            pos.setAlt(p.alt);
            pos.setVacc(p.vAcc);
            positions.add(pos);
        }
        for (Point p : points1) {
            Position pos = new Position();
            pos.setActivityId(activityId1);
            pos.setLegId(1);
            pos.setLat(p.lat);
            pos.setLng(p.lng);
            pos.setTs(p.ts);
            pos.setAcc(p.acc);
            pos.setAlt(p.alt);
            pos.setVacc(p.vAcc);
            positions1.add(pos);
        }
        viewModel.insertPositionsList(positions);
        viewModel.insertPositionsList(positions1);


        Activity activity = new Activity();
        activity.setActivityId(activityId);
        activity.setName("Flying the Spinnaker Solo");
        activity.setType(Activity.TYPE_SAILING);
        Activity activity1 = new Activity();
        activity1.setActivityId(activityId1);
        activity1.setName("Hiking to Clingman's Dome");
        activity1.setType(Activity.TYPE_HIKING);

        viewModel.insertActivities(activity, activity1);


        String longText = "This is a very long string that explains my recent activity in great detail. It was so great. I had so much fun. Here's some Lorem Ipsum: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas rutrum tincidunt quam, eu hendrerit mauris blandit vitae. Donec eu laoreet nulla. Cras facilisis tempor eros vel eleifend. Curabitur eros mi, volutpat eget urna in, efficitur tempus leo. Duis non efficitur felis, id posuere magna. Nullam ultrices nisi ac nisi venenatis laoreet. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In hac habitasse platea dictumst. Morbi accumsan volutpat dui eget consectetur.";
        Content textContent = new Content();
        textContent.setType(Content.TYPE_TEXT);
        textContent.setPosition(0);
        textContent.setActivityId(activityId);
        textContent.setValue(longText);

        String shortText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas rutrum tincidunt quam, eu hendrerit mauris blandit vitae. Donec eu laoreet nulla. Cras facilisis tempor eros vel eleifend.";
        Content textContent2 = new Content();
        textContent2.setType(Content.TYPE_TEXT);
        textContent2.setPosition(2);
        textContent2.setActivityId(activityId);
        textContent2.setValue(shortText);

//        String imageUri = "/storage/self/primary/DCIM/Camera/IMG_20170715_205901021.jpg";
//        Content imageContent = new Content();
//        imageContent.setType(Content.TYPE_IMAGE);
//        imageContent.setPosition(1);
//        imageContent.setActivityId(activityId);
//        imageContent.setValue(imageUri);

        viewModel.insertContents(textContent, textContent2);
    }

    private static final int READ_REQUEST_CODE = 42;

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            String uri = null;
            if (resultData != null) {
                uri = resultData.getData().getPath();
                Log.i(TAG, "Uri: " + uri);
            }
        }
    }
}
