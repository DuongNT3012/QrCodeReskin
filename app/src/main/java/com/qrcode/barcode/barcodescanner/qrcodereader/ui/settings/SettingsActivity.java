package com.qrcode.barcode.barcodescanner.qrcodereader.ui.settings;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.qrcode.barcode.barcodescanner.qrcodereader.VAppUtility;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.constant.PreferenceKey;
import com.qrcode.barcode.barcodescanner.qrcodereader.helpers.util.SharedPrefUtil;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.about_us.AboutUsActivity;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.feedback.FeedbackActivity;
import com.qrcode.barcode.barcodescanner.qrcodereader.ui.privacy_policy.PrivayPolicyActivity;

import com.qrcode.barcode.barcodescanner.qrcodereader.R;
import com.qrcode.barcode.barcodescanner.qrcodereader.databinding.ActivitySettingsBinding;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private ActivitySettingsBinding mBinding;
    //ads
    NativeAd nativeAd;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        //firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //

        initializeToolbar();
        loadSettings();
        setListeners();
        setBack();

        //add ads
        addAdsBanner();
        showNativeAd();
        //
    }

    //add ads banner
    private void addAdsBanner(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLoaded() {

                mBinding.adView.setOnPaidEventListener(new OnPaidEventListener() {
                    @Override
                    public void onPaidEvent(@NonNull AdValue adValue) {
                        VAppUtility.logAdAdmobValue(adValue,
                                mBinding.adView.getAdUnitId(),
                                mBinding.adView.getResponseInfo().getMediationAdapterClassName(),
                                mFirebaseAnalytics);
                    }
                });
            }

            @Override
            public void onAdClicked() {

            }
        });

        mBinding.adView.loadAd(adRequest);

    }
    //

    //ads native
    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));

        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));


        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);

        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);


    }
    private void showNativeAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_native_settings));
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = findViewById(R.id.fl_native);
                NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_ads, null);

                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);

            }
        }).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }
    //end


    private void loadSettings() {
        mBinding.switchCompatPlaySound.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.PLAY_SOUND));
        mBinding.switchCompatVibrate.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.VIBRATE));
        mBinding.switchCompatSaveHistory.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.SAVE_HISTORY));
        mBinding.switchCompatCopyToClipboard.setChecked(SharedPrefUtil.readBooleanDefaultTrue(PreferenceKey.COPY_TO_CLIPBOARD));
    }

    private void setListeners() {
        mBinding.switchCompatPlaySound.setOnCheckedChangeListener(this);
        mBinding.switchCompatVibrate.setOnCheckedChangeListener(this);
        mBinding.switchCompatSaveHistory.setOnCheckedChangeListener(this);
        mBinding.switchCompatCopyToClipboard.setOnCheckedChangeListener(this);

        mBinding.textViewPlaySound.setOnClickListener(this);
        mBinding.textViewVibrate.setOnClickListener(this);
        mBinding.textViewSaveHistory.setOnClickListener(this);
        mBinding.textViewCopyToClipboard.setOnClickListener(this);
    }

    private void initializeToolbar() {
        setSupportActionBar(mBinding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_compat_play_sound:
                SharedPrefUtil.write(PreferenceKey.PLAY_SOUND, isChecked);
                break;

            case R.id.switch_compat_vibrate:
                SharedPrefUtil.write(PreferenceKey.VIBRATE, isChecked);
                break;

            case R.id.switch_compat_save_history:
                SharedPrefUtil.write(PreferenceKey.SAVE_HISTORY, isChecked);
                break;

            case R.id.switch_compat_copy_to_clipboard:
                SharedPrefUtil.write(PreferenceKey.COPY_TO_CLIPBOARD, isChecked);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_play_sound:
                mBinding.switchCompatPlaySound.setChecked(!mBinding.switchCompatPlaySound.isChecked());
                break;

            case R.id.text_view_vibrate:
                mBinding.switchCompatVibrate.setChecked(!mBinding.switchCompatVibrate.isChecked());
                break;

            case R.id.text_view_save_history:
                mBinding.switchCompatSaveHistory.setChecked(!mBinding.switchCompatSaveHistory.isChecked());
                break;

            case R.id.text_view_copy_to_clipboard:
                mBinding.switchCompatCopyToClipboard.setChecked(!mBinding.switchCompatCopyToClipboard.isChecked());
                break;

            default:
                break;
        }
    }

    public void startAboutUsActivity(View view) {

        startActivity(new Intent(this, AboutUsActivity.class));
    }

    public void startPrivacyPolicyActivity(View view) {
//        startActivity(new Intent(this, PrivayPolicyActivity.class));
        catchPrivacyPolicy();
    }

    public void startFeedbackActivity(View view){
//        startActivity(new Intent(this, FeedbackActivity.class));
        showDialog();
    }

    private void catchPrivacyPolicy(){
        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/qr-code-s2.appspot.com/o/Privacy-Policy-QRcode.html?alt=media&token=5de50727-c6bd-4235-bce2-249fc2d66a3d");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.feedback_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText edt_title = (EditText) dialog.findViewById(R.id.edt_title);
        EditText edt_content = (EditText) dialog.findViewById(R.id.edt_content);
        Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
        Button btn_no = (Button) dialog.findViewById(R.id.btn_no);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriText = "mailto:" + mail() +
                        "?subject=" + "feedback Qr code" +
                        "&body=" + "Title : " + edt_title.getText() + "\nContent: " + edt_content.getText();
                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                try {
                    startActivity(Intent.createChooser(sendIntent, "Send Email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SettingsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private String mail(){
        String mailTest = "hoanganh710pt@gmail.com";
        String mailThat = "anhnt.android@gmail.com";
        return mailThat;
    }

    private void setBack(){
        mBinding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
