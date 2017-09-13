package com.aipxperts.ecountdown.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.ListEventAdapter;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.FbFragmentLayoutBinding;
import com.aipxperts.ecountdown.databinding.FragmentDashboardBinding;
import com.aipxperts.ecountdown.utils.Pref;
import com.aipxperts.ecountdown.utils.RealmBackupRestore;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

/**
 * Created by aipxperts-ubuntu-01 on 6/7/17.
 */

public class FbFragment extends Fragment  {
    FbFragmentLayoutBinding mBinding;
    View rootView;
    Context context;
    int PEMISION_REQUEST=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fb_fragment_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();

        preview();




        mBinding.txtBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmBackupRestore realmBackupRestore = new RealmBackupRestore(getActivity());

                realmBackupRestore.backup();
            }
        });
        mBinding.txtRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRunTimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PEMISION_REQUEST);
            }
        });

        mBinding.txtSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                RealmBackupRestore realmBackupRestore = new RealmBackupRestore(getActivity());
                Uri path = Uri.fromFile(realmBackupRestore.dbPath());
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent .setType("vnd.android.cursor.dir/email");
                // the attachment
                emailIntent .putExtra(Intent.EXTRA_STREAM, path);
                // the mail subject
                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(emailIntent , "Send email..."));

            }
        });
        return rootView;
    }

    private void preview() {
        ((DashBoardActivity)context).mBinding.includeHeader.txtTitle.setText("Events");
        ((DashBoardActivity)context).mBinding.includeHeader.imgBack.setVisibility(View.GONE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgDrawer.setVisibility(View.VISIBLE);
        ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setVisibility(View.GONE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.GONE);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean openActivityOnce = true;
        boolean openDialogOnce = true;
        boolean isPermitted = false;

        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];

            isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
// user rejected the permission
                boolean showRationale = shouldShowRequestPermissionRationale(permission);
                if (!showRationale) {
                } else {
                    if (openDialogOnce) {
                        if (requestCode == PEMISION_REQUEST) {
                            Toast.makeText(getActivity(), "Please Grant Permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }

        if (isPermitted) {

            if (requestCode == PEMISION_REQUEST) {
                RealmBackupRestore realmBackupRestore = new RealmBackupRestore(getActivity());

                realmBackupRestore.restore();
            }

        }

    }
    private void checkRunTimePermission(String[] permissionArrays, int REQUEST) {
// String[] permissionArrays = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, REQUEST);
        } else {

            if (REQUEST == PEMISION_REQUEST) {
                RealmBackupRestore realmBackupRestore = new RealmBackupRestore(getActivity());

                realmBackupRestore.restore();
            }


// if already permition granted
// PUT YOUR ACTION (Like Open cemara etc..)
        }
    }

}
