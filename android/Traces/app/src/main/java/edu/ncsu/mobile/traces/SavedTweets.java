package edu.ncsu.mobile.traces;

public class SavedTweets {
    private long id;
    private String author;
    private String authorImageURL;
    private String tweetContent;
    private String tweetURL;

    public void setID(long _id) {
        this.id = _id;
    }

    public long getID() {
        return id;
    }

    public void setAuthor(String _author) {
        this.author = _author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthorImageURL(String _url) {
        authorImageURL = _url;
    }

    public String getAuthorImageURL () {
        return authorImageURL;
    }

    public void setTweetContent(String _content) {
        tweetContent = _content;
    }

    public String getTweetContent() {
        return tweetContent;
    }

    public void setTweetURL(String _url) {
        tweetURL = _url;
    }

    public String getTweetURL() {
        return tweetURL;
    }

    @Override
    public String toString() {
        return author + authorImageURL + tweetURL + tweetContent;
    }
}
