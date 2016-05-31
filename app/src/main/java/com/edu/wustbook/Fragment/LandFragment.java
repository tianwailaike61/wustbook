package com.edu.wustbook.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.wustbook.Activity.BaseActivity;
import com.edu.wustbook.Activity.BookDetailActivity;
import com.edu.wustbook.Activity.MyLibaryActivity;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.IMGUtils;
import com.edu.wustbook.Tool.LoginTask;
import com.edu.wustbook.Tool.PictureTask;

import java.util.Map;

public abstract class LandFragment extends Fragment {

    public AutoCompleteTextView mUsernameView;
    public EditText mPasswordView, mVerification_codeView;
    private AppCompatImageView mIdentifying_Code_ImageView;
    private View mProgressView;
    private View mLoginFormView;
    private AppCompatCheckBox remenbarAccount;

    protected String baseUrlStr;
    protected String imgVerificationUrlStr;
    protected String landUrlStr;

    private String cookie;

    public boolean isRemenbarAccount = false, isChanged = false;

    private String errorStr = "";

    public ContentValues values;

    private LoginTask lt;

    public Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressView.setVisibility(View.GONE);
            mLoginFormView.setVisibility(View.VISIBLE);
            switch (msg.what) {
                case HttpConnection.RESPONSE:
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    Object ck = map.get("cookie");
                    if (ck != null)
                        cookie = (String) ck;
                    if (msg.arg1 == 1) {
                        Bitmap bitmap = (Bitmap) map.get("IMG");
                        if (bitmap != null)
                            mIdentifying_Code_ImageView.setBackground(IMGUtils.zoomBitmap(bitmap));
//                        else
//                            mIdentifying_Code_ImageView.setBackgroundResource(R.drawable.ic_menu_camera);
                    } else {
                        Object html = map.get("html");
                        if (html != null) {
                            if (makeSureLogin(html.toString().trim())) {
                                loginSuccess(cookie);
                                return;
                            }
                        }
                        loginFail();
                    }
                    break;
                case HttpConnection.Exception:
                    Toast.makeText(parentActivity, getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                    break;
                case HttpConnection.NORESPONSE:
                    Toast.makeText(parentActivity, getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                    break;
                default:

            }
        }
    };

    private Activity parentActivity;

    private final static String TAG = "LandFragment";


    public LandFragment(Activity activity) {
        parentActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_land, container, false);
        initView(v);
        loadData();
        init();
        return v;
    }

    private void initView(View v) {
        mUsernameView = (AutoCompleteTextView) v.findViewById(R.id.username);
        mPasswordView = (EditText) v.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button loginbutton = (Button) v.findViewById(R.id.landbutton);
        loginbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button cancelbutton = (Button) v.findViewById(R.id.cancelbutton);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    }
                }).start();
            }
        });


        mProgressView = v.findViewById(R.id.login_progress);
        mLoginFormView = v.findViewById(R.id.login_form);

        mVerification_codeView = (EditText) v.findViewById(R.id.identifying_code_text);
        mVerification_codeView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    setVerificationImg();
            }
        });

        mIdentifying_Code_ImageView = (AppCompatImageView) v.findViewById(R.id.identifying_code_img);


        remenbarAccount = (AppCompatCheckBox) v.findViewById(R.id.remenber_account);
        remenbarAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemenbarAccount = isChecked;
                isChanged = true;
            }
        });

    }

    private void loadData() {
        if (parentActivity instanceof MyLibaryActivity) {
            SharedPreferences sp = getActivity().getSharedPreferences("mylibary", 0);
            if (sp.getBoolean("remenbarAccount", false)) {
                remenbarAccount.setChecked(true);
                mUsernameView.setText(sp.getString("username", ""));
                mPasswordView.setText(sp.getString("password", ""));
            }
        } else if (parentActivity instanceof BaseActivity) {
            SharedPreferences sp = getActivity().getSharedPreferences("bookstore", 0);
            if (sp.getBoolean("remenbarAccount", false)) {
                remenbarAccount.setChecked(true);
                mUsernameView.setText(sp.getString("username", ""));
                mPasswordView.setText(sp.getString("password", ""));
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (lt != null)
            return;
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String code = mVerification_codeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(username) && !isPasswordValid(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(code)){
            mVerification_codeView.setError(getString(R.string.error_field_identifying_code));
            focusView = mVerification_codeView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(code) && !isCodeValid(code)) {
            mVerification_codeView.setError(getString(R.string.error_invalid_identifying_code));
            focusView = mVerification_codeView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            login();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lt != null && lt.getStatus() == AsyncTask.Status.RUNNING)
            lt.cancel(true);
    }

    private boolean isUserNameValid(String username) {
        return username.length() > 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean isCodeValid(String code) {
        return code.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public String getUsername() {
        return mUsernameView.getText().toString();
    }

    public String getPassword() {
        return mPasswordView.getText().toString();
    }

    public String getVerificationCode() {
        return mVerification_codeView.getText().toString();
    }

    protected void setVerificationImg() {
        PictureTask task = new PictureTask(imgVerificationUrlStr, handler);
        task.execute();
    }

//    public boolean checkInput() {
//        String name = mUsernameView.getText().toString().trim();
//        String pswd = mPasswordView.getText().toString().trim();
//        String code = mVerification_codeView.getText().toString().trim();
//        if ("".equals(name) || "".equals(pswd) || "".equals(code)) {
//            Toast.makeText(getActivity(), getString(R.string.emptyinput), Toast.LENGTH_LONG).show();
//            return false;
//        }
//        return true;
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.landbutton:
//                login();
//                break;
//            case R.id.cancelbutton:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Instrumentation inst = new Instrumentation();
//                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
//                    }
//                }).start();
//                //parentActivity.finish();
//                break;
//            case R.id.identifying_code_img:
//                setVerificationImg();
//                break;
//            default:
//                break;
//        }
//    }

    private void login() {
        lt = new LoginTask(baseUrlStr + landUrlStr, handler);
        setCookie(lt);
        setValue();
        if (values == null)
            lt.execute();
        else
            lt.execute(values);
    }

    protected void setCookie(LoginTask lt) {
        if (cookie != null)
            lt.setCookie(cookie);
    }

    public String getCookie() {
        return cookie;
    }

    private void loginFail() {
        Toast.makeText(getActivity(), errorStr, Toast.LENGTH_LONG).show();
    }

    protected abstract void setValue();

    public abstract void init();

    public abstract void loginSuccess(String cookie);

    public abstract boolean makeSureLogin(String html);

    public abstract void saveAccount();

}