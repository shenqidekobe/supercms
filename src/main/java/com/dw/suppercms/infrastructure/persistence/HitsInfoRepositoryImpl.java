package com.dw.suppercms.infrastructure.persistence;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.plugin.HitsInfo;
import com.dw.suppercms.domain.plugin.HitsInfoRepository;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.support.ManuscriptDto.ManuStatus;

@Repository
public class HitsInfoRepositoryImpl extends GenericRepositoryImpl<HitsInfo,Long> implements HitsInfoRepository{
	
	@SuppressWarnings("unchecked")
	@Override
	public List<HitsInfo> findHitsTopList(final String siteId,final String columnId,final boolean isOrgan,
			final String startTime,final String endTime,final Pager pager){
			String sql="select t.HID,t.COUNT,t.LINK,t.TITLE,t.ORGAN_ID as userName,count(h.HID) as ps from t_c_hits t inner join t_c_hits_history h on t.HID=h.HID ";
			if(StringUtils.isNotEmpty(siteId)){
				sql+="  and t.SITE_ID in ("+siteId+")";
			}
			if(StringUtils.isNotEmpty(columnId)){
				sql+="  and t.COLUMN_ID in ("+columnId+")";
			}
			if(StringUtils.isNotEmpty(startTime)){
				sql+=" and h.CREATE_TIME>'"+startTime+"'";
			}
			if(StringUtils.isNotEmpty(endTime)){
				sql+=" and h.CREATE_TIME<'"+endTime+"'";
			}
			if(isOrgan){
				sql+=" and t.MANU_ID IS NOT NULL ";
				sql+=" group by t.ORGAN_ID order by ps "+pager.getDir();//按单位用户分组
			}else{
				sql+=" group by t.RECORD_ID order by ps "+pager.getDir();  //按文章分组
			}
			
			Query query=getSession().createSQLQuery(sql);
			query.setMaxResults(pager.getPageSize());
			query.setFirstResult(pager.getStartIndex());
			
			query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
	
			List<Map<String, Object>> list = (List<Map<String, Object>>) query.list();
	
			List<HitsInfo> result = new ArrayList<HitsInfo>();
	
			try {
				PropertyDescriptor[] props = Introspector
						.getBeanInfo(HitsInfo.class)
						.getPropertyDescriptors();
	
				for (Map<String, Object> map : list) {
					HitsInfo t = HitsInfo.class.newInstance();
					for (Entry<String, Object> entry : map.entrySet()) {
						String attrName = entry.getKey().toLowerCase();
						for (PropertyDescriptor prop : props) {
							if (!attrName.equals(prop.getName().toLowerCase())) {
								continue;
							}
							Method method = prop.getWriteMethod();
							if(prop.getPropertyType().equals(Long.class)){
								method.invoke(t,((BigInteger)entry.getValue()).longValue());
							}else if(attrName.equals("ps")){
								method.invoke(t,((BigInteger)entry.getValue()).intValue());
							}else{
								method.invoke(t,entry.getValue());
							}
							
						}
					}
					result.add(t);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return result;
	}
	
	@Override
	public Long findHitsTopListCount(final String siteId,final String columnId,final boolean isOrgan,
			final String startTime,final String endTime){
			String sql="select t.* from t_hits t inner join t_hits_history h on t.HID=h.HID ";
			if(StringUtils.isNotEmpty(siteId)){
				sql+="  and t.SITE_ID in ("+siteId+")";
			}
			if(StringUtils.isNotEmpty(columnId)){
				sql+="  and t.COLUMN_ID in ("+columnId+")";
			}
			if(StringUtils.isNotEmpty(startTime)){
				sql+=" and h.CREATE_TIME>'"+startTime+"'";
			}
			if(StringUtils.isNotEmpty(endTime)){
				sql+=" and h.CREATE_TIME<'"+endTime+"'";
			}
			if(isOrgan){
				sql+=" and t.MANU_ID IS NOT NULL ";
				sql+=" group by t.ORGAN_ID";//按单位用户分组
			}else{
				sql+=" group by t.RECORD_ID";  //按文章分组
			}
			Query query=getSession().createSQLQuery(sql).addEntity(HitsInfo.class);
			return (long) query.list().size();
	}
	
	@Override
	public Long findHitsTopCountByPid(final Long pid,final String startTime,final String endTime){
			String sql="select count(*) from HitsHistory h where h.hid=? ";
			if(StringUtils.isNotEmpty(startTime)){
				sql+=" and h.createTime>'"+startTime+"'";
			}
			if(StringUtils.isNotEmpty(endTime)){
				sql+=" and h.createTime<'"+endTime+"'";
			}
			Query query=getSession().createQuery(sql);
			query.setLong(0, pid);
			return  (Long) query.uniqueResult();
	}

	@Override
	public Long findHitsCountByOrgan(final Long userId) {
			String sql="select sum(count) from t_c_hits p where " +
					"exists (select * from t_manuscript t where " +
					"t.CREATE_CMS_USER_ID="+userId+" and " +
					"t.MANU_STATUS='"+ManuStatus.auditPass+"' and p.MANU_ID=t.MANU_ID)";
			Query query=getSession().createSQLQuery(sql);
			return ((BigDecimal)query.uniqueResult()).longValue();
	}

	@Override
	public Long findHitsByRecordId(final Long recordId) {
			String sql="select sum(count) from t_hits p where record_id="+recordId;
			Query query=getSession().createSQLQuery(sql);
			return ((BigDecimal)query.uniqueResult()).longValue();
	}

}
