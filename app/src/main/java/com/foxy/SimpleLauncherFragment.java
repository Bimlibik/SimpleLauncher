package com.foxy;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleLauncherFragment extends Fragment {

    private static final String TAG = "SimpleLauncherFragment";

    private RecyclerView recyclerView;

    public static SimpleLauncherFragment newInstance() {
        return new SimpleLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_launcher, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return view;
    }

    // получение списка activities  по заданному intent'у от PackageManager'а
    // установка адаптера
    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        //сортировка в алфовитном порядке
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString());
            }
        });

        Log.i(TAG, "Найдено: " + activities.size() + " activities.");

        recyclerView.setAdapter(new ActivityAdapter(activities));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ResolveInfo resolveInfo;
        private TextView nameTextView;
        private ImageView icon;

        public ActivityHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.label);
            icon = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(this);
        }

        public void bind(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = this.resolveInfo.loadLabel(pm).toString();
            nameTextView.setText(appName);
            icon.setImageDrawable(resolveInfo.loadIcon(pm));
        }

        // при нажатии на элемент списка откроется выбранное приложение
        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // при запуске activity запускается новая задача
            startActivity(intent);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private final List<ResolveInfo> activities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            this.activities = activities;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.item_activity, parent, false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = activities.get(position);
            holder.bind(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return activities.size();
        }
    }
}
