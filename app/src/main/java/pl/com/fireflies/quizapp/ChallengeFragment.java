package pl.com.fireflies.quizapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Seba on 19.05.2018.
 */

public class ChallengeFragment extends Fragment {
    View v;

    public ChallengeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.challenge_fragment, container, false);
        return v;
    }
}
