package com.aipxperts.ecountdown.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
    public static Context _context;
    public static ConnectionDetector cd;
    public Constants(Context context) {
        this._context = context;
        cd = new ConnectionDetector(context);
    }


    //login
    public static String Token_tag = "token";

   /* public static void setTextWatcher(final Context context, final EditText editText, final TextView textView) {

        TextWatcher textWatcher = new TextWatcher() {
            boolean flag = false;

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                if (arg0.length() == 0) {
                    flag = true;
                } else {
                    flag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {


                if (s.length() == 0) {
                    Animation slidedown = AnimationUtils.loadAnimation(context, R.anim.hint_slide_down);
                    textView.startAnimation(slidedown);
                    textView.setVisibility(View.INVISIBLE);
                } else {
                    if (s.length() == 1 && flag) {
                        Animation slideup = AnimationUtils.loadAnimation(context, R.anim.hint_slide_up);
                        textView.startAnimation(slideup);

                    }
                    editText.setError(null);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
    }
    public static void setTextWatcher1(final Context context, final TextView editText, final TextView textView) {

        TextWatcher textWatcher = new TextWatcher() {
            boolean flag = false;

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                if (arg0.length() == 0) {
                    flag = true;
                } else {
                    flag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {


                if (s.length() == 0) {
                    Animation slidedown = AnimationUtils.loadAnimation(context, R.anim.hint_slide_down);
                    textView.startAnimation(slidedown);
                    textView.setVisibility(View.GONE);
                } else {
                    if (s.length() == 1 && flag) {
                        Animation slideup = AnimationUtils.loadAnimation(context, R.anim.hint_slide_up);
                        textView.startAnimation(slideup);

                    }
                    editText.setError(null);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
    }*/
   /* public static  void hidekeyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager)_context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }*/




   /* public static void Authorization()
    {
        mValidator.showtoast(_context.getResources().getString(R.string.token_expire));
        MyInvestmentFragment.list_type_main.clear();
        Pref.deleteAll(_context);
        Intent i = new Intent(_context, SplashActivity.class);
        _context.startActivity(i);
        ((FragmentActivity)_context).finish();
    }*/

//date format change

    public static String parseDateToAddDatabase(String time) {
        String inputPattern = "dd MMM yyyy";
        String outputPattern = "MM/dd/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date ;
        String str = null;


        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return str;
    }


    public static String parseDateDatabaseToDisplay(String time) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "dd MMM yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date ;
        String str = "";


        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return str;
    }
    public static String parseDateDatabaseToDisplay1(String time) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "EEEE,dd MMMM yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date ;
        String str = "";


        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return str;
    }
    public static String parseDateDatabaseToDisplay_month(String time) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "MMMM yyyy";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date ;
        String str = "";


        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return str;
    }
    public static String parseDateDatabaseToDisplay_day(String time) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "dd";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date ;
        String str = "";


        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return str;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


}
