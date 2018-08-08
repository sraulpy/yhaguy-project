package com.yhaguy.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.domain.Domain;

@SuppressWarnings("serial")
public class ServicioTecnico extends Domain {
	
	private Date fecha;
	private Date fechaFin;
	private String numero;
	private String receptor;
	private String tecnico;
	private Cliente cliente;
	private boolean entregado;
	private String numeroReclamo;
	private String numeroReparto;
	private String choferReparto;
	
	private Set<Venta> facturas = new HashSet<Venta>();
	private Set<ServicioTecnicoDetalle> detalles = new HashSet<ServicioTecnicoDetalle>();
	
	/**
	 * @return las observaciones..
	 */
	public String getObservaciones() {
		String out = "";
		for (ServicioTecnicoDetalle det : this.detalles) {
			out += det.getObservacion() + " ";
		}
		return out.trim().isEmpty() ? "- - -" : out;
	}
	
	/**
	 * @return el nro de reclamo..
	 */
	public String getNumeroReclamo_() {
		return (this.numeroReclamo == null || this.numeroReclamo.trim().isEmpty()) ? "- - -" : this.numeroReclamo;
	}
	
	/**
	 * @return el nro de reparto..
	 */
	public String getNumeroReparto_() {
		return (this.numeroReparto == null || this.numeroReparto.trim().isEmpty()) ? "- - -" : 
			this.numeroReparto.replace("REP-", "") + " - " + this.choferReparto;
	}
	
	/**
	 * @return las facturas..
	 */
	public List<Venta> getFacturasList() {
		List<Venta> list = new ArrayList<>();
		list.addAll(this.facturas);
		return list;
	}
	
	/**
	 * @return los detalles..
	 */
	public List<ServicioTecnicoDetalle> getDetallesList() {
		List<ServicioTecnicoDetalle> list = new ArrayList<>();
		list.addAll(this.detalles);
		return list;
	}

	@DependsOn("facturas")
	public String getFacturas_() {
		String out = "";
		for (Venta venta : this.facturas) {
			out += venta.getNumero() + " - ";
		}
		return out;
	}
	
	@DependsOn("facturas")
	public List<String> getFacturas__() {
		List<String> out = new ArrayList<String>();
		for (Venta venta : this.facturas) {
			out.add(venta.getNumero());
		}
		return out;
	}
	
	@DependsOn("facturas")
	public List<Articulo> getArticulos() {
		List<Articulo> out = new ArrayList<Articulo>();
		for (Venta venta : this.facturas) {
			for (VentaDetalle det : venta.getDetalles()) {
				out.add(det.getArticulo());
			}
		}
		return out;
	}
	
	/**
	 * @return true si ya fue diagnosticado..
	 */
	public boolean isDiagnosticado() {
		if(this.detalles.size() == 0) return false;
		for (ServicioTecnicoDetalle det : this.detalles) {
			if (det.getDiagnostico() == null || det.getDiagnostico().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return los estados..
	 */
	public List<String> getEstados() {
		List<String> out = new ArrayList<String>();
		out.add("BUEN ESTADO"); out.add("GOLPEADA");
		out.add("ADULTERADA");	out.add("FILTRACIÓN");
		out.add("TAPA DESPRENDIDA"); out.add("BORNE DERRETIDO");
		out.add("BORNE ROTO"); out.add("CELDA SECA");
		return out;
	}
	
	/**
	 * @return las cargas..
	 */
	public List<String> getCargas() {
		List<String> out = new ArrayList<String>();
		out.add("CARGADA"); out.add("DESCARGADA");
		return out;
	}
	
	/**
	 * @return los bornes..
	 */
	public List<String> getBornes() {
		List<String> out = new ArrayList<String>();
		out.add("BUEN ESTADO"); out.add("MAL ESTADO");
		return out;
	}
	
	/**
	 * @return las celdas..
	 */
	public List<String> getCeldas() {
		List<String> out = new ArrayList<String>();
		out.add("SECA"); out.add("CARGADA");
		return out;
	}
	
	/**
	 * @return las conexiones..
	 */
	public List<String> getConexiones() {
		List<String> out = new ArrayList<String>();
		out.add("CORRECTA"); out.add("INTERRUMPIDA");
		return out;
	}
	
	@Override
	public int compareTo(Object arg0) {
		return -1;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getReceptor() {
		return receptor;
	}

	public void setReceptor(String receptor) {
		this.receptor = receptor;
	}

	public String getTecnico() {
		return tecnico;
	}

	public void setTecnico(String tecnico) {
		this.tecnico = tecnico;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Set<Venta> getFacturas() {
		return facturas;
	}

	public void setFacturas(Set<Venta> facturas) {
		this.facturas = facturas;
	}

	public Set<ServicioTecnicoDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<ServicioTecnicoDetalle> detalles) {
		this.detalles = detalles;
	}

	public boolean isEntregado() {
		return entregado;
	}

	public void setEntregado(boolean entregado) {
		this.entregado = entregado;
	}

	public String getNumeroReclamo() {
		return numeroReclamo;
	}

	public void setNumeroReclamo(String numeroReclamo) {
		this.numeroReclamo = numeroReclamo;
	}

	public String getNumeroReparto() {
		return numeroReparto;
	}

	public void setNumeroReparto(String numeroReparto) {
		this.numeroReparto = numeroReparto;
	}

	public String getChoferReparto() {
		return choferReparto;
	}

	public void setChoferReparto(String choferReparto) {
		this.choferReparto = choferReparto;
	}
}
