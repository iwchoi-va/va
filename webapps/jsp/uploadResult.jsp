<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="org.json.JSONArray"%>
<%@ page import="org.json.JSONException"%>
<%@ page import="org.json.JSONObject"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.disk.*"%>
<%@ page import="org.apache.commons.fileupload.portlet.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.*"%>
<%@ page import="org.apache.commons.fileupload.util.*"%>
<%@ page import="jxl.Sheet"%>
<%@ page import="jxl.Workbook"%>
<%@ page import="jxl.read.biff.BiffException"%>
<%@ page import="jxl.write.Label"%>
<%@ page import="jxl.write.WritableSheet"%>
<%@ page import="jxl.write.WritableWorkbook"%>
<%@ page import="jxl.write.WriteException"%>
<%@ page import="jxl.write.biff.RowsExceededException"%>
<%@ page import="com.locus.jedi.transfer.ListParam"%>
<%@ page import="com.locus.jedi.transfer.Param"%>
<%@ page import="com.locus.jedi.transfer.ParamException"%>
<%@ page import="com.locus.jedi.log.ErrorLogger"%>
<%@ page import="cs.com.util.SecurityUtil"%>
<%!public String delete(HttpServletRequest request, String filePath,
			String filename, String key) {
		String realPath = request.getRealPath(filePath);
		File fl = new File(realPath + "/" + filename);
		String json = null;
		ErrorLogger
				.debug("-------------------- delete file --------------------");
		if (fl.exists()) {
			ErrorLogger.debug("exist file");
			Boolean result = fl.delete();
			json = "{deleteResult : " + result + "," + " filename:'" + filename
					+ "'" + "}";
		} else {
			ErrorLogger.debug("no exist file");
			json = "{deleteResult : false," + " filename:'" + filename + "'"
					+ " resultMessage : 'no exist file'}";
		}
		ErrorLogger.debug(json);
		return json;
	}

	public String[] arrayResult(HttpServletRequest req, String filename,
			String key, String extension, String filePath, String remoteFileName) {
		String realPath = req.getRealPath(filePath);
		File fl = new File(realPath + "/" + remoteFileName);
		String array = null;
		ErrorLogger
				.debug("-------------------- Start ArrayResult --------------------");
		if (fl.exists()) {
			array = "true," + filename + "," + fl.length() + "," + key + ","
					+ extension + "," + filePath + "," + remoteFileName + "";
		} else {
			array = "false," + filename + ",0,'',''," + filePath + "," + "";
		}
		ErrorLogger.debug("file info : " + array);
		ErrorLogger
				.debug("-------------------- End ArrayResult --------------------");
		return array.split(",");
	}

	public String processFormField(FileItem item) {
		// Process a regular form field
		String name = "";
		String value = "";
		if (item.isFormField()) {
			name = item.getFieldName();
			value = item.getString();
		}

		return ",'" + name + "':'" + value + "'";
	}

	public static String stringReplace(String str) {
		int str_length = str.length();
		String strlistchar = "";
		String str_imsi = "";
		String[] filter_word = { "", "\\.", "\\?", "\\/", "\\~", "\\!", "\\@",
				"\\#", "\\$", "\\%", "\\^", "\\&", "\\*", "\\(", "\\)", "\\_",
				"\\+", "\\=", "\\|", "\\\\", "\\}", "\\]", "\\{", "\\[",
				"\\\"", "\\'", "\\:", "\\;", "\\<", "\\,", "\\>", "\\.", "\\?",
				"\\/" };

		for (int i = 0; i < filter_word.length; i++) {
			str_imsi = str.replaceAll(filter_word[i], "");
			str = str_imsi;
		}
		return str;
	}

	public String[] processUploadedFile(HttpServletRequest req, FileItem item,
			String filePath) {
		ErrorLogger
				.debug("-------------------- Start processUploadedFile --------------------");
		// Process a file upload
		String fieldName = "";
		String fileName = "";
		String contentType = "";
		Calendar cal = Calendar.getInstance();
		String key = "";
		String extension = "";
		String remoteFileName = "";
		String[] result = new String[7];
		String str = String.format("%04d%02d%02d%02d%02d%02d",
				cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1),
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		if (!item.isFormField()) {
			fieldName = item.getFieldName();
			fileName = item.getName();
			String[] dis = fileName.split("\\.");
			contentType = item.getContentType();
			boolean isInMemory = item.isInMemory();
			long sizeInBytes = item.getSize();

			ErrorLogger.debug("file full name : " + fileName);
			ErrorLogger.debug("file name : " + dis[0]);
			ErrorLogger.debug("file extension : " + dis[1]);
			ErrorLogger.debug("file size : " + item.getSize());
			if (filePath == null || "".equals(filePath)) {
				filePath = "uploadFiles";
			}

			String dirpath = getServletContext().getRealPath("") + "/"
					+ filePath;

			File dir = new File(dirpath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			ErrorLogger.debug("file write path : " + dirpath);

			//FileUploadResultXwingWebAction test = new FileUploadResultXwingWebAction();
			try {
				//remoteFileName = dis[0] + "_" + str)+ "." + dis[1];
				//remoteFileName = SecurityUtil.getCryptoMD5String(dis[0] + "_" + str);
				key = stringReplace(SecurityUtil.getCryptoMD5String(dis[0]
						+ "_" + str));
				extension = dis[dis.length - 1];
				remoteFileName = key + "." + extension;
				ErrorLogger.debug("remote file name : " + remoteFileName);
				item.write(new File(dirpath, remoteFileName));
				result = arrayResult(req, fileName, key, extension, filePath,
						remoteFileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ErrorLogger
				.debug("-------------------- End processUploadedFile --------------------");
		return result;
	}

	public String[] processUploadedFileNoSecurity(HttpServletRequest req, FileItem item,
			String filePath) {
		ErrorLogger
				.debug("-------------------- Start processUploadedFileNoSecurity --------------------");
		// Process a file upload
		String fieldName = "";
		String fileName = "";
		String contentType = "";
		Calendar cal = Calendar.getInstance();
		String key = "";
		String extension = "";
		String remoteFileName = "";
		String[] result = new String[7];
		String str = String.format("%04d%02d%02d%02d%02d%02d",
				cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1),
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		if (!item.isFormField()) {
			fieldName = item.getFieldName();
			fileName = item.getName();
			String[] dis = fileName.split("\\.");
			contentType = item.getContentType();
			boolean isInMemory = item.isInMemory();
			long sizeInBytes = item.getSize();

			ErrorLogger.debug("file full name : " + fileName);
			ErrorLogger.debug("file name : " + dis[0]);
			ErrorLogger.debug("file extension : " + dis[1]);
			ErrorLogger.debug("file size : " + item.getSize());
			if (filePath == null || "".equals(filePath)) {
				filePath = "uploadFiles";
			}

			String dirpath = getServletContext().getRealPath("") + "/"
					+ filePath;

			File dir = new File(dirpath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			ErrorLogger.debug("file write path : " + dirpath);

			//FileUploadResultXwingWebAction test = new FileUploadResultXwingWebAction();
			try {
				//remoteFileName = dis[0] + "_" + str)+ "." + dis[1];
				//remoteFileName = SecurityUtil.getCryptoMD5String(dis[0] + "_" + str);
				//key = stringReplace(SecurityUtil.getCryptoMD5String(dis[0]+ "_" + str));
				key = dis[0]+ "_" +str;
				extension = dis[dis.length - 1];
				remoteFileName = key + "." + extension;
				ErrorLogger.debug("remote file name : " + remoteFileName);
				item.write(new File(dirpath, remoteFileName));
				result = arrayResult(req, fileName, key, extension, filePath,
						remoteFileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ErrorLogger
				.debug("-------------------- End processUploadedFileNoSecurity --------------------");
		return result;
	}
	
	public Param readSheet(String dir) {
		ErrorLogger.debug("-------------------- Start readSheet --------------------");
		ErrorLogger.debug("Excel File Name : " + dir);
		Param result = null;
		Workbook workbook = null;
		Sheet sheet = null;
		String[] columns = null;
		Param param = null;
		ListParam listparam = null;
		int rows = 0;
		int cols = 0;
		String contents = "";
		try {
			workbook = Workbook.getWorkbook(new File(dir));
			result = new Param();
			param = new Param();			
			ErrorLogger.error("Sheet Length : " + workbook.getSheets().length);
			int sheetcnt = 0;
			for (int i = 0; i < workbook.getSheets().length; i++) {
				sheet = workbook.getSheet(i);
				rows = sheet.getRows();
				cols = sheet.getColumns();
				columns = new String[cols];
				ErrorLogger.debug("Start Sheet" + i + "....Column Count : "
						+ cols);
				if (cols == 0) {
					ErrorLogger.debug("End Sheet" + i + "....");
					continue;
				}

				for (int j = 0; j < cols; j++) {
					columns[j] = "COL" + j;
				}

				listparam = new ListParam(columns);
				for (int j = 0; j < rows; j++) {
					ErrorLogger.debug("Sheet Row length : " + sheet.getRow(j).length);
					for (int k = 0; k < sheet.getRow(j).length; k++) {
						contents = sheet.getRow(j)[k].getContents();
						param.addValue(columns[k], (contents == null || contents.length() == 0 ? "" : contents));
					}
					listparam.addParam(param);
					param.clear();
				}

				for (int j = 0; j < listparam.rowSize(); j++) {
					for (int k = 0; k < listparam.colSize(); k++) {
						if (listparam.getValue(j, k) == null) {
							listparam.setValue(j, k, "");
						}
					}
				}
				result.addValue("Sheet" + i, listparam);
				sheetcnt++;
				ErrorLogger.debug("End Sheet" + i + "....");
			}
			result.addValue("COUNT", sheetcnt);
			ErrorLogger.debug("-------------------- End readSheet --------------------");
		} catch (BiffException e) {
			ErrorLogger.debug(e.getMessage());
			e.printStackTrace();	
		} catch (IOException e) {
			ErrorLogger.debug(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			ErrorLogger.debug(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}%>