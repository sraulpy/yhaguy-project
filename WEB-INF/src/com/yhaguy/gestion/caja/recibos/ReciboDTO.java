package com.yhaguy.gestion.caja.recibos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.gestion.contabilidad.retencion.RetencionIvaDTO;
import com.yhaguy.gestion.empresa.AssemblerFuncionario;
import com.yhaguy.gestion.empresa.FuncionarioDTO;
import com.yhaguy.util.Utiles;

@SuppressWarnings("serial")
public class ReciboDTO extends DTO {

	private String numero = "";
	private long nro = 0;
	private long idUsuarioCarga = 0;
	private String nombreUsuarioCarga = "";
	private Date fechaEmision = new Date();
	private double tipoCambio = 0;
	private String motivoAnulacion = "";
	private String tesaka = "";
	private String numeroPlanilla = "";
	private String numeroRecibo = "";
	private String cobrador = "";
	private Date fechaRecibo;

	private boolean movimientoBancoActualizado = false;
	
	/** Para cobros a otra sucursal **/
	private boolean cobroExterno = false;
	
	private boolean entregado = false;

	private MyPair estadoComprobante = new MyPair();
	private MyArray tipoMovimiento;
	private MyArray proveedor = new MyArray(); 
	private MyArray cliente = new MyArray(); 
	private MyArray moneda; 
	private MyPair sucursal;

	private List<ReciboDetalleDTO> detalles = new ArrayList<ReciboDetalleDTO>();
	private List<ReciboFormaPagoDTO> formasPago = new ArrayList<ReciboFormaPagoDTO>();

	private double totalImporteGs = 0;
	private double totalImporteDs = 0;

	private String tituloDetalles = "";
	private String labelEmpresa = "";
	private boolean anticipo = false;
	private boolean imputar = false; 

	/** los estados de comprobantes **/
	private List<MyPair> estadosComprobantes;

	private RetencionIvaDTO retencion;

	public boolean isDetalleVacio() {
		return this.detalles.size() == 0;
	}

	public void setPendienteCobro(boolean pendiente) {

		String sigla = pendiente == true ? Configuracion.SIGLA_ESTADO_COMPROBANTE_PENDIENTE
				: Configuracion.SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO;

		for (MyPair estado : this.estadosComprobantes) {
			if (estado.getSigla().compareTo(sigla) == 0) {
				this.setEstadoComprobante(estado);
			}
		}
	}

	public boolean isPendienteCobro() {
		return this.estadoComprobante.getSigla().compareTo(
				Configuracion.SIGLA_ESTADO_COMPROBANTE_PENDIENTE) == 0;
	}
	
	/**
	 * @return el importe total sin iva..
	 */
	public double getTotalImporteGsSinIva() {
		return this.getTotalImporteGs() - Utiles.getIVA(this.getTotalImporteGs(), Configuracion.VALOR_IVA_10);
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isAnticipoPago() { 
		String sigla = this.getTipoMovimiento().getPos2() + "";		
		if (sigla.equals(Configuracion.SIGLA_TM_ANTICIPO_PAGO)) {
			return true;
		}		
		return false;
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isAnticipoCobro() { 
		String sigla = this.getTipoMovimiento().getPos2() + "";		
		if (sigla.equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO)) {
			return true;
		}		
		return false;
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isCobro() {
		if(this.tipoMovimiento == null) return false;
		String sigla = (String) this.getTipoMovimiento().getPos2();
		boolean cobro = sigla.equals(Configuracion.SIGLA_TM_RECIBO_COBRO);
		boolean anticipoCobro = sigla
				.equals(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
		return cobro || anticipoCobro;

	}
	
	@DependsOn("tipoMovimiento")
	public boolean isCancelacionChequeRechazado() {
		String sigla = (String) this.getTipoMovimiento().getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_CANCELACION_CHEQ_RECHAZADO);

	}
	
	@DependsOn("tipoMovimiento")
	public boolean isReembolsoPrestamo() {
		String sigla = (String) this.getTipoMovimiento().getPos2();
		return sigla.equals(Configuracion.SIGLA_TM_REEMBOLSO_PRESTAMO);
	}
	
	@DependsOn("tipoMovimiento")
	public boolean isOrdenPago() {
		String sigla = (String) this.getTipoMovimiento().getPos2();
		boolean pago = sigla.equals(Configuracion.SIGLA_TM_RECIBO_PAGO);
		return pago;

	}
	
	@DependsOn("moneda")
	public boolean isMonedaLocal() {
		String sigla = (String) this.getMoneda().getPos2();
		return sigla.equals(Configuracion.SIGLA_MONEDA_GUARANI);
	}
	
	/**
	 * @return true si es orden de pago de gastos contado (puede incluir gasto credito)..
	 */
	public boolean isOrdenPagoGastosContado() {
		for (ReciboDetalleDTO det : this.detalles) {
			if (det.getMovimiento() != null) {
				String sigla = (String) det.getMovimiento().getTipoMovimiento().getPos2();
				if (sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return true si es orden de pago de gastos contado (solo incluye gastos contado)..
	 */
	public boolean isOrdenPagoGastosContado_() {
		boolean out = false;
		for (ReciboDetalleDTO det : this.detalles) {
			if (det.getMovimiento() != null) {
				String sigla = (String) det.getMovimiento().getTipoMovimiento().getPos2();
				if (sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO)) {
					out = true;
				}
				if (sigla.equals(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO)) {
					return false;
				}
			}
		}
		return out;
	}
	
	/**
	 * @return el vendedor..
	 */
	public FuncionarioDTO getVendedor() throws Exception {
		if (this.getDetalles().size() == 0 || !this.isCobro()) {
			return null;
		}
		long idVendedor = this.getDetalles().get(0).getMovimiento().getIdVendedor();
		return (FuncionarioDTO) new AssemblerFuncionario().getDTObyId(Funcionario.class.getName(), idVendedor);
	}
	
	/**
	 * @return la razon social del vendedor..
	 */
	public String getVendedorRazonSocial() throws Exception {
		FuncionarioDTO vendedor = this.getVendedor();
		return vendedor == null ? "" : vendedor.getEmpresa().getRazonSocial();
	}

	/**
	 * Suma todos los cheques propios
	 * 
	 * @return
	 */
	public double getTotalImporteChequePropio() {
		double out = this
				.getTotalImporteReciboFormaPago(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO);
		return out;
	}

	/**
	 * Suma todos los importes en efectivo
	 * 
	 * @return
	 */
	public double getTotalEfectivo() {
		double out = this
				.getTotalImporteReciboFormaPago(Configuracion.SIGLA_FORMA_PAGO_EFECTIVO);
		return out;
	}

	/**
	 * Suma todos depositos
	 * 
	 * @return
	 */
	public double getTotalImporteDepositoBanco() {
		double out = this
				.getTotalImporteReciboFormaPago(Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO);
		return out;
	}

	/**
	 * Suma todas los cobros con retenciones
	 * 
	 * @return
	 */
	public double getTotalImporteCobroConRetencion() {
		double out = this
				.getTotalImporteReciboFormaPago(Configuracion.SIGLA_FORMA_PAGO_RETENCION);
		return out;
	}

	/**
	 * Suma los importes de un tipo en particular
	 * 
	 * @param tipoFormaPago
	 * @return
	 */
	private double getTotalImporteReciboFormaPago(String tipoFormaPago) {
		double out = 0.0;
		List<ReciboFormaPagoDTO> lfp = this.getFormasPago();
		for (int i = 0; i < lfp.size(); i++) {
			ReciboFormaPagoDTO fp = lfp.get(i);
			if (fp.getTipo().getSigla().compareTo(tipoFormaPago) == 0) {
				out += fp.getMontoGs();
			}
		}
		return out;
	}
	
	/**
	 * @return el total de saldo a favor del cliente
	 */
	@DependsOn("formasPago")
	public double getTotalSaldoFavor() {
		double out = 0;
		for (ReciboFormaPagoDTO item : this.formasPago) {
			if (item.isChequeTercero()) {
				out += item.getSaldoFavorCliente();
			}
		}
		return out;
	}
	
	/**
	 * @return la razon social..
	 */
	public String getRazonSocial() {
		return (String) (this.isCobro() ? this.cliente.getPos2()
				: this.proveedor.getPos2());
	}
	
	/**
	 * @return el ruc..
	 */
	public String getRuc() {
		return (String) (this.isCobro() ? this.cliente.getPos3()
				: this.proveedor.getPos3());
	}
	
	/**
	 * @return el importe en letras..
	 */
	public String getImporteEnLetras() {
		return getMisc().numberToLetter(this.getTotalImporteGs());
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
		if (this.isCobro() && numero.contains("-")) {
			this.nro = Long.parseLong(numero.substring(numero.lastIndexOf("-") + 1, numero.length()));
		} else {
			try {
				this.nro = Long.parseLong(numero);
			} catch (Exception e) {
				System.out.println("formato incorrecto de nro recibo: " + numero);
			}			
		}		
	}

	public long getIdUsuarioCarga() {
		return idUsuarioCarga;
	}

	public void setIdUsuarioCarga(long idUsuarioCarga) {
		this.idUsuarioCarga = idUsuarioCarga;
	}

	public String getNombreUsuarioCarga() {
		return nombreUsuarioCarga;
	}

	public void setNombreUsuarioCarga(String nombreUsuarioCarga) {
		this.nombreUsuarioCarga = nombreUsuarioCarga;
	}

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public double getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public MyArray getProveedor() {
		return proveedor;
	}

	public void setProveedor(MyArray proveedor) {
		this.proveedor = proveedor;
		this.detalles = new ArrayList<ReciboDetalleDTO>();
		this.formasPago = new ArrayList<ReciboFormaPagoDTO>();
	}

	public MyArray getMoneda() {
		return moneda;
	}

	public void setMoneda(MyArray moneda) {
		this.moneda = moneda;
	}

	public List<ReciboDetalleDTO> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<ReciboDetalleDTO> detalles) {
		this.detalles = detalles;
	}

	public List<ReciboFormaPagoDTO> getFormasPago() {
		return formasPago;
	}

	public void setFormasPago(List<ReciboFormaPagoDTO> formasPago) {
		this.formasPago = formasPago;
	}

	public double getTotalImporteGs() {
		return totalImporteGs;
	}

	public void setTotalImporteGs(double totalPagoGs) {
		this.totalImporteGs = this.getMisc().redondeoDosDecimales(totalPagoGs);
	}

	public double getTotalImporteDs() {
		return totalImporteDs;
	}

	public void setTotalImporteDs(double totalPagoDs) {
		this.totalImporteDs = this.getMisc().redondeoDosDecimales(totalPagoDs);
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public MyArray getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(MyArray tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public String getTituloDetalles() {
		return tituloDetalles;
	}

	public void setTituloDetalles(String tituloDetalles) {
		this.tituloDetalles = tituloDetalles;
	}

	public String getLabelEmpresa() {
		return labelEmpresa;
	}

	public void setLabelEmpresa(String labelEmpresa) {
		this.labelEmpresa = labelEmpresa;
	}

	public MyArray getCliente() {
		return cliente;
	}

	public void setCliente(MyArray cliente) {
		this.cliente = cliente;
	}

	public boolean isImputar() {
		return imputar;
	}

	public void setImputar(boolean imputar) {
		this.imputar = imputar;
	}

	@DependsOn("tipoMovimiento")
	public boolean isAnticipo() {
		if(this.isAnticipoPago() || this.isAnticipoCobro())
			return true;
		return anticipo;
	}

	public void setAnticipo(boolean anticipo) {
		this.anticipo = anticipo;
	}

	public boolean isMovimientoBancoActualizado() {
		return movimientoBancoActualizado;
	}

	public void setMovimientoBancoActualizado(boolean movimientoBancoActualizado) {
		this.movimientoBancoActualizado = movimientoBancoActualizado;
	}

	public MyPair getEstadoComprobante() {
		return estadoComprobante;
	}

	public void setEstadoComprobante(MyPair estadoComprobante) {
		this.estadoComprobante = estadoComprobante;
	}

	public List<MyPair> getEstadosComprobantes() {
		return estadosComprobantes;
	}

	public void setEstadosComprobantes(List<MyPair> estadosComprobantes) {
		this.estadosComprobantes = estadosComprobantes;
	}

	public RetencionIvaDTO getRetencion() {
		return retencion;
	}

	public void setRetencion(RetencionIvaDTO retencion) {
		this.retencion = retencion;
	}

	public String getMotivoAnulacion() {
		return motivoAnulacion;
	}

	public void setMotivoAnulacion(String motivoAnulacion) {
		this.motivoAnulacion = motivoAnulacion;
	}

	public boolean isCobroExterno() {
		return cobroExterno;
	}

	public void setCobroExterno(boolean cobroExterno) {
		this.cobroExterno = cobroExterno;
	}

	public String getTesaka() {
		return tesaka;
	}

	public void setTesaka(String tesaka) {
		this.tesaka = tesaka;
	}

	public String getNumeroPlanilla() {
		return numeroPlanilla;
	}

	public void setNumeroPlanilla(String numeroPlanilla) {
		this.numeroPlanilla = numeroPlanilla;
	}

	public boolean isEntregado() {
		return entregado;
	}

	public void setEntregado(boolean entregado) {
		this.entregado = entregado;
	}

	public String getNumeroRecibo() {
		return numeroRecibo;
	}

	public void setNumeroRecibo(String numeroRecibo) {
		this.numeroRecibo = numeroRecibo;
	}

	public Date getFechaRecibo() {
		return fechaRecibo;
	}

	public void setFechaRecibo(Date fechaRecibo) {
		this.fechaRecibo = fechaRecibo;
	}

	public long getNro() {
		return nro;
	}

	public void setNro(long nro) {
		this.nro = nro;
	}

	public String getCobrador() {
		return cobrador;
	}

	public void setCobrador(String cobrador) {
		this.cobrador = cobrador;
	}
}
