package com.chinh.covidapp.ui.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chinh.covidapp.MainActivity;
import com.chinh.covidapp.R;
import com.chinh.covidapp.adapter.CommonListAdapter;
import com.chinh.covidapp.api.APIClient;
import com.chinh.covidapp.api.ApiInterface;
import com.chinh.covidapp.model.ConfirmModel;

import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class DeathsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private RecyclerView recyclerView;
    private TextView txtTotal;
    private TextView txtTips;
    private EditText searchEditText;

    ProgressDialog progressBar;

    public static DeathsFragment newInstance(int index) {
        DeathsFragment fragment = new DeathsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_common, container, false);
        txtTotal = root.findViewById(R.id.txt_total);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        searchEditText = root.findViewById(R.id.searchView);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //  textView.setText(s);
                loadDeathsData();
            }
        });

        txtTips = root.findViewById(R.id.txt_help);
        return root;
    }

    public void loadDeathsData() {

            progressBar = new ProgressDialog(getActivity());
            progressBar.setMessage("Please wait !");
            progressBar.show();
        try {
            ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);
            /**
             GET List Resources
             **/
            Call<ConfirmModel> call = apiService.getDeaths();
            call.enqueue(new Callback<ConfirmModel>() {
                @Override
                public void onResponse(Call<ConfirmModel> call, Response<ConfirmModel> response) {
                    progressBar.dismiss();
                    setAdapter(response.body());
                }

                @Override
                public void onFailure(Call<ConfirmModel> call, Throwable t) {
                    call.cancel();

                    // recyclerView.setVisibility(View.GONE);
                    // emptyView.setVisibility(View.GONE);
                    progressBar.dismiss();

                    Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_LONG).show();
        }
    }

    private void setAdapter(final ConfirmModel confirmModel) {
        if (confirmModel.getLocations().size() == 0) {
            Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_LONG).show();
        } else {
            txtTotal.setText("Total : " + confirmModel.getLatest());
            Collections.sort(confirmModel.getLocations(), new Comparator<ConfirmModel.Location>() {
                @Override
                public int compare(ConfirmModel.Location lhs, ConfirmModel.Location rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.getLatest() > rhs.getLatest() ? -1 : (lhs.getLatest() < rhs.getLatest()) ? 1 : 0;
                }
            });

            final CommonListAdapter adapter = new CommonListAdapter(confirmModel.getLocations());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //fragment to activity transition
            txtTips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("list", confirmModel);
                    intent.putExtra("type", "Confirm");
                    startActivity(intent);
                }
            });

        }
    }
}