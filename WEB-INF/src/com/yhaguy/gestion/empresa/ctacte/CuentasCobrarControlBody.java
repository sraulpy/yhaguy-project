package com.yhaguy.gestion.empresa.ctacte;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Tab;

import com.coreweb.domain.IiD;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.gestion.empresa.AssemblerEmpresa;
import com.yhaguy.gestion.empresa.EmpresaControlBody;
import com.yhaguy.gestion.empresa.EmpresaDTO;

public class CuentasCobrarControlBody extends EmpresaControlBody {

	@Wire
	Tab tb2;

	private String razonSocial = "Ninguno.";
	private MyArray cuentaClienteSeleccionada = null;
	private List<MyArray> listadoCuentasClientes = new ArrayList<MyArray>();

	@Init(superclass = true)
	public void init() throws Exception {

		this.setEstadoABMConsulta();
		this.ctasCobrar = true;
		this.listadoCuentasClientes = this.getControlCtaCte().getListadoCuentasClientes();
	}

	@AfterCompose(superclass = true)
	public void afterCompose() {
	}

	@Override
	public boolean verificarAlGrabar() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Assembler getAss() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DTO getDTOCorriente() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		// TODO Auto-generated method stub

	}

	@Override
	public DTO nuevoDTO() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntidadPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DTO> getAllModel() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public MyArray getCuentaClienteSeleccionada() {
		return cuentaClienteSeleccionada;
	}

	public void setCuentaClienteSeleccionada(MyArray cuentaClienteSeleccionada) {
		this.cuentaClienteSeleccionada = cuentaClienteSeleccionada;
	}

	public List<MyArray> getListadoCuentasClientes() {
		return listadoCuentasClientes;
	}

	public void setListadoCuentasClientes(List<MyArray> listadoCuentasClientes) {
		this.listadoCuentasClientes = listadoCuentasClientes;
	}

	@Command
	@NotifyChange("listadoCuentasClientes")
	public void actualizarListadoCuentasClientes() throws Exception {
		this.listadoCuentasClientes = this.getControlCtaCte().getListadoCuentasClientes();
	}

	@Command
	@NotifyChange({ "selectedMovimientos", "infoCtaCteDisable", "dtoEmp", "buscarPorFechaDisable", "movimientos",
			"fechaDesde", "fechaHasta", "buscarPorFecha", "saldoSuma", "infoBusqueda", "sucursalSeleccionada",
			"movimientosSeleccionados", "razonSocial" })
	public void verMovimientos(@BindingParam("seleccionado") MyArray seleccionado) throws Exception {

		this.cuentaClienteSeleccionada = seleccionado;
		RegisterDomain rr = RegisterDomain.getInstance();
		Empresa dom = (Empresa) rr.getEmpresaById((long) this.cuentaClienteSeleccionada.getPos1());
		this.setDtoEmp((EmpresaDTO) (new AssemblerEmpresa()).domainToDto(dom));
		this.razonSocial = this.getDtoEmp().getRazonSocial();
		this.restoreDefaultValuesOfCtaCte();
		this.tb2.setSelected(true);

	}

	@Override
	public boolean getImprimirDeshabilitado() {
		return false;
	}

}
