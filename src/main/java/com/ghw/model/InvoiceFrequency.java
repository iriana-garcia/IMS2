package com.ghw.model;

public enum InvoiceFrequency {

	W("Weekly"), B("Biweekly"), T("Twice per month ");

	InvoiceFrequency(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	private String valor;
}
