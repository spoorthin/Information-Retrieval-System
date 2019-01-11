package assignment1;

import java.net.URL;

public class ItemUrl {
	
	URL url; 
	int depth;
	
	public ItemUrl(URL url, int depth){
		this.url=url;
		this.depth=depth;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}
	
	public int getDepth(){
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	@Override
	public String toString() {
		return this.url + "\t" + this.depth;
	}

	
	@Override
	public boolean equals(Object object) {
		boolean same = false;
		if (object != null && object instanceof ItemUrl) {

			if (this.url.toString().equals(((ItemUrl) object).url.toString())) {
				same = true;
			}
		}
		return same;
	}
}
