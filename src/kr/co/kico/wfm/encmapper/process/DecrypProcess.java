package kr.co.kico.wfm.encmapper.process;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.kico.wfm.encmapper.vo.EncMappingVO;

import com.hansol.dbenc.util.SafeDBUtil;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;

public class DecrypProcess extends CommonProcess {
	public DecrypProcess() throws Exception {
		
	}

	public SQLParam decrypSQLParam(String queryId, SQLParam sqlParam) {
		try {
			if (sqlParam == null) {
				return sqlParam;
			}

			if (sqlParam != null)
				sqlParam = (SQLParam)decrypParam(queryId, sqlParam);
		} catch (Exception e) {
			ErrorLogger.error("======================================================");
			ErrorLogger.error("decryp SQLParam Exception:" + e);
			ErrorLogger.error("======================================================");

			return sqlParam;
		}

		return sqlParam;
	}

	public SQLParam[] decrypSQLParamArray(String queryId, SQLParam[] sqlparams) {
		SQLParam[] tempSqlParamAry = sqlparams;

		if (sqlparams == null) {
			return sqlparams;
		}
		
		try {
			for (int i = 0; i < tempSqlParamAry.length; i++)
				tempSqlParamAry[i] = decrypSQLParam(queryId, tempSqlParamAry[i]);
		} catch (Exception e) {
			tempSqlParamAry = sqlparams;
		}

		return tempSqlParamAry;
	}

	public Param decrypParam(String queryId, Param param) throws Exception {
		Param tempParam = param;

		EncMappingVO mappingVO = this.mapperConfig.getDecrypInfoByQueryId(queryId);
		try
		{
			if (mappingVO == null) {
				tempParam = param;
				//throw new Exception("query-id:" + queryId + " 에 대한 복호화 정보가 없습니다");
				return tempParam;
			}

			String[] keyAry = param.keys();

			HashMap decrypInfoMap = getEncInfo(mappingVO);
			ArrayList decrypColList = (ArrayList)decrypInfoMap.get("ENC_COL");
			ArrayList polyColList = (ArrayList)decrypInfoMap.get("POLY_COL");

			if ((keyAry != null) && (keyAry.length > 0)) {
				for (int i = 0; i < keyAry.length; i++) {
					Object obj = tempParam.getValue(keyAry[i]);

					if (obj != null) {
						if ((obj instanceof ListParam)) {
							param.addValue(keyAry[i], decrypListParam((ListParam)obj, mappingVO));
						} else {
							int index = decrypColList.indexOf(keyAry[i].toUpperCase());

							if (index >= 0) {
								String decrypVal = SafeDBUtil.sdbDecrypt((String)polyColList.get(index), obj.toString());

								tempParam.addValue(keyAry[i], decrypVal);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ErrorLogger.error("======================================================");
			ErrorLogger.error("decryp Param Exception:" + e);
			ErrorLogger.error("======================================================");

			tempParam = param;
		}

		return tempParam;
	}

	public ListParam decrypListParam(ListParam listParam, EncMappingVO mappingVO) throws Exception {
		ListParam tempListParam = listParam;
		try {
			if (listParam != null) {
				int rowSize = tempListParam.rowSize();

				if (rowSize > 0) {
					String[] columnAry = tempListParam.getColumns();

					HashMap decrypInfoMap = getEncInfo(mappingVO);
					ArrayList decrypColList = (ArrayList)decrypInfoMap.get("ENC_COL");
					ArrayList polyColList = (ArrayList)decrypInfoMap.get("POLY_COL");

					for (int i = 0; i < rowSize; i++) {
						for (int j = 0; j < columnAry.length; j++) {
							int index = decrypColList.indexOf(columnAry[j].toUpperCase());

							//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> column:" + columnAry[j].toUpperCase() + "        index:" + index);

							if (index >= 0) {
								String v_value = "";
								
								if (tempListParam.getValue(i, j) instanceof Clob) {
									v_value = getClobConvertToStr((Clob)tempListParam.getValue(i, j));
								} else {
									v_value = (String)tempListParam.getValue(i, j);
								}
								String decrypVal = SafeDBUtil.sdbDecrypt((String)polyColList.get(index), v_value);
								//String decrypVal = SafeDBUtil.sdbDecrypt((String)polyColList.get(index), (String)tempListParam.getValue(i, j));
								tempListParam.setValue(i, j, decrypVal);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			ErrorLogger.error("======================================================");
			ErrorLogger.error("decryp listParam Exception:" + e);
			ErrorLogger.error("======================================================");

			tempListParam = listParam;
		}

		return tempListParam;
	}
	
	public String getClobConvertToStr(Clob clob) {
		int size;
		String str = "";

		try {
			if (clob == null) {
				size = 0;
			} else {
				size = (int) clob.length();
			}

			if (size != 0) {
				str = clob.getSubString(1, size);
			} else {
				str = "";
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return str;
	}
}
