package com.dw.suppercms.infrastructure.webservice.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import com.dw.suppercms.domain.data.Datasource;
import com.dw.suppercms.domain.manu.Manuscript;
import com.dw.suppercms.domain.manu.ManuscriptMember;
import com.dw.suppercms.domain.manu.ManuscriptOrgan;
import com.dw.suppercms.domain.manu.ManuscriptService;
import com.dw.suppercms.domain.manu.ReplyInfo;
import com.dw.suppercms.domain.manu.SendUserService;
import com.dw.suppercms.domain.modules.Column;
import com.dw.suppercms.support.ColumnDto;
import com.dw.suppercms.support.ManuscriptDto;
import com.dw.suppercms.support.ManuscriptServiceDto;
import com.dw.suppercms.support.MemberDto;
import com.dw.suppercms.support.MemberServiceDto;
import com.dw.suppercms.support.ReplyDto;
import com.dw.suppercms.support.ServiceDto;

/**
 * 接口包需要的数据对象转换工具包
 * */
public class DataObjectTransformUtils {
	
	/**
	 * 稿件对象转换为稿件dto
	 * @param 稿件对象manuscript
	 * @param 稿件的数据源关系msList
	 * @param 稿件的批复记录replys
	 * */
	public static ManuscriptDto manuscriptToManuscriptDto(Manuscript manuscript,List<ManuscriptService> msList,List<ReplyInfo> replys){
		ManuscriptDto dto=new ManuscriptDto();
		try {
			BeanUtils.copyProperties(manuscript, dto);
			if(CollectionUtils.isEmpty(msList)){
				return dto;
			}
			//稿件数据源得到数据源的信息
			List<ServiceDto> services=new ArrayList<ServiceDto>();//数据源名称集合(所属栏目=数据源)
			ServiceDto serviceDto=null;
			for(ManuscriptService ms:msList){
				Datasource dataSource= ms.getDataSource();
				serviceDto=dataSourceToServiceDto(dataSource);
				services.add(serviceDto);
			}
			dto.setServices(services);
			//批复列表转换dto列表
			List<ReplyDto> replyList=new ArrayList<ReplyDto>();//数据源名称集合(所属栏目=数据源)
			ReplyDto replyDto=null;
			for(ReplyInfo reply:replys){
				replyDto=replyToReplyDto(reply);
				replyList.add(replyDto);
			}
			dto.setReplyList(replyList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	/**
	 * 稿件dto对象转换为稿件实体对象
	 * */
	public static Manuscript manuscriptDtoToManuscript(ManuscriptDto dto){
		Manuscript manuscript=new Manuscript();
		try {
			BeanUtils.copyProperties(dto, manuscript);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return manuscript;
	}
	
	/**
	 * 稿件批复对象转换为批复dto
	 * */
	public static ReplyDto replyToReplyDto(ReplyInfo reply){
		ReplyDto dto=new ReplyDto();
		try {
			if(reply!=null){
				BeanUtils.copyProperties(reply, dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	/**
	 * 数据源对象转换为数据源dto
	 * */
	public static ServiceDto dataSourceToServiceDto(Datasource dataSource){
		ServiceDto dto=new ServiceDto();
		try {
			dto.setId(dataSource.getId());
			dto.setTitle(dataSource.getTitle());
			dto.setDescription(dataSource.getDescription());
			dto.setOrigin(dataSource.getOrigin());
			dto.setSortId(dataSource.getSortId());
			dto.setModelId(dataSource.getModelId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	/**
	 * 栏目对象转换为栏目dto
	 * */
	public static ColumnDto columnToColumnDto(Column column){
		ColumnDto dto=new ColumnDto();
		try {
			dto.setId(column.getId());
			dto.setTitle(column.getTitle());
			dto.setDirName(column.getDirName());
			dto.setSiteName(column.getSite().getTitle());
			dto.setDatasourceName(column.getDatasource().getTitle());
			dto.setPublishHref(column.getDirWebpath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	/**
	 * 会员对象转换为会员dto
	 * */
	public static MemberDto memberToMemberDto(ManuscriptMember member){
		MemberDto dto=new MemberDto();
		try {
			ManuscriptOrgan organ=member.getOrgan();
			BeanUtils.copyProperties(member, dto,new String[]{"memberType","state","organ"});
			dto.setMemberType(member.getMemberType().toString());
			dto.setState(member.getState().toString());
			dto.setOrganName(organ.getOrganName());
			dto.setOrganAlreadyCount(organ.getAlreadyManuCount());
			dto.setOrganMaxCount(organ.getManuMaxCount());
			dto.setOrganLevel(organ.getOrganLevel().toString());
			dto.setAbortTime(organ.getAbortTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	/**
	 * 稿件数据源关系转换dto
	 * */
	public static ManuscriptServiceDto manuscriptServiceToManuscriptServiceDto(ManuscriptService ms){
		ManuscriptServiceDto dto=new ManuscriptServiceDto();
		try {
			dto.setManuId(ms.getManuId());
			dto.setDataSourceId(ms.getServiceId());
			dto.setDataSourceName(ms.getDataSource().getTitle());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	/**
	 * 会员数据源关系转换dto
	 * */
	public static MemberServiceDto sendUserServiceToMemberServiceDto(SendUserService sus){
		MemberServiceDto dto=new MemberServiceDto();
		try {
			dto.setMemberId(sus.getUserId());
			dto.setMemberName(sus.getMember().getMemberName());
			dto.setDataSourceId(sus.getServiceId());
			dto.setDataSourceName(sus.getDatasource().getTitle());
			dto.setAudit(sus.getIsAudit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
}
