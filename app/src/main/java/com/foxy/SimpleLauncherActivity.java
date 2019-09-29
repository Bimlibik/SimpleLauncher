package com.foxy;

import androidx.fragment.app.Fragment;

public class SimpleLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SimpleLauncherFragment.newInstance();
    }
}
