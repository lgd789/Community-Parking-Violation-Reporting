package kr.ac.mmu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RankListActivity extends AppCompatActivity {
    List<Rank> ranks = new ArrayList<>();
    private RankListAdapter adapterRank;
    private RecyclerView recyclerViewRank;
    private ImageButton buttonRankListBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_list);

        buttonRankListBack = findViewById(R.id.imageButton_rankList_back);
        buttonRankListBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getRanking("-1");

        recyclerViewRank = findViewById(R.id.recyclerView_rankList);
        recyclerViewRank.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRank.setHasFixedSize(true);

        adapterRank = new RankListAdapter(ranks);
        recyclerViewRank.setAdapter(adapterRank);
    }

    private void getRanking(String limit){
        try {
            String messages = new HttpTask().execute("#showRank", limit).get();
            String[] message = messages.split("/");


            for(int i = 0; i < message.length; i++){
                String id = message[i++];
                String point = message[i];
                Rank rank = new Rank(id, point);

                ranks.add(rank);
            }
            Rank.setCount(0);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}