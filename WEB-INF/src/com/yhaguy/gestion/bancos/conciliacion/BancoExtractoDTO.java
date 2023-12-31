package com.yhaguy.gestion.bancos.conciliacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;

import com.coreweb.dto.DTO;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;

@SuppressWarnings("serial")
public class BancoExtractoDTO extends DTO {
	
	private String numero = "";
	private Date desde;
	private Date hasta;
	private boolean cerrado;
	private double saldoInicial;
	private double totalDepositosAconfirmar;
	
	private List<BancoExtractoDetalleDTO> detalles2 = new ArrayList<BancoExtractoDetalleDTO>();
	private MyPair sucursal;
	private BancoCtaDTO banco;
	
	/**
	 * @return en un map los numeros de movimientos..
	 */
	public Map<String, String> getNumeros() {
		Map<String, String> out = new HashMap<String, String>();
		for (BancoExtractoDetalleDTO item : this.detalles2) {
			out.put(item.getNumero(), item.getNumero());
			if (!item.getAuxi().isEmpty()) {
				String[] auxis = item.getAuxi().split(";");
				for (int i = 0; i < auxis.length; i++) {
					out.put(auxis[i], auxis[i]);
				}
			}
		}
		return out;
	}
	
	@DependsOn("detalles2")
	public double getTotalDebe() {
		double out = 0;
		for (BancoExtractoDetalleDTO item : this.detalles2) {
			out += item.getDebe();
		}
		return out;
	}
	
	@DependsOn("detalles2")
	public double getTotalHaber() {
		double out = 0;
		for (BancoExtractoDetalleDTO item : this.detalles2) {
			out += item.getHaber();
		}
		return out;
	}
	
	@DependsOn({ "banco", "desde", "hasta" })
	public List<Object[]> getDepositosAconfirmar() throws Exception {
		this.totalDepositosAconfirmar = 0;
		if (this.banco != null && this.desde != null && this.hasta != null) {
			RegisterDomain rr = RegisterDomain.getInstance();
			List<Object[]> out = rr.getDepositosPorBancoAconfirmar(this.banco.getId(), this.desde, this.hasta);
			for (Object[] obj : out) {
				this.totalDepositosAconfirmar += ((double) obj[3]);
			}
			return out;
		}
		BindUtils.postNotifyChange(null, null, this, "totalDepositosAconfirmar");
		return new ArrayList<Object[]>();
	}
	
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public boolean isCerrado() {
		return cerrado;
	}

	public void setCerrado(boolean cerrado) {
		this.cerrado = cerrado;
	}

	public MyPair getSucursal() {
		return sucursal;
	}

	public void setSucursal(MyPair sucursal) {
		this.sucursal = sucursal;
	}

	public BancoCtaDTO getBanco() {
		return banco;
	}

	public void setBanco(BancoCtaDTO banco) {
		this.banco = banco;
	}

	public List<BancoExtractoDetalleDTO> getDetalles2() {
		return detalles2;
	}

	public void setDetalles2(List<BancoExtractoDetalleDTO> detalles2) {
		this.detalles2 = detalles2;
	}

	public double getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public double getTotalDepositosAconfirmar() {
		return totalDepositosAconfirmar;
	}

	public void setTotalDepositosAconfirmar(double totalDepositosAconfirmar) {
		this.totalDepositosAconfirmar = totalDepositosAconfirmar;
	}
}
