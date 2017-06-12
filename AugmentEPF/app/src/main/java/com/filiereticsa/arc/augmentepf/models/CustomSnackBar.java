package com.filiereticsa.arc.augmentepf.models;

import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.filiereticsa.arc.augmentepf.R;

/**
 * Created by Cécile on 09/06/2017.
 */

public class CustomSnackBar extends BaseTransientBottomBar<CustomSnackBar> {

    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent              The parent for this transient bottom bar.
     * @param content             The content view for this transient bottom bar.
     * @param contentViewCallback The content view callback for this transient bottom bar.
     */
    protected CustomSnackBar(@NonNull ViewGroup parent, @NonNull View content, @NonNull BaseTransientBottomBar.ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
    }

    public static CustomSnackBar make(ViewGroup parent, int duration) {
        // inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.snackbar_guidance, parent, false);

        // create with custom view
        CustomSnackBar.ContentViewCallback callback= new CustomSnackBar.ContentViewCallback(view);
        CustomSnackBar customSnackBar = new CustomSnackBar(parent, view, callback);

        customSnackBar.setDuration(duration);

        customSnackBar.getView().setBackgroundResource(R.color.colorPrimary);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        customSnackBar.getView().setLayoutParams(params);

        return customSnackBar;
    }

    public void setText(String text){
        TextView guideText = (TextView) getView().findViewById(R.id.guidance_text);
        guideText.setText(text);
    }

    private static class ContentViewCallback
            implements BaseTransientBottomBar.ContentViewCallback {

        // view inflated from custom layout
        private View view;

        public ContentViewCallback(View view) {
            this.view = view;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            // TODO: handle enter animation
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            // TODO: handle exit animation
        }
    }

}
