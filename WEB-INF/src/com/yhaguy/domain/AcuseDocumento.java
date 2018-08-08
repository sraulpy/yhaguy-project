package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class AcuseDocumento extends Domain {

	private Date fecha;
	private String numero;
	private String tipoMovimiento;
	private String motivo;
	private String documentos;
	private String receptor;
	private boolean devuelto;
	
	private Set<AcuseDocumentoDetalle> detalles = new HashSet<AcuseDocumentoDetalle>();
	
	@Override
	public int compareTo(Object o) {
		return -1;
	}
	
	@DependsOn("documentos")
	public List<String> getNrosDocumentos() {
		if (this.documentos == null) {
			return new ArrayList<String>();
		}
		String[] list = documentos.split(";");
		return new ArrayList<String>(Arrays.asList(list));
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo.toUpperCase();
	}

	public String getReceptor() {
		return receptor;
	}

	public void setReceptor(String receptor) {
		this.receptor = receptor.toUpperCase();
	}

	public boolean isDevuelto() {
		return devuelto;
	}

	public void setDevuelto(boolean devuelto) {
		this.devuelto = devuelto;
	}

	public String getDocumentos() {
		return documentos;
	}

	public void setDocumentos(String documentos) {
		this.documentos = documentos;
	}

	public Set<AcuseDocumentoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<AcuseDocumentoDetalle> detalles) {
		this.detalles = detalles;
	}
}
