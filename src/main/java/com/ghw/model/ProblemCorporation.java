package com.ghw.model;

public enum ProblemCorporation {

	A("Bank Information not found"), B("Principal owner not found"), AB(
			"Principal owner and Bank Information not found");

	ProblemCorporation(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	private String valor;
}
