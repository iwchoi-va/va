package kr.co.kico.wfm.encmapper.process;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.kico.wfm.encmapper.vo.EncMappingVO;

import com.hansol.dbenc.util.SafeDBUtil;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;

public class EncProcess extends CommonProcess {
	public EncProcess() throws Exception {
		
	}

	public SQLParam encSQLParam(String queryId, SQLParam sqlParam) {
		try {
			if (sqlParam == null) {
				return sqlParam;
			}
			if (sqlParam != null)
				sqlParam = (SQLParam)encParam(queryId, sqlParam);
		} catch (Exception e) {
			ErrorLogger.error("==========================================================");
			ErrorLogger.error("enc SQLParam Exception:" + e);
			ErrorLogger.error("==========================================================");

			return sqlParam;
		}

		return sqlParam;
	}

	public SQLParam[] encSQLParamArray(String queryId, SQLParam[] sqlparams) {
		SQLParam[] tempSqlParamAry = sqlparams;

		if (sqlparams == null) {
			return sqlparams;
		}
		
		try {
			for (int i = 0; i < tempSqlParamAry.length; i++)
				tempSqlParamAry[i] = encSQLParam(queryId, tempSqlParamAry[i]);
		} catch (Exception e) {
			ErrorLogger.error("===================================================");
			ErrorLogger.error("enc SQLParamArray Exception:" + e);
			ErrorLogger.error("===================================================");
			tempSqlParamAry = sqlparams;
		}

		return tempSqlParamAry;
	}

	public Param encParam(String queryId, Param param) throws Exception {
		EncMappingVO encMappingVO = this.mapperConfig.getEncInfoByQueryId(queryId);
		if (encMappingVO == null) {
			return param;
		}

		String[] keyAry = param.keys();
		
		/*for(int i=0; i<keyAry.length; i++){
			System.out.println(keyAry[i]);
		}*/

		HashMap encInfoMap = getEncInfo(encMappingVO);
		ArrayList encColList = (ArrayList)encInfoMap.get("ENC_COL");
		ArrayList polyColList = (ArrayList)encInfoMap.get("POLY_COL");

		if ((keyAry != null) && (keyAry.length > 0)) {
			for (int i = 0; i < keyAry.length; i++) {
				Object obj = param.getValue(keyAry[i]);

				//boolean encYn = false;

				if (obj != null) {
					if ((obj instanceof ListParam)) {
						param.addValue(keyAry[i], encListParam((ListParam)obj, encMappingVO));
					} else {
						int index = encColList.indexOf(keyAry[i].toUpperCase());
						if (index >= 0) {
							try {
								/*try {
									System.out.println("seocowboy7");
									SafeDBUtil.sdbDecrypt((String)polyColList.get(index), obj.toString());
								} catch (Exception e) {
									encYn = true;
								}
								*/
								String encVal = "";
								/*if (encYn)
								{*/
									encVal = SafeDBUtil.sdbEncrypt((String)polyColList.get(index), obj.toString());

									param.addValue(keyAry[i], encVal);
								/*} else {
									param.addValue(keyAry[i], obj);
								}*/
							} catch (Exception e) {
								param.addValue(keyAry[i], obj);
							}
						}
					}
				}
			}
		}
		else {
			return param;
		}

		return param;
	}

	public ListParam encListParam(ListParam listParam, EncMappingVO mappingVO) throws Exception {
		ListParam tempListParam = listParam;
		try {
			if (listParam != null) {
				int rowSize = tempListParam.rowSize();

				if (rowSize > 0) {
					String[] columnAry = tempListParam.getColumns();

					HashMap encInfoMap = getEncInfo(mappingVO);
					ArrayList encColList = (ArrayList)encInfoMap.get("ENC_COL");
					ArrayList polyColList = (ArrayList)encInfoMap.get("POLY_COL");

					for (int i = 0; i < rowSize; i++) {
						for (int j = 0; j < columnAry.length; j++) {
							boolean encYn = false;

							int index = encColList.indexOf(columnAry[j].toUpperCase());

							if (index >= 0) {
								try {
									SafeDBUtil.sdbDecrypt((String)polyColList.get(index), (String)tempListParam.getValue(i, j));
								} catch (Exception e) {
									encYn = true;
								}
								String encVal = "";
								if (encYn) {
									encVal = SafeDBUtil.sdbEncrypt((String)polyColList.get(index), (String)tempListParam.getValue(i, j));
									tempListParam.setValue(i, j, encVal);
								} else {
									tempListParam.setValue(i, j, tempListParam.getValue(i, j));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ErrorLogger.error("======================================================");
			ErrorLogger.error("enc listParam Exception:" + e);
			ErrorLogger.error("======================================================");
			tempListParam = listParam;
		}

		return tempListParam;
	}
}
