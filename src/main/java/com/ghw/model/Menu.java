package com.ghw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "menu")
public class Menu implements Serializable, IEntity {

	@Id
	@Column(name = "men_id")
	private Integer id;

	@Column(name = "men_descripcion")
	private String descripcion;

	@Column(name = "men_action")
	private String action;

	@Transient
	private Integer father;

	@Column(name = "men_action_listener")
	private String actionListener;

	@Transient
	private boolean mostrar, disabled = false, rendered = true;

	@ManyToOne(targetEntity = Menu.class)
	@JoinColumn(name = "men_father", nullable = false)
	private Menu menuFather;

	@OneToMany(mappedBy = "menuFather")
	private List<Menu> submenus = new ArrayList<Menu>();

	@ManyToMany
	@JoinTable(name = "permissions_menu", joinColumns = @JoinColumn(name = "men_id"), inverseJoinColumns = @JoinColumn(name = "per_id"))
	private List<Permission> permissions = new ArrayList<Permission>();

	public String getActionListener() {
		return actionListener;
	}

	public void setActionListener(String actionListener) {
		this.actionListener = actionListener;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isMenu() {
		return (getAction() != null && getAction().length() > 0);
	}

	public boolean isMenuGroup() {
		return (!isMenu() && (getMenuFather() != null));
	}

	public boolean isDropDownMenu() {
		return (!isMenu() && (getMenuFather() == null));
	}

	public boolean tienePadre() {
		return (getMenuFather() != null);
	}

	public int compareTo(Object o) {

		if (o instanceof Menu) {
			Menu item = (Menu) o;
			return item.getId() - getId();
		}
		return 0;
	}

	public boolean isMostrar() {
		return mostrar;
	}

	public void setMostrar(boolean mostrar) {
		this.mostrar = mostrar;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public Integer getFather() {
		return father;
	}

	public void setFather(Integer father) {
		this.father = father;
	}

	public List<Menu> getSubmenus() {
		return submenus;
	}

	public void setSubmenus(List<Menu> submenus) {
		this.submenus = submenus;
	}

	public Menu getMenuFather() {
		return menuFather;
	}

	public void setMenuFather(Menu menuFather) {
		this.menuFather = menuFather;
	}

	@Override
	public String getIdentity() {
		return id.toString();
	}

}
