package com.yhaguy;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.coreweb.Config;
import com.coreweb.templateABM.Body;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.ArticuloListaPrecio;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.inicio.AccesoDTO;

/**
 * Esta clase intermedia nos permite tener métodos propios para cada aplicación, y mantener 
 * la estructura del Core.
 * NOTA: se puede implementar una clase similar para los otros controles, SimpleViewModel y SoloViewModel
 * @author daniel
 *
 */
public abstract class BodyApp extends Body {

	static final String ID_MASK_ANULADO = "maskAnulacion";
	
	private UsuarioPropiedadApp usuarioPropiedad = null;
	private AccesoDTO acceso = null;
	
	@Init(superclass = true)
	public void initBodyApp(){
		this.usuarioPropiedad = new UsuarioPropiedadApp(this.getUs());
	}
	
	@AfterCompose(superclass = true)
	public void afterComposeBodyApp(){
		
	}
	
	// ============== Util core ===================================

	@Override
	/**
	 * Hace el cast para trabajar en la aplicacion.
	 */
	public UtilDTO getDtoUtil() {
		UtilDTO u = (UtilDTO)super.getDtoUtil();
		return u;
	}
		
	// ========================================================================
	
	public UsuarioPropiedadApp getUsuarioPropiedad(){
		return this.usuarioPropiedad;
	}

	public AccesoDTO getAcceso() {
		if (this.acceso == null){
			Session s = Sessions.getCurrent();
			this.acceso = (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
		}
		return acceso;
	}
	
	/**
	 * @return el funcionario asociado al login..
	 */
	public MyArray getLoginFuncionario() {
		return this.getAcceso().getFuncionario();
	}
	
	/**
	 * @return la sucursal operativa..
	 */
	public MyPair getSucursal() {
		return this.getAcceso().getSucursalOperativa();
	}
	
	/**
	 * @return estado comprobante pendiente..
	 */
	public MyPair getEstadoComprobantePendiente() {
		return this.getDtoUtil().getEstadoComprobantePendiente();
	}
	
	/**
	 * @return estado comprobante cerrado..
	 */
	public MyPair getEstadoComprobanteCerrado() {
		return this.getDtoUtil().getEstadoComprobanteCerrado();
	}
	
	/**
	 * Devuelve la ruta del icono que representa el estado del documento..
	 */
	@DependsOn("dto.estadoComprobante")
	public String getIconoEstadoComprobante(MyPair estado) {
		String sigla = estado.getSigla();
		String siglaCbte = getDtoUtil().getEstadoComprobantePendiente()
				.getSigla();
		boolean pendiente = sigla.compareTo(siglaCbte) == 0;		
		return pendiente? Config.ICONO_EXCLAMACION_16X16 : Config.IMAGEN_OK;
	}
	
	/**
	 * @return los depositos segun la sucursal operativa..
	 */
	public List<MyPair> getDepositosSucursal() throws Exception {
		List<MyPair> out = new ArrayList<MyPair>();
		RegisterDomain rr = RegisterDomain.getInstance();
		List<Deposito> deps = rr.getDepositosPorSucursal(this.getSucursal().getId());
		for (Deposito deposito : deps) {
			MyPair dep = new MyPair(deposito.getId(), deposito.getDescripcion());
			out.add(dep);
		}
		return out;
	}
	
	/**
	 * @return los tipos de iva..
	 */
	public List<MyPair> getTiposDeIva() {
		List<MyPair> out = new ArrayList<MyPair>();
		for (MyArray iva : this.getDtoUtil().getTiposDeIva()) {
			MyPair iva_ = new MyPair(iva.getId(), (String) iva.getPos1());
			iva_.setSigla((String) iva.getPos2());
			out.add(iva_);
		}
		return out;
	}
	
	/**
	 * @return el tipo de iva 10..
	 */
	public MyPair getIva10() {
		MyArray iva10 =  this.getDtoUtil().getTipoIva10();
		MyPair out = new MyPair();
		out.setId(iva10.getId());
		out.setText((String) iva10.getPos1());
		out.setSigla((String) iva10.getPos2());
		return out;
	}
	
	/**
	 * @return las condiciones..
	 */
	public List<MyArray> getCondiciones() {
		return this.getDtoUtil().getCondicionesPago();
	}
	
	/**
	 * @return las listas de precio..
	 * pos1:descripcion
	 * pos2:margen
	 */
	public List<MyArray> getListasDePrecio() throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		List<ArticuloListaPrecio> precios = rr.getListasDePrecio();
		List<MyArray> out = new ArrayList<MyArray>();
		for (ArticuloListaPrecio precio : precios) {
			MyArray lprecio = new MyArray(precio.getDescripcion(),
					precio.getMargen());
			lprecio.setId(precio.getId());
			out.add(lprecio);
		}
		return out;
	}	
	
	/**
	 * mascara para los movimientos anulados..
	 */
	protected void enmascararAnulados(boolean enmascarar) {
		Window win = (Window) this.mainComponent;

		Vbox v = new Vbox();
		v.setHeight("100%");
		v.setWidth("100%");
		v.setPack("center");
		v.setAlign("center");

		Label l = new Label();
		l.setValue("ANULADO");
		l.setStyle("font-weight:bold;font-size:50pt");

		Div mask = new Div();
		mask.setId(ID_MASK_ANULADO);
		mask.setZclass("z-modal-mask");
		mask.setStyle("z-index: 2000; display: block; background:#ffcc99; opacity:0.2; filter:alpha(opacity=30); ");

		l.setParent(v);
		v.setParent(mask);

		if (enmascarar) {
			win.appendChild(mask);
		} else {
			Div dv = (Div) win.getFellowIfAny(ID_MASK_ANULADO);
			if (dv != null) {
				dv.setParent(null);
			}
		}
	}	
}
