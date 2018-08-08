package com.yhaguy.domain;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class InvLoteOK extends Domain{

	private int numeroLote;
	
	public int getNumeroLote() {
		return numeroLote;
	}

	public void setNumeroLote(int numeroLote) {
		this.numeroLote = numeroLote;
	}

	@Override
	public int compareTo(Object o) {
		InvLoteOK cmp = (InvLoteOK) o;
		boolean isOk = true;
		
		isOk = isOk && (this.id.compareTo(cmp.getId()) == 0);
		
		if (isOk) {
			return 0;
		} else {
			return -1;	
		}		
	}

}
