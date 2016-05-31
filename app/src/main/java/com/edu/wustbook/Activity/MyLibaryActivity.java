package com.edu.wustbook.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.edu.wustbook.Fragment.LandFragment;
import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.ItemClickListener;
import com.edu.wustbook.Model.RecyclerViewAdapter;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.SearchTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyLibaryActivity extends AppCompatActivity {
    private final static int UP = 1;
    private final static int DOWN = -1;

    private Context context;

    private LandFragment mlandFragment;
    private Fragment currentFragment;
    private LibaryBooksFragment mLibaryBooksFragment;

    private FragmentManager fragmentManager;

    private String cookie;

    private boolean island = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_libary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MyLibaryActivity.this;

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (island) {
            fragmentTransaction.add(R.id.content_mylibary, getLibaryBooksFragment());
            currentFragment = mLibaryBooksFragment;
        } else {
            fragmentTransaction.add(R.id.content_mylibary, getLandFragment());
            currentFragment = mlandFragment;
        }
        fragmentTransaction.commit();
    }

    private void moveFragment(Fragment mFragment, int action) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (action == UP) {
            ft.setCustomAnimations(R.animator.slide_fragment_vertical_up_in, R.animator.slide_fragment_vertical_up_out);
        }
        if (action == DOWN) {
            ft.setCustomAnimations(R.animator.slide_fragment_vertical_down_in,
                    R.animator.slide_fragment_vertical_down_out);
        }
        if (mFragment.isAdded()) {
            ft.hide(currentFragment).show(mFragment);
        } else {
            ft.hide(currentFragment).add(R.id.content_mylibary, mFragment);
        }
        currentFragment = mFragment;
        ft.commit();
    }

    private LibaryBooksFragment getLibaryBooksFragment() {
        if (mLibaryBooksFragment == null)
            mLibaryBooksFragment = new LibaryBooksFragment();
        return mLibaryBooksFragment;
    }


    private Fragment getLandFragment() {
        if (mlandFragment == null) {
            mlandFragment = new LandFragment(this) {
                @Override
                protected void setValue() {
                    values = new ContentValues();
                    values.put("select", "bar_no");    //密码类型
                    values.put("number", mUsernameView.getText().toString());    //学号
                    values.put("passwd", mPasswordView.getText().toString());   //密码
                    values.put("captcha", mVerification_codeView.getText().toString());
                }

                @Override
                public void init() {
                    baseUrlStr = "http://opac.lib.wust.edu.cn:8080/reader/";
                    imgVerificationUrlStr = "http://opac.lib.wust.edu.cn:8080/reader/captcha.php";
                    landUrlStr = "redr_verify.php";
                }

                @Override
                public void loginSuccess(String cookie) {
                    MyLibaryActivity.this.cookie = cookie;
                    moveFragment(getLibaryBooksFragment(), UP);
                    saveAccount();
                }

                @Override
                public boolean makeSureLogin(String html) {
                    Document document = Jsoup.parse(html);
                    Elements elements = document.select("p.header_right_font a");
                    if ("登录".equals(elements.get(1).text().trim())) {
                        Toast.makeText(getApplicationContext(), document.select("table font").get(2).text(), Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public void saveAccount() {
                    if (isChanged) {
                        SharedPreferences sp = context.getSharedPreferences("mylibary", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("remenbarAccount", isRemenbarAccount);
                        editor.putString("username", getUsername());
                        editor.putString("password", getPassword());
//                        if(isAutoLogin){
//                            editor.putString("cookie",MyLibaryActivity.this.cookie);
//                        }else{
//                            editor.putString("cookie","");
//                        }
                        editor.commit();
                    }
                }
            };
        }
        return mlandFragment;
    }


    private class LibaryBooksFragment extends Fragment implements ItemClickListener {
        private RecyclerView booklist;
        private RecyclerViewAdapter adapter;

        private List<Book> books;

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HttpConnection.RESPONSE:
                        books = (List<Book>) msg.obj;
                        setData(books);
                        break;
                    case HttpConnection.Exception:
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                        break;
                    case HttpConnection.NORESPONSE:
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                        break;
                    default:

                }
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_mylibary, container, false);
            booklist = (RecyclerView) v.findViewById(R.id.booklist);
            SearchTask st = new SearchTask("http://opac.lib.wust.edu.cn:8080/reader/book_hist.php", SearchTask.MYLIBARY, handler);
            st.setCookie(cookie);
            st.execute();
            loadData();
            return v;
        }

        private void loadData() {
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            adapter = new RecyclerViewAdapter(context, data);
            adapter.setItemClickListener(this);
            booklist.setLayoutManager(new LinearLayoutManager(getActivity()));
            booklist.setAdapter(adapter);
        }

        private void setData(List<Book> books) {
            if (books != null && books.size() != 0) {
                List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
                for (Book b : books) {
                    Map map = new HashMap<String, Object>();
                    map.put("bookname", b.getName());
                    map.put("bookauthor", b.getAuthor());
                    map.put("flag", "libary");
                    map.put("price", b.getCallNo());
                    maps.add(map);
                }
                adapter.insertData(maps);
            }
        }

        @Override
        public void onItemClick(View view, int postion) {
            Intent intent = new Intent(MyLibaryActivity.this, BookDetailActivity.class);
            Book book= books.get(postion);
            book.setUrl(getString(R.string.libaryBaseUrl) + books.get(postion).getUrl());
            Bundle bundle = new Bundle();
            bundle.putSerializable("book", book);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(View view, int postion) {

        }
    }
}
