# MovieInfo
This is the open source version of my final project for Intermediate class of Indonesia Android Kejar batch 2

Created with Android Studio version 2.2.3

This project use API from http://themoviedb.org

If you want to clone it and make it works, please provide your api key from http://themoviedb.org in hendrawd.ganteng.movieinfo.network.UrlComposer.API_KEY

## Main Feature:
* Material design.
* Support minimal android version 15(Ice Cream Sandwich).
* Support portrait and landscape orientation.
* Responsive design for phones and tablets.
* 3 main categories: Popular, Top Rated, and Upcoming.
* Search movie.
* Add to favorite and show favorited movies.
* Detail screen that has field: Original title, Release date, Genre, Rating, Overview with TextToSpeech capability, Related videos, and Latest reviews. You can also share trailer and detail via other applications.

## Libraries Used:
### Design and compatibility
* com.android.support:design:25.0.1
* com.android.support:appcompat-v7:25.0.1
* com.android.support:cardview-v7:25.0.1
* com.android.support:recyclerview-v7:25.0.1
* com.android.support:support-v4:25.0.1
* me.zhanghai.android.materialratingbar:library:1.0.2

### Network
* com.android.volley:volley:1.0.0
* com.squareup.okhttp3:okhttp:3.4.2
OkHttp here to boost the performance of volley

### JSON Serializer/Deserializer
* com.google.code.gson:gson:2.8.0

### View Binder
* com.jakewharton:butterknife:8.4.0
* com.jakewharton:butterknife-compiler:8.4.0

### Network Image Loader
* com.github.bumptech.glide:glide:3.7.0

### ORM Database
* org.greenrobot:greendao:2.2.0

### Others
* YouTubeAndroidPlayerApi
