package com.sunsta.bear.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

import com.sunsta.bear.R;
import com.sunsta.bear.model.entity.Element;

public class PageView {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final View mView;
    private CharSequence mDescription;
    private int mImage = 0;
    private boolean mIsRTL = false;

    public PageView(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mView = mInflater.inflate(R.layout.item_aboutpage, null);
    }

    public PageView addEmail(String email) {
//        return addEmail(email, mContext.getString(R.string.));
        return addEmail(email, "about");
    }

    public PageView addEmail(String email, String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        emailElement.setIconTint(R.color.an_color_hint);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailElement.setIntent(intent);

        addItem(emailElement);
        return this;
    }

    public PageView addFacebook(String id) {
//        return addFacebook(id, mContext.getString(R.string.about_facebook));
        return addFacebook(id, "facebook");
    }

    public PageView addFacebook(String id, String title) {
        Element facebookElement = new Element();
        facebookElement.setTitle(title);
        facebookElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        facebookElement.setIconTint(R.color.an_color_hint);
        facebookElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

//        if (AboutPageUtils.isAppInstalled(mContext, "com.facebook.katana")) {
//            intent.setPackage("com.facebook.katana");
//            int versionCode = 0;
//            try {
//                versionCode = mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            if (versionCode >= 3002850) {
//                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + "http://m.facebook.com/" + id);
//                intent.setData(uri);
//            } else {
//                Uri uri = Uri.parse("fb://page/" + id);
//                intent.setData(uri);
//            }
//        } else {
//            intent.setData(Uri.parse("http://m.facebook.com/" + id));
//        }

        facebookElement.setIntent(intent);

        addItem(facebookElement);
        return this;
    }
    public PageView addTwitter(String id) {
        return addTwitter(id, "about_twitter");
    }

    public PageView addTwitter(String id, String title) {
        Element twitterElement = new Element();
        twitterElement.setTitle(title);
        twitterElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        twitterElement.setIconTint(R.color.an_color_hint);
        twitterElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

//        if (AboutPageUtils.isAppInstalled(mContext, "com.twitter.android")) {
//            intent.setPackage("com.twitter.android");
//            intent.setData(Uri.parse(String.format("twitter://user?screen_name=%s", id)));
//        } else {
//            intent.setData(Uri.parse(String.format("http://twitter.com/intent/user?screen_name=%s", id)));
//        }

        twitterElement.setIntent(intent);
        addItem(twitterElement);
        return this;
    }

    public PageView addPlayStore(String id) {
//        return addPlayStore(id, mContext.getString(R.string.about_play_store));
        return addPlayStore(id, "play_store");
    }

    public PageView addPlayStore(String id, String title) {
        Element playStoreElement = new Element();
        playStoreElement.setTitle(title);
        playStoreElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        playStoreElement.setIconTint(R.color.an_color_hint);
        playStoreElement.setValue(id);

        Uri uri = Uri.parse("market://details?id=" + id);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        playStoreElement.setIntent(goToMarket);

        addItem(playStoreElement);
        return this;
    }

    public PageView addYoutube(String id) {
        return addYoutube(id, "youtube");
    }

    public PageView addYoutube(String id, String title) {
        Element youtubeElement = new Element();
        youtubeElement.setTitle(title);
        youtubeElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        youtubeElement.setIconTint(R.color.an_color_hint);
        youtubeElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(String.format("http://youtube.com/channel/%s", id)));

//        if (AboutPageUtils.isAppInstalled(mContext, "com.google.android.youtube")) {
//            intent.setPackage("com.google.android.youtube");
//        }

        youtubeElement.setIntent(intent);
        addItem(youtubeElement);

        return this;
    }
    public PageView addInstagram(String id) {
        return addInstagram(id, "instagram");
    }

    public PageView addInstagram(String id, String title) {
        Element instagramElement = new Element();
        instagramElement.setTitle(title);
        instagramElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        instagramElement.setIconTint(R.color.an_color_hint);
        instagramElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://instagram.com/_u/" + id));

//        if (AboutPageUtils.isAppInstalled(mContext, "com.instagram.android")) {
//            intent.setPackage("com.instagram.android");
//        }

        instagramElement.setIntent(intent);
        addItem(instagramElement);

        return this;
    }

    public PageView addGitHub(String id) {
        return addGitHub(id, "about_github");
    }

    public PageView addGitHub(String id, String title) {
        Element gitHubElement = new Element();
        gitHubElement.setTitle(title);
        gitHubElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        gitHubElement.setIconTint(R.color.an_color_hint);
        gitHubElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(String.format("https://github.com/%s", id)));

        gitHubElement.setIntent(intent);
        addItem(gitHubElement);

        return this;
    }

    public PageView addWebsite(String url) {
        return addWebsite(url, "about_website");
    }

    public PageView addWebsite(String url, String title) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Element websiteElement = new Element();
        websiteElement.setTitle(title);
        websiteElement.setIconDrawable(R.mipmap.ic_color_copy_fav);
        websiteElement.setIconTint(R.color.an_color_hint);
        websiteElement.setValue(url);

        Uri uri = Uri.parse(url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);

        websiteElement.setIntent(browserIntent);
        addItem(websiteElement);

        return this;
    }

    public PageView addItem(Element element) {
        LinearLayout wrapper = (LinearLayout) mView.findViewById(R.id.about_providers);
        wrapper.addView(createItem(element));
        wrapper.addView(getSeparator(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.an_dimen_line)));
        return this;
    }

    public PageView setImage(@DrawableRes int resource) {
        this.mImage = resource;
        return this;
    }

    public PageView addGroup(String name) {

        TextView textView = new TextView(mContext);
        textView.setText(name);
        TextViewCompat.setTextAppearance(textView, R.style.an_auto_wrap_Text);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.an_dimen_padding_left);
        textView.setPadding(padding, padding, padding, padding);


        if (mIsRTL) {
            textView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        } else {
            textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        }
        textView.setLayoutParams(textParams);

        ((LinearLayout) mView.findViewById(R.id.about_providers)).addView(textView);
        return this;
    }

    public PageView isRTL(boolean value) {
        this.mIsRTL = value;
        return this;
    }

    public PageView setDescription(CharSequence description) {
        this.mDescription = description;
        return this;
    }

    public View create() {
        TextView description = mView.findViewById(R.id.description);
        ImageView image = mView.findViewById(R.id.image);
        if (mImage > 0) {
            image.setImageResource(mImage);
        }

        if (!TextUtils.isEmpty(mDescription)) {
            description.setText(mDescription);
        }

        description.setGravity(Gravity.CENTER);

        return mView;
    }

    private View createItem(final Element element) {
        LinearLayout wrapper = new LinearLayout(mContext);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setClickable(true);

        if (element.getOnClickListener() != null) {
            wrapper.setOnClickListener(element.getOnClickListener());
        } else if (element.getIntent() != null) {
            wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mContext.startActivity(element.getIntent());
                    } catch (Exception e) {
                    }
                }
            });

        }

        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.anBackground, outValue, true);
        wrapper.setBackgroundResource(outValue.resourceId);

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.an_dimen_padding_left);
        wrapper.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapper.setLayoutParams(wrapperParams);


        TextView textView = new TextView(mContext);
        TextViewCompat.setTextAppearance(textView, R.style.an_auto_wrap_Text);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textParams);

        ImageView iconView = null;

        if (element.getIconDrawable() != null) {
            iconView = new ImageView(mContext);
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.an_ivbar_height);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
            iconView.setLayoutParams(iconParams);
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.an_dimen_padding_left);
            iconView.setPadding(iconPadding, 0, iconPadding, 0);

            iconView.setImageResource(element.getIconDrawable());

            Drawable wrappedDrawable = DrawableCompat.wrap(iconView.getDrawable());
            wrappedDrawable = wrappedDrawable.mutate();
            if (element.getAutoApplyIconTint()) {
                int currentNightMode = mContext.getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
                    if (element.getIconTint() != null) {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, element.getIconTint()));
                    } else {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, R.color.an_color_hint));
                    }
                } else if (element.getIconNightTint() != null) {
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, element.getIconNightTint()));
                } else {
//                    DrawableCompat.setTint(wrappedDrawable, AboutPageUtils.getThemeAccentColor(mContext));2020zhish
                }
            }

        } else {
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.an_dimen_padding_left);
            textView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        }


        textView.setText(element.getTitle());


        if (mIsRTL) {

            final int gravity = element.getGravity() != null ? element.getGravity() : Gravity.END;

            wrapper.setGravity(gravity | Gravity.CENTER_VERTICAL);
            //noinspection ResourceType
            textParams.gravity = gravity | Gravity.CENTER_VERTICAL;
            wrapper.addView(textView);
            if (element.getIconDrawable() != null) {
                wrapper.addView(iconView);
            }

        } else {
            final int gravity = element.getGravity() != null ? element.getGravity() : Gravity.START;
            wrapper.setGravity(gravity | Gravity.CENTER_VERTICAL);
            textParams.gravity = gravity | Gravity.CENTER_VERTICAL;
            if (element.getIconDrawable() != null) {
                wrapper.addView(iconView);
            }
            wrapper.addView(textView);
        }

        return wrapper;
    }

    private View getSeparator() {
        return mInflater.inflate(R.layout.an_item_line, null);
    }
}
