package com.woniukeji.jianguo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2016/7/22.
 */
public class CityCategoryBase implements Parcelable {
    /**
     * name : 礼仪模特
     * id : 1
     */

    private List<TypeListBean> type_list;
    /**
     * code : 0899
     * cityName : 三亚
     * areaList : [{"areaName":"市辖区","id":1,"city_id":"0899"},{"areaName":"三亚湾","id":2,"city_id":"0899"},{"areaName":"海棠湾","id":3,"city_id":"0899"},{"areaName":"清水湾","id":4,"city_id":"0899"},{"areaName":"大东海","id":5,"city_id":"0899"},{"areaName":"凤凰岛","id":6,"city_id":"0899"},{"areaName":"吉阳镇","id":7,"city_id":"0899"},{"areaName":"田独镇","id":8,"city_id":"0899"},{"areaName":"崖城","id":9,"city_id":"0899"},{"areaName":"育才","id":10,"city_id":"0899"},{"areaName":"天涯","id":11,"city_id":"0899"},{"areaName":"其他","id":12,"city_id":"0899"}]
     * id : 1
     */

    private List<CityListBean> city_list;


    protected CityCategoryBase(Parcel in) {
        type_list = in.createTypedArrayList(TypeListBean.CREATOR);
        city_list = in.createTypedArrayList(CityListBean.CREATOR);
    }

    public static final Creator<CityCategoryBase> CREATOR = new Creator<CityCategoryBase>() {
        @Override
        public CityCategoryBase createFromParcel(Parcel in) {
            return new CityCategoryBase(in);
        }

        @Override
        public CityCategoryBase[] newArray(int size) {
            return new CityCategoryBase[size];
        }
    };

    public List<TypeListBean> getType_list() {
        return type_list;
    }

    public void setType_list(List<TypeListBean> type_list) {
        this.type_list = type_list;
    }




    public List<CityListBean> getCity_list() {
        return city_list;
    }

    public void setCity_list(List<CityListBean> city_list) {
        this.city_list = city_list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(type_list);
        dest.writeTypedList(city_list);
    }


    public static class TypeListBean implements Parcelable{
        private String name;
        private int id;
        private boolean isSelect;

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public TypeListBean(Parcel in) {
            name = in.readString();
            id = in.readInt();
        }
        public TypeListBean() {
        }
        public static final Creator<TypeListBean> CREATOR = new Creator<TypeListBean>() {
            @Override
            public TypeListBean createFromParcel(Parcel in) {
                return new TypeListBean(in);
            }

            @Override
            public TypeListBean[] newArray(int size) {
                return new TypeListBean[size];
            }
        };

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeInt(id);
        }
    }



    public static class CityListBean implements Parcelable{
        private String code;
        private String cityName;
        private int id;
        /**
         * areaName : 市辖区
         * id : 1
         * city_id : 0899
         */

        private List<AreaListBean> areaList;

        protected CityListBean(Parcel in) {
            code = in.readString();
            cityName = in.readString();
            id = in.readInt();
        }

        public static final Creator<CityListBean> CREATOR = new Creator<CityListBean>() {
            @Override
            public CityListBean createFromParcel(Parcel in) {
                return new CityListBean(in);
            }

            @Override
            public CityListBean[] newArray(int size) {
                return new CityListBean[size];
            }
        };

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<AreaListBean> getAreaList() {
            return areaList;
        }

        public void setAreaList(List<AreaListBean> areaList) {
            this.areaList = areaList;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(code);
            dest.writeString(cityName);
            dest.writeInt(id);
        }

        public static class AreaListBean {
            private String areaName;
            private int id;
            private String city_id;

            public String getAreaName() {
                return areaName;
            }

            public void setAreaName(String areaName) {
                this.areaName = areaName;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCity_id() {
                return city_id;
            }

            public void setCity_id(String city_id) {
                this.city_id = city_id;
            }
        }
    }
}
