package com.ghw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "invoice_state")
public class InvoiceState implements Serializable {

	@Id
	@Column(name = "ivt_id", length = 36, columnDefinition = "CHAR(36)")
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@Column(name = "ivt_name", nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
	private List<Invoice> listInvoice = new ArrayList<Invoice>();

	public static final String PENDING = "1";
	public static final String SUBMITTED = "2";
	public static final String APPROVED = "3";
	public static final String CANCELED = "4";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Invoice> getListInvoice() {
		return listInvoice;
	}

	public void setListInvoice(List<Invoice> listInvoice) {
		this.listInvoice = listInvoice;
	}

	public InvoiceState(String id) {
		super();
		this.id = id;
	}

	public InvoiceState(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public InvoiceState() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InvoiceState))
			return false;
		InvoiceState other = (InvoiceState) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
