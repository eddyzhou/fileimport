package org.zq.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InitData extends SQLAction {
	
	public InitData(){
		super();
	}
	
	private static final String SQL_INSERT_T_PREFIXS_INFO = "insert into t_prefixs_info(prefix,city_id,networktypeid,isenabled,ismgid,smscid,areacode,province_id,districtid,miscid,userbrand,mmsc_id) " +
			"values (?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public void batchInsert(List<String[]> data) throws SQLException {
		this.batch(SQL_INSERT_T_PREFIXS_INFO, this.parseParameter(data));
	}
	
	private List<String[]> fomatData(String file) throws IOException {
		List<String[]> list = new ArrayList<String[]>();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = in.readLine()) != null) {
			if (!"".equals(line.trim())) {
				String[] lineData = line.split("\\s{1,}");
				String[] needData = {lineData[0], lineData[3]};
				list.add(needData);
			}
			
		}
		
		return list;
	}
	
	private List<Object[]> parseParameter(List<String[]> params) {
		List<Object[]> list = new ArrayList<Object[]>();
		for (String[] param : params) {
			Object[] objs = new Object[12];
			objs[0] = param[0]; // prefix
			objs[1] = 0; // city_id
			objs[2] = 0; // networktypeid
			objs[3] = 1; // isenabled
			objs[4] = 37102; // ismgid
			objs[5] = 1; // smscid
			objs[6] = param[1]; // areacode
			objs[7] = 10; // province_id
			objs[8] = "001"; // districtid
			objs[9] = "0010"; // miscid
			objs[10] = "1"; // userbrand
			objs[11] = "0"; // mmsc_id
			
			list.add(objs);
		}
		return list;
	}
	
	public static void main(String[] args) {
		InitData dataInit = new InitData();
		try {
			dataInit.batchInsert(dataInit.fomatData("F:\\prefix.txt"));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
