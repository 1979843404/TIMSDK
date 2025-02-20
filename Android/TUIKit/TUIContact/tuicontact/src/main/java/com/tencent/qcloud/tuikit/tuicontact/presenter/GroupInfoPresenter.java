package com.tencent.qcloud.tuikit.tuicontact.presenter;

import android.text.TextUtils;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.interfaces.TUIValueCallback;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.timcommon.bean.UserBean;
import com.tencent.qcloud.tuikit.timcommon.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactConstants;
import com.tencent.qcloud.tuikit.tuicontact.TUIContactService;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuicontact.bean.GroupMemberInfo;
import com.tencent.qcloud.tuikit.tuicontact.interfaces.GroupEventListener;
import com.tencent.qcloud.tuikit.tuicontact.interfaces.IGroupMemberLayout;
import com.tencent.qcloud.tuikit.tuicontact.model.GroupInfoProvider;
import com.tencent.qcloud.tuikit.tuicontact.util.TUIContactLog;
import com.tencent.qcloud.tuikit.tuicontact.util.ContactUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoPresenter {
    public static final String TAG = GroupInfoPresenter.class.getSimpleName();

    private final IGroupMemberLayout layout;
    private final GroupInfoProvider provider;

    private GroupInfo groupInfo;
    private GroupMemberInfo selfGroupMemberInfo;

    private GroupEventListener groupEventListener;

    public GroupInfoPresenter(IGroupMemberLayout layout) {
        this.layout = layout;
        provider = new GroupInfoProvider();
    }

    public void setGroupEventListener() {
        groupEventListener = new GroupEventListener() {
            @Override
            public void onGroupInfoChanged(String groupID) {
                GroupInfoPresenter.this.onGroupInfoChanged(groupID);
            }

            @Override
            public void onGroupMemberCountChanged(String groupID) {
                GroupInfoPresenter.this.onGroupCountChanged(groupID);
            }
        };
        TUIContactService.getInstance().addGroupEventListener(groupEventListener);
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public void loadGroupInfo(String groupId) {
        loadGroupInfo(groupId, GroupInfo.GROUP_MEMBER_FILTER_ALL);
    }

    public void loadGroupInfo(String groupId, int filter) {
        provider.loadGroupInfo(groupId, filter, new IUIKitCallback<GroupInfo>() {
            @Override
            public void onSuccess(GroupInfo data) {
                groupInfo = data;
                layout.onGroupInfoChanged(data);

                String conversationId = ContactUtils.getConversationIdByUserId(groupId, true);
                V2TIMManager.getConversationManager().getConversation(conversationId, new V2TIMValueCallback<V2TIMConversation>() {
                    @Override
                    public void onSuccess(V2TIMConversation v2TIMConversation) {
                        boolean isTop = v2TIMConversation.isPinned();
                        groupInfo.setTopChat(isTop);

                        List<Long> markList = v2TIMConversation.getMarkList();
                        if (markList.contains(V2TIMConversation.V2TIM_CONVERSATION_MARK_TYPE_FOLD)) {
                            groupInfo.setFolded(true);
                        }

                        layout.onGroupInfoChanged(groupInfo);
                        loadGroupMemberList(groupInfo, filter);
                    }

                    @Override
                    public void onError(int code, String desc) {
                        loadGroupMemberList(groupInfo, filter);
                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIContactLog.e("loadGroupInfo", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    private void loadGroupMemberList(GroupInfo groupInfo, int filter) {
        provider.loadGroupMembers(groupInfo, filter, 0, new IUIKitCallback<GroupInfo>() {
            @Override
            public void onSuccess(GroupInfo data) {
                layout.onGroupMemberListChanged(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIContactLog.e("loadGroupMembers", errCode + ":" + errMsg);
            }
        });
    }

    private void onGroupInfoChanged(String groupID) {
        if (groupInfo != null && TextUtils.equals(groupID, groupInfo.getId())) {
            loadGroupInfo(groupID);
        }
    }

    private void onGroupCountChanged(String groupID) {
        if (groupInfo != null && TextUtils.equals(groupID, groupInfo.getId())) {
            loadGroupInfo(groupID);
        }
    }

    public void getGroupMembers(GroupInfo groupInfo, final IUIKitCallback<GroupInfo> callBack) {
        provider.loadGroupMembers(groupInfo, groupInfo.getNextSeq(), new IUIKitCallback<GroupInfo>() {
            @Override
            public void onSuccess(GroupInfo data) {
                if (layout != null) {
                    layout.onGroupMemberListChanged(data);
                }
                ContactUtils.callbackOnSuccess(callBack, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIContactLog.e("loadGroupInfo", errCode + ":" + errMsg);
                ContactUtils.callbackOnError(callBack, errCode, errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public void modifyGroupName(final String name) {
        if (groupInfo == null) {
            return;
        }
        provider.modifyGroupInfo(groupInfo, name, TUIContactConstants.Group.MODIFY_GROUP_NAME, new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                layout.onGroupInfoModified(name, TUIContactConstants.Group.MODIFY_GROUP_NAME);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIContactLog.e("modifyGroupName", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public void modifyGroupNotice(final String notice) {
        if (groupInfo == null) {
            return;
        }
        provider.modifyGroupInfo(groupInfo, notice, TUIContactConstants.Group.MODIFY_GROUP_NOTICE, new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                layout.onGroupInfoModified(notice, TUIContactConstants.Group.MODIFY_GROUP_NOTICE);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIContactLog.e("modifyGroupNotice", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public String getNickName() {
        String nickName = "";
        GroupMemberInfo self = getSelfGroupMemberInfo();
        if (self != null) {
            nickName = self.getNameCard();
        }
        return nickName == null ? "" : nickName;
    }

    public void modifyMyGroupNickname(final String nickname) {
        provider.modifyMyGroupNickname(groupInfo, nickname, new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                layout.onGroupInfoModified(nickname, TUIContactConstants.Group.MODIFY_MEMBER_NAME);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIContactLog.e("modifyMyGroupNickname", errCode + ":" + errMsg);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    public void deleteGroup(IUIKitCallback<Void> callback) {
        if (groupInfo == null) {
            return;
        }
        String groupId = groupInfo.getId();
        provider.deleteGroup(groupId, new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                ContactUtils.callbackOnSuccess(callback, null);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIContactLog.e("deleteGroup", errCode + ":" + errMsg);
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
            }
        });
    }

    public void setTopConversation(String groupId, boolean isSetTop, IUIKitCallback<Void> callback) {
        String conversationId = ContactUtils.getConversationIdByUserId(groupId, true);
        provider.setTopConversation(conversationId, isSetTop, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                groupInfo.setTopChat(isSetTop);
                ContactUtils.callbackOnSuccess(callback, null);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
            }
        });
    }

    public void quitGroup(IUIKitCallback<Void> callback) {
        if (groupInfo == null) {
            return;
        }
        String groupId = groupInfo.getId();
        provider.quitGroup(groupId, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                ContactUtils.callbackOnSuccess(callback, null);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
                TUIContactLog.e("quitGroup", errCode + ":" + errMsg);
            }
        });
    }

    public void modifyGroupInfo(int value, int type) {
        if (groupInfo == null) {
            return;
        }
        provider.modifyGroupInfo(groupInfo, value, type, new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                layout.onGroupInfoModified(data, TUIContactConstants.Group.MODIFY_GROUP_JOIN_TYPE);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("modifyGroupInfo fail :" + errCode + "=" + errMsg);
            }
        });
    }

    private void inviteGroupMembers(List<String> addMembers, IUIKitCallback<Object> callback) {
        provider.inviteGroupMembers(groupInfo, addMembers, new IUIKitCallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                ContactUtils.callbackOnSuccess(callback, data);
                if (layout != null) {
                    layout.onGroupMemberListChanged(groupInfo);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
            }
        });
    }

    public void inviteGroupMembers(String groupId, List<String> addMembers, IUIKitCallback<Object> callback) {
        provider.loadGroupInfo(groupId, new IUIKitCallback<GroupInfo>() {
            @Override
            public void onSuccess(GroupInfo data) {
                groupInfo = data;
                inviteGroupMembers(addMembers, callback);
                loadGroupMemberList(groupInfo, GroupInfo.GROUP_MEMBER_FILTER_ALL);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
            }
        });
    }

    public void deleteGroupMembers(String groupId, List<String> members, IUIKitCallback<List<String>> callback) {
        provider.removeGroupMembers(groupId, members, callback);
    }

    public void removeGroupMembers(GroupInfo groupInfo, List<GroupMemberInfo> delMembers, IUIKitCallback callBack) {
        List<String> memberAccountList = new ArrayList<>();
        for (GroupMemberInfo memberInfo : delMembers) {
            memberAccountList.add(memberInfo.getUserId());
        }
        provider.removeGroupMembers(groupInfo.getId(), memberAccountList, new IUIKitCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                List<GroupMemberInfo> memberInfoList = groupInfo.getMemberDetails();
                for (int i = 0; i < data.size(); i++) {
                    for (int j = memberInfoList.size() - 1; j >= 0; j--) {
                        if (memberInfoList.get(j).getUserId().equals(data.get(i))) {
                            memberInfoList.remove(j);
                            break;
                        }
                    }
                }
                ContactUtils.callbackOnSuccess(callBack, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callBack, errCode, errMsg);
            }
        });
    }

    public void setGroupNotDisturb(GroupInfo groupInfo, boolean isChecked, IUIKitCallback<Void> callback) {
        if (groupInfo == null) {
            TUIContactLog.e(TAG, "mGroupInfo is NULL");
            return;
        }

        provider.setGroupReceiveMessageOpt(groupInfo.getId(), !isChecked, new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                groupInfo.setMessageReceiveOption(!isChecked);
                ContactUtils.callbackOnSuccess(callback, null);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
            }
        });
    }

    public void setGroupFold(GroupInfo groupInfo, boolean isChecked, IUIKitCallback<Void> callback) {
        if (groupInfo == null) {
            TUIContactLog.e(TAG, "mGroupInfo is NULL");
            return;
        }

        provider.setGroupFold(groupInfo.getId(), isChecked, new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                groupInfo.setFolded(isChecked);
                ContactUtils.callbackOnSuccess(callback, null);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
            }
        });
    }

    public void modifyGroupFaceUrl(String groupId, String faceUrl, IUIKitCallback<Void> callback) {
        provider.modifyGroupFaceUrl(groupId, faceUrl, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                ContactUtils.callbackOnSuccess(callback, null);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ContactUtils.callbackOnError(callback, module, errCode, errMsg);
            }
        });
    }

    public GroupMemberInfo getSelfGroupMemberInfo() {
        if (selfGroupMemberInfo != null) {
            return selfGroupMemberInfo;
        }
        if (groupInfo == null) {
            return null;
        }

        GroupMemberInfo groupMemberInfo = provider.getSelfGroupMemberInfo(groupInfo);
        selfGroupMemberInfo = groupMemberInfo;
        return selfGroupMemberInfo;
    }

    public boolean isAdmin(int memberType) {
        return provider.isAdmin(memberType);
    }

    public boolean isOwner(int memberType) {
        return provider.isOwner(memberType);
    }

    public boolean isSelf(String userId) {
        return provider.isSelf(userId);
    }

    public void transferGroupOwner(String groupId, String userId, IUIKitCallback<Void> callback) {
        provider.transferGroupOwner(groupId, userId, callback);
    }

    public void setGroupManager(String groupId, String userId, IUIKitCallback<Void> callback) {
        provider.setGroupManagerRole(groupId, userId, callback);
    }

    public void setGroupMemberRole(String groupId, String userId, IUIKitCallback<Void> callback) {
        provider.setGroupMemberRole(groupId, userId, callback);
    }

    public void getFriendList(TUIValueCallback<List<UserBean>> callback) {
        provider.getFriendList(callback);
    }

    public void getGroupMembersInfo(String groupID, List<String> userIDs, TUIValueCallback<List<UserBean>> callback) {
        provider.getGroupMembersInfo(groupID, userIDs, callback);
    }

    public void getFriendListInGroup(String groupID, TUIValueCallback<List<UserBean>> callback) {
        getFriendList(new TUIValueCallback<List<UserBean>>() {
            @Override
            public void onSuccess(List<UserBean> userBeans) {
                List<String> userIDs = new ArrayList<>();
                for (UserBean userBean : userBeans) {
                    userIDs.add(userBean.getUserId());
                }
                getGroupMembersInfo(groupID, userIDs, new TUIValueCallback<List<UserBean>>() {
                    @Override
                    public void onSuccess(List<UserBean> groupMembers) {
                        TUIValueCallback.onSuccess(callback, groupMembers);
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        TUIValueCallback.onError(callback, errorCode, errorMessage);
                    }
                });
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                TUIValueCallback.onError(callback, errorCode, errorMessage);
            }
        });
    }
}
