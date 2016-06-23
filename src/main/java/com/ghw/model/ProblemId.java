package com.ghw.model;

public enum ProblemId {

	A("Event is not associate to a Program"), B("No Rate"), C(
			"Program not associate to IBO"), D("Work not associate to invoice");

	ProblemId(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	private String valor;
}
