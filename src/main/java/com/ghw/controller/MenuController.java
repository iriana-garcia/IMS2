package com.ghw.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.ghw.model.Menu;
import com.ghw.model.User;
import com.ghw.service.MenuService;
import com.ghw.util.Context;

//@ManagedBean
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MenuController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MenuModel model;

	@Autowired
	private SessionBean sessionBean;

	@Autowired
	private MenuService menuService;

	public MenuModel getModel() {

		try {

			if (model == null) {

				List<Menu> menus;
				User user = sessionBean.getUser();

				// if all ok get menu
				// if is an IBO inactive don't search anything
				if (user.isAnIbo() && !user.isState()) {
					menus = new ArrayList<Menu>();
				} else {
					menus = menuService.getDataByUser(user);
				}

				List<Menu> listMenuItem = new ArrayList<Menu>();

				Map<String, DefaultSubMenu> listSubMenuItem = new HashMap<String, DefaultSubMenu>();

				List<DefaultMenuItem> listWithOutPather = new ArrayList<DefaultMenuItem>();

				model = new DefaultMenuModel();

				for (Menu menu : menus) {
					if (!menu.tienePadre()
							&& StringUtils.isNotBlank(menu.getAction())) {

						DefaultMenuItem item = new DefaultMenuItem(
								Context.getMSGText(menu.getDescripcion()));
						item.setCommand(menu.getAction());

						listWithOutPather.add(item);
					} else if (!menu.tienePadre()) {
						listSubMenuItem.put(
								menu.getId().toString(),
								new DefaultSubMenu(Context.getMSGText(menu
										.getDescripcion())));

					} else if (menu.tienePadre() && menu.isDropDownMenu()) {

						listMenuItem.add(menu);
						listSubMenuItem.put(
								menu.getId().toString(),
								new DefaultSubMenu(Context.getMSGText(menu
										.getDescripcion())));
					} else {
						listMenuItem.add(menu);
					}
				}

				// associate all the menuitem with their menu father
				for (Menu m : listMenuItem) {

					if (m.isDropDownMenu() && m.getSubmenus() != null
							&& m.getSubmenus().size() > 0) {

						listSubMenuItem.get(m.getMenuFather().getId())
								.addElement(
										new DefaultSubMenu(Context.getMSGText(m
												.getDescripcion())));
					} else {

						DefaultMenuItem item = new DefaultMenuItem(
								Context.getMSGText(m.getDescripcion()));
						item.setCommand(m.getAction());

						DefaultSubMenu ds = listSubMenuItem.get(m
								.getMenuFather().getId().toString());

						ds.addElement(item);
					}
				}

				DefaultMenuItem homeMenu = new DefaultMenuItem(
						Context.getMSGText("home"));
				homeMenu.setCommand("default");
				homeMenu.setIcon("ui-icon-home");

				DefaultSeparator separator = new DefaultSeparator();

				// add home menu
				model.addElement(homeMenu);
				model.addElement(separator);

				// add to menu bar only the menu without parent
				for (Map.Entry<String, DefaultSubMenu> entry : listSubMenuItem
						.entrySet()) {
					DefaultSubMenu s = entry.getValue();
					if (s.getParent() == null && s.getElementsCount() > 0) {
						model.addElement(s);
						model.addElement(separator);
					}
				}

				for (DefaultMenuItem d : listWithOutPather) {
					model.addElement(d);
					model.addElement(separator);
				}
			}

		} catch (Exception e) {
			Context.addErrorMessageFromMSG(e.getMessage());
		}
		return model;
	}

	public void setModel(MenuModel model) {
		this.model = model;
	}

}
