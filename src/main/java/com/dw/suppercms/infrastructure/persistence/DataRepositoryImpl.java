package com.dw.suppercms.infrastructure.persistence;

import static java.util.stream.Collectors.toMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.dw.framework.core.domain.GenericRepositoryImpl;
import com.dw.suppercms.domain.data.Data;
import com.dw.suppercms.domain.data.Data.CheckStatus;
import com.dw.suppercms.domain.data.DataRepository;
import com.dw.suppercms.domain.data.Datasource.PassStatus;
import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.data.ModelField;
import com.dw.suppercms.domain.data.ModelField.DataType;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;
import com.dw.suppercms.infrastructure.utils.Pager;
import com.dw.suppercms.produce.rule.VariableDefine;

/**
 * 
 * DataRepositoryImpl
 *
 * @author osmos
 * @date 2015年8月10日
 */
@Log4j
@Repository
@SuppressWarnings("unchecked")
public class DataRepositoryImpl
		extends GenericRepositoryImpl<Data, Long>
		implements DataRepository {

	@Override
	public Long selectLastInsertId() {
		return Long.parseLong(getSession().createSQLQuery("select LAST_INSERT_ID()").uniqueResult().toString());
	}

	@Override
	public int countModelTable(String tableCode) {
		return Integer.valueOf(getSession()
				.createSQLQuery("select count(*) from " + tableCode)
				.uniqueResult().toString());
	}

	@Override
	public Map<String, Object> findDynamicData(String tableCode, Long id) {
		String sql = "select * from " + tableCode + " m where m.id=" + id;
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		sqlQuery.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) sqlQuery.uniqueResult();
	}

	@Override
	public void saveDynamicData(Model model, Map<String, Object> valuesMap) {

		Map<String, String> codeMapToType = codeMapToType(model);

		BiFunction<String, Object, String> fn = new BiFunction<String, Object, String>() {
			public String apply(String k, Object v) {
				String rs = null;
				DataType type = DataType.valueOf(codeMapToType.get(k));
				switch (type) {
				case BOOLEAN:
					rs = Objects.equals(v, true) ? "1" : "0";
					break;
				case NUMBER:
					rs = Objects.toString(v);
					break;
				case DATE:
					if (v == null) {
						rs = "null";
					} else {
						rs = "'" + DateFormatUtils.format((Date) v, "yyyy-MM-dd HH:mm:ss") + "'";
					}
					break;
				default:
					if (v == null) {
						rs = "null";
					} else {
						rs = "'" + v.toString() + "'";
					}
					break;
				}
				return rs;
			}
		};
		Map<String, String> codeMapToValue = valuesMap.keySet().stream()
				.collect(toMap(Function.identity(), k -> fn.apply(k, valuesMap.get(k))));

		String sql = String.format("insert into %s(%s) values(%s)",
				model.getTableCode(),
				StringUtils.join(codeMapToValue.keySet(), ","),
				StringUtils.join(codeMapToValue.values(), ","));

		getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void updateDynamicData(Model model, Map<String, Object> oldValuesMap, Map<String, Object> newValuesMap) {

		Map<String, String> codeMapToType = codeMapToType(model);

		BiFunction<String, Object, String> fn = new BiFunction<String, Object, String>() {
			public String apply(String k, Object v) {
				if (StringUtils.equals(k, "columnDatas")) {
					return "";
				}
				String rs = k + "=";
				DataType type = DataType.valueOf(codeMapToType.get(k));
				switch (type) {
				case BOOLEAN:
					rs += Objects.equals(v, true) ? "1" : "0";
					break;
				case NUMBER:
					rs += Objects.toString(v);
					break;
				case DATE:
					if (v == null) {
						rs += "null";
					} else {
						rs += "'" + DateFormatUtils.format((Date) v, "yyyy-MM-dd HH:mm:ss") + "'";
					}
					break;
				default:
					rs += "'" + Objects.toString(v) + "'";
					break;
				}
				return rs;
			}
		};

		Map<String, String> codeMapToValue = model
				.getFields()
				.stream()
				.filter(f -> !Objects.equals(newValuesMap.get(f.getFieldCode()), oldValuesMap.get(f.getFieldCode())))
				.collect(
						toMap(ModelField::getFieldCode,
								f -> fn.apply(f.getFieldCode(), newValuesMap.get(f.getFieldCode()))));

		codeMapToValue.put("update_time", "update_time='" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss")
				+ "'");

		String sql = String.format("update %s set %s where id=%s",
				model.getTableCode(),
				StringUtils.join(codeMapToValue.values(), ","),
				newValuesMap.get("id"));

		getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void deleteDynamicData(String tableCode, Long id) {
		String sql = "delete from " + tableCode + " where id=" + id;
		getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void stick(String tableCode, Long id) {
		String sql = "update " + tableCode + " set stick=1 where id=" + id;
		getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void unstick(String tableCode, Long id) {
		String sql = "update " + tableCode + " set stick=0 where id=" + id;
		getSession().createSQLQuery(sql).executeUpdate();
	}

	@Override
	public void copy(Model sourceModel, Model targetModel, Map<String, Object> valuesMap, Long datasourceId) {}

	@Override
	public void submit(String tableCode, String datasourcePassStatus, Long id, String user) {
		String sql = "update " + tableCode + " set check_status=:check_status where id=:id";
		getSession().createSQLQuery(sql)
				.setParameter("check_status", CheckStatus.FIRST.name())
				.setParameter("id", id).executeUpdate();
	}

	@Override
	public void pass1(String tableCode, String datasourcePassStatus, Long id, String user) {
		String sql = "update "
				+ tableCode
				+ " set check_status=:check_status, passed_first_user=:passed_first_user, passed_first_time=:passed_first_time";
		sql += " where id=:id";
		String checkStatus = datasourcePassStatus.equals(PassStatus.ONE.name()) ? CheckStatus.PASS.name()
				: CheckStatus.SECOND.name();
		getSession().createSQLQuery(sql)
				.setParameter("check_status", checkStatus)
				.setParameter("passed_first_user", user)
				.setParameter("passed_first_time", new Date())
				.setParameter("id", id).executeUpdate();
	}

	@Override
	public void refuse1(String tableCode, String datasourcePassStatus, Long id, String user) {
		String sql = "update "
				+ tableCode
				+ " set check_status=:check_status, refused_first_user=:refused_first_user, refused_first_time=:refused_first_time where id=:id";
		String checkStatus = CheckStatus.FIRST_REFUSE.name();
		getSession().createSQLQuery(sql)
				.setParameter("check_status", checkStatus)
				.setParameter("refused_first_user", user)
				.setParameter("refused_first_time", new Date())
				.setParameter("id", id).executeUpdate();

	}

	@Override
	public void pass2(String tableCode, Long id, String user) {
		String sql = "update "
				+ tableCode
				+ " set check_status=:check_status, passed_second_user=:passed_second_user, passed_second_time=:passed_second_time where id=:id";
		String checkStatus = CheckStatus.PASS.name();
		getSession().createSQLQuery(sql)
				.setParameter("check_status", checkStatus)
				.setParameter("passed_second_user", user)
				.setParameter("passed_second_time", new Date())
				.setParameter("id", id).executeUpdate();

	}

	@Override
	public void refuse2(String tableCode, String datasourcePassStatus, Long id, String user) {
		String sql = "update "
				+ tableCode
				+ " set check_status=:check_status, refused_second_user=:refused_second_user, refused_second_time=:refused_second_time where id=:id";
		String checkStatus = CheckStatus.SECOND_REFUSE.name();
		getSession().createSQLQuery(sql)
				.setParameter("check_status", checkStatus)
				.setParameter("refused_second_user", user)
				.setParameter("refused_second_time", new Date())
				.setParameter("id", id).executeUpdate();

	}

	@Override
	public int countOfCheckStatus(String tableCode, List<String> checkStatus) {
		String sql = "select count(*) from " + tableCode + " where check_status in(:checkStatus)";
		return Integer.parseInt(getSession().createSQLQuery(sql).setParameterList("checkStatus", checkStatus)
				.uniqueResult().toString());
	}

	@Override
	public List<Map<String, Object>> paginateForManage(String tableCode, Long datasourceId,
			Map<String, Object> condition) {

		StringBuffer sb = new StringBuffer();

		sb.append("select * from ");
		sb.append(tableCode);
		sb.append(" t where t.datasource_id = :datasourceId");

		condition = condition == null ? new HashMap<>() : condition;

		if (condition.get("checkStatus") != null) {
			String checkStatus = condition.get("checkStatus").toString();
			if (checkStatus.equals("PASS")) {
				sb.append(" and t.check_status = 'PASS'");
			} else if (checkStatus.equals("ORIGIN")) {
				sb.append(" and (t.check_status = 'ORIGIN' or t.check_status='FIRST_REFUSE')");
			} else if (checkStatus.equals("FIRST")) {
				sb.append(" and (t.check_status = 'FIRST' or t.check_status='SECOND_REFUSE')");
			} else if (checkStatus.equals("SECOND")) {
				sb.append(" and t.check_status = 'SECOND'");
			}
		}
		if (condition.get("title") != null) {
			String titleVal = condition.get("title").toString();
			sb.append(" and t.title like '%" + titleVal + "%'");
		}
		if (condition.get("startTime") != null) {
			Date startTime = (Date) condition.get("startTime");
			sb.append(" and t.create_time >= '" + DateFormatUtils.format(startTime, "yyyy-MM-dd") + "'");
		}
		if (condition.get("endTime") != null) {
			Date endTime = (Date) condition.get("endTime");
			sb.append(" and t.create_time <= '" + DateFormatUtils.format(DateUtils.addDays(endTime, 1), "yyyy-MM-dd")
					+ "'");
		}

		sb.append(" order by t.stick desc, t.create_time desc");

		SQLQuery sqlQuery = getSession().createSQLQuery(sb.toString());
		sqlQuery.setLong("datasourceId", datasourceId);
		sqlQuery.setFirstResult((Integer) condition.get("firstResult"));
		sqlQuery.setMaxResults((Integer) condition.get("maxResults"));
		sqlQuery.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

		return sqlQuery.list();
	}

	@Override
	public int countForManage(String tableCode, Long datasourceId, Map<String, Object> condition) {
		StringBuffer sb = new StringBuffer();

		sb.append("select count(*) from ");
		sb.append(tableCode);
		sb.append(" t where t.datasource_id = :datasourceId");

		condition = condition == null ? new HashMap<>() : condition;

		if (condition.get("checkStatus") != null) {
			String checkStatus = condition.get("checkStatus").toString();
			if (checkStatus.equals("PASS")) {
				sb.append(" and t.check_status = 'PASS'");
			} else if (checkStatus.equals("ORIGIN")) {
				sb.append(" and (t.check_status = 'ORIGIN' or t.check_status='FIRST_REFUSE')");
			} else if (checkStatus.equals("FIRST")) {
				sb.append(" and (t.check_status = 'FIRST' or t.check_status='SECOND_REFUSE')");
			} else if (checkStatus.equals("SECOND")) {
				sb.append(" and t.check_status = 'SECOND'");
			}
		}
		if (condition.get("title") != null) {
			String titleVal = condition.get("title").toString();
			sb.append(" and t.title like '%" + titleVal + "%'");
		}
		if (condition.get("startTime") != null) {
			Date startTime = (Date) condition.get("startTime");
			sb.append(" and t.create_time >= '" + DateFormatUtils.format(startTime, "yyyy-MM-dd") + "'");
		}
		if (condition.get("endTime") != null) {
			Date endTime = (Date) condition.get("endTime");
			sb.append(" and t.create_time <= '" + DateFormatUtils.format(DateUtils.addDays(endTime, 1), "yyyy-MM-dd")
					+ "'");
		}

		SQLQuery sqlQuery = getSession().createSQLQuery(sb.toString());
		sqlQuery.setLong("datasourceId", datasourceId);
		return Integer.valueOf(sqlQuery.uniqueResult().toString());
	}

	@Override
	public List<Map<String, Object>> getListData(String tableCode, Map<String, Object> condition) {
		StringBuffer sb = new StringBuffer();

		sb.append("select * from ");
		sb.append(tableCode);
		
		sb.append(" t where 1=1");

		for (String key : condition.keySet()) {
			Object val = condition.get(key);
			if (val.getClass().toString().equals("Long") || val.getClass().toString().equals("Integer")) {
				sb.append(" and t." + key + " =" + val);
			} else {
				sb.append(" and t." + key + " ='" + val + "'");
			}
		}

		sb.append(" order by t.stick desc, t.create_time desc");

		log.debug("sb-sql = "+sb.toString());
		SQLQuery sqlQuery = getSession().createSQLQuery(sb.toString());
		sqlQuery.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

		List<Map<String, Object>> list = sqlQuery.list();
		return list;
	}

	@Override
	public List<Map<String, Object>> getListDataToRecordDirective(
			Model model, Map<String, Object> params, List<Long> columnIds,
			Date startTime, Date endTime, Pager pager) {
		StringBuffer sb = new StringBuffer();
		// 从map得到是否获取置顶的数据
		String stickKey = VariableDefine.map_params_query_stick_key;
		String isStick = params.get(stickKey) != null ? params.get(stickKey).toString() : "";
		// 得到排序字段和排序方式。默认创建时间倒序
		String sort = StringUtils.isEmpty(pager.getSort()) ? "create_time"
				: pager.getSort().equals("createTime") ? "create_time" : pager.getSort();
		String dir = StringUtils.isEmpty(pager.getDir()) ? "desc" : pager.getDir();
		// 从Model中得到模型的所有的字段以及模型的表名
		Map<String, String> codeMapToType = codeMapToType(model);
		String tableCode = model.getTableCode();
		// 通过模型字段的map转换为需要查询的表名.字段名的字符串
		String queryCodeStr = "";
		for (String key : codeMapToType.keySet()) {
			queryCodeStr += "t1." + key + ",";
		}
		queryCodeStr = StringUtils.chop(queryCodeStr);

		sb.append("select distinct " + queryCodeStr + " from ");
		sb.append(tableCode);

		sb = recordDirectiveQuerySqlJoint(sb, params, columnIds, startTime, endTime);

		if (VariableDefine.map_params_true.equals(isStick)) {
			sb.append(" order by t1.stick desc, t1." + sort + " " + dir);
		} else {
			sb.append(" order by t1." + sort + " " + dir);
		}

		log.warn("record findDataRecordListUseDirective list  sql = " + sb.toString());
		SQLQuery sqlQuery = getSession().createSQLQuery(sb.toString());
		sqlQuery.setFirstResult(pager.getStartIndex());
		sqlQuery.setMaxResults(pager.getPageSize());
		sqlQuery.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);

		return sqlQuery.list();

	}

	@Override
	public int getListDataCountToRecordDirective(String tableCode,
			Map<String, Object> params, List<Long> columnIds, Date startTime,
			Date endTime) {

		StringBuffer sb = new StringBuffer();

		sb.append("select count(distinct t1.id) from ");
		sb.append(tableCode);

		sb = recordDirectiveQuerySqlJoint(sb, params, columnIds, startTime, endTime);

		// log.debug("record findDataRecordListUseDirective count  sql = "+sb.toString());
		SQLQuery sqlQuery = getSession().createSQLQuery(sb.toString());
		return Integer.valueOf(sqlQuery.uniqueResult().toString());
	}

	private StringBuffer recordDirectiveQuerySqlJoint(StringBuffer sb, Map<String, Object> params,
			List<Long> columnIds, Date startTime, Date endTime) {
		// 得到所有的栏目ID
		String cids = StringUtils.join(columnIds, ",");
		// 从map得到是获取最终页数据还是列表页数据
		String produceKey = VariableDefine.map_params_query_produce_key;
		String produce = params.get(produceKey) != null ? params.get(produceKey).toString() : "";
		// 从map得到需要排除的数据ID
		String excludeKey = VariableDefine.map_params_query_exclude_key;
		String excludeId = params.get(excludeKey) != null ? params.get(excludeKey).toString() : null;
		// 从map得到是否获取置顶的数据
		String stickKey = VariableDefine.map_params_query_stick_key;
		// 从map得到标签信息，然后按标签进行查询
		String tagKey = VariableDefine.map_params_query_tag_key;
		String tags = params.get(tagKey) != null ? params.get(tagKey).toString() : null;
		// 从map中取出响应数据后，去除取出的key，方便于后续做条件查询
		sb.append(" t1 left join column_data t2 on t1.id=t2.data_id");
		sb.append(" left join channel ct on t2.datasource_id=ct.datasource_id");
		if (tags != null) {
			sb.append(" left join tag_data t3 on t1.id=t3.data_id ");
			sb.append(" where exists(select * from tag t4 where t4.title in(" + CommonsUtil.characterAddQuotes(tags)
					+ ") and t4.id=t3.tag_id)");
		} else {
			sb.append(" where 1=1");
		}
		sb.append(" and ct.id in(" + cids + ")");

		if (StringUtils.isNotEmpty(excludeId)) {
			sb.append(" and t1.id not in(" + excludeId + ")");
		}

		if (produce.equals(VariableDefine.map_params_query_produced)) {
			sb.append(" and t2.produce_state=true"); // 获取已发布的数据
		} else if (produce.equals(VariableDefine.map_params_query_producenot)) {
			sb.append(" and t2.produce_state=false"); // 获取未发布的数据
		}

		if (startTime != null) {
			sb.append(" and t1.create_time >= '" + DateFormatUtils.format(startTime, "yyyy-MM-dd") + "'");
		}
		if (endTime != null) {
			sb.append(" and t1.create_time <= '" + DateFormatUtils.format(DateUtils.addDays(endTime, 1), "yyyy-MM-dd")
					+ "'");
		}

		sb.append(" and t1.check_status = 'PASS'");// 获取状态为审核通过的

		for (String key : params.keySet()) {
			if (key.equals(produceKey) || key.equals(stickKey) || key.equals(tagKey) || key.equals(excludeKey))
				continue;
			Object val = params.get(key);
			if (val.getClass().toString().equals("Long") || val.getClass().toString().equals("Integer")) {
				sb.append(" and t1." + key + " = " + val);
			} else {
				sb.append(" and t1." + key + " =' " + val + "'");
			}
		}
		return sb;
	}

	private Map<String, String> codeMapToType(Model model) {
		Map<String, String> codeMapToType = model.getFields().stream().collect(
				toMap(ModelField::getFieldCode, f -> f.getDataType()));
		codeMapToType.put("id", DataType.NUMBER.name());
		codeMapToType.put("editor", DataType.STRING.name());
		codeMapToType.put("check_status", DataType.STRING.name());
		codeMapToType.put("passed_first_user", DataType.STRING.name());
		codeMapToType.put("passed_first_time", DataType.DATE.name());
		codeMapToType.put("passed_second_user", DataType.STRING.name());
		codeMapToType.put("passed_second_time", DataType.DATE.name());
		codeMapToType.put("refused_first_user", DataType.STRING.name());
		codeMapToType.put("refused_first_time", DataType.DATE.name());
		codeMapToType.put("refused_second_user", DataType.STRING.name());
		codeMapToType.put("refused_second_time", DataType.DATE.name());
		codeMapToType.put("editor", DataType.STRING.name());
		codeMapToType.put("stick", DataType.BOOLEAN.name());
		codeMapToType.put("datasource_id", DataType.NUMBER.name());
		codeMapToType.put("create_user_id", DataType.NUMBER.name());
		codeMapToType.put("manu_id", DataType.NUMBER.name());
		codeMapToType.put("model_id", DataType.NUMBER.name());
		codeMapToType.put("publish_time", DataType.DATE.name());
		codeMapToType.put("create_time", DataType.DATE.name());
		codeMapToType.put("update_time", DataType.DATE.name());
		return codeMapToType;
	}

}
