package com.yhaguy.gestion.notacredito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;

@SuppressWarnings("serial")
public class NotaCreditoDTO extends DTO {
	
	private String numero = "";
	private String timbrado_ = "";
	private String observacion = "Sin obs..";
	private String enlace = "";
	private String numeroNotaCredito = "";
	
	private Date fechaEmision = new Date();
	
	private double importeDs = 0;
	
	private double tipoCambio = 0;
	
	private String cajaNro = "";
	private String planillaCajaNro = "";
	private String cajero = "";
	
	private MyPair moneda = new MyPair();
	private MyArray tipoMovimiento = new MyArray();	
	private MyPair estadoComprobante = new MyPair();
	private MyPair motivo = new MyPair();
	private MyPair sucursal = new MyPair();
	private MyArray cliente = new MyArray();
	private MyArray proveedor = new MyArray();
	private MyArray vendedor = new MyArray();
	private MyArray timbrado = new MyArray();

	private List<NotaCreditoDetalleDTO> detalles = new ArrayList<NotaCreditoDetalleDTO>();
	private List<MyArray> serviciosTecnicos = new ArrayList<MyArray>();
	
	/** para que el assembler sepa que debe actualizar los datos de cta cte, stock, costos, etc **/
	private boolean actualizarDatos = false;
	
	@DependsOn("serviciosTecnicos")
	public String getServiciosTecnicos_() {
		String out = "";
		for (MyArray servtec : this.serviciosTecnicos) {
			out += servtec.getPos1().toString().replace("SER-TEC-", "") + " - ";
		}
		return out;
	}
	
	@DependsOn({"detalles", "motivo"})
	public double getTotalIva10() {
		double out = 0;
		for (NotaCreditoDetalleDTO item : this.getDetallesByMotivo()) {
			if (item.isIva10())
				out += item.getIva10();
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva5() {
		double out = 0;		
		for (NotaCreditoDetalleDTO item : this.getDetallesByMotivo()) {
			if(item.isIva5())
				out += item.getIva5();
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getImporteGs() {
		double out = 0;		
		for (NotaCreditoDetalleDTO item : this.getDetallesByMotivo()) {
			out += item.getImporteGs();				
		}		
		return out;
	}
	
	/**
	 * @return el importe total sin iva..
	 */
	@DependsOn("detalles")
	public double getTotalImporteGsSinIva() {
		return this.getImporteGs() - this.getTotalIva10();
	}
	
	@DependsOn("motivo")
	public List<NotaCreditoDetalleDTO> getDetallesByMotivo() {
		return this.isMotivoDescuento() ? this.getDetallesFacturas() : this
				.getDetallesArticulos();
	}
	
	/**
	 * @return el total del iva..
	 */
	public double getTotalIva() {
		return this.getTotalIva5() + this.getTotalIva10();
	}
	
	/**
	 * Retorna true si la moneda de la N.C. es local
	 */
	public boolean isMonedaLocal(){
		String sigla = (String) this.moneda.getSigla();
		return sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0;
	}
	
	/**
	 * Retorna true si es una N.C. de venta..
	 */
	public boolean isNotaCreditoVenta() {		
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA)
				|| sigla.equals(Configuracion.SIGLA_TM_SOLICITUD_NC_VENTA);
	}
	
	/**
	 * @return si se aplica a una venta contado..
	 */
	public boolean isNotaCreditoVentaContado() {
		for (NotaCreditoDetalleDTO item : this.detalles) {
			if (item.getVenta() != null && (item.getVenta().getPos5().toString().equals(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO)))
				return true;
		}
		return false;
	}
	
	/**
	 * Retorna true si es una N.C. de compra..
	 */
	public boolean isNotaCreditoCompra() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA);
	}
	
	/**
	 * @return true si es una Solicitud N.C. de venta..
	 */
	public boolean isSolicitudNotaCreditoVenta() {
		String sigla = (String) this.tipoMovimiento.getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_SOLICITUD_NC_VENTA);	
	}
	
	/**
	 * @return true si el motivo es por descuento..
	 */
	public boolean isMotivoDescuento() {
		String sigla = (String) this.motivo.getSigla();
		return sigla.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DESCUENTO);	
	}
	
	/**
	 * @return true si el motivo es por devolucion..
	 */
	public boolean isMotivoDevolucion(){
		String sigla = (String) this.motivo.getSigla();
		return sigla.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION);	
	}
	
	/**
	 * @return true si el motivo es por reclamo..
	 */
	public boolean isMotivoReclamo(){
		String sigla = (String) this.motivo.getSigla();
		return sigla.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_RECLAMO);	
	}
	
	/**
	 * @return true si el motivo es por diferencia de precio..
	 */
	public boolean isMotivoDiferenciaPrecio(){
		String sigla = (String) this.motivo.getSigla();
		return sigla.equals(Configuracion.SIGLA_TIPO_NC_MOTIVO_DIF_PRECIO);	
	}
	
	/**
	 * @return true si esta anulado..
	 */
	public boolean isAnulado(){
		String sigla = (String) this.estadoComprobante.getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO);	
	}
	
	/**
	 * @return los detalles que son de tipo factura..
	 */
	public List<NotaCreditoDetalleDTO> getDetallesFacturas() {
		List<NotaCreditoDetalleDTO> out = new ArrayList<NotaCreditoDetalleDTO>();
		
		for (NotaCreditoDetalleDTO item : this.detalles) {
			if (item.isDetalleFactura() == true) {
				out.add(item);
			}
		}		
		return out;
	}
	
	/**
	 * @return los detalles que son de tipo articulo..
	 */
	public List<NotaCreditoDetalleDTO> getDetallesArticulos() {
		List<NotaCreditoDetalleDTO> out = new ArrayList<NotaCreditoDetalleDTO>();
		
		for (NotaCreditoDetalleDTO item : this.detalles) {
			if (item.isDetalleFactura() == false) {
				out.add(item);
			}
		}		
		return out;
	}
	
	/**
	 * Retorna las facturas que fueron aplicadas..
	 */
	public List<MyArray> getFacturas(){
		List<MyArray> out = new ArrayList<MyArray>();		
		
		for (NotaCreditoDetalleDTO item : this.getDetallesFacturas()) {
						
			if (item.isDetalleVenta() == true) {
				out.add(item.getVenta());
				
			} else if (item.isDetalleGasto() == true) {
				out.add(item.getGasto());
				
			} else if (item.isDetalleCompra() == true) {
				out.add(item.getCompra());
			
			} else if (item.isDetalleImportacion() == true) {
				out.add(item.getImportacion());
			}
		}		
		return out;
	}
	
	/**
	 * Retorna el importe en moneda local y extranjera de la lista de facturas..
	 * [0] importe Gs
	 * [1] importe Ds
	 */
	public Object[] getImportesFacturas(){
		double importeGs = 0;
		double importeDs = 0;
		
		for (NotaCreditoDetalleDTO item : this.getDetallesFacturas()) {
			importeGs += item.getMontoGs();
			importeDs += item.getMontoDs();
		}		
		return new Object[]{importeGs, importeDs};
	}
	
	/**
	 * Retorna el importe en moneda local y extranjera de la lista de devoluciones..
	 * [0] importe Gs
	 * [1] importe Ds
	 */
	public Object[] getImportesDevoluciones(){
		double importeGs = 0;
		double importeDs = 0;
		
		for (NotaCreditoDetalleDTO item : this.getDetallesArticulos()) {
			importeGs += item.getImporteGs();
			importeDs += item.getImporteDs();
		}		
		return new Object[]{importeGs, importeDs};
	}
	
	/**
	 * @return el importe solo de las ventas contado de la nota de credito..
	 */
	public double getImporteVentaContadoGs() {
		double out = 0;
		for (NotaCreditoDetalleDTO item : this.getDetallesFacturas()) {
			if (item.isDetalleVenta()) {
				String siglaTm = (String) item.getVenta().getPos5();
				if (siglaTm.equals(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO))
					out += item.getImporteGs();
			}
		}
		return out;
	}
	
	/**
	 * Retorna true si el detalle esta vacio..
	 */
	public boolean isDetalleEmpty(){
		return this.detalles.size() == 0;
	}
	
	/**
	 * @return la razon social..
	 */
	public String getRazonSocial() {
		return (String) this.cliente.getPos2();
	}
	
	/**
	 * @return el ruc..
	 */
	public String getRuc() {
		return (String) this.cliente.getPos3();
	}
	
	/**
	 * @return la direccion..
	 */
	public String getDireccion() {
		return (String) this.cliente.getPos5();
	}
	
	/**
	 * @return la direccion..
	 */
	public String getTelefono() {
		return (String) this.cliente.getPos6();
	}
	
	/**
	 * @return la fecha formateada..
	 */
	public String getFechaEmision_() {
		return this.getMisc().dateToString(this.fechaEmision, Misc.DD_MM_YYYY);
	}
	
	/**
	 * @return el importe en letras..
	 */
	public String getImporteEnLetras() {
		return getMisc().numberToLetter(this.getImporteGs()).toLowerCase();
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public void setImporteGs(double importeGs) {
	}

	public double getImporteDs() {
		return importeDs;
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}

	public double getImporteIva() {
		double importeFac = (double) this.getImportesFacturas()[0];
		double importeDev = (double) this.getImportesDevoluciones()[0];
		double importe = this.isMotivoDescuento()? importeFac : importeDev;
		double iva = this.getMisc().calcularIVA(importe,
				Configuracion.VALOR_IVA_10);
		return this.getMisc().redondeoDosDecimales(iva);
	}

	public void setImporteIva(double importeIva) {
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyPair getMotivo() {
		return motivo;
	}

	public void setMotivo(MyPair motivo) {
		this.motivo = motivo;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public List<NotaCreditoDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<NotaCreditoDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public MyPair getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(MyPair estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public MyArray getCliente() {
		return cliente;
	}

	public void setCliente(MyArray cliente) {
		this.cliente = cliente;
	}

	public MyArray getProveedor() {
		return proveedor;
	}

	public void setProveedor(MyArray proveedor) {
		this.proveedor = proveedor;
		this.timbrado = new MyArray();
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public MyPair getMoneda() {
		return moneda;
	}

	public void setMoneda(MyPair moneda) {
		this.moneda = moneda;
	}

	public String getEnlace() {
		return enlace;
	}

	public void setEnlace(String enlace) {
		this.enlace = enlace;
	}

	public String getCajaNro() {
		return cajaNro;
	}

	public void setCajaNro(String cajaNro) {
		this.cajaNro = cajaNro;
	}

	public String getPlanillaCajaNro() {
		return planillaCajaNro;
	}

	public void setPlanillaCajaNro(String planillaCajaNro) {
		this.planillaCajaNro = planillaCajaNro;
	}

	public String getCajero() {
		return cajero;
	}

	public void setCajero(String cajero) {
		this.cajero = cajero;
	}

	public boolean isActualizarDatos() {
		return actualizarDatos;
	}

	public void setActualizarDatos(boolean actualizarCtaCte) {
		this.actualizarDatos = actualizarCtaCte;
	}

	public MyArray getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(MyArray timbrado) {
		this.timbrado = timbrado;
	}

	public String getNumeroNotaCredito() {
		return numeroNotaCredito;
	}

	public void setNumeroNotaCredito(String numeroNotaCredito) {
		this.numeroNotaCredito = numeroNotaCredito;
	}

	public void setVendedor(MyArray vendedor) {
		this.vendedor = vendedor;
	}

	public MyArray getVendedor() {
		return vendedor;
	}

	public String getTimbrado_() {
		return timbrado_;
	}

	public void setTimbrado_(String timbrado_) {
		this.timbrado_ = timbrado_;
	}

	public List<MyArray> getServiciosTecnicos() {
		return serviciosTecnicos;
	}

	public void setServiciosTecnicos(List<MyArray> serviciosTecnicos) {
		this.serviciosTecnicos = serviciosTecnicos;
	}
}
