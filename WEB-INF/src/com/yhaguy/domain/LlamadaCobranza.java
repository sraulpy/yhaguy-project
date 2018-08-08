package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class LlamadaCobranza extends Domain {
	
	public static final String NO_RESPONDE = "NO RESPONDE";
	public static final String VOLVER_A_LLAMAR = "VOLVER A LLAMAR";
	public static final String PASAR_A_COBRAR = "PASAR A COBRAR";
	public static final String PASA_A_PAGAR = "PASA A PAGAR";
	
	private Date fecha;
	private String usuario;
	private String detalle;
	private String resultado;
	
	private Empresa empresa;

	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	/**
	 * @return los resultados de las llamadas..
	 */
	public static List<String> getResultados() {
		List<String> out = new ArrayList<String>();
		out.add(NO_RESPONDE);
		out.add(VOLVER_A_LLAMAR);
		out.add(PASAR_A_COBRAR);
		out.add(PASA_A_PAGAR);
		return out;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

}
