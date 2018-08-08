package com.yhaguy.gestion.reparto.definiciones;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.coreweb.componente.VerificaAceptarCancelar;
import com.coreweb.componente.WindowPopup;
import com.coreweb.control.SimpleViewModel;
import com.coreweb.util.AutoNumeroControl;
import com.yhaguy.Configuracion;
import com.yhaguy.inicio.AccesoDTO;

public class RepartoDefinicionesVM extends SimpleViewModel {
	
	static final String PATH = "/yhaguy/gestion/reparto/insertarVehiculo.zul";
	static final String KEY_NRO = "VEH";

	private VehiculoDTO dto ;
	private VehiculoDTO selectedItem = new VehiculoDTO();
	private AssemblerVehiculo ass = new AssemblerVehiculo();
	private List<VehiculoDTO> listaVehiculos = new ArrayList<VehiculoDTO>();
	

	@Init(superclass = true)
	public void init() throws Exception {
		this.getVehiculos();
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	

	/**
	 * COMANDOS
	 */
	
	@Command
	@NotifyChange("*")
	public void abrirPopupVehiculo(@BindingParam("agregar") boolean agregar)
			throws Exception {
		String titulo = "";
		VehiculoDTO obj = new VehiculoDTO();
		if (agregar == true) {
			this.dto = new VehiculoDTO();
			titulo = "Nuevo Vehículo";
		} else {
			titulo = "Editar Vehículo";
			this.dto = this.selectedItem;
			obj = this.selectedItem;
		}
		WindowPopup wp = new WindowPopup();
		wp.setTitulo(titulo);
		wp.setModo(WindowPopup.NUEVO);
		wp.setCheckAC(new ValidadorAgregarVehiculo());
		wp.setDato(this);
		wp.setWidth("400px");
		wp.setHigth("400px");
		wp.show(PATH);
		if (wp.isClickAceptar() == true) {
			if (agregar == true) {
				dto.setSucursal(this.getAcceso().getSucursalOperativa());
				this.dto.setCodigo(AutoNumeroControl.getAutoNumeroKey(KEY_NRO, 4));
			} else {
				this.listaVehiculos.remove(obj);
			}
			this.getAss().saveVehiculo(this.dto);
			this.listaVehiculos.add(this.dto);
		}
	}
	
	
	/**
	 * FUNCIONES
	 */

	public void getVehiculos() throws Exception {
		this.listaVehiculos = this.ass.getVehiculosAss(this.getAcceso()
				.getSucursalOperativa());
	}
	

	/**
	 * VALIDACIONES
	 */
	
	/**
	 * validador de agregar vehiculo
	 */
	class ValidadorAgregarVehiculo implements VerificaAceptarCancelar {
		
		private String mensaje;

		@Override
		public boolean verificarAceptar() {
			boolean out = true;
			this.mensaje = "No se puede completar la operación debido a: \n";
			
			String marca = dto.getMarca();
			String modelo = dto.getModelo();
			String chapa = dto.getChapa();
			String color = dto.getColor();
			
			if (marca.isEmpty()) {
				out = false;
				this.mensaje += "\n - Debe ingresar la marca..";
			}
			if (modelo.isEmpty()) {
				this.mensaje += "\n - Debe agregar un modelo.. ";
				out = false;
			}
			if (chapa.isEmpty()) {
				this.mensaje += "\n - Debe agregar una chapa.. ";
				out = false;
			}
			if (color.isEmpty()) {
				this.mensaje += "\n - Debe agregar un color.. ";
				out = false;
			}
			
			return out;
		}

		@Override
		public String textoVerificarAceptar() {
			return this.mensaje;
		}

		@Override
		public boolean verificarCancelar() {
			return true;
		}

		@Override
		public String textoVerificarCancelar() {
			return "error al cancelar";
		}
		
	}
	

	/**
	 * GETS / SETS
	 */
	public AccesoDTO getAcceso() {
		Session s = Sessions.getCurrent();
		return (AccesoDTO) s.getAttribute(Configuracion.ACCESO);
	}

	public VehiculoDTO getDto() {
		return dto;
	}

	public void setDto(VehiculoDTO dto) {
		this.dto = dto;
	}

	public AssemblerVehiculo getAss() {
		return ass;
	}

	public void setAss(AssemblerVehiculo ass) {
		this.ass = ass;
	}

	public List<VehiculoDTO> getListaVehiculos() {
		return listaVehiculos;
	}

	public void setListaVehiculos(List<VehiculoDTO> listaVehiculos) {
		this.listaVehiculos = listaVehiculos;
	}

	public VehiculoDTO getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(VehiculoDTO selectedItem) {
		this.selectedItem = selectedItem;
	}
}
