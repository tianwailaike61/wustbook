package com.edu.wustbook.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.wustbook.Model.Book;
import com.edu.wustbook.Model.BookDBManager;
import com.edu.wustbook.R;
import com.edu.wustbook.Tool.BindView;
import com.edu.wustbook.Tool.BookDetailTask;
import com.edu.wustbook.Tool.ConversionUtils;
import com.edu.wustbook.Tool.HttpConnection;
import com.edu.wustbook.Tool.IMGUtils;
import com.edu.wustbook.Tool.PictureTask;
import com.edu.wustbook.Tool.ViewUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(values = {R.id.fab1})
    private FloatingActionButton fab1;

    //@BindView(values = {R.id.username})
    private CollapsingToolbarLayout bookname;

    private Context context;

    public boolean collected = false;

    private final int[] ids = new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.text5, R.id.text6, R.id.text7};
    public TextView[] tvs = new TextView[ids.length];

    private Book book;

    private BookDBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        ViewUtils.autoInjectAllFiled(this);
        fab1.setOnClickListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
        manager = BookDBManager.getInstance(context);
        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("book");
//        if (intent.getBooleanExtra("collected", false)) {
//            collected = true;
//        } else
            collected = search() == null ? false : true;
        bookname = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        bookname.setTitle(book.getName());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (Book.LIBARY == book.getType()) {
            libaryBookDetailFragment fragment = new libaryBookDetailFragment();
            transaction.add(R.id.bookcontent, fragment);
        } else if (Book.BOOKSTORE == book.getType()) {
            transaction.add(R.id.bookcontent, new storeBookDetailFragment());
        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
        }
        transaction.commit();
        setButton();
    }

    @Override
    public void onClick(View v) {
        if (v == fab1) {
            if (collected)
                cancelCollect();
            else
                collect();
        }
    }

    public Book search() {
        List<Book> books = manager.search(book);
        return books.size() == 0 ? null : books.get(0);
    }

    public void cancelCollect() {
        if (manager.delete(manager.search(book).get(0).getId())) {
            collected = false;
            fab1.setImageResource(R.drawable.uncollected);
            Toast.makeText(context, getString(R.string.deletesuccess), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, getString(R.string.deletefail), Toast.LENGTH_LONG).show();
        }
    }

    public void collect() {
        if (Book.LIBARY == book.getType()) {
            //book.setUrl(url);
            String ss[] = tvs[0].getText().toString().split("\n");
            String author = ss[1].replace("责任者:", "");
            book.setAuthor(TextUtils.isEmpty(author) ? null : author);
            String name = ss[0].replace("题名:", "");
            book.setName(TextUtils.isEmpty(name) ? null : name);
            book.setPublisher(tvs[2].getText().toString());
            book.setState(0);
            book.setType(Book.LIBARY);
        }
        //else {
        //book=(Book) getIntent().getParcelableExtra("book");
        //book.setUrl(url);
        // String ss = tvs[0].getText().toString();
        //book.setAuthor(ss.replace("责任者:", ""));
        // book.setName(tvs[0].getText().toString().replace("书名:", ""));
        //book.setSaler(tvs[3].getText().toString().replace("出售者:", ""));
        //Log.e("jhk","---"+tvs[2].getText().toString());
        //float f=Float.parseFloat(tvs[2].getText().toString().replace("价格:", ""));
        //int price = (int) f;
        // book.setPrice(f);
        //  book.setType(Book.BOOKSTORE);
        //}
        if (manager.insert(book)) {
            collected = true;
            fab1.setImageResource(R.drawable.collection);
            Toast.makeText(context, getString(R.string.addsuccess), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, getString(R.string.addfail), Toast.LENGTH_LONG).show();
        }
    }

    public void setButton() {
        if (collected) {
            fab1.setImageResource(R.drawable.collection);
        } else {
            fab1.setImageResource(R.drawable.uncollected);
        }
    }

    public class libaryBookDetailFragment extends Fragment {
        private ImageView bookcover;
        private TableLayout tableLayout;

        private Handler handler = new Handler() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HttpConnection.RESPONSE:

                        Map<String, Object> map = (Map<String, Object>) msg.obj;
                        if (msg.arg1 == 1) {
                            Bitmap bitmap = (Bitmap) map.get("IMG");
                            if (bitmap != null)
                                bookcover.setBackground(IMGUtils.zoomBitmap(bitmap));
//                            else
//                                bookcover.setBackgroundResource(R.drawable.ic_launcher);
                        } else {

                            Object html = map.get("html");
                            if (html != null) {
                                loadData(html.toString().trim());
                                fab1.setClickable(true);
                            }
                        }
                        // setButton();
                        break;
                    case HttpConnection.Exception:
                        fab1.setClickable(false);
                        Toast.makeText(getActivity(), getResources().getString(R.string.network_excetption), Toast.LENGTH_LONG).show();
                        break;
                    case HttpConnection.NORESPONSE:
                        fab1.setClickable(false);
                        Toast.makeText(getActivity(), getResources().getString(R.string.network_no_response), Toast.LENGTH_LONG).show();
                        break;
                    default:

                }
            }
        };

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_libary_book_detail, container, false);
            initView(v);
            //setButton();
            new BookDetailTask(book.getUrl(), handler).execute();
            return v;
        }

        private void initView(View v) {
            bookcover = (ImageView) v.findViewById(R.id.bookcover);
            tableLayout = (TableLayout) v.findViewById(R.id.table);
            for (int i = 0; i < 5; i++) {
                tvs[i] = (TextView) v.findViewById(ids[i]);
            }
        }

        private void loadData(String html) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("img#book_img");
            String pictureUrl = elements.attr("src");
            if (pictureUrl != null)
                new PictureTask("http://opac.lib.wust.edu.cn:8080" + pictureUrl.replace("..", ""), handler).execute();
            elements.clear();
            for (Element element : document.select("dl.booklist")) {
                String title = element.select("dt").text().trim();
                String all = element.text().trim();
                if ("题名/责任者:".equals(title)) {
                    String[] ss = element.select("dd").text().split("/");
                    if (ss.length >= 2) {
                        String title1 = "题名:" + ss[0];
                        String author = "责任者:" + ss[1];
                        tvs[0].setText(title1 + "\n" + author);
                    } else {
                        tvs[0].setText(all);
                    }
                } else if ("版本说明:".equals(title)) {
                    tvs[1].setText(all);
                } else if ("出版发行项:".equals(title)) {
                    tvs[2].setText(all);
                } else if ("ISBN及定价:".equals(title)) {
                    CharSequence cs = tvs[3].getText();
                    if (cs != null && !"".equals(cs.toString()))
                        tvs[3].setText(tvs[3].getText() + "\n" + all);
                    else
                        tvs[3].setText(all);
                } else if ("提要文摘附注:".equals(title)) {
                    tvs[4].setText(all);
                }
            }
            getBookMessage(document);
        }

        private void getBookMessage(Document document) {
            if (this.isAdded()) {
                Elements elements = document.select("tr.whitetext");
                tableLayout.setStretchAllColumns(true);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                for (Element element : elements) {
                    Elements elements1 = element.select("td");
                    final TableRow tableRow = new TableRow(getActivity());
                    TextView tv1 = new TextView(getActivity());
                    final String callNo = elements1.get(0).text().trim();
                    tv1.setText(callNo);
                    tableRow.addView(tv1);
                    TextView tv2 = new TextView(getActivity());
                    final String position = elements1.get(3).text().trim();
                    tv2.setText(position);
                    tableRow.addView(tv2);
                    TextView tv3 = new TextView(getActivity());
                    final String state = elements1.get(4).text().trim();
                    tv3.setText(state);
                    tableRow.addView(tv3);
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(String.format(getResources().getString(R.string.libarybookdetail), callNo, position, state)).create().show();
                        }
                    });
                    tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        }

        private void getIntroduction(final String urlStr) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpConnection httpConnection = new HttpConnection(urlStr);
                    if (httpConnection.open() != null) {
                        String html = ConversionUtils.inputStreamToString(httpConnection.getInputStream());
                        if (httpConnection.getConnState() == HttpConnection.RESPONSE) {
                            Document document = Jsoup.parse(html);
                            tvs[4].setText(document.select("div#tab_default_1").text().trim());
                            httpConnection.close();
                            return;
                        }
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.network_no_response),
                            Toast.LENGTH_LONG).show();

                }
            }).start();
        }
    }

    public class storeBookDetailFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_bookstore_book_detail, container, false);
            for (int i = 0; i < 6; i++) {
                tvs[i] = (TextView) v.findViewById(ids[i]);
            }
            Book book = (Book) getIntent().getSerializableExtra("book");
            tvs[0].setText("书名:" + getString(book.getName()));
            tvs[1].setText("作者:" + getString(book.getAuthor()));
            tvs[2].setText("价格:" + book.getPrice());
            tvs[3].setText("出售者:" + getString(book.getSaler()));
            tvs[4].setText("电话号码:" + getString(book.getPhoneNumber()));
            tvs[5].setText("qq:" + getString(book.getQq()));
            return v;
        }

        private String getString(String s) {
            if (s == null || TextUtils.isEmpty(s))
                return "";
            return s;
        }

//        private void setButton() {
//            bt1.setVisibility(View.VISIBLE);
//            bt1.setText(getString(R.string.buy));
//            bt2.setVisibility(View.VISIBLE);
//            bt2.setText(getString(R.string.collect));
//        }
    }
}
