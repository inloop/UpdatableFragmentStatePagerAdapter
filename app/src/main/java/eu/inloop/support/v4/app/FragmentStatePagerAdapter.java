package eu.inloop.support.v4.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adammihalik on 05/12/2016.
 */

public abstract class FragmentStatePagerAdapter extends PagerAdapter {

    private static final String TAG = FragmentStatePagerAdapter.class.getSimpleName();

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    private List<Fragment.SavedState> mSavedState;
    private List<Fragment> mFragments;
    private Fragment mCurrentPrimaryItem = null;

    public FragmentStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
        mFragments = buildFragmentsList();
        mSavedState = buildFragmentStatesList();
    }

    /**
     * Return not null and empty {@link List} for storing of {@link android.support.v4.app.Fragment.SavedState}s. Depend on adapter's purpose (only-read, only-insert, insert-delete), developer have to decide which implementation if list should be used. Default is {@link ArrayList}.
     */
    @NonNull
    protected List<Fragment.SavedState> buildFragmentStatesList() {
        return new ArrayList<Fragment.SavedState>();
    }

    /**
     * Return not null and empty {@link List} for storing of initialized {@link Fragment}s. Depend on adapter's purpose (only-read, only-insert, insert-delete), developer have to decide which implementation if list should be used. Default is {@link ArrayList}.
     */
    @NonNull
    protected List<Fragment> buildFragmentsList() {
        return new ArrayList<Fragment>();
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment result = tryGetExistingFragment(position); //performance, if fragment has been initialized before, just return it
        if (result != null) {
            return result;
        }

        Fragment fragment = getItem(position);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Adding item #" + position + ": f=" + fragment);
        }
        Fragment.SavedState fss = tryGetSavedState(position);
        if (fss != null) {
            fragment.setInitialSavedState(fss);
        }
        setupFragmentAsSecondary(fragment);
        addFragmentToList(position, fragment);
        getOrCreateFragmentTransaction().add(container.getId(), fragment);

        return fragment;
    }

    /**
     * Add given {@link Fragment} to fragments list. Ensure, that this fragment will be found on given 'position' anytime (except if fragment will be removed).
     */
    protected void addFragmentToList(int position, @Nullable Fragment fragment) {
        while (mFragments.size() <= position) {
            //TODO: could be more effective???
            mFragments.add(null);
        }
        mFragments.set(position, fragment);
    }

    /**
     * Get existing {@link FragmentTransaction}. If transaction is not initialized, create it.
     */
    @NonNull
    protected FragmentTransaction getOrCreateFragmentTransaction() {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        return mCurTransaction;
    }

    /**
     * Return saved {@link android.support.v4.app.Fragment.SavedState} for given 'position'. If state is not saved, NULL is returned.
     */
    @Nullable
    protected Fragment.SavedState tryGetSavedState(int position) {
        if (position >= 0 && mSavedState.size() > position) {
            return mSavedState.get(position);
        }
        return null;
    }

    /**
     * Return existing {@link Fragment} at defined 'position'. If fragment does not exist, NULL is returned.
     */
    @Nullable
    protected Fragment tryGetExistingFragment(int position) {
        if (position >= 0 && mFragments.size() > position) {
            return mFragments.get(position);
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Removing item #" + position + ": f=" + object + " v=" + fragment.getView());
        }
        addStateOfFragmentToList(position, (fragment.isAdded() ? mFragmentManager.saveFragmentInstanceState(fragment) : null));
        addFragmentToList(position, null);
        getOrCreateFragmentTransaction().remove(fragment);
    }


    protected void addStateOfFragmentToList(int position, Fragment.SavedState state) {
        while (mSavedState.size() <= position) {
            //TODO: could be more effective???
            mSavedState.add(null);
        }
        mSavedState.set(position, state);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                setupFragmentAsSecondary(mCurrentPrimaryItem);
            }
            if (fragment != null) {
                setupFragmentAsPrimary(fragment);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    /**
     * Setup given {@link Fragment} to be ready as primary.
     */
    protected void setupFragmentAsPrimary(Fragment fragment) {
        fragment.setMenuVisibility(true);
        fragment.setUserVisibleHint(true);
    }

    /**
     * Setup given {@link Fragment} to be ready as NOT primary.
     */
    protected void setupFragmentAsSecondary(Fragment fragment) {
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitNowAllowingStateLoss();
            mCurTransaction = null;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i=0; i<mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i=0; i<fss.length; i++) {
                    mSavedState.add((Fragment.SavedState)fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key: keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        f.setMenuVisibility(false);
                        addFragmentToList(index, f);
                    } else {
                        Log.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

}
