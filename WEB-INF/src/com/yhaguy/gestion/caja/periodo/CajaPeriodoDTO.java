package com.yhaguy.gestion.caja.periodo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.gestion.caja.principal.CajaDTO;
import com.yhaguy.gestion.caja.recibos.ReciboDTO;
import com.yhaguy.gestion.compras.gastos.subdiario.GastoDTO;
import com.yhaguy.gestion.notacredito.NotaCreditoDTO;
import com.yhaguy.gestion.venta.VentaDTO;

@SuppressWarnings("serial")
public class CajaPeriodoDTO extends DTO {

	private String numero = "";
	private Date apertura = new Date();
	private Date cierre;
	private String tipo = "";
	
	private MyArray verificador = new MyArray();
	private MyArray responsable = new MyArray();
	private MyArray funcionario = new MyArray();
	private MyArray arqueo = new MyArray();

	/** Estado de la planilla: abierta - cerrada **/
	private MyPair estado = new MyPair();

	/** La Caja Padre al cual Pertenece la planilla **/
	private CajaDTO caja = new CajaDTO();

	/** Los recibos de Pago y de Cobro **/
	private List<ReciboDTO> recibos = new ArrayList<ReciboDTO>();

	/** Las ventas dentro de la Caja.. **/
	private List<MyArray> ventas = new ArrayList<MyArray>();

	/** Las ventas generadas que debe imputarse en Cta Cte.. **/
	private List<VentaDTO> ventas_a_imputar = new ArrayList<VentaDTO>();

	/** Las Notas de Credito dentro de la Caja **/
	private List<MyArray> notasCredito = new ArrayList<MyArray>();

	/** La Nota de Credito generada que debe imputarse en Cta Cte.. **/
	private NotaCreditoDTO notaCredito_a_imputar = new NotaCreditoDTO();

	/** Las reposiciones hechas a la Caja (Ingreso para Fondo de la Caja) **/
	private List<MyArray> reposiciones = new ArrayList<MyArray>();

	/**
	 * pos1: tipo - pos2: fecha - pos3: descripcion - pos4: importe Ds - importe
	 * Gs - pos5: boolean guardado
	 **/
	private List<MyArray> detalles = new ArrayList<MyArray>();

	/** Los gastos de Fondo Fijo **/
	private List<GastoDTO> gastos = new ArrayList<GastoDTO>();

	public String getStringResumen() {
		String out = "";
		out += this.numero;
		out += " [" + this.apertura + "-"
				+ ((this.cierre != null) ? this.cierre : " abierta ") + "]";
		out += " " + this.responsable.getPos1();
		return out;
	}
	
	public List<MyArray> getDetallesOrdenado() {
		return this.detalles;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getApertura() {
		return apertura;
	}

	public void setApertura(Date apertura) {
		this.apertura = apertura;
	}

	public Date getCierre() {
		return cierre;
	}

	public void setCierre(Date cierre) {
		this.cierre = cierre;
	}

	public MyArray getVerificador() {
		return verificador;
	}

	public void setVerificador(MyArray verificador) {
		this.verificador = verificador;
	}

	public MyArray getResponsable() {
		return responsable;
	}

	public void setResponsable(MyArray responsable) {
		this.responsable = responsable;
	}

	public List<ReciboDTO> getRecibos() {
		return recibos;
	}

	public void setRecibos(List<ReciboDTO> pagos) {
		this.recibos = pagos;
	}

	public List<MyArray> getReposiciones() {
		return reposiciones;
	}

	public void setReposiciones(List<MyArray> reposiciones) {
		this.reposiciones = reposiciones;
	}

	public List<MyArray> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<MyArray> detalles) {
		this.detalles = detalles;
	}

	public MyArray getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(MyArray funcionario) {
		this.funcionario = funcionario;
	}

	public String toString() {
		return this.numero;
	}

	public List<MyArray> getVentas() {
		return ventas;
	}

	public void setVentas(List<MyArray> ventas) {
		this.ventas = ventas;
	}

	public List<VentaDTO> getVentas_a_imputar() {
		return ventas_a_imputar;
	}

	public void setVentas_a_imputar(List<VentaDTO> ventas_a_imputar) {
		this.ventas_a_imputar = ventas_a_imputar;
	}

	public List<MyArray> getNotasCredito() {
		return notasCredito;
	}

	public void setNotasCredito(List<MyArray> notasCredito) {
		this.notasCredito = notasCredito;
	}

	public NotaCreditoDTO getNotaCredito_a_imputar() {
		return notaCredito_a_imputar;
	}

	public void setNotaCredito_a_imputar(NotaCreditoDTO notaCredito_a_imputar) {
		this.notaCredito_a_imputar = notaCredito_a_imputar;
	}

	public CajaDTO getCaja() {
		return caja;
	}

	public void setCaja(CajaDTO caja) {
		this.caja = caja;
	}

	public List<GastoDTO> getGastos() {
		return gastos;
	}

	public void setGastos(List<GastoDTO> gastos) {
		this.gastos = gastos;
	}

	public MyPair getEstado() {
		return estado;
	}

	public void setEstado(MyPair estado) {
		this.estado = estado;
	}

	public MyArray getArqueo() {
		return arqueo;
	}

	public void setArqueo(MyArray arqueo) {
		this.arqueo = arqueo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}


class OrdenarDetalles implements Comparator<MyArray>{
	/**
	 * Ordena por Tipo y luego por Fecha
	 */
	
	// pos1: tipo - pos2: fecha
	
	@Override
	public int compare(MyArray o1, MyArray o2) {
		String numero1 = ((String) o1.getPos14()).replace("-", "");
		String numero2 = ((String) o1.getPos14()).replace("-", "");
		Integer tipo1 = Integer.valueOf(numero1);
		Integer tipo2 = Integer.valueOf(numero2);
		
		int out = 0;
		out = tipo1.compareTo(tipo2);
		
		return out;
	}
	
}



