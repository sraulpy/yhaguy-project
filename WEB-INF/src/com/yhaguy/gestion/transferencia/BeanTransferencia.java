package com.yhaguy.gestion.transferencia;

public class BeanTransferencia {

	private String fecha;
	private String numero;
	private String origen;
	private String destino;
	private String nroremision;
	private String importe;

	public BeanTransferencia(String fecha, String numero, String origen,
			String destino, String nroremision, String importe) {
		this.fecha = fecha;
		this.numero = numero;
		this.origen = origen;
		this.destino = destino;
		this.nroremision = nroremision;
		this.importe = importe;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getNroremision() {
		return nroremision;
	}

	public void setNroremision(String nroremision) {
		this.nroremision = nroremision;
	}

	public String getImporte() {
		return importe;
	}

	public void setImporte(String importe) {
		this.importe = importe;
	}
}
