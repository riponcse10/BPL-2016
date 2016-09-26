package com.tigersapp.bdcricket.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tigersapp.bdcricket.R;
import com.tigersapp.bdcricket.adapter.BasicListAdapter;
import com.tigersapp.bdcricket.model.CricketNews;
import com.tigersapp.bdcricket.util.CircleImageView;
import com.tigersapp.bdcricket.util.Constants;
import com.tigersapp.bdcricket.util.FetchFromWeb;
import com.tigersapp.bdcricket.util.RecyclerItemClickListener;
import com.tigersapp.bdcricket.util.RoboAppCompatActivity;
import com.tigersapp.bdcricket.util.ViewHolder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author ripon
 */
@ContentView(R.layout.news)
public class CricketNewsListActivity extends RoboAppCompatActivity {

    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    @InjectView(R.id.adViewNews)
    AdView adView;

    @Inject
    ArrayList<CricketNews> cricketNewses;

    @Inject
    Gson gson;

    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("স্পোর্টস নিউজ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        typeface = Typeface.createFromAsset(getAssets(), Constants.SOLAIMAN_LIPI_FONT);

        recyclerView.setAdapter(new BasicListAdapter<CricketNews,NewsViewHolder>(cricketNewses) {
            @Override
            public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlenews, parent, false);
                return new NewsViewHolder(view);
            }

            @Override
            public void onBindViewHolder(NewsViewHolder holder, final int position) {

                holder.headline.setTypeface(typeface);
                holder.author.setTypeface(typeface);
                holder.headline.setText(cricketNewses.get(position).getTitle());
                holder.author.setText(cricketNewses.get(position).getSourceBangla());

                holder.time.setText(cricketNewses.get(position).getDate());
                Picasso.with(CricketNewsListActivity.this)
                        .load(cricketNewses.get(position).getImageUrl())
                        .placeholder(R.drawable.default_image)
                        .into(holder.circleImageView);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(CricketNewsListActivity.this, NewsDetailsActivity.class);
                        intent.putExtra(NewsDetailsActivity.EXTRA_NEWS_OBJECT, cricketNewses.get(position));
                        CricketNewsListActivity.this.startActivity(intent);
                    }
                })
        );

        String url = "http://www.banglanews24.com/api/category/5";
        Log.d(Constants.TAG, url);

        final AlertDialog progressDialog = new SpotsDialog(CricketNewsListActivity.this, R.style.Custom);
        progressDialog.show();
        progressDialog.setCancelable(true);
        FetchFromWeb.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progressDialog.dismiss();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CricketNews cricketNews = new CricketNews(jsonObject.getString("ContentID"),"http://www.banglanews24.com/media/imgAll/"+jsonObject.getString("ImageSMPath"),
                                "http://www.banglanews24.com/api/details/"+jsonObject.getString("ContentID"),jsonObject.getString("ContentHeading"),
                                jsonObject.getString("updated_at"),"banglanews","বাংলানিউজ ২৪ ");
                        cricketNewses.add(cricketNews);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Collections.sort(cricketNewses);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.d(Constants.TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressDialog.dismiss();
                Toast.makeText(CricketNewsListActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                Toast.makeText(CricketNewsListActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });

        url = "http://www.kalerkantho.com/api/categorynews/8";
        Log.d(Constants.TAG, url);


        FetchFromWeb.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CricketNews cricketNews = new CricketNews(jsonObject.getString("item_id"),jsonObject.getString("featured_image"),
                                jsonObject.getString("main_news_url"),jsonObject.getString("title"),
                                jsonObject.getString("datetime"),"kalerkantho","কালের কণ্ঠ");
                        cricketNewses.add(cricketNews);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Collections.sort(cricketNewses);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.d(Constants.TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });

        url = "http://www.bd-pratidin.com/api/categorynews/9";
        Log.d(Constants.TAG, url);

        FetchFromWeb.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CricketNews cricketNews = new CricketNews(jsonObject.getString("item_id"),jsonObject.getString("featured_image"),
                                jsonObject.getString("main_news_url"),jsonObject.getString("title"),
                                jsonObject.getString("datetime"),"bdprotidin","বাংলাদেশ প্রতিদিন");
                        cricketNewses.add(cricketNews);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(cricketNewses);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.d(Constants.TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Constants.ONE_PLUS_TEST_DEVICE)
                .addTestDevice(Constants.XIAOMI_TEST_DEVICE).build();
        adView.loadAd(adRequest);
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        protected TextView headline;
        protected TextView author;
        protected TextView time;
        protected CircleImageView circleImageView;

        public NewsViewHolder(View itemView) {
            super(itemView);

            headline = ViewHolder.get(itemView, R.id.tv_headline);
            author = ViewHolder.get(itemView, R.id.tv_author);
            time = ViewHolder.get(itemView, R.id.tv_times_ago);
            circleImageView = ViewHolder.get(itemView, R.id.civ_news_thumb);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}