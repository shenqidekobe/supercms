package com.dw.suppercms.domain.data;

import java.util.List;
import java.util.function.Function;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.annotation.StringEnumeration;
import com.dw.framework.core.domain.IdentifiedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

/**
 * ModelField
 *
 * @author osmos
 * @date 2015年7月30日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class ModelField extends
		IdentifiedEntity {
	private static final long serialVersionUID = 8049138754779960383L;

	// --------------------------------------------enums

	/**
	 * field data type
	 * DataType
	 *
	 * @author osmos
	 * @date 2015年7月30日
	 */
	public enum DataType {
		STRING, NUMBER, BOOLEAN, DATE, CLOB
	}

	/**
	 * field form type
	 * FormType
	 *
	 * @author osmos
	 * @date 2015年7月30日
	 */
	public enum FormType {
		TEXT, TEXTAREA, RICHTEXT, DATE, FILE, SINGLE, MULTI
	}

	// --------------------------------------------Simple Fiels

	// title
	@NotNull
	@Size(max = 16)
	private String title;

	// field code
	@NotNull
	@Size(max = 16)
	private String fieldCode;

	// data type
	@NotNull
	@StringEnumeration(enumClass = DataType.class)
	private String dataType;

	// data length
	private Integer dataLength;

	// form type
	@NotNull
	@StringEnumeration(enumClass = FormType.class)
	private String formType;

	// display order in form
	private Integer ordinal;

	// if require typing value
	@NotNull
	private boolean required;

	// used for form that type is SINGLE or MULTI
	private String options;

	// the form html for create and modify
	private String formCode;

	// --------------------------------------------Associate Fiels

	// associate model id
	@NotNull
	private Long modelId;

	// associate model object
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "modelId", insertable = false, updatable = false)
	private Model model;

	// --------------------------------------------Own Implements

	/**
	 * build a new model field
	 * 
	 * @param field holds the new building model state
	 * @return the built model
	 */
	public static ModelField newOf(ModelField field) {
		ModelField newField = ModelField.newOf();
		int length = 0;
		if (field.getDataType().equals(DataType.STRING.name())) {
			length = (field.getDataLength() == null ? 32 : field.getDataLength());
		} else if (field.getDataType().equals(DataType.NUMBER.name())) {
			length = (field.getDataLength() == null ? 11 : field.getDataLength());
		} else if (field.getDataType().equals(DataType.BOOLEAN.name())) {
			length = 1;
		}
		newField.setDataLength(length);
		newField.setDataType(field.getDataType());
		newField.setFieldCode(field.getFieldCode());
		newField.setFormType(field.getFormType());
		newField.setModelId(field.getModelId());
		newField.setOrdinal(field.getOrdinal());
		newField.setTitle(field.getTitle());
		newField.setRequired(field.getRequired());
		newField.setOptions(field.getOptions());
		return newField;
	}

	/**
	 * build default fields
	 * 
	 * @return the saved fields
	 */
	public static List<ModelField> newDefaults(Long modelId) {
		List<ModelField> fields = Lists.newArrayList(
				ModelField.newDefault("标题", "title", 1, DataType.STRING.name(), 50, FormType.TEXT.name(), true, null,
						modelId),
				ModelField.newDefault("短标题", "shortTitle", 2, DataType.STRING.name(), 50, FormType.TEXT.name(), false,
						null,
						modelId),
				ModelField.newDefault("地址引用", "urlRef", 3, DataType.STRING.name(), 50, FormType.TEXT.name(), false,
						null,
						modelId),
				ModelField.newDefault("头图", "headerPic", 4, DataType.STRING.name(), 50, FormType.FILE.name(), false,
						null,
						modelId),
				ModelField.newDefault("头图引用", "headerPicRef", 5, DataType.STRING.name(), 50, FormType.TEXT.name(),
						false, null,
						modelId),
				ModelField.newDefault("来源", "source", 6, DataType.STRING.name(), 50, FormType.TEXT.name(), false, null,
						modelId),
				ModelField.newDefault("作者", "author", 7, DataType.STRING.name(), 50, FormType.TEXT.name(), false, null,
						modelId),
				ModelField.newDefault("简介", "introduce", 8, DataType.STRING.name(), 50, FormType.TEXT.name(), false,
						null,
						modelId),
				ModelField.newDefault("正文内容", "content", 9, DataType.CLOB.name(), 50, FormType.RICHTEXT.name(), true,
						null,
						modelId));
		return fields;
	}

	private static ModelField newDefault(String title, String code, int ordinal, String dataType, int dataLength,
			String formType, boolean required, String options, Long modelId) {
		ModelField field = new ModelField();
		field.setTitle(title);
		field.setFieldCode(code);
		field.setOrdinal(ordinal);
		field.setDataType(dataType);
		field.setDataLength(dataLength);
		field.setFormType(formType);
		field.setRequired(required);
		field.setOptions(options);
		field.setModelId(modelId);
		return field;
	}

	/**
	 * alter model field state
	 * 
	 * @param field holds the altering model field state
	 * @return the altered model field
	 */
	public ModelField alterOf(ModelField field) {
		int length = 0;
		if (field.getDataType().equals(DataType.STRING.name())) {
			length = (field.getDataLength() == null ? 32 : field.getDataLength());
		} else if (field.getDataType().equals(DataType.NUMBER.name())) {
			length = (field.getDataLength() == null ? 11 : field.getDataLength());
		} else if (field.getDataType().equals(DataType.BOOLEAN.name())) {
			length = 1;
		}
		setDataLength(length);
		setDataType(field.getDataType());
		setFieldCode(field.getFieldCode());
		setFormType(field.getFormType());
		setOrdinal(field.getOrdinal());
		setTitle(field.getTitle());
		setRequired(field.getRequired());
		setOptions(field.getOptions());
		return this;
	}

	/**
	 * @return the form html of the field
	 */
	public String getDefaultFormCode() {
		String fieldFormCode = "";
		Function<ModelField, String> fnTitle = new Function<ModelField, String>() {
			public String apply(ModelField f) {
				return f.required ? String.format("<font color='red'>*</font>%s", f.getTitle()) : f.getTitle();
			}
		};
		switch (formType) {
		case "TEXT":
			fieldFormCode = String
					.format("<section>\n\t<label class='label'>%s</label> <label class='input'><input name='%s' ng-model='data.%s'/></label>\n</section>",
							fnTitle.apply(this),getFieldCode(), getFieldCode());
			break;
		case "TEXTAREA":
			fieldFormCode = String
					.format("<section>\n\t<label class='label'>%s</label> <label class='textarea'><textarea name='%s' ng-model='data.%s'></textarea></label>\n</section>",
							fnTitle.apply(this),getFieldCode(), getFieldCode());
			break;
		case "RICHTEXT":
			fieldFormCode = String
					.format("<section>\n\t<label class='label'>%s</label> <label class='textarea'><textarea id='ck%s' name='%s' ng-model='data.%s'  data-smart-ck-editor></textarea></label>\n</section>",
							fnTitle.apply(this),getFieldCode(),getFieldCode(), getFieldCode());
			break;
		case "DATE":
			fieldFormCode = String
					.format("<section>\n\t<label class='label'>%s</label> <label class='input'><input name='%s' ng-model='data.%s'/></label>\n</section>",
							fnTitle.apply(this),getFieldCode(), getFieldCode());
			break;
		case "FILE":
			fieldFormCode = String
					.format("<section>\n\t<label class='label'>%s</label><div class='input input-file'><span class='button'><input id='headerPic' upload-header-pic privew-container='privew' type='file' name='%s' ng-model='data.%s'>浏览</span><input type='text' readonly=''></div>\n</section>",
							fnTitle.apply(this),getFieldCode(), getFieldCode());
			break;
		case "SINGLE":
			fieldFormCode = String
					.format("<section>\n\t<label class='label'>%s</label> <label class='input'><input name='%s' ng-model='data.%s'/></label>\n</section>",
							fnTitle.apply(this),getFieldCode(), getFieldCode());
			break;
		case "MULTI":
			fieldFormCode = String
					.format("<section>\n\t<label class='label'>%s</label> <label class='input'><input name='%s' ng-model='data.%s'/></label>\n</section>",
							fnTitle.apply(this),getFieldCode(), getFieldCode());
			break;
		default:
			break;
		}
		return fieldFormCode;
	}
}
