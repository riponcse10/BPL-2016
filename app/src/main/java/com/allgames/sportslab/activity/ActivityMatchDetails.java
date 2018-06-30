package com.allgames.sportslab.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.allgames.sportslab.model.Match;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.allgames.sportslab.R;
import com.allgames.sportslab.adapter.MatchDetailsViewPagerAdapter;
import com.allgames.sportslab.fragment.FragmentCommentry;
import com.allgames.sportslab.fragment.FragmentScoreBoard;
import com.allgames.sportslab.fragment.GossipFragment;
import com.allgames.sportslab.fragment.PlayingXIFragment;
import com.allgames.sportslab.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * @author ripon
 */
@ContentView(R.layout.activity_match_details)
public class ActivityMatchDetails extends CommonActivity {

    @InjectView(R.id.viewPager)
    private ViewPager viewPager;

    @InjectView(R.id.adViewMatchDetails)
    private AdView adView;

    @InjectView(R.id.tabLayout)
    private TabLayout tabLayout;

    private Match match;
    private MatchDetailsViewPagerAdapter matchDetailsViewPagerAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.match = (Match) getIntent().getSerializableExtra("summary");

        viewPager.setOffscreenPageLimit(4);
        setupViewPage(this.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Constants.ONE_PLUS_TEST_DEVICE)
                .addTestDevice(Constants.XIAOMI_TEST_DEVICE).build();
        adView.loadAd(adRequest);
    }


    private void setupViewPage(ViewPager viewPager) {
        this.matchDetailsViewPagerAdapter = new MatchDetailsViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putSerializable("summary", match);

        FragmentScoreBoard fragmentScoreBoard = new FragmentScoreBoard();
        fragmentScoreBoard.setArguments(bundle);
        this.matchDetailsViewPagerAdapter.addFragment(fragmentScoreBoard, "স্কোর বোর্ড");

        FragmentCommentry fragmentCommentry = new FragmentCommentry();
        fragmentCommentry.setArguments(bundle);
        this.matchDetailsViewPagerAdapter.addFragment(fragmentCommentry, "কমেন্ট্রি");

        PlayingXIFragment playingXIFragment = new PlayingXIFragment();
        playingXIFragment.setArguments(bundle);
        this.matchDetailsViewPagerAdapter.addFragment(playingXIFragment, "একাদশ");

        GossipFragment fragment = new GossipFragment();
        fragment.setArguments(bundle);
        this.matchDetailsViewPagerAdapter.addFragment(fragment, "আড্ডা");
        viewPager.setAdapter(this.matchDetailsViewPagerAdapter);
    }
}