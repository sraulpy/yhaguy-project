package com.yhaguy.gestion.mobile.appstore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.VentaPromo1;

public class Promo1ViewModel extends SimpleViewModel {
	
	private String ruc = "";
	private String nombreApellido = "";
	private String direccion = "";
	private String telefono = "";
	private Date fechaNacimiento;
	private String correo = "";
	private String operadora = "";
	
	private String razonSocial = "";
	private String mensaje = "";
	private Empresa empresa;
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void registrarse() throws Exception {		
		if (this.chequearRuc()) {			
			if (!chequearRegistro()) {
				if(!this.chequearDatos()) {
					this.mensaje = "DEBE INGRESAR TODOS LOS DATOS..";
					return;
				}				
				this.registrar();
			} else {
				this.mensaje = "CLIENTE YA FUE REGISTRADO : " + this.empresa.getRazonSocial();
			}
		} else {
			this.mensaje = "CLIENTE NO ENCONTRADO..";
		}
	}
	
	/**
	 * @return true si los datos son validos..
	 */
	private boolean chequearDatos() {
		if (this.ruc.trim().isEmpty() || this.nombreApellido.trim().isEmpty() || this.direccion.trim().isEmpty()
				|| this.telefono.trim().isEmpty() || this.fechaNacimiento == null || this.correo.trim().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * chequea el ruc en la bd..
	 */
	private boolean chequearRuc() throws Exception {
		if (this.ruc.trim().isEmpty()) {
			return false;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Empresa> emps = rr.getEmpresasByRuc(this.ruc);
		if(emps.size() > 0) {
			this.razonSocial = emps.get(0).getRazonSocial();
			this.empresa = emps.get(0);
		}
		return emps.size() > 0;
	}
	
	/**
	 * chequea el ruc en la bd..
	 */
	private boolean chequearRegistro() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<VentaPromo1> promos = rr.getVentaPromo1Registros(this.ruc);	
		return promos.size() > 0;
	}
	
	/**
	 * realiza el registro..
	 */
	private void registrar() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		VentaPromo1 promo = new VentaPromo1();
		promo.setEmpresa(this.empresa);
		promo.setFecha(new Date());
		promo.setNombreApellido(this.nombreApellido.toUpperCase());
		promo.setDireccion(this.direccion.toUpperCase());
		promo.setTelefono(this.telefono);
		promo.setNacimiento(this.fechaNacimiento);
		promo.setCorreo(this.correo);
		promo.setAuxi(this.operadora);
		rr.saveObject(promo, "mobile");
		this.mensaje = "CLIENTE CORRECTAMENTE REGISTRADO: " + promo.getEmpresa().getRazonSocial();
	}
	
	/**
	 * GETS / SETS..
	 */
	
	public List<String> getOperadoras() {
		List<String> out = new ArrayList<String>();
		out.add("TIGO");
		out.add("PERSONAL");
		out.add("CLARO");
		out.add("VOX");
		return out;
	}
	
	public String getRuc() {
		return ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getNombreApellido() {
		return nombreApellido;
	}

	public void setNombreApellido(String nombreApellido) {
		this.nombreApellido = nombreApellido;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getOperadora() {
		return operadora;
	}

	public void setOperadora(String operadora) {
		this.operadora = operadora;
	}
}
