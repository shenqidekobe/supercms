package com.dw.suppercms.infrastructure.web.data;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.dw.framework.core.SpringHelper;
import com.dw.suppercms.application.data.DataService;
import com.dw.suppercms.application.data.TagService;
import com.dw.suppercms.application.modules.ProduceFileService;
import com.dw.suppercms.domain.data.Tag;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.domain.modules.Module;
import com.dw.suppercms.domain.system.SysLogInfo.OPER_TYPE;
import com.dw.suppercms.infrastructure.annotation.SystemLog;
import com.dw.suppercms.infrastructure.utils.Thumbnail;
import com.dw.suppercms.infrastructure.web.BaseController;
import com.dw.suppercms.infrastructure.web.ui.Datatable;
import com.dw.suppercms.produce.MakeFileService;
import com.dw.suppercms.produce.data.DataRecord;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * 
 * DataController
 *
 * @author osmos
 * @date 2015年8月18日
 */
@RestController
@RequestMapping("/datas")
public class DataController extends BaseController {

	@Autowired
	private DataService dataService;
	@Autowired
	private TagService tagService;
	@Autowired
	MultipartResolver multipartResolver;
	@Resource
	private ProduceFileService produceFileService;
	@Resource
	private MakeFileService makeFileService;

	// retrieve all datas as datatable
	@RequestMapping(value = "/datas", method = { RequestMethod.GET }, params = { "datatable" })
	public Datatable datatable(int draw, Long datasourceId, String title, Date startTime, Date endTime,
			String checkStatus, int start,
			int length) {
		if (datasourceId == null) {
			return new Datatable(draw, 0, 0, null);
		}
		Map<String, Object> condition = new HashMap<>();
		condition.put("firstResult", start);
		condition.put("maxResults", length);
		condition.put("title", title);
		condition.put("startTime", startTime);
		condition.put("endTime", endTime);
		condition.put("checkStatus", checkStatus);
		List<Map<String, Object>> list = dataService.paginateForManage(datasourceId, condition);
		int count = dataService.countForManage(datasourceId, condition);

		return new Datatable(draw, count, count, list);
	}

	// retreive a data
	@RequestMapping(value = "/datas/{id}", method = { RequestMethod.GET }, params = { "datasourceId" })
	public Map<String, Object> id(Long id, Long datasourceId) {
		return dataService.retrieve(datasourceId, id);
	}

	// retrieve tags on data
	@RequestMapping(value = "/datas", method = { RequestMethod.GET }, params = { "tags" })
	public List<Tag> tags(Long datasourceId, Long dataId) {
		List<Tag> list = tagService.tagsOnData(datasourceId, dataId);
		return list;
	}

	// create data
	@RequestMapping(value = "/datas", method = { RequestMethod.POST })
	@SystemLog(operation = "新增数据", operType = OPER_TYPE.create)
	@Description("创建数据")
	@RequiresPermissions({ "app.datas.data.create" })
	public void create(@RequestBody Map<String, Object> data) {
		dataService.create(data);
		data.remove("content");
		requestBodyAsLog(data);
	}

	// save data
	@RequestMapping(value = "/datas/{id}", method = { RequestMethod.PUT })
	@SystemLog(operation = "编辑数据", operType = OPER_TYPE.save)
	@Description("修改数据")
	@RequiresPermissions({ "app.datas.data.save" })
	public void save(@RequestBody Map<String, Object> data, HttpServletRequest request) {
		dataService.update(data);
		data.remove("content");
		requestBodyAsLog(data);
	}

	// remove data
	@RequestMapping(value = "/datas", method = { RequestMethod.DELETE }, params = { "id", "datasourceId" })
	@SystemLog(operation = "删除数据", operType = OPER_TYPE.remove)
	@Description("删除数据")
	@RequiresPermissions({ "app.datas.data.remove" })
	public void remove(Long id, Long datasourceId) {
		dataService.delete(id, datasourceId);
	}

	// stick data
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "stick" })
	@SystemLog(operation = "置顶数据", operType = OPER_TYPE.stick)
	@Description("置顶数据")
	@RequiresPermissions({ "app.datas.data.stick" })
	public void stick(Long id, Long datasourceId) {
		dataService.stick(id, datasourceId);
	}

	// cancle stick data
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "unstick" })
	@SystemLog(operation = "取消置顶数据", operType = OPER_TYPE.unstick)
	@Description("取消置顶数据")
	@RequiresPermissions({ "app.datas.data.unstick" })
	public void unstick(Long id, Long datasourceId) {
		dataService.unstick(id, datasourceId);
	}

	// submit data check
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "submit" })
	@SystemLog(operation = "提交至一审", operType = OPER_TYPE.dataReview,operDesc = "数据id=%s数据源datasourceId=%s提交至一审")
	@Description("提交至一审")
	@RequiresPermissions({ "app.datas.data.create" })
	public void submit(Long id, Long datasourceId) {
		dataService.submit(datasourceId, id);
	}

	// pass1 data
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "pass1" })
	@SystemLog(operation = "一审通过", operType = OPER_TYPE.dataReview,operDesc = "数据id=%s数据源datasourceId=%s一审通过")
	@Description("一审通过")
	@RequiresPermissions({ "app.datas.data.pass1" })
	public void pass1(Long id, Long datasourceId) {
		dataService.pass1(datasourceId, id);
	}

	// refuse1 data
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "refuse1" })
	@SystemLog(operation = "一审拒绝", operType = OPER_TYPE.dataReview,operDesc = "数据id=%s数据源datasourceId=%s一审拒绝")
	@Description("一审拒绝")
	@RequiresPermissions({ "app.datas.data.pass1" })
	public void refuse1(Long id, Long datasourceId) {
		dataService.refuse1(datasourceId, id);
	}

	// pass2 data
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "pass2" })
	@SystemLog(operation = "二审通过", operType = OPER_TYPE.dataReview,operDesc = "数据id=%s数据源datasourceId=%s二审通过")
	@Description("二审通过")
	@RequiresPermissions({ "app.datas.data.pass2" })
	public void pass2(Long id, Long datasourceId) {
		dataService.pass2(datasourceId, id);
	}

	// refuse2 data
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "refuse2" })
	@SystemLog(operation = "二审拒绝", operType = OPER_TYPE.dataReview,operDesc = "数据id=%s数据源datasourceId=%s二审拒绝")
	@Description("二审拒绝")
	@RequiresPermissions({ "app.datas.data.pass2" })
	public void refuse2(Long id, Long datasourceId) {
		dataService.refuse2(datasourceId, id);
	}

	// copy data to datasources
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "copy" })
	@SystemLog(operation = "复制数据", operType = OPER_TYPE.copy, operDesc = "数据id=%s从数据源datasourceId=%s复制到数据源datasourceIds=%s")
	@Description("复制数据")
	@RequiresPermissions({ "app.datas.data.copy" })
	public void copy(Long id, Long datasourceId, String datasourceIds) {
		List<Long> lDatasourceIds = Lists.newArrayList(datasourceIds.split(",")).stream().map(d -> Long.parseLong(d))
				.collect(toList());
		dataService.copy(id, datasourceId, lDatasourceIds);

	}

	// move data to datasources
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "move" })
	@SystemLog(operation = "移动数据", operType = OPER_TYPE.move, operDesc = "数据id=%s从数据源datasourceId=%s移动到数据源datasourceIds=%s")
	@Description("移动数据")
	@RequiresPermissions({ "app.datas.data.move" })
	public void move(Long id, Long datasourceId, String datasourceIds) {
		List<Long> lDatasourceIds = Lists.newArrayList(datasourceIds.split(",")).stream().map(d -> Long.parseLong(d))
				.collect(toList());
		dataService.move(id, datasourceId, lDatasourceIds);

	}

	// ref data to datasources
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "ref" })
	@SystemLog(operation = "引用数据", operType = OPER_TYPE.ref, operDesc = "栏目columnId=%s的数据从数据源datasourceId=%s引用到数据源datasourceIds=%s")
	@Description("引用数据")
	@RequiresPermissions({ "app.datas.data.ref" })
	public void ref(Long id, Long columnId, Long datasourceId, String datasourceIds) {
		List<Long> lDatasourceIds = Lists.newArrayList(datasourceIds.split(",")).stream().map(d -> Long.parseLong(d))
				.collect(toList());
		dataService.ref(id, columnId, datasourceId, lDatasourceIds);

	}

	// deal tag to data
	@RequestMapping(value = "/datas", method = { RequestMethod.PUT }, params = { "dealTags" })
	@SystemLog(operation = "数据打标签", operType = OPER_TYPE.dealTags, operDesc = "数据id=%s,数据源datasourceId=%s,打入标签tagIds=%s")
	@Description("打标签")
	@RequiresPermissions({ "app.datas.data.dealTags" })
	public void dealTags(Long id, Long datasourceId, String tagIds) {
		List<Long> lTagIds = Lists.newArrayList();
		if (!StringUtils.isEmpty(tagIds)) {
			lTagIds = Lists.newArrayList(tagIds.split(",")).stream().map(sTagId -> Long.parseLong(sTagId))
					.collect(toList());
		}
		tagService.dealTags(datasourceId, id, lTagIds);
	}
	
	//preview
	@RequestMapping(value = "/datas", method = { RequestMethod.GET }, params = { "preview" })
	public ModelAndView  preview(Model model,Long id,Long columnId,Long datasourceId,HttpServletResponse rsp){
		DataRecord dataRecord=produceFileService.findDataRecord(datasourceId, id);
		Column column=produceFileService.findColumnById(columnId);
		InputStream is= makeFileService.previewContentFile(dataRecord, column);
		String html="";
		try {
			html=IOUtils.toString(is, "UTF-8");
			String address=Module.MAKE_DIR+column.getSite().getDirName();
			html=html.replaceAll("<link href=\"", "<link href=\""+address+"");
			html=html.replaceAll("<script src=\"", "<script src=\""+address+"");
			//html=html.replaceAll("<!--#include virtual=\"", "<link rel=\"import\" href=\"/sites/"+column.getSite().getDirName());
			//html=html.replaceAll("-->", "/>");
			rsp.getWriter().println(html);
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.addAttribute("html", html);
		return new ModelAndView("preview");
	}

	// upload header pic
	@RequestMapping(value = "/datas/headerPic")
	public String uploadHeaderPic(HttpServletRequest request) throws Exception {
		String headerPic = "";
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> iter = multiRequest.getFileNames();
			if (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile(iter.next().toString());
				if (file != null) {
					Date now = new Date();
					String rootPath = SpringHelper.servletContext.getRealPath("/");
					String uploadDir = Module.IMAGE_UPLOAD_DIR;
					String dateDir = DateFormatUtils.format(now, "yyyy-MM-dd");
					String imageDir = uploadDir + "/" + dateDir;

					// process origin pic
					String extendName = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
					String fileName = now.getTime() + "." + extendName;
					String relativePath = imageDir + "/" + fileName;
					String savePath = rootPath + relativePath;
					File saveFile = new File(savePath);
					Files.createParentDirs(saveFile);
					file.transferTo(saveFile);

					// process big pic
					String fileNameOfBig = StringUtils.replace(fileName, ".", ("_" + Module.BIG_PIC_WIDTH + "."));
					String relativePathOfBig = imageDir + "/" + fileNameOfBig;
					String savePathOfBig = rootPath + relativePathOfBig;
					File saveFileOfBig = new File(savePathOfBig);
					Thumbnail.thumbnailImg(saveFile, saveFileOfBig, Module.BIG_PIC_WIDTH, Module.BIG_PIC_HEIGHT);

					// process small pic
					String fileNameOfSmall = StringUtils.replace(fileName, ".", ("_" + Module.SMALL_PIC_WIDTH + "."));
					String relativePathOfSmall = imageDir + "/" + fileNameOfSmall;
					String savePathOfSmall = rootPath + relativePathOfSmall;
					File saveFileOfSmall = new File(savePathOfSmall);
					Thumbnail.thumbnailImg(saveFile, saveFileOfSmall, Module.SMALL_PIC_WIDTH, Module.SAMLL_PIC_HEIGHT);

					headerPic = relativePath.replace("/resource", "");
				}
			}
		}
		return headerPic;
	}
}
