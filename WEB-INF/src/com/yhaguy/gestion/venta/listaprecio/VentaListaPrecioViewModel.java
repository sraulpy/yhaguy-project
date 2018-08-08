package com.yhaguy.gestion.venta.listaprecio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.componente.ViewPdf;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.extras.reporte.DatosColumnas;
import com.coreweb.util.MyArray;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.ArticuloListaPrecioDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.comun.ControlArticuloCosto;
import com.yhaguy.inicio.AccesoDTO;
import com.yhaguy.util.Utiles;
import com.yhaguy.util.reporte.ReporteYhaguy;

public class VentaListaPrecioViewModel extends SimpleViewModel {
	
	static final String ZUL_LISTA_PRECIO = "/yhaguy/gestion/venta/editListaPrecio.zul";
	static final String ZUL_LISTA_DETALLE = "/yhaguy/gestion/venta/editListaPrecioDetalle.zul";
	static final String ZUL_PRINT_LISTA_PRECIO = "/yhaguy/gestion/venta/PrintListaPrecio.zul";
	
	private MyArray selectedLista;
	private MyArray selectedListaPrecioDetalle;
	
	private Proveedor selectedProveedor;
	
	private String filterCodigo = "";
	
	private int listaPrecioSize = 0;
	private int listaDetalleSize = 0;

	@Init(superclass = true)
	public void init() {	
	}	
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Command
	@NotifyChange("*")
	public void deleteItem() throws Exception {
		if (!this.mensajeSiNo("Desea eliminar el item seleccionado..?")) {
			return;
		}
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloListaPrecio lprecio = (ArticuloListaPrecio) rr
				.getObject(ArticuloListaPrecio.class.getName(),
						this.selectedLista.getId());
		lprecio.setDbEstado('D');
		rr.saveObject(lprecio, this.getLoginNombre());
		this.selectedLista = null;
		Clients.showNotification("Lista de Precio eliminada..");
	}
	
	@Command
	@NotifyChange("*")
	public void insertItem() throws Exception {
		this.selectedLista = new MyArray();
		this.selectedLista.setPos2(0);
		this.selectedLista.setPos6(new Date());
		this.selectedLista.setPos7((Date) null);
		this.showListaPrecio(true);
		this.selectedLista = null;
	}
	
	@Command
	@NotifyChange("*")
	public void editItem() throws Exception {
		this.showListaPrecio(false);
		this.selectedLista = null;
	}
	
	@Command
	@NotifyChange("articulos")
	public void editDetalle() throws Exception {
		this.showListaPrecioDetalle();
	}
	
	@Command
	public void imprimir() throws Exception {
		this.imprimirListaPrecio();
	}
	
	/**
	 * Impresion de la lista de precios..
	 */
	private void imprimirListaPrecio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setHigth("350px");
		wp.setWidth("350px");
		wp.setTitulo("Imprimir Lista de Precio..");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_PRINT_LISTA_PRECIO);
		
		if (wp.isClickAceptar()) {
			
			List<Articulo> arts = rr.getArticulos("", 50000);	
			List<Articulo> arts_ = new ArrayList<Articulo>();
			List<Object[]> data = new ArrayList<Object[]>();

			for (Articulo articulo : arts) {
				if (this.selectedProveedor == null) {
					arts_.add(articulo);
				} else if (articulo.isProveedor(this.selectedProveedor.getId())) {
					arts_.add(articulo);
				}
			}
			
			for (Articulo articulo : arts_) {
				String cod = articulo.getCodigoInterno();
				MyArray precio = this.getPrecio(cod, articulo.getId());
				Object[] obj1 = new Object[] { articulo.getCodigoInterno(),
						precio.getPos1(), precio.getPos2(),
						0.0 };
				data.add(obj1);
			}

			String lista = (String) this.selectedLista.getPos1();
			String proveedor = this.selectedProveedor == null ? "TODOS.." : this.selectedProveedor.getRazonSocial();
			String sucursal = this.getAcceso().getSucursalOperativa().getText();
			
			ReporteYhaguy rep = new ReporteListaPrecio(lista, proveedor, sucursal);
			rep.setDatosReporte(data);
			rep.setApaisada();

			ViewPdf vp = new ViewPdf();
			vp.setBotonImprimir(false);
			vp.setBotonCancelar(false);
			vp.showReporte(rep, this);
		}
	}
	
	/**
	 * despliega la ventana de datos de la lista de precio..
	 */
	private void showListaPrecio(boolean add) throws Exception {
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setHigth("350px");
		wp.setWidth("350px");
		wp.setTitulo((add ? "Agregar" : "Modificar") + " Lista de Precio..");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_LISTA_PRECIO);

		if (wp.isClickAceptar()) {
			RegisterDomain rr = RegisterDomain.getInstance();
			ArticuloListaPrecio precio = new ArticuloListaPrecio();
			
			if (add == false)
				precio = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), this.selectedLista.getId());
			
			precio.setDescripcion(((String) this.selectedLista.getPos1()).toUpperCase());
			precio.setMargen((int) this.selectedLista.getPos2());
			precio.setDesde((Date) this.selectedLista.getPos6());
			precio.setHasta((Date) this.selectedLista.getPos7());
			precio.setActivo(true);
			
			rr.saveObject(precio, this.getLoginNombre());

			Clients.showNotification("Registro " + (add ? "agregado.." : "modificado.."));
		}
	}
	
	/**
	 * despliega la ventana de datos de la lista de precio detalle..
	 */
	private void showListaPrecioDetalle() throws Exception {
		WindowPopup wp = new WindowPopup();
		wp.setDato(this);
		wp.setHigth("350px");
		wp.setWidth("350px");
		wp.setTitulo("Modificar Lista de Precio..");
		wp.setModo(WindowPopup.NUEVO);
		wp.show(ZUL_LISTA_DETALLE);

		if (wp.isClickAceptar()) {
			MyArray precioDet = (MyArray) this.selectedListaPrecioDetalle.getPos3();
			
			RegisterDomain rr = RegisterDomain.getInstance();
			ArticuloListaPrecioDetalle det = new ArticuloListaPrecioDetalle();
			
			if (!precioDet.esNuevo()) {
				det = (ArticuloListaPrecioDetalle) 
						rr.getObject(ArticuloListaPrecioDetalle.class.getName(), precioDet.getId());
			}
			
			det.setPrecioGs_contado((double) precioDet.getPos1());
			det.setPrecioGs_credito((double) precioDet.getPos2());
			det.setActivo(true);
			
			if (precioDet.esNuevo()) {
				ArticuloListaPrecio precio = (ArticuloListaPrecio) 
						rr.getObject(ArticuloListaPrecio.class.getName(), this.selectedLista.getId());
				Articulo art = (Articulo) 
						rr.getObject(Articulo.class.getName(), this.selectedListaPrecioDetalle.getId());
				det.setArticulo(art);
				precio.getDetalles().add(det);				
				rr.saveObject(precio, this.getLoginNombre());				
			} else {
				rr.saveObject(det, this.getLoginNombre());
			}
			Clients.showNotification("Registro modificado..");
		}
	}
	
	/**
	 * @return las listas de precio..
	 */
	public List<MyArray> getListasDePrecio() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloListaPrecio> list = rr.getListasDePrecio();
		for (ArticuloListaPrecio item : list) {
			MyArray my = new MyArray();
			my.setId(item.getId());
			my.setPos1(item.getDescripcion());
			my.setPos2(item.getMargen());
			my.setPos3(item.isVigente() ? "SI" : "NO");
			my.setPos4(Utiles.getDateToString(item.getDesde(), Utiles.DD_MM_YY));
			my.setPos5(item.getHasta() == null ? "- - -" : Utiles.getDateToString(item.getHasta(), Utiles.DD_MM_YY));
			my.setPos6(item.getDesde());
			my.setPos7(item.getHasta());
			my.setPos8(item.getFormula());
			out.add(my);
		}
		this.listaPrecioSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "listaPrecioSize");
		return out;
	}
	
	/**
	 * @return los articulos..
	 */
	@DependsOn({ "filterCodigo", "selectedLista" })
	public List<MyArray> getArticulos() throws Exception {
		List<MyArray> out = new ArrayList<MyArray>();
		
		if (this.selectedLista == null) {
			return out;
		}
		
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Articulo> list = rr.getArticulos(this.filterCodigo, 10);
		for (Articulo item : list) {			
			MyArray my = new MyArray();
			my.setId(item.getId());
			my.setPos1(item.getCodigoInterno());
			my.setPos2(this.selectedLista);
			my.setPos3(this.getPrecio(item.getCodigoInterno(), item.getId()));
			my.setPos4(ControlArticuloCosto.getCostoPromedio(item.getId(), new Date()));
			out.add(my);
		}
		this.selectedListaPrecioDetalle = null;
		BindUtils.postNotifyChange(null, null, this, "selectedListaPrecioDetalle");
		
		this.listaDetalleSize = out.size();
		BindUtils.postNotifyChange(null, null, this, "listaDetalleSize");
		return out;
	}
	
	/**
	 * @return el precio..
	 */
	private MyArray getPrecio(String codArticulo, long idArticulo) throws Exception {
		MyArray out = new MyArray();
		RegisterDomain rr = RegisterDomain.getInstance();
		ArticuloListaPrecioDetalle precio = rr.getListaPrecioDetalle(this.selectedLista.getId(), codArticulo);
		double costo = this.getCostoArticulo(idArticulo);
		String formula = (String) this.selectedLista.getPos8();
		if (precio != null && formula == null) {
			out.setId(precio.getId());
			out.setPos1(precio.getPrecioGs_contado());
			out.setPos2(precio.getPrecioGs_credito());
			out.setPos4(this.m.obtenerPorcentajeDelValor((precio.getPrecioGs_contado() - costo), costo));
		} else {
			int margen = (int) this.selectedLista.getPos2();
			/*double porcIva = Configuracion.VALOR_IVA_10;
			double iva = this.m.obtenerValorDelPorcentaje(costo, porcIva);
			double ganancia = this.m.obtenerValorDelPorcentaje(costo + iva, margen);*/
			
			// formula lista precio mayorista..
			if (this.selectedLista.getId().longValue() == 2 && formula != null) {
				ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
				ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
				if (precioDet != null) {
					double cont = precioDet.getPrecioGs_contado();
					double cred = precioDet.getPrecioGs_credito();
					double formulaCont = cont + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 10);
					double formulaCred = cred + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 10);
					out.setPos1(formulaCont);
					out.setPos2(formulaCred);
					out.setPos4(margen);
				} else {
					margen = distribuidor.getMargen();
					double precioGs = ControlArticuloCosto.getPrecioVenta(costo, margen);
					double formula_ = precioGs + Utiles.obtenerValorDelPorcentaje(precioGs, 10);
					out.setPos1(formula_);
					out.setPos2(formula_);
					out.setPos4(margen);
				}
			
			// formula lista precio autocentros..
			} else if (this.selectedLista.getId().longValue() == 3 && formula != null) {
				ArticuloListaPrecio distribuidor = (ArticuloListaPrecio) rr.getObject(ArticuloListaPrecio.class.getName(), 1);
				ArticuloListaPrecioDetalle precioDet = rr.getListaPrecioDetalle(distribuidor.getId(), codArticulo);
				if (precioDet != null) {
					double cont = precioDet.getPrecioGs_contado() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_contado(), 10);
					double cred = precioDet.getPrecioGs_credito() + Utiles.obtenerValorDelPorcentaje(precioDet.getPrecioGs_credito(), 10);
					double formulaCont = (cont * 1.18) / 0.8;
					double formulaCred = (cred * 1.18) / 0.8;
					out.setPos1(formulaCont);
					out.setPos2(formulaCred);
					out.setPos4(margen);
				} else {
					margen = distribuidor.getMargen();
					double precioGs = ControlArticuloCosto.getPrecioVenta(costo, margen);
					double formula_ = ((precioGs + Utiles.obtenerValorDelPorcentaje(precioGs, 10)) * 1.18) / 0.8;
					out.setPos1(formula_);
					out.setPos2(formula_);
					out.setPos4(margen);
				}				

			} else {
				out.setPos1(ControlArticuloCosto.getPrecioVenta(costo, margen));
				out.setPos2(ControlArticuloCosto.getPrecioVenta(costo, margen));
				out.setPos4(margen);
			}			
		}	
		out.setPos3(costo);
		return out;
	}
	
	/**
	 * @return el costo del articulo..
	 */
	private double getCostoArticulo(long idArticulo) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		return rr.getCostoGs(idArticulo);
	}

	
	/**
	 * GETS / SETS
	 */
	private AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}
	
	public MyArray getSelectedLista() {
		return selectedLista;
	}

	public void setSelectedLista(MyArray selectedLista) {
		this.selectedLista = selectedLista;
	}

	public String getFilterCodigo() {
		return filterCodigo;
	}

	public void setFilterCodigo(String filterCodigo) {
		this.filterCodigo = filterCodigo;
	}

	public int getListaPrecioSize() {
		return listaPrecioSize;
	}

	public void setListaPrecioSize(int sizeListaPrecio) {
		this.listaPrecioSize = sizeListaPrecio;
	}

	public MyArray getSelectedListaPrecioDetalle() {
		return selectedListaPrecioDetalle;
	}

	public void setSelectedListaPrecioDetalle(MyArray selectedListaPrecioDetalle) {
		this.selectedListaPrecioDetalle = selectedListaPrecioDetalle;
	}

	public int getListaDetalleSize() {
		return listaDetalleSize;
	}

	public void setListaDetalleSize(int listaDetalleSize) {
		this.listaDetalleSize = listaDetalleSize;
	}

	public Proveedor getSelectedProveedor() {
		return selectedProveedor;
	}

	public void setSelectedProveedor(Proveedor selectedProveedor) {
		this.selectedProveedor = selectedProveedor;
	}
}

/**
 * Reporte de Lista de Precios..
 */
class ReporteListaPrecio extends ReporteYhaguy {
	
	private String lista;
	private String proveedor;
	private String sucursal;
	
	static List<DatosColumnas> cols = new ArrayList<DatosColumnas>();
	static DatosColumnas col1 = new DatosColumnas("Código", TIPO_STRING, 50);
	static DatosColumnas col2 = new DatosColumnas("Precio Cont.", TIPO_DOUBLE, 30); 
	static DatosColumnas col3 = new DatosColumnas("Precio Créd.", TIPO_DOUBLE, 30);
	static DatosColumnas col4 = new DatosColumnas("Margen", TIPO_DOUBLE, 30);
	
	public ReporteListaPrecio(String lista, String proveedor, String sucursal) {
		this.lista = lista;
		this.proveedor = proveedor;
		this.sucursal = sucursal;
	}
	
	static {
		cols.add(col1);
		cols.add(col2);
		cols.add(col3);
		cols.add(col4);
	}

	@Override
	public void informacionReporte() {
		this.setTitulo("Lista de Precios");
		this.setDirectorio("ventas");
		this.setNombreArchivo("precios");
		this.setTitulosColumnas(cols);
		this.setBody(this.getCuerpo());
	}
	
	/**
	 * cabecera del reporte..
	 */
	@SuppressWarnings("rawtypes")
	private ComponentBuilder getCuerpo() {
		VerticalListBuilder out = cmp.verticalList();
		out.add(cmp.horizontalFlowList().add(
				this.textoParValor("Lista", this.lista))
				.add(this.textoParValor("Proveedor", this.proveedor))
				.add(this.textoParValor("Sucursal", this.sucursal)));
		out.add(cmp.horizontalFlowList().add(this.texto("")));

		return out;
	}
}
