package com.exsample.android.newsapp;

public class Art {

    private String mCategory;
    private String mTitle;
    private String mRealiseDate;
    private String mUrl;
    private String mAuthor;

    /**
     * Constructs a new {@link Art} object.
     *
     * @param category    is the article category (Art and design, Education, ... )
     * @param title       is the name of article
     * @param realiseDate is the realise date of article
     * @param url         is the website URL to find more details
     * @param author      is the article author name
     */
    public Art(String category, String title, String realiseDate, String url, String author) {
        mCategory = category;
        mTitle = title;
        mRealiseDate = realiseDate;
        mUrl = url;
        mAuthor = author;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getRealiseDate() {
        return mRealiseDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }
}