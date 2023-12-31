package com.yhaguy.gestion.reparto;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Popup;

import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SoloViewModel;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.ID;
import com.yhaguy.UtilDTO;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.TransferenciaDetalle;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.inicio.AssemblerAcceso;

@SuppressWarnings("unchecked")
public class RepartoSimpleVM extends SoloViewModel {
	
	private RepartoViewModel dato;
	List<MyArray> listaDet = new ArrayList<MyArray>();
	
	private String filterNumero = "";

	@Wire
	private Popup popDetalle;

	@Init(superclass = true)
	public void init(
			@ExecutionArgParam(Configuracion.DATO_SOLO_VIEW_MODEL) RepartoViewModel dato)
			throws Exception {
		this.dato = dato;
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public String getAliasFormularioCorriente() {
		return ID.F_REPARTO;
	}
	
	/*************************************************/
	
	
	/******************* COMANDOS ********************/

	@Command
	@NotifyChange("*")
	public void mostrarDetalles(@BindingParam("movimiento") MyArray m) throws Exception{
		
		this.listaDet = (List<MyArray>) m.getPos6();
		WindowPopup w = new WindowPopup();
		w.setModo(WindowPopup.NUEVO);
		w.setTitulo("Detalle Comprobante " + m.getPos9() + " Cliente " + m.getPos4());
		w.setWidth("810px");
		w.setHigth("500px");
		w.setDato(this);
		w.setSoloBotonCerrar();
		w.show(Configuracion.VER_DETALLES_ZUL);
	}
	
	/**
	 * Muestra en un popup el detalle del item..
	 */
	@Command
	@NotifyChange("listaDet")
	public void verDetalle(@BindingParam("item") MyArray item,
			@BindingParam("comp") Component comp) {
		this.listaDet = (List<MyArray>) item.getPos6();
		this.popDetalle.open(comp, "after_end");
	}
	
	/*************************************************/
	
	
	/******************* FUNCIONES *******************/

	/**
	 * filtra los movimientos para no evitar duplicados..
	 */
	public void filtrarMovimientos(List<MyArray> movimientos,
			List<RepartoDetalleDTO> detalles) {
		List<MyArray> temp = new ArrayList<MyArray>(movimientos);
		for (MyArray item : temp) {
			for (RepartoDetalleDTO detalle : detalles) {
				if (item.getPos1() == detalle.getIdMovimiento()) {
					movimientos.remove(item);
				}
			}
		}
	}

	/**
	 * @return las transferencias para reparto..
	 */
	public List<MyArray> getTransferencias() throws Exception {

		long idTipoTransf = this.getIdTipoTransfExterna();
		long idEstadoTransf = this.getIdEstadoTransfConfirmada();

		RegisterDomain rr = RegisterDomain.getInstance();
		List<Transferencia> transferencias = rr.getTransferenciasParaReparto(
				idTipoTransf, idEstadoTransf);

		return this.getTransferenciasMyArray(transferencias);
	}	

	/**
	 * @return las ventas para reparto..
	 */
	public List<MyArray> getVentas(String numero) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (numero.trim().isEmpty()) {
			return new ArrayList<MyArray>();
		}
		List<Venta> ventas;
		long idSucursal = this.getAcceso().getSucursalOperativa().getId();
		
		if (Configuracion.empresa.equals(Configuracion.EMPRESA_GTSA)) {
			ventas = rr.getVentasParaRepartoBaterias(numero);
		} else {
			ventas = rr.getVentasParaReparto(numero, idSucursal);
		}

		return this.getVentasMyArray(ventas);
	}
	
	/**
	 * @return las ventas para reparto..
	 */
	public List<MyArray> getRemisiones(String numero) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		if (numero.trim().isEmpty()) {
			return new ArrayList<MyArray>();
		}
		List<Object[]> remisiones = rr.getRemisiones(numero, "", "", "", "", 50);
		return this.getRemisionesMyArray(remisiones);
	}
	
	/**
	 * @return las ventas convertidas a MyArray..
	 */
	private List<MyArray> getVentasMyArray(List<Venta> ventas) {
		List<MyArray> out = new ArrayList<MyArray>();
		for (Venta venta : ventas) {
			MyArray vta = this.getVentaMyArray(venta);
			out.add(vta);
		}
		return out;
	}
	
	/**
	 * @return las ventas convertidas a MyArray..
	 */
	private List<MyArray> getRemisionesMyArray(List<Object[]> remisiones) {
		List<MyArray> out = new ArrayList<MyArray>();
		for (Object[] remision : remisiones) {
			MyArray rem = this.getRemisionMyArray(remision);
			out.add(rem);
		}
		return out;
	}
	
	/**
	 * @return la venta convertida a MyArray..
	 */
	public MyArray getVentaMyArray(Venta venta) {		
		MyArray out = new MyArray();
		
		Object[] detalles = this.getDetallesVenta(venta);
		MyArray tipoMovimiento = new MyArray();
		tipoMovimiento.setId(venta.getTipoMovimiento().getId());
		tipoMovimiento.setPos1(venta.getTipoMovimiento().getDescripcion());	
		tipoMovimiento.setPos2(venta.getTipoMovimiento().getSigla());	

		out.setId(venta.getId());
		out.setPos1(venta.getId());
		out.setPos2(venta.getNumero());
		out.setPos3(venta.getFecha());
		out.setPos4(venta.getCliente().getRazonSocial());
		out.setPos5(venta.getTipoMovimiento().getDescripcion());
		out.setPos6(detalles[0]);
		out.setPos7(detalles[1]);
		out.setPos8(detalles[2]);
		out.setPos9(tipoMovimiento);
		out.setPos10(venta.getCliente().getDireccion());
		out.setPos11(venta.getPesoTotal());
		out.setPos12(venta.getTotalImporteGs_());
		out.setPos13(venta.getFormaEntrega());
		out.setPos14(venta.getObservacion().toUpperCase());
		
		return out;
	}
	
	/**
	 * @return la venta convertida a MyArray..
	 */
	public MyArray getRemisionMyArray(Object[] remision) {		
		MyArray out = new MyArray();
		TipoMovimiento tm = (TipoMovimiento) remision[5];
		
		Object[] detalles = new Object[] { "", "", "" };
		MyArray tipoMovimiento = new MyArray();
		tipoMovimiento.setId(tm.getId());
		tipoMovimiento.setPos1(tm.getDescripcion());	
		tipoMovimiento.setPos2(tm.getSigla());	

		out.setId((Long) remision[0]);
		out.setPos1(remision[0]);
		out.setPos2(remision[1]);
		out.setPos3(remision[4]);
		out.setPos4(remision[3]);
		out.setPos5(tm.getDescripcion());
		out.setPos6(detalles[0]);
		out.setPos7(detalles[1]);
		out.setPos8(detalles[2]);
		out.setPos9(tipoMovimiento);
		out.setPos10("");
		out.setPos11(0.0);
		out.setPos12(remision[6]);
		out.setPos13("");
		out.setPos14(remision[8]);
		
		return out;
	}
	
	/**
	 * @return las transferencias convertidas a MyArray..
	 */
	private List<MyArray> getTransferenciasMyArray(List<Transferencia> transfs) {
		List<MyArray> out = new ArrayList<MyArray>();
		for (Transferencia trans : transfs) {
			MyArray transf = this.getTransferenciaMyArray(trans);
			out.add(transf);
		}
		return out;
	}
	
	/**
	 * @return la transferencia convertida a MyArray..
	 */
	public MyArray getTransferenciaMyArray(Transferencia transf) {
		MyArray out = new MyArray();
		
		Object[] detalles = this.getDetallesTransferencia(transf);
		MyArray tipoMovimiento = this.getTmTransferencia();

		out.setPos1(transf.getId());
		out.setPos2(transf.getNumeroRemision());
		out.setPos3(transf.getFechaCreacion());
		out.setPos4(transf.getSucursalDestino().getDescripcion());
		out.setPos5(transf.getTransferenciaTipo().getDescripcion());
		out.setPos6(detalles[0]);
		out.setPos7(detalles[1]);
		out.setPos8(detalles[2]);
		out.setPos9(tipoMovimiento);
		out.setPos11(0.0);
		out.setPos12(transf.getImporteGs());
		out.setPos13("");
		out.setPos14(transf.getObservacion().toUpperCase());
		return out;
	}
	
	/**
	 * @return el detalle de ventas procesado..
	 */
	private Object[] getDetallesVenta(Venta venta) {
		List<MyArray> detalles = new ArrayList<MyArray>();

		long cantArt = 0;
		double montoTotal = 0;
		double totalUnitario = 0;

		for (VentaDetalle item : venta.getDetalles()) {
			MyArray det = new MyArray();
			det.setId(item.getId());
			det.setPos1(item.getArticulo().getCodigoInterno());
			det.setPos2(item.getArticulo().getDescripcion());
			det.setPos3(item.getCantidad());
			det.setPos4("");
			det.setPos5(item.getCantidadEntregada());

			cantArt = cantArt + item.getCantidad();

			totalUnitario = cantArt * (item.getPrecioGs() * item.getCantidad());
			montoTotal = montoTotal + totalUnitario;

			detalles.add(det);
		}

		return new Object[] { detalles, cantArt, montoTotal };
	}
	
	/**
	 * @return el detalle de transferencias procesadas..
	 */
	private Object[] getDetallesTransferencia(Transferencia transf) {
		List<MyArray> detalles = new ArrayList<MyArray>();
		
		long cantArt = 0;
		double costoTotal = 0;

		for (TransferenciaDetalle item : transf.getDetalles()) {
			MyArray det = new MyArray();
			det.setPos1(item.getArticulo().getCodigoInterno());
			det.setPos2(item.getArticulo().getDescripcion());
			det.setPos3(item.getCantidad());
			det.setPos4("");
			
			cantArt = cantArt + item.getCantidadEnviada();
			costoTotal = costoTotal + item.getCosto();
			detalles.add(det);
		}

		return new Object[] { detalles, cantArt, costoTotal };
	}

	/*************************************************/
	
	
	/******************** GET/SET ********************/
	
	@DependsOn("filterNumero")
	public List<MyArray> getMovimientos() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		out.addAll(this.getVentas(this.filterNumero));
		out.addAll(this.getTransferencias());
		out.addAll(this.getRemisiones(this.filterNumero));
		this.filtrarMovimientos(out, this.dato.getDto().getDetalles());
		return out;
	}
	
	/**
	 * @return el acceso..
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		AccesoDTO out = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		if (out == null) {
			try {
				AssemblerAcceso as = new AssemblerAcceso();
				out = (AccesoDTO) as.obtenerAccesoDTO(Configuracion.USER_MOBILE);
				s.setAttribute(Configuracion.ACCESO, out);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}			
		return out;
	}
	
	private UtilDTO getUtil() {
		return (UtilDTO) this.getDtoUtil();
	}
	
	private MyArray getTmTransferencia() {
		return this.getUtil().getTmTransferenciaMercaderia();
	}
	
	private long getIdTipoTransfExterna() {
		return this.getUtil().getTipoTransferenciaExterna().getId().longValue();
	}
	
	private long getIdEstadoTransfConfirmada() {
		return this.getUtil().getEstadoTransferenciaConfirmada().getId()
				.longValue();
	}
	
	public RepartoViewModel getDato() {
		return dato;
	}

	public void setDato(RepartoViewModel dato) {
		this.dato = dato;
	}
	
	public List<MyArray> getListaDet() {
		return listaDet;
	}

	public void setListaDet(List<MyArray> listaDet) {
		this.listaDet = listaDet;
	}

	public String getFilterNumero() {
		return filterNumero;
	}

	public void setFilterNumero(String filterNumero) {
		this.filterNumero = filterNumero;
	}
}
