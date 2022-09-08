import org.junit.jupiter.api.Test;
import txt.SiZheng;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SiZhengSql {

    @Test
    public void readTxt() throws Exception {
        //数据源文件
        File readGuanJianCiFile = new File("常态化第二批思政关键词时间戳0816.txt");
        File readRenLianFile = new File("【视频】常态化第二批思政人脸时间戳0816.txt");
        //输出文件
        File hebing = new File("合并.sql");
        File renlian = new File("人脸独有.sql");
        File guanjianci = new File("关键词独有.sql");

        //存放准备写入脚本的对象集合
        Map map = new HashMap();
        //暂存关键词对象
        Map guanJianCiMap = new HashMap();
        //暂存人脸对象
        Map renLianMap = new HashMap();
        //读取到的关键词对象集合
        guanJianCiMap = readGuanJianCi(readGuanJianCiFile);
        //读取到的人脸对象集合
        renLianMap = readRenLian(readRenLianFile);
        //合并关键词和人脸数据
        Iterator ite = guanJianCiMap.entrySet().iterator();
        while (ite.hasNext()){
            SiZheng sz = new SiZheng();
            Map.Entry entry = (Map.Entry) ite.next();
            String key = (String) entry.getKey();
            SiZheng guanJianCi = (SiZheng) entry.getValue();
            SiZheng renLian = (SiZheng) renLianMap.get(key);
            if(renLian != null){
                sz.setGuid(guanJianCi.getGuid());
                sz.setName(renLian.getName());
                sz.setNtime(renLian.getNtime());
                sz.setKeyworda(guanJianCi.getKeyworda());
                sz.setKtime(guanJianCi.getKtime());
                map.put(sz.getGuid(),sz);
                ite.remove();
                renLianMap.remove(key);
            }
        }

//        //合并数据写入sql脚本
        writeSqlToTxt(hebing,map);

//        //关键词独有数据写入sql脚本
        writeSqlToTxt(guanjianci,guanJianCiMap);

//        //人脸独有数据写入sql脚本
        writeSqlToTxt(renlian,renLianMap);



//        //合并之后的数据集合
//        Set set = map.keySet();
//        Iterator iterator = set.iterator();
//        while (iterator.hasNext()){
//            String key = (String) iterator.next();
//            SiZheng s = (SiZheng) map.get(key);
//            System.out.println(s.getGuid()+"-----"+s.getName()+"-----"+s.getNtime()+"-----"+s.getKeyworda()+"-----"+s.getKtime());
//        }

//        //关键词集合独有数据
//        Set set1 = guanJianCiMap.keySet();
//        Iterator iterator1 = set1.iterator();
//        while (iterator1.hasNext()){
//            String key = (String) iterator1.next();
//            SiZheng s = (SiZheng) guanJianCiMap.get(key);
//            if(s != null){
//                System.out.println(s.getGuid()+"-----"+s.getName()+"-----"+s.getNtime()+"-----"+s.getKeyworda()+"-----"+s.getKtime());
//            }
//        }

//        //人脸集合独有数据
//        Set set2 = renLianMap.keySet();
//        Iterator iterator2 = set2.iterator();
//        while (iterator2.hasNext()){
//            String key = (String) iterator2.next();
//            SiZheng s = (SiZheng) renLianMap.get(key);
//            if(s != null){
//                System.out.println(s.getGuid()+"-----"+s.getName()+"-----"+s.getNtime()+"-----"+s.getKeyworda()+"-----"+s.getKtime());
//            }
//        }
    }

    /**
     * 数据写入sql脚本
     * @param file
     * @param map
     * @throws Exception
     */
    private void writeSqlToTxt(File file, Map map) throws Exception {
        Statement statement = getConnection().createStatement();
        FileOutputStream fos = new FileOutputStream(file,true);
        List result = getGuid("SELECT guid FROM d_recog",statement);
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            SiZheng sz = (SiZheng) map.get(key);
            //sql
            StringBuilder writeStr = new StringBuilder();
            if(!result.contains(sz.getGuid())){
                writeStr.append("INSERT INTO d_recog(vdbid,guid,name,ntime,keyworda,ktime,isdelete,keywordb,yuyin,ytime) values(")
                        .append(0).append(",'")
                        .append(sz.getGuid()).append("','")
                        .append(sz.getName()).append("','")
                        .append(sz.getNtime()).append("','")
                        .append(sz.getKeyworda()).append("','")
                        .append(sz.getKtime()).append("',")
                        .append(0).append(",'")
                        .append(sz.getKeywordb()).append("','")
                        .append(sz.getYuyin()).append("','")
                        .append(sz.getYtime()).append("');");
            }else {
                writeStr.append("UPDATE d_recog SET name = '")
                        .append(sz.getName())
                        .append("',ntime = '")
                        .append(sz.getNtime())
                        .append("',keyworda = '")
                        .append(sz.getKeyworda())
                        .append("',ktime = '")
                        .append(sz.getKtime())
                        .append("',WHERE guid = '")
                        .append(sz.getGuid())
                        .append("';");
            }
            System.out.println(writeStr);
            //向文件打印sql
            fos.write(writeStr.toString().getBytes());
            fos.write(("\r\n").getBytes());
        }
        fos.close();
    }

    /**
     * 读取关键词文件拼接数据
     * @param readGuanJianCiFile
     * @return
     * @throws Exception
     */
    private Map readGuanJianCi(File readGuanJianCiFile) throws Exception {
        BufferedReader reader = null;
        Map map = new HashMap<String,SiZheng>();
        try {
            reader = new BufferedReader(new FileReader(readGuanJianCiFile));
            //按行读取的数据
            String tempString = null;
            //拼接数据的对象
            SiZheng sz = new SiZheng();
            while (true){
                //存入map集合的对象
                SiZheng ss = new SiZheng();
                tempString = reader.readLine();
                //读取到最后一行进行处理
                if(tempString == null){
                    //判断集合中是否已存在当前guid
                    if(map.containsKey(sz.getGuid())){
                        //从map集合中获取到的对象进行拼接
                        SiZheng s = (SiZheng) map.get(sz.getGuid());
                        s.setKeyworda(s.getKeyworda() + "," + sz.getKeyworda());
                        s.setKtime(s.getKtime() + ";" + sz.getKtime());
                        ss.setGuid(s.getGuid());
                        ss.setKeyworda(s.getKeyworda());
                        ss.setKtime(s.getKtime());
                        map.put(ss.getGuid(),ss);
                    }else {
                        ss.setGuid(sz.getGuid());
                        ss.setKeyworda(sz.getKeyworda());
                        ss.setKtime(sz.getKtime());
                        map.put(sz.getGuid(),ss);
                    }
                    return map;
                }
                //按行读取数局转数组做数据拼接
                String[] str = tempString.split(",");
                //按行读取的对象
                SiZheng siZheng = new SiZheng();
                siZheng.setGuid(str[1]);
                siZheng.setKeyworda(str[4]);
                String ktime = null;
                for (int i = 5; i < str.length; i++) {
                    if(i == 5){
                        ktime = str[i];
                    }else {
                        ktime = ktime + "," + str[i];
                    }
                }
                siZheng.setKtime(ktime);
                //根据guid合并数据
                if(sz.getGuid() == null){
                    sz = siZheng;
                }else if (sz.getGuid().equals(siZheng.getGuid())){
                    sz.setKeyworda(sz.getKeyworda() + "," + siZheng.getKeyworda());
                    sz.setKtime(sz.getKtime() + ";" + siZheng.getKtime());
                }else {
                    //判断集合中是否已存在当前guid
                    if(map.containsKey(sz.getGuid())){
                        //从map集合中获取到的对象进行拼接
                        SiZheng s = (SiZheng) map.get(sz.getGuid());
                        s.setKeyworda(s.getKeyworda() + "," + sz.getKeyworda());
                        s.setKtime(s.getKtime() + ";" + sz.getKtime());
                        ss.setGuid(s.getGuid());
                        ss.setKeyworda(s.getKeyworda());
                        ss.setKtime(s.getKtime());
                        map.put(ss.getGuid(),ss);
                    }else {
                        ss.setGuid(sz.getGuid());
                        ss.setKeyworda(sz.getKeyworda());
                        ss.setKtime(sz.getKtime());
                        map.put(sz.getGuid(),ss);
                    }
                    sz.setKtime(siZheng.getKtime());
                    sz.setKeyworda(siZheng.getKeyworda());
                    sz.setGuid(siZheng.getGuid());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 读取人脸文件拼接数据
     * @param readRenLianFile
     * @return
     * @throws Exception
     */
    private Map readRenLian(File readRenLianFile) throws Exception {
        BufferedReader reader = null;
        Map map = new HashMap<String,SiZheng>();
        try {
            reader = new BufferedReader(new FileReader(readRenLianFile));
            String tempString = null;
            SiZheng sz = new SiZheng();
            while (true){
                //存入map集合的对象
                SiZheng ss = new SiZheng();
                //按行读取的信息
                tempString = reader.readLine();
                if (tempString == null || tempString == ""){
                    //判断集合中是否已存在当前guid
                    if(map.containsKey(sz.getGuid())){
                        //从map集合中获取到的对象进行拼接
                        SiZheng s = (SiZheng) map.get(sz.getGuid());
                        s.setKeyworda(s.getKeyworda() + "," + sz.getKeyworda());
                        s.setKtime(s.getKtime() + ";" + sz.getKtime());
                        //给准备存入map的对象赋值
                        ss.setGuid(s.getGuid());
                        ss.setKeyworda(s.getKeyworda());
                        ss.setKtime(s.getKtime());
                        map.put(ss.getGuid(),ss);
                    }else {
                        //给准备存入map的对象赋值
                        ss.setGuid(sz.getGuid());
                        ss.setKeyworda(sz.getKeyworda());
                        ss.setKtime(sz.getKtime());
                        map.put(sz.getGuid(),ss);
                    }
                    return map;
                }
                //读取的信息转对象
                String[] str = tempString.split(",");
                SiZheng siZheng = new SiZheng();
                siZheng.setGuid(str[1]);
                siZheng.setName(str[4]);
                String ntime = null;
                for (int i = 5; i < str.length; i++) {
                    if(i == 5){
                        ntime = str[i];
                    }else {
                        ntime = ntime + "," + str[i];
                    }
                }
                siZheng.setNtime(ntime);
                //数据拼接存入map集合
                if(sz.getGuid() == null){
                    sz = siZheng;
                }else if (sz.getGuid().equals(siZheng.getGuid())){
                    sz.setName(sz.getName() + "," + siZheng.getName());
                    sz.setNtime(sz.getNtime() + ";" + siZheng.getNtime());
                }else {
                    //判断集合中是否已存在当前guid
                    if(map.containsKey(sz.getGuid())){
                        //从map集合中获取到的对象进行拼接
                        SiZheng s = (SiZheng) map.get(sz.getGuid());
                        s.setName(s.getName() + "," + sz.getName());
                        s.setNtime(s.getNtime() + ";" + sz.getNtime());
                        //给存入map集合的对象赋值
                        ss.setGuid(s.getGuid());
                        ss.setName(s.getName());
                        ss.setNtime(s.getNtime());
                        map.put(ss.getGuid(),ss);
                    }else {
                        ss.setGuid(sz.getGuid());
                        ss.setName(sz.getName());
                        ss.setNtime(sz.getNtime());
                        map.put(sz.getGuid(),ss);
                    }
                    sz.setNtime(siZheng.getNtime());
                    sz.setName(siZheng.getName());
                    sz.setGuid(siZheng.getGuid());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     *查询数据库guid
     * @param sql
     * @param statement
     * @return
     */
    private List getGuid(String sql,Statement statement) throws Exception {
        // 展开结果集数据库
        ResultSet resultSet = statement.executeQuery(sql);
        List list = new ArrayList();
        while (resultSet.next()){
            String guid = resultSet.getString("guid");
            list.add(guid);
        }
        // 完成后需要依次关闭
        resultSet.close();
        statement.close();
        return list;
    }

    /**
     * 连接数据库
     * @return
     */
    private Connection getConnection(){

        String url = "jdbc:mysql://10.70.36.234:3306/szk";
        String user = "duhongli";
        String password = "duhongli0046";
        String driverClass = "com.mysql.cj.jdbc.Driver";
        Connection connection = null;
        try {
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (connection != null) {
            System.out.println("数据库连接成功");
        } else {
            System.out.println("数据库连接失败");
            try {
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}
