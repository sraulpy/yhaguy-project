package com.yhaguy.gestion.compras.definiciones;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.AssemblerUtil;
import com.yhaguy.UtilDTO;

public class ComprasDefinicionesViewModel extends SimpleViewModel {

	AssemblerComprasDefiniciones assComprasDefiniciones = new AssemblerComprasDefiniciones();	
	private MyPair selectedMoneda = new MyPair();
	private MyArray tipoDeCambio;
	private MyArray tipoDeCambioAPP;
	private MyArray tipoDeCambioBCP;
	private boolean visible;
	private String mssj = "";
	private List<MyArray> listaTipoDeCambio;
	
	
	@Init(superclass = true)
	public void init() {
		this.inicializarTipoDeCambio();
		visible = false;
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose(){
	}
	
	public UtilDTO getUtil(){
		return (UtilDTO) this.getDtoUtil();
	}
	
	@Command 
	@NotifyChange("*")
	public void refreshlistaTipoDeCambio() throws Exception{
		this.listaTipoDeCambio = new ArrayList<MyArray>();
		this.listaTipoDeCambio = this.assComprasDefiniciones.refreshListaTipoDeCambioAss(this.selectedMoneda.getId());
		this.setVisible(true);
	}
	
	@SuppressWarnings("static-access")
	@Command
	@NotifyChange("*")
	public void saveTipoDeCambio() throws Exception{
		boolean refrescar= false;
		String fechaCambio = this.m.dateToString((Date) this.tipoDeCambio.getPos1(), this.m.YYYY_MM_DD);
		boolean existeTipoDeCamio= this.assComprasDefiniciones.hashTipoCambio.containsKey(fechaCambio);
		boolean existeCompraVentaBCP = this.verificarCompraVenta(this.tipoDeCambio.getPos2(),this.tipoDeCambio.getPos3());
		boolean existeCompraVentaAPP = this.verificarCompraVenta(this.tipoDeCambio.getPos4(),this.tipoDeCambio.getPos5());
		boolean continuarGuardarTC= true;
		
		if(!this.verificarCampos(fechaCambio, this.tipoDeCambio,existeCompraVentaBCP, existeCompraVentaAPP, existeTipoDeCamio)){
			continuarGuardarTC= this.mensajeAgregar(this.getMssj());
		}
		//graba tipos de cambio BCP si los campos fueron cargados en el .zul
		if(continuarGuardarTC && existeCompraVentaBCP){
			this.tipoDeCambioBCP = new MyArray();
			this.tipoDeCambioBCP.setPos1(this.getSelectedMoneda());
			this.tipoDeCambioBCP.setPos2(this.getUtil().getCambioBCP());
			this.tipoDeCambioBCP.setPos3(this.tipoDeCambio.getPos1());
			this.tipoDeCambioBCP.setPos4(this.tipoDeCambio.getPos2());
			this.tipoDeCambioBCP.setPos5(this.tipoDeCambio.getPos3());
			this.tipoDeCambioBCP.setPos6(fechaCambio);
			this.assComprasDefiniciones.saveTipoCambioAss(this.tipoDeCambioBCP);
			refrescar=true;
		}
		//graba tipos de cambio APP si los campos fueron cargados en el .zul
		if(continuarGuardarTC && existeCompraVentaAPP){
			this.tipoDeCambioAPP = new MyArray();
			this.tipoDeCambioAPP.setPos1(this.getSelectedMoneda());
			this.tipoDeCambioAPP.setPos2(this.getUtil().getCambioAPP());
			this.tipoDeCambioAPP.setPos3(this.tipoDeCambio.getPos1());
			this.tipoDeCambioAPP.setPos4(this.tipoDeCambio.getPos4());
			this.tipoDeCambioAPP.setPos5(this.tipoDeCambio.getPos5());
			this.tipoDeCambioAPP.setPos6(fechaCambio);
			this.assComprasDefiniciones.saveTipoCambioAss(this.tipoDeCambioAPP);
			refrescar = true;
		}
		if(refrescar){
			AssemblerUtil as = new AssemblerUtil();
			as.refrescaValoresTiposDeCambio(this.getUtil());
			this.refreshlistaTipoDeCambio();
			this.inicializarTipoDeCambio();
		}
	}
	
	/*
	 * metodo para inicializar el atributo que se muestra en el .zul
	 */
	public void inicializarTipoDeCambio(){
		this.tipoDeCambio = new MyArray();
		this.tipoDeCambio.setPos1(new Date());
		this.tipoDeCambio.setPos2(0.0);
		this.tipoDeCambio.setPos3(0.0);
		this.tipoDeCambio.setPos4(0.0);
		this.tipoDeCambio.setPos5(0.0);		
	}
	
	/**
	 * verificarCompraVenta retorna verdadero si se han cargado los campos de compra o venta.
	 * @param compra
	 * @param venta
	 * @return
	 */
	public boolean verificarCompraVenta(Object compra, Object venta){
		boolean cargar = false;

		if ((compra != null)&&(venta != null)){
			double compraDb = (double) compra;
			double ventaDb = (double) venta;
			
			if( (compraDb > 0 ) && (ventaDb > 0)){
				cargar = true;;
			}
		}
		return cargar;
	}
	
	/*
	 * verifica los datos cargados antes de grabar, retorna true si son todos válidos 
	 */
	public boolean verificarCampos(String fecha, MyArray tipoDeCambio,boolean existeCompraVentaBCP,boolean existeCompraVentaAPP,boolean existeTipoDeCamio){
		boolean cargar = true;
		this.setMssj("");
		
		if(!existeCompraVentaBCP){
			this.setMssj(this.getMssj() + "\n- Los campos en tipo cambio BCP están vacíos. No se grabarán!\n");
			cargar = false;
		} else if(((double) tipoDeCambio.getPos2()) <= 0){
			this.setMssj(this.getMssj() + "\n- El valor de la compra en BCP está en 0!\n");
			cargar = false;
		} else if(((double) tipoDeCambio.getPos3()) <= 0){
			this.setMssj(this.getMssj() + "\n- El valor de la venta en BCP está en 0!\n");
			cargar = false;
		} else if(((double) tipoDeCambio.getPos3()) < ((double) tipoDeCambio.getPos2())){
			this.setMssj(this.getMssj() + "\n- El valor de la venta es menor a la compra en BCP!\n");
			cargar = false;
		}		
		
		if(!existeCompraVentaAPP){
			this.setMssj(this.getMssj() + "\n- Los campos en tipo cambio Empresa están vacíos. No se grabarán!\n");
			cargar = false;
		} else if(((double) tipoDeCambio.getPos4()) <= 0){
			this.setMssj(this.getMssj() + "\n- El valor de la compra en Empresa está en 0!\n");
			cargar = false;
		} else if(((double) tipoDeCambio.getPos5()) <= 0){
			this.setMssj(this.getMssj() + "\n- El valor de la venta en Empresa está en 0!\n");
			cargar = false;
		} else if(((double) tipoDeCambio.getPos5()) < ((double) tipoDeCambio.getPos4())){
			this.setMssj(this.getMssj() + "\n- El valor de la venta es menor a la compra en Empresa!\n");
			cargar = false;
		}
		if(existeTipoDeCamio && !cargar){
			this.setMssj("Ya existe Tipo de Cambio para el día "+fecha+". Se remplazarán los valores."
					+ "\n\n Verifique la/as siguientes anotaciones\n"+this.getMssj());
		}else if(existeTipoDeCamio && cargar){
			this.setMssj("Ya existe Tipo de Cambio para el día "+fecha+". Se remplazarán los valores.");
			cargar = false;
		}
		this.setMssj(this.getMssj() + "\n\nDesea continuar?\n");
		
		return cargar;
	}
		
	
	/**
	 * get y set
	 * @return
	 */
	public MyPair getSelectedMoneda() {
		return selectedMoneda;
	}

	public void setSelectedMoneda(MyPair selectedMoneda) {
		this.selectedMoneda = selectedMoneda;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getMssj() {
		return mssj;
	}

	public void setMssj(String mssj) {
		this.mssj = mssj;
	}

	public List<MyArray> getListaTipoDeCambio() {
		return listaTipoDeCambio;
	}

	public void setListaTipoDeCambio(List<MyArray> listaTipoDeCambio) {
		this.listaTipoDeCambio = listaTipoDeCambio;
	}

	public MyArray getTipoDeCambioAPP() {
		return tipoDeCambioAPP;
	}

	public void setTipoDeCambioAPP(MyArray tipoDeCambioAPP) {
		this.tipoDeCambioAPP = tipoDeCambioAPP;
	}

	public MyArray getTipoDeCambioBCP() {
		return tipoDeCambioBCP;
	}

	public void setTipoDeCambioBCP(MyArray tipoDeCambioBCP) {
		this.tipoDeCambioBCP = tipoDeCambioBCP;
	}

	public MyArray getTipoDeCambio() {
		return tipoDeCambio;
	}

	public void setTipoDeCambio(MyArray tipoDeCambio) {
		this.tipoDeCambio = tipoDeCambio;
	}	
}
