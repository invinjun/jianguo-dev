package com.woniukeji.jianguo.entity;

/**
 * Created by invinjun on 2016/3/8.

 用户简历表
 t_user_resume
 id       ID
 login_id 用户登录表关联ID
 nickname 昵称
 name	     姓名
 name_image   头像
 school       学校
 intoschool_date   入学时间
 sex          性别（int型：0=女，1=男）
 height       身高（int型）
 student      学生（int型：0=不是学生，1=是学生）
 birth_date   出生日期
 shoe_size    鞋码
 clothing_size 服装尺码
 sign         个性签名
 label        标签
 */
public class Resume {

        /**
         * qq : 0
         * shoe_size : 0
         * birth_date : 1998-07-16
         * sex : 1
         * is_student : 0
         * b_user_id : 0
         * intoschool_date :
         * school :
         * integral : 0
         * auth_time : 0
         * name : 谢军
         * nickname : 你啊黑哦
         * id : 0
         * credit : 0
         * head_img_url : http://7xlell.com2.z0.glb.qiniucdn.com/android_18101050625_1479890089968
         * height : 167
         */

        private int qq;
        private int shoe_size;
        private String birth_date;
        private int sex;
        private int is_student;
        private int b_user_id;
        private String intoschool_date;
        private String school;
        private int integral;
        private int auth_time;
        private String name;
        private String nickname;
        private int id;
        private int credit;
        private String head_img_url;
        private int height;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getQq() {
            return qq;
        }

        public void setQq(int qq) {
            this.qq = qq;
        }

        public int getShoe_size() {
            return shoe_size;
        }

        public void setShoe_size(int shoe_size) {
            this.shoe_size = shoe_size;
        }

        public String getBirth_date() {
            return birth_date;
        }

        public void setBirth_date(String birth_date) {
            this.birth_date = birth_date;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getIs_student() {
            return is_student;
        }

        public void setIs_student(int is_student) {
            this.is_student = is_student;
        }

        public int getB_user_id() {
            return b_user_id;
        }

        public void setB_user_id(int b_user_id) {
            this.b_user_id = b_user_id;
        }

        public String getIntoschool_date() {
            return intoschool_date;
        }

        public void setIntoschool_date(String intoschool_date) {
            this.intoschool_date = intoschool_date;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public int getAuth_time() {
            return auth_time;
        }

        public void setAuth_time(int auth_time) {
            this.auth_time = auth_time;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCredit() {
            return credit;
        }

        public void setCredit(int credit) {
            this.credit = credit;
        }

        public String getHead_img_url() {
            return head_img_url;
        }

        public void setHead_img_url(String head_img_url) {
            this.head_img_url = head_img_url;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
}
