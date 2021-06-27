package com.chinh.covidapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private WebView webView;
    private  String Load_url="https://www.healthmap.org/covid-19/";
    private String default_url = "https://data.opendevelopmentmekong.net/odm-short-url/a7eadcb6-61ae-4804-9205-b02696b2c5f3";
    //"""//https://bnonews.com/index.php/2020/02/the-latest-coronavirus-cases/";
    private final static long threshold = 150000;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment India_State_wise_Graphical_View_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_map, container, false);

        webView = v.findViewById(R.id.mbEmbeddedIndiaStateWiseWebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                openDefaultURL();
                super.onReceivedError(view, request, error);
            }
        });
        openURL();

        // Inflate the layout for this fragment
        return v;
    }
    private void openURL() {
        webView.loadUrl(Load_url);
        webView.requestFocus();
    }
    private void openDefaultURL()
    {
        webView.loadUrl(default_url);
        webView.requestFocus();
    }
}
