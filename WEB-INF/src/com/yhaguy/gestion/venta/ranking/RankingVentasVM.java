package com.yhaguy.gestion.venta.ranking;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

import com.coreweb.control.SimpleViewModel;
import com.yhaguy.domain.ArticuloFamilia;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.util.Utiles;

public class RankingVentasVM extends SimpleViewModel {
	
	private List<Object[]> rankingClientes;
	private List<Object[]> rankingArticulos;
	private List<Object[]> rankingClientesArticulos;
	
	private Date desde;
	private Date hasta;
	private Funcionario vendedor;
	private ArticuloFamilia familia;
	private Object[] cliente;
	
	private String filterRuc;
	private String filterRazonSocial;
	
	@Init(superclass = true)
	public void init() {
		this.rankingClientes = new ArrayList<>();
		this.rankingArticulos = new ArrayList<>();
		this.rankingClientesArticulos = new ArrayList<>();
		this.filterRuc = "";
		this.filterRazonSocial = "";
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
	
	@NotifyChange("rankingClientesArticulos")
	@Command
	public void loadRankingClientesArticulos() {
		if (this.desde == null || this.hasta == null) {
			Clients.showNotification("DEBE INGRESAR UN RANGO DE FECHA", Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
			return;
		}
		long idVendedor = this.vendedor != null ? this.vendedor.getId() : 0;
		long idFamilia = this.familia != null ? this.familia.getId() : 0;
		long idCliente = this.cliente != null ? (long) this.cliente[0] : 0;
		RegisterDomain rr = RegisterDomain.getInstance();
		this.rankingClientesArticulos = rr.getRankingClientesArticulos(this.desde, this.hasta, idVendedor, idFamilia, idCliente);
	}
	
	@NotifyChange("vendedor")
	@Command
	public void refreshVendedor() {
		this.vendedor = null;
	}
	
	@NotifyChange("cliente")
	@Command
	public void refreshCliente() {
		this.cliente = null;
	}
	
	@NotifyChange("familia")
	@Command
	public void refreshFamilia() {
		this.familia = null;
	}
	
	@NotifyChange({ "filterRazonSocial", "filterRuc" })
	@Command
	public void refreshFilter() {
	}
		
	@Command
	public void exportRankingClientes() throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Ranking Clientes");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("NUMERO");
		r.createCell(cell++).setCellValue("DENOMINACION");
		r.createCell(cell++).setCellValue("RUC");
		r.createCell(cell++).setCellValue("IMPORTE");
		
		for (Object[] item : this.rankingClientes) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue("" + (rowIndex - 1)); 
			row.createCell(cellIndex++).setCellValue(item[0] + "");
			row.createCell(cellIndex++).setCellValue(item[1] + ""); 
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) item[2]).replace(".", ""));
		}	
		listSheet.autoSizeColumn(0); listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2); listSheet.autoSizeColumn(3);
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("RankingClientes.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Command
	public void exportRankingArticulos() throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Ranking Articulos");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("NUMERO");
		r.createCell(cell++).setCellValue("CODIGO");
		r.createCell(cell++).setCellValue("DESCRIPCION");
		r.createCell(cell++).setCellValue("IMPORTE");
		
		for (Object[] item : this.rankingArticulos) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue("" + (rowIndex - 1)); 
			row.createCell(cellIndex++).setCellValue(item[0] + "");
			row.createCell(cellIndex++).setCellValue(item[1] + ""); 
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) item[2]).replace(".", ""));
		}	
		listSheet.autoSizeColumn(0); listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2); listSheet.autoSizeColumn(3);
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("RankingArticulos.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@Command
	public void exportRankingClientesArticulos() throws Exception {
		Workbook workbook = new HSSFWorkbook();
		Sheet listSheet = workbook.createSheet("Ranking Clientes Articulos");

		int rowIndex = 0;
		Row r = listSheet.createRow(rowIndex++);
		int cell = 0;
		r.createCell(cell++).setCellValue("CLIENTE");
		r.createCell(cell++).setCellValue("NUMERO");
		r.createCell(cell++).setCellValue("CODIGO");
		r.createCell(cell++).setCellValue("DESCRIPCION");
		r.createCell(cell++).setCellValue("IMPORTE");
		
		for (Object[] item : this.rankingClientesArticulos) {
			Row row = listSheet.createRow(rowIndex++);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(item[0] + "");
			row.createCell(cellIndex++).setCellValue("" + (rowIndex - 1)); 
			row.createCell(cellIndex++).setCellValue(item[1] + "");
			row.createCell(cellIndex++).setCellValue(item[2] + ""); 
			row.createCell(cellIndex++).setCellValue(Utiles.getNumberFormat((double) item[3]).replace(".", ""));
		}	
		listSheet.autoSizeColumn(0); listSheet.autoSizeColumn(1);
		listSheet.autoSizeColumn(2); listSheet.autoSizeColumn(3);
		listSheet.autoSizeColumn(4);
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			AMedia amedia = new AMedia("RankingClientesArticulos.xls", "xls", "application/file", baos.toByteArray());
			Filedownload.save(amedia);
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Funcionario> getVendedores() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getVendedores("");
	}
	
	@DependsOn({ "filterRazonSocial", "filterRuc" })
	public List<Object[]> getClientes() throws Exception {
		if (this.filterRazonSocial.trim().isEmpty() && this.filterRuc.trim().isEmpty()) {
			return new ArrayList<>();
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getClientesByRazonSocial(this.filterRazonSocial, this.filterRuc);
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

	public List<Object[]> getRankingClientesArticulos() {
		return rankingClientesArticulos;
	}

	public void setRankingClientesArticulos(List<Object[]> rankingClientesArticulos) {
		this.rankingClientesArticulos = rankingClientesArticulos;
	}

	public String getFilterRuc() {
		return filterRuc;
	}

	public void setFilterRuc(String filterRuc) {
		this.filterRuc = filterRuc;
	}

	public String getFilterRazonSocial() {
		return filterRazonSocial;
	}

	public void setFilterRazonSocial(String filterRazonSocial) {
		this.filterRazonSocial = filterRazonSocial;
	}

	public Object[] getCliente() {
		return cliente;
	}

	public void setCliente(Object[] cliente) {
		this.cliente = cliente;
	}	

}
