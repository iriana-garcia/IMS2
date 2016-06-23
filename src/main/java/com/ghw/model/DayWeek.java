package com.ghw.model;

public enum DayWeek {

	M("Monday"), T("Tuesday"), W("Wednesday"), U("Thursday"), F("Friday"), S(
			"Saturday"), O("Sunday");

	DayWeek(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	private String valor;
}
