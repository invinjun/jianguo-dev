package com.woniukeji.jianguo.db;
/**
 * Created by Administrator on 2016/11/22.
 */
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;

        import com.woniukeji.jianguo.entity.AreaBean;
        import com.woniukeji.jianguo.entity.CityBean;

        import java.util.ArrayList;
        import java.util.List;

/**
 *  CityAreaDao  数据库操作类  dao后缀的都是数据库操作类
 *
 *  我们这里的每一个 增删改查 的方法都通过getWritableDatabase()去实例化了一个数据库,这里必须这么做
 *  不客气抽取 成为一个成员变量, 否则报错,若是觉得麻烦可以通过定义方法来置为null和重新赋值
 *
 *  —— 其实dao类在这里做得事情特别简单：
 *  1、定义一个构造方法，利用这个方法去实例化一个  数据库帮助类
 *  2、编写dao类的对应的 增删改查 方法。
 *
 */
public class CityAreaDao {

    private MyDBHelper mMyDBHelper;

    /**
     * dao类需要实例化数据库Help类,只有得到帮助类的对象我们才可以实例化 SQLiteDatabase
     * @param context
     */
    public CityAreaDao(Context context) {
        mMyDBHelper=new MyDBHelper(context);
    }

    // 将数据库打开帮帮助类实例化，然后利用这个对象
    // 调用谷歌的api去进行增删改查

    // 增加的方法吗，返回的的是一个long值
    public long addCityDate(String cityName,String cityCode){
        // 增删改查每一个方法都要得到数据库，然后操作完成后一定要关闭
        // getWritableDatabase(); 执行后数据库文件才会生成
        // 数据库文件利用DDMS可以查看，在 data/data/包名/databases 目录下即可查看
        SQLiteDatabase sqLiteDatabase =  mMyDBHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put("cityName",cityName);
        contentValues.put("cityCode", cityCode);
        contentValues.put("isSelected", "0");
        // 返回,显示数据添加在第几行
        // 加了现在连续添加了3行数据,突然删掉第三行,然后再添加一条数据返回的是4不是3
        // 因为自增长
        long rowid=sqLiteDatabase.replace("cityinfo",null,contentValues);
        sqLiteDatabase.close();
        return rowid;
    }
    public long addAreaDate(String areaName,String areaCode,String parentCode){
        // 增删改查每一个方法都要得到数据库，然后操作完成后一定要关闭
        // getWritableDatabase(); 执行后数据库文件才会生成
        // 数据库文件利用DDMS可以查看，在 data/data/包名/databases 目录下即可查看
        SQLiteDatabase sqLiteDatabase =  mMyDBHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put("areaName",areaName);
        contentValues.put("areaCode", areaCode);
        contentValues.put("parentCode", parentCode);

        // 返回,显示数据添加在第几行
        // 加了现在连续添加了3行数据,突然删掉第三行,然后再添加一条数据返回的是4不是3
        // 因为自增长
        long rowid=sqLiteDatabase.replace("areainfo",null,contentValues);

        sqLiteDatabase.close();
        return rowid;
    }

    // 删除的方法，返回值是int
    public int deleteDate(String name){
        SQLiteDatabase sqLiteDatabase = mMyDBHelper.getWritableDatabase();
        int deleteResult = sqLiteDatabase.delete("contactinfo", "name=?", new String[]{name});
        sqLiteDatabase.close();
        return deleteResult;
    }

    /**
     * 修改的方法选中城市
     * @param cityCode
     * @return
     */
    public int updateData(String cityCode){
        SQLiteDatabase sqLiteDatabase = mMyDBHelper.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put("isSelected", "1");
        int updateResult = sqLiteDatabase.update("cityinfo", contentValues, "cityCode=?", new String[]{cityCode});
        contentValues.put("isSelected", "0");
        int updateResult1 = sqLiteDatabase.update("cityinfo", contentValues, "cityCode!=?", new String[]{cityCode});
        sqLiteDatabase.close();
        return updateResult;
    }
    /**
     * 查询是否设置过
     * @return
     */
    public int queryData(){
        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query("cityinfo", new String[]{"cityName","cityCode"}, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        readableDatabase.close();
        return  count;
    }
    /**
     * 查询的方法（查找城市city）
     * @return
     */
    public List<CityBean> alterCityDate(){
        List<CityBean> list = new ArrayList<>();
        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
        // 查询比较特别,涉及到 cursor
        Cursor cursor = readableDatabase.query("cityinfo", new String[]{"cityName","cityCode"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            CityBean city=new CityBean();
            String name=cursor.getString(0);
            String code=cursor.getString(1);
            city.setCityName(name);
            city.setCode(code);
            list.add(city);
        }
        cursor.close(); // 记得关闭 corsor
        readableDatabase.close(); // 关闭数据库
        return list;
    }

    /**
     * 查询被选中的城市
     * @return
     */
    public CityBean queryCitySelected(){
        CityBean city=new CityBean();
        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
        // 查询比较特别,涉及到 cursor
        Cursor cursor = readableDatabase.query("cityinfo", new String[]{"cityName","cityCode"},"isSelected=?",new String[]{"1"}, null, null, null);
        while(cursor.moveToNext()){
            String name=cursor.getString(0);
            String code=cursor.getString(1);
            city.setCityName(name);
            city.setCode(code);
        }
        cursor.close(); // 记得关闭 corsor
        readableDatabase.close(); // 关闭数据库
        return city;
    }
    /**
     * 查询地区
     * @param cityCode
     * @return
     */
    public List<AreaBean> alterAreaDate(String cityCode){
        List<AreaBean> beanList = new ArrayList<>();

        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
        // 查询比较特别,涉及到 cursor
        Cursor cursor = readableDatabase.query("areainfo", new String[]{"areaName","areaCode"}, "parentCode=?", new String[]{cityCode}, null, null, null);
        while(cursor.moveToNext()){
            AreaBean areaBean=new AreaBean();
            String areaName=cursor.getString(0);
            String areaCode=cursor.getString(1);
            areaBean.setAreaName(areaName);
            areaBean.setCity_id(areaCode);
            beanList.add(areaBean);
        }
        cursor.close(); // 记得关闭 corsor
        readableDatabase.close(); // 关闭数据库
        return beanList;
    }

}
