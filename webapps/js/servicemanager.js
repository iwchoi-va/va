// SERVICE CONFIG 셋팅

var serviceConfig = {
		/*
		 * [[engine]] webaction에서 분기 처리 될 command 지정
		 * : rfield와 동일하게 지정
		 */

		
		cmd : {
				recordlist      : "recordlist"	          // 녹취리스트(REC010)
		},
		

		sfield : {
			// -----------------쿼리 기본 가이드
			 
			  fn_query          : "select=sVal&from=fVal&where=wVal order by oVal&hliete-fields=hVal&offset=offVal&limit=lVal"
			
			, fn_select         : "select"
			, fn_from           : "from"	
			, fn_where          : "where"
			, fn_order			: "orderby"
			, fn_hliete			: "hliete-fields"
			, fn_offset			: "offset"
			, fn_limit			: "limit"	
			// ----------------------------------
			
			, order				: " order by value"
			
			, table				: "stt_search.value"	//볼륨.table
			, ucid_date         : "(UCID_DATE >= 'value1') and (UCID_DATE <= 'value2')"
			, keyword           : " c_val text_idx = 'value' allword"
			, user_id           : " and USER_ID in {value}"	
			, user_name         : " and USER_NAME = 'value'" 
			, org1_cd           : " and TREATYHQCD = 'value'"
			, org2_cd           : " and TREATYBRHCD in {value}"
			, org3_cd           : " and TREATYDEPTCD = 'value'"
			, duration          : " and (DURATION >= 'value1') and (DURATION <= 'value2')"
			, ced_no            : " and CON_ENT_DGN_NO like 'value*'"	
					
		}
};



