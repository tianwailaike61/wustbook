package com.edu.wustbook.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.edu.wustbook.Fragment.BookStoreFragment;
import com.edu.wustbook.Fragment.LandFragment;
import com.edu.wustbook.Fragment.MyBookStoreFragment;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.JsonUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;

    private Fragment currentFragment, lastFragment, bookstoreFragment, mybookstoreFragment;

    private LandFragment mlandFragment;

    private int checkedId;

    private static boolean island = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkedId = R.id.nav_search;
        setDefaultFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setDefaultFragment() {
        bookstoreFragment = new BookStoreFragment();
        currentFragment = bookstoreFragment;
        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.content_bookstore, currentFragment);
        mFragmentTransaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (!item.isChecked()) {
            int id = item.getItemId();
            switch (id) {
                case R.id.nav_search:
                    checkedId = R.id.nav_search;
                    if (bookstoreFragment == null)
                        bookstoreFragment = new BookStoreFragment();
                    turnToFragment(bookstoreFragment);
                    break;
                case R.id.nav_mystore:
                    checkedId = R.id.nav_mystore;
                    if (island) {
                        if (mybookstoreFragment == null)
                            mybookstoreFragment = new MyBookStoreFragment();
                        turnToFragment(mybookstoreFragment);
                    } else {
                        lastFragment = currentFragment;
                        turnToFragment(getLandFragment());
                    }
                    break;
                case R.id.nav_uploadbook:
                    if (island) {
                        Intent intent = new Intent(this, UploadBookActivity.class);
                        startActivity(intent);
                    } else {
                        turnToFragment(getLandFragment());
                    }
                    break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setCheckedItem(checkedId);
    }

//    private void showLandFragment() {
//        FragmentManager mFragmentManager = getFragmentManager();
//        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
//        Fragment fragment = getLandFragment();
//        if (!fragment.isAdded())
//            mFragmentTransaction.add(fragment, fragment.getTag());
//        mFragmentTransaction.hide(currentFragment).show(fragment);
//        mFragmentTransaction.commit();
//    }

    private void turnToFragment(Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            //BookStoreFragment mBookStoreFragment = new BookStoreFragment();
            if (!fragment.isAdded())
                mFragmentTransaction.add(fragment, fragment.getTag());
            mFragmentTransaction.hide(currentFragment).show(fragment);
            mFragmentTransaction.remove(currentFragment);
            currentFragment = fragment;
            mFragmentTransaction.replace(R.id.content_bookstore, currentFragment);

            mFragmentTransaction.commit();
        }
    }

    private Fragment getLandFragment() {
        if (mlandFragment == null) {
            mlandFragment = new LandFragment(this) {
                @Override
                protected void setValue() {
                    values = new ContentValues();
                    values.put("code", getVerificationCode());

                    values.put("Uname", getUsername());    //学号
                    values.put("password", getPassword());   //密码
                    String []ss=getCookie().split(";");
                    String []ss1=ss[0].split("=");
                    values.put("cookie",ss1[1] );
                }

                @Override
                public void init() {
                    baseUrlStr = getString(R.string.bookstoreUrl);
                    imgVerificationUrlStr = "http://opac.lib.wust.edu.cn:8080/reader/captcha.php";
                    landUrlStr = "LandServlet";
                }

                @Override
                public void loginSuccess(String cookie) {
                    if (lastFragment != null) {
                        turnToFragment(lastFragment);
                        lastFragment = null;
                    } else {
                        Intent intent = new Intent(getApplicationContext(), UploadBookActivity.class);
                        startActivity(intent);
                    }
                    saveAccount();
                    island = true;
                }

                @Override
                public boolean makeSureLogin(String html) {
                    if ("fail".equals(html)) {
                        return false;
                    }
                    int Uid= JsonUtils.getUid(html);
                    if (Uid!=-1)
                        return true;
                    return false;
                }

                @Override
                public void saveAccount() {
                    if (isChanged) {
                        SharedPreferences sp = getSharedPreferences("bookstore", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("remenbarAccount", isRemenbarAccount);
                        editor.putString("username", getUsername());
                        editor.putString("password", getPassword());
                        editor.commit();
                    }
                }
            };
        }
        return mlandFragment;
    }
}
