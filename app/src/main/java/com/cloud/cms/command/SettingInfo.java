package landytest3;

import java.io.Serializable;

/**
* @author chunlan.chen
* @Date 2019年3月7日
* @Description
*/
public class SettingInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int total;
	private SettingInfoDetails rows;
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public SettingInfoDetails getRows() {
		return rows;
	}
	public void setRows(SettingInfoDetails rows) {
		this.rows = rows;
	}
}
