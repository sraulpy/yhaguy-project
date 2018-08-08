package com.yhaguy.gestion.configuracion;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import com.coreweb.control.Control;
import com.coreweb.dto.Assembler;
import com.coreweb.dto.DTO;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.AssemblerUtil;
import com.yhaguy.BodyApp;
import com.yhaguy.Configuracion;
import com.yhaguy.UtilDTO;

public class ConfiguracionControlBody extends BodyApp {

	private String tab = "all";

	@Init(superclass = true)
	public void initConfiguracionControlBody(@ExecutionParam("tab") String tab) {
		this.dto = this.getDtoUtil();
		if (tab != null) {
			this.tab = tab;
			this.setTextoFormularioCorriente("Definiciones [" + this.tab + "]");
		}
	}

	@AfterCompose(superclass = true)
	public void afterComposeConfiguracionControlBody() {
	}

	UtilDTO dto = null;

	@SuppressWarnings("static-access")
	public void afterSave() {
		Control.setInicialDtoUtil(new AssemblerUtil().getDTOUtil());
	}

	public UtilDTO getDto() {
		return dto;
	}

	public void setDto(UtilDTO dto) {
		this.dto = dto;
	}

	private List<MyArray> nuevosMA = new ArrayList<MyArray>();
	private List<MyPair> nuevosMP = new ArrayList<MyPair>();

	@Override
	public Assembler getAss() {
		// TODO Auto-generated method stub
		return new AssemblerUtil();
	}

	@Override
	public DTO getDTOCorriente() {
		// TODO Auto-generated method stub
		return this.dto;
	}

	@Override
	public void setDTOCorriente(DTO dto) {
		this.dto = (UtilDTO) dto;

	}

	@Override
	public DTO nuevoDTO() throws Exception {
		// TODO Auto-generated method stub
		return (UtilDTO) AssemblerUtil.getDTOUtil();
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

	// =========== rubro empresa ================================

	private MyPair selectedRubroEmpresa = null;

	public MyPair getSelectedRubroEmpresa() {
		return selectedRubroEmpresa;
	}

	public void setSelectedRubroEmpresa(MyPair rub) {
		this.selectedRubroEmpresa = rub;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarRubroEmpresa() {
		if (this.selectedRubroEmpresa != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el rubro?")) {
				// verificar que no este asociado a ningun objeto

				this.getDto().getRubroEmpresa()
						.remove(this.selectedRubroEmpresa);
			}
			this.nuevosMP.remove(this.selectedRubroEmpresa);
			this.setSelectedRubroEmpresa(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarRubroEmpresa() {

		if (mensajeAgregar("Agregar un nuevo rubro?")) {
			MyPair nRub = new MyPair();
			nRub.setText("--editar--");
			nRub.setSigla(Configuracion.ID_TIPO_RUBRO_EMPRESAS); // deberia ser
																	// sigla aca
			this.getDto().getRubroEmpresa().add(nRub);
			this.setSelectedRubroEmpresa(nRub);
			this.nuevosMP.add(this.selectedRubroEmpresa);
		}
	}

	// =========== tipo de contacto interno empresa ======================

	private MyPair selectedTipoContactoInterno = null;

	public MyPair getSelectedTipoContactoInterno() {
		return selectedTipoContactoInterno;
	}

	public void setSelectedTipoContactoInterno(
			MyPair selectedTipoContactoInterno) {
		this.selectedTipoContactoInterno = selectedTipoContactoInterno;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarTipoContactoInterno() {
		if (this.selectedTipoContactoInterno != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el tipo de contacto?")) {
				// verificar que no este asociado a ningun objeto

				this.getDto().getTipoContactoInterno()
						.remove(this.selectedTipoContactoInterno);
			}
			this.nuevosMP.remove(this.selectedTipoContactoInterno);
			this.setSelectedTipoContactoInterno(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarTipoContactoInterno() {

		if (mensajeAgregar("Agregar un nuevo tipo de contacto interno?")) {
			MyPair nTipo = new MyPair();
			nTipo.setText("--editar--");
			nTipo.setSigla(Configuracion.SIGLA_TIPO_CONTACTO_INTERNO);
			this.getDto().getTipoContactoInterno().add(nTipo);
			this.setSelectedTipoContactoInterno(nTipo);
			this.nuevosMP.add(this.selectedTipoContactoInterno);
		}
	}

	// =========== categoria cliente ================================

	private MyPair selectedCatCliente = null;

	public MyPair getSelectedCatCliente() {
		return selectedCatCliente;
	}

	public void setSelectedCatCliente(MyPair cat) {
		this.selectedCatCliente = cat;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarCatCliente() {
		if (this.selectedCatCliente != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la categoría?")) {
				this.getDto().getCategoriaCliente()
						.remove(this.selectedCatCliente);
			}
			this.nuevosMP.remove(this.selectedCatCliente);
			this.setSelectedCatCliente(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarCatCliente() {

		if (mensajeAgregar("Agregar una nueva categoría?")) {
			MyPair nCat = new MyPair();
			nCat.setText("--editar--");
			nCat.setSigla(Configuracion.SIGLA_CATEGORIA_CLIENTE);
			this.getDto().getCategoriaCliente().add(nCat);
			this.setSelectedCatCliente(nCat);
			this.nuevosMP.add(this.selectedCatCliente);
		}
	}

	// =========== tipo cliente ================================

	private MyPair selectedTipoCliente = null;

	public MyPair getSelectedTipoCliente() {
		return selectedTipoCliente;
	}

	public void setSelectedTipoCliente(MyPair tipo) {
		this.selectedTipoCliente = tipo;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarTipoCliente() {
		if (this.selectedTipoCliente != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el tipo de cliente?")) {
				this.getDto().getTipoCliente().remove(this.selectedTipoCliente);
			}
			this.nuevosMP.remove(this.selectedTipoCliente);
			this.setSelectedTipoCliente(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarTipoCliente() {

		if (mensajeAgregar("Agregar un nuevo tipo de cliente?")) {
			MyPair nTipo = new MyPair();
			nTipo.setText("--editar--");
			this.getDto().getTipoCliente().add(nTipo);
			this.setSelectedTipoCliente(nTipo);
			this.nuevosMP.add(this.selectedTipoCliente);
		}
	}

	// =========== estado cliente ================================

	private MyPair selectedEstadoCliente = null;

	public MyPair getSelectedEstadoCliente() {
		return selectedEstadoCliente;
	}

	public void setSelectedEstadoCliente(MyPair estado) {
		this.selectedEstadoCliente = estado;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarEstadoCliente() {
		if (this.selectedEstadoCliente != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el estado?")) {
				this.getDto().getEstadoCliente()
						.remove(this.selectedEstadoCliente);
			}
			this.nuevosMP.remove(this.selectedEstadoCliente);
			this.setSelectedEstadoCliente(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarEstadoCliente() {

		if (mensajeAgregar("Agregar un nuevo estado?")) {
			MyPair nEstado = new MyPair();
			nEstado.setText("--editar--");
			nEstado.setSigla(Configuracion.SIGLA_ESTADO_CLIENTE);
			this.getDto().getEstadoCliente().add(nEstado);
			this.setSelectedEstadoCliente(nEstado);
			this.nuevosMP.add(this.selectedEstadoCliente);
		}
	}

	// =========== articulo familia ================================

	private MyPair selectedArticuloFamilia = null;

	public MyPair getSelectedArticuloFamilia() {
		return selectedArticuloFamilia;
	}

	public void setSelectedArticuloFamilia(MyPair selectedArticuloFamilia) {
		this.selectedArticuloFamilia = selectedArticuloFamilia;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloFamilia() {
		if (this.selectedArticuloFamilia != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la familia?")) {
				this.getDto().getArticuloFamilia()
						.remove(this.selectedArticuloFamilia);
			}
			this.nuevosMP.remove(this.selectedArticuloFamilia);
			this.setSelectedArticuloFamilia(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloFamilia() {

		if (mensajeAgregar("Agregar una nueva familia?")) {
			MyPair nFamilia = new MyPair();
			nFamilia.setText("--editar--");
			nFamilia.setSigla(Configuracion.SIGLA_ARTICULO_FAMILIA);
			this.getDto().getArticuloFamilia().add(nFamilia);
			this.setSelectedArticuloFamilia(nFamilia);
			this.nuevosMP.add(this.selectedArticuloFamilia);
		}
	}

	// =========== articulo linea ================================

	private MyPair selectedArticuloLinea = null;

	public MyPair getSelectedArticuloLinea() {
		return selectedArticuloLinea;
	}

	public void setSelectedArticuloLinea(MyPair selectedArticuloLinea) {
		this.selectedArticuloLinea = selectedArticuloLinea;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloLinea() {
		if (this.selectedArticuloLinea != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la linea?")) {
				this.getDto().getArticuloLinea()
						.remove(this.selectedArticuloLinea);
			}
			this.nuevosMP.remove(this.selectedArticuloLinea);
			this.setSelectedArticuloLinea(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloLinea() {

		if (mensajeAgregar("Agregar una nueva linea?")) {
			MyPair nLinea = new MyPair();
			nLinea.setText("--editar--");
			nLinea.setSigla(Configuracion.SIGLA_ARTICULO_LINEA);
			this.getDto().getArticuloLinea().add(nLinea);
			this.setSelectedArticuloLinea(nLinea);
			this.nuevosMP.add(this.selectedArticuloLinea);
		}
	}

	// =========== articulo marca ================================

	private MyPair selectedArticuloMarca = null;

	public MyPair getSelectedArticuloMarca() {
		return selectedArticuloMarca;
	}

	public void setSelectedArticuloMarca(MyPair selectedArticuloMarca) {
		this.selectedArticuloMarca = selectedArticuloMarca;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloMarca() {
		if (this.selectedArticuloMarca != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la marca?")) {
				this.getDto().getArticuloMarca()
						.remove(this.selectedArticuloMarca);
			}
			this.nuevosMP.remove(this.selectedArticuloMarca);
			this.setSelectedArticuloMarca(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloMarca() {

		if (mensajeAgregar("Agregar una nueva marca?")) {
			MyPair nMarca = new MyPair();
			nMarca.setText("--editar--");
			nMarca.setSigla(Configuracion.SIGLA_ARTICULO_MARCA);
			this.getDto().getArticuloMarca().add(nMarca);
			this.setSelectedArticuloMarca(nMarca);
			this.nuevosMP.add(this.selectedArticuloMarca);
		}
	}

	// =========== articulo parte ================================

	private MyPair selectedArticuloParte = null;

	public MyPair getSelectedArticuloParte() {
		return selectedArticuloParte;
	}

	public void setSelectedArticuloParte(MyPair selectedArticuloParte) {
		this.selectedArticuloParte = selectedArticuloParte;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloParte() {
		if (this.selectedArticuloParte != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la parte?")) {
				this.getDto().getArticuloParte()
						.remove(this.selectedArticuloParte);
			}
			this.nuevosMP.remove(this.selectedArticuloParte);
			this.setSelectedArticuloParte(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloParte() {

		if (mensajeAgregar("Agregar una nueva parte?")) {
			MyPair nParte = new MyPair();
			nParte.setText("--editar--");
			nParte.setSigla(Configuracion.SIGLA_ARTICULO_PARTE);
			this.getDto().getArticuloParte().add(nParte);
			this.setSelectedArticuloParte(nParte);
			this.nuevosMP.add(this.selectedArticuloParte);
		}
	}

	// =========== articulo unidad medida ================================

	private MyPair selectedArticuloUnidadMedida = null;

	public MyPair getSelectedArticuloUnidadMedida() {
		return selectedArticuloUnidadMedida;
	}

	public void setSelectedArticuloUnidadMedida(
			MyPair selectedArticuloUnidadMedida) {
		this.selectedArticuloUnidadMedida = selectedArticuloUnidadMedida;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloUnidadMedida() {
		if (this.selectedArticuloUnidadMedida != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la unidad de medida?")) {
				this.getDto().getArticuloUnidadMedida()
						.remove(this.selectedArticuloUnidadMedida);
			}
			this.nuevosMP.remove(this.selectedArticuloUnidadMedida);
			this.setSelectedArticuloUnidadMedida(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloUnidadMedida() {

		if (mensajeAgregar("Agregar una nueva unidad de medida?")) {
			MyPair nUnidad = new MyPair();
			nUnidad.setText("--editar--");
			nUnidad.setSigla(Configuracion.SIGLA_ARTICULO_UNID_MED);
			this.getDto().getArticuloUnidadMedida().add(nUnidad);
			this.setSelectedArticuloUnidadMedida(nUnidad);
			this.nuevosMP.add(this.selectedArticuloUnidadMedida);
		}
	}

	// =========== articulo estado ================================

	private MyPair selectedArticuloEstado = null;

	public MyPair getSelectedArticuloEstado() {
		return selectedArticuloEstado;
	}

	public void setSelectedArticuloEstado(MyPair selectedArticuloEstado) {
		this.selectedArticuloEstado = selectedArticuloEstado;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloEstado() {
		if (this.selectedArticuloEstado != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el estado?")) {
				this.getDto().getArticuloEstado()
						.remove(this.selectedArticuloEstado);
			}
			this.nuevosMP.remove(this.selectedArticuloEstado);
			this.setSelectedArticuloEstado(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloEstado() {

		if (mensajeAgregar("Agregar un nuevo estado?")) {
			MyPair nEstado = new MyPair();
			nEstado.setText("--editar--");
			nEstado.setSigla("");
			this.getDto().getArticuloEstado().add(nEstado);
			this.setSelectedArticuloEstado(nEstado);
			this.nuevosMP.add(this.selectedArticuloEstado);
		}
	}

	// =========== articulo marca y modelo aplicacion ======================

	private MyArray selectedArticuloMarcaAplicacion = null;

	public MyArray getSelectedArticuloMarcaAplicacion() {
		return selectedArticuloMarcaAplicacion;
	}

	public void setSelectedArticuloMarcaAplicacion(
			MyArray selectedArticuloMarcaAplicacion) {
		this.selectedArticuloMarcaAplicacion = selectedArticuloMarcaAplicacion;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloMarcaAplicacion() {
		if (this.selectedArticuloMarcaAplicacion != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la marca y todos sus modelos?")) {
				this.getDto().getArticuloMarcaAplicacion()
						.remove(this.selectedArticuloMarcaAplicacion);
			}
			this.setSelectedArticuloMarcaAplicacion(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloMarcaAplicacion() {

		if (mensajeAgregar("Agregar una nueva marca?")) {
			MyArray nMarcaA = new MyArray();
			nMarcaA.setPos1("--editar--");

			// como la marca es nueva no tiene ningun modelo
			nMarcaA.setPos2(new ArrayList<MyPair>());

			this.getDto().getArticuloMarcaAplicacion().add(nMarcaA);
			this.setSelectedArticuloMarcaAplicacion(nMarcaA);
		}
	}

	// ===================================================================
	private MyPair selectedArticuloModeloAplicacion = null;

	public MyPair getSelectedArticuloModeloAplicacion() {
		return selectedArticuloModeloAplicacion;
	}

	public void setSelectedArticuloModeloAplicacion(
			MyPair selectedArticuloModeloAplicacion) {
		this.selectedArticuloModeloAplicacion = selectedArticuloModeloAplicacion;
	}

	@SuppressWarnings("rawtypes")
	@Command()
	@NotifyChange("*")
	public void eliminarArticuloModeloAplicacion() {
		if (selectedArticuloMarcaAplicacion.getPos2() != null) {
			if (mensajeEliminar("Está seguro que quiere eliminar el modelo?")) {
				((List) this.selectedArticuloMarcaAplicacion.getPos2())
						.remove(selectedArticuloModeloAplicacion);

				System.out.println("------------------- ID ("
						+ selectedArticuloModeloAplicacion.getId() + " - "
						+ selectedArticuloModeloAplicacion.getText() + ")");

				((UtilDTO) this.getDtoUtil()).getArticuloModeloAplicacion()
						.remove(selectedArticuloModeloAplicacion);

			}
			this.setSelectedArticuloModeloAplicacion(null);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command()
	@NotifyChange("*")
	public void agregarArticuloModeloAplicacion() {
		if (mensajeAgregar("Agregar un nuevo modelo?")) {
			MyPair nModeloA = new MyPair();
			nModeloA.setText("--editar--");
			((List) this.selectedArticuloMarcaAplicacion.getPos2())
					.add(nModeloA);
			this.setSelectedArticuloModeloAplicacion(nModeloA);

			MyArray nModeloA_aux = new MyArray();
			nModeloA_aux.setId(nModeloA.getId());
			((UtilDTO) this.getDtoUtil()).getArticuloModeloAplicacion().add(
					nModeloA_aux);

		}
	}

	// =========== articulo presentacion================================

	private MyArray selectedArticuloPresentacion = null;

	public MyArray getSelectedArticuloPresentacion() {
		return selectedArticuloPresentacion;
	}

	public void setSelectedArticuloPresentacion(
			MyArray selectedArticuloPresentacion) {
		this.selectedArticuloPresentacion = selectedArticuloPresentacion;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarArticuloPresentacion() {
		if (this.selectedArticuloPresentacion != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la presestacion?")) {
				this.getDto().getArticuloPresentacion()
						.remove(this.selectedArticuloPresentacion);
			}
			this.nuevosMA.remove(this.selectedArticuloPresentacion);
			this.setSelectedArticuloPresentacion(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarArticuloPresentacion() {

		if (mensajeAgregar("Agregar una nueva presentacion?")) {
			MyArray nPresentacion = new MyArray();
			nPresentacion.setPos1("--editar--");
			this.getDto().getArticuloPresentacion().add(nPresentacion);
			this.setSelectedArticuloPresentacion(nPresentacion);
			this.nuevosMA.add(this.selectedArticuloPresentacion);
		}
	}

	// =========== tipo proveedor ================================

	private MyPair selectedProveedorTipo = null;

	public MyPair getSelectedProveedorTipo() {
		return selectedProveedorTipo;
	}

	public void setSelectedProveedorTipo(MyPair selectedProveedorTipo) {
		this.selectedProveedorTipo = selectedProveedorTipo;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarProveedorTipo() {
		if (this.selectedProveedorTipo != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el tipo de proveedor?")) {
				this.getDto().getProveedorTipo()
						.remove(this.selectedProveedorTipo);
			}
			this.nuevosMP.remove(this.selectedProveedorTipo);
			this.setSelectedProveedorTipo(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarProveedorTipo() {

		if (mensajeAgregar("Agregar un nuevo tipo de proveedor?")) {
			MyPair nTipo = new MyPair();
			nTipo.setText("--editar--");
			nTipo.setSigla("");
			this.getDto().getProveedorTipo().add(nTipo);
			this.setSelectedProveedorTipo(nTipo);
			this.nuevosMP.add(this.selectedProveedorTipo);
		}
	}

	// =========== tipo de pago proveedor ================================

	private MyPair selectedProveedorTipoPago = null;

	public MyPair getSelectedProveedorTipoPago() {
		return selectedProveedorTipoPago;
	}

	public void setSelectedProveedorTipoPago(MyPair selectedProveedorTipoPago) {
		this.selectedProveedorTipoPago = selectedProveedorTipoPago;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarProveedorTipoPago() {
		if (this.selectedProveedorTipoPago != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el tipo de pago?")) {
				this.getDto().getProveedorTipoPago()
						.remove(this.selectedProveedorTipoPago);
			}
			this.nuevosMP.remove(this.selectedProveedorTipoPago);
			this.setSelectedProveedorTipoPago(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarProveedorTipoPago() {

		if (mensajeAgregar("Agregar un nuevo tipo de pago?")) {
			MyPair nPago = new MyPair();
			nPago.setText("--editar--");
			nPago.setSigla("");
			this.getDto().getProveedorTipoPago().add(nPago);
			this.setSelectedProveedorTipoPago(nPago);
			this.nuevosMP.add(this.selectedProveedorTipoPago);
		}
	}

	// =========== estado proveedor ================================

	private MyPair selectedProveedorEstado = null;

	public MyPair getSelectedProveedorEstado() {
		return selectedProveedorEstado;
	}

	public void setSelectedProveedorEstado(MyPair selectedProveedorEstado) {
		this.selectedProveedorEstado = selectedProveedorEstado;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarProveedorEstado() {
		if (this.selectedProveedorEstado != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el estado?")) {
				this.getDto().getProveedorEstado()
						.remove(this.selectedProveedorEstado);
			}
			this.nuevosMP.remove(this.selectedProveedorEstado);
			this.setSelectedProveedorEstado(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarProveedorEstado() {

		if (mensajeAgregar("Agregar un nuevo estado?")) {
			MyPair nEstado = new MyPair();
			nEstado.setText("--editar--");
			nEstado.setSigla("");
			this.getDto().getProveedorEstado().add(nEstado);
			this.setSelectedProveedorEstado(nEstado);
			this.nuevosMP.add(this.selectedProveedorEstado);
		}
	}

	// =========== condicion pago proveedor ================================

	private MyArray selectedProveedorCondicionPago = null;

	public MyArray getSelectedProveedorCondicionPago() {
		return selectedProveedorCondicionPago;
	}

	public void setSelectedProveedorCondicionPago(
			MyArray selectedProveedorCondicionPago) {
		this.selectedProveedorCondicionPago = selectedProveedorCondicionPago;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarProveedorCondicionPago() {
		if (this.selectedProveedorCondicionPago != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la condición de pago?")) {
				this.getDto().getCondicionesPago()
						.remove(this.selectedProveedorCondicionPago);
			}
			this.nuevosMA.remove(this.selectedProveedorCondicionPago);
			this.setSelectedProveedorCondicionPago(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarProveedorCondicionPago() {

		if (mensajeAgregar("Agregar una nueva condición de pago?")) {
			MyArray nCPago = new MyArray();
			nCPago.setPos1("--editar--");
			nCPago.setPos2(0);
			this.getDto().getCondicionesPago().add(nCPago);
			this.setSelectedProveedorCondicionPago(nCPago);
			this.nuevosMA.add(this.selectedProveedorCondicionPago);
		}
	}

	// =========== cta cte linea credito ================================

	private MyArray selectedLineaCredito = null;

	public MyArray getSelectedLineaCredito() {
		return selectedLineaCredito;
	}

	public void setSelectedLineaCredito(MyArray selectedLineaCredito) {
		this.selectedLineaCredito = selectedLineaCredito;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarLineaCredito() {
		if (this.selectedLineaCredito != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la linea crédito?")) {
				this.getDto().getCtaCteLineaCredito()
						.remove(this.selectedLineaCredito);
			}
			this.nuevosMA.remove(this.selectedLineaCredito);
			this.setSelectedLineaCredito(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarLineaCredito() {

		if (mensajeAgregar("Agregar una nueva linea crédito?")) {
			MyArray nLinea = new MyArray();
			nLinea.setPos1("--editar--");
			nLinea.setPos2(0);
			this.getDto().getCtaCteLineaCredito().add(nLinea);
			this.setSelectedLineaCredito(nLinea);
			this.nuevosMA.remove(this.selectedLineaCredito);
		}
	}

	// =========== cta cte tipo de operacion ================================

	private MyPair selectedTipoOperacion = null;

	public MyPair getSelectedTipoOperacion() {
		return selectedTipoOperacion;
	}

	public void setSelectedTipoOperacion(MyPair selectedTipoOperacion) {
		this.selectedTipoOperacion = selectedTipoOperacion;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarTipoOperacion() {
		if (this.selectedTipoOperacion != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el tipo de operación?")) {
				this.getDto().getCtaCteTipoOperacion()
						.remove(this.selectedTipoOperacion);
			}
			this.nuevosMP.remove(this.selectedTipoOperacion);
			this.setSelectedTipoOperacion(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarTipoOperacion() {

		if (mensajeAgregar("Agregar un nuevo tipo de operación?")) {
			MyPair nTipoO = new MyPair();
			nTipoO.setText("--editar--");
			nTipoO.setSigla(Configuracion.SIGLA_TIPO_OPERACION);
			this.getDto().getCtaCteTipoOperacion().add(nTipoO);
			this.setSelectedTipoOperacion(nTipoO);
			this.nuevosMP.add(this.selectedTipoOperacion);
		}
	}

	// =========== cta cte estado ================================

	private MyPair selectedCtaCteEstado = null;

	public MyPair getSelectedCtaCteEstado() {
		return selectedCtaCteEstado;
	}

	public void setSelectedCtaCteEstado(MyPair selectedCtaCteEstado) {
		this.selectedCtaCteEstado = selectedCtaCteEstado;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarCtaCteEstado() {
		if (this.selectedCtaCteEstado != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el estado?")) {
				this.getDto().getCtaCteEstado()
						.remove(this.selectedCtaCteEstado);
			}
			this.nuevosMP.remove(this.selectedCtaCteEstado);
			this.setSelectedCtaCteEstado(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarCtaCteEstado() {

		if (mensajeAgregar("Agregar un nuevo estado?")) {
			MyPair nEstado = new MyPair();
			nEstado.setText("--editar--");
			nEstado.setSigla(Configuracion.SIGLA_CTA_CTE_ESTADO);
			this.getDto().getCtaCteEstado().add(nEstado);
			this.setSelectedCtaCteEstado(nEstado);
			this.nuevosMP.add(this.selectedCtaCteEstado);
		}
	}

	// =========== funcionario estado ================================

	private MyPair selectedFuncionarioEstado = null;

	public MyPair getSelectedFuncionarioEstado() {
		return selectedFuncionarioEstado;
	}

	public void setSelectedFuncionarioEstado(MyPair selectedFuncionarioEstado) {
		this.selectedFuncionarioEstado = selectedFuncionarioEstado;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarFuncionarioEstado() {
		if (this.selectedFuncionarioEstado != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el estado?")) {
				this.getDto().getFuncionarioEstado()
						.remove(this.selectedFuncionarioEstado);
			}
			this.nuevosMP.remove(this.selectedFuncionarioEstado);
			this.setSelectedFuncionarioEstado(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarFuncionarioEstado() {

		if (mensajeAgregar("Agregar un nuevo estado?")) {
			MyPair nEstado = new MyPair();
			nEstado.setText("--editar--");
			//nEstado.setSigla(Configuracion.SIGLA_ESTADO_FUNCIONARIO);
			this.getDto().getFuncionarioEstado().add(nEstado);
			this.setSelectedFuncionarioEstado(nEstado);
			this.nuevosMP.add(this.selectedFuncionarioEstado);
		}
	}

	// =========== funcionario cargo ================================

	private MyPair selectedFuncionarioCargo = null;

	public MyPair getSelectedFuncionarioCargo() {
		return selectedFuncionarioCargo;
	}

	public void setSelectedFuncionarioCargo(MyPair selectedFuncionarioCargo) {
		this.selectedFuncionarioCargo = selectedFuncionarioCargo;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarFuncionarioCargo() {
		if (this.selectedFuncionarioCargo != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el cargo?")) {
				this.getDto().getFuncionarioCargo()
						.remove(this.selectedFuncionarioCargo);
			}
			this.nuevosMP.remove(this.selectedFuncionarioCargo);
			this.setSelectedFuncionarioCargo(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarFuncionarioCargo() {

		if (mensajeAgregar("Agregar un nuevo cargo?")) {
			MyPair nCargo = new MyPair();
			nCargo.setText("--editar--");
			//nCargo.setSigla(Configuracion.SIGLA_FUNCIONARIO_CARGO);
			this.getDto().getFuncionarioCargo().add(nCargo);
			this.setSelectedFuncionarioCargo(nCargo);
			this.nuevosMP.add(this.selectedFuncionarioCargo);
		}
	}

	// =========== profesion ================================

	private MyPair selectedProfesion = null;

	public MyPair getSelectedProfesion() {
		return selectedProfesion;
	}

	public void setSelectedProfesion(MyPair selectedProfesion) {
		this.selectedProfesion = selectedProfesion;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarProfesion() {
		if (this.selectedProfesion != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la profesión?")) {
				this.getDto().getProfesion().remove(this.selectedProfesion);
			}
			this.nuevosMP.remove(this.selectedProfesion);
			this.setSelectedProfesion(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarProfesion() {

		if (mensajeAgregar("Agregar una nueva profesión?")) {
			MyPair nProfesion = new MyPair();
			nProfesion.setText("--editar--");
			nProfesion.setSigla(Configuracion.SIGLA_PROFESIONES);
			this.getDto().getProfesion().add(nProfesion);
			this.setSelectedProfesion(nProfesion);
			this.nuevosMP.add(this.selectedProfesion);
		}
	}

	// =========== importacion estado pedido ================================

	private MyPair selectedImportacionEstadoPedido = null;

	public MyPair getSelectedImportacionEstadoPedido() {
		return selectedImportacionEstadoPedido;
	}

	public void setSelectedImportacionEstadoPedido(
			MyPair selectedImportacionEstadoPedido) {
		this.selectedImportacionEstadoPedido = selectedImportacionEstadoPedido;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarImportacionEstadoPedido() {
		if (this.selectedImportacionEstadoPedido != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el estado?")) {
				this.getDto().getImportacionEstados()
						.remove(this.selectedImportacionEstadoPedido);
			}
			this.nuevosMP.remove(this.selectedImportacionEstadoPedido);
			this.setSelectedImportacionEstadoPedido(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarImportacionEstadoPedido() {

		if (mensajeAgregar("Agregar un nuevo estado?")) {
			MyPair nImpEstado = new MyPair();
			nImpEstado.setText("--editar--");
			this.getDto().getImportacionEstados().add(nImpEstado);
			this.setSelectedImportacionEstadoPedido(nImpEstado);
			this.nuevosMP.add(this.selectedImportacionEstadoPedido);
		}
	}

	// =========== moneda ================================

	private MyArray selectedMoneda = null;

	public MyArray getSelectedMoneda() {
		return selectedMoneda;
	}

	public void setSelectedMoneda(MyArray selectedMoneda) {
		this.selectedMoneda = selectedMoneda;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarMoneda() {
		if (this.selectedMoneda != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la moneda?")) {
				this.getDto().getMonedasConSimbolo()
						.remove(this.selectedMoneda);
			}
			this.nuevosMA.remove(this.selectedMoneda);
			this.setSelectedMoneda(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarMoneda() {

		if (mensajeAgregar("Agregar una nueva moneda?")) {
			MyArray nMoneda = new MyArray();
			nMoneda.setPos1("--editar--");
			this.getDto().getMonedasConSimbolo().add(nMoneda);
			this.setSelectedMoneda(nMoneda);
			this.nuevosMA.add(this.selectedMoneda);
		}
	}

	// =========== tipo de descuento ================================

	private MyArray selectedTipoDescuento = null;

	public MyArray getSelectedTipoDescuento() {
		return selectedTipoDescuento;
	}

	public void setSelectedTipoDescuento(MyArray selectedTipoDescuento) {
		this.selectedTipoDescuento = selectedTipoDescuento;
	}

	// =========== departamentos ================================

	private MyArray selectedDepartamento = null;

	public MyArray getSelectedDepartamento() {
		return selectedDepartamento;
	}

	public void setSelectedDepartamento(MyArray selectedDepartamento) {
		this.selectedDepartamento = selectedDepartamento;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarDepartamento() {
		if (this.selectedDepartamento != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el departamento?")) {
				this.getDto().getDepartamentos()
						.remove(this.selectedDepartamento);
			}
			this.nuevosMA.remove(this.selectedDepartamento);
			this.setSelectedDepartamento(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarDepartamento() {

		if (mensajeAgregar("Agregar un nuevo departamento?")) {
			MyArray nDpto = new MyArray();
			nDpto.setPos1("--editar--");
			nDpto.setPos3("--editar--");
			this.getDto().getDepartamentos().add(nDpto);
			this.setSelectedDepartamento(nDpto);
			this.nuevosMA.add(this.selectedDepartamento);
		}
	}

	// =========== sucursales ================================

	private MyArray selectedSucursal = null;

	public MyArray getSelectedSucursal() {
		return selectedSucursal;
	}

	public void setSelectedSucursal(MyArray selectedSucursal) {
		this.selectedSucursal = selectedSucursal;
	}

	@Command()
	@NotifyChange("*")
	public void eliminarSucursal() {
		if (this.selectedSucursal != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar la sucursal?")) {
				this.getDto().getSucursales().remove(this.selectedSucursal);
			}
			this.nuevosMA.remove(this.selectedSucursal);
			this.setSelectedSucursal(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarSucursal() {

		if (mensajeAgregar("Agregar una nueva sucursal?")) {
			MyArray nSucu = new MyArray();
			nSucu.setPos1("--editar--");
			nSucu.setPos2("--editar--");
			nSucu.setPos3("--editar--");
			nSucu.setPos4("--editar--");
			nSucu.setPos5("--editar--");
			nSucu.setPos6(new ArrayList<MyPair>());
			this.getDto().getSucursales().add(nSucu);
			this.setSelectedSucursal(nSucu);
			this.nuevosMA.add(this.selectedSucursal);
		}
	}

	// =========== depositos ================================

	private MyPair selectedDeposito = null;

	public MyPair getSelectedDeposito() {
		return selectedDeposito;
	}

	public void setSelectedDeposito(MyPair selectedDeposito) {
		this.selectedDeposito = selectedDeposito;
	}

	@SuppressWarnings("unchecked")
	@Command()
	@NotifyChange("*")
	public void eliminarDeposito() {
		if (this.selectedDeposito != null) {
			if (mensajeEliminar("Está seguro que quiere eliminar el deposito?")) {
				((List<MyPair>) this.selectedSucursal.getPos6())
						.remove(this.selectedDeposito);
			}
			this.nuevosMP.remove(this.selectedDeposito);
			this.setSelectedDeposito(null);
		}
	}

	@SuppressWarnings("unchecked")
	@Command()
	@NotifyChange("*")
	public void agregarDeposito() {

		if (mensajeAgregar("Agregar un nuevo deposito?")) {
			MyPair nDepo = new MyPair();
			nDepo.setText("--editar--");
			((List<MyPair>) this.selectedSucursal.getPos6()).add(nDepo);
			this.setSelectedDeposito(nDepo);
			this.nuevosMP.add(this.selectedDeposito);

		}
	}

	// =========== banco cta tipos ============================

	private MyArray selectedBanco = null;
	private MyPair selectedBancoCuentaTipos = null;

	public MyArray getSelectedBanco() {
		return selectedBanco;
	}

	public void setSelectedBanco(MyArray selectedBanco) {
		this.selectedBanco = selectedBanco;
	}
	
	@Command()
	@NotifyChange("*")
	public void eliminarBanco() {
		if (this.selectedBanco != null) {

			if (mensajeEliminar("Está seguro que quiere eliminar el banco?")) {
				this.getDto().getBancos().remove(this.selectedBanco);
			}
			this.nuevosMA.remove(this.selectedBanco);
			this.setSelectedSucursal(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarBanco() {

		if (mensajeAgregar("Agregar un nuevo banco?")) {
			MyArray nBanco = new MyArray();
			nBanco.setPos1("--editar--");
			nBanco.setPos2("--editar--");
			nBanco.setPos3("--editar--");
			nBanco.setPos4("--editar--");
			nBanco.setPos5("--editar--");
			this.getDto().getBancos().add(nBanco);
			this.setSelectedBanco(nBanco);
			this.nuevosMA.add(this.selectedBanco);
		}
	}
	
	public MyPair getSelectedBancoCuentaTipos() {
		return selectedBancoCuentaTipos;
	}

	public void setSelectedBancoCuentaTipos(MyPair selectedBancoCuentaTipos) {
		this.selectedBancoCuentaTipos = selectedBancoCuentaTipos;
	}
	
	@Command()
	@NotifyChange("*")
	public void eliminarBancoCuentaTipos() {
		if (this.selectedBancoCuentaTipos != null) {
			if (mensajeEliminar("Está seguro que quiere eliminar el tipo de cuenta?")) {
				this.getDto().getBancoCtaTipos().remove(this.selectedBancoCuentaTipos);
			}
			this.nuevosMP.remove(this.selectedBancoCuentaTipos);
			this.setSelectedDeposito(null);
		}
	}

	@Command()
	@NotifyChange("*")
	public void agregarBancoCuentaTipos() {
		if (mensajeAgregar("Agregar un nuevo tipo de cuenta?")) {
			MyPair nBTipo = new MyPair();
			nBTipo.setText("--editar--");
			this.getDto().getBancoCtaTipos().add(nBTipo);
			this.setSelectedBancoCuentaTipos(nBTipo);
			this.nuevosMP.add(this.selectedBancoCuentaTipos);
		}
	}

	// ============================ TABs ================================

	public boolean tab(String tab) {
		if (this.tab.compareTo("all") == 0) {
			return true;
		}
		return this.tab.contains(tab);

	}

	@Override
	public boolean verificarAlGrabar() {
		// TODO Auto-generated method stub
		boolean res = true;

		for (MyArray ma : this.nuevosMA) {
			if (((String) ma.getPos1()).compareTo("") == 0
					|| ((String) ma.getPos1()).compareTo("--editar--") == 0) {
				res = false;
				System.out.println("---OJO MA--- " + ma.getId() + " - "
						+ ma.getPos1());
			}
		}
		for (MyPair mp : this.nuevosMP) {
			if (((String) mp.getText()).compareTo("") == 0
					|| ((String) mp.getText()).compareTo("--editar--") == 0) {
				res = false;
				System.out.println("---OJO MP--- " + mp.getId() + " - "
						+ mp.getText() + " - " + mp.getSigla());
			}
		}
		/*
		 * System.out.println("Acceso: " + acceso.getNombreFuncionario() + " " +
		 * acceso.getDepartamento().getPos1() + " " +
		 * acceso.getSucursalOperativa().getText());
		 */
		return res;
	}

	@Override
	public String textoErrorVerificarGrabar() {
		// TODO Auto-generated method stub
		return " Verifique que la descripción de los elementos es correcta,\n no puede contener la plabra --editar-- ni ser vacía. ";
	}
}
