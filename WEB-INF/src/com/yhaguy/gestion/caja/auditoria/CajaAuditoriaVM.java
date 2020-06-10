package com.yhaguy.gestion.caja.auditoria;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.CajaAuditoria;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class CajaAuditoriaVM extends SimpleViewModel {
	
	private Date filterDesde;
	private Date filterHasta;
	
	private String filterResumen = "";
	private String filterConcepto = "";
	private String filterNumero = "";
	
	private double totalDebe = 0;
	private double totalHaber = 0;
	private double totalSaldo = 0;

	@Init(superclass = true)
	public void init() {
		try {
			this.filterDesde = Utiles.getFechaInicioMes();
			this.filterHasta = new Date();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/**
	 * GETS / SETS
	 */
	
	@DependsOn({ "filterDesde", "filterHasta", "filterResumen", "filterConcepto", "filterNumero" })
	public List<CajaAuditoria> getCajaAuditorias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<CajaAuditoria> out = rr.getCajaAuditorias(this.filterDesde, this.filterHasta, this.filterResumen,
				this.filterConcepto, this.filterNumero);
		// ordena la lista segun fecha..
		Collections.sort(out, new Comparator<CajaAuditoria>() {
			@Override
			public int compare(CajaAuditoria o1, CajaAuditoria o2) {
				Date fecha1 = (Date) o1.getFecha();
				Date fecha2 = (Date) o2.getFecha();
				return fecha1.compareTo(fecha2);
			}
		});
		this.totalDebe = 0;
		this.totalHaber = 0;
		this.totalSaldo = 0;
		for (CajaAuditoria aud : out) {
			this.totalDebe += aud.getDebe();
			this.totalHaber += aud.getHaber();
			this.totalSaldo += aud.getSaldo();
		}
		BindUtils.postNotifyChange(null, null, this, "totalDebe");
		BindUtils.postNotifyChange(null, null, this, "totalHaber");
		BindUtils.postNotifyChange(null, null, this, "totalSaldo");
		return out;
	}

	public Date getFilterDesde() {
		return filterDesde;
	}

	public void setFilterDesde(Date filterDesde) {
		this.filterDesde = filterDesde;
	}

	public Date getFilterHasta() {
		return filterHasta;
	}

	public void setFilterHasta(Date filterHasta) {
		this.filterHasta = filterHasta;
	}

	public double getTotalDebe() {
		return totalDebe;
	}

	public void setTotalDebe(double totalDebe) {
		this.totalDebe = totalDebe;
	}

	public double getTotalHaber() {
		return totalHaber;
	}

	public void setTotalHaber(double totalHaber) {
		this.totalHaber = totalHaber;
	}

	public double getTotalSaldo() {
		return totalSaldo;
	}

	public void setTotalSaldo(double totalSaldo) {
		this.totalSaldo = totalSaldo;
	}

	public String getFilterResumen() {
		return filterResumen;
	}

	public void setFilterResumen(String filterResumen) {
		this.filterResumen = filterResumen;
	}

	public String getFilterConcepto() {
		return filterConcepto;
	}

	public void setFilterConcepto(String filterConcepto) {
		this.filterConcepto = filterConcepto;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}
}