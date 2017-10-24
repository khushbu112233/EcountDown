package com.aipxperts.ecountdown.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.SearchImageCustomAdapter;
import com.aipxperts.ecountdown.Interface.OnImageClick;
import com.aipxperts.ecountdown.Model.MediaModel;
import com.aipxperts.ecountdown.Network.ApiClient;
import com.aipxperts.ecountdown.Network.ApiInterface;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.GoogleSearchLayoutBinding;
import com.aipxperts.ecountdown.utils.ConnectionDetector;
import com.aipxperts.ecountdown.utils.Pref;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by aipxperts-ubuntu-01 on 13/9/17.
 */

public class GoogleSearchFragment extends Fragment {


    GoogleSearchLayoutBinding mBinding;
    View rootView;
    Context context;
    String query;
    ArrayList<MediaModel> img_list=new ArrayList<>();
    SearchImageCustomAdapter searchImageCustomAdapter;
    boolean isHavingMoreData =true;
    int limit=15;
    int offset=1;
    ConnectionDetector cd;
    OnImageClick onImageClick;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.google_search_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=  new ConnectionDetector(context);
        preview();
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            query = bundle.getString("Event_name", "");

        }
        onImageClick = new OnImageClick() {
            @Override
            public void OnImageClick(int position, final String url) {

                if(!url.equalsIgnoreCase("")) {
                    Pref.setValue(context,"url",url);
                    img_list.clear();
                    ((FragmentActivity)context).getSupportFragmentManager().popBackStack();

                }
            }
        };
        mBinding.edtSearch.setText(query);
        searchImageCustomAdapter = new SearchImageCustomAdapter(context,img_list);
        mBinding.gridImg.setAdapter(searchImageCustomAdapter);
        searchImageCustomAdapter.onImageClick(onImageClick);

        callApiService();


        //set default adapter


        mBinding.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    query=textView.getText().toString();
                    img_list.clear();
                    if (textView != null) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                    if (cd.isConnectingToInternet()) {

                        if (mBinding.edtSearch.getText().toString().trim().length() > 0) {
                            callApiService();
                        } else {
                            Toast.makeText(context, "Please enter text to search.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        mBinding.gridImg.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (cd.isConnectingToInternet()) {
                    if (mBinding.gridImg.getLastVisiblePosition() == (searchImageCustomAdapter.getCount() - 1)) {

                        int first = mBinding.gridImg.getFirstVisiblePosition();
                        int count = mBinding.gridImg.getChildCount();

                        if (scrollState == SCROLL_STATE_IDLE && first + count == searchImageCustomAdapter.getCount() && isHavingMoreData) {
                            offset = offset + limit;
                            callApiService();
                        }
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

        });



        return rootView;
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        return MoveAnimation.create(MoveAnimation.LEFT, enter, 500);

    }
    private void preview() {
      /*  ((DashBoardActivity) context).mBinding.footer.imgCalendar.setImageResource(R.mipmap.calendar_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgCountDown.setImageResource(R.mipmap.countdown_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgFb.setImageResource(R.mipmap.user_profile);
        ((DashBoardActivity) context).mBinding.footer.imgSettings.setImageResource(R.mipmap.setting_deselect);
    */
        ((DashBoardActivity) context).mBinding.includeHeader.txtTitle.setText("Global Search");
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setVisibility(View.VISIBLE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgDrawer.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.txtDone.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.GONE);

        if (Pref.getValue(context, "add_display", "").equalsIgnoreCase("0")) {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.GONE);

        } else {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.VISIBLE);

        }
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                img_list.clear();
                getActivity().getSupportFragmentManager().popBackStack();


            }
        });

    }
    public void callApiService() {
        final ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        // Set progressdialog title
        mProgressDialog.setTitle("Please wait");
        // Set progressdialog message
        mProgressDialog.setIndeterminate(false);
        // Show progressdialog
        mProgressDialog.show();
        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        Call<ResponseBody> call = apiService.peticularGroup(""+limit,""+offset, query);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                mProgressDialog.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getString("status").equalsIgnoreCase("success"))
                    {
                        JSONObject jsonObject0 = jsonObject.getJSONObject("data");
                        JSONObject jsonObject1 = jsonObject0.getJSONObject("result");
                        JSONArray jsonArray = jsonObject1.getJSONArray("items");

                        MediaModel[] media_model=new MediaModel[jsonArray.length()];
                        if(jsonArray.length()==0)
                        {
                            isHavingMoreData=false;
                            mProgressDialog.dismiss();
                        }
                        for (int i=0;i<jsonArray.length();i++)
                        {

                            media_model[i]=new MediaModel();
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            String title=  jsonObject2.optString("title");
                            String media = jsonObject2.optString("media");
                            String _id = jsonObject2.optString("_id");

                            media_model[i].title = title;
                            media_model[i].media = media;
                            media_model[i]._id = _id;
                            img_list.add(media_model[i]);


                        }

                        searchImageCustomAdapter.notifyDataSetChanged();
                        if(img_list.size()>0)
                        {
                            mBinding.gridImg.setVisibility(View.VISIBLE);
                            mBinding.txtMsg.setVisibility(View.GONE);
                        }else
                        {

                            mBinding.gridImg.setVisibility(View.GONE);
                            mBinding.txtMsg.setVisibility(View.VISIBLE);
                            mBinding.txtMsg.setText("No records yet!");
                        }


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

              /*  if (response != 400) {
                    ArrayList<ResultModel> griupList = new ArrayList<ResultModel>();


                }*/

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("VVV", "Failuar : " + t.toString());
            }
        });
    }
}
