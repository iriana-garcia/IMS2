package com.ghw.model;

import java.util.ArrayList;
import java.util.List;

public class TypeSkill {

	public static final short test = 0;
	public static final short training = 1;
	public static final short production = 2;

	public static String getTypeName(Short type) {

		if (type == null)
			return "";

		switch (type) {
		case 0:
			return "Test";
		case 1:
			return "Training";
		case 2:
			return "Production";
		default:
			return "";
		}

	}

	public static List<Object[]> getList() {

		List<Object[]> listTypeSkill = new ArrayList<Object[]>();
		listTypeSkill.add(new Object[] { 0, "Test" });
		listTypeSkill.add(new Object[] { 1, "Training" });
		listTypeSkill.add(new Object[] { 2, "Production" });

		return listTypeSkill;
	}
}
