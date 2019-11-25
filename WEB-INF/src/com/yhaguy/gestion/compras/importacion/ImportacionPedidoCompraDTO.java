package com.yhaguy.gestion.compras.importacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.empresa.ProveedorDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class ImportacionPedidoCompraDTO extends DTO {

	private String numeroPedidoCompra = "";
	private Date fechaCreacion = new Date();
	private Date fechaCierre;
	private String observacion = "SIN OBS..";
	private String via = "";
	private double cambio = 0;
	private double totalImporteGs = 0;
	private double totalImporteDs = 0;
	
	private boolean confirmadoImportacion = false;
	private boolean confirmadoVentas = false;
	private boolean confirmadoAdministracion = false;
	private int propietarioActual = 1;
	private boolean pedidoConfirmado = false;
	private boolean importacionConfirmada = false;
	private boolean cifProrrateado = false;
	private boolean recepcionHabilitada = false;
	
	private boolean conteo1 = false;
	private boolean conteo2 = false;
	private boolean conteo3 = false;

	private MyArray proveedorCondicionPago = new MyArray();
	private MyPair estado = new MyPair();
	private MyArray moneda = new MyArray();
	private MyPair tipo = new MyPair();
	private MyArray tipoMovimiento = new MyArray();
	private MyPair deposito;
	
	private List<ImportacionPedidoCompraDetalleDTO> solicitudCotizaciones = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
	private List<ImportacionPedidoCompraDetalleDTO> importacionPedidoCompraDetalle = new ArrayList<ImportacionPedidoCompraDetalleDTO>();
	private List<ImportacionFacturaDTO> importacionFactura = new ArrayList<ImportacionFacturaDTO>();
	private ProveedorDTO proveedor = new ProveedorDTO();
	private ResumenGastosDespachoDTO resumenGastosDespacho = new ResumenGastosDespachoDTO();
	private MyArray subDiario;
	private List<ImportacionGastoImprevistoDTO> gastosImprevistos = new ArrayList<ImportacionGastoImprevistoDTO>();
	private List<ImportacionAplicacionAnticipoDTO> aplicacionAnticipos = new ArrayList<ImportacionAplicacionAnticipoDTO>();
	private List<MyArray> trazabilidad = new ArrayList<MyArray>();
	private List<CtaCteEmpresaMovimiento> desglose = new ArrayList<CtaCteEmpresaMovimiento>();
	
	private double totalGastos = 0;
	private double totalGastosDs = 0;
	
	/**
	 * @return true si es una compra en moneda local..
	 */
	public boolean isMonedaLocal() {
		String sigla = (String) this.moneda.getPos2();
		return sigla.equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	/**
	 * @return los gastos de importacion..
	 */
	public List<Object[]> getGastos() throws Exception {
		this.totalGastos = 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getGastosDeImportacion(this.getId());
		for (Object[] gasto : out) {
			double importe = (double) gasto[6];
			double importeDs = (double) gasto[7];
			this.totalGastos += importe;
			this.totalGastosDs += importeDs;
		}
		BindUtils.postNotifyChange(null, null, this, "totalGastos");
		return out;
	}
	
	/**
	 * @return los gastos de importacion..
	 */
	public List<Object[]> getGastosDetallado() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Object[]> out = rr.getGastosDeImportacionDetallado(this.getId());
		return out;
	}
	
	/**
	 * @return el total de anticipo aplicado..
	 */
	public double getTotalAnticipoAplicadoDs() {
		double out = 0;
		for (ImportacionAplicacionAnticipoDTO anticipo : this.aplicacionAnticipos) {
			out += anticipo.getImporteDs();
		}
		return out;
	}
	
	/**
	 * @return nro y fecha..
	 */
	public String getNumeroFecha() {
		return this.numeroPedidoCompra + " - " + Utiles.getDateToString(this.fechaCreacion, Utiles.DD_MM_YYYY);
	}
	
	/**
	 * @return el desglose de facturas..
	 */
	public Set<CtaCteEmpresaMovimiento> getDesglose_() {
		Set<CtaCteEmpresaMovimiento> out = new HashSet<CtaCteEmpresaMovimiento>();
		out.addAll(this.getDesglose());
		return out;
	}
	
	@DependsOn("subDiario")
	public boolean isSubDiarioConfirmado() {
		boolean subDiarioConfirmado = false;
		if (subDiario != null) {
			subDiarioConfirmado = (boolean) subDiario.getPos4();
		}
		return subDiarioConfirmado;
	}
	
	@DependsOn({"importacionPedidoCompraDetalle", "pedidoConfirmado"})
	public boolean isProveedorEditable() {
		boolean editable = true;
		if ((importacionPedidoCompraDetalle.size() > 0)
				|| (pedidoConfirmado == true)) {
			editable = false;
		}
		return editable;
	}
	
	public boolean isCondicionContado() {
		return this.proveedorCondicionPago.getId() 
				== Configuracion.ID_CONDICION_PAGO_CONTADO;
	}
	
	/**
	 * @return booleano que indica si es anulado
	 */
	public boolean isImportacionAnulada() {
		String sigla = this.estado.getSigla();
		return sigla.compareTo(Configuracion.SIGLA_IMPORTACION_ESTADO_ANULADO) == 0;
	}
	
	/**
	 * @return el importe segun tipo cambio despacho
	 */
	public double getImporteGsTcDespacho() {
		double totalFacDs = this.getImportacionFactura().get(0).getTotalImporteDs();
		double tcDesp = this.getResumenGastosDespacho().getTipoCambio();
		return totalFacDs * tcDesp;
	}

	public String getDetalleSize() {
		return this.importacionPedidoCompraDetalle.size() + " ítems";
	}

	public String getNumeroPedidoCompra() {
		return numeroPedidoCompra;
	}

	public void setNumeroPedidoCompra(String numeroPedidoCompra) {
		this.numeroPedidoCompra = numeroPedidoCompra;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion.trim().toUpperCase();
	}

	public double getCambio() {
		return cambio;
	}

	public void setCambio(double cambio) {
		this.cambio = this.getMisc().redondeoCuatroDecimales(cambio);
	}

	public boolean isConfirmadoImportacion() {
		return confirmadoImportacion;
	}

	public void setConfirmadoImportacion(boolean confirmadoImportacion) {
		this.confirmadoImportacion = confirmadoImportacion;
	}

	public MyArray getProveedorCondicionPago() {
		return proveedorCondicionPago;
	}

	public void setProveedorCondicionPago(MyArray proveedorCondicionPago) {
		this.proveedorCondicionPago = proveedorCondicionPago;
	}

	public boolean isConfirmadoVentas() {
		return confirmadoVentas;
	}

	public void setConfirmadoVentas(boolean confirmadoVentas) {
		this.confirmadoVentas = confirmadoVentas;
	}

	public boolean isConfirmadoAdministracion() {
		return confirmadoAdministracion;
	}

	public void setConfirmadoAdministracion(boolean confirmadoAdministracion) {
		this.confirmadoAdministracion = confirmadoAdministracion;
	}

	public int getPropietarioActual() {
		return propietarioActual;
	}

	public void setPropietarioActual(int propietarioActual) {
		this.propietarioActual = propietarioActual;
	}

	public boolean isPedidoConfirmado() {
		return pedidoConfirmado;
	}

	public void setPedidoConfirmado(boolean pedidoConfirmado) {
		this.pedidoConfirmado = pedidoConfirmado;
	}

	public boolean isImportacionConfirmada() {
		return importacionConfirmada;
	}

	public void setImportacionConfirmada(boolean importacionConfirmada) {
		this.importacionConfirmada = importacionConfirmada;
	}

	public boolean isCifProrrateado() {
		return cifProrrateado;
	}

	public void setCifProrrateado(boolean cifProrrateado) {
		this.cifProrrateado = cifProrrateado;
	}

	public MyArray getMoneda() {
		return moneda;
	}

	public void setMoneda(MyArray moneda) {
		this.moneda = moneda;
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public MyPair getEstado() {
		return estado;
	}

	public void setEstado(MyPair estado) {
		this.estado = estado;
	}

	public List<ImportacionPedidoCompraDetalleDTO> getImportacionPedidoCompraDetalle() {
		return importacionPedidoCompraDetalle;
	}

	public void setImportacionPedidoCompraDetalle(
			List<ImportacionPedidoCompraDetalleDTO> importacionPedidoCompraDetalle) {
		this.importacionPedidoCompraDetalle = importacionPedidoCompraDetalle;
	}

	public List<ImportacionFacturaDTO> getImportacionFactura() {
		return importacionFactura;
	}

	public void setImportacionFactura(List<ImportacionFacturaDTO> importacionFactura) {
		this.importacionFactura = importacionFactura;
	}

	public ProveedorDTO getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorDTO proveedor) {
		this.proveedor = proveedor;
	}

	public ResumenGastosDespachoDTO getResumenGastosDespacho() {
		return resumenGastosDespacho;
	}

	public void setResumenGastosDespacho(ResumenGastosDespachoDTO resumenGastosDespacho) {
		this.resumenGastosDespacho = resumenGastosDespacho;
	}

	public MyArray getSubDiario() {
		return subDiario;
	}

	public void setSubDiario(MyArray subDiario) {
		this.subDiario = subDiario;
	}

	public String getPropietario() {
		String out = "";

		switch (this.propietarioActual) {
		case 1:
			out = Configuracion.PROPIETARIO_IMPORTACION_DESCRIPCION;
			break;
		case 2:
			out = Configuracion.PROPIETARIO_VENTAS_DESCRIPCION;
			break;
		case 3:
			out = Configuracion.PROPIETARIO_ADMINISTRACION_DESCRIPCION;
			break;
		}

		return out;
	}

	public List<ImportacionGastoImprevistoDTO> getGastosImprevistos() {
		return gastosImprevistos;
	}

	public void setGastosImprevistos(List<ImportacionGastoImprevistoDTO> gastosImprevistos) {
		this.gastosImprevistos = gastosImprevistos;
	}

	public MyPair getTipo() {
		return tipo;
	}

	public void setTipo(MyPair tipo) {
		this.tipo = tipo;
	}

	public String toString() {
		String out = this.getNumeroPedidoCompra() + " - " + this.getProveedor().getRazonSocial();
		return out;
	}

	public MyPair getDeposito() {
		return deposito;
	}

	public void setDeposito(MyPair deposito) {
		this.deposito = deposito;
	}

	public double getTotalImporteGs() {
		return totalImporteGs;
	}

	public void setTotalImporteGs(double totalImporteGs) {
		this.totalImporteGs = totalImporteGs;
	}

	public double getTotalImporteDs() {
		return totalImporteDs;
	}

	public void setTotalImporteDs(double totalImporteDs) {
		this.totalImporteDs = totalImporteDs;
	}

	public List<ImportacionAplicacionAnticipoDTO> getAplicacionAnticipos() {
		return aplicacionAnticipos;
	}

	public void setAplicacionAnticipos(List<ImportacionAplicacionAnticipoDTO> aplicacionAnticipos) {
		this.aplicacionAnticipos = aplicacionAnticipos;
	}

	public List<ImportacionPedidoCompraDetalleDTO> getSolicitudCotizaciones() {
		return solicitudCotizaciones;
	}

	public void setSolicitudCotizaciones(List<ImportacionPedidoCompraDetalleDTO> solicitudCotizaciones) {
		this.solicitudCotizaciones = solicitudCotizaciones;
	}

	public List<MyArray> getTrazabilidad() {
		return trazabilidad;
	}

	public void setTrazabilidad(List<MyArray> trazabilidad) {
		this.trazabilidad = trazabilidad;
	}

	public double getTotalGastos() {
		return totalGastos;
	}

	public void setTotalGastos(double totalGastos) {
		this.totalGastos = totalGastos;
	}

	public String getVia() {
		return via;
	}

	public void setVia(String via) {
		this.via = via;
	}

	public double getTotalGastosDs() {
		return totalGastosDs;
	}

	public void setTotalGastosDs(double totalGastosDs) {
		this.totalGastosDs = totalGastosDs;
	}

	public boolean isRecepcionHabilitada() {
		return recepcionHabilitada;
	}

	public void setRecepcionHabilitada(boolean recepcionHabilitada) {
		this.recepcionHabilitada = recepcionHabilitada;
	}

	public boolean isConteo1() {
		return conteo1;
	}

	public void setConteo1(boolean conteo1) {
		this.conteo1 = conteo1;
	}

	public boolean isConteo2() {
		return conteo2;
	}

	public void setConteo2(boolean conteo2) {
		this.conteo2 = conteo2;
	}

	public boolean isConteo3() {
		return conteo3;
	}

	public void setConteo3(boolean conteo3) {
		this.conteo3 = conteo3;
	}

	public List<CtaCteEmpresaMovimiento> getDesglose() {
		return desglose;
	}

	public void setDesglose(List<CtaCteEmpresaMovimiento> desglose) {
		this.desglose = desglose;
	}
}
