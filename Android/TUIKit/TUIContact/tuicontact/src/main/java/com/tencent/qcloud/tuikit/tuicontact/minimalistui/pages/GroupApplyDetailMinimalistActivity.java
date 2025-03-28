package com.tencent.qcloud.tuikit.tuicontact.minimalistui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.tencent.qcloud.tuikit.timcommon.component.TitleBarLayout;
import com.tencent.qcloud.tuikit.timcommon.component.activities.BaseMinimalistLightActivity;
import com.tencent.qcloud.tuikit.timcommon.component.gatherimage.ShadeImageView;
import com.tencent.qcloud.tuikit.timcommon.component.impl.GlideEngine;
import com.tencent.qcloud.tuikit.timcommon.component.interfaces.ITitleBarLayout;
import com.tencent.qcloud.tuikit.timcommon.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.timcommon.util.ScreenUtil;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupApplyInfo;
import com.tencent.qcloud.tuikit.tuicontact.interfaces.IGroupApplyLayout;
import com.tencent.qcloud.tuikit.tuicontact.presenter.GroupApplyPresenter;

public class GroupApplyDetailMinimalistActivity extends BaseMinimalistLightActivity implements IGroupApplyLayout {
    private static final String TAG = GroupApplyDetailMinimalistActivity.class.getSimpleName();

    private TitleBarLayout mTitleBar;
    private GroupApplyPresenter presenter;
    private ShadeImageView avatarView;
    private TextView userNameTv;
    private TextView userIDTv;
    private TextView addWordingTv;
    private TextView agreeButton;
    private TextView rejectButton;
    private TextView resultButton;
    private GroupApplyInfo groupApplyInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_minimalist_new_group_apply_detail_activity);
        Intent intent = getIntent();
        if (intent != null) {
            groupApplyInfo = (GroupApplyInfo) intent.getSerializableExtra(TUIContactConstants.Group.MEMBER_APPLY);
            presenter = new GroupApplyPresenter(this);
            init();
        }
    }

    private void init() {
        mTitleBar = findViewById(R.id.new_apply_detail_title_bar);
        mTitleBar.setTitle(getResources().getString(R.string.group_group_application_detail_title), ITitleBarLayout.Position.MIDDLE);
        mTitleBar.getRightIcon().setVisibility(View.GONE);

        avatarView = findViewById(R.id.avatar);
        avatarView.setRadius(ScreenUtil.dip2px(25));
        GlideEngine.loadImage(avatarView, groupApplyInfo.getFromUserFaceUrl());

        userNameTv = findViewById(R.id.user_name);
        if (TextUtils.isEmpty(groupApplyInfo.getFromUserNickName())) {
            userNameTv.setText(groupApplyInfo.getFromUserID());
        } else {
            userNameTv.setText(groupApplyInfo.getFromUserNickName());
        }
        userIDTv = findViewById(R.id.user_id);
        userIDTv.setText(groupApplyInfo.getFromUserID());

        addWordingTv = findViewById(R.id.validation_message);
        addWordingTv.setText(groupApplyInfo.getAddWording());

        agreeButton = findViewById(R.id.agree);
        rejectButton = findViewById(R.id.reject);
        resultButton = findViewById(R.id.result_tv);

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.acceptApply(groupApplyInfo, new IUIKitCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        agreeButton.setVisibility(View.GONE);
                        rejectButton.setVisibility(View.GONE);
                        resultButton.setVisibility(View.VISIBLE);
                        groupApplyInfo.setStatus(GroupApplyInfo.APPLIED);

                        Intent intent = new Intent();
                        intent.putExtra(TUIContactConstants.Group.MEMBER_APPLY, groupApplyInfo);
                        setResult(RESULT_OK, intent);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        super.onError(module, errCode, errMsg);
                    }
                });
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.refuseApply(groupApplyInfo, new IUIKitCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        agreeButton.setVisibility(View.GONE);
                        rejectButton.setVisibility(View.GONE);
                        resultButton.setVisibility(View.VISIBLE);
                        resultButton.setText(getResources().getString(R.string.refused));
                        resultButton.setTextColor(0xFFFF584C);
                        groupApplyInfo.setStatus(GroupApplyInfo.REFUSED);
                        Intent intent = new Intent();
                        intent.putExtra(TUIContactConstants.Group.MEMBER_APPLY, groupApplyInfo);
                        setResult(RESULT_OK, intent);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        super.onError(module, errCode, errMsg);
                    }
                });
            }
        });
    }
}
