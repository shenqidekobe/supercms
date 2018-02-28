package com.dw.suppercms.domain.data;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.dw.framework.core.domain.IdentifiedEntity;

/**
 * Model
 *
 * @author osmos
 * @date 2015年7月30日
 */
@Entity
@Data(staticConstructor = "newOf")
@EqualsAndHashCode(callSuper = true, of={})
@ToString(callSuper = true, of={})
public class Model
		extends IdentifiedEntity {
	private static final long serialVersionUID = -3123714050688435788L;

	// --------------------------------------------simple Fiels

	@NotNull
	@Size(max = 16)
	private String title;

	@NotNull
	@Size(max = 16)
	private String tableCode;

	@NotNull
	@Size(max = 64)
	private String description;

	@Lob
	private String formCode;

	// --------------------------------------------associate Fiels
	@OneToMany(mappedBy = "model")
	private Set<ModelField> fields = new HashSet<ModelField>();

	/**
	 * build a new model
	 * 
	 * @param model holds the new building model state
	 * @return the built model
	 */
	public static Model newOf(Model model) {
		Model newModel = Model.newOf();
		newModel.setTitle(model.getTitle());
		newModel.setTableCode(model.getTableCode());
		newModel.setDescription(model.getDescription());
		return newModel;
	}

	/**
	 * alter model state
	 * 
	 * @param model holds the altering model state
	 * @return the altered model
	 */
	public Model alterOf(Model model) {
		setTitle(model.getTitle());
		setTitle(model.getTitle());
		setDescription(model.getDescription());
		setTableCode(model.getTableCode());
		setFormCode(model.getFormCode());
		return this;
	}

	public String getDefaultFormCode() {
		String headerPicSection = "<section>\n\t<div id='privew'><span><img ng-show='data.headerPic!=null' style='max-height:70px;border:0px solid #000;margin: 0 0 0 0' src='/resource{{data.headerPic}}' /></span></div>\n</section>";
		return headerPicSection + getFields().stream().sorted(Comparator.comparing(ModelField::getOrdinal))
				.map(ModelField::getDefaultFormCode).reduce("", (a, b) -> a + "\n" + b);
	}
}
