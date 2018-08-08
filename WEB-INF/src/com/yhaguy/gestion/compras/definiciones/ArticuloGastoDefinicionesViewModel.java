package com.yhaguy.gestion.compras.definiciones;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;

public class ArticuloGastoDefinicionesViewModel extends SimpleViewModel {

	@Init(superclass = true)
	public void init() throws Exception {
		this.refreshArticulosGastos();
		this.inicializarArticuloGasto();

	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	private AssemblerArticuloGastoDefiniciones assArticulosGastosDef = new AssemblerArticuloGastoDefiniciones();
	private List<MyArray> listaArticulosGastos;
	private String mensaje;
	private MyArray selectArticuloGasto = null;
	private String tituloPopap;
	private boolean agregar;
	private boolean editar;

	public MyArray articuloGasto;
	public MyArray ctaContable = new MyArray();
	

	@Command
	@NotifyChange("*")
	public void abrirPopupAarticulosGastos(
			@BindingParam("agregar") boolean agregar,
			@BindingParam("editar") boolean editar) throws Exception {

		boolean guardarAG = true;
		String pathPopapZul = "/yhaguy/gestion/compras/definicionesArticuloGastoPopup.zul";
		this.setAgregar(false);
		this.setEditar(false);

		if (agregar) {
			this.setTituloPopap("Agregar Nuevo Articulo Gasto");
			this.setAgregar(true);
		} else {
			if (this.selectArticuloGasto == null) {
				guardarAG = false;
				this.mensajeError("Seleccione un item!");
			} else {
				this.setTituloPopap("Modificar Articulo Gasto");
				this.setEditar(true);
			}
		}
		WindowPopup wp = new WindowPopup();
		wp.setTitulo(this.getTituloPopap());
		wp.setModo(WindowPopup.NUEVO);
		wp.setDato(this);
		wp.setWidth("400px");
		wp.setHigth("300px");
		while (guardarAG) {
			wp.show(pathPopapZul);
			if (wp.isClickAceptar()) {				
				if(agregar){
					if (this.verificarCamposArticuloGasto(this.getArticuloGasto())) {
						this.assArticulosGastosDef.saveArticuloGastoASS(this
								.getArticuloGasto(), true);
						this.refreshArticulosGastos();
						guardarAG = false;
					} else {
						this.mensajeError(this.getMensaje());
					}
				}else{
					if(this.verificarCamposArticuloGasto(this.getSelectArticuloGasto())){
						this.assArticulosGastosDef.saveArticuloGastoASS(this.getSelectArticuloGasto(), false);
						this.refreshArticulosGastos();
						guardarAG = false;
					}else{
						this.mensajeError(this.getMensaje());
					}
				}				
			} else if (editar)  {				
				this.refreshArticulosGastos();
				guardarAG = false;
			} else {
				guardarAG = false;
			}
		}
		this.inicializarArticuloGasto();
	}

	public boolean verificarCamposArticuloGasto(MyArray articuloGasto) {
		this.setMensaje("");
		boolean cargar = true;
		if (articuloGasto.getPos1().toString().equals("")) {
			this.setMensaje("\n- El campo del Nombre está vacío.\n");
			cargar = false;
		}
		if (articuloGasto.getPos4().toString().equals("")) {
			this.setMensaje(this.getMensaje()
					+ "\n- El campo de Tipo de IVA está vacío.\n");
			cargar = false;
		}
		if (articuloGasto.getPos5().toString().equals("")) {
			this.setMensaje(this.getMensaje()
					+ "\n- El campo de Cta. Contable está vacío.\n");
			cargar = false;
		}
		if (!cargar) {
			this.setMensaje("Verifique la/as siguientes anotaciones:\n"
					+ this.getMensaje() + "\n");
		}
		return cargar;
	}

	/*
	 * resfrescar lista de articulos gastos
	 */
	public void refreshArticulosGastos() throws Exception {
		this.listaArticulosGastos = new ArrayList<MyArray>();
		this.listaArticulosGastos = this.assArticulosGastosDef.getArticulosGastosAss();
		this.setSelectArticuloGasto(null);

	}

	/*
	 * inicializar articulo gasto
	 */
	public void inicializarArticuloGasto() {
		this.articuloGasto = new MyArray();
		this.articuloGasto.setPos10("");
		this.articuloGasto.setPos2(this.getLoginNombre());
		this.articuloGasto.setPos3("");
		this.ctaContable = new MyArray();
		//this.articuloGasto.setPos5(new MyArray());
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@Command
	@NotifyChange("*")
	public void verificarArticuloGastoON(
			@BindingParam("artGasto") MyArray artGasto) throws Exception {
		artGasto.setPos3(this.getLoginNombre());
		this.assArticulosGastosDef.saveArticuloGastoASS(artGasto, false);
		this.refreshArticulosGastos();
	}

	// get y set //
	public List<MyArray> getListaArticulosGastos() {
		return listaArticulosGastos;
	}

	public void setListaArticulosGastos(List<MyArray> listaArticulosGastos) {
		this.listaArticulosGastos = listaArticulosGastos;
	}

	public MyArray getArticuloGasto() {
		return articuloGasto;
	}

	public void setArticuloGasto(MyArray articuloGasto) {
		this.articuloGasto = articuloGasto;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public MyArray getSelectArticuloGasto() {
		return selectArticuloGasto;
	}

	public void setSelectArticuloGasto(MyArray selectArticuloGasto) {
		this.selectArticuloGasto = selectArticuloGasto;
	}

	public String getTituloPopap() {
		return tituloPopap;
	}

	public void setTituloPopap(String tituloPopap) {
		this.tituloPopap = tituloPopap;
	}

	public boolean isAgregar() {
		return agregar;
	}

	public void setAgregar(boolean agregar) {
		this.agregar = agregar;
	}

	public boolean isEditar() {
		return editar;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}
	
	public MyArray getCtaContable() {
		return ctaContable;
	}

	public void setCtaContable(MyArray ctaContable) {
		this.ctaContable = ctaContable;
	}

}
