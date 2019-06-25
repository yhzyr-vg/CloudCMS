package landytest3;

import java.io.Serializable;

/**
* @author chunlan.chen
* @Date 2019年3月7日
* @Description
*/
public class SettingInfoDetails implements Serializable{

	private Integer id;
	private Integer vol;
	private Integer maxvol;
	private Integer status;
	private String pic;
	private String video;
	private String welcomepic;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getVol() {
		return vol;
	}
	public void setVol(Integer vol) {
		this.vol = vol;
	}
	public Integer getMaxvol() {
		return maxvol;
	}
	public void setMaxvol(Integer maxvol) {
		this.maxvol = maxvol;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getWelcomepic() {
		return welcomepic;
	}
	public void setWelcomepic(String welcomepic) {
		this.welcomepic = welcomepic;
	}	
}



