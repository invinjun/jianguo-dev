package com.woniukeji.jianguo.entity;

import java.util.List;

/**
 * Created by invinjun on 2016/3/18.
 */
public class CityBannerEntity {

        /**
         * id : 1
         * image : http://101.200.205.243/banner01.jpg
         * url :
         */

        /**
         * id : 1
         * city : 三亚
         */

        private List<ListTCityEntity> list_t_city;


        public void setList_t_city(List<ListTCityEntity> list_t_city) {
            this.list_t_city = list_t_city;
        }


        public List<ListTCityEntity> getList_t_city() {
            return list_t_city;
        }



        public static class ListTCityEntity {
            private String city;
            private String code;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCity() {
                return city;
            }
        }
}
