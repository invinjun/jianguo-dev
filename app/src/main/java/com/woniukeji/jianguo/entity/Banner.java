package com.woniukeji.jianguo.entity;

/**
 * Created by Administrator on 2016/11/25.
 */

    public  class Banner {
        private int id;
        private String image;
        private String url;

        public void setId(int id) {
            this.id = id;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public String getUrl() {
            return url;
        }
    }
