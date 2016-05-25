package com.edu.wustbook.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.edu.wustbook.R;
import com.edu.wustbook.Tool.AnimUtils;
import com.edu.wustbook.Tool.BindView;
import com.edu.wustbook.Tool.FloatingBallFrameLayout;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.RecyclerViewController;
import com.edu.wustbook.Tool.ScreenUtils;
import com.edu.wustbook.Tool.SearchTask;
import com.edu.wustbook.Tool.ViewUtils;
import com.edu.wustbook.View.FloatingBall;
import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.ItemClickListener;
import com.edu.wustbook.Model.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LibaryActivity extends AppCompatActivity implements View.OnTouchListener {
    @BindView(values = R.id.fab)
    private FloatingActionButton fab;

    private final static int UP = 1;
    private final static int DOWN = -1;

    private final static long AnimationDuration = 300;

    private float startX, endX;
    private boolean isHidedFloatingActionButton = false;
    private int buttonWidth = 0;

    private String searchStr;

    private final String TAG = "LibaryActivity";

    private SearchFragment searchFragment;
    private PushFragment pushFragment;

    public View progressBar;

    private View emptyView;

    private SearchTask st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        ViewUtils.autoInjectAllFiled(this);
        setDefaultFragment();
        // fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHidedFloatingActionButton)
                    showAllFloatingActionButton();
                else {
                    if (searchFragment == null)
                        searchFragment = new SearchFragment();
                    if (!searchFragment.isVisible()) {
                        moveFragment(UP);
                    }
                }
            }
        });
        fab.setOnTouchListener(this);
    }

    private void setDefaultFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (pushFragment == null)
            pushFragment = new PushFragment();
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
        }
        ft.add(pushFragment, "pushFragment");
        ft.add(searchFragment, "searchFragment").hide(searchFragment);
        ft.commit();
    }

    private void moveFragment(int action) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (searchFragment != null) {
            ViewPropertyAnimator animator = searchFragment.getParentView().animate();
            if (action == UP) {
                animator.alpha(1).y(100).setInterpolator(new LinearInterpolator())
                        .setDuration(AnimationDuration);
                if (isHidedFloatingActionButton) {
                    animator.start();
                    fab.setVisibility(View.GONE);
                } else {
                    animator.setStartDelay(AnimationDuration).start();
                    AnimUtils.scaleOut(fab, (int) AnimationDuration);
                }
                ft.hide(pushFragment).show(searchFragment);
            }
            if (action == DOWN) {
                animator.alpha(0).y(ScreenUtils.getScreenHeight(getApplicationContext()))
                        .setInterpolator(new LinearInterpolator()).setDuration(AnimationDuration).start();
                ft.hide(searchFragment).show(pushFragment);
            }
            ft.commit();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean ismove = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ismove = false;
                startX = event.getX(event.getActionIndex());
                break;
            case MotionEvent.ACTION_MOVE:
                ismove = true;
                endX = event.getX(event.getActionIndex());
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX(event.getActionIndex());
                float X = endX - startX;
                if (buttonWidth == 0) {
                    buttonWidth = fab.getWidth();
                }
                if (X >= buttonWidth / 5) {
                    hideHalfFloatingActionButton();
                } else if (X < -1 * buttonWidth / 5) {
                    showAllFloatingActionButton();
                }
                if (startX == endX) {
                    ismove = false;
                }
                break;
        }
        return ismove;
    }

    private void showAllFloatingActionButton() {
        if (isHidedFloatingActionButton) {
            PropertyValuesHolder pvhX, pvhY;
            pvhX = PropertyValuesHolder.ofFloat("alpha", 1);
            pvhY = PropertyValuesHolder.ofFloat("x", ScreenUtils.getScreenWidth(this) - buttonWidth - 5);
            ObjectAnimator.ofPropertyValuesHolder(fab, pvhX, pvhY).setDuration(500).start();
            isHidedFloatingActionButton = false;
        }
    }

    private void hideHalfFloatingActionButton() {
        if (!isHidedFloatingActionButton) {
            PropertyValuesHolder pvhX, pvhY;
            pvhX = PropertyValuesHolder.ofFloat("alpha", 1);
            pvhY = PropertyValuesHolder.ofFloat("x", ScreenUtils.getScreenWidth(this) - buttonWidth / 2);
            ObjectAnimator.ofPropertyValuesHolder(fab, pvhX, pvhY).setDuration(500).start();
            isHidedFloatingActionButton = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.libary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.mylibary) {
            intent = new Intent(getApplicationContext(), MyLibaryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.accessforlibary) {
            intent = new Intent(getApplicationContext(), AccessOffCampussActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (progressBar != null && progressBar.getVisibility() != View.GONE) {
                return true;
            }
            if (searchStr != null && !"".equals(searchStr)) {
                return true;
            }
            if (searchFragment == null || searchFragment.isHidden()) {
                return super.onKeyDown(keyCode, event);
            }
            if (searchFragment.isVisible()) {
                moveFragment(DOWN);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class PushFragment extends Fragment implements FloatingBallFrameLayout.CenterBallFunction {

        private View view;
        private FrameLayout parent;

        // private SwipeRefreshLayout refreshLayout;

        private List<Book> books;

        private FloatingBallFrameLayout floatingBallFrameLayout;
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HttpConnection.RESPONSE:
                        books = (List<Book>) msg.obj;
                        addBall();
//                        refreshLayout.setVisibility(View.GONE);
//                        refreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        floatingBallFrameLayout.setVisibility(View.VISIBLE);
                        break;
                    case HttpConnection.Exception:
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                        break;
                    case HttpConnection.NORESPONSE:
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                        break;
                    default:
                }
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            parent = (FrameLayout) findViewById(R.id.content_pushfragment);
            view = inflater.inflate(R.layout.fragment_push, container, false);
            parent.addView(view);
            floatingBallFrameLayout = (FloatingBallFrameLayout) view.findViewById(R.id.floatingballframelayout);
//            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
//            refreshLayout.setOnRefreshListener(this);
            progressBar = view.findViewById(R.id.progressbar_layout);
            startPush();
            return parent;
        }

        private void startPush() {
            st = new SearchTask("http://www.lib.wust.edu.cn/%E4%B8%AD%E9%97%B4%E5%86%85%E5%AE%B9/top_lend_more.aspx",
                    SearchTask.PUSH, handler);
            st.execute();
//            refreshLayout.setVisibility(View.VISIBLE);
//            refreshLayout.setRefreshing(true);
            progressBar.setVisibility(View.VISIBLE);
        }

        private void addBall() {
            if (books != null) {
                int k = -1;
                for (int i = -1; i < 2; i = i + 2) {
                    for (int j = -1; j < 2; j += 2) {
                        FloatingBall ball = new FloatingBall(getActivity());
                        ball.setBallCenter(floatingBallFrameLayout.getScreenCenter().getX() + i * 250,
                                floatingBallFrameLayout.getScreenCenter().getY() + j * 250);
                        ball.setRadius(100);
                        ball.setTextSize(30);
                        k++;
                        final String name = books.get(k).getName();
                        ball.setText(name);
                        ball.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (searchFragment == null)
                                    searchFragment = new SearchFragment();
                                if (!searchFragment.isVisible()) {
                                    searchFragment.search_content.setText(name);
                                    moveFragment(UP);
                                }
                            }
                        });
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        //ball.getLayoutParams();
//                        params.width=200;
//                        params.height=200;
//                        ball.setLayoutParams(params);
                        floatingBallFrameLayout.addView(ball, params);
                    }
                }
                floatingBallFrameLayout.setCenterBall(null);
                floatingBallFrameLayout.setFunction(this);
            }
        }

        @Override
        public void onClick(View v) {
            startPush();
        }

        @Override
        public View getView() {
            return parent;
        }

        public View getParentView() {
            return parent;
        }
    }

    class SearchFragment extends Fragment implements Animator.AnimatorListener, ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
        // @BindView(values = R.id.clear_input)
        private AppCompatImageButton clearInput;

        //@BindView(values = R.id.searchtext)
        public AppCompatEditText search_content;

        //@BindView(values = R.id.search_list)
        private RecyclerView recyclerView;

        private SwipeRefreshLayout refreshLayout;

        private RecyclerViewAdapter adapter;

        private View view;
        private FrameLayout parent;

        private ContentValues params;

        private List<Book> books = new ArrayList<Book>();
        private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        private boolean isLoadingMore;
        private int currentPage = 1;

        private Handler hanler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HttpConnection.RESPONSE:
                        List<Book> newBooks = (List<Book>) msg.obj;
                        if (newBooks != null && newBooks.size() != 0) {
                            setData(newBooks);
                            books.addAll(newBooks);
                        } else
                            Toast.makeText(getApplicationContext(), getString(R.string.nomore), Toast.LENGTH_LONG).show();
                        setRefreshing(false);
                        isLoadingMore = false;
                        break;
                    case HttpConnection.Exception:
                        Toast.makeText(getApplicationContext(), getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                        break;
                    case HttpConnection.NORESPONSE:
                        Toast.makeText(getApplicationContext(), getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                        break;
                    default:

                }
            }
        };

        private TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null)
                    searchStr = s.toString();
                if (st != null && st.getStatus() == AsyncTask.Status.RUNNING) {
                    st.cancel(false);
                    setRefreshing(false);
                    isLoadingMore = false;
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null)
                    searchStr = s.toString();
                currentPage = 0;
                loadMore();
            }
        };

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            parent = (FrameLayout) findViewById(R.id.content_searchfragment);
            view = inflater.inflate(R.layout.fragment_search, container, false);
            init();
            loadData();
            return view;
        }

        private void setRefreshing(boolean isRefreshing) {
            refreshLayout.setRefreshing(isRefreshing);
        }

        @Override
        public void onRefresh() {

        }

        @Override
        public View getView() {
            return parent;
        }

        public View getParentView() {
            return parent;
        }

        public void init() {
            parent.addView(view);
            // ViewUtils.autoInjectAllFiled(parent);
            parent.animate().setListener(this);
            emptyView = findViewById(R.id.maybeclosesearch);
            emptyView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (searchStr == null || searchStr.equals(""))
                            moveFragment(DOWN);
                    }
                    return true;
                }
            });

            clearInput = (AppCompatImageButton) parent.findViewById(R.id.clear_input);
            clearInput.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    search_content.setText("");
                    adapter.deleteAllDate();
                }
            });

            search_content = (AppCompatEditText) parent.findViewById(R.id.searchtext);
            search_content.addTextChangedListener(textWatcher);

            recyclerView = (RecyclerView) parent.findViewById(R.id.search_list);

            refreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.refreshlayout);
            refreshLayout.setOnRefreshListener(this);
        }

        private void initSearchTask() {
            st = new SearchTask(getString(R.string.libarySearchUrl), SearchTask.LIBARY, hanler);
        }

        private void loadData() {
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            adapter = new RecyclerViewAdapter(getActivity(), data);
            adapter.setItemClickListener(this);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                        if (!isLoadingMore) {
                            loadMore();
                        }
                    }
                }
            });

            initSearchTask();

            params = new ContentValues();
            params.put("strSearchType", "title");
            params.put("match_flag", "forward");
            params.put("strText", "android");
            params.put("doctype", "ALL");
            params.put("onlylendable", "no");
            params.put("displaypg", 20);
            params.put("showmode", "list");
            params.put("sort", "CATA_DATE");
            params.put("orderby", "desc");
            params.put("dept", "ALL");
            params.put("page", 1);
        }

        private void loadMore() {
            if (searchStr != null && !"".equals(searchStr)) {
                isLoadingMore = true;
                initSearchTask();

                currentPage++;
                params.put("strSearchType", "title");
                params.put("strText", searchStr);
                params.put("page", currentPage);
                adapter.deleteAllDate();
                //books.clear();
                st.execute(params);
                setRefreshing(true);
            } else {
                adapter.deleteAllDate();
            }
        }

        private void setData(List<Book> newBooks) {
            if (newBooks != null && newBooks.size() != 0) {
                List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
                for (Book b : newBooks) {
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

        private void clear() {
            currentPage = 0;
            params.put("strSearchType", "title");
            params.put("match_flag", "forward");
            params.put("strText", "android");
            params.put("doctype", "ALL");
            params.put("onlylendable", "no");
            params.put("displaypg", 10);
            params.put("showmode", "list");
            params.put("sort", "CATA_DATE");
            params.put("orderby", "desc");
            params.put("dept", "ALL");
            params.put("page", 1);
            st = null;
            adapter.deleteAllDate();
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (!this.isHidden()) {
                parent.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (this.isHidden()) {
                clear();
                if (isHidedFloatingActionButton)
                    fab.setVisibility(View.VISIBLE);
                else
                    AnimUtils.scaleIn(fab, (int) AnimationDuration, 0);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onItemClick(View view, int postion) {
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra("type", "libary");
            intent.putExtra("url", getString(R.string.libaryBaseUrl) + books.get(postion).getUrl());
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(View view, int postion) {

        }
    }
}
