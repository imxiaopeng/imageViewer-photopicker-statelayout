package com.example.permission;

import java.util.List;

public class Permissions {

    private List<PermissionsListBean> PermissionsList;

    public List<PermissionsListBean> getPermissionsList() {
        return PermissionsList;
    }

    public void setPermissionsList(List<PermissionsListBean> PermissionsList) {
        this.PermissionsList = PermissionsList;
    }

    public static class PermissionsListBean {
        /**
         * Key : android.permission.ACCESS_CHECKIN_PROPERTIES
         * Level : 0
         * Memo : 允许对检入服务上传的属性进行读/写访问。普通应用程序不能使用此权限。
         * Title : 访问检入属性
         */

        private String Key;
        private int Level;
        private String Memo;
        private String Title;

        public String getKey() {
            return Key;
        }

        public void setKey(String Key) {
            this.Key = Key;
        }

        public int getLevel() {
            return Level;
        }

        public void setLevel(int Level) {
            this.Level = Level;
        }

        public String getMemo() {
            return Memo;
        }

        public void setMemo(String Memo) {
            this.Memo = Memo;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }
    }
}
