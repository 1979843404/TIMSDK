package com.tencent.qcloud.tuikit.tuicontact.minimalistui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.ContactItemBean;
import com.tencent.qcloud.tuikit.tuicontact.minimalistui.MinimalistUIExtensionObserver;
import com.tencent.qcloud.tuikit.tuicontact.minimalistui.util.MinimalistUIUtils;
import com.tencent.qcloud.tuikit.tuicontact.minimalistui.widget.ContactLayout;
import com.tencent.qcloud.tuikit.tuicontact.minimalistui.widget.ContactListView;
import com.tencent.qcloud.tuikit.tuicontact.presenter.ContactPresenter;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;

public class TUIContactMinimalistFragment extends Fragment {
    private static final String TAG = TUIContactMinimalistFragment.class.getSimpleName();
    private ContactLayout mContactLayout;

    private ContactPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.minimalist_contact_fragment, container, false);
        initViews(baseView);

        return baseView;
    }

    private void initViews(View view) {
        mContactLayout = view.findViewById(R.id.contact_layout);

        presenter = new ContactPresenter();
        presenter.setFriendListListener();
        mContactLayout.setPresenter(presenter);
        mContactLayout.initDefault();

        mContactLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void finishActivity() {
                getActivity().finish();
            }
        });

        mContactLayout.getContactListView().setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                if (position == 0) {
                    Intent intent = new Intent(TUIContactService.getAppContext(), NewFriendApplicationMinimalistActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIContactService.getAppContext().startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(TUIContactService.getAppContext(), GroupListMinimalistActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIContactService.getAppContext().startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(TUIContactService.getAppContext(), BlackListMinimalistActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIContactService.getAppContext().startActivity(intent);
                } else {
                    MinimalistUIUtils.showContactDetails(contact.getId());
                }
            }
        });
    }

    public void reloadData() {
        if (mContactLayout != null) {
            mContactLayout.reloadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TUIContactLog.i(TAG, "onResume");
        mContactLayout.initUI();
    }

    public interface OnClickListener {
        void finishActivity();
    }
}
