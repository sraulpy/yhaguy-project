package com.yhaguy.gestion.empresa;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;

public class ContactoInternoDTO extends DTO {

	private MyPair funcionario = new MyPair();
	private MyPair tipoContactoInterno = new MyPair();
	
	
	public MyPair getFuncionario() {
		return funcionario;
	}
	public void setFuncionario(MyPair funcionario) {
		this.funcionario = funcionario;
	}
	public MyPair getTipoContactoInterno() {
		return tipoContactoInterno;
	}
	public void setTipoContactoInterno(MyPair tipoContactoInterno) {
		this.tipoContactoInterno = tipoContactoInterno;
	}
	
	
	public String toString(){
		String out = "";
		out += this.tipoContactoInterno.getText()+ " - " + this.funcionario.getText();
		return out;
	}
	
}
