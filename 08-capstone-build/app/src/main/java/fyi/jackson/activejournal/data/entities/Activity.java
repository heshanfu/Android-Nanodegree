package fyi.jackson.activejournal.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

import fyi.jackson.activejournal.R;

@Entity(tableName = "activities")
public class Activity implements Parcelable {

    public static final int TYPE_WALKING = 349;
    public static final int TYPE_RUNNING = 319;
    public static final int TYPE_HIKING = 439;
    public static final int TYPE_BIKING = 723;
    public static final int TYPE_SAILING = 127;
    public static final int TYPE_BOATING = 859;
    public static final int TYPE_DRIVING = 740;
    public static final int TYPE_OTHER = 22;

    public Activity() {}

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "activityId")
    private long activityId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private int type = TYPE_OTHER;

    @ColumnInfo(name = "thumbnail")
    private String thumbnail;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getTypeResId() {
        switch (type) {
            case TYPE_WALKING:
                return R.drawable.ic_directions_walk_black_24dp;
            case TYPE_RUNNING:
                return R.drawable.ic_directions_run_black_24dp;
            case TYPE_HIKING:
                return R.drawable.ic_directions_hike_black_24dp;
            case TYPE_SAILING:
                return R.drawable.ic_directions_sail_black_24dp;
            case TYPE_BOATING:
                return R.drawable.ic_directions_boat_black_24dp;
            case TYPE_BIKING:
                return R.drawable.ic_directions_bike_black_24dp;
            case TYPE_DRIVING:
                return R.drawable.ic_drive_eta_black_24dp;
            default:
                return R.drawable.ic_location_on_black_24dp;
        }
    }

    public static int getRandomTypeResId() {
        int[] types = {
                R.drawable.ic_directions_walk_black_24dp,
                R.drawable.ic_directions_run_black_24dp,
                R.drawable.ic_directions_hike_black_24dp,
                R.drawable.ic_directions_sail_black_24dp,
                R.drawable.ic_directions_boat_black_24dp,
                R.drawable.ic_directions_bike_black_24dp,
                R.drawable.ic_drive_eta_black_24dp,
                R.drawable.ic_location_on_black_24dp
        };
        Random random = new Random();
        int randomPos = random.nextInt(types.length);
        return types[randomPos];
    }

    public Activity(Parcel in) {
        setUid(in.readInt());
        setActivityId(in.readLong());
        setName(in.readString());
        setType(in.readInt());
        setThumbnail(in.readString());
    }

    public static final Parcelable.Creator<Activity> CREATOR = new Parcelable.Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel source) {
            return new Activity(source);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeLong(activityId);
        parcel.writeString(name);
        parcel.writeInt(type);
        parcel.writeString(thumbnail);
    }
}
