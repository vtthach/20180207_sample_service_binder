package cbsa.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

import timber.log.Timber;

/**
 * Created by nguyenminhhai on 22/9/15.
 */
public class UIUtil {
    /**
     * showDialog keyboard
     *
     * @param context
     * @param v
     */
    public static void showKeyboard(Context context, View v) {
        if (v == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    /**
     * hideDialog keyboard
     *
     * @param context
     * @param v
     */
    public static void hideKeyboard(Context context, View v) {
        if (v == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * hideDialog keyboard
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getWindow()
                            .getDecorView().getWindowToken(), 0);
        } catch (Exception e) { // NOSONAR
            Timber.e("Exception: " + e.getMessage(), e);// NOSONAR
        }
    }

    /**
     * check the fragment is visible or not
     *
     * @param f fragment object to check visibility
     * @return true if the checked fragment is visible, false otherwise
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isVisibleFragment(Fragment f) {
        if (f.getParentFragment() != null && f.getParentFragment().isVisible() && f.isVisible()) {
            // current fragment has parent fragment
            return true;
        } else if (f.getParentFragment() == null && f.isVisible()) {
            // current fragment is root fragment
            return true;
        } else if (f.getUserVisibleHint()) {
            return true;
        }
        return false;
    }

    /**
     * @param rootView
     * @return
     */
    public static boolean isKeyboardShown(View rootView) {
            /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
            /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
            /* Threshold size: dp to pixels, multiply with display density */
        return heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;
    }


    /**
     * get captureShopperCardView's location on screen
     *
     * @param v captureShopperCardView to observer
     * @return location[2] with the following position: left, top
     */
    public static int[] getLocationOnScreen(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }

    /**
     * get captureShopperCardView's location in window
     *
     * @param v captureShopperCardView to observer
     * @return location[2] with the following position: left, top
     */
    public static int[] getLocationInWindow(View v) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        return location;
    }

    /**
     * get captureShopperCardView's Rectangle position
     *
     * @param v captureShopperCardView to observer
     * @return location[4] with the following position: left, top, right, bottom
     */
    public static int[] getLocalVisibleRect(View v) {
        int[] location = new int[4];
        Rect rect = new Rect();
        v.getLocalVisibleRect(rect);
        location[0] = rect.left;
        location[1] = rect.top;
        location[2] = rect.right;
        location[3] = rect.bottom;
        return location;
    }

    public static EditText hasFocused(EditText[] list) {
        for (EditText e : list) {
            if (e != null && e.hasFocus()) {
                return e;
            }
        }
        return null;
    }

    /**
     * get resource's drawable
     *
     * @param context
     * @param resId
     * @return
     */
    public static Drawable getDrawable(Context context, int resId) {
        return ResourcesCompat.getDrawable(context.getResources(), resId, null);
    }

    /**
     * get resource's drawable
     *
     * @param context
     * @param resId
     * @param theme
     * @return
     */
    public static Drawable getDrawable(Context context, int resId, Resources.Theme theme) {
        return ResourcesCompat.getDrawable(context.getResources(), resId, theme);
    }

    /**
     * get color drawable
     *
     * @param context
     * @param drawableResId
     * @return
     */
    public static ColorStateList getColorDrawable(Context context, Integer drawableResId) {
        ColorStateList csl = null;
        try {
            csl = ColorStateList.createFromXml(context.getResources(), context.getResources().getXml(drawableResId));

        } catch (Exception e) { // NOSONAR
            Timber.e("Exception: " + e.getMessage(), e);// NOSONAR
        }
        return csl;
    }

    /**
     * @param editTexts
     */
    public static void clearFocus(EditText[] editTexts) {
        for (EditText edt : editTexts) {
            if (edt.isFocused()) {
                edt.clearFocus();
                edt.requestFocus();
            }
        }
    }

    /**
     * @param list
     */
    public static void requestFocus(EditText[] list) {
        for (EditText e : list) {
            if (e.hasFocus()) {
                e.requestFocus();
            }
        }
    }

    /**
     * set spanned string to textview object
     *
     * @param tv:             textview object
     * @param message:        content of the popup - Must be in SPANNED STRING format(not HTML format).
     * @param spannedMessage: clickable string of the content
     * @param isUnderline     : undeline message
     * @param isMessageBold:  is message bold
     * @param linkColorValue: color code for linked message
     * @param clickableSpan:  linkedMessage listener
     */
    public static void setSpannedMessageToView(TextView tv,
                                               String message,
                                               String spannedMessage,
                                               boolean isUnderline,
                                               boolean isMessageBold,
                                               int linkColorValue,
                                               ClickableSpan clickableSpan) {
        if (tv != null) {
            // set spannable for text captureShopperCardView
            int startIndex = message.indexOf("%s");
            int endIndex = startIndex + spannedMessage.length();
            message = String.format(message, spannedMessage);
            Spannable span = Spannable.Factory.getInstance().newSpannable(message);

            // set span color
            span.setSpan(new ForegroundColorSpan(linkColorValue), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // set span underline
            if (isUnderline)
                span.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // set bold message
            if (isMessageBold)
                span.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            span.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            tv.setText(span);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static void unbindDrawables(Context ctx, View view) {
        if (view == null) {
            return;
        }
        if (view.getBackground() != null) {
            if (view.getId() != View.NO_ID)
                System.out.println(ctx.getResources().getResourceEntryName(
                        view.getId()));
            view.getBackground().setCallback(null);
            view.setBackgroundResource(0);
        }

        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                unbindDrawables(ctx, viewGroup.getChildAt(i));

            if (!(view instanceof AdapterView))
                viewGroup.removeAllViews();
        }
    }

    /**
     * Get screen size of device
     *
     * @param context
     * @return screenSize[0] is widthPixels ;  screenSize[1] is heightPixels
     */
    public static int[] getScreenSize(@NonNull Context context) {
        int[] screenSize = new int[2];
        screenSize[0] = context.getResources().getDisplayMetrics().widthPixels;
        screenSize[1] = context.getResources().getDisplayMetrics().heightPixels;
        return screenSize;
    }


    public static int getDistanceFromTwoPoint(Point p1, Point p2) {
        return (int) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public static float getPixelFromDP(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static void safeSetBackgroundColor(View view, int color) {
        if (view.getBackground() instanceof GradientDrawable) {
            GradientDrawable background = (GradientDrawable) view.getBackground();
            background.setColor(color);
        } else {
            view.setBackgroundColor(color);
        }
    }

    public static int getCenterX(@NonNull View view) {
        int[] locations = getLocationOnScreen(view);
        return locations[0] + (view.getWidth() / 2);
    }

    public static String getWordSpacing(String str, int wordSpacingMultiply) {
        for (int i = 0; i < wordSpacingMultiply; i++) {
            str = str.replace(" ", "  ");
        }
        return str;
    }
}
