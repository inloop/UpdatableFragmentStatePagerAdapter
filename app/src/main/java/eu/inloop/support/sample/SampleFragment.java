package eu.inloop.support.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by adammihalik on 05/12/2016.
 */

public class SampleFragment extends Fragment {
    public static SampleFragment newInstance(String label) {
        Bundle bundle = new Bundle();
        bundle.putString("label", label);
        SampleFragment testFragment = new SampleFragment();
        testFragment.setArguments(bundle);
        return testFragment;
    }

    public String getLabel() {
        return getArguments().getString("label");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_template, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((TextView)view.findViewById(R.id.fragment_label)).setText(getLabel());
    }
}
