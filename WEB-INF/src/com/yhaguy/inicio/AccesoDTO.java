package com.yhaguy.inicio;

import java.util.ArrayList;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

@SuppressWarnings("serial")
public class AccesoDTO extends DTO {

	private MyArray funcionario;

	private MyPair sucursalOperativa;

	private List<MyArray> sucursales = new ArrayList<MyArray>();

	private MyArray departamento;
	
	private MyArray usuario;



	public MyArray getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(MyArray funcionario) {
		this.funcionario = funcionario;
	}

	public MyArray getUsuario() {
		return usuario;
	}

	public void setUsuario(MyArray usuario) {
		this.usuario = usuario;
	}

	public MyPair getSucursalOperativa() {
		return sucursalOperativa;
	}

	public void setSucursalOperativa(MyPair sucursalOperativa) {
		this.sucursalOperativa = sucursalOperativa;
	}

	public List<MyArray> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<MyArray> sucursales) {
		this.sucursales = sucursales;
	}

	public MyArray getDepartamento() {
		return departamento;
	}

	public void setDepartamento(MyArray departamento) {
		this.departamento = departamento;
	}

}
