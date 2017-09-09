package com.allgames.sportslab.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;
import com.allgames.sportslab.R;
import com.allgames.sportslab.adapter.BasicListAdapter;
import com.allgames.sportslab.model.Match;
import com.allgames.sportslab.util.CircleImageView;
import com.allgames.sportslab.util.Constants;
import com.allgames.sportslab.util.DefaultMessageHandler;
import com.allgames.sportslab.util.NetworkService;
import com.allgames.sportslab.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author ripon
 */
@ContentView(R.layout.fixture)
public class FixtureActivity extends CommonActivity {

    @InjectView(R.id.recycler_view)
    private RecyclerView recyclerView;

    @Inject
    private ArrayList<Match> data;

    @InjectView(R.id.adViewFixture)
    private AdView adView;

    @Inject
    private NetworkService networkService;

    private String series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        series = getIntent().getStringExtra("series");

        recyclerView.setAdapter(new BasicListAdapter<Match, FixtureViewHolder>(data) {
            @Override
            public FixtureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match, parent, false);
                return new FixtureViewHolder(view);
            }

            @Override
            public void onBindViewHolder(FixtureViewHolder holder, int position) {
                if (Constants.SHOW_PLAYER_IMAGE.equals("true")) {
                    holder.imgteam1.setVisibility(View.VISIBLE);
                    holder.imgteam2.setVisibility(View.VISIBLE);

                    Picasso.with(FixtureActivity.this)
                            .load(Constants.resolveLogo(data.get(position).getTeam1()))
                            .placeholder(R.drawable.default_image)
                            .into(holder.imgteam1);

                    Picasso.with(FixtureActivity.this)
                            .load(Constants.resolveLogo(data.get(position).getTeam2()))
                            .placeholder(R.drawable.default_image)
                            .into(holder.imgteam2);
                } else {
                    holder.imgteam1.setVisibility(View.GONE);
                    holder.imgteam2.setVisibility(View.GONE);
                }




                holder.textteam1.setText(data.get(position).getTeam1());
                holder.textteam2.setText(data.get(position).getTeam2());
                holder.venue.setText(data.get(position).getVenue());

                String timeparts[] = data.get(position).getTime().split("T");
                holder.time.setText(timeparts[0] + "  " + timeparts[1]);
                holder.seriesName.setText(data.get(position).getSeriesName());
                holder.matchNo.setText(data.get(position).getMatchNo());

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (series.equals("fixture")) {
            String url = Constants.FIXTURE_URL;
            Log.d(Constants.TAG, url);

            networkService.fetchFixture(new DefaultMessageHandler(this, true) {
                @Override
                public void onSuccess(Message msg) {
                    String string = (String) msg.obj;
                    try {
                        JSONObject response = new JSONObject(string);

                        String team1, team2, venue, time, seriesName, matcNo;
                        response = response.getJSONObject("query").getJSONObject("results");
                        JSONArray jsonArray = response.getJSONArray("Match");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            JSONArray array = obj.getJSONArray("Team");
                            seriesName = obj.getString("series_name");
                            matcNo = obj.getString("MatchNo");

                            team1 = array.getJSONObject(0).getString("Team");
                            team2 = array.getJSONObject(1).getString("Team");

                            venue = obj.getJSONObject("Venue").getString("content");
                            time = obj.getString("StartDate");
                            Match match = new Match(team1, team2, venue, time, seriesName, matcNo, "");
                            data.add(match);
                        }

                        recyclerView.getAdapter().notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (series.equals("trination")) {
            String url = "http://get.yesdhaka.com/cricket/crickettv/tri_schedule.json";
            Log.d(Constants.TAG, url);

            networkService.fetchTriNationSchedule(new DefaultMessageHandler(this, true) {
                @Override
                public void onSuccess(Message msg) {
                    String string = (String) msg.obj;
                    try {
                        JSONObject response = new JSONObject(string);

                        String team1, team2, venue, time, seriesName, matcNo;

                        JSONArray jsonArray = response.getJSONArray("matches");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            seriesName = "Tri Nation Series";
                            matcNo = "";

                            team1 = obj.getString("team1_name");
                            team2 = obj.getString("team2_name");

                            venue = obj.getString("venue");
                            time = obj.getString("match_date") + "T" + obj.getString("match_time");
                            Match match = new Match(team1, team2, venue, time, seriesName, matcNo, "");
                            data.add(match);
                        }

                        recyclerView.getAdapter().notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (series.equals("championstrophy")) {
            String url = "http://get.yesdhaka.com/cricket/champ_schedule.json";
            Log.d(Constants.TAG, url);

            networkService.fetchCTSchedule(new DefaultMessageHandler(this, true) {
                @Override
                public void onSuccess(Message msg) {
                    String string = (String) msg.obj;
                    try {
                        JSONObject response = new JSONObject(string);

                        String team1, team2, venue, time, seriesName, matcNo;

                        JSONArray jsonArray = response.getJSONArray("matches");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            seriesName = "CT";
                            matcNo = "";

                            team1 = obj.getString("team1_name");
                            team2 = obj.getString("team2_name");

                            venue = obj.getString("venue");
                            time = obj.getString("match_date") + "T" + obj.getString("match_time");
                            Match match = new Match(team1, team2, venue, time, seriesName, matcNo, "");
                            data.add(match);
                        }

                        recyclerView.getAdapter().notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Constants.ONE_PLUS_TEST_DEVICE)
                .addTestDevice(Constants.XIAOMI_TEST_DEVICE).build();
        adView.loadAd(adRequest);
    }

    private static class FixtureViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView imgteam1;
        protected CircleImageView imgteam2;
        protected TextView textteam1;
        protected TextView textteam2;
        protected TextView venue;
        protected TextView time;
        protected TextView seriesName;
        protected TextView matchNo;
        protected LinearLayout linearLayout;

        public FixtureViewHolder(View itemView) {
            super(itemView);

            imgteam1 = ViewHolder.get(itemView, R.id.civTeam1);
            imgteam2 = ViewHolder.get(itemView, R.id.civTeam2);
            textteam1 = ViewHolder.get(itemView, R.id.tvTeam1);
            textteam2 = ViewHolder.get(itemView, R.id.tvTeam2);
            venue = ViewHolder.get(itemView, R.id.tvVenue);
            time = ViewHolder.get(itemView, R.id.tvTime);
            seriesName = ViewHolder.get(itemView, R.id.tvSeriesname);
            matchNo = ViewHolder.get(itemView, R.id.tvMatchNo);
            linearLayout = ViewHolder.get(itemView, R.id.match_layout);
        }
    }
}
