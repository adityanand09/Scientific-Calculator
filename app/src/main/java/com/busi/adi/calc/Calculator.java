package com.busi.adi.calc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;


public class Calculator extends Activity implements expEvaluator.EvaluateCallback, View.OnLongClickListener{

    private static final String NAME = Calculator.class.getName();
    private static final String C_STATE = NAME + "_cs";
    private static final String C_EXPRESSION = NAME + "_ce";


    private enum CalculatorState {
        INPUT, EVALUATE, RESULT, ERROR
    }
    public static final int INVALID_RES_ID = -1;

    private CalculatorState currState;
    private expTokenizer mTokenizer;
    private expEvaluator mEvaluator;

    private CalcTextView inputText;
    private CalcTextView result;
    private View del;
    private View eq;

    private Vibrator vibe;

    private View currBtn;
    private LinearLayout adv_pan_nor_1, adv_pan_nor_2, adv_pan_inv_1, adv_pan_inv_2;



    private final TextWatcher inputTextwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            setState(CalculatorState.INPUT);
            mEvaluator.evaluate(editable, Calculator.this);
        }
    };

    private final Editable.Factory inputFactory = new Editable.Factory() {
        @Override
        public Editable newEditable(CharSequence source) {
            final boolean isEdited = currState == CalculatorState.INPUT
                    || currState == CalculatorState.ERROR;
            return new expBuilder(source, mTokenizer, isEdited);
        }
    };

    private final View.OnKeyListener inputOnKeyListner = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        currBtn = eq;
                        onEquals();
                    }
                    return true;
                case KeyEvent.KEYCODE_DEL:
                    onDelete();
                    return true;
            }
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        adv_pan_nor_1 = findViewById(R.id.adv_panel_norm_1);
        adv_pan_nor_2 = findViewById(R.id.adv_panel_norm_2);
        adv_pan_inv_1 = findViewById(R.id.adv_panel_inv_1);
        adv_pan_inv_2 = findViewById(R.id.adv_panel_inv_2);
        adv_pan_inv_1.setVisibility(View.GONE);
        adv_pan_inv_2.setVisibility(View.GONE);
        mTokenizer = new expTokenizer(this);
        mEvaluator = new expEvaluator(mTokenizer);
        
        inputText = (CalcTextView) findViewById(R.id.input);
        inputText.setEditableFactory(inputFactory);
        inputText.addTextChangedListener(inputTextwatcher);
        inputText.setOnKeyListener(inputOnKeyListner);
        result = (CalcTextView) findViewById(R.id.result);

        savedInstanceState = savedInstanceState == null ? Bundle.EMPTY : savedInstanceState;
        setState(CalculatorState.values()[
                savedInstanceState.getInt(C_STATE, CalculatorState.INPUT.ordinal())]);
        inputText.setText(mTokenizer.getLocalizedExpression(
                savedInstanceState.getString(C_EXPRESSION, "")));
        mEvaluator.evaluate(inputText.getText(), this);
        del = findViewById(R.id.del);
        del.setOnLongClickListener(this);
        eq = findViewById(R.id.eq);


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(C_STATE, currState.ordinal());
        outState.putString(C_EXPRESSION, mTokenizer.getNormalizedExpression(inputText.getText().toString()));
    }



    @Override
    public boolean onLongClick(View view) {
        vibe.vibrate(100);
        currBtn = view;

        if (view.getId() == R.id.del) {
            onClear();
            return true;
        }
        return false;
    }

    private void setState(CalculatorState state) {
        if (currState != state) {
            currState = state;
        }

        if (state == CalculatorState.ERROR) {
            result.setTextColor(getResources().getColor(R.color.calculator_error_color));
        } else {
            result.setTextColor(getResources().getColor(R.color.display_result_text_color));
        }
    }

    @Override
    public void onEvaluate(String res, int errId) {
        if (currState == CalculatorState.INPUT) {
            result.setText(res);
        } else if (errId != INVALID_RES_ID) {
            onError(errId);
        } else if (!TextUtils.isEmpty(res)) {
            onResult(res);
        } else if (currState == CalculatorState.EVALUATE) {
            setState(CalculatorState.INPUT);
        }
        inputText.requestFocus();
    }

    public void btnClicked(View view) {
        vibe.vibrate(50);
        currBtn = view;

        switch (view.getId()) {
            case R.id.eq:
                onEquals();
                break;
            case R.id.del:
                onDelete();
                break;
            case R.id.fun_ln:
            case R.id.fun_log:

            case R.id.fun_sin:
            case R.id.fun_cos:
            case R.id.fun_tan:

            case R.id.fun_sin_inv:
            case R.id.fun_cos_inv:
            case R.id.fun_tan_inv:

            case R.id.fun_sinh:
            case R.id.fun_cosh:
            case R.id.fun_tanh:

            case R.id.fun_sinh_inv:
            case R.id.fun_cosh_inv:
            case R.id.fun_tanh_inv:
                // Add left parenthesis after functions.
                inputText.append(((Button) view).getText() + "(");
                break;
            case R.id.fun_shift:
                if(adv_pan_inv_1.getVisibility() == View.GONE && adv_pan_inv_2.getVisibility() == View.GONE){
                    adv_pan_nor_1.setVisibility(View.GONE);
                    adv_pan_nor_2.setVisibility(View.GONE);
                    adv_pan_inv_1.setVisibility(View.VISIBLE);
                    adv_pan_inv_2.setVisibility(View.VISIBLE);
                }
                else {
                    adv_pan_inv_1.setVisibility(View.GONE);
                    adv_pan_inv_2.setVisibility(View.GONE);
                    adv_pan_nor_1.setVisibility(View.VISIBLE);
                    adv_pan_nor_2.setVisibility(View.VISIBLE);
                }
                break;
            default:
                inputText.append(((Button) view).getText());
                break;
        }
    }

    private void onEquals() {
        if (currState == CalculatorState.INPUT) {
            setState(CalculatorState.EVALUATE);
            mEvaluator.evaluate(inputText.getText(), this);
        }
    }

    private void onDelete() {
        // Delete works like backspace; remove the last character from the expression.
        final Editable formulaText = inputText.getEditableText();
        int formulaLength = formulaText.length();
        if (formulaLength > 0) {
            formulaText.delete(formulaLength - 1, formulaLength);
        }
        formulaLength -= 1;
        if (formulaLength>0){
            while(formulaLength>0 && (Character.isAlphabetic(formulaText.charAt(formulaLength-1)) || formulaText.charAt(formulaLength-1) == '⁻' || formulaText.charAt(formulaLength-1) == '¹')) {
                formulaText.delete(formulaLength - 1, formulaLength);
                formulaLength -= 1;
            }
        }
    }

    private void onClear() {
        if (TextUtils.isEmpty(inputText.getText())) {
            return;
        }
        inputText.getEditableText().clear();
    }

    private void onResult(final String res) {
        result.setText(null);
        inputText.setText(res);
        setState(CalculatorState.RESULT);
    }

    private void onError(final int errId) {

        if (currState != CalculatorState.EVALUATE) {
            result.setText(errId);
            return;
        }

        setState(CalculatorState.ERROR);
        result.setText(errId);
    }

}
