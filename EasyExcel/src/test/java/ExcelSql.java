import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import excelandtxtTosql.excel.dto.DVideo;
import excelandtxtTosql.excel.dto.JiChuDto;
import excelandtxtTosql.excel.dto.TupuDto;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;

public class ExcelSql {

    //【视频】常态化第二批20220802基础标签结果生成sql
    @Test
    public void test1() throws Exception {
        String jichuFile = "【视频】常态化第二批20220802基础标签结果(1).xlsx";
        String tupuFile = "【视频】常态化第二批20220802图谱结果.xls";
        File hebingSql = new File("基础_图谱.sql");
        File jichuSql = new File("基础独有.sql");
        File tupuSql = new File("图谱独有.sql");

        //准备写入集合
        Map writeMap = new HashMap();
        //基础结果集合
        Map jiChuMap = readJiChu(jichuFile);
        //图谱结果集合
        Map tupuMap = readTuPu(tupuFile);

        //合并基础和图谱数据
        Iterator ite = jiChuMap.entrySet().iterator();
        while (ite.hasNext()){
            DVideo dVideo = new DVideo();
            Map.Entry entry = (Map.Entry) ite.next();
            String key = (String) entry.getKey();
            JiChuDto jichu = (JiChuDto) entry.getValue();
            TupuDto tupu = (TupuDto) tupuMap.get(key);
            if(tupu != null){
                //合并数据
                dVideo.setGuid(jichu.getGuid());
                dVideo.setWkeyword(jichu.getWkeyword());
                dVideo.setWpublicpeople(jichu.getWpublicpeople());
                dVideo.setWtime(jichu.getWtime());
                dVideo.setWlocation(jichu.getWlocation());
                dVideo.setWfigure(jichu.getWfigure());
                dVideo.setWorgan(jichu.getWorgan());
                dVideo.setWentityrelation(jichu.getWentityrelation());
                dVideo.setWscene(jichu.getWscene());
                dVideo.setWpoetry(jichu.getWpoetry());
                String[] str = tupu.getBiaoqian().split("/");
                dVideo.setKnowledgeTop(str[0]);
                dVideo.setKnowledgeSub(str[1]);
                writeMap.put(dVideo.getGuid(),dVideo);
                //删除基础和图谱集合中的数据
                ite.remove();
                tupuMap.remove(key);
            }
        }

        //合并后的数据写入脚本
        writeSql(hebingSql,writeMap);

//        //合并后写入脚本的数据
//        Set set = writeMap.keySet();
//        Iterator iterator = set.iterator();
//        while (iterator.hasNext()){
//            String key = (String) iterator.next();
//            DVideo s = (DVideo) writeMap.get(key);
//            System.out.println(s.toString());
//        }

//        //基础结果读取到的数据
//        Set set = jiChuMap.keySet();
//        Iterator iterator = set.iterator();
//        while (iterator.hasNext()){
//            String key = (String) iterator.next();
//            JiChuDto s = (JiChuDto) jiChuMap.get(key);
//            System.out.println(s.toString());
//        }


//        //图谱结果读取到的数据
//        Set set = tupuMap.keySet();
//        Iterator iterator = set.iterator();
//        while (iterator.hasNext()){
//            String key = (String) iterator.next();
//            TupuDto s = (TupuDto) tupuMap.get(key);
//            System.out.println(s.toString());
//        }

    }

    /**
     * 合并后的数据写入脚本
     * @param writeFile
     * @param map
     * @throws Exception
     */
    private void writeSql(File writeFile,Map map) throws Exception {
        FileOutputStream fos = new FileOutputStream(writeFile,true);
        //获取数据库guid
        List guidList = getGuid("SELECT guid FROM d_video");
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            StringBuilder writeStr = new StringBuilder();
            String key = (String) iterator.next();
            DVideo dVideo = (DVideo) map.get(key);
            //guid存在
            if(guidList.contains(dVideo.getGuid())){
                writeStr.append("UPDATE d_video SET wkeyword = '")
                        .append(dVideo.getWkeyword())
                        .append("',wpublicpeople = '")
                        .append(dVideo.getWpublicpeople())
                        .append("',wtime = '")
                        .append(dVideo.getWtime())
                        .append("',wlocation = '")
                        .append(dVideo.getWlocation())
                        .append("',wfigure = '")
                        .append(dVideo.getWfigure())
                        .append("',worgan = '")
                        .append(dVideo.getWorgan())
                        .append("',wentityrelation = '")
                        .append(dVideo.getWentityrelation())
                        .append("',wscene = '")
                        .append(dVideo.getWscene())
                        .append("',wpoetry = '")
                        .append(dVideo.getWpoetry())
                        .append("',knowledge_top = '")
                        .append(dVideo.getKnowledgeTop())
                        .append("',knowledge_sub = '")
                        .append(dVideo.getKnowledgeSub())
                        .append("',WHERE guid = '")
                        .append(dVideo.getGuid())
                        .append("';");
            }else {
                writeStr.append("INSERT INTO d_video(courseId,catalogId1,catalogId2,title,guid,videoid,pageurl,knowledgeTop,knowledgeSub")
                        .append(",wkeyword,wpublicpeople,wtime,wlocation,wfigure,worgan,wentityrelation,wscene,wpoetry")
                        .append(",length,bitrate,subtitle,brief,editor,photoa,photob,photoc,keyword,provider,channel,program,origintype,originmedia,cataloga,catalogb,isdelete,pubtime,updatetime) values(")
                        .append(0).append(",")
                        .append(0).append(",")
                        .append(0).append(",'")
                        .append(dVideo.getTitle()).append("','")
                        .append(dVideo.getGuid()).append("','")
                        .append(dVideo.getVideoid()).append("','")
                        .append(dVideo.getPageurl()).append("','")
                        .append(dVideo.getKnowledgeTop()).append("',")
                        .append(dVideo.getKnowledgeSub()).append("','")
                        .append(dVideo.getWkeyword()).append("','")
                        .append(dVideo.getWpublicpeople()).append("','")
                        .append(dVideo.getWtime()).append("','")
                        .append(dVideo.getWlocation()).append("','")
                        .append(dVideo.getWfigure()).append("','")
                        .append(dVideo.getWorgan()).append("','")
                        .append(dVideo.getWentityrelation()).append("','")
                        .append(dVideo.getWscene()).append("','")
                        .append(dVideo.getWpoetry()).append("',")
                        .append(dVideo.getLength()).append(",'")
                        .append(dVideo.getBitrate()).append("','")
                        .append(dVideo.getSubtitle()).append("','")
                        .append(dVideo.getBrief()).append("','")
                        .append(dVideo.getEditor()).append("','")
                        .append(dVideo.getPhotoa()).append("','")
                        .append(dVideo.getPhotob()).append("','")
                        .append(dVideo.getPhotoc()).append("','")
                        .append(dVideo.getKeyword()).append("','")
                        .append(dVideo.getProvider()).append("','")
                        .append(dVideo.getChannel()).append("','")
                        .append(dVideo.getProgram()).append("','")
                        .append(dVideo.getOrigintype()).append("','")
                        .append(dVideo.getOriginmedia()).append("','")
                        .append(dVideo.getCataloga()).append("','")
                        .append(dVideo.getCatalogb()).append("','")
                        .append(dVideo.getIsdelete()).append("','")
                        .append(dVideo.getPubtime()).append("','")
                        .append(dVideo.getUpdatetime()).append("');");
            }
            fos.write(writeStr.toString().getBytes());
            fos.write(("\r\n").getBytes());
        }
        fos.close();
    }

    /**
     * 读取图谱结果数据
     * @param fileName
     * @return
     */
    private Map readTuPu(String fileName) {
        Map map = new HashMap();
        EasyExcel.read(fileName, TupuDto.class, new AnalysisEventListener<TupuDto>() {
            @Override
            public void invoke(TupuDto tupuDto, AnalysisContext analysisContext) {
                if(map.containsKey(tupuDto.getGuid())){
                    TupuDto mapTupu = (TupuDto) map.get(tupuDto.getGuid());
                    //集合中标签字段数据
                    String biaoqianMap = mapTupu.getBiaoqian();
                    String[] strsMap = biaoqianMap.split("/");
                    String[] sqMap = strsMap[0].split(";");
                    String[] shMap = strsMap[1].split(";");
                    //读取的数据标签字段
                    String[] strTupu = tupuDto.getBiaoqian().split("/");
                    String sqTupu = strTupu[0];
                    String shTupu = strTupu[1];
                    if(Arrays.asList(sqMap).contains(sqTupu)){
                        int index = getIndex(sqMap, sqTupu);
                        shMap[index] = shMap[index] + "," + shTupu;
                        strsMap[1] = arrayString(shMap);
                        biaoqianMap = strsMap[0] + "/" + strsMap[1];
                    }else {
                        strsMap[0] = strsMap[0] + ";" + sqTupu;
                        strsMap[1] = strsMap[1] + ";" + shTupu;
                        biaoqianMap = strsMap[0] + "/" + strsMap[1];
                    }
                    mapTupu.setBiaoqian(biaoqianMap);
                    map.put(mapTupu.getGuid(),mapTupu);
                }else {
                    map.put(tupuDto.getGuid(),tupuDto);
                }
            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            }
        }).sheet().headRowNumber(1).doRead();
        return map;
    }

    /**
     * 数组转字符串
     * @param str
     * @return
     */
    private String arrayString(String[] str) {
        if(str == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : str) {
            sb.append(s + ";");
        }
        String sub = sb.toString().substring(0,sb.toString().length()-1);
        return sub;
    }

    /**
     * 获取数组下标
     * @param array
     * @param str
     * @return
     */
    private int getIndex(String[] array,String str){
        for(int i = 0;i<array.length;i++){
            if(array[i].equals(str)){
                return i;
            }
        }
        return -1;//当if条件不成立时，默认返回一个负数值-1
    }

    /**
     * 读取基础结果数据
     * @param fileName
     * @return
     */
    private Map readJiChu(String fileName) {
        Map map = new HashMap();
        EasyExcel.read(fileName, JiChuDto.class, new AnalysisEventListener<JiChuDto>() {
            @Override
            public void invoke(JiChuDto jiChuDto, AnalysisContext analysisContext) {
                //判断集合中是否已包含此guid数据
                if(map.containsKey(jiChuDto.getGuid())){
                    JiChuDto mapJiChu = (JiChuDto) map.get(jiChuDto.getGuid());
                    mapJiChu.setWkeyword(mapJiChu.getWkeyword()+","+jiChuDto.getWkeyword());
                    mapJiChu.setWpublicpeople(mapJiChu.getWpublicpeople()+","+jiChuDto.getWpublicpeople());
                    mapJiChu.setWtime(mapJiChu.getWtime()+","+jiChuDto.getWtime());
                    mapJiChu.setWlocation(mapJiChu.getWlocation()+","+jiChuDto.getWlocation());
                    mapJiChu.setWfigure(mapJiChu.getWfigure()+","+jiChuDto.getWfigure());
                    mapJiChu.setWorgan(mapJiChu.getWorgan()+","+jiChuDto.getWorgan());
                    mapJiChu.setWentityrelation(mapJiChu.getWentityrelation()+","+jiChuDto.getWentityrelation());
                    mapJiChu.setWscene(mapJiChu.getWscene()+","+jiChuDto.getWscene());
                    mapJiChu.setWpoetry(mapJiChu.getWpoetry()+","+jiChuDto.getWpoetry());
                    map.put(mapJiChu.getGuid(),mapJiChu);
                }else {
                    map.put(jiChuDto.getGuid(),jiChuDto);
                }
            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            }
        }).sheet().headRowNumber(2).doRead();
        return map;
    }

    /**
     * 获取guid
     * @param sql
     * @return
     * @throws Exception
     */
    private List getGuid(String sql) throws Exception {
        // 实例化 Statement 对象
        Statement statement = getConnection().createStatement();
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

}
