package com.ghw.model;

public enum TypeYear {

	C("Calendar"), F("Fiscal");

	TypeYear(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	private String valor;
}
