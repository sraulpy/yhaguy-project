package com.yhaguy.gestion.anulaciones;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.componente.BuscarElemento;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.AjusteStock;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Venta;
import com.yhaguy.gestion.comun.ControlAnulacionMovimientos;
import com.yhaguy.gestion.stock.ajustes.AjusteStockAssembler;
import com.yhaguy.gestion.stock.ajustes.AjusteStockDTO;

public class AnulacionesViewModel extends SimpleViewModel {
	
	private MyPair selectedItem;
	private MyArray selectedMovimiento= new MyArray();
	
	private List<MyPair> tiposDeMovimientos;
	
	private String filterDesc = "";

	@Init(superclass = true)
	public void init() {
		this.selectedMovimiento.setPos5(new Date());
		this.selectedMovimiento.setPos6(false);
		this.setTiposDeMovimientos();
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	/******************* COMANDOS *******************/
	
	@Command
	@NotifyChange("*")
	public void buscarMovimientos(@BindingParam("posFiltro") int posFiltro) throws Exception {
		String filtro = "";
		
		switch (posFiltro) {
		case 0:
			filtro = (String) this.selectedMovimiento.getPos1();
			break;
		case 1:
			filtro = (String) this.selectedMovimiento.getPos2();
			break;
		case 2:
			filtro = (String) this.selectedMovimiento.getPos3();
			break;
		}
		
		if (this.isVenta()) {
			this.buscarVentas(filtro, posFiltro);
		}
		
		if (this.isAjusteStock()) {
			this.buscarAjustes(filtro, posFiltro);
		}
		
		if (this.isNotaCreditoVenta()) {
			this.buscarNotasCredito(filtro, posFiltro);
		}
		
		this.selectedMovimiento.setPos6(false);
	}
	
	@Command
	@NotifyChange("*")
	public void anularMovimiento() {
		String motivo = this.getMotivoAnulacion();
		if (!motivo.trim().isEmpty()) {
			this.anularMovimiento_(motivo);
		} else {
			Clients.showNotification("Debe especificar el motivo..",
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}
	}

	/**
	 * FUNCIONES..
	 */

	/**
	 * busqueda de las ventas..
	 */
	private void buscarVentas(String filtro, int posFiltro) throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Venta.class);
		b.setAnchoColumnas(new String[] { "140px", "140px", "", "140px", "140px" });
		b.setAtributos(new String[] { "numero", "cliente.empresa.ruc",
				"cliente.empresa.razonSocial", "tipoMovimiento.descripcion",
				"fecha" });
		b.setHeight("400px");
		b.setWidth("900px");
		b.setNombresColumnas(new String[] { "Número", "Ruc", "Razón Social",
				"Tipo Movimiento", "Fecha" });
		b.setTitulo("Ventas");
		b.addWhere("c.tipoMovimiento.sigla = '"
				+ this.selectedItem.getSigla() + "' and c.estadoComprobante IS NULL");
		b.show(filtro, posFiltro);
		if (b.isClickAceptar()) {
			this.selectedMovimiento = b.getSelectedItem();
		}
	}
	
	/**
	 * busqueda de las notas de credito..
	 */
	private void buscarNotasCredito(String filtro, int posFiltro) throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(NotaCredito.class);
		b.setAnchoColumnas(new String[] { "140px", "140px", "", "140px", "140px" });
		b.setAtributos(new String[] { "numero", "cliente.empresa.ruc",
				"cliente.empresa.razonSocial", "tipoMovimiento.descripcion",
				"fechaEmision" });
		b.setHeight("400px");
		b.setWidth("900px");
		b.setNombresColumnas(new String[] { "Número", "Ruc", "Razón Social", "Tipo Movimiento", "Fecha" });
		b.setTitulo("Notas de Crédito");
		b.addWhere("c.tipoMovimiento.sigla = '"
				+ this.selectedItem.getSigla() + "' and c.estadoComprobante.sigla != '" + Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA + "'");
		b.show(filtro, posFiltro);
		if (b.isClickAceptar()) {
			this.selectedMovimiento = b.getSelectedItem();
		}
	}
	
	/**
	 * busqueda de los ajustes..
	 */
	private void buscarAjustes(String filtro, int posFiltro) throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(AjusteStock.class);
		b.setAssembler(new AjusteStockAssembler());
		b.setAnchoColumnas(new String[] { "140px", "140px", "" });
		b.setAtributos(new String[] { "numero", "fecha", "descripcion" });
		b.setHeight("400px");
		b.setWidth("900px");
		b.setNombresColumnas(new String[] { "Número", "Fecha", "Descripción" });
		b.setTitulo("Ajustes de Stock");
		b.addWhere("c.tipoMovimiento.sigla = '"
				+ this.selectedItem.getSigla() + "' and c.estadoComprobante.id != " 
				+ this.getUtilDto().getEstadoComprobanteAnulado().getId());
		b.show(filtro, posFiltro);
		if (b.isClickAceptar()) {
			AjusteStockDTO dto = (AjusteStockDTO) b.getSelectedItemDTO();
			MyArray my = new MyArray();
			my.setId(dto.getId());
			my.setPos1(dto.getNumero());
			my.setPos2("- - -");
			my.setPos3(dto.getDescripcion());
			my.setPos4(dto.getTipoMovimiento().getPos1());
			my.setPos5(dto.getFecha());			
			this.selectedMovimiento = my;
		}
	}
	
	/**
	 * anula el movimiento..
	 */
	private void anularMovimiento_(String motivo) {		
		try {			
			if (this.isVentaContado()) {
				this.anularVentaContado(motivo);
			} else if (this.isVentaCredito()) {
				this.anularVentaCredito(motivo);
			} else if (this.isAjusteStock()) {
				this.anularAjusteStock(motivo);
			} else if (this.isNotaCreditoVenta()) {
				this.anularNotaCredito(motivo);
			}
			this.selectedMovimiento.setPos6(true);
			Clients.showNotification("Movimiento anulado..");

		} catch (Exception e) {
			e.printStackTrace();
			Clients.showNotification(
					"Hubo un error al intentar anular..\n " + e.getMessage(),
					Clients.NOTIFICATION_TYPE_ERROR, null, null, 0);
		}	
	}
	
	/**
	 * anulacion de venta contado..
	 */
	private void anularVentaContado(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularVentaContado(
				this.selectedMovimiento.getId(), motivo, this.getLoginNombre());
	}
	
	/**
	 * anulacion de venta credito..
	 */
	private void anularVentaCredito(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularVentaCredito(
				this.selectedMovimiento.getId(), motivo, this.getLoginNombre());
	}
	
	/**
	 * anulacion de ajuste stock..
	 */
	private void anularAjusteStock(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularAjusteStock(
				this.selectedMovimiento.getId(), motivo, this.getLoginNombre());
	}
	
	/**
	 * anulacion de nota de credito venta..
	 */
	private void anularNotaCredito(String motivo) throws Exception {
		ControlAnulacionMovimientos.anularNotaCreditoVenta(
				this.selectedMovimiento.getId(), motivo, this.getLoginNombre());
	}
	
	/**
	 * GETS'SETS
	 */
	
	/**
	 * @return los tipos de movimientos..
	 */
	@DependsOn("filterDesc")
	public List<MyPair> getTiposMovimientos() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		if (this.filterDesc.trim().isEmpty()) {
			return this.tiposDeMovimientos;
		}
		for (MyPair tm : this.tiposDeMovimientos) {
			if (tm.getText().toUpperCase()
					.indexOf(this.filterDesc.toUpperCase()) >= 0) {
				out.add(tm);
			}
		}
		return out;
	}
	
	/**
	 * setea los tipos de movimientos..
	 */
	private void setTiposDeMovimientos() {
		this.tiposDeMovimientos = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		try {
			List<TipoMovimiento> tms = rr.getTiposMovimientosLegales();
			for (TipoMovimiento tm : tms) {
				MyPair mp = new MyPair(tm.getId(), tm.getDescripcion(),
						tm.getSigla());
				this.tiposDeMovimientos.add(mp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return true si el item seleccionado es venta..
	 */
	private boolean isVenta() {
		return this.isVentaContado() || this.isVentaCredito();
	}
	
	/**
	 * @return true si es venta contado..
	 */
	private boolean isVentaContado() {
		return this.selectedItem.getSigla().equals(
				Configuracion.SIGLA_TM_FAC_VENTA_CONTADO);
	}
	
	/**
	 * @return true si es venta credito..
	 */
	private boolean isVentaCredito() {
		return this.selectedItem.getSigla().equals(
				Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
	}
	
	/**
	 * @return true si es ajuste stock positivo..
	 */
	private boolean isAjustePositivo() {
		return this.selectedItem.getSigla().equals(
				Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
	}
	
	/**
	 * @return true si el item seleccionado es ajuste de stock..
	 */
	private boolean isAjusteStock() {
		return this.isAjustePositivo() || this.isAjusteNegativo();
	}
	
	/**
	 * @return true si es ajuste stock negativo..
	 */
	private boolean isAjusteNegativo() {
		return this.selectedItem.getSigla().equals(
				Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
	}
	
	/**
	 * @return true si el item seleccionado es nota de credito..
	 */
	private boolean isNotaCreditoVenta() {
		return this.selectedItem.getSigla().equals(
				Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);
	}
	
	private UtilDTO getUtilDto() {
		return (UtilDTO) this.getDtoUtil();
	}

	public MyPair getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MyPair selectedItem) {
		this.selectedItem = selectedItem;
	}

	public MyArray getSelectedMovimiento() {
		return selectedMovimiento;
	}

	public void setSelectedMovimiento(MyArray selectedMovimiento) {
		this.selectedMovimiento = selectedMovimiento;
	}

	public String getFilterDesc() {
		return filterDesc;
	}

	public void setFilterDesc(String filterDesc) {
		this.filterDesc = filterDesc;
	}
}
