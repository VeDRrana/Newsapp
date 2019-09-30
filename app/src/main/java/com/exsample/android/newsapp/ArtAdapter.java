package com.exsample.android.newsapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Define the ArtAdapter as extending from the ArrayAdapter super class and overriding the
 * getView method to control how the list item views get created.
 * These list item layouts will be provided to an adapter view like ListView to be displayed
 * to the user.
 */
public class ArtAdapter extends ArrayAdapter<Art> {

    /**
     * Create a new ArtAdapter object
     *
     * @param context of the app
     * @param arts    is the list of arts news, which is the data source of the adapter
     */
    public ArtAdapter(Context context, List<Art> arts) {
        super(context, 0, arts);
    }

    /**
     * Returns a list item view that displays information about the art news at the given position
     * in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list__item,
                    parent, false);
        }

        Art currentArt = getItem(position);

        // Binding the data from the movie object to the views in the movie list item layout

        TextView categoryView = listItemView.findViewById(R.id.category_text_view);
        categoryView.setText(currentArt.getCategory());

        // Set the proper background color on the category rectangle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable categoryRectangle = (GradientDrawable) categoryView.getBackground();
        // Get the appropriate background color based on the art news category
        String categoryColor = getCategoryColor(currentArt.getCategory());
        // Set the color on the rating circle
        categoryRectangle.setColor(Integer.parseInt(categoryColor));

        // Get the original realise date string from the Art object
        String originalDate = currentArt.getRealiseDate();
        // --- replace char T with tab and deleted char Z
        String newDate;
        //create local String constants to store replacement values for date string
        //which is in the format of "2018-05-09T16:49:09Z"
        String t = "T", z = "Z", t_replace = "\t", z_replace = "";
        newDate = originalDate.replace(t, t_replace).replace(z, z_replace);
        TextView dateView = listItemView.findViewById(R.id.date_text_view);
        dateView.setText(newDate);

        TextView titleView = listItemView.findViewById(R.id.title_text_view);
        titleView.setText(currentArt.getTitle());

        TextView authorView = listItemView.findViewById(R.id.author_text_view);
        authorView.setText(currentArt.getAuthor());

        return listItemView;
    }

    /**
     * Return the color for the art news category
     *
     * @param category of the art news
     */
    private String getCategoryColor(String category) {
        int ratingColorResourceId;
        switch (category) {
            case "Culture":
                ratingColorResourceId = R.color.color1;
                break;
            case "Art and design":
                ratingColorResourceId = R.color.color2;
                break;
            case "Film":
                ratingColorResourceId = R.color.color3;
                break;
            case "Music":
                ratingColorResourceId = R.color.color4;
                break;
            case "Education":
                ratingColorResourceId = R.color.color5;
                break;
            case "Television & radio":
                ratingColorResourceId = R.color.color6;
                break;
            case "Cities":
                ratingColorResourceId = R.color.color7;
                break;
            case "Life and style":
                ratingColorResourceId = R.color.color8;
                break;
            case "Opinion":
                ratingColorResourceId = R.color.color9;
                break;
            case "Global":
                ratingColorResourceId = R.color.color10;
                break;
            default:
                // Executed if all cases are not matched
                ratingColorResourceId = R.color.defaultColor;
                break;
        }
        return String.valueOf(ContextCompat.getColor(getContext(), ratingColorResourceId));
    }
}