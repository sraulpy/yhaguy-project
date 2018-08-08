package com.yhaguy.domain;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;

@SuppressWarnings("serial")
public class ContactoInterno extends Domain {

	Funcionario funcionario;
	Tipo tipoContactoInterno;

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public Tipo getTipoContactoInterno() {
		return tipoContactoInterno;
	}

	public void setTipoContactoInterno(Tipo tipoContactoInterno) {
		this.tipoContactoInterno = tipoContactoInterno;
	}

	public ContactoInterno() {
	}

	public String getDescripcionTipoContactoInterno() {
		String descripcion = this.tipoContactoInterno.getDescripcion();
		return descripcion;
	}

	public int compareTo(Object o1) {
		ContactoInterno cmp = (ContactoInterno) o1;
		boolean isOk = true;

		isOk = isOk && (this.id.compareTo(cmp.id) == 0);

		if (isOk == true) {
			return 0;
		} else {

			return -1;

		}
	}
}
