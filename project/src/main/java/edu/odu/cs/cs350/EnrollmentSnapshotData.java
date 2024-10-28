package edu.odu.cs.cs350;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvBindByName;

/**
 * A java bean class to be used with CsvToBeanBuilder for loading 
 * beans with data from each enrollment snapshot.
 */
public class EnrollmentSnapshotData  {
    
    @CsvBindByName(column = "CRN")
    private String CRN;

    @CsvBindByName(column = "SUBJ")
    private String SUBJ;

    @CsvBindByName(column = "CRSE")
    private int CRSE;
    
    @CsvBindByName(column = "XLST CAP")
    private int XLST_CAP;

    @CsvBindByName(column = "ENR")
    private int ENR;

    @CsvBindByName(column = "LINK")
    private String LINK;

    @CsvBindByName(column = "XLST GROUP")
    private String XLST_GROUP;

    @CsvBindByName(column = "OVERALL CAP")
    private int OVERALL_CAP;

    @CsvBindByName(column = "OVERALL ENR")
    private int OVERALL_ENR;

    @CsvBindByName(column = "Seats")
    private int seats;
    @CsvBindByName(column = "TITLE")
    private String TITLE;
    @CsvBindByName(column = "CR HRS")
    private int CR_HRS;
    @CsvBindByName(column = "SCHED TYPE")
    private String SCHED_TYPE;
    @CsvBindByName(column = "CAMPUS")
    private String CAMPUS;
    @CsvBindByName(column = "INSM")
    private String INSM;
    @CsvBindByName(column = "PRINT?")
    private String PRINT;
    @CsvBindByName(column = "TIME")
    private String TIME;
    @CsvBindByName(column = "DAYS")
    private String DAYS;
    @CsvBindByName(column = "BLDG")
    private String BLDG;
    @CsvBindByName(column = "ROOM")
    private String ROOM;
    @CsvBindByName(column = "OVERRIDE")
    private String OVERRIDE;
    @CsvBindByName(column = "INSTRUCTOR")
    private String INSTRUCTOR;
    @CsvBindByName(column = "PTRM START")
    private String PTRM_START;
    @CsvBindByName(column = "PTRM END")
    private String PTRM_END;
    @CsvBindByName(column = "WL CAP")
    private int WL_CAP;
    @CsvBindByName(column = "WL")
    private int WL;
    @CsvBindByName(column = "WL REMAIN")
    private int WL_REMAIN;
    @CsvBindByName(column = "NOTES")
    private String NOTES;
    @CsvBindByName(column = "COMMENTS")
    private String COMMENTS;
    @CsvBindByName(column = "COLL")
    private String COLL;

    @CsvBindByPosition(position = 13)
    private String empty1;
    @CsvBindByPosition(position = 23)
    private String empty2;
    @CsvBindByPosition(position = 26)
    private String empty3;
    @CsvBindByPosition(position = 27)
    private String empty4;



    public EnrollmentSnapshotData() {}

    public String getEmpty1() {
        return this.empty1;
    }
    public void setEmpty1(String num) {
        this.empty1 = num;
    }
    public String getEmpty2() {
        return this.empty2;
    }
    public void setEmpty2(String num) {
        this.empty2 = num;
    }
    public String getEmpty3() {
        return this.empty3;
    }
    public void setEmpty3(String num) {
        this.empty3 = num;
    }
    public String getEmpty4() {
        return this.empty4;
    }
    public void setEmpty4(String num) {
        this.empty4 = num;
    }

    public String getSUBJ() {
        return this.SUBJ;
    }
    public void setSUBJ(String subject) {
        this.SUBJ = subject;
    }
    public String getCRN() {
        return this.CRN;
    }
    public void setCRN(String crseNum) {
        this.CRN = crseNum;
    }
    public void setCRSE(int course) {
        this.CRSE = course;
    }
    public int getCRSE() {
        return this.CRSE;
    }
    public void setXLST_CAP(int xcap) {
        this.XLST_CAP = xcap;
    }
    public int getXLST_CAP() {
        return this.XLST_CAP;
    }
    public void setENR(int enrolled) {
        this.ENR = enrolled;
    }
    public int getENR() {
        return this.ENR;
    }
    public void setLINK(String link) {
        this.LINK = link;
    }
    public String getLINK() {
        return this.LINK;
    }
    public void setXLST_GROUP(String xlgroup) {
        this.XLST_GROUP = xlgroup;
    }
    public String getXLST_GROUP() {
        return this.XLST_GROUP;
    }
    public void setOVERALL_CAP(int ocap) {
        this.OVERALL_CAP = ocap;
    }
    public int getOVERALL_CAP() {
        return this.OVERALL_CAP;
    }
    public void setOVERALL_ENR(int oenr) {
        this.OVERALL_ENR = oenr;
    }
    public int getOVERALL_ENR() {
        return this.OVERALL_ENR;
    }
    public int getSeats() {
        return this.seats;
    }
    public void setSEATS(int seats) {
        this.seats = seats;
    }
    public String getTITLE() {
        return this.TITLE;
    }
    public void setTITLE(String title) {
        this.TITLE = title;
    }
    public int getCR_HRS() {
        return this.CR_HRS;
    }
    public void setCR_HRS(int hrs) {
        this.CR_HRS = hrs;
    }
    public String getSCHED_TYPE() {
        return this.SCHED_TYPE;
    }
    public void setSCHED_TYPE(String type) {
        this.SCHED_TYPE = type;
    }
    public String getCAMPUS() {
        return this.CAMPUS;
    }
    public void setCAMPUS(String campus) {
        this.CAMPUS = campus;
    }
    public String getINSM() {
        return this.INSM;
    }
    public void setINSM(String insm) {
        this.INSM = insm;
    }
    public String getPRINT() {
        return this.PRINT;
    }
    public void setPRINT(String print) {
        this.PRINT = print;
    }
    public String getTIME() {
        return this.TIME;
    }
    public void setTIME(String time) {
        this.TIME = time;
    }
    public String getDAYS() {
        return this.DAYS;
    }
    public void setDAYS(String days) {
        this.DAYS = days;
    }
    public String getBLDG() {
        return this.BLDG;
    }
    public void setBLDG(String bldg) {
        this.BLDG = bldg;
    }
    public String getROOM() {
        return this.ROOM;
    }
    public void setROOM(String room) {
        this.ROOM = room;
    }
    public String getOVERRIDE() {
        return this.OVERRIDE;
    }
    public void setOVERRIDE(String override) {
        this.OVERRIDE = override;
    }
    public String getINSTRUCTOR() {
        return this.INSTRUCTOR;
    }
    public void setINSTRUCTOR(String instr) {
        this.INSTRUCTOR = instr;
    }
    public String getPTRM_START() {
        return this.PTRM_START;
    }
    public void setPTRM_START(String ptrms) {
        this.PTRM_START = ptrms;
    }
    public String getPTRM_END() {
        return this.PTRM_END;
    }
    public void setPTRM_END(String ptrme) {
        this.PTRM_END = ptrme;
    }
    public int getWL_CAP() {
        return this.WL_CAP;
    }
    public void setWL_CAP(int wlcap) {
        this.WL_CAP = wlcap;
    }
    public int getWL() {
        return this.WL;
    }
    public void setWL(int wl) {
        this.WL = wl;
    }
    public int getWL_REMAIN() {
        return this.WL_REMAIN;
    }
    public void setWL_REMAIN(int wlr) {
        this.WL_REMAIN = wlr;
    }
    public String getNOTES() {
        return this.NOTES;
    }
    public void setNOTES(String notes) {
        this.NOTES = notes;
    }
    public String getCOMMENTS() {
        return this.COMMENTS;
    }
    public void setCOMMENTS(String comments) {
        this.COMMENTS = comments;
    }
    public String getCOLL() {
        return this.COLL;
    }
    public void setCOLL(String coll) {
        this.COLL = coll;
    }
}
