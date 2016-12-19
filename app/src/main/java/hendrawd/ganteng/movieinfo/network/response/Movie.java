package hendrawd.ganteng.movieinfo.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author hendrawd on 11/17/16
 */

public class Movie implements Parcelable {

    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("original_title")
    private String original_title;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String release_date;

    @SerializedName("backdrop_path")
    private String backdrop_path;

    @SerializedName("vote_average")
    private float vote_average;

    @SerializedName("id")
    private String id;

    @SerializedName("genre_ids")
    private int[] genre_ids;

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int[] getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(int[] genre_ids) {
        this.genre_ids = genre_ids;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.poster_path);
        dest.writeString(this.original_title);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
        dest.writeString(this.backdrop_path);
        dest.writeFloat(this.vote_average);
        dest.writeString(this.id);
        dest.writeIntArray(this.genre_ids);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.poster_path = in.readString();
        this.original_title = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.backdrop_path = in.readString();
        this.vote_average = in.readFloat();
        this.id = in.readString();
        this.genre_ids = in.createIntArray();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
