package eu.inloop.support.sample.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import eu.inloop.support.sample.SampleFragment;
import eu.inloop.support.v4.app.FragmentStatePagerAdapter;

/**
 * Extends {@link FragmentStatePagerAdapter}, therefore {@link #notifyDataSetChanged()} will not fail.
 */
public class CorrectFragmentAdapter extends FragmentStatePagerAdapter {

    private boolean mState = true;

    public CorrectFragmentAdapter(FragmentManager fragmentManager) {
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
