package com.yhaguy.gestion.venta.ranking;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;

public class RankingVentasVM extends SimpleViewModel {
	
	private List<Object[]> rankingClientes;
	private List<Object[]> rankingArticulos;
	
	private Date desde;
	private Date hasta;
	private Funcionario vendedor;
	private ArticuloFamilia familia;
	
	@Init(superclass = true)
	public void init() {
		this.rankingClientes = new ArrayList<>();
		this.rankingArticulos = new ArrayList<>();
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@NotifyChange("rankingClientes")
	@Command
	public void loadRankingClientes() {
		if (this.desde == null || this.hasta == null) {
			Clients.showNotification("DEBE INGRESAR UN RANGO DE FECHA", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		long idVendedor = this.vendedor != null ? this.vendedor.getId() : 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		this.rankingClientes = rr.getRankingClientes(this.desde, this.hasta, idVendedor);
	}
	
	@NotifyChange("rankingArticulos")
	@Command
	public void loadRankingArticulos() {
		if (this.desde == null || this.hasta == null) {
			Clients.showNotification("DEBE INGRESAR UN RANGO DE FECHA", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		long idVendedor = this.vendedor != null ? this.vendedor.getId() : 0;
		long idFamilia = this.familia != null ? this.familia.getId() : 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		this.rankingArticulos = rr.getRankingArticulos(this.desde, this.hasta, idVendedor, idFamilia);
	}
	
	@NotifyChange("vendedor")
	@Command
	public void refreshVendedor() {
		this.vendedor = null;
	}
	
	@NotifyChange("familia")
	@Command
	public void refreshFamilia() {
		this.familia = null;
	}
	
	public List<Funcionario> getVendedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVendedores();
	}
	
	@SuppressWarnings("unchecked")
	public List<ArticuloFamilia> getFamilias() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getObjects(ArticuloFamilia.class.getName());
	}
	
	public List<Object[]> getRankingClientes() {
		return rankingClientes;
	}

	public void setRankingClientes(List<Object[]> rankingClientes) {
		this.rankingClientes = rankingClientes;
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

	public Funcionario getVendedor() {
		return vendedor;
	}

	public void setVendedor(Funcionario vendedor) {
		this.vendedor = vendedor;
	}

	public List<Object[]> getRankingArticulos() {
		return rankingArticulos;
	}

	public void setRankingArticulos(List<Object[]> rankingArticulos) {
		this.rankingArticulos = rankingArticulos;
	}

	public ArticuloFamilia getFamilia() {
		return familia;
	}

	public void setFamilia(ArticuloFamilia familia) {
		this.familia = familia;
	}	

}
