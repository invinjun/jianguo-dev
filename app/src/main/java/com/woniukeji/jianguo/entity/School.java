package com.woniukeji.jianguo.entity;

/**
 * Created by invinjun on 2016/5/3.
 */
public class School {

        /**
         * id : 24
         * city_id : 10
         * name : 中国人民大学
         */

        private int id;
        private int city_id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCity_id() {
            return city_id;
        }

        public void setCity_id(int city_id) {
            this.city_id = city_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
}
