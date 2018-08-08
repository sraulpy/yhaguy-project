package com.yhaguy.gestion.compras.gastos.subdiario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.caja.recibos.ReciboFormaPagoDTO;

@SuppressWarnings({ "unused", "serial" })
public class GastoDTO extends DTO{

	private Date fecha = new Date();
	private Date vencimiento;
	private String numeroFactura = "";
	private String numeroTimbrado = "";
	private double tipoCambio = 0;
	private double totalAsignado = 0;
	private double totalIvaAsignado = 0;	
	private boolean existeComprobanteFisico = true;
	private String motivoComprobanteFisico = "";
	private String cajaPagoNumero = "";
	private String observacion = "";
	
	/** cuando es autoFactura se especifica el beneficiario **/
	private String beneficiario = "";
	
	private double importeGs = 0;
	private double importeDs = 0;
	private double importeIva10 = 0;
	private double importeIva5 = 0;
	
	/** Id que hace referencia a las importaciones **/
	private long idImportacion = -1;
	private String numeroImportacion = "";
	private String despachante = "";
	
	/** indica si es un debito bancario **/
	private boolean debitoBancario = false;
	
	/** gastos contado opcion no generar saldo **/
	private boolean no_generar_saldo = false;
	
	/** Booleano que indica si es un Gasto de Fondo Fijo **/
	private boolean fondoFijo = false;
	
	private MyArray proveedor = new MyArray(); //pos1:codigo - pos2:razonSocial - pos3:ruc
	private MyArray moneda = new MyArray();
	private MyArray tipoMovimiento = new MyArray();
	private MyArray timbrado = new MyArray();
	private MyArray condicionPago = new MyArray();
	private MyPair estadoComprobante = new MyPair();
	private MyPair sucursal = new MyPair();
	private MyArray banco;
	
	private MyArray importacion;
	private boolean gastoImportacion = false;
	
	private List<GastoDetalleDTO> detalles = new ArrayList<GastoDetalleDTO>();
	private List<ReciboFormaPagoDTO> formasPago = new ArrayList<ReciboFormaPagoDTO>();
	
	/** las autofacturas se enumeran segun el talonario **/
	private MyArray talonarioAutoFactura;
	
	/**
	 * @return el importe total en la moneda que recibe como parametro..
	 */
	public double getImporteTotal(String siglaMoneda) {
		boolean monedaLocal = siglaMoneda.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0;
		return monedaLocal? this.getTotalImporteGs() : this.getTotalImporteDs();
	}
	
	@DependsOn("formasPago")
	public double getImporteFormaPagoGs() {
		double out = 0;
		for (ReciboFormaPagoDTO item : this.formasPago) {
			out += item.getMontoGs();
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalImporteGs() {
		double out = 0;
		for (GastoDetalleDTO det : detalles) {
			out = out + det.getImporteGs();
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalImporteDs() {
		double out = 0;
		for (GastoDetalleDTO det : detalles) {
			out = out + det.getImporteDs();
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIvaCalculado() {
		double out = 0;
		for (GastoDetalleDTO det : detalles) {
			out = out + det.getIvaCalculado();
		}
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva10() {
		double out = 0;
		
		for (GastoDetalleDTO det : detalles) {
			if (det.isIva10() == true)
				out += det.getIvaCalculado();			
		}		
		return out;
	}
	
	@DependsOn("detalles")
	public double getTotalIva5() {
		double out = 0;
		
		for (GastoDetalleDTO det : detalles) {
			if (det.isIva5() == true)
				out += det.getIvaCalculado();
		}		
		return out;
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isNotaCredito() { 
		boolean out = false;
		String sigla = this.getTipoMovimiento().getPos2() + "";		
		if (sigla.compareTo(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA) == 0) {
			out = true;
		}		
		return out;
	}
	
	@DependsOn("moneda")
	public boolean isMonedaLocal() {
		String sigla = (String) this.moneda.getPos2();
		return sigla.compareTo(Configuracion.SIGLA_MONEDA_GUARANI) == 0;
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isCredito() { 
		String sigla = (String) this.getTipoMovimiento().getPos2();					
		return sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isAutoFactura() {
		String sigla = (String) this.getTipoMovimiento().getPos2();					
		return sigla.equals(Configuracion.SIGLA_TM_AUTO_FACTURA);
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isBoletaVenta() {
		String sigla = (String) this.getTipoMovimiento().getPos2();					
		return sigla.equals(Configuracion.SIGLA_TM_BOLETA_VENTA);
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isGastoContado() {
		String sigla = (String) this.getTipoMovimiento().getPos2();					
		return sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO);
	}
	
	public boolean isAnulado() {
		String sigla = this.getEstadoComprobante().getSigla();
		return sigla.equals(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO);
	}
	
	public Date getFecha() {
		return fecha;
	}
	
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public String getNumeroFactura() {
		return numeroFactura;
	}
	
	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
	
	public String getNumeroTimbrado() {
		return numeroTimbrado;
	}

	public void setNumeroTimbrado(String numeroTimbrado) {
		this.numeroTimbrado = numeroTimbrado;
	}
	
	public double getTipoCambio() {
		return tipoCambio;
	}
	
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	
	public double getTotalAsignado() {
		return totalAsignado;
	}
	
	public void setTotalAsignado(double totalAsignado) {
		this.totalAsignado = totalAsignado;
	}

	public double getTotalIvaAsignado() {
		return totalIvaAsignado;
	}

	public void setTotalIvaAsignado(double totalIva) {
		this.totalIvaAsignado = totalIva;
	}
	
	public boolean isExisteComprobanteFisico() {
		return existeComprobanteFisico;
	}

	public void setExisteComprobanteFisico(boolean existeComprobanteFisico) {
		this.existeComprobanteFisico = existeComprobanteFisico;
	}

	public String getMotivoComprobanteFisico() {
		return motivoComprobanteFisico;
	}

	public void setMotivoComprobanteFisico(String motivoComprobanteFisico) {
		this.motivoComprobanteFisico = motivoComprobanteFisico;
	}

	public String getCajaPagoNumero() {
		return cajaPagoNumero;
	}

	public void setCajaPagoNumero(String cajaPagoNumero) {
		this.cajaPagoNumero = cajaPagoNumero;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	public MyArray getProveedor() {
		return proveedor;
	}

	public void setProveedor(MyArray proveedor) {
		this.proveedor = proveedor;
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

	public MyArray getTimbrado() {
		return timbrado;
	}

	public void setTimbrado(MyArray timbrado) {
		this.timbrado = timbrado;
	}

	public MyArray getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(MyArray condicionPago) {
		this.condicionPago = condicionPago;
	}

	public List<GastoDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<GastoDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public long getIdImportacion() {
		return idImportacion;
	}

	public void setIdImportacion(long idImportacion) {
		this.idImportacion = idImportacion;
	}

	public boolean isFondoFijo() {
		return fondoFijo;
	}

	public void setFondoFijo(boolean fondoFijo) {
		this.fondoFijo = fondoFijo;
	}

	public double getImporteGs() {
		return this.getTotalImporteGs();
	}

	public void setImporteGs(double importeGs) {
		this.importeGs = importeGs;
	}

	public double getImporteDs() {
		return this.getTotalImporteDs();
	}

	public void setImporteDs(double importeDs) {
		this.importeDs = importeDs;
	}

	public double getImporteIva10() {
		return this.getTotalIva10();
	}

	public void setImporteIva10(double importeIva) {
		this.importeIva10 = importeIva;
	}

	public double getImporteIva5() {
		return this.getTotalIva5();
	}

	public void setImporteIva5(double importeIva5) {
		this.importeIva5 = importeIva5;
	}

	public MyPair getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(MyPair estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public MyArray getTalonarioAutoFactura() {
		return talonarioAutoFactura;
	}

	public void setTalonarioAutoFactura(MyArray talonarioAutoFactura) {
		this.talonarioAutoFactura = talonarioAutoFactura;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario.toUpperCase();
	}

	public List<ReciboFormaPagoDTO> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(List<ReciboFormaPagoDTO> formasPago) {
		this.formasPago = formasPago;
	}

	public MyArray getImportacion() {
		return importacion;
	}

	public void setImportacion(MyArray importacion) {
		this.idImportacion = importacion.getId();
		this.numeroImportacion = (String) importacion.getPos1();
		this.despachante = (String) importacion.getPos2();
		this.importacion = importacion;
	}

	@DependsOn({ "importacion", "gastoImportacion" })
	public String getNumeroImportacion() {
		return numeroImportacion;
	}

	public void setNumeroImportacion(String numeroImportacion) {
		this.numeroImportacion = numeroImportacion;
	}

	@DependsOn({ "importacion", "gastoImportacion" })
	public String getDespachante() {
		return despachante;
	}

	public void setDespachante(String despachante) {
		this.despachante = despachante;
	}

	@DependsOn("idImportacion")
	public boolean isGastoImportacion() {
		if (this.idImportacion != -1) {
			return true;
		}
		return gastoImportacion;
	}

	public void setGastoImportacion(boolean gastoImportacion) {
		if (gastoImportacion == false) {
			this.idImportacion = -1;
			this.numeroImportacion = "";
			this.despachante = "";
		}
		this.gastoImportacion = gastoImportacion;
	}

	public boolean isDebitoBancario() {
		return debitoBancario;
	}

	public void setDebitoBancario(boolean debitoBancario) {
		this.debitoBancario = debitoBancario;
	}

	public boolean isNo_generar_saldo() {
		return no_generar_saldo;
	}

	public void setNo_generar_saldo(boolean no_generar_saldo) {
		this.no_generar_saldo = no_generar_saldo;
	}

	public MyArray getBanco() {
		return banco;
	}

	public void setBanco(MyArray banco) {
		this.banco = banco;
	}
}
