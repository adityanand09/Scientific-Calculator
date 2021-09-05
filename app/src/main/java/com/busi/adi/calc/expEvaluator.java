package com.busi.adi.calc;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;
import org.javia.arity.Util;

public class expEvaluator {

    private static final int MAX_DIGITS = 12;
    private static final int ROUNDING_DIGITS = 2;

    private final Symbols mSymbols;
    private final expTokenizer mTokenizer;

    public expEvaluator(expTokenizer tokenizer) {
        mSymbols = new Symbols();
        mTokenizer = tokenizer;
    }

    public void evaluate(CharSequence expr, EvaluateCallback callback) {
        evaluate(expr.toString(), callback);
    }

    public void evaluate(String expr, EvaluateCallback callback) {
        expr = mTokenizer.getNormalizedExpression(expr);

        // remove any trailing operators
        while (expr.length() > 0 && "+-/*".indexOf(expr.charAt(expr.length() - 1)) != -1) {
            expr = expr.substring(0, expr.length() - 1);
        }

        try {
            if (expr.length() == 0 || Double.valueOf(expr) != null) {
                callback.onEvaluate( null, Calculator.INVALID_RES_ID);
                return;
            }
        } catch (NumberFormatException e) {
        }

        try {
            double result = mSymbols.eval(expr);
            if (Double.isNaN(result)) {
                callback.onEvaluate( null, Calculator.INVALID_RES_ID);
            } else {
                final String resultString = mTokenizer.getLocalizedExpression(
                        Util.doubleToString(result, MAX_DIGITS, ROUNDING_DIGITS));
                callback.onEvaluate( resultString, Calculator.INVALID_RES_ID);
            }
        } catch (SyntaxException e) {
            callback.onEvaluate( null, R.string.error_syntax);
        }
    }

    public interface EvaluateCallback {
        public void onEvaluate(String result, int errorResourceId);
    }
}
