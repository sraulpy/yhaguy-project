package com.yhaguy.gestion.bancos.debitos;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.extras.browser.Browser;
import com.yhaguy.BodyApp;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoDebito;
import com.yhaguy.gestion.bancos.libro.AssemblerBancoCtaCte;
import com.yhaguy.gestion.bancos.libro.BancoCtaDTO;
import com.yhaguy.gestion.bancos.libro.ControlBancoMovimiento;

public class BancoDebitoViewModel extends BodyApp {
	
	private BancoDebitoDTO dto;
	private String mensajeError = "";

	@Init(superclass = true)
	public void init() {
	}
	
	@AfterCompose(superclass = true)
	public void afterCompose() {
	}
	
	@Override
	public boolean verificarAlGrabar() {
		return this.validarDatos();
	}

	@Override
	public String textoErrorVerificarGrabar() {
		return this.mensajeError;
	}

	@Override
	public Assembler getAss() {
		return new BancoDebitoAssembler();
	}

	@Override
	public DTO getDTOCorriente() {
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (BancoDebitoDTO) dto;
	}

	@Override
	public DTO nuevoDTO() throws Exception {
		BancoDebitoDTO nDto = new BancoDebitoDTO();
		this.sugerirValores(nDto);
		return nDto;
	}

	@Override
	public String getEntidadPrincipal() {
		return BancoDebito.class.getName();
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		return this.getAllDTOs(this.getEntidadPrincipal());
	}
	
	@Override
	public Browser getBrowser() {
		return new BancoDebitoBrowser();
	}
	
	
	/**
	 * ----- COMANDOS -----
	 */
	
	@Command
	@NotifyChange("*")
	public void confirmar() throws Exception {
		if (this.mensajeSiNo("Desea confirmar el registro..")) {
			this.confirmarDebito(this.dto);
		}
	}
	
	
	/**
	 * ----- FUNCIONES -----
	 */

	/**
	 * valores por defecto..
	 */
	private void sugerirValores(BancoDebitoDTO dto) {
		dto.setSucursal(this.getSucursal());
	}
	
	/**
	 * confirmar debito..
	 */
	private void confirmarDebito(BancoDebitoDTO dto) throws Exception {		
		dto.setConfirmado(true);
		dto.setReadonly();
		this.dto = (BancoDebitoDTO) this.saveDTO(dto);
		this.setEstadoABMConsulta();
		ControlBancoMovimiento.addMovimientoDebitoBancario(dto, this.getLoginNombre());
		Clients.showNotification("Debito Bancario confirmado..");
	}
	
	/**
	 * @return validacion de datos..
	 */
	private boolean validarDatos() {
		boolean out = true;
		this.mensajeError = "No se puede completar la operación debido a:";
		
		if (this.dto.getNumero().isEmpty()) {
			out = false;
			this.mensajeError += "\n - Debe ingresar el número..";
		}
		
		if (this.dto.getFecha() == null) {
			out = false;
			this.mensajeError += "\n - Debe ingresar la fecha..";
		}
		
		if (this.dto.getDescripcion().isEmpty()) {
			out = false;
			this.mensajeError += "\n - Debe ingresar la descripción..";
		}
		
		if (this.dto.getImporte() <= 0) {
			out = false;
			this.mensajeError += "\n - El importe debe ser mayor a cero..";
		}
		
		return out;
	}
	
	/**
	 * ----- GETS / SETS -----
	 */
	public List<BancoCtaDTO> getBancos() throws Exception {
		List<BancoCtaDTO> out = new ArrayList<BancoCtaDTO>();
		for (DTO dto : this.getAllDTOs(BancoCta.class.getName(), new AssemblerBancoCtaCte())) {
			BancoCtaDTO bdto = (BancoCtaDTO) dto;
			out.add(bdto);
		}
		return out;
	}
	
	@DependsOn({ "deshabilitado", "dto" })
	public boolean isConfirmarDisabled() {
		return this.isDeshabilitado() || this.dto.esNuevo();
	}
	
	public BancoDebitoDTO getDto() {
		return dto;
	}

	public void setDto(BancoDebitoDTO dto) {
		this.dto = dto;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}
}
