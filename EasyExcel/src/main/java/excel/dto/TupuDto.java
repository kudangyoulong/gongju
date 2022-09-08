package excel.dto;

import com.alibaba.excel.annotation.ExcelProperty;

public class TupuDto {
    @ExcelProperty("guid")
    private String guid;
    @ExcelProperty("书本")
    private String shuben;
    @ExcelProperty("章节")
    private String zhangjie;
    @ExcelProperty("一级标签")
    private String biaoqian;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getShuben() {
        return shuben;
    }

    public void setShuben(String shuben) {
        this.shuben = shuben;
    }

    public String getZhangjie() {
        return zhangjie;
    }

    public void setZhangjie(String zhangjie) {
        this.zhangjie = zhangjie;
    }

    public String getBiaoqian() {
        return biaoqian;
    }

    public void setBiaoqian(String biaoqian) {
        this.biaoqian = biaoqian;
    }

    @Override
    public String toString() {
        return "TupuDto{" +
                "guid='" + guid + '\'' +
                ", shuben='" + shuben + '\'' +
                ", zhangjie='" + zhangjie + '\'' +
                ", biaoqian='" + biaoqian + '\'' +
                '}';
    }
}
