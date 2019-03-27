package com.example.moviesnow.roomdb;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "movieinfo")
public class MovieInfo implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int _id;

    public String imageUrl;
    public String popularity;
    public String rating;
    public String name;
    public String originalTitle;
    public String overview;
    public String releaseDate;
    public String id;
    public String genere;
    public String status;
    public String backDrop;
    public String voteCount;
    public String tagLine;
    public String runtime;
    public String language;
    public String poster;
    public boolean isFavorite;
    public boolean ispopular;

    public MovieInfo() {

    }

    @NonNull
    public int get_id() {
        return _id;
    }

    public void set_id(@NonNull int _id) {
        this._id = _id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isIspopular() {
        return ispopular;
    }

    public void setIspopular(boolean ispopular) {
        this.ispopular = ispopular;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBackDrop() {
        return backDrop;
    }

    public void setBackDrop(String backDrop) {
        this.backDrop = backDrop;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageUrl);
        dest.writeString(popularity);
        dest.writeString(rating);
        dest.writeString(name);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(id);
        dest.writeString(genere);
        dest.writeString(status);
        dest.writeString(backDrop);
        dest.writeString(voteCount);
        dest.writeString(tagLine);
        dest.writeString(runtime);
        dest.writeString(language);
        dest.writeString(poster);
        dest.writeInt(isFavorite ? 1 : 0);
        dest.writeInt(ispopular ? 1 : 0);
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR
            = new Parcelable.Creator<MovieInfo>() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    private MovieInfo(Parcel in) {
        imageUrl = in.readString();
        popularity = in.readString();
        rating = in.readString();
        name = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        id = in.readString();
        genere = in.readString();
        status = in.readString();
        backDrop = in.readString();
        voteCount = in.readString();
        tagLine = in.readString();
        runtime = in.readString();
        language = in.readString();
        poster = in.readString();
        isFavorite = (in.readInt() == 1);
        ispopular = (in.readInt() == 1);
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "_id=" + _id +
                ", imageUrl='" + imageUrl + '\'' +
                ", popularity=" + popularity +
                ", rating=" + rating +
                ", name='" + name + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", id=" + id +
                ", isFavorite=" + isFavorite +
                ", ispopular=" + ispopular +
                '}';
    }
}
