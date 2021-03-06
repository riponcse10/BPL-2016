package com.allgames.sportslab.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allgames.sportslab.R;
import com.allgames.sportslab.adapter.BasicListAdapter;
import com.allgames.sportslab.model.RecordDetailsModel1;
import com.allgames.sportslab.model.RecordDetailsModel2;
import com.allgames.sportslab.model.RecordDetailsModel3;

import java.util.ArrayList;

/**
 * @author Ripon
 */
public class RecordsDetailsFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    public void setUpSixElements(final ArrayList<RecordDetailsModel1> recordDetailsModel1s) {
        recyclerView.setAdapter(new BasicListAdapter<RecordDetailsModel1, SixElementViewHolder>(recordDetailsModel1s) {
            @Override
            public SixElementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_six_elements, parent, false);
                return new SixElementViewHolder(view);
            }

            @Override
            public void onBindViewHolder(SixElementViewHolder holder, int position) {
                holder.tv1.setText(recordDetailsModel1s.get(position).getString1());
                holder.tv2.setText(recordDetailsModel1s.get(position).getString2());
                holder.tv3.setText(recordDetailsModel1s.get(position).getString3());
                holder.tv4.setText(recordDetailsModel1s.get(position).getString4());
                holder.tv5.setText(recordDetailsModel1s.get(position).getString5());
                holder.tv6.setText(recordDetailsModel1s.get(position).getString6());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setUpFourElements(final ArrayList<RecordDetailsModel2> recordDetailsModel2s) {
        recyclerView.setAdapter(new BasicListAdapter<RecordDetailsModel2, FourElementViewHolder>(recordDetailsModel2s) {
            @Override
            public FourElementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_four_element, parent, false);
                return new FourElementViewHolder(view);
            }

            @Override
            public void onBindViewHolder(FourElementViewHolder holder, int position) {
                holder.tv1.setText(recordDetailsModel2s.get(position).getString1());
                holder.tv2.setText(recordDetailsModel2s.get(position).getString2());
                holder.tv3.setText(recordDetailsModel2s.get(position).getString3());
                holder.tv4.setText(recordDetailsModel2s.get(position).getString4());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setUpThreeElements(final ArrayList<RecordDetailsModel3> recordDetailsModel3s) {
        recyclerView.setAdapter(new BasicListAdapter<RecordDetailsModel3, ThreeElementViewHolder>(recordDetailsModel3s) {
            @Override
            public ThreeElementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_three_elements, parent, false);
                return new ThreeElementViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ThreeElementViewHolder holder, int position) {
                holder.tv1.setText(recordDetailsModel3s.get(position).getString1());
                holder.tv2.setText(recordDetailsModel3s.get(position).getString2());
                holder.tv3.setText(recordDetailsModel3s.get(position).getString3());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private static class SixElementViewHolder extends RecyclerView.ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        TextView tv5;
        TextView tv6;

        SixElementViewHolder(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv_str1);
            tv2 = itemView.findViewById(R.id.tv_str2);
            tv3 = itemView.findViewById(R.id.tv_str3);
            tv4 = itemView.findViewById(R.id.tv_str4);
            tv5 = itemView.findViewById(R.id.tv_str5);
            tv6 = itemView.findViewById(R.id.tv_str6);
        }
    }

    private static class FourElementViewHolder extends RecyclerView.ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;

        FourElementViewHolder(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv_str1);
            tv2 = itemView.findViewById(R.id.tv_str2);
            tv3 = itemView.findViewById(R.id.tv_str3);
            tv4 = itemView.findViewById(R.id.tv_str4);
        }
    }

    private static class ThreeElementViewHolder extends RecyclerView.ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;

        ThreeElementViewHolder(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv_str1);
            tv2 = itemView.findViewById(R.id.tv_str2);
            tv3 = itemView.findViewById(R.id.tv_str3);

        }
    }
}
