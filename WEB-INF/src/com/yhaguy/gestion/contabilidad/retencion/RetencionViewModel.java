package com.yhaguy.gestion.contabilidad.retencion;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.Config;
import com.coreweb.componente.BuscarElemento;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.coreweb.extras.browser.ColumnaBrowser;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.MyArray;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.RetencionIva;
import com.yhaguy.gestion.caja.recibos.ReciboDTO;
import com.yhaguy.gestion.caja.recibos.ReciboDetalleDTO;

public class RetencionViewModel extends BodyApp {
	
	static final String NRO_RET = Configuracion.NRO_RETENCION_IVA;
	
	private RetencionIvaDTO dto = new RetencionIvaDTO();
	
	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public boolean verificarAlGrabar() {
		return true;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return null;
	}

	@Override
	public Assembler getAss() {
		return new RetencionIvaAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (RetencionIvaDTO) dto;
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		RetencionIvaDTO retencion = new RetencionIvaDTO();
		this.inicializarValores(retencion);
		return retencion;
	}

	@Override
	public String getEntidadPrincipal() {
		return RetencionIva.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}
	
	@Override
	public boolean getImprimirDeshabilitado() {
		return this.isRetencionConfirmada() == false;
	}
	
	@Override
	public void showImprimir() {
		Clients.showNotification("Impresión de la retencion..");
	}
	
	@Override
	public Browser getBrowser() {
		return new RetencionBrowser();
		
	};
	
	
	/***************** COMANDOS ****************/
	
	@Command
	@NotifyChange("*")
	public void buscarFacturas() throws Exception {
		this.buscarFacturas(this.dto);
	}
	
	@Command
	@NotifyChange("*")
	public void confirmar() throws Exception {
		this.confirmarRetencion();
	}
	
	/*******************************************/
	
	
	/***************** FUNCIONES ***************/
	
	/**
	 * Inicializa valores por defecto..
	 */
	private void inicializarValores(RetencionIvaDTO retencion) {
		retencion.setEstadoComprobante(this.getEstadoComprobantePendiente());
	}
	
	/**
	 * Busca los movimientos que seran aplicados..
	 */
	private void buscarFacturas(RetencionIvaDTO retencion) throws Exception {
		
		String[] atributos = new String[] { "tipoMovimiento.descripcion", "numeroFactura",
				"fecha", "importeGs", "importeIva5", "importeIva10" };

		String[] columnas = new String[] { "Tipo Movimiento", "Número",
				"Fecha", "Importe Gs.", "Iva 5%", "Iva 10%" };
		
		String[] tipos = new String[] { Config.TIPO_STRING, Config.TIPO_STRING,
				Config.TIPO_DATE, Config.TIPO_STRING, Config.TIPO_NUMERICO, Config.TIPO_NUMERICO };

		long idProveedor = this.dto.getProveedor().getId();

		BuscarElemento b = new BuscarElemento();
		b.setClase(Gasto.class);
		b.setTitulo("Facturas de Compra / Gastos - Proveedor: "
				+ retencion.getProveedor().getPos2() + " - Sucursal: "
				+ this.getSucursal().getText());
		b.setAtributos(atributos);
		b.setNombresColumnas(columnas);
		b.setTipos(tipos);
		b.setWidth("980px");
		b.addOrden("numeroFactura");
		b.addWhere("c.proveedor.id = " + idProveedor + " and "
				+ "(c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_GASTO_CONTADO + "' "
				+ " or c.tipoMovimiento.sigla = '"
				+ Configuracion.SIGLA_TM_FAC_GASTO_CREDITO
				+ "') and c.sucursal.id = " + this.getSucursal().getId());
		b.setContinuaSiHayUnElemento(false);
		b.show("%");

		if (b.isClickAceptar()) {
			MyArray movimiento = b.getSelectedItem();
			this.insertarItem(movimiento);
		}
	}
	
	/**
	 * inserta un nuevo item al detalle..
	 */
	private void insertarItem(MyArray movimiento) {
		double iva5 = (double) movimiento.getPos5();
		double iva10 = (double) movimiento.getPos6();
		RetencionIvaDetalleDTO item = new RetencionIvaDetalleDTO();
		item.setGasto(movimiento);
		item.setPorcentaje(this.dto.getPorcentaje());
		item.setImporteIvaGs(iva5 + iva10);
		this.dto.getDetalles().add(item);
	}
	
	/**
	 * Confirma la retencion..
	 */
	private void confirmarRetencion() throws Exception {
		
		if (this.mensajeSiNo("Esta seguro de confirmar la retención..") == false)
			return;
		
		this.dto.setEstadoComprobante(this.getEstadoComprobanteCerrado());
		this.dto.setNumero(this.getNumero());
		this.dto.setReadonly();
		this.dto = (RetencionIvaDTO) this.saveDTO(this.dto);
		this.setEstadoABMConsulta();
		
		Clients.showNotification("Retención Confirmada..");
	}
	
	/**
	 * Genera retenciones a partir de un pago..
	 */
	public void generarRetencionDesdePago(ReciboDTO pago, String numero)
			throws Exception {

		List<MyArray> gastos = new ArrayList<MyArray>();
		List<MyArray> compras = new ArrayList<MyArray>();

		for (ReciboDetalleDTO detalle : pago.getDetalles()) {

			long idMov = detalle.getMovimiento().getIdMovimientoOriginal();
			long idTipoMov = detalle.getMovimiento().getTipoMovimiento()
					.getId().longValue();

			if (idTipoMov == this.getIdTmGastoCredito()
					|| idTipoMov == this.getIdTmGastoContado()) {
				MyArray gasto = this.getGasto(idMov);
				boolean retenido = (boolean) gasto.getPos3();
				if (!retenido)
					gastos.add(gasto);
			}
			
			if (idTipoMov == this.getIdTmCompraCredito()
					|| idTipoMov == this.getIdTmCompraContado()) {
				MyArray compra = this.getCompra(idMov);
				if(compra == null)
					return;
				boolean retenido = (boolean) compra.getPos3();
				if (!retenido)
					compras.add(compra);
			}
		}
		
		// si no hay detalles no genera la retencion..
		if(gastos.size() == 0 && compras.size() == 0)
			return;

		MyArray emp = new MyArray();
		emp.setId(pago.getDetalles().get(0).getMovimiento().getIdEmpresa());

		RetencionIvaDTO ret = new RetencionIvaDTO();
		ret.setNumero(numero);
		ret.setEmpresa(emp);
		ret.setEstadoComprobante(this.getEstadoComprobanteCerrado());

		for (MyArray gasto : gastos) {
			double iva5 = (double) gasto.getPos1();
			double iva10 = (double) gasto.getPos2();

			RetencionIvaDetalleDTO det = new RetencionIvaDetalleDTO();
			det.setGasto(gasto);
			det.setImporteIvaGs(iva5 + iva10);
			ret.getDetalles().add(det);
		}
		
		for (MyArray compra : compras) {
			double iva5 = (double) compra.getPos1();
			double iva10 = (double) compra.getPos2();

			RetencionIvaDetalleDTO det = new RetencionIvaDetalleDTO();
			det.setCompra(compra);
			det.setImporteIvaGs(iva5 + iva10);
			ret.getDetalles().add(det);
		}
		pago.setRetencion(ret);
	}
	
	/**
	 * @return el gasto convertido a MyArray
	 */
	private MyArray getGasto(long idGasto) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Gasto domain = (Gasto) rr.getObject(Gasto.class.getName(), idGasto);
		MyArray out = new MyArray();
		out.setId(domain.getId());
		out.setPos1(domain.getImporteIva5());
		out.setPos2(domain.getImporteIva10());
		out.setPos3(domain.isIvaRetenido());
		return out;
	}
	
	/**
	 * @return la compra convertido a MyArray
	 */
	private MyArray getCompra(long idCompra) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		CompraLocalFactura domain = (CompraLocalFactura) rr.getObject(
				CompraLocalFactura.class.getName(), idCompra);
		if(domain == null)
			return null;
		MyArray out = new MyArray();
		out.setId(domain.getId());
		out.setPos1(domain.getImporteIva5());
		out.setPos2(domain.getImporteIva10());
		out.setPos3(domain.isIvaRetenido());
		return out;
	}
	
	/*******************************************/

	
	/**************** GET / SET ****************/
	
	@DependsOn("dto.empresa")
	public boolean isDetalleVisible() {
		return ((this.dto.getEmpresa() != null));
	}
	
	/**
	 * @return el id del tipo movimiento gasto credito..
	 */
	private long getIdTmGastoCredito() {
		return this.getDtoUtil().getTmFacturaGastoCredito().getId().longValue();
	}
	
	/**
	 * @return el id del tipo movimiento gasto contado..
	 */
	private long getIdTmGastoContado() {
		return this.getDtoUtil().getTmFacturaGastoContado().getId().longValue();
	}
	
	/**
	 * @return el id del tipo movimiento compra credito..
	 */
	private long getIdTmCompraCredito() {
		return this.getDtoUtil().getTmFacturaCompraCredito().getId().longValue();
	}
	
	/**
	 * @return el id del tipo movimiento compra contado..
	 */
	private long getIdTmCompraContado() {
		return this.getDtoUtil().getTmFacturaCompraContado().getId().longValue();
	}
	
	public boolean isRetencionConfirmada() {
		long confirmado = this.getEstadoComprobanteCerrado().getId();
		long actual = this.dto.getEstadoComprobante().getId();
		return actual == confirmado;
	}
	
	private String getNumero() throws Exception {
		String nro = AutoNumeroControl.getAutoNumeroKey(NRO_RET, 7);
		return nro;
	}
	
	public RetencionIvaDTO getDto() {
		return dto;
	}

	public void setDto(RetencionIvaDTO dto) {
		this.dto = dto;
	}
}

/**
 * Browser de retenciones..
 */
class RetencionBrowser extends Browser {

	@Override
	public List<ColumnaBrowser> getColumnasBrowser() {
		ColumnaBrowser col1 = new ColumnaBrowser();
		ColumnaBrowser col2 = new ColumnaBrowser();
		ColumnaBrowser col3 = new ColumnaBrowser();
		ColumnaBrowser col4 = new ColumnaBrowser();

		col1.setCampo("fecha"); 	
		col1.setTitulo("Fecha");
		col1.setComponente(LABEL_DATE);
		col1.setTipo(Config.TIPO_DATE);
		
		col2.setCampo("numero"); 	
		col2.setTitulo("Número");
		col2.setComponente(LABEL_NUMERICO);
		col2.setTipo(Config.TIPO_NUMERICO);
		
		col3.setCampo("montoRetencion"); 	
		col3.setTitulo("Monto Retención");
		col3.setComponente(LABEL_NUMERICO);
		col3.setTipo(Config.TIPO_NUMERICO);

		col4.setCampo("empresa.razonSocial"); 	
		col4.setTitulo("Empresa");		
		
		List<ColumnaBrowser> columnas = new ArrayList<ColumnaBrowser>();
		columnas.add(col1);
		columnas.add(col2);
		columnas.add(col3);
		columnas.add(col4);
		
		return columnas;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getEntidadPrincipal() {
		return RetencionIva.class;
	}

	@Override
	public void setingInicial() {
		this.addOrden("id");
		this.setWidthWindows("900px");
		this.setHigthWindows("80%");
	}

	@Override
	public String getTituloBrowser() {
		// TODO Auto-generated method stub
		return "Retenciones";
	}
	
}
