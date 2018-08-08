package com.yhaguy.gestion.caja.recibos;

import java.util.Date;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.gestion.bancos.cheques.BancoChequeDTO;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;

@SuppressWarnings("serial")
public class ReciboFormaPagoDTO extends DTO {
	
	private double montoGs = 0;
	private double montoDs = 0;
	
	/** el monto original del cheque **/
	private double montoChequeGs = 0;
	
	private String descripcion = "";
	
	private String tarjetaNumero = "";
	private String tarjetaNumeroComprobante = "";
	
	/** La cantidad de cuotas de la tarjeta **/
	private int tarjetaCuotas = 0;

	/** El tipo de pago: efectivo - cheque - tarjeta **/
	private MyPair tipo; 		
	
	/** El tipo de tarjeta: visa - mastercard - etc **/
	private MyPair tarjetaTipo = new MyPair();	
	
	/** La procesadora de tarjeta: bancard - procard - etc **/
	private MyArray tarjetaProcesadora = new MyArray();	
	
	/** La cuenta bancaria seleccionada **/
	private BancoCtaDTO bancoCta = new BancoCtaDTO();
	
	private Date chequeFecha;		
	private MyPair chequeBanco = new MyPair();
	private String chequeNumero = "";
	private String chequeLibrador = "";
	private String chequeBancoDescripcion = "";
	private MyPair moneda = new MyPair();
	
	private MyArray depositoBancoCta = new MyArray();
	private String depositoNroReferencia = "";
	
	/** cobros con retencion **/
	private String retencionNumero = "";
	private String retencionTimbrado = "";
	private Date retencionVencimiento;
	
	/** Pagos con debito cobranza central **/
	private String reciboDebitoNro = "";
	
	/** Cobros con saldo a favor cta cte **/
	private MyArray ctaCteSaldoFavor;	
	
	/**
	 * @return true si la forma de pago es cheque propio..
	 */
	public boolean isChequePropio() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO);
	}
	
	/**
	 * @return true si la forma de pago es cheque tercero..
	 */
	public boolean isChequeTercero() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO);
	}
	
	/**
	 * @return true si la forma de pago es cheque propio..
	 */
	public boolean isRetencionIVA() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_RETENCION);
	}
	
	/**
	 * @return true si la forma de pago es efectivo..
	 */
	public boolean isEfectivo() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_EFECTIVO);
	}
	
	/**
	 * @return true si la forma de pago es saldo a favor del cliente..
	 */
	public boolean isSaldoFavorGenerado() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_GENERADO);
	}
	
	/**
	 * @return true si la forma de pago es saldo a favor cobrado..
	 */
	public boolean isSaldoFavorCobrado() {
		String sigla = this.tipo.getSigla();
		return sigla.equals(Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_COBRADO);
	}
	
	@DependsOn({ "montoChequeGs", "montoGs" })
	public double getSaldoFavorCliente() {
		return this.montoChequeGs - this.montoGs;
	}
	
	/**
	 * @return true si es cheque al dia..
	 */
	public boolean isChequeAlDia(Date fecha) {
		int cmp = fecha.compareTo(this.chequeFecha);
		return cmp >= 0;
	}
	
	/**
	 * @return true si es cheque adelantado..
	 */
	public boolean isChequeAdelantado(Date fecha) {
		return (this.isChequeTercero()) && (this.isChequeAlDia(fecha) == false);
	}
	
	public void setChequePropio(BancoChequeDTO bch, String banco) {		
		this.setMontoGs(bch.getMonto());
		this.setChequeNumero(String.valueOf(bch.getNumero()));
		this.setChequeLibrador(bch.getBeneficiario());
		this.setChequeFecha(bch.getFechaVencimiento());
		this.setChequeBancoDescripcion(banco);
	}	
	
	public double getMontoGs() {
		return montoGs;
	}

	public void setMontoGs(double montoGs) {
		this.montoGs = montoGs;
	}

	public double getMontoDs() {
		return montoDs;
	}

	public void setMontoDs(double montoDs) {
		this.montoDs = this.getMisc().redondeoDosDecimales(montoDs);
	}

	public MyPair getTipo() {
		return tipo;
	}

	public void setTipo(MyPair tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return la descripcion de la forma de pago..
	 */
	public String getDescripcion() {
		if (this.isChequePropio())
			return this.chequeBancoDescripcion
					+ " - "
					+ this.chequeNumero
					+ " - Vto. "
					+ this.getMisc().dateToString(this.chequeFecha,
							Misc.DD_MM_YYYY);
		if(this.isRetencionIVA())
			return "RETENCIÓN IVA" + " - " + this.retencionNumero;		
		if(this.isEfectivo())
			return "EFECTIVO GS.";
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BancoCtaDTO getBancoCta() {
		return bancoCta;
	}

	public void setBancoCta(BancoCtaDTO bancoCta) {
		this.bancoCta = bancoCta;
	}

	public String getTarjetaNumero() {
		return tarjetaNumero;
	}

	public void setTarjetaNumero(String numeroTarjeta) {
		this.tarjetaNumero = numeroTarjeta;
	}

	public MyPair getTarjetaTipo() {
		return tarjetaTipo;
	}

	public void setTarjetaTipo(MyPair tipoTarjeta) {
		this.tarjetaTipo = tipoTarjeta;
	}

	public MyArray getTarjetaProcesadora() {
		return tarjetaProcesadora;
	}

	public void setTarjetaProcesadora(MyArray procesadoraTarjeta) {
		this.tarjetaProcesadora = procesadoraTarjeta;
	}

	public String getTarjetaNumeroComprobante() {
		return tarjetaNumeroComprobante;
	}

	public void setTarjetaNumeroComprobante(String nroComprobanteTarjeta) {
		this.tarjetaNumeroComprobante = nroComprobanteTarjeta;
	}

	public MyPair getMoneda() {
		return moneda;
	}

	public void setMoneda(MyPair moneda) {
		this.moneda = moneda;
	}

	public int getTarjetaCuotas() {
		return tarjetaCuotas;
	}

	public void setTarjetaCuotas(int tarjetaCuotas) {
		this.tarjetaCuotas = tarjetaCuotas;
	}

	public MyArray getDepositoBancoCta() {
		return depositoBancoCta;
	}

	public void setDepositoBancoCta(MyArray depositoBancoCta) {
		this.depositoBancoCta = depositoBancoCta;
	}

	public String getDepositoNroReferencia() {
		return depositoNroReferencia;
	}

	public void setDepositoNroReferencia(String depositoNroReferencia) {
		this.depositoNroReferencia = depositoNroReferencia;
	}

	public Date getChequeFecha() {
		return chequeFecha;
	}

	public void setChequeFecha(Date chequeFecha) {
		this.chequeFecha = chequeFecha;
	}

	public MyPair getChequeBanco() {
		return chequeBanco;
	}

	public void setChequeBanco(MyPair chequeBanco) {
		this.chequeBanco = chequeBanco;
	}

	public String getChequeNumero() {
		return chequeNumero;
	}

	public void setChequeNumero(String chequeNumero) {
		this.chequeNumero = chequeNumero;
	}

	public String getChequeLibrador() {
		return chequeLibrador;
	}

	public void setChequeLibrador(String chequeLibrador) {
		this.chequeLibrador = chequeLibrador;
	}

	public String getRetencionNumero() {
		return retencionNumero;
	}

	public void setRetencionNumero(String retencionNumero) {
		this.retencionNumero = retencionNumero;
	}

	public String getRetencionTimbrado() {
		return retencionTimbrado;
	}

	public void setRetencionTimbrado(String retencionTimbrado) {
		this.retencionTimbrado = retencionTimbrado;
	}

	public Date getRetencionVencimiento() {
		return retencionVencimiento;
	}
	
	public void setRetencionVencimiento(Date retencionVencimiento) {
		this.retencionVencimiento = retencionVencimiento;
	}

	public String getChequeBancoDescripcion() {
		return chequeBancoDescripcion;
	}

	public void setChequeBancoDescripcion(String chequeBancoDescripcion) {
		this.chequeBancoDescripcion = chequeBancoDescripcion;
	}

	public double getMontoChequeGs() {
		return montoChequeGs;
	}

	public void setMontoChequeGs(double montoChequeGs) {
		this.montoChequeGs = montoChequeGs;
		if (this.isChequeTercero()) {
			this.montoGs = montoChequeGs;
		}
	}

	public String getReciboDebitoNro() {
		return reciboDebitoNro;
	}

	public void setReciboDebitoNro(String reciboDebitoNro) {
		this.reciboDebitoNro = reciboDebitoNro;
	}

	public MyArray getCtaCteSaldoFavor() {
		return ctaCteSaldoFavor;
	}

	public void setCtaCteSaldoFavor(MyArray ctaCteSaldoFavor) {
		this.ctaCteSaldoFavor = ctaCteSaldoFavor;
		if (ctaCteSaldoFavor != null) {
			this.descripcion = this.tipo.getText() + " (" + ctaCteSaldoFavor.getPos2() + ")";
			this.montoGs = (double) ctaCteSaldoFavor.getPos3();
			BindUtils.postNotifyChange(null, null, this, "montoGs");
		}		
	}	
}
