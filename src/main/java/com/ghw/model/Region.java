package com.ghw.model;

public enum Region {

	D("Domestic"), I("International");

	Region(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	private String valor;
}
