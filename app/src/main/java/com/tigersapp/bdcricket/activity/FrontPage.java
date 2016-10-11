package com.tigersapp.bdcricket.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.batch.android.Batch;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.tigersapp.bdcricket.R;
import com.tigersapp.bdcricket.adapter.BasicListAdapter;
import com.tigersapp.bdcricket.adapter.MatchDetailsViewPagerAdapter;
import com.tigersapp.bdcricket.adapter.SlideShowViewPagerAdapter;
import com.tigersapp.bdcricket.fragment.ChattingFragment;
import com.tigersapp.bdcricket.fragment.FragmentMatchSummary;
import com.tigersapp.bdcricket.fragment.FragmentScoreBoard;
import com.tigersapp.bdcricket.fragment.LiveScoreFragment;
import com.tigersapp.bdcricket.fragment.PlayingXIFragment;
import com.tigersapp.bdcricket.model.Match;
import com.tigersapp.bdcricket.util.CircleImageView;
import com.tigersapp.bdcricket.util.Constants;
import com.tigersapp.bdcricket.util.FetchFromWeb;
import com.tigersapp.bdcricket.util.RecyclerItemClickListener;
import com.tigersapp.bdcricket.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;

public class FrontPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MatchDetailsViewPagerAdapter matchDetailsViewPagerAdapter;
    private ViewPager viewPager;
    AdView adView;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        adView = (AdView) findViewById(R.id.adViewFontPage);
        setSupportActionBar(toolbar);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9201945236996508/3198106475");

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(Constants.ONE_PLUS_TEST_DEVICE).addTestDevice(Constants.XIAOMI_TEST_DEVICE)
                .build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });

        this.viewPager = (ViewPager) findViewById(R.id.viewPager);

        this.viewPager.setOffscreenPageLimit(3);
        setupViewPage(this.viewPager);
        ((TabLayout) findViewById(R.id.tabLayout)).setupWithViewPager(this.viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(Gravity.LEFT);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AdRequest adRequest1 = new AdRequest.Builder().addTestDevice(Constants.ONE_PLUS_TEST_DEVICE)
                .addTestDevice(Constants.XIAOMI_TEST_DEVICE).build();
        adView.loadAd(adRequest1);


    }

    public final void setupViewPage(ViewPager viewPager) {
        this.matchDetailsViewPagerAdapter = new MatchDetailsViewPagerAdapter(getSupportFragmentManager());
        this.matchDetailsViewPagerAdapter.addFragment(new LiveScoreFragment(), "লাইভ স্কোর");
        this.matchDetailsViewPagerAdapter.addFragment(new ChattingFragment(), "চ্যাটিং");
        viewPager.setAdapter(this.matchDetailsViewPagerAdapter);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_live_streaming) {
            String isAllowedUrl = Constants.ACCESS_CHECKER_URL;
            Log.d(Constants.TAG, isAllowedUrl);

            final AlertDialog progressDialog = new SpotsDialog(FrontPage.this, R.style.Custom);
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestParams params = new RequestParams();
            params.add("key", "bl905577");

            FetchFromWeb.get(isAllowedUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        if (response.getString("msg").equals("Successful")) {
                            String source = response.getJSONArray("content").getJSONObject(0).getString("livestream");
                            if (source.equals("true")) {
                                Intent intent = new Intent(FrontPage.this, Highlights.class);
                                intent.putExtra("cause", "livestream");
                                startActivity(intent);
                            } else {
                                Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d(Constants.TAG, response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_LONG).show();
                }
            });

        } else if (id == R.id.nav_sports_news) {
            String isAllowedUrl = Constants.ACCESS_CHECKER_URL;
            Log.d(Constants.TAG, isAllowedUrl);

            final AlertDialog progressDialog = new SpotsDialog(FrontPage.this, R.style.Custom);
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestParams params = new RequestParams();
            params.add("key", "bl905577");

            FetchFromWeb.get(isAllowedUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        if (response.getString("msg").equals("Successful")) {
                            String source = response.getJSONArray("content").getJSONObject(0).getString("news");
                            if (source.equals("true")) {
                                Intent intent = new Intent(FrontPage.this, CricketNewsListActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d(Constants.TAG, response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_LONG).show();
                }
            });
        } else if (id == R.id.nav_highlights) {
            String isAllowedUrl = Constants.ACCESS_CHECKER_URL;
            Log.d(Constants.TAG, isAllowedUrl);

            final AlertDialog progressDialog = new SpotsDialog(FrontPage.this, R.style.Custom);
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestParams params = new RequestParams();
            params.add("key", "bl905577");

            FetchFromWeb.get(isAllowedUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        if (response.getString("msg").equals("Successful")) {
                            String source = response.getJSONArray("content").getJSONObject(0).getString("highlights");
                            if (source.equals("true")) {
                                Intent intent = new Intent(FrontPage.this, Highlights.class);
                                intent.putExtra("cause", "highlights");
                                startActivity(intent);
                            } else {
                                Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d(Constants.TAG, response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_LONG).show();
                }
            });

        } else if (id == R.id.nav_fixture) {
            Intent intent = new Intent(FrontPage.this, FixtureActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_past_matches) {
            Intent intent = new Intent(FrontPage.this, PastMatchesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            String isAllowedUrl = Constants.ACCESS_CHECKER_URL;
            Log.d(Constants.TAG, isAllowedUrl);

            final AlertDialog progressDialog = new SpotsDialog(FrontPage.this, R.style.Custom);
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestParams params = new RequestParams();
            params.add("key", "bl905577");

            FetchFromWeb.get(isAllowedUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        if (response.getString("msg").equals("Successful")) {
                            String source = response.getJSONArray("content").getJSONObject(0).getString("gallery");
                            if (source.equals("true")) {
                                Intent intent = new Intent(FrontPage.this, GalleryActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d(Constants.TAG, response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_LONG).show();
                }
            });


        } else if (id == R.id.nav_series_stats) {
            Intent intent = new Intent(FrontPage.this, SeriesStatsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_ranking) {
            Intent intent = new Intent(FrontPage.this, RankingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_records) {
            Intent intent = new Intent(FrontPage.this, RecordsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_points_table) {
            Intent intent = new Intent(FrontPage.this, PointsTableActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_quotes) {
            String isAllowedUrl = Constants.ACCESS_CHECKER_URL;
            Log.d(Constants.TAG, isAllowedUrl);

            final AlertDialog progressDialog = new SpotsDialog(FrontPage.this, R.style.Custom);
            progressDialog.show();
            progressDialog.setCancelable(true);
            RequestParams params = new RequestParams();
            params.add("key", "bl905577");

            FetchFromWeb.get(isAllowedUrl, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    try {
                        if (response.getString("msg").equals("Successful")) {
                            String source = response.getJSONArray("content").getJSONObject(0).getString("quotes");
                            if (source.equals("true")) {
                                Intent intent = new Intent(FrontPage.this, QuotesListActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        Log.d(Constants.TAG, response.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    Toast.makeText(FrontPage.this, "Failed", Toast.LENGTH_LONG).show();
                }
            });

        } else if (id == R.id.nav_team_profile) {
            Intent intent = new Intent(FrontPage.this, TeamProfile.class);
            startActivity(intent);
        } else if (id == R.id.nav_update_app) {
            String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Batch.onStart(this);
    }

    @Override
    protected void onStop() {
        Batch.onStop(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Batch.onDestroy(this);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Batch.onNewIntent(this, intent);

        super.onNewIntent(intent);
    }


}
