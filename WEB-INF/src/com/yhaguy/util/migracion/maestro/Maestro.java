package com.yhaguy.util.migracion.maestro;

import com.coreweb.domain.Domain;

public class Maestro extends Domain {

	// Lista de campos que hay en Yhaguy
	String codigoProveedor = "";
	String codigoOriginal = "";
	String descripcion = "";
	String datoExtra = "";
	String marca_Aplicacion = "";
	String imagen = "" ;
	String peso = "" ;
	String marca = "";
	String parte = "" ;
	String linea = "" ; 
	String familia = "" ; 
	String codigoBarra = "" ;
	
	
	
	
	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}

	public String getParte() {
		return parte;
	}

	public void setParte(String parte) {
		this.parte = parte;
	}

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getFamilia() {
		return familia;
	}

	public void setFamilia(String familia) {
		this.familia = familia;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getMarca_Aplicacion() {
		return marca_Aplicacion;
	}

	public void setMarca_Aplicacion(String marca_Aplicacion) {
		this.marca_Aplicacion = marca_Aplicacion;
	}

	public String getCodigoProveedor() {
		return codigoProveedor;
	}

	public void setCodigoProveedor(String codigoProveedor) {
		this.codigoProveedor = codigoProveedor;
	}

	public String getCodigoOriginal() {
		return codigoOriginal;
	}

	public void setCodigoOriginal(String codigoOriginal) {
		this.codigoOriginal = codigoOriginal;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDatoExtra() {
		return datoExtra;
	}

	public void setDatoExtra(String datoExtra) {
		this.datoExtra = datoExtra;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
