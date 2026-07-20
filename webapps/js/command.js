//*〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
//* 전체 유효성 검사ㅋ! 120210.-JOON
//*〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓

function checkCommandParams(command, fields, fieldObjs, gubuns) {
	
	if(gubuns == undefined) { 
		
		gubuns = new Array(fields.length);
		
	}
	
	for(var i = 0; i < fields.length; i++) {
		if(!checkCommandParam(command, fields[i], fieldObjs[i].getAttribute('value'), gubuns[i])) {
			fieldObjs[i].focus();
			return false;
		}
	}
	return true;
}

function checkCommandParamsVal(command, fields, values, gubuns) {
	
	if(gubuns == undefined) { 
		
		gubuns = new Array(fields.length);
		
	}
	
	for(var i = 0; i < fields.length; i++) {
		if(!checkCommandParam(command, fields[i], values[i], gubuns[i])) {
			fieldObjs[i].focus();
			return false;
		}
	}
	return true;
}

//*〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
//* 유효성 검사! 120210.-JOON
//*〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓
function checkCommandParam(command, field, fieldvalue, gubun) {
 
    var result      = false;
    var ds_xml      = "";
    var parentPath  = "Xwing.parent";
    
    if(fieldvalue == undefined || fieldvalue == null) fieldvalue = '';
    if(fieldvalue.indexOf(' ') != -1) {
    	
    	alert(field+"항목에 공백을 제거해 주세요.");
    	return result;
    }

  //* 다이얼로그 새창으로 열렸을경우 부모창 데이터셋 찾기(추가)! 1202101444.-Y  
  //* 다이얼로그 새창으로 열렸을경우 부모창 데이터셋 찾기(수정)! 1202131252.-JOON   
    for(var i = 0; i < 10; i++){
        ds_xml = eval(parentPath).Xwing.getDataset('ds_xml');
        if (ds_xml == null) {
            parentPath += ".parent";
        } else {
            break;
        }
    }
    if (ds_xml == null) {
        alert("해당 객체[ds_xml]를 찾을 수 없습니다. \n관리자한테 문의하십시오.");
        return result;
    }

    var crow     = ds_xml.indexOfRow('command', command);
    var option   = "";
    var min      = "";
    var max      = "";
    var value    = "";
    var datatype = "";

    while (true) {
        var commadName = ds_xml.getValue(crow, 'command');
        var fieldName  = ds_xml.getValue(crow, 'field');

        if (command == commadName && field == fieldName) {
            datatype = ds_xml.getValue(crow, 'datatype');
            option   = ds_xml.getValue(crow, 'option');
            min      = ds_xml.getValue(crow, 'min');
            max      = ds_xml.getValue(crow, 'max');
            value    = ds_xml.getValue(crow, 'value');
            break;
        }else if(command != commadName){
        	return true;
        }
        crow += 1;
    }

    if (option == "M" && fieldvalue.trim() == "") {
        alert(field + "은 필수 입력항목입니다. \n꼭 입력하십시오.");        
        return result;
    }
 

    switch (datatype){
    case 'int':
        fieldvalue = parseInt(fieldvalue);
        if (min.length > 0 && fieldvalue < min) {
            alert(field + "항목에 입력하신  값(" + fieldvalue +")이 " + min +"값보다 작습니다. \n다시 입력하십시오.");
            return result;
        }

        if (max.length > 0 && fieldvalue > max) {
            alert(field + "항목에 입력하신 값("+ fieldvalue +")이 " + max + "값보다 큽니다. \n다시 입력하십시오.");
            return result;
        }

        break;

    case 'string':
        var fieldvalue_len = parseInt(checkValueLength(fieldvalue));
        if (min.length > 0 &&  fieldvalue_len < min) {
            alert(field +"에 입력하신 값의 길이(" + fieldvalue_len + ")는 " + min +"보다 작습니다. \n다시 입력하십시오.");
            return result;
        }

        if (max.length > 0 && fieldvalue_len > max) {
            alert(field +"에 입력하신 값의 길이(" + fieldvalue_len + ")는 " + max + "보다  큽니다. \n다시 입력하십시오.");
            return result;
        }

        if (value.length > 0 ){
            if (gubun == null) {
                var values = value.split("|");
                var cnt = 0;
                if (values.length > 1) {
                    for (var i = 0; i < values.length; i++){
                        if (fieldvalue == values[i]) {
                            break;
                        }
                        cnt += 1;
                    }
                    if (cnt == values.length) {
                        alert("입력하신 " +field +"의 값(" + fieldvalue + ")은  " + replaceAll(value, "|", ",")
                             + "값들에서 포함되어 있지 않습니다. \n다시 입력하거나 선택하십시오.");
                        return result;
                    }
                }
            } else {
                if (!checkValueChar(value, fieldvalue)) {
                    alert("해당 " + field + " 항목은 다음과 같은 문자들만 " + replaceAll(value, "|", ",")
                         + "입력할 수 있습니다. \n입력하신 값(" + fieldvalue + ")의 문자열에  해당 문자외 다른 문자를 내포하고 있습니다."
                         + "\n다시 입력하십시오.");
                    return result;
                }
            }
        }
        break;
    }
    result = true;
    return result;
}

 

function checkValueChar(value, fieldvalue) {

    var values = value.split("|");
    var result = false;

    for (var i = 0; i < fieldvalue.length; i++){
        for (var j = 0; j < values.length; j++) {
            if ( fieldvalue.charAt(i) != values[j]) {
                result = false;
            }else{
            	result = true;
            	break;
            }
        }
        if(result == false) return;
    }
    return true;
}

 

function checkValueLength(value){

    var li_byte     = 0;    
    var ls_one_char = "";

    for(var i=0; i< value.length; i++)
    {
       ls_one_char = value.charAt(i);
       if (escape(ls_one_char).length > 4) {    
          li_byte += 2;
       } else {
          li_byte++;
       };
    }
    return li_byte;
}

function replaceAll(str, orgstr, repstr){
    return str.split(orgstr).join(repstr);
}


function ifnull(obj_val) {
	if(obj_val.trim() == '' || obj_val.trim() == '-') {
		return 'NULL';
	}
	
	return obj_val;
}