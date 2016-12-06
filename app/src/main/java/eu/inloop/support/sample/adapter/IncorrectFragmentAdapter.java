package eu.inloop.support.sample.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import eu.inloop.support.sample.SampleFragment;

/**
 * Extends {@link FragmentStatePagerAdapter} that fails on {@link #notifyDataSetChanged()}.
 */

public class IncorrectFragmentAdapter extends FragmentStatePagerAdapter {
    private boolean mState = true;

    public IncorrectFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void toggleState() {
        mState = !mState;
        notifyDataSetChanged();
    }

    private String getLabel(int position) {
        switch (position) {
            case 0:
                return "A";
            case 1:
                return mState ? "B" : "C";
            default:
                return mState ? "C" : "B";
        }
    }

    @Override
    public int getItemPosition(Object object) {
        String label = ((SampleFragment) object).getLabel();
        if (label.equals("A")) {
            return 0;
        } else if (label.equals("B")) {
            return mState ? 1 : 2;
        } else {
            return mState ? 2 : 1;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getLabel(position);
    }

    @Override
    public Fragment getItem(int position) {
        return SampleFragment.newInstance(getLabel(position));
    }
}
