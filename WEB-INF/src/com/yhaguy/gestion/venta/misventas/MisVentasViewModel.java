package com.yhaguy.gestion.venta.misventas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Venta;
import com.yhaguy.inicio.AccesoDTO;

@SuppressWarnings("deprecation")
public class MisVentasViewModel extends SimpleViewModel {
	
	static final long ID_FUNCIONARIO_NOELIA = 1;
	
	private Date fechaDesde = new Date();
	private Date fechaHasta = new Date();
	
	private double totalContado = 0;
	private double totalCredito = 0;
	private double totalNotaCredito = 0;
	
	@Init
	public void init() {
		this.setFecha();
	}
	
	/**
	 * @return el acceso..
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}	
	
	/**
	 * @return las ventas contado..
	 */
	@DependsOn({ "fechaDesde", "fechaHasta" })
	public List<MyArray> getVentasContado() throws Exception {
		this.totalContado = 0;
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		
		List<Venta> ventas = null;
		if (this.getIdFuncionario() == ID_FUNCIONARIO_NOELIA) {
			ventas = rr.getVentasContado(this.fechaDesde, this.fechaHasta, 0);
		} else {
			ventas = rr.getVentasContadoPorVendedor(this.fechaDesde,
					this.fechaHasta, this.getIdFuncionario());
		}

		for (Venta venta : ventas) {
			if (venta.isAnulado() == false) {
				MyArray my = new MyArray();
				my.setPos1(venta.getNumero().substring(8,
						venta.getNumero().length()));
				my.setPos2(venta.getCliente().getRazonSocial());
				my.setPos3(venta.getTotalImporteGs());
				out.add(my);
				this.totalContado += venta.getTotalImporteGs();
			}
		}
		BindUtils.postNotifyChange(null, null, this, "totalContado");
		return out;
	}
	
	/**
	 * @return las ventas contado..
	 */
	@DependsOn({ "fechaDesde", "fechaHasta" })
	public List<MyArray> getVentasCredito() throws Exception {
		this.totalCredito = 0;
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		
		List<Venta> ventas = null;
		if (this.getIdFuncionario() == ID_FUNCIONARIO_NOELIA) {
			ventas = rr.getVentasCredito(this.fechaDesde, this.fechaHasta, 0);
		} else {
			ventas = rr.getVentasCreditoPorVendedor(this.fechaDesde,
					this.fechaHasta, this.getIdFuncionario());
		}
		for (Venta venta : ventas) {
			if (venta.isAnulado() == false) {
				MyArray my = new MyArray();
				my.setPos1(venta.getNumero().substring(8,
						venta.getNumero().length()));
				my.setPos2(venta.getCliente().getRazonSocial());
				my.setPos3(venta.getTotalImporteGs());
				out.add(my);
				this.totalCredito += venta.getTotalImporteGs();
			}
		}
		BindUtils.postNotifyChange(null, null, this, "totalCredito");
		return out;
	}
	
	/**
	 * @return las notas de credito..
	 */
	@DependsOn({ "fechaDesde", "fechaHasta" })
	public List<MyArray> getNotasCredito() throws Exception {
		this.totalNotaCredito = 0;
		long idVendedor = this.getIdFuncionario();
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<NotaCredito> ncs = rr.getNotasCreditoVenta(this.fechaDesde,
				this.fechaHasta, 0);
		for (NotaCredito nc : ncs) {
			if (nc.isAnulado() == false
					&& this.getIdFuncionario() == ID_FUNCIONARIO_NOELIA) {
				MyArray my = new MyArray();
				my.setPos1(nc.getNumero().substring(8, nc.getNumero().length()));
				my.setPos2(nc.getCliente().getRazonSocial());
				my.setPos3(nc.getImporteGs());
				out.add(my);
				this.totalNotaCredito += nc.getImporteGs();
			} else if (((nc.isAnulado() == false) && (nc.getVendedor().getId()
					.longValue() == idVendedor))) {
				MyArray my = new MyArray();
				my.setPos1(nc.getNumero().substring(8, nc.getNumero().length()));
				my.setPos2(nc.getCliente().getRazonSocial());
				my.setPos3(nc.getImporteGs());
				out.add(my);
				this.totalNotaCredito += nc.getImporteGs();
			}

		}
		BindUtils.postNotifyChange(null, null, this, "totalNotaCredito");
		return out;
	}
	
	/**
	 * GET / SET
	 */
	private void setFecha() {
		Calendar c = Calendar.getInstance(); 
	    c.set(Calendar.DAY_OF_MONTH, 1);
	    this.setFechaDesde(c.getTime());
	}
	
	@DependsOn({ "totalContado", "totalCredito", "totalNotaCredito" })
	public double getTotal() {
		return this.totalContado + this.totalCredito - this.totalNotaCredito;
	}
	
	/**
	 * @return el id del funcionario..
	 */
	private long getIdFuncionario() {
		return this.getAcceso().getFuncionario().getId().longValue();
	}
	
	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
		if (fechaDesde != null) {
			this.fechaDesde.setHours(00);
			this.fechaDesde.setMinutes(00);
			this.fechaDesde.setSeconds(00);
		}
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;

		if (fechaHasta != null) {
			this.fechaHasta.setHours(23);
			this.fechaHasta.setMinutes(59);
			this.fechaHasta.setSeconds(59);
		}
	}

	public double getTotalContado() {
		return totalContado;
	}

	public void setTotalContado(double totalContado) {
		this.totalContado = totalContado;
	}

	public double getTotalCredito() {
		return totalCredito;
	}

	public void setTotalCredito(double totalCredito) {
		this.totalCredito = totalCredito;
	}

	public double getTotalNotaCredito() {
		return totalNotaCredito;
	}

	public void setTotalNotaCredito(double totalNotaCredito) {
		this.totalNotaCredito = totalNotaCredito;
	}
}
