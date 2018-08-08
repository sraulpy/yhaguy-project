package com.yhaguy.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.util.Misc;

@SuppressWarnings("serial")
public class InvPlanilla extends Domain {

	Misc m = new Misc();

	// string con las ubicaciones
	private String ubicacion;

	// todas las copias de una planilla, tienen el mismo número de lote
	// este número va a ser más corto que cargar las ubicaciones.
	private int lote;
	// numero de conteo de una planilla, se pueden hacer varios conteos de una
	// planilla
	private int conteo = 0;

	private Date fechaCarga;
	private String codigoVerificacion = "666-999";
	private String observacion;
	private Set<InvPlanillaDetalle> invPlanillaDetalle = new HashSet<InvPlanillaDetalle>();
	private boolean esFinal = false;
	private boolean recarga = false;
	private boolean cerrada = false;

	public void resetCodigoVerificacion(){
	
		String l =  m.ceros(""+(((this.lote + 3)*54)-13), 3);
		String r =  m.ceros(""+(((this.lote + 7)*32)-13), 3);
		
		this.codigoVerificacion = l+"-"+r;		
	}
	
	
	public boolean isCerrada() {
		return cerrada;
	}

	public void setCerrada(boolean cerrada) {
		this.cerrada = cerrada;
	}

	public boolean isRecarga() {
		return recarga;
	}

	public void setRecarga(boolean recarga) {
		this.recarga = recarga;
	}

	// contadores
	private String contador1;
	private String contador2;
	// primera carga
	private String cargador1A;
	private String cargador1B;
	// segunda carga
	private String cargador2A;
	private String cargador2B;

	// cuando se quiere hacer copia de una planilla para un nuevo conteo
	// OjO, hay que ver que número de conteo le corresponde
	@SuppressWarnings("rawtypes")
	public InvPlanilla getCopia() {
		InvPlanilla pl = new InvPlanilla();

		pl.setLote(this.getLote());
		pl.setUbicacion(this.getUbicacion());

		for (Iterator iterator = this.getInvPlanillaDetalle().iterator(); iterator
				.hasNext();) {
			InvPlanillaDetalle dep = (InvPlanillaDetalle) iterator.next();

			InvPlanillaDetalle depC = new InvPlanillaDetalle();

			depC.setInvArticulo(dep.getInvArticulo());
			depC.setInvUbicacion(dep.getInvUbicacion());

			pl.getInvPlanillaDetalle().add(depC);
		}

		return pl;
	}

	public int getConteo() {
		return conteo;
	}

	public void setConteo(int conteo) {
		this.conteo = conteo;
	}

	public int getLote() {
		return lote;
	}

	public void setLote(int lote) {
		this.lote = lote;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getContador1() {
		return contador1;
	}

	public void setContador1(String contador1) {
		this.contador1 = contador1;
	}

	public String getContador2() {
		return contador2;
	}

	public void setContador2(String contador2) {
		this.contador2 = contador2;
	}

	public String getCargador1A() {
		return cargador1A;
	}

	public void setCargador1A(String cargador1a) {
		cargador1A = cargador1a;
	}

	public String getCargador1B() {
		return cargador1B;
	}

	public void setCargador1B(String cargador1b) {
		cargador1B = cargador1b;
	}

	public String getCargador2A() {
		return cargador2A;
	}

	public void setCargador2A(String cargador2a) {
		cargador2A = cargador2a;
	}

	public String getCargador2B() {
		return cargador2B;
	}

	public void setCargador2B(String cargador2b) {
		cargador2B = cargador2b;
	}

	public boolean isEsFinal() {
		return esFinal;
	}

	public void setEsFinal(boolean esFinal) {
		this.esFinal = esFinal;
	}

	public Set<InvPlanillaDetalle> getInvPlanillaDetalle() {
		return invPlanillaDetalle;
	}

	public void setInvPlanillaDetalle(Set<InvPlanillaDetalle> invPlanillaDetalle) {
		this.invPlanillaDetalle = invPlanillaDetalle;
	}

	public Date getFechaCarga() {
		return fechaCarga;
	}

	public void setFechaCarga(Date fechaCarga) {
		this.fechaCarga = fechaCarga;
	}

	public String getCodigoVerificacion() {
		return codigoVerificacion;
	}

	public void setCodigoVerificacion(String codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
