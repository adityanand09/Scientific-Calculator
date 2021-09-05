package com.busi.adi.calc;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class expTokenizer {

    private final Map<String, String> mReplacementMap;

    public expTokenizer(Context context) {
        mReplacementMap = new HashMap<>();

        mReplacementMap.put(".", context.getString(R.string.dec));

        mReplacementMap.put("0", context.getString(R.string.digit_0));
        mReplacementMap.put("1", context.getString(R.string.digit_1));
        mReplacementMap.put("2", context.getString(R.string.digit_2));
        mReplacementMap.put("3", context.getString(R.string.digit_3));
        mReplacementMap.put("4", context.getString(R.string.digit_4));
        mReplacementMap.put("5", context.getString(R.string.digit_5));
        mReplacementMap.put("6", context.getString(R.string.digit_6));
        mReplacementMap.put("7", context.getString(R.string.digit_7));
        mReplacementMap.put("8", context.getString(R.string.digit_8));
        mReplacementMap.put("9", context.getString(R.string.digit_9));

        mReplacementMap.put("/", context.getString(R.string.op_div));
        mReplacementMap.put("*", context.getString(R.string.op_mul));
        mReplacementMap.put("-", context.getString(R.string.op_sub));
        mReplacementMap.put("%", context.getString(R.string.op_per));

        mReplacementMap.put("cos", context.getString(R.string.fun_cos));
        mReplacementMap.put("ln", context.getString(R.string.fun_ln));
        mReplacementMap.put("log", context.getString(R.string.fun_log));
        mReplacementMap.put("sin", context.getString(R.string.fun_sin));
        mReplacementMap.put("tan", context.getString(R.string.fun_tan));
        mReplacementMap.put("asin", context.getString(R.string.fun_sin_inv));
        mReplacementMap.put("acos", context.getString(R.string.fun_cos_inv));
        mReplacementMap.put("atan", context.getString(R.string.fun_tan_inv));
        mReplacementMap.put("asinh", context.getString(R.string.fun_sinh_inv));
        mReplacementMap.put("acosh", context.getString(R.string.fun_cosh_inv));
        mReplacementMap.put("atanh", context.getString(R.string.fun_tanh_inv));

        mReplacementMap.put("Infinity", context.getString(R.string.inf));
    }

    public String getNormalizedExpression(String expr) {
        for (Entry<String, String> replacementEntry : mReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getValue(), replacementEntry.getKey());
        }
        return expr;
    }

    public String getLocalizedExpression(String expr) {
        for (Entry<String, String> replacementEntry : mReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getKey(), replacementEntry.getValue());
        }
        return expr;
    }
}
