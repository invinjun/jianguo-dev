package com.woniukeji.jianguo.entity;

/**
 * Created by Administrator on 2016/11/14.
 */

public class NewUser {
    /**
     * resume : 0
     * createTime : 0
     * tel : 18101050625
     * auth_status : 1
     * id : 798021483296067584
     * type : 2
     * status : 1
     * token : Nzk4MDIxNDgzMjk2MDY3NTg0Nl8xM0E0OEI0RjRDREUwQjY4QzcxMDE5MDc4RjEzQjcwNQ==
     */
        private int resume; //0 未填写 1 已经填写
        private int createTime;
        private String tel;
        private int auth_status;//认证状态
        private String id;
        private int type;
        private int status;
        private String token;
        String qiniu_token;
        private String head_img_url;

    public String getHead_img_url() {
        return head_img_url;
    }

    public void setHead_img_url(String head_img_url) {
        this.head_img_url = head_img_url;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String nickName;

    public void setQiniu_token(String qiniu_token) {
        this.qiniu_token = qiniu_token;
    }

    public int getResume() {
            return resume;
        }

        public void setResume(int resume) {
            this.resume = resume;
        }

        public int getCreateTime() {
            return createTime;
        }

        public void setCreateTime(int createTime) {
            this.createTime = createTime;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public int getAuth_status() {
            return auth_status;
        }

        public void setAuth_status(int auth_status) {
            this.auth_status = auth_status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

    public String getQiniu_token() {
        return qiniu_token;
    }
}
