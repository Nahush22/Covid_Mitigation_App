package com.example.Covid19App;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class SkillFragment extends Fragment {

    RecyclerView skillList;
    SkillAdapter skillAdapter;

    String skillSet[] = {"Machine Learning","Web Development","Neural Networks","AngularJS","ReactJS","Learning C","Python Development","Learning Java","Deep Learning",
            "Natural Language Processing","Android App Development","Flutter App Development","Blockchain"};

    ArrayList<String> skills = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_skill, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skills = new ArrayList<String>(Arrays.asList(skillSet));

        skillList = view.findViewById(R.id.courseView);
        skillList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        skillAdapter = new SkillAdapter(view.getContext(), skills);
        skillList.setAdapter(skillAdapter);
    }
}
