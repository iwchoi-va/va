package sens.src.grade;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jedix.xwing.action.XwingWebAction;
import wfm.com.util.AES256Cipher;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

public class GradeResultWebAction extends XwingWebAction {
	
  private static final long serialVersionUID = 1L;
  private ListParam GradeRslt = new ListParam(new String[] { "CON_ENT_DGN_NO", "VERSION", "GRADE", "GRADE_SUM", "VRS_SCORE", "BAN_SCORE", "BEF_SCORE", "SEC_SCORE", "FOC_SCORE", "MEN_SCORE" });
  private ListParam Grade = new ListParam(new String[] { "VERSION", "GRADE", "GRADE_SCORE" });
  private ListParam GradeWeight = new ListParam(new String[] { "VERSION", "ITEM_CD", "WEIGHT" });
  private ListParam GradeDetail = new ListParam(new String[] { "VERSION", "ITEM_CD", "ITEM_CNT1", "ITEM_CNT2", "ITEM_SCORE", "REG_COMMENT" });
  private ListParam GradeSubject = new ListParam(new String[] { "CON_EN_DGN_NO", "USER_ID", "WORK_MONTH" });
  private ListParam SubjectScore = null;
  private ListParam GradeTmp = new ListParam(new String[] { "CON_EN_DGN_NO", "VRS_SCORE", "BAN_SCORE", "BEF_SCORE", "SEC_SCORE", "FOC_SCORE", "MEN_SCORE" });
  private ListParam GradeRollback = null;
  private ListParam GradeGuide = null;
  
  public void perform(JediRequest req, JediResponse res)
    throws WebActionException
  {
    IVRLogger.info("GradeResultWebAction Start!!");
    JediTransaction tran = JediTransactionManager.getJediTransaction();
    try
    {
      SQLParam sqlParam1 = new SQLParam();
      
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_3");
      sqlParam1.clear();
      sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_3");
      SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_3::Count::" + sqlResult1.getCount());
      
      boolean ta_yn = true;
      boolean src_yn = true;
      if (sqlResult1.getCount() > 0) {
        for (int i = 0; i < sqlResult1.getCount(); i++)
        {
          String item_cd = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_3").getParam(i).getString("ITEM_CD");
          String weight = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_3").getParam(i).getString("WEIGHT");
          this.GradeWeight.addRow(new Object[] {
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_3").getParam(i).getString("VERSION"), 
            item_cd, 
            weight });
          if ("01".equals(item_cd)) {
            if (Integer.parseInt(weight) > 0) {
              ta_yn = true;
            } else {
              ta_yn = false;
            }
          }
          if ("02".equals(item_cd)) {
            if (Integer.parseInt(weight) > 0) {
              src_yn = true;
            } else {
              src_yn = false;
            }
          }
        }
      }
      String v_sql = "";
      if ((ta_yn) && (src_yn)) {
        v_sql = "msens.xcron.hansol.setgraderesultwebaction_1_1";
      } else if ((ta_yn) && (!src_yn)) {
        v_sql = "msens.xcron.hansol.setgraderesultwebaction_1_2";
      } else if ((!ta_yn) && (src_yn)) {
        v_sql = "msens.xcron.hansol.setgraderesultwebaction_1_3";
      }
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_1");
      
      sqlParam1.clear();
      sqlParam1.setSqlName(v_sql);
      sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_1::Count::" + sqlResult1.getCount());
      if (sqlResult1.getCount() > 0)
      {
        this.GradeRollback = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1");
        tran.begin();
        
        SQLParam sqlParam4 = new SQLParam();
        sqlParam4.setSqlName("msens.xcron.hansol.setgraderesultwebaction_13");
        sqlParam4.addValue("GradeRslt", sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1"));
        SQLServiceManager.getInstance().execute(sqlParam4, tran);
        
        tran.commit();
        for (int i = 0; i < sqlResult1.getCount(); i++) {
          this.GradeSubject.addRow(new Object[] {
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1").getParam(i).getString("CON_EN_DGN_NO"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1").getParam(i).getString("USER_ID"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_1").getParam(i).getString("WORK_MONTH") });
        }
      }
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_2");
      sqlParam1.clear();
      sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_2");
      sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
      
      this.GradeGuide = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_2");
      
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_2::Count::" + sqlResult1.getCount());
      if (sqlResult1.getCount() > 0) {
        for (int i = 0; i < sqlResult1.getCount(); i++) {
          this.Grade.addRow(new Object[] {
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_2").getParam(i).getString("VERSION"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_2").getParam(i).getString("GRADE"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_2").getParam(i).getString("GRADE_SCORE") });
        }
      }
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_4");
      sqlParam1.clear();
      sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_4");
      sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
      IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_4::Count::" + sqlResult1.getCount());
      if (sqlResult1.getCount() > 0) {
        for (int i = 0; i < sqlResult1.getCount(); i++) {
          this.GradeDetail.addRow(new Object[] {
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("VERSION"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_CD"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_CNT1"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_CNT2"), 
            sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_4").getParam(i).getString("ITEM_SCORE"), 
            "" });
        }
      }
      if (this.GradeRollback != null)
      {
        for (int z = 0; z < this.GradeRollback.rowSize(); z++)
        {
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_5");
          
          String p_ced_no = this.GradeRollback.getValue(z, "CON_ENT_DGN_NO").toString();
          
          IVRLogger.debug(z + "���� ced_no = " + p_ced_no);
          
          sqlParam1.clear();
          sqlParam1.setSqlName("msens.xcron.hansol.setgraderesultwebaction_5");
          sqlParam1.addValue("v_ced_no", p_ced_no);
          sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
          
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_5::CON_ENT_DGN_NO::" + p_ced_no);
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_5::Count::" + sqlResult1.getCount());
          
          this.SubjectScore = sqlResult1.getListParam("msens.xcron.hansol.setgraderesultwebaction_5");
          
          Code[] code = CodeUtil.getCodes("VSS170");
          for (int i = 0; i < this.SubjectScore.rowSize(); i++)
          {
            String ced_no = this.SubjectScore.getValue(i, "CON_ENT_DGN_NO").toString();
            double vrs_score_res = 0.0D;
            double ban_score_res = 0.0D;
            double bef_score_res = 0.0D;
            double sec_score_res = 0.0D;
            double foc_score_res = 0.0D;
            double men_score_res = 0.0D;
            double tot_score = 0.0D;
            
            this.GradeTmp.addRow(new Object[] {
              ced_no, 
              "", 
              "", 
              "", 
              "", 
              "", 
              "" });
            
            this.GradeRslt.addRow(new Object[] {
              ced_no, 
              this.Grade.getValue(0, "VERSION"), 
              "", 
              "", 
              "", 
              "", 
              "", 
              "", 
              "", 
              "" });
            
            IVRLogger.debug("###########가입설계번호 - " + ced_no);
            for (int j = 0; (code != null) && (j < code.length); j++) {
              if (("Y".equalsIgnoreCase(code[j].getUseYn())) && ("Y".equalsIgnoreCase(code[j].getEtc2()))) {
                if ("01".equals(code[j].getCodeId()))
                {
                  double vrs_score = Double.parseDouble(this.SubjectScore.getValue(i, "SCORE_" + code[j].getCodeId()).toString());
                  int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                  double weight = 0.0D;
                  if (idx > -1) {
                    weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                  }
                  this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "VRS_SCORE", Double.valueOf(vrs_score));
                  vrs_score_res = Math.round(vrs_score * weight / 100.0D);
                  this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "VRS_SCORE", Double.valueOf(vrs_score_res));
                  
                  tot_score += vrs_score_res;
                  
                  IVRLogger.debug("vrs준수결과- " + vrs_score_res + "// 총점수 - " + tot_score);
                }
                else if ("03".equals(code[j].getCodeId()))
                {
                  double bef_score = Double.parseDouble(this.SubjectScore.getValue(i, "SCORE_" + code[j].getCodeId()).toString());
                  int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                  double weight = 0.0D;
                  if (idx > -1) {
                    weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                  }
                  this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "BEF_SCORE", Double.valueOf(bef_score));
                  bef_score_res = Math.round(bef_score * weight / 100.0D);
                  
                  this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "BEF_SCORE", Double.valueOf(bef_score_res));
                  tot_score += bef_score_res;
                  
                  IVRLogger.debug("전월평가점수- " + bef_score_res + "// 총 점수 - " + tot_score);
                }
                else if ("04".equals(code[j].getCodeId()))
                {
                  double secret_score = Double.parseDouble(this.SubjectScore.getValue(i, "SCORE_" + code[j].getCodeId()).toString());
                  
                  int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                  double weight = 0.0D;
                  if (idx > -1) {
                    weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                  }
                  this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "SEC_SCORE", Double.valueOf(secret_score));
                  sec_score_res = Math.round(secret_score * weight / 100.0D);
                  this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "SEC_SCORE", Double.valueOf(sec_score_res));
                  tot_score += sec_score_res;
                  
                  IVRLogger.debug("개인정보처리항목 점수- " + sec_score_res + "// 총점수 - " + tot_score);
                }
                else if ("05".equals(code[j].getCodeId()))
                {
                  double focus_score = Double.parseDouble(this.SubjectScore.getValue(i, "SCORE_" + code[j].getCodeId()).toString());
                  
                  int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                  double weight = 0.0D;
                  if (idx > -1) {
                    weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                  }
                  this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "FOC_SCORE", Double.valueOf(focus_score));
                  foc_score_res = Math.round(focus_score * weight / 100.0D);
                  this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "FOC_SCORE", Double.valueOf(foc_score_res));
                  tot_score += foc_score_res;
                  
                  IVRLogger.debug("집중관리 항목 점수 - " + foc_score_res + "// 총점수 - " + tot_score);
                }
                else
                {
                  for (int k = 0; k < this.GradeDetail.rowSize(); k++) {
                    if (("02".equals(this.GradeDetail.getValue(k, "ITEM_CD"))) && ("02".equals(code[j].getCodeId())))
                    {
                      int ban_cnt = Integer.parseInt(this.SubjectScore.getValue(i, "SCORE_" + code[j].getCodeId()).toString());
                      String comp_cnt1 = this.GradeDetail.getValue(k, "ITEM_CNT1").toString();
                      String comp_cnt2 = this.GradeDetail.getValue(k, "ITEM_CNT2").toString();
                      
                      double ban_score = 0.0D;
                      if (!"".equals(comp_cnt2))
                      {
                        if ((ban_cnt >= Integer.parseInt(comp_cnt1)) && (ban_cnt < Integer.parseInt(comp_cnt2)))
                        {
                          ban_score = Double.parseDouble(this.GradeDetail.getValue(k, "ITEM_SCORE").toString());
                          this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "BAN_SCORE", this.GradeDetail.getValue(k, "ITEM_SCORE"));
                          
                          int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                          double weight = 0.0D;
                          if (idx > -1) {
                            weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                          }
                          ban_score_res = Math.round(ban_score * weight / 100.0D);
                          this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "BAN_SCORE", Double.valueOf(ban_score_res));
                          
                          tot_score += ban_score_res;
                          
                          IVRLogger.debug("금칙어 점수 - " + ban_score_res + "// 총 점수 - " + tot_score);
                        }
                      }
                      else if (ban_cnt >= Integer.parseInt(comp_cnt1))
                      {
                        ban_score = Double.parseDouble(this.GradeDetail.getValue(k, "ITEM_SCORE").toString());
                        this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "BAN_SCORE", this.GradeDetail.getValue(k, "ITEM_SCORE"));
                        
                        int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                        double weight = 0.0D;
                        if (idx > -1) {
                          weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                        }
                        ban_score_res = Math.round(ban_score * weight / 100.0D);
                        this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "BAN_SCORE", Double.valueOf(ban_score_res));
                        
                        tot_score += ban_score_res;
                        
                        IVRLogger.debug("금칙어 점수- " + ban_score_res + "// 총 점수- " + tot_score);
                      }
                    }
                    else if (("06".equals(this.GradeDetail.getValue(k, "ITEM_CD"))) && ("06".equals(code[j].getCodeId())))
                    {
                      int ment_cnt = Integer.parseInt(this.SubjectScore.getValue(i, "SCORE_" + code[j].getCodeId()).toString());
                      
                      String comp_cnt1 = this.GradeDetail.getValue(k, "ITEM_CNT1").toString();
                      String comp_cnt2 = this.GradeDetail.getValue(k, "ITEM_CNT2").toString();
                      
                      double men_score = 0.0D;
                      if (!"".equals(comp_cnt2))
                      {
                        if ((ment_cnt >= Integer.parseInt(comp_cnt1)) && (ment_cnt < Integer.parseInt(comp_cnt2)))
                        {
                          men_score = Double.parseDouble(this.GradeDetail.getValue(k, "ITEM_SCORE").toString());
                          this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "MEN_SCORE", this.GradeDetail.getValue(k, "ITEM_SCORE"));
                          
                          int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                          double weight = 0.0D;
                          if (idx > -1) {
                            weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                          }
                          men_score_res = Math.round(men_score * weight / 100.0D);
                          this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "MEN_SCORE", Double.valueOf(men_score_res));
                          
                          tot_score += men_score_res;
                          
                          IVRLogger.debug("필수멘트 점수- " + men_score_res + "// 총 점수 - " + tot_score);
                        }
                      }
                      else if (ment_cnt >= Integer.parseInt(comp_cnt1))
                      {
                        men_score = Double.parseDouble(this.GradeDetail.getValue(k, "ITEM_SCORE").toString());
                        this.GradeTmp.setValue(this.GradeTmp.rowSize() - 1, "MEN_SCORE", this.GradeDetail.getValue(k, "ITEM_SCORE"));
                        
                        int idx = this.GradeWeight.findRow("ITEM_CD", code[j].getCodeId());
                        double weight = 0.0D;
                        if (idx > -1) {
                          weight = Double.parseDouble(this.GradeWeight.getValue(idx, "WEIGHT").toString());
                        }
                        men_score_res = Math.round(men_score * weight / 100.0D);
                        this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "MEN_SCORE", Double.valueOf(men_score_res));
                        
                        tot_score += men_score_res;
                        
                        IVRLogger.debug("필수멘트 점수 - " + men_score_res + "// 총 점수 - " + tot_score);
                      }
                    }
                  }
                }
              }
            }
            this.GradeRslt.setValue(this.GradeRslt.rowSize() - 1, "GRADE_SUM", Long.valueOf(Math.round(tot_score)));
          }
        }
        if (this.GradeRslt.rowSize() > 0) {
          for (int i = 0; i < this.GradeRslt.rowSize(); i++)
          {
            double grade_sum = Double.parseDouble(this.GradeRslt.getValue(i, "GRADE_SUM").toString());
            for (int j = 0; j < this.Grade.rowSize(); j++)
            {
              double comp_score = Double.parseDouble(this.Grade.getValue(j, "GRADE_SCORE").toString());
              if (grade_sum >= comp_score)
              {
                this.GradeRslt.setValue(i, "GRADE", this.Grade.getValue(j, "GRADE"));
                break;
              }
            }
          }
        }
        tran.begin();
        if (this.GradeRslt.rowSize() > 0)
        {
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_6");
          
          SQLParam sqlParam2 = new SQLParam();
          sqlParam2.setSqlName("msens.xcron.hansol.setgraderesultwebaction_6");
          sqlParam2.addValue("GradeRslt", this.GradeRslt);
          
          SQLServiceManager.getInstance().execute(sqlParam2, tran);
          
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_7");
          
          SQLParam sqlParam3 = new SQLParam();
          sqlParam3.setSqlName("msens.xcron.hansol.setgraderesultwebaction_7");
          sqlParam3.addValue("GradeRslt", this.GradeRslt);
          
          SQLServiceManager.getInstance().execute(sqlParam3, tran);
          
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_8");
          
          SQLParam sqlParam5 = new SQLParam();
          sqlParam5.setSqlName("msens.xcron.hansol.setgraderesultwebaction_8");
          sqlParam5.addValue("VERSION", this.GradeRslt.getValue(0, "VERSION"));
          SQLParam sqlResult5 = SQLServiceManager.getInstance().execute(sqlParam5);
          if (sqlResult5.getCount() > 0) {
            for (int i = 0; i < sqlResult5.getCount(); i++)
            {
              String smonth = "0";
              String emonth = "0";
              String grade = "";
              String reg_comment = "";
              if (i == 0)
              {
                emonth = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("ITEM_CNT1");
              }
              else
              {
                smonth = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i - 1).getString("ITEM_CNT1");
                emonth = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("ITEM_CNT1");
              }
              grade = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("ITEM_SCORE");
              reg_comment = sqlResult5.getListParam("msens.xcron.hansol.setgraderesultwebaction_8").getParam(i).getString("REG_COMMENT");
              
              IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_9");
              for (int j = 0; j < this.GradeRslt.rowSize(); j++)
              {
                String p_ced_no = this.GradeRslt.getValue(j, "CON_ENT_DGN_NO").toString();
                SQLParam sqlParam4 = new SQLParam();
                sqlParam4.setSqlName("msens.xcron.hansol.setgraderesultwebaction_9");
                sqlParam4.addValue("VERSION", this.GradeRslt.getValue(0, "VERSION"));
                sqlParam4.addValue("CON_ENT_DGN_NO", p_ced_no);
                sqlParam4.addValue("SMONTH", smonth);
                sqlParam4.addValue("EMONTH", emonth);
                sqlParam4.addValue("GRADE", grade);
                sqlParam4.addValue("REG_COMMENT", reg_comment);
                
                SQLServiceManager.getInstance().execute(sqlParam4, tran);
              }
            }
          }
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_10");
          
          SQLParam sqlParam6 = new SQLParam();
          sqlParam6.setSqlName("msens.xcron.hansol.setgraderesultwebaction_10");
          sqlParam6.addValue("GradeRslt", this.GradeRollback);
          
          SQLServiceManager.getInstance().execute(sqlParam6, tran);
        }
        tran.commit();
        
        ListParam GradeRslt_copy = new ListParam(new String[] { "CON_ENT_DGN_NO", "VERSION", "GRADE", "GRADE_NM", "GRADE_SUM", "VRS_SCORE", "BAN_SCORE", "BEF_SCORE", "SEC_SCORE", "FOC_SCORE", "MEN_SCORE", "CHG_GRADE", "CHG_GRADE_NM", "CHG_COMMENT", "OBSV_CNT", "ALL_ITEM_CNT", "HAPP_CALL_ENCO_YN", "REAL_OWN_CFRM_YN"});
        
        ListParam Grade_Monitoring = new ListParam(new String[] { "CON_ENT_DGN_NO", "GRADE" });
        try
        {
          for (int i = 0; i < this.GradeRslt.rowSize(); i++)
          {
            String p_ced_no = this.GradeRslt.getValue(i, "CON_ENT_DGN_NO").toString();
            
            IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_11");
            
            SQLParam sqlParam7 = new SQLParam();
            sqlParam7.setSqlName("msens.xcron.hansol.setgraderesultwebaction_11");
            sqlParam7.addValue("CON_ENT_DGN_NO", p_ced_no);
            SQLParam sqlResult7 = SQLServiceManager.getInstance().execute(sqlParam7);
            
            /* 추가(202211) - 준수개수, 전체 스크립트 개수 TM 추가 연계 */
            IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_17");
            SQLParam sqlParam10 = new SQLParam();
            sqlParam10.setSqlName("msens.xcron.hansol.setgraderesultwebaction_17");
            sqlParam10.addValue("CED_NO", p_ced_no);
            SQLParam sqlResult10 = SQLServiceManager.getInstance().execute(sqlParam10);
            
            /* 추가(202307) - 해피콜 독려 여부, CDD 이행 여부 조회 */
            IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_18");
            SQLParam sqlParam11 = new SQLParam();
            sqlParam11.setSqlName("msens.xcron.hansol.setgraderesultwebaction_18");
            sqlParam11.addValue("CED_NO", p_ced_no);
            SQLParam sqlResult11 = SQLServiceManager.getInstance().execute(sqlParam11);
            
            if (sqlResult7.getCount() > 0) {
              for (int k = 0; k < sqlResult7.getCount(); k++)
              {
                String v_grade = sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("GRADE");
                Param v_gradeInfo = this.GradeGuide.getParam("GRADE", v_grade);
                
                // 전수 모니터링(집중모니터링) 대상 체크 프로세스 --------------------------------------------------
                // String v_mon_yn = v_gradeInfo.getValue("MON_YN", "N").toString();
                // if ("Y".equals(v_mon_yn))
                // {
                //   SQLParam sqlParam8 = new SQLParam();
                //   sqlParam8.setSqlName("msens.xcron.hansol.setgraderesultwebaction_16");
                //   sqlParam8.addValue("CON_ENT_DGN_NO", p_ced_no);
                //   SQLParam sqlResult8 = SQLServiceManager.getInstance().execute(sqlParam8);
                //   if (sqlResult8.getCount() > 0)
                //   {
                //     int est_cnt = sqlResult8.getListParam("msens.xcron.hansol.setgraderesultwebaction_16").getParam(0).getInt("EST_CNT");
                //     if (est_cnt == 0) {
                //       Grade_Monitoring.addRow(new Object[] {
                //         sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CON_ENT_DGN_NO"), 
                //         v_grade });
                //     }
                //   }
                // }
                // ------------------------------------------------------------------------------

                /* 추가(202211) - 준수개수, 전체 스크립트 개수 TM 추가 연계 */
                String obsv_cnt = "0";
                String all_item_cnt = "0";
                if (sqlResult10.getCount() > 0) {
	                obsv_cnt = sqlResult10.getListParam("msens.xcron.hansol.setgraderesultwebaction_17").getParam(0).getString("OBSV_CNT"); 
	                all_item_cnt = sqlResult10.getListParam("msens.xcron.hansol.setgraderesultwebaction_17").getParam(0).getString("ALL_ITEM_CNT");
                }
                
                /* 추가(202307) - 해피콜 독려 여부, CDD 이행 여부  TM 추가 연계 */
                String hcall_yn = sqlResult11.getListParam("msens.xcron.hansol.setgraderesultwebaction_18").getParam(0).getString("HCALL_YN"); 
                String cdd_yn = sqlResult11.getListParam("msens.xcron.hansol.setgraderesultwebaction_18").getParam(0).getString("CDD_YN"); 
                IVRLogger.debug("hcall_yn::"+hcall_yn+"//cdd_yn::"+cdd_yn);

                
                IVRLogger.debug("obsv_cnt::"+obsv_cnt+"/all_item_cnt::"+all_item_cnt);
                GradeRslt_copy.addRow(new Object[] {
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CON_ENT_DGN_NO"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("VERSION"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("GRADE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("GRADE_NM"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("GRADE_SUM"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("VRS_SCORE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("BAN_SCORE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("BEF_SCORE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("SEC_SCORE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("FOC_SCORE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("MEN_SCORE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CHG_GRADE"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CHG_GRADE_NM"), 
                  sqlResult7.getListParam("msens.xcron.hansol.setgraderesultwebaction_11").getParam(k).getString("CHG_COMMENT"),
                  obsv_cnt,
                  all_item_cnt,
                  hcall_yn,
                  cdd_yn
                  });
              }
            }
          }
          tran.begin();
          
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_12 :: " + GradeRslt_copy.rowSize());
          if (GradeRslt_copy.rowSize() > 0)
          {
            SQLParam sqlParam8 = new SQLParam();
            sqlParam8.setSqlName("msens.xcron.hansol.setgraderesultwebaction_12");
            sqlParam8.addValue("DS_GRD_RES", GradeRslt_copy);
            
            SQLServiceManager.getInstance().execute(sqlParam8, tran);
          }
          
          //전수 모니터링(집중모니터링 대상 TM Table에 insert ----------------------------------------------------
          IVRLogger.info("msens.xcron.hansol.setgraderesultwebaction_15 :: " + Grade_Monitoring.rowSize());
          // if (Grade_Monitoring.rowSize() > 0)
          // {
          //   SQLParam sqlParam9 = new SQLParam();
          //   sqlParam9.setSqlName("msens.xcron.hansol.setgraderesultwebaction_15");
          //   sqlParam9.addValue("DS_MON_RES", Grade_Monitoring);
            
          //   SQLServiceManager.getInstance().execute(sqlParam9, tran);
          // }
          //------------------------------------------------------------------------------------------
          
          tran.commit();
        }
        catch (Exception e)
        {
          tran.rollback();
          e.getStackTrace();
          ErrorLogger.error("#####TM DB에 Grade 넣는 과정 오류 발생 ::  = " + e.getMessage());
          IVRLogger.error("#####TM DB에 Grade 넣는 과정 오류 발생  ::  = " + e.getMessage());
        }
        try
        {
          if ((this.GradeRollback != null) && 
            (this.GradeRollback.rowSize() > 0)) {
            setEncrypt();
          }
        }
        catch (Exception e)
        {
          ErrorLogger.error("#####Encryption 과정 중 에러 발생 ::  = " + e.getMessage());
          IVRLogger.error("#####Encryption 과정 중 에러 발생 ::  = " + e.getMessage());
        }
      }
      IVRLogger.info("GradeResultWebAction Stop!!");
    }
    catch (Exception e)
    {
      tran.rollback();
      if ((this.GradeRollback != null) && 
        (this.GradeRollback.rowSize() > 0)) {
        try
        {
          tran.begin();
          
          SQLParam sqlParam5 = new SQLParam();
          sqlParam5.setSqlName("msens.xcron.hansol.setgraderesultwebaction_14");
          sqlParam5.addValue("GradeRollback", this.GradeRollback);
          
          SQLServiceManager.getInstance().execute(sqlParam5, tran);
          
          tran.commit();
        }
        catch (SQLServiceException e1)
        {
          tran.rollback();
          e1.printStackTrace();
        }
      }
      e.getStackTrace();
      
      IVRLogger.error("#####Grade 등급 산정 중 에러 발생= " + e.getMessage());
    }
  }
  
  public void setEncrypt(){
		IVRLogger.info("###########setEncrryption Start###################");
		JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		ListParam SttEncryptContent = new ListParam(new String[] {"UCID", "CONTENT"});
		ListParam SttEncryptSent = new ListParam(new String[] {"UCID", "STT_SENT_ID","STT_SENT"});
		
		//String PROP_DIR = System.getProperty("jedi.home")+"/webapps/WEB-INF/conf.properties";
		String PROP_DIR = System.getProperty("jedi.home")+"/WEB-INF/conf.properties";
		
		String key = "";
	    
		Properties prop = new Properties();
		FileInputStream fis;
		
		try {
			fis = new FileInputStream(PROP_DIR);
			prop.load(new java.io.BufferedInputStream(fis));
			key = prop.getProperty("cipher_key");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//암호화 대상자 찾아오기
		try {
			
			for(int i=0; i< GradeRollback.rowSize(); i++){
				String ced_no = GradeRollback.getValue(i, "CON_ENT_DGN_NO").toString();
				
				SQLParam sqlParam1 = new SQLParam();
				sqlParam1.setSqlName("msens.xcron.hansol.setEncryptionwebaction_1");
				sqlParam1.addValue("CON_ENT_DGN_NO", ced_no);
				SQLParam sqlResult1 = SQLServiceManager.getInstance().execute(sqlParam1);
				
				
				
				IVRLogger.info("###########msens.xcron.hansol.setEncryptionwebaction_1::count::" + sqlResult1.getCount());
				
				ListParam res = null;
				
				//tran.begin();
				
				if(sqlResult1.getCount() > 0){
					res = sqlResult1.getListParam("msens.xcron.hansol.setEncryptionwebaction_1");
					
					AES256Cipher aes_cipher = AES256Cipher.getInstance(key);
					
					for(int j=0; j<res.rowSize(); j++){
						String ucid = res.getValue(j, "UCID").toString();
						
						SttEncryptSent.clear();
						SttEncryptContent.clear();
						
						IVRLogger.debug("###ucid ::"+ ucid +" ///// 분석개수 : " + res.getValue(j, "ANL_CNT").toString() + ", 콜개수 : " + res.getValue(j, "CALL_CNT").toString() + "// 암호화 여부 : " + res.getValue(j, "ENC_FLAG").toString());
						if((Integer.parseInt(res.getValue(j, "ANL_CNT").toString()) == Integer.parseInt(res.getValue(j, "CALL_CNT").toString())) && "N".equals(res.getValue(j, "ENC_FLAG").toString())){
							SQLParam sqlParam2 = new SQLParam();
							
							//IVRLogger.debug("##########s로 업데이트시작");
							
							tran.begin();
							
							/*//해당 UCID 암호화 작업 중임을 알리기 위해 S로 업데이트 치기
							//sqlParam2.clear();
							sqlParam2.setSqlName("msens.xcron.hansol.setEncryptionwebaction_6");
							sqlParam2.addValue("UCID", ucid);
							
							SQLServiceManager.getInstance().execute(sqlParam2, tran);*/
							
							
							//IVRLogger.debug("#############ucid :::: "+ucid);
							SQLParam sqlParam5 =  new SQLParam();  
							sqlParam5.setSqlName("msens.xcron.hansol.setEncryptionwebaction_6");
							sqlParam5.addValue("UCID", ucid);
							
							SQLServiceManager.getInstance().execute(sqlParam5, tran);
							
							
							
							tran.commit();
							
							//IVRLogger.debug("##########s로 업데이트완료");
							
							//같으면 분석이 다된거니까 암호화 돌리기
							
							SQLParam sqlParam3 = new SQLParam();
							
							//IVRLogger.debug("!!!!!!!!!!!!");
							sqlParam3.setSqlName("msens.xcron.hansol.setEncryptionwebaction_2");
							sqlParam3.addValue("UCID", ucid);
							SQLParam sqlResult3 = SQLServiceManager.getInstance().execute(sqlParam3);
							
							IVRLogger.debug("######msens.xcron.hansol.setEncryptionwebaction_2 ::count:: " + sqlResult3.getCount());
							if(sqlResult3.getCount() > 0){
								String v_content = sqlResult3.getListParam("msens.xcron.hansol.setEncryptionwebaction_2").getParam(0).getString("CONTENT");
								String enc_content = aes_cipher.encrypt(v_content);
								SttEncryptContent.addRow(new Object[] {
										sqlResult3.getListParam("msens.xcron.hansol.setEncryptionwebaction_2").getParam(0).getString("UCID"),
										enc_content
								});
							}
							
							SQLParam sqlParam4 = new SQLParam();
							sqlParam4.setSqlName("msens.xcron.hansol.setEncryptionwebaction_3");
							sqlParam4.addValue("UCID", ucid);
							SQLParam sqlResult4 = SQLServiceManager.getInstance().execute(sqlParam4);
							
							IVRLogger.debug("######msens.xcron.hansol.setEncryptionwebaction_3 ::count:: " + sqlResult4.getCount());
							
							if(sqlResult4.getCount() > 0){
								for(int k =0; k< sqlResult4.getCount(); k++){
									String v_sent = sqlResult4.getListParam("msens.xcron.hansol.setEncryptionwebaction_3").getParam(k).getString("STT_SENT");
									String enc_sent = aes_cipher.encrypt(v_sent);
									SttEncryptSent.addRow(new Object[] {
											sqlResult4.getListParam("msens.xcron.hansol.setEncryptionwebaction_3").getParam(k).getString("UCID"),
											sqlResult4.getListParam("msens.xcron.hansol.setEncryptionwebaction_3").getParam(k).getString("STT_SENT_ID"),
											enc_sent
									});
								}
							}
							
							//SQLParam sqlParam3 = new SQLParam();
							
							//IVRLogger.debug("###############CONTENT : :" + SttEncryptContent.rowSize());
							//IVRLogger.debug("###############SENT : :" + SttEncryptSent.rowSize());
							
							tran.begin();
							
							if(SttEncryptContent.rowSize() > 0){
								sqlParam3.clear();
								sqlParam3.setSqlName("msens.xcron.hansol.setEncryptionwebaction_4");
								sqlParam3.addValue("SttEncryptContent", SttEncryptContent);
								
								SQLServiceManager.getInstance().execute(sqlParam3, tran);
							}
							
							if(SttEncryptSent.rowSize() > 0){
								sqlParam3.clear();
								sqlParam3.setSqlName("msens.xcron.hansol.setEncryptionwebaction_5");
								sqlParam3.addValue("SttEncryptSent", SttEncryptSent);
								
								SQLServiceManager.getInstance().execute(sqlParam3, tran);
							}
							
							tran.commit();
						}
	
					}
				}
				
				

			}

			
	
		} catch (Exception e) {
			tran.rollback();
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			IVRLogger.error("##########GradeWebAction :: Encryption Error :: " + e.getMessage());
		}
		
	}
}
