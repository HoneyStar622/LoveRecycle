package com.example.loverecycle.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.loverecycle.R;

public class MyFragment extends Fragment {


    private FragmentManager fManager;
    //private ArrayList<Data> datas;
    private ListView my_list;
    private TextView name;
    private View view;

    public MyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my,container,false);
        name = (TextView) view.findViewById(R.id.Name);
        return view;
    }


}