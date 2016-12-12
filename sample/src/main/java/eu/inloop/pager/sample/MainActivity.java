package eu.inloop.pager.sample;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<Integer> sDataSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (sDataSet == null) {
            sDataSet = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                sDataSet.add(i);
            }
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), sDataSet);
        viewPager.setAdapter(adapter);

        findViewById(R.id.btn_shuffle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.shuffle(sDataSet);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        private final List<Integer> mItems;

        PagerAdapter(FragmentManager fm, List<Integer> items) {
            super(fm);
            mItems = items;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(mItems.get(position));
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public int getItemPosition(Object object) {
            PageFragment item = (PageFragment) object;
            int itemValue = item.getSomeIdentifier();
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).equals(itemValue)) {
                    int position = i;
                    return position;
                }
            }
            return POSITION_NONE;
        }
    }
}
