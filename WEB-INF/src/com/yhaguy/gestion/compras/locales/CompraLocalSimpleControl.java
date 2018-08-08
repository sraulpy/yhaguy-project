package com.yhaguy.gestion.compras.locales;

import java.util.Date;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.Gasto;
import com.yhaguy.gestion.compras.timbrado.WindowTimbrado;

public class CompraLocalSimpleControl extends SoloViewModel {
	
	static final int FILTRO_COD_INTERNO = 0;
	static final int FILTRO_COD_PROVEEDOR = 1;
	static final int FILTRO_COD_ORIGINAL = 2;
	static final int FILTRO_DESCRIPCION = 3;

	private CompraLocalControlBody dato;
	
	static String[] attArticulo = { "codigoInterno", "codigoProveedor",
			"codigoOriginal", "descripcion" };
	static String[] columnas = {"Código", "Código Proveedor", "Código Original", "Descripción"};	
	
	static String[] attGastos = {"numeroFactura", "proveedor.empresa.razonSocial", "importeGs"};
	static String[] colGastos = {"Número", "Proveedor", "Importe Gs."};	
	
	@Wire
	private Intbox cant;

	@Init(superclass=true)
	public void init(
			@ExecutionArgParam(Config.DATO_SOLO_VIEW_MODEL) CompraLocalControlBody dato) {
		this.dato = dato;
	}
	
	@AfterCompose(superclass=true)
	public void afterCompose(){
	}
	
	@Override
	public String getAliasFormularioCorriente(){
		return ID.F_COMPRA_LOCAL_ABM;
	}
	
	/*********************************************/
	
	@Command
	@NotifyChange("*")
	public void buscarArticulo(@BindingParam("tipo") Object tipo,
			@BindingParam("filtro") int filtro) {
		String cod = this.getFiltro(tipo, filtro);		
		try {
			BuscarElemento b = new BuscarElemento();
			b.setClase(Articulo.class);
			b.setTitulo("Buscar Artículo");
			b.setAtributos(attArticulo);
			b.setNombresColumnas(columnas);
			b.setAnchoColumnas(new String[] { "120px", "120px", "120px", "" });
			b.setWidth("800px");
			b.addWhere("c.articuloEstado.sigla = '" + Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO + "'");
			b.show(cod, filtro);
			if ((b.isClickAceptar())
					&& (tipo instanceof CompraLocalFacturaDetalleDTO)) {
				this.dato.getNvoItem().setArticulo(b.getSelectedItem());
				this.cant.focus();
			} else if ((b.isClickAceptar())
					&& (tipo instanceof CompraLocalOrdenDetalleDTO)) {
				this.dato.getNvoDetalle().setArticulo(b.getSelectedItem());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return el filtro para buscar articulo..
	 */
	private String getFiltro(Object tipo, int filtro) {
		MyArray articulo;
		
		if (tipo instanceof CompraLocalFacturaDetalleDTO) {
			articulo = this.dato.getNvoItem().getArticulo();
		} else {
			articulo = this.dato.getNvoDetalle().getArticulo();
		}
		
		switch (filtro) {
		
		case FILTRO_COD_INTERNO:
			return (String) articulo.getPos1();

		case FILTRO_COD_PROVEEDOR:
			return (String) articulo.getPos2();
			
		case FILTRO_COD_ORIGINAL:
			return (String) articulo.getPos3();
			
		case FILTRO_DESCRIPCION:
			return (String) articulo.getPos4();
		}
		
		return "";
	}

	@Command
	@NotifyChange("*")
	public void abrirVentanaTimbrado() {

		WindowTimbrado w = new WindowTimbrado();
		w.setIdProveedor(this.dato.getDto().getProveedor().getId());
		w.setTimbrado((String) this.dato.getNvaFactura().getTimbrado()
				.getPos1());
		w.show(WindowPopup.NUEVO, w);

		if (w.isClickAceptar()) {
			this.dato.getNvaFactura().setTimbrado(w.getSelectedTimbrado());
		} else {
			this.dato.getNvaFactura().setTimbrado(new MyArray("", null));
		}

		BindUtils.postNotifyChange(null, null, this.dato.getNvaFactura(),
				"timbrado");
	}
	
	@Command
	@NotifyChange("*")
	public void buscarGastos() throws Exception {
		BuscarElemento b = new BuscarElemento();
		b.setClase(Gasto.class);
		b.setTitulo("Buscar Gastos");
		b.setAtributos(attGastos);
		b.setAnchoColumnas(new String[] { "200px", "", "130px" });
		b.setNombresColumnas(colGastos);
		b.setWidth("800px");
		b.setContinuaSiHayUnElemento(true);
		b.show("");
		if (b.isClickAceptar()) {
			this.dato.getNvoGasto().setGasto(b.getSelectedItem());
			this.dato.getNvoGasto().setMontoGs(
					(double) b.getSelectedItem().getPos3());
		}
	}	
	
	@Wire
	private Doublebox porcDesc;
	
	@Command
	public void dolarizar(){
		double costoGs = this.dato.getNvoDetalle().getCostoGs();
		this.dato.getNvoDetalle().setCostoDs(m.redondeoDosDecimales(costoGs / this.dato.getDto().getTipoCambio()));
		BindUtils.postNotifyChange(null, null, this.dato.getNvoDetalle(), "costoDs");
	}
	
	@Command
	public void guaranizar(){
		double costoDs = this.dato.getNvoDetalle().getCostoDs();
		this.dato.getNvoDetalle().setCostoGs(costoDs * this.dato.getDto().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvoDetalle(), "costoGs");
	}
	
	@Command
	public void dolarizarFactura(){
		double costoGs = this.dato.getNvoItem().getCostoGs();
		this.dato.getNvoItem().setCostoDs(m.redondeoDosDecimales(costoGs / this.dato.getDto().getTipoCambio()));
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "costoDs");
	}
	
	@Command
	public void guaranizarFactura(){
		double costoDs = this.dato.getNvoItem().getCostoDs();
		this.dato.getNvoItem().setCostoGs(costoDs * this.dato.getDto().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "costoGs");
	}
	
	@Command
	public void dolarizarGasto(){
		double montoGs = this.dato.getNvoGasto().getMontoGs();
		this.dato.getNvoGasto().setMontoDs(m.redondeoDosDecimales(montoGs / this.dato.getDto().getTipoCambio()));
		BindUtils.postNotifyChange(null, null, this.dato.getNvoGasto(), "montoDs");
	}
	
	@Command
	public void guaranizarGasto(){
		double montoDs = this.dato.getNvoGasto().getMontoDs();
		this.dato.getNvoGasto().setMontoGs(montoDs * this.dato.getDto().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvoGasto(), "montoGs");
	}
	
	@Command
	public void dolarizarTotalAsignado(){
		double totalGs = this.dato.getNvaFactura().getTotalAsignadoGs();
		this.dato.getNvaFactura().setTotalAsignadoDs(m.redondeoDosDecimales(totalGs / this.dato.getDto().getTipoCambio()));
		BindUtils.postNotifyChange(null, null, this.dato.getNvaFactura(), "totalAsignadoDs");
	}
	
	@Command
	public void guaranizarTotalAsignado(){
		double totalDs = this.dato.getNvaFactura().getTotalAsignadoDs();
		this.dato.getNvaFactura().setTotalAsignadoGs(totalDs * this.dato.getDto().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvaFactura(), "totalAsignadoGs");
	}
	
	@Command
	public void guaranizarDescuento(){
		double descDs = this.dato.getNvoItem().getImporteDescuentoDs();
		if (descDs > 0) {
			descDs = descDs * -1;
			this.dato.getNvoItem().setImporteDescuentoDs(descDs);
		}
		this.dato.getNvoItem().setImporteDescuentoGs(descDs * this.dato.getDto().getTipoCambio());
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "importeDescuentoGs");
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "importeDescuentoDs");
		this.porcDesc.setValue(0);
	}
	
	@Command
	public void calcularDescuento(@BindingParam("porc") double porc){
		double descGs = (this.dato.getImporteFactura()[2] * porc) / 100;
		this.dato.getNvoItem().setCostoGs(descGs);
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "costoGs");
	}	
	
	@Command
	public void verificarNotaCredito(){
		if (this.dato.getSelectedFactura().getTipoMovimiento()
				.compareTo(this.dato.getDtoUtil().getTmNotaCreditoCompra()) == 0) {
			if (this.dato.getNvoItem().getCantidad() > 0) {
				this.dato.getNvoItem().setCantidad(this.dato.getNvoItem().getCantidad() * -1);
			}
		}
		BindUtils.postNotifyChange(null, null, this.dato.getNvoItem(), "cantidad");
	}
	
	//Adjunto de los mails
	public String getNombreArchivoAdjunto(){
		return dato.getNombreArchivo() + ".pdf";
	}
	
	@Command
	public void calcularVencimiento(){
		CompraLocalFacturaDTO factura = this.dato.getNvaFactura();
		int plazo = (Integer) factura.getCondicionPago().getPos2();
		Date emision = factura.getFechaOriginal();
		Date vencimiento = m.calcularFechaVencimiento(emision, plazo);
		factura.setFechaVencimiento(vencimiento);
		BindUtils.postNotifyChange(null, null, factura, "fechaVencimiento");
	}
	
	@Command
	public void setCondicion() {
		CompraLocalFacturaDTO factura = this.dato.getNvaFactura();
		MyArray condicion = this.getUtil().getCondicionPagoContado();
		String sigla = (String) factura.getTipoMovimiento().getPos2();
		if(sigla.equals(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO))
			condicion = this.getUtil().getCondicionPagoCredito30();
		factura.setCondicionPago(condicion);
		BindUtils.postNotifyChange(null, null, factura, "condicionPago");
	}
	
	private UtilDTO getUtil() {
		return (UtilDTO) this.getDtoUtil();
	}
	
	public CompraLocalControlBody getDato() {
		return dato;
	}

	public void setDato(CompraLocalControlBody dato) {
		this.dato = dato;
	}
}
