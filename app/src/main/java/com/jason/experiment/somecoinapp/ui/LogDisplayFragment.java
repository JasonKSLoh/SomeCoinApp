package com.jason.experiment.somecoinapp.ui;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jason.experiment.somecoinapp.R;

/**
 * LogDisplayFragment
 * Created by jason.
 */
public class LogDisplayFragment extends DialogFragment {

    MainViewModel mainViewModel;
    TextView tvLogData;
    TextView tvDismiss;
    TextView tvClear;

    public LogDisplayFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_log, null);
        builder.setView(view);
        builder.setCancelable(true);
        bindViews(view);
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        bindObservers();
    }


    private void bindViews(View view){
        tvLogData = view.findViewById(R.id.tv_log_data);
        tvDismiss = view.findViewById(R.id.tv_log_dismiss);
        tvDismiss.setOnClickListener(v -> {
            Log.d("+_+", "Dismiss clicked");
            dismiss();
        });
        tvClear = view.findViewById(R.id.tv_log_clear);
        tvClear.setOnClickListener(v -> {
            Log.d("+_+", "Clear logs clicked");
            mainViewModel.clearLogData();
        });
    }

    private void bindObservers(){
        Observer<String> logDataObserver = s -> {
            if(s == null) {
                return;
            } else if(s.isEmpty()) {
                tvLogData.setText(R.string.no_logs_message);
            } else {
                tvLogData.setText(s);
            }
        };
        mainViewModel.getLogData().observe(this, logDataObserver);
    }


}
