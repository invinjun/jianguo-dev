package com.woniukeji.jianguo.entity;

/**
 * Created by invinjun on 2016/4/20.
 */
public class WageLog {
        /**
         * id : 803873684736315392
         * type : 1
         * user_id : 800630391256780800
         * money : 12
         * source : 1
         * from_id : 801697896259784704
         * note :
         * status : 0
         * pay_type_id : 0
         * createTime : 0
         * walletInfoEntity : null
         * jobEntity : {"id":0,"publisher_id":0,"job_name":"测试报名","auth_name":null,"job_type_id":0,"job_type_name":null,"start_date":1480435200,"end_date":1482508800,"begin_time":1480467600,"end_time":1482570000,"address":null,"type":0,"mode":0,"money":0,"term":"\u0000","limit_sex":0,"count":0,"sum":0,"status":0,"createTime":0,"content":null,"city_id":null,"city_name":null,"area_id":null,"area_name":null,"require":null,"browse_count":0,"json_limit":null,"json_label":null,"json_welfare":null,"set_place":null,"set_time":null,"tel":0,"girl_sum":0,"boy_sum":0,"job_image":"http://7xlell.com2.z0.glb.qiniucdn.com/FjoSbS7xao7g_5W1pZq-txWwP-JT","job_model":null,"model_name":null,"is_model":0,"contact_name":null,"user_count":0}
         */

        private long id;
        private int type;
        private long user_id;
        private int money;
        private long from_id;
        private String note;
        private int status;
        private long pay_type_id;
        private long createTime;
        private JobEntityBean jobEntity;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getUser_id() {
            return user_id;
        }

        public void setUser_id(long user_id) {
            this.user_id = user_id;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public long getFrom_id() {
            return from_id;
        }

        public void setFrom_id(long from_id) {
            this.from_id = from_id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getPay_type_id() {
            return pay_type_id;
        }

        public void setPay_type_id(long pay_type_id) {
            this.pay_type_id = pay_type_id;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public JobEntityBean getJobEntity() {
            return jobEntity;
        }

        public void setJobEntity(JobEntityBean jobEntity) {
            this.jobEntity = jobEntity;
        }

        public static class JobEntityBean {
            /**
             * id : 0
             * publisher_id : 0
             * job_name : 测试报名
             * auth_name : null
             * job_type_id : 0
             * job_type_name : null
             * start_date : 1480435200
             * end_date : 1482508800
             * begin_time : 1480467600
             * end_time : 1482570000
             * address : null
             * type : 0
             * mode : 0
             * money : 0
             * term :  
             * limit_sex : 0
             * count : 0
             * sum : 0
             * status : 0
             * createTime : 0
             * content : null
             * city_id : null
             * city_name : null
             * area_id : null
             * area_name : null
             * require : null
             * browse_count : 0
             * json_limit : null
             * json_label : null
             * json_welfare : null
             * set_place : null
             * set_time : null
             * tel : 0
             * girl_sum : 0
             * boy_sum : 0
             * job_image : http://7xlell.com2.z0.glb.qiniucdn.com/FjoSbS7xao7g_5W1pZq-txWwP-JT
             * job_model : null
             * model_name : null
             * is_model : 0
             * contact_name : null
             * user_count : 0
             */

            private long id;
            private int publisher_id;
            private String job_name;
            private String job_type_name;
            private long start_date;
            private long end_date;
            private int money;
            private int tel;
            private String job_image;
            private String contact_name;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public int getPublisher_id() {
                return publisher_id;
            }

            public void setPublisher_id(int publisher_id) {
                this.publisher_id = publisher_id;
            }

            public String getJob_name() {
                return job_name;
            }

            public void setJob_name(String job_name) {
                this.job_name = job_name;
            }

            public String getJob_type_name() {
                return job_type_name;
            }

            public void setJob_type_name(String job_type_name) {
                this.job_type_name = job_type_name;
            }

            public long getStart_date() {
                return start_date;
            }

            public void setStart_date(long start_date) {
                this.start_date = start_date;
            }

            public long getEnd_date() {
                return end_date;
            }

            public void setEnd_date(long end_date) {
                this.end_date = end_date;
            }

            public int getMoney() {
                return money;
            }

            public void setMoney(int money) {
                this.money = money;
            }

            public int getTel() {
                return tel;
            }

            public void setTel(int tel) {
                this.tel = tel;
            }

            public String getJob_image() {
                return job_image;
            }

            public void setJob_image(String job_image) {
                this.job_image = job_image;
            }

            public String getContact_name() {
                return contact_name;
            }

            public void setContact_name(String contact_name) {
                this.contact_name = contact_name;
            }
        }
}
