package com.woniukeji.jianguo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/1.
 */

public class BindInfo implements Parcelable{
        /**
         * entity : {"id":2,"user_id":800630391256780800,"type":0,"name":null,"number":null,"receive_name":null,"pay_password":"E10ADC3949BA59ABBE56E057F20F883E"}
         * type : 0
         */
        private EntityBean entity;
        private int type;

    protected BindInfo(Parcel in) {
        entity = in.readParcelable(EntityBean.class.getClassLoader());
        type = in.readInt();
    }

    public static final Creator<BindInfo> CREATOR = new Creator<BindInfo>() {
        @Override
        public BindInfo createFromParcel(Parcel in) {
            return new BindInfo(in);
        }

        @Override
        public BindInfo[] newArray(int size) {
            return new BindInfo[size];
        }
    };

    public EntityBean getEntity() {
            return entity;
        }

        public void setEntity(EntityBean entity) {
            this.entity = entity;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(entity, flags);
        dest.writeInt(type);
    }

    public static class EntityBean implements Parcelable{
            /**
             * id : 2
             * user_id : 800630391256780800
             * type : 0
             * name : null
             * number : null
             * receive_name : null
             * pay_password : E10ADC3949BA59ABBE56E057F20F883E
             */

            private long id;
            private long user_id;
            private int type;
            private String name;
            private String number;
            private String receive_name;
            private String pay_password;

            protected EntityBean(Parcel in) {
                id = in.readLong();
                user_id = in.readLong();
                type = in.readInt();
                name = in.readString();
                number = in.readString();
                receive_name = in.readString();
                pay_password = in.readString();
            }

            public static final Creator<EntityBean> CREATOR = new Creator<EntityBean>() {
                @Override
                public EntityBean createFromParcel(Parcel in) {
                    return new EntityBean(in);
                }

                @Override
                public EntityBean[] newArray(int size) {
                    return new EntityBean[size];
                }
            };

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getUser_id() {
                return user_id;
            }

            public void setUser_id(long user_id) {
                this.user_id = user_id;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getReceive_name() {
                return receive_name;
            }

            public void setReceive_name(String receive_name) {
                this.receive_name = receive_name;
            }

            public String getPay_password() {
                return pay_password;
            }

            public void setPay_password(String pay_password) {
                this.pay_password = pay_password;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeLong(id);
                dest.writeLong(user_id);
                dest.writeInt(type);
                dest.writeString(name);
                dest.writeString(number);
                dest.writeString(receive_name);
                dest.writeString(pay_password);
            }
        }
}
