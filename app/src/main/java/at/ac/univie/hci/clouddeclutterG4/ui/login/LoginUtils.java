package at.ac.univie.hci.clouddeclutterG4.ui.login;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

public class LoginUtils {
    @NonNull
    public static Spannable getSpannable(String fullText, String linkText, Consumer<View> rdFunc) {
        Spannable spannable = new SpannableString(fullText);

        int start = fullText.indexOf(linkText);
        int end = start + linkText.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                rdFunc.accept(widget);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        };

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}
