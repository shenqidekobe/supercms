package com.dw.suppercms.application.data.impl;

import java.util.Map;

import lombok.extern.log4j.Log4j2;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.dw.suppercms.domain.data.Model;
import com.dw.suppercms.domain.data.ModelField;
import com.dw.suppercms.domain.data.ModelField.DataType;

/**
 * 
 * SchemalEventListener
 *
 * @author osmos
 * @date 2015年7月31日
 */
@Service
@Log4j2
@SuppressWarnings("unchecked")
public class SchemalEventListener implements ApplicationListener<SchemalEvent> {

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	private final static String CREATE_TABLE_SQL = "CREATE TABLE `%s` ("
			+ "`id` bigint(20) NOT NULL AUTO_INCREMENT,"
			+ "`title` varchar(50),"
			+ "`shortTitle` varchar(25),"
			+ "`urlRef` varchar(100),"
			+ "`headerPic` varchar(100),"
			+ "`headerPicRef` varchar(100),"
			+ "`source` varchar(25),"
			+ "`author` varchar(25),"
			+ "`introduce` varchar(100),"
			+ "`content` longtext,"

			+ "`check_status` varchar(20),"
			+ "`passed_first_user` varchar(10),"
			+ "`passed_first_time` datetime,"
			+ "`passed_second_user` varchar(10),"
			+ "`passed_second_time` datetime,"
			+ "`refused_first_user` varchar(10),"
			+ "`refused_first_time` datetime,"
			+ "`refused_second_user` varchar(10),"
			+ "`refused_second_time` datetime,"

            + "`manu_id`  bigint(20),"
			+ "`editor` varchar(25),"
			+ "`stick` bit(1),"
			+ "`datasource_id` bigint(20),"
			+ "`model_id` bigint(20),"
			+ "`create_user_id`  bigint(20),"
			+ "`publish_time` datetime,"
			+ "`create_time` datetime,"
			+ "`update_time` datetime,"
			+ "PRIMARY KEY (`id`))";
	private final static String RENAME_TABLE_SQL = "ALTER TABLE `%s` rename `%s`";
	private final static String DELETE_TABLE_SQL = "DROP TABLE `%s`";
	private final static String ADD_COLUMN_SQL = "ALTER TABLE `%s` ADD COLUMN `%s` %s";
	private final static String ALTER_COLUMN_SQL = "ALTER TABLE `%s` CHANGE COLUMN `%s` `%s` %s";
	private final static String DELETE_COLUMN_SQL = "ALTER TABLE `%s` DROP COLUMN `%s`";

	@Override
	public void onApplicationEvent(SchemalEvent event) {

		log.info("processing schemal event: " + event.getEventType());
		Map<String, Object> data = (Map<String, Object>) event.getData();
		switch (event.getEventType()) {
		case CREATE_TABLE:
			createTable(data);
			break;
		case RENAME_TABLE:
			renameTable(data);
			break;
		case DELETE_TABLE:
			deleteTable(data);
			break;
		case CREATE_FIELD:
			createField(data);
			break;
		case ALTER_FIELD:
			alterField(data);
			break;
		case DELETE_FIELD:
			deleteField(data);
			break;
		default:
			break;
		}
	}

	private void createTable(Map<String, Object> data) {
		String newTableCode = ((Model) data.get("model")).getTableCode();
		String creatTableSQL = String.format(CREATE_TABLE_SQL, newTableCode);
		sessionFactory.getCurrentSession().createSQLQuery(creatTableSQL).executeUpdate();
	}

	private void renameTable(Map<String, Object> data) {
		String oldTableCode = ((Model) data.get("oldModel")).getTableCode();
		String newTableCode = ((Model) data.get("newModel")).getTableCode();
		String alterTableSQL = String.format(RENAME_TABLE_SQL, oldTableCode, newTableCode);
		sessionFactory.getCurrentSession().createSQLQuery(alterTableSQL).executeUpdate();
	}

	private void deleteTable(Map<String, Object> data) {
		String tableCode = ((Model) data.get("model")).getTableCode();
		String dropTableSQL = String.format(DELETE_TABLE_SQL, tableCode);
		sessionFactory.getCurrentSession().createSQLQuery(dropTableSQL).executeUpdate();
	}

	private void createField(Map<String, Object> data) {
		Model model = (Model) data.get("model");
		ModelField field = (ModelField) data.get("field");

		String tableCode = model.getTableCode();
		String fieldCode = field.getFieldCode();
		String columnDesc = columnDesc(field.getDataType(), field.getDataLength());

		String addColumnSQL = String.format(ADD_COLUMN_SQL, tableCode, fieldCode, columnDesc);
		sessionFactory.getCurrentSession().createSQLQuery(addColumnSQL).executeUpdate();
	}

	private void alterField(Map<String, Object> data) {
		Model model = (Model) data.get("model");
		ModelField oldField = (ModelField) data.get("oldField");
		ModelField newField = (ModelField) data.get("newField");

		String tableCode = model.getTableCode();
		String oldFieldCode = oldField.getFieldCode();
		String newFieldCode = newField.getFieldCode();
		String columnDesc = columnDesc(newField.getDataType(), newField.getDataLength());

		String alterColumnSQL = String.format(ALTER_COLUMN_SQL, tableCode, oldFieldCode, newFieldCode, columnDesc);
		sessionFactory.getCurrentSession().createSQLQuery(alterColumnSQL).executeUpdate();
	}

	private void deleteField(Map<String, Object> data) {
		ModelField field = (ModelField) data.get("field");
		String tableCode = field.getModel().getTableCode();
		String fieldCode = field.getFieldCode();

		String deleteColumnSQL = String.format(DELETE_COLUMN_SQL, tableCode, fieldCode);
		sessionFactory.getCurrentSession().createSQLQuery(deleteColumnSQL).executeUpdate();
	}

	private String columnDesc(String dataType, Integer dataLength) {
		String columnDesc = null;

		int length = 0;
		if (dataType.equals(DataType.STRING.name())) {
			length = (dataLength == null ? 32 : dataLength);
			columnDesc = "VARCHAR(" + length + ")";
		} else if (dataType.equals(DataType.NUMBER.name())) {
			length = (dataLength == null ? 11 : dataLength);
			if (length < 12) {
				columnDesc = "int(" + length + ")";
			} else {
				columnDesc = "bigint(" + length + ")";
			}
		} else if (dataType.equals(DataType.BOOLEAN.name())) {
			columnDesc = "BIT(1)";
		} else if (dataType.equals(DataType.DATE.name())) {
			columnDesc = "DATETIME";
		} else if (dataType.equals(DataType.CLOB.name())) {
			columnDesc = "LONGTEXT";
		}

		return columnDesc;
	}

}
