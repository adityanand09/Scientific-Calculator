package com.busi.adi.calc;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

public class expBuilder extends SpannableStringBuilder {

    private final expTokenizer mTokenizer;
    private boolean mIsEdited;

    public expBuilder(
            CharSequence text, expTokenizer tokenizer, boolean isEdited) {
        super(text);

        mTokenizer = tokenizer;
        mIsEdited = isEdited;
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart,
            int tbend) {
        if (start != length() || end != length()) {
            mIsEdited = true;
            return super.replace(start, end, tb, tbstart, tbend);
        }

        String appendExpr = tb.subSequence(tbstart, tbend).toString();
        if (appendExpr.length() == 1) {
            final String expr = mTokenizer.getLocalizedExpression(toString());
            switch (appendExpr.charAt(0)) {
                case '.':
                    // don't allow two decimals in the same number
                    final int index = expr.lastIndexOf('.');
                    if (index != -1 && TextUtils.isDigitsOnly(expr.substring(index + 1, start))) {
                        appendExpr = "";
                    }
                    break;
                case '+':
                case '÷':
                case '×':
                    // don't allow leading operator
                    if (start == 0) {
                        appendExpr = "";
                        break;
                    }

                    // don't allow multiple successive operators
                    while (start > 0 && "+-×÷".indexOf(expr.charAt(start - 1)) != -1) {
                        --start;
                    }
                    // fall through
                case '-':
                    // don't allow -- or +-
                    if (start > 0 && "+-".indexOf(expr.charAt(start - 1)) != -1) {
                        --start;
                    }

                    mIsEdited = true;
                    break;
                default:
                    break;
            }
        }

        // since this is the first edit replace the entire string
        if (!mIsEdited && appendExpr.length() > 0) {
            start = 0;
            mIsEdited = true;
        }

        appendExpr = mTokenizer.getLocalizedExpression(appendExpr);
        return super.replace(start, end, appendExpr, 0, appendExpr.length());
    }
}
