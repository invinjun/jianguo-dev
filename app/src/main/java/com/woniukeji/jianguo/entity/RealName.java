package com.woniukeji.jianguo.entity;

/**
 * Created by invinjun on 2016/3/9.
 * 用户实名表
 t_user_realname
 id           ID
 login_id     用户登录表关联ID
 front_image  正面
 behind_image 反面
 realname     真实姓名
 id_number    身份证号
 sex	     性别
 */
public  class RealName {
        /**
         * IDcard : 152103199009087211
         * sex : 1
         * id : 800630391256780800
         * auth_status : 1
         * type : 1
         * front_img_url :
         * behind_img_url :
         * realname : 谢军
         */

        private String IDcard;
        private int sex;
        private long id;
        private int auth_status;
        private int type;
        private String front_img_url;
        private String behind_img_url;
        private String realname;

        public String getIDcard() {
            return IDcard;
        }

        public void setIDcard(String IDcard) {
            this.IDcard = IDcard;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getAuth_status() {
            return auth_status;
        }

        public void setAuth_status(int auth_status) {
            this.auth_status = auth_status;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getFront_img_url() {
            return front_img_url;
        }

        public void setFront_img_url(String front_img_url) {
            this.front_img_url = front_img_url;
        }

        public String getBehind_img_url() {
            return behind_img_url;
        }

        public void setBehind_img_url(String behind_img_url) {
            this.behind_img_url = behind_img_url;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }
}
