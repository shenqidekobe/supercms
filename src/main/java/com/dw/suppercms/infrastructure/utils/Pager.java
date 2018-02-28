package com.dw.suppercms.infrastructure.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * pager
 */
public class Pager implements Serializable {

	private static final long serialVersionUID = 2041867004894668270L;
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Pager.class);

	/** 默认页码列表大小 */
	public final static int DEFAULT_DISPLAY_PAGETHS_COUNT = 6;
	
	Integer startIndex = 0;
	/** 分页大小 */
	Integer pageSize = 10;
	/** 排序字段 */
	String sort = "createTime";
	/** 排序方向 */
	String dir = "desc";
	/** 当前页码 */
	Integer pageth = 1;
	/** 页码列表 */
	List<Integer> pageths;
	/** 总页码数 */
	Integer pageCount = 0;
	/** 总记录数 */
	Long rowCount = 0L;	
	
	public Pager() {}

	public Pager(Integer startIndex, Integer pageSize) {
		setStartIndex(startIndex);
		setPageSize(pageSize);
	}

	public Pager(Integer startIndex, Integer pageSize, String sort, String dir) {
		setStartIndex(startIndex);
		setPageSize(pageSize);
		setSort(sort);
		setDir(dir);
	}

	public void calcPageCount(Long rowCount) {
		setRowCount(rowCount);
		if (pageSize != 0) {
			pageCount = rowCount.intValue() / pageSize;
			if (pageCount * pageSize < rowCount) {
				pageCount++;
			}
		}
		if (rowCount == 0) {
			pageth = 1;
		}
		calcPageths();

	}

	private void calcPageths() {
		int first = this.pageth - DEFAULT_DISPLAY_PAGETHS_COUNT / 2;
		if (first < 2) {
			first = 1;
		}
		int length = 0;
		if (first + DEFAULT_DISPLAY_PAGETHS_COUNT <= this.pageCount) {
			length = DEFAULT_DISPLAY_PAGETHS_COUNT;
		} else {
			length = this.pageCount - first + 1;
		}
		this.pageths = new ArrayList<Integer>();

		for (int i = 0; i < length; i++) {
			this.pageths.add(first + i);
		}
		if(!this.pageths.isEmpty() && pageths.get(0)!=1){
		    this.pageths.remove(0);
		    this.pageths.add(0, 1);
		}
		if(!this.pageths.isEmpty() && pageths.get(this.pageths.size()-1) != this.pageCount){
		    this.pageths.add(this.getPageCount());
		}
		
		//this.logger.debug("pageths:" + ArrayUtils.toString(this.pageths));
	}
	
	public void configPages(Integer pages){
		if(pages < getPageCount()){
			calcPageCount((long)pages * getPageSize());
		}
	}

	
	public Integer getPrev(){
		Integer prev = getPageth() -1;
		if(prev < 1){
			prev = 1;
		}
		return prev;
	}
	
	public Integer getNext(){
		Integer next = getPageth() + 1;
		if(next > getPageCount()){
			next = getPageCount();
		}
		return next;
	}
	
	public Integer getPageth() {return pageth;}
	public void setPageth(Integer pageth) {
		this.pageth = pageth;
		Integer ps = getPageSize();
		startIndex = (getPageth() - 1) * ps;
	}

	public Integer getPageCount() {return pageCount;}
	public void setPageCount(Integer pageCount) {this.pageCount = pageCount;}
	
	public Integer getPageSize() {return pageSize;}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		setPageth(getPageth());
	}

	public Long getRowCount() {return rowCount;}
	public void setRowCount(Long rowCount) {this.rowCount = rowCount;}

	public String getSort() {return sort;}
	public void setSort(String sort) {
		if("undefined".equals(sort)||"null".equals(sort)){
        	sort = null;
        }
		this.sort = sort;
	}

	public Integer getStartIndex() {return startIndex;}
	public void setStartIndex(Integer startIndex) {this.startIndex = startIndex;}

	public String getDir() {return dir;}
	public void setDir(String dir) {this.dir = dir;}

	public List<Integer> getPageths() {return this.pageths;}
	public void setPageths(List<Integer> pageths) {this.pageths = pageths;}
	
}
