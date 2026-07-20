package kr.co.kico.wfm.encmapper.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import kr.co.kico.wfm.encmapper.config.WfmEncMapperConfig;
import kr.co.kico.wfm.encmapper.util.KicoStringUtil;
import kr.co.kico.wfm.encmapper.vo.EncMappingVO;

public class CommonProcess {
	protected WfmEncMapperConfig mapperConfig;

	public CommonProcess() throws Exception {
		this.mapperConfig = WfmEncMapperConfig.getInstance();
	}

	public HashMap getEncInfo(EncMappingVO mappingVO) {
		HashMap resultMap = new HashMap();

		String encColumns = KicoStringUtil.nvl(mappingVO.getColumns(), "");
		String polyColumns = KicoStringUtil.nvl(mappingVO.getInitechPolyColumns(), "");

		resultMap.put("ENC_COL", strAryToList(encColumns));
		resultMap.put("POLY_COL", strAryToList(polyColumns));

		return resultMap;
	}

	private ArrayList strAryToList(String val) {
		ArrayList resultList = new ArrayList();

		StringTokenizer token = new StringTokenizer(val, ",");

		while (token.hasMoreElements()) {
			resultList.add(token.nextToken().toUpperCase());
		}

		return resultList;
	}
}
