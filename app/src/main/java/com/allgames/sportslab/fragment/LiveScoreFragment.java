package com.allgames.sportslab.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.allgames.sportslab.R;
import com.allgames.sportslab.activity.ActivityMatchDetails;
import com.allgames.sportslab.activity.LiveScore;
import com.allgames.sportslab.adapter.BasicListAdapter;
import com.allgames.sportslab.model.Match;
import com.allgames.sportslab.util.CircleImageView;
import com.allgames.sportslab.util.Constants;
import com.allgames.sportslab.util.DefaultMessageHandler;
import com.allgames.sportslab.util.Dialogs;
import com.allgames.sportslab.util.FetchFromWeb;
import com.allgames.sportslab.util.NetworkService;
import com.allgames.sportslab.util.RecyclerItemClickListener;
import com.allgames.sportslab.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * @author Ripon
 */

public class LiveScoreFragment extends RoboFragment {

    @InjectView(R.id.tv_welcome_text)
    private TextView welcomeText;
    @InjectView(R.id.live_matches)
    private RecyclerView recyclerView;
    @InjectView(R.id.tour_image)
    private ImageView imageView;
    @InjectView(R.id.tv_empty_view)
    private TextView emptyView;

    @Inject
    private ArrayList<Match> datas;
    @Inject
    private NetworkService networkService;

    private Dialogs dialogs;
    private PackageInfo pInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_front_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogs = new Dialogs(getContext());
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String welcomeTextUrl = Constants.WELCOME_TEXT_URL;
        Log.d(Constants.TAG, welcomeTextUrl);

        networkService.fetchWelcomeText(new DefaultMessageHandler(getContext(), true) {
            @Override
            public void onSuccess(Message msg) {
                String string = (String) msg.obj;
                try {
                    final JSONObject response = new JSONObject(string);

                    Constants.SHOW_PLAYER_IMAGE = response.getJSONArray("content").getJSONObject(0).getString("playerimage");
                    welcomeText.setText(Html.fromHtml(response.getJSONArray("content").getJSONObject(0).getString("description")));
                    if (response.getJSONArray("content").getJSONObject(0).getString("clickable").equals("true")) {
                        welcomeText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Uri uriUrl = Uri.parse(String.valueOf(response.getJSONArray("content").getJSONObject(0).getString("link")));
                                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                                    startActivity(launchBrowser);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if (isNetworkAvailable() && response.getJSONArray("content").getJSONObject(0).getString("appimage").equals("true")) {
                        imageView.setVisibility(View.VISIBLE);
                        Picasso.with(getContext())
                                .load(response.getJSONArray("content").getJSONObject(0).getString("appimageurl"))
                                .placeholder(R.drawable.default_image)
                                .into(imageView);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(response.getJSONArray("content").getJSONObject(0).getString("applink"))));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        imageView.setVisibility(View.GONE);
                    }

                    if (!response.getJSONArray("content").getJSONObject(0).getString("checkversion").contains(pInfo.versionName)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setMessage(response.getJSONArray("content").getJSONObject(0).getString("popupmessage"));
                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(response.getJSONArray("content").getJSONObject(0).getString("popuplink"))));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    if (response.getJSONArray("content").getJSONObject(0).getString("showlivescore").equals("false")) {
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        networkService.fetchLiveScoreSource(new DefaultMessageHandler(getContext(), true) {
            @Override
            public void onSuccess(Message msg) {
                String string = (String) msg.obj;
                try {
                    JSONObject response = new JSONObject(string);

                    if (response.getString("msg").equals("Successful")) {
                        final String source = response.getJSONArray("content").getJSONObject(0).getString("scoresource");
                        String url = response.getJSONArray("content").getJSONObject(0).getString("url");
                        if (source.equals("myself") || source.equals("cricinfo") || source.equals("webview")) {
                            recyclerView.setAdapter(new BasicListAdapter<Match, LiveScoreViewHolder>(datas) {
                                @Override
                                public LiveScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_live_score, parent, false);
                                    return new LiveScoreViewHolder(view);
                                }

                                @Override
                                public void onBindViewHolder(LiveScoreViewHolder holder, final int position) {

                                    if (Constants.SHOW_PLAYER_IMAGE.equals("true")) {
                                        Picasso.with(getContext())
                                                .load(Constants.resolveLogo(datas.get(position).getTeam1()))
                                                .placeholder(R.drawable.default_image)
                                                .into(holder.imgteam1);

                                        Picasso.with(getContext())
                                                .load(Constants.resolveLogo(datas.get(position).getTeam2()))
                                                .placeholder(R.drawable.default_image)
                                                .into(holder.imgteam2);
                                    }

                                    holder.textteam1.setText(datas.get(position).getTeam1());
                                    holder.textteam2.setText(datas.get(position).getTeam2());
                                    holder.venue.setText(Html.fromHtml(datas.get(position).getVenue()));
                                    holder.time.setText(datas.get(position).getTime());
                                }
                            });
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                            recyclerView.addOnItemTouchListener(
                                    new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            if (source.equals("webview")) {
                                                Intent intent = new Intent(getContext(), LiveScore.class);
                                                intent.putExtra("url", "http://www.criconly.com/ipl/2013/get__summary.php?id=" + datas.get(position).getMatchId());
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(getContext(), ActivityMatchDetails.class);
                                                intent.putExtra("matchID", datas.get(position).getMatchId());
                                                startActivity(intent);
                                            }

                                        }
                                    })
                            );

                            if (source.equals("cricinfo")) {
                                Log.d(Constants.TAG, url);

                                dialogs.showDialog();

                                FetchFromWeb.get(url, null, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        dialogs.dismissDialog();
                                        try {
                                            Log.d(Constants.TAG, response.toString());
                                            JSONArray jsonArray = response.getJSONArray("items");
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject obj = jsonArray.getJSONObject(i);

                                                datas.add(new Match(obj.getJSONObject("team1").getString("teamName"), obj.getJSONObject("team2").getString("teamName"),
                                                        obj.getString("matchDescription"), "", "", "", obj.getString("matchId")));
                                            }
                                            recyclerView.getAdapter().notifyDataSetChanged();

                                            emptyView.setVisibility(View.GONE);
                                            if (datas.size() == 0) {
                                                emptyView.setVisibility(View.VISIBLE);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        dialogs.dismissDialog();
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        dialogs.dismissDialog();
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else if (source.equals("myself")) {
                                //String url = "http://apisea.xyz/Cricket/apis/v1/fetchMyLiveScores.php?key=bl905577";
                                Log.d(Constants.TAG, url);

                                dialogs.showDialog();
                                FetchFromWeb.get(url, null, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        dialogs.dismissDialog();
                                        try {
                                            Log.d(Constants.TAG, response.toString());
                                            if (response.getString("msg").equals("Successful")) {
                                                JSONArray jsonArray = response.getJSONArray("content");
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject obj = jsonArray.getJSONObject(i);
                                                    datas.add(new Match(obj.getString("team1"), obj.getString("team2"),
                                                            obj.getString("status"), "", "", "", obj.getString("matchId")));
                                                }
                                            }
                                            recyclerView.getAdapter().notifyDataSetChanged();
                                            emptyView.setVisibility(View.GONE);
                                            if (datas.size() == 0) {
                                                emptyView.setVisibility(View.VISIBLE);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        dialogs.dismissDialog();
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        dialogs.dismissDialog();
                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else if (source.equals("webview")) {
                                Log.d(Constants.TAG, url);

                                dialogs.showDialog();

                                FetchFromWeb.get(url, null, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        dialogs.dismissDialog();
                                        try {

                                            JSONArray jsonArray = response.getJSONArray("live");
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject obj = jsonArray.getJSONObject(i);
                                                datas.add(new Match(obj.getString("team1_sname"), obj.getString("team2_sname"),
                                                        obj.getString("result"), "", "", "", obj.getString("match_id")));
                                            }

                                            recyclerView.getAdapter().notifyDataSetChanged();
                                            emptyView.setVisibility(View.GONE);
                                            if (datas.size() == 0) {
                                                emptyView.setVisibility(View.VISIBLE);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d(Constants.TAG, response.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        dialogs.dismissDialog();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        dialogs.dismissDialog();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        dialogs.dismissDialog();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(getContext(), "Please Update App", Toast.LENGTH_SHORT).show();
                        }
                    }

                    Log.d(Constants.TAG, response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Log.d(Constants.TAG, Constants.LIVE_SCORE_SOURCE_URL);

        recyclerView.setNestedScrollingEnabled(false);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private static class LiveScoreViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgteam1;
        CircleImageView imgteam2;
        TextView textteam1;
        TextView textteam2;
        TextView venue;
        TextView time;

        LiveScoreViewHolder(final View itemView) {
            super(itemView);

            imgteam1 = ViewHolder.get(itemView, R.id.civTeam1);
            imgteam2 = ViewHolder.get(itemView, R.id.civTeam2);
            textteam1 = ViewHolder.get(itemView, R.id.tvTeam1);
            textteam2 = ViewHolder.get(itemView, R.id.tvTeam2);
            venue = ViewHolder.get(itemView, R.id.tvVenue);
            time = ViewHolder.get(itemView, R.id.tvTime);
        }
    }
}
