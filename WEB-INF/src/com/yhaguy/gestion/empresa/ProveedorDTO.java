package com.yhaguy.gestion.empresa;

import java.util.ArrayList;
import java.util.List;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;

public class ProveedorDTO extends DTO {

	EmpresaDTO empresa = new EmpresaDTO();
	MyPair estadoProveedor;
	MyPair tipoProveedor;
	List<MyArray> condicionPagos = new ArrayList<MyArray>();
	private List<MyArray> emails = new ArrayList<MyArray>();
	
	private MyArray cuentaContable = new MyArray();

	private long prioridad = 0;
	private boolean completo = false;

	public String getTextOrden() {
		return this.getNombre();
	}

	public long getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(long prioridad) {
		this.prioridad = prioridad;
	}

	public boolean isCompleto() {
		return completo;
	}

	public void setCompleto(boolean completo) {
		this.completo = completo;
	}

	public String getNombre() {
		return this.getEmpresa().getNombre();
	}

	public void setNombre(String nombre) {
		this.getEmpresa().setNombre(nombre);
	}

	public String getCodigoEmpresa() {
		return this.getEmpresa().getCodigoEmpresa();
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.getEmpresa().setCodigoEmpresa(codigoEmpresa);
	}

	public String getRazonSocial() {
		return this.getEmpresa().getRazonSocial();
	}

	public void setRazonSocial(String razonSocial) {
		this.getEmpresa().setRazonSocial(razonSocial);
	}

	public String getRuc() {
		return this.getEmpresa().getRuc();
	}

	public void setRuc(String ruc) {
		this.getEmpresa().setRuc(ruc);
	}

	public MyPair getMoneda() {
		return this.getEmpresa().getMoneda();
	}

	public MyArray getMonedaConSimbolo() {
		return this.getEmpresa().getMonedaConSimbolo();
	}

	public EmpresaDTO getEmpresa() {
		return empresa;
	}

	public void setEmpresa(EmpresaDTO empresa) {
		this.empresa = empresa;
	}

	public MyPair getEstadoProveedor() {
		return estadoProveedor;
	}

	public void setEstadoProveedor(MyPair estadoProveedor) {
		this.estadoProveedor = estadoProveedor;
	}

	public MyPair getTipoProveedor() {
		return tipoProveedor;
	}

	public void setTipoProveedor(MyPair tipoProveedor) {
		this.tipoProveedor = tipoProveedor;
	}

	public List<MyArray> getCondicionPagos() {
		return condicionPagos;
	}

	public void setCondicionPagos(List<MyArray> condicionPagos) {
		this.condicionPagos = condicionPagos;
	}

	public List<MyArray> getEmails() {
		return emails;
	}

	public void setEmails(List<MyArray> emails) {
		this.emails = emails;
	}

	public MyArray getCuentaContable() {
		return cuentaContable;
	}

	public void setCuentaContable(MyArray cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public String toString() {
		String out = "";
		if (this.getEmpresa() != null) {
			out += this.getEmpresa().getNombre() + " ["
					+ this.getEmpresa().getRazonSocial() + "] - ruc:"
					+ this.getEmpresa().getRuc() + "   (" + this.getId() + ")";
		} else {
			out += " sin empresa " + "(" + this.getId() + ")";
		}
		return out;
	}

}
