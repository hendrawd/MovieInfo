package hendrawd.ganteng.movieinfo.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author hendrawd on 11/21/16
 */

public class GetGenreList {

    @SerializedName("genres")
    public List<Genre> genres;
}
