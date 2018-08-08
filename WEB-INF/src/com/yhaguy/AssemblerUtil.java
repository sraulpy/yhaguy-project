package com.yhaguy;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Ping;
import com.coreweb.domain.Tipo;
import com.coreweb.dto.AssemblerCoreUtil;
import com.coreweb.dto.DTO;
import com.coreweb.dto.UtilCoreDTO;
import com.coreweb.util.Misc;
import com.coreweb.util.MyArray;
import com.coreweb.util.MyPair;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CondicionPago;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.EmpresaGrupoSociedad;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.TipoCambio;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Zona;

public class AssemblerUtil extends AssemblerCoreUtil {

	public static UtilDTO getDTOUtil() {
		AssemblerUtil as = new AssemblerUtil();
		UtilCoreDTO dto = getDTOUtilCore(as);
		return (UtilDTO) dto;
	}

	@Override
	public Domain dtoToDomain(DTO dtoC) throws Exception {
		// TODO Auto-generated method stub
		UtilDTO dto = (UtilDTO) dtoC;

		listaMyPairToListaDomainTipo(dto.getTipoContactoInterno(),
				Configuracion.ID_TIPO_TIPO_CONTACTO_INTERNO);
		listaMyPairToListaDomainTipo(dto.getCategoriaCliente(),
				Configuracion.ID_TIPO_CATEGORIA_CLIENTE);
		listaMyPairToListaDomainTipo(dto.getEstadoCliente(),
				Configuracion.ID_TIPO_ESTADO_CLIENTE);
		listaMyPairToListaDomainTipo(dto.getProveedorTipo(),
				Configuracion.ID_TIPO_TIPO_PROVEEDOR);
		listaMyPairToListaDomainTipo(dto.getProveedorEstado(),
				Configuracion.ID_TIPO_ESTADO_PROVEEDOR);

		listaMyArrayToListaDomain(dto.getCondicionesPago(),
				com.yhaguy.domain.CondicionPago.class, new String[] {
						"descripcion", "plazo" });

		listaMyPairToListaDomainTipo(dto.getArticuloFamilia(),
				Configuracion.ID_TIPO_ARTICULO_FAMILIA);
		listaMyPairToListaDomainTipo(dto.getArticuloLinea(),
				Configuracion.ID_TIPO_ARTICULO_LINEA);
		listaMyPairToListaDomainTipo(dto.getArticuloMarca(),
				Configuracion.ID_TIPO_ARTICULO_MARCA);
		listaMyPairToListaDomainTipo(dto.getArticuloParte(),
				Configuracion.ID_TIPO_ARTICULO_PARTE);
		listaMyPairToListaDomainTipo(dto.getArticuloUnidadMedida(),
				Configuracion.ID_TIPO_ARTICULO_UNID_MED);
		listaMyPairToListaDomainTipo(dto.getArticuloEstado(),
				Configuracion.ID_TIPO_ARTICULO_ESTADO);

		listaMyArrayToListaDomain(dto.getArticuloModeloAplicacion(),
				com.yhaguy.domain.ArticuloModeloAplicacion.class, new String[] {
						"descripcion", "tipo", "articuloMarcaAplicacion" });

		listaMyArrayToListaDomain(dto.getArticuloMarcaAplicacion(),
				com.yhaguy.domain.ArticuloMarcaAplicacion.class, new String[] {
						"descripcion", "sigla", "articuloModeloAplicacions" });

		listaMyArrayToListaDomain(dto.getArticuloPresentacion(),
				com.yhaguy.domain.ArticuloPresentacion.class, new String[] {
						"descripcion", "unidad", "peso", "unidadMedida" });

		listaMyArrayToListaDomain(dto.getCtaCteLineaCredito(),
				com.yhaguy.domain.CtaCteLineaCredito.class, new String[] {
						"descripcion", "linea" });

		listaMyPairToListaDomainTipo(dto.getCtaCteTipoOperacion(),
				Configuracion.ID_TIPO_CTA_CTE_TIPO_OPERACION);

		listaMyPairToListaDomainTipo(dto.getCtaCteEstado(),
				Configuracion.ID_TIPO_CTA_CTE_ESTADO);

		listaMyPairToListaDomainTipo(dto.getCtaCteEmpresaEstado(),
				Configuracion.ID_TIPO_CTA_CTE_EMPRESA_ESTADO);
		listaMyPairToListaDomainTipo(dto.getCtaCteEmpresaSeleccionMov(),
				Configuracion.ID_TIPO_CTA_CTE_EMPRESA_SELECCION_MOV);

		listaMyPairToListaDomainTipo(dto.getFuncionarioEstado(),
				Configuracion.ID_TIPO_ESTADO_FUNCIONARIO);

		listaMyPairToListaDomainTipo(dto.getFuncionarioCargo(),
				Configuracion.ID_TIPO_CARGO_FUNCIONARIO);

		listaMyPairToListaDomainTipo(dto.getProfesion(),
				Configuracion.ID_TIPO_PROFESIONES);

		listaMyArrayToListaDomainTipo(dto.getMonedasConSimbolo(), Configuracion.ID_TIPO_MONEDA);

		listaMyArrayToListaDomain(dto.getDepartamentos(),
				com.yhaguy.domain.DepartamentoApp.class, new String[] {
						"nombre", "sucursal", "descripcion" });

		listaMyPairToListaDomain(dto.getDepositosMyPair(),
				com.yhaguy.domain.Deposito.class);

		listaMyArrayToListaDomain(dto.getSucursales(),
				com.yhaguy.domain.SucursalApp.class, new String[] { "nombre",
						"descripcion", "establecimiento", "direccion",
						"telefono", "depositos" });

		listaMyPairToListaDomain(dto.getTransportes(),
				com.yhaguy.domain.Transporte.class);

		listaMyArrayToListaDomain(dto.getBancos(),
				com.yhaguy.domain.Banco.class, new String[] { "descripcion",
						"direccion", "telefono", "correo", "contacto",
						"sucursales", "bancoTipo" });

		listaMyPairToListaDomainTipo(dto.getCajaTipos(),
				Configuracion.ID_TIPO_CAJA);

		listaMyPairToListaDomainTipo(dto.getCajaClasificaciones(),
				Configuracion.ID_TIPO_CAJA_CLASIFICACION);

		listaMyPairToListaDomainTipo(dto.getCajaDuraciones(),
				Configuracion.ID_TIPO_CAJA_DURACION);

		listaMyPairToListaDomainTipo(dto.getCajaEstados(),
				Configuracion.ID_TIPO_CAJA_ESTADO);

		listaMyPairToListaDomainTipo(dto.getImportacionEstados(),
				Configuracion.ID_TIPO_IMPORTACION_ESTADO);

		listaMyPairToListaDomainTipo(dto.getReciboEstados(),
				Configuracion.ID_TIPO_RECIBO_ESTADO);

		listaMyPairToListaDomainTipo(dto.getBancoCtaTipos(),
				Configuracion.ID_TIPO_BANCO_CUENTA);

		listaMyPairToListaDomainTipo(dto.getCompraTiposDescuento(),
				Configuracion.ID_TIPO_DESCUENTO_COMPRA);

		listaMyPairToListaDomainTipo(dto.getCompraTiposGastos(),
				Configuracion.ID_TIPO_GASTO_COMPRA);

		listaMyPairToListaDomainTipo(dto.getRubroEmpresa(),
				Configuracion.ID_TIPO_RUBRO_EMPRESAS);

		listaMyPairToListaDomainTipo(dto.getTiposImportacion(),
				Configuracion.ID_TIPO_IMPORTACION);

		listaMyPairToListaDomainTipo(dto.getTipoEstadoReglaPrecioVolumen(),
				Configuracion.ID_TIPO_ESTADO_REGLA_PRECIO_VOLUMEN);

		Ping ping = new Ping();
		ping.setEcho("Configuracion modificada: " + System.currentTimeMillis());
		return ping;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DTO domainToDto(Domain domain) throws Exception {
		UtilDTO dto = new UtilDTO();
		RegisterDomain rr = RegisterDomain.getInstance();

		// funcionario
		List<Tipo> funcionarioEstado = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_FUNCIONARIO);
		dto.setFuncionarioEstado(this
				.listaTiposToListaMyPair(funcionarioEstado));
		List<Tipo> funcionarioCargo = rr
				.getTipos(Configuracion.ID_TIPO_CARGO_FUNCIONARIO);
		dto.setFuncionarioCargo(this.listaTiposToListaMyPair(funcionarioCargo));

		// CtaCte
		List<Tipo> ctaCteTipoOperacion = rr
				.getTipos(Configuracion.ID_TIPO_CTA_CTE_TIPO_OPERACION);
		dto.setCtaCteTipoOperacion(this
				.listaTiposToListaMyPair(ctaCteTipoOperacion));
		List<Tipo> ctaCteEstado = rr
				.getTipos(Configuracion.ID_TIPO_CTA_CTE_ESTADO);
		dto.setCtaCteEstado(this.listaTiposToListaMyPair(ctaCteEstado));

		utilDomainToListaMyArray(dto, "ctaCteLineaCredito",
				com.yhaguy.domain.CtaCteLineaCredito.class.getName(),
				new String[] { "descripcion", "linea" });

		List<Tipo> ctaCteEmpresaEstado = rr
				.getTipos(Configuracion.ID_TIPO_CTA_CTE_EMPRESA_ESTADO);
		dto.setCtaCteEmpresaEstado(this
				.listaTiposToListaMyPair(ctaCteEmpresaEstado));

		CtaCteLineaCredito ctaCteEmpresaLineaCreditoDefault = rr
				.getCtaCteEmpresaLineaCreditoByDescripcion(Configuracion.CTA_CTE_EMPRESA_LINEA_CREDITO_DEFAULT);
		MyArray linea = new MyArray();
		linea.setId(ctaCteEmpresaLineaCreditoDefault.getId());
		linea.setPos1(ctaCteEmpresaLineaCreditoDefault.getDescripcion());
		linea.setPos2(ctaCteEmpresaLineaCreditoDefault.getLinea());
		dto.setCtaCteLineaCreditoDefault(linea);

		List<Tipo> ctaCteEmpresaSeleccionMov = rr
				.getTipos(Configuracion.ID_TIPO_CTA_CTE_EMPRESA_SELECCION_MOV);
		dto.setCtaCteEmpresaSeleccionMov(this
				.listaTiposToListaMyPair(ctaCteEmpresaSeleccionMov));

		Tipo ctaCteImputacionParcial = rr
				.getTipoPorDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_IMPUTACION_PARCIAL);
		dto.setCtaCteImputacionParcial(this
				.tipoToMyPair(ctaCteImputacionParcial));

		Tipo ctaCteImputacionCompleta = rr
				.getTipoPorDescripcion(Configuracion.TIPO_CTA_CTE_EMPRESA_IMPUTACION_COMPLETA);
		dto.setCtaCteImputacionCompleta(this
				.tipoToMyPair(ctaCteImputacionCompleta));

		Tipo ctaCteEmpresaCaracterMovProveedor = rr
				.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_PROVEEDOR);
		dto.setCtaCteEmpresaCaracterMovProveedor(this
				.tipoToMyPair(ctaCteEmpresaCaracterMovProveedor));

		Tipo ctaCteEmpresaCaracterMovCliente = rr
				.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_CARACTER_MOV_CLIENTE);
		dto.setCtaCteEmpresaCaracterMovCliente(this
				.tipoToMyPair(ctaCteEmpresaCaracterMovCliente));

		Tipo ctaCteEmpresaEstadoActivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_ACTIVO);
		dto.setCtaCteEmpresaEstadoActivo(this
				.tipoToMyPair(ctaCteEmpresaEstadoActivo));

		Tipo ctaCteEmpresaEstadoInactivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_INACTIVO);
		dto.setCtaCteEmpresaEstadoInactivo(this
				.tipoToMyPair(ctaCteEmpresaEstadoInactivo));

		Tipo ctaCteEmpresaEstadoBloqueado = rr
				.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_BLOQUEADO);
		dto.setCtaCteEmpresaEstadoBloqueado(this
				.tipoToMyPair(ctaCteEmpresaEstadoBloqueado));

		Tipo ctaCteEmpresaEstadoSinCuenta = rr
				.getTipoPorSigla(Configuracion.SIGLA_CTA_CTE_EMPRESA_ESTADO_SINCUENTA);
		dto.setCtaCteEmpresaEstadoSinCuenta(this
				.tipoToMyPair(ctaCteEmpresaEstadoSinCuenta));

		Tipo tipoTarjetaExtractoDetalleTE = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_DET_TARJ);
		dto.setTipoTarjetaExtractoDetalleTE(this.tipoToMyPair(tipoTarjetaExtractoDetalleTE));
		Tipo tipoTarjetaExtractoDetalleBM = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_BANCO_TARJETA_EXTRACTO_DETALLE_BANCO_MOVIMIENTO);
		dto.setTipoTarjetaExtractoDetalleBM(this.tipoToMyPair(tipoTarjetaExtractoDetalleBM));

		// ordenar los demás
		List<Tipo> articuloEstado = rr
				.getTipos(Configuracion.ID_TIPO_ARTICULO_ESTADO);
		dto.setArticuloEstado(this.listaTiposToListaMyPair(articuloEstado));
		Tipo artEstadoActivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_ARTICULO_ESTADO_ACTIVO);
		dto.setArticuloEstadoActivo(this.tipoToMyPair(artEstadoActivo));

		List<Tipo> articuloFamilia = rr
				.getTipos(Configuracion.ID_TIPO_ARTICULO_FAMILIA);
		dto.setArticuloFamilia(this.listaTiposToListaMyPair(articuloFamilia));

		List<Tipo> articuloLinea = rr
				.getTipos(Configuracion.ID_TIPO_ARTICULO_LINEA);
		dto.setArticuloLinea(this.listaTiposToListaMyPair(articuloLinea));

		List<Tipo> articuloMarca = rr
				.getTipos(Configuracion.ID_TIPO_ARTICULO_MARCA);
		dto.setArticuloMarca(this.listaTiposToListaMyPair(articuloMarca));

		List<Tipo> articuloParte = rr
				.getTipos(Configuracion.ID_TIPO_ARTICULO_PARTE);
		dto.setArticuloParte(this.listaTiposToListaMyPair(articuloParte));

		List<Tipo> articuloUnidadMedida = rr
				.getTipos(Configuracion.ID_TIPO_ARTICULO_UNID_MED);
		dto.setArticuloUnidadMedida(this
				.listaTiposToListaMyPair(articuloUnidadMedida));

		// CLIENTES
		List<Tipo> tipoCliente = rr.getTipos(Configuracion.ID_TIPO_CLIENTE);
		dto.setTipoCliente(this.listaTiposToListaMyPair(tipoCliente));

		Tipo tpClMinorista = rr
				.getTipoPorSigla(Configuracion.SIGLA_CLIENTE_TIPO_MINORISTA);
		Tipo tpClOcasional = rr
				.getTipoPorSigla(Configuracion.SIGLA_CLIENTE_TIPO_OCASIONAL);

		dto.setTipoClienteMinorista(this.tipoToMyPair(tpClMinorista));
		dto.setTipoClienteOcasional(this.tipoToMyPair(tpClOcasional));

		List<Tipo> categoriaCliente = rr
				.getTipos(Configuracion.ID_TIPO_CATEGORIA_CLIENTE);
		dto.setCategoriaCliente(this.listaTiposToListaMyPair(categoriaCliente));

		List<Tipo> estadoCliente = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_CLIENTE);
		dto.setEstadoCliente(this.listaTiposToListaMyPair(estadoCliente));

		List<Tipo> profesion = rr.getTipos(Configuracion.ID_TIPO_PROFESIONES);
		dto.setProfesion(this.listaTiposToListaMyPair(profesion));

		// PROVEEDORES
		List<Tipo> proveedorEstado = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_PROVEEDOR);
		dto.setProveedorEstado(this.listaTiposToListaMyPair(proveedorEstado));

		List<Tipo> proveedorTipo = rr
				.getTipos(Configuracion.ID_TIPO_TIPO_PROVEEDOR);
		dto.setProveedorTipo(this.listaTiposToListaMyPair(proveedorTipo));

		Tipo prvEstadoActivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_PROVEEDOR_ESTADO_ACTIVO);
		Tipo prvTipoLocal = rr
				.getTipoPorSigla(Configuracion.SIGLA_PROVEEDOR_TIPO_LOCAL);

		dto.setProveedorEstadoActivo(this.tipoToMyPair(prvEstadoActivo));
		dto.setProveedorTipoLocal(this.tipoToMyPair(prvTipoLocal));

		List<Tipo> tipoContactoInterno = rr
				.getTipos(Configuracion.ID_TIPO_TIPO_CONTACTO_INTERNO);
		dto.setTipoContactoInterno(this
				.listaTiposToListaMyPair(tipoContactoInterno));

		utilDomainToListaMyPair(dto, "contactoSexo",
				com.yhaguy.domain.ContactoSexo.class.getName());
		utilDomainToListaMyPair(dto, "estadoCivil",
				com.yhaguy.domain.EstadoCivil.class.getName());

		utilDomainToListaMyArray(dto, "condicionesPago",
				com.yhaguy.domain.CondicionPago.class.getName(), new String[] {
						"descripcion", "plazo", "cuotas", "diasEntreCuotas" });

		dto.setImportado(this.listaSiNo());

		utilDomainToListaMyArray(
				dto,
				"articuloModeloAplicacion",
				com.yhaguy.domain.ArticuloModeloAplicacion.class.getName(),
				new String[] { "descripcion", "tipo", "articuloMarcaAplicacion" });

		utilDomainToListaMyArray(dto, "articuloMarcaAplicacion",
				com.yhaguy.domain.ArticuloMarcaAplicacion.class.getName(),
				new String[] { "descripcion", "sigla",
						"articuloModeloAplicaciones" });

		utilDomainToListaMyArray(
				dto,
				"articuloPresentacion",
				com.yhaguy.domain.ArticuloPresentacion.class.getName(),
				new String[] { "descripcion", "unidad", "peso", "unidadMedida" });

		utilDomainToListaMyArray(dto, "departamentos",
				com.yhaguy.domain.DepartamentoApp.class.getName(),
				new String[] { "nombre", "sucursal", "descripcion" });

		utilDomainToListaMyPair(dto, "depositosMyPair",
				com.yhaguy.domain.Deposito.class.getName());

		utilDomainToListaMyArray(dto, "sucursales",
				com.yhaguy.domain.SucursalApp.class.getName(), new String[] {
						"nombre", "descripcion", "establecimiento",
						"direccion", "telefono", "depositos" });

		HashMap<Long, MyArray> sucPorDep = new HashMap<Long, MyArray>();

		for (MyArray sucursal : dto.getSucursales()) {
			for (MyPair deposito : (List<MyPair>) sucursal.getPos6()) {
				sucPorDep.put(deposito.getId(), sucursal);
			}
		}

		dto.setSucPorDep(sucPorDep);

		utilDomainToListaMyPair(dto, "sucursalesMyPair",
				com.yhaguy.domain.SucursalApp.class.getName());

		utilDomainToListaMyPair(dto, "sucursalesAppSeleccion",
				com.yhaguy.domain.SucursalApp.class.getName());

		MyPair sucursalAppTodos = new MyPair();
		sucursalAppTodos.setSigla(Configuracion.SIGLA_SUCURSAL_APP_TODAS);
		sucursalAppTodos.setText(Configuracion.TIPO_SUCURSAL_APP_TODAS);

		dto.getSucursalesAppSeleccion().add(sucursalAppTodos);
		dto.setSucursalAppTodas(sucursalAppTodos);

		utilDomainToListaMyArray(dto, "usuarios",
				com.coreweb.domain.Usuario.class.getName(), new String[] {
						"nombre", "login" });

		utilDomainToListaMyPair(dto, "transportes",
				com.yhaguy.domain.Transporte.class.getName());

		this.refrescarBancos(dto);

		Tipo movimientoBancoPendiente = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_CONCILIACION_PENDIENTE);
		dto.setEstadoMovimientoBancoPendiente(this
				.tipoToMyPair(movimientoBancoPendiente));
		
		utilDomainToListaMyArray(dto, "funcionarios",
				com.yhaguy.domain.Funcionario.class.getName(), new String[] {
						"descripcion", "funcionarioEstado", "funcionarioCargo" });

		utilDomainToListaMyPair(dto, "funcionariosMyPair",
				com.yhaguy.domain.Funcionario.class.getName());

		utilDomainToListaMyPair(dto, "gruposDeEmpresa",
				com.yhaguy.domain.EmpresaGrupoSociedad.class.getName());

		utilDomainToListaMyPair(dto, "zonas", Zona.class.getName());

		List<Tipo> compraLocalGastos = rr
				.getTipos(Configuracion.ID_TIPO_GASTO_COMPRA_LOCAL);
		dto.setCompraLocalGastos(this
				.listaTiposToListaMyPair(compraLocalGastos));

		List<Tipo> tiposRepartoAlternativo = rr
				.getTipos(Configuracion.ID_TIPO_REPARTOS);
		dto.setRepartoTipos(this
				.listaTiposToListaMyPair(tiposRepartoAlternativo));

		List<Tipo> tiposRubroEmpresas = rr
				.getTipos(Configuracion.ID_TIPO_RUBRO_EMPRESAS);
		dto.setRubroEmpresa(this.listaTiposToListaMyPair(tiposRubroEmpresas));

		// Monedas

		List<Tipo> tiposMonedas = rr.getTipos(Configuracion.ID_TIPO_MONEDA);
		dto.setMonedas(this.listaTiposToListaMyPair(tiposMonedas));
		dto.setMonedasConSimbolo(this.listaTiposToListaMyArray(tiposMonedas));

		Tipo monedaGuarani = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		dto.setMonedaGuarani(tipoToMyPair(monedaGuarani));
		dto.setMonedaGuaraniConSimbolo(tipoToMyArray(monedaGuarani));
		
		Tipo monedaDolares = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR);
		dto.setMonedaDolaresConSimbolo(tipoToMyArray(monedaDolares));

		// seteo de los estados y tipos individuales
		Tipo tipoTransferenciaInternaDom = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_TM_TRANSF_INTERNA);
		dto.setTipoTransferenciaInterna(this
				.tipoToMyPair(tipoTransferenciaInternaDom));

		Tipo tipoTransferenciaRemisionDom = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_TM_TRANSF_EXTERNA);
		dto.setTipoTransferenciaExterna(this
				.tipoToMyPair(tipoTransferenciaRemisionDom));

		Tipo estadoTransferenciaElaboracionDom = (Tipo) rr
				.getTipoPorSigla("TRF-ELAB");
		dto.setEstadoTransferenciaElaboracion(this
				.tipoToMyPair(estadoTransferenciaElaboracionDom));

		Tipo estadoTransferenciaPedidoDom = (Tipo) rr
				.getTipoPorSigla("TRF-PEND");
		dto.setEstadoTransferenciaPedido(this
				.tipoToMyPair(estadoTransferenciaPedidoDom));

		Tipo estadoTransferenciaPreparadoDom = (Tipo) rr
				.getTipoPorSigla("TRF-PREP");
		dto.setEstadoTransferenciaPreparado(this
				.tipoToMyPair(estadoTransferenciaPreparadoDom));

		Tipo estadoTransferenciaEnviadaDom = (Tipo) rr
				.getTipoPorSigla("TRF-CONF");
		dto.setEstadoTransferenciaConfirmada(this
				.tipoToMyPair(estadoTransferenciaEnviadaDom));

		Tipo estadoTransferenciaRecibidaDom = (Tipo) rr
				.getTipoPorSigla("TRF-RECI");
		dto.setEstadoTransferenciaRecibida(this
				.tipoToMyPair(estadoTransferenciaRecibidaDom));

		Tipo estadoTransferenciaUbicadaDom = (Tipo) rr
				.getTipoPorSigla("TRF-FINA");
		dto.setEstadoTransferenciaUbicada(this
				.tipoToMyPair(estadoTransferenciaUbicadaDom));

		Tipo estadoTransferenciaCanceladaDom = (Tipo) rr
				.getTipoPorSigla("TRF-CANC");
		dto.setEstadoTransferenciaCancelada(this
				.tipoToMyPair(estadoTransferenciaCanceladaDom));

		Tipo estadoRepartoPreparacion = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_REP_PREPARACION);
		dto.setEstadoRepartoPreparacion(this
				.tipoToMyPair(estadoRepartoPreparacion));

		Tipo estadoRepartoEnTransitoDom = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_REP_TRANSITO);
		dto.setEstadoRepartoEnTransito(this
				.tipoToMyPair(estadoRepartoEnTransitoDom));

		Tipo estadoRepartoEntregado = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_REP_ENTREGADO);
		dto.setEstadoRepartoEntregado(this.tipoToMyPair(estadoRepartoEntregado));

		Tipo tipoRepartoTerminalDom = (Tipo) rr.getTipoPorSigla("REP-TER");
		dto.setTipoRepartoTerminal(this.tipoToMyPair(tipoRepartoTerminalDom));

		Tipo tipoRepartoColectivoDom = (Tipo) rr.getTipoPorSigla("REP-COL");
		dto.setTipoRepartoColectivo(this.tipoToMyPair(tipoRepartoColectivoDom));

		Tipo tipoRepartoEncomiendaDom = (Tipo) rr.getTipoPorSigla("REP-ENC");
		dto.setTipoRepartoEncomienda(this
				.tipoToMyPair(tipoRepartoEncomiendaDom));

		Tipo tipoRepartoYhaguyDom = (Tipo) rr.getTipoPorSigla("REP-YHA");
		dto.setTipoRepartoYhaguy(this.tipoToMyPair(tipoRepartoYhaguyDom));

		Tipo reciboEstadoGuardado = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_RECIBO_ESTADO_GUARDADO);
		dto.setReciboEstadoGuardado(this.tipoToMyPair(reciboEstadoGuardado));

		Tipo reciboEstadoAnulado = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_RECIBO_ESTADO_ANULADO);
		dto.setReciboEstadoAnulado(this.tipoToMyPair(reciboEstadoAnulado));

		Tipo cajaEstadoActivo = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_ESTADO_ACTIVO);
		dto.setCajaEstadoActivo(this.tipoToMyPair(cajaEstadoActivo));
		
		Tipo cajaTipoMovimientosVarios = (Tipo) rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_TIPO_MOVIMIENTOS_VARIOS);
		dto.setCajaTipoMovimientosVarios(this.tipoToMyPair(cajaTipoMovimientosVarios));


		Tipo cajaPeriodoAbierta = rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_PERIODO_ABIERTA);
		dto.setCajaPeriodoEstadoAbierta(this.tipoToMyPair(cajaPeriodoAbierta));

		Tipo cajaPeriodoCerrada = rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_PERIODO_CERRADA);
		dto.setCajaPeriodoEstadoCerrada(this.tipoToMyPair(cajaPeriodoCerrada));

		Tipo cajaPeriodoProcesada = rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_PERIODO_PROCESADA);
		dto.setCajaPeriodoEstadoProcesada(this
				.tipoToMyPair(cajaPeriodoProcesada));

		/******************************* TIPOS DE MOVIMIENTO *****************************/

		Tipo _operacionCompra = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_COMPRA);
		Tipo _operacionGasto = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_GASTO);
		Tipo _operacionPago = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_PAGO);
		Tipo _operacionVenta = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_VENTA);
		Tipo _operacionCobro = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_COBRO);
		Tipo _operacionRemision = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_REMISION);
		Tipo _operacionBancaria = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_BANCARIA);
		Tipo _operacionAjuste = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_OPERACION_AJUSTE);

		List<TipoMovimiento> _movimientosDeCompra = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionCompra);
		List<TipoMovimiento> _movimientosDeGasto = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionGasto);
		List<TipoMovimiento> _movimientosDePago = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionPago);
		List<TipoMovimiento> _movimientosDeVenta = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionVenta);
		List<TipoMovimiento> _movimientosDeCobro = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionCobro);
		List<TipoMovimiento> _movimientosDeRemision = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionRemision);
		List<TipoMovimiento> _movimientosBancarios = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionBancaria);
		List<TipoMovimiento> _movimientosDeAjuste = rr
				.getTiposDeMovimientoByTipoOperacion(_operacionAjuste);

		TipoMovimiento _facCompraContado = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_COMPRA_CONTADO);
		TipoMovimiento _facCompraCredito = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_COMPRA_CREDITO);
		TipoMovimiento _facImportacionContado = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_IMPORT_CONTADO);
		TipoMovimiento _facImportacionCredito = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_IMPORT_CREDITO);
		TipoMovimiento _notaCreditoCompra = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_COMPRA);
		TipoMovimiento _notaDebitoCompra = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_DEBITO_COMPRA);
		TipoMovimiento _ordenCompra = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_COMPRA);
		TipoMovimiento _ordenCompraImportacion = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_COMPRA_IMPOR);
		TipoMovimiento _solicitudNotaCreditoCompra = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_SOLICITUD_NC_COMPRA);
		TipoMovimiento _facGastoContado = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_GASTO_CONTADO);
		TipoMovimiento _facGastoCredito = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_GASTO_CREDITO);
		TipoMovimiento _autoFactura = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AUTO_FACTURA);
		TipoMovimiento _boletaVenta = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_BOLETA_VENTA);
		TipoMovimiento _ordenGasto = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ORDEN_GASTO);
		TipoMovimiento _reciboPago = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_RECIBO_PAGO);
		TipoMovimiento _pagare = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PAGARE);
		TipoMovimiento _retencionIva = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_RETENCION);
		TipoMovimiento _anticipoPago = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ANTICIPO_PAGO);
		TipoMovimiento _facVentaContado = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_VENTA_CONTADO);
		TipoMovimiento _facVentaCredito = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_FAC_VENTA_CREDITO);
		TipoMovimiento _notaCreditoVenta = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_CREDITO_VENTA);
		TipoMovimiento _notaDebitoVenta = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_DEBITO_VENTA);
		TipoMovimiento _presupuestoVenta = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PRESUPUESTO_VENTA);
		TipoMovimiento _pedidoVenta = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_PEDIDO_VENTA);
		TipoMovimiento _solicitudNotaCreditoVenta = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_SOLICITUD_NC_VENTA);
		TipoMovimiento _reciboCobro = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_RECIBO_COBRO);
		TipoMovimiento _anticipoCobro = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_ANTICIPO_COBRO);
		TipoMovimiento _cancelacionCheque = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_CANCELACION_CHEQ_RECHAZADO);
		TipoMovimiento _reembolsoPrestamo = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_REEMBOLSO_PRESTAMO);
		TipoMovimiento _notaRemision = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_NOTA_REMISION);
		TipoMovimiento _transferenciaInterna = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_TRANS_MERCADERIA);
		TipoMovimiento _otrosComprobantes = rr
				.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_OTROS_COMPROBANTES);

		TipoMovimiento _ajustePositivo = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_POSITIVO);
		TipoMovimiento _ajusteNegativo = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_AJUSTE_NEGATIVO);
		TipoMovimiento _inventario = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_INVENTARIO_MERCADERIAS);
		TipoMovimiento _chequePropio = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_EMISION_CHEQUE);
		TipoMovimiento _depositoEfectivo = rr.getTipoMovimientoBySigla(Configuracion.SIGLA_TM_DEPOSITO_BANCARIO);

		MyPair operacionCompra = this.tipoToMyPair(_operacionCompra);
		MyPair operacionGasto = this.tipoToMyPair(_operacionGasto);
		MyPair operacionPago = this.tipoToMyPair(_operacionPago);
		MyPair operacionVenta = this.tipoToMyPair(_operacionVenta);
		MyPair operacionCobro = this.tipoToMyPair(_operacionCobro);
		MyPair operacionRemision = this.tipoToMyPair(_operacionRemision);
		MyPair operacionBancaria = this.tipoToMyPair(_operacionBancaria);
		MyPair operacionAjuste = this.tipoToMyPair(_operacionAjuste);

		List<MyArray> movimientosDeCompra = this
				.listTipoMovToListMyArray(_movimientosDeCompra);
		List<MyArray> movimientosDeGasto = this
				.listTipoMovToListMyArray(_movimientosDeGasto);
		List<MyArray> movimientosDePago = this
				.listTipoMovToListMyArray(_movimientosDePago);
		List<MyArray> movimientosDeVenta = this
				.listTipoMovToListMyArray(_movimientosDeVenta);
		List<MyArray> movimientosDeCobro = this
				.listTipoMovToListMyArray(_movimientosDeCobro);
		List<MyArray> movimientosDeRemision = this
				.listTipoMovToListMyArray(_movimientosDeRemision);
		List<MyArray> movimientosBancarios = this
				.listTipoMovToListMyArray(_movimientosBancarios);
		List<MyArray> movimientosDeAjuste = this
				.listTipoMovToListMyArray(_movimientosDeAjuste);

		MyArray facCompraContado = this.tipoMovToMyArray(_facCompraContado);
		MyArray facCompraCredito = this.tipoMovToMyArray(_facCompraCredito);
		MyArray facImportacionContado = this
				.tipoMovToMyArray(_facImportacionContado);
		MyArray facImportacionCredito = this
				.tipoMovToMyArray(_facImportacionCredito);
		MyArray notaCreditoCompra = this.tipoMovToMyArray(_notaCreditoCompra);
		MyArray notaDebitoCompra = this.tipoMovToMyArray(_notaDebitoCompra);
		MyArray ordenCompra = this.tipoMovToMyArray(_ordenCompra);
		MyArray ordenCompraImportacion = this
				.tipoMovToMyArray(_ordenCompraImportacion);
		MyArray solicitudNotaCreditoCompra = this
				.tipoMovToMyArray(_solicitudNotaCreditoCompra);
		MyArray facGastoContado = this.tipoMovToMyArray(_facGastoContado);
		MyArray facGastoCredito = this.tipoMovToMyArray(_facGastoCredito);
		MyArray autoFactura = this.tipoMovToMyArray(_autoFactura);
		MyArray boletaVenta = this.tipoMovToMyArray(_boletaVenta);
		MyArray ordenGasto = this.tipoMovToMyArray(_ordenGasto);
		MyArray reciboPago = this.tipoMovToMyArray(_reciboPago);
		MyArray pagare = this.tipoMovToMyArray(_pagare);
		MyArray retencionIva = this.tipoMovToMyArray(_retencionIva);
		MyArray anticipoPago = this.tipoMovToMyArray(_anticipoPago);
		MyArray facVentaContado = this.tipoMovToMyArray(_facVentaContado);
		MyArray facVentaCredito = this.tipoMovToMyArray(_facVentaCredito);
		MyArray notaCreditoVenta = this.tipoMovToMyArray(_notaCreditoVenta);
		MyArray notaDebitoVenta = this.tipoMovToMyArray(_notaDebitoVenta);
		MyArray presupuestoVenta = this.tipoMovToMyArray(_presupuestoVenta);
		MyArray pedidoVenta = this.tipoMovToMyArray(_pedidoVenta);
		MyArray solicitudNotaCreditoVenta = this
				.tipoMovToMyArray(_solicitudNotaCreditoVenta);
		MyArray reciboCobro = this.tipoMovToMyArray(_reciboCobro);
		MyArray anticipoCobro = this.tipoMovToMyArray(_anticipoCobro);
		MyArray cancelacionCheque = this.tipoMovToMyArray(_cancelacionCheque);
		MyArray reembolsoPrestamo = this.tipoMovToMyArray(_reembolsoPrestamo);
		MyArray notaRemision = this.tipoMovToMyArray(_notaRemision);
		MyArray transferenciaInterna = this
				.tipoMovToMyArray(_transferenciaInterna);
		MyArray chequePropio = this.tipoMovToMyArray(_chequePropio);
		MyArray depositoEfectivo = this.tipoMovToMyArray(_depositoEfectivo);
		MyArray ajustePositivo = this.tipoMovToMyArray(_ajustePositivo);
		MyArray ajusteNegativo = this.tipoMovToMyArray(_ajusteNegativo);
		MyArray inventario = this.tipoMovToMyArray(_inventario);
		MyArray otrosComprobantes = this.tipoMovToMyArray(_otrosComprobantes);

		dto.setOperacionCompra(operacionCompra);
		dto.setOperacionGasto(operacionGasto);
		dto.setOperacionPago(operacionPago);
		dto.setOperacionVenta(operacionVenta);
		dto.setOperacionCobro(operacionCobro);
		dto.setOperacionRemision(operacionRemision);
		dto.setOperacionBancaria(operacionBancaria);
		dto.setOperacionAjuste(operacionAjuste);

		dto.setMovimientosDeCompra(movimientosDeCompra);
		dto.setMovimientosDeGasto(movimientosDeGasto);
		dto.setMovimientosDePago(movimientosDePago);
		dto.setMovimientosDeVenta(movimientosDeVenta);
		dto.setMovimientosDeCobro(movimientosDeCobro);
		dto.setMovimientosDeRemision(movimientosDeRemision);
		dto.setMovimientosBancarios(movimientosBancarios);
		dto.setMovimientosDeAjuste(movimientosDeAjuste);

		dto.setTmFacturaCompraContado(facCompraContado);
		dto.setTmFacturaCompraCredito(facCompraCredito);
		dto.setTmNotaCreditoCompra(notaCreditoCompra);
		dto.setTmNotaDebitoCompra(notaDebitoCompra);
		dto.setTmFacturaImportacionContado(facImportacionContado);
		dto.setTmFacturaImportacionCredito(facImportacionCredito);
		dto.setTmOrdenCompra(ordenCompra);
		dto.setTmOrdenCompraImportacion(ordenCompraImportacion);
		dto.setTmSolicitudNotaCreditoCompra(solicitudNotaCreditoCompra);
		dto.setTmFacturaGastoContado(facGastoContado);
		dto.setTmFacturaGastoCredito(facGastoCredito);
		dto.setTmAutoFactura(autoFactura);
		dto.setTmBoletaVenta(boletaVenta);
		dto.setTmOrdenGasto(ordenGasto);
		dto.setTmReciboPago(reciboPago);
		dto.setTmPagare(pagare);
		dto.setTmRetencionIva(retencionIva);
		dto.setTmAnticipoPago(anticipoPago);
		dto.setTmFacturaVentaContado(facVentaContado);
		dto.setTmFacturaVentaCredito(facVentaCredito);
		dto.setTmNotaCreditoVenta(notaCreditoVenta);
		dto.setTmNotaDebitoVenta(notaDebitoVenta);
		dto.setTmPresupuestoVenta(presupuestoVenta);
		dto.setTmPedidoVenta(pedidoVenta);
		dto.setTmSolicitudNotaCreditoVenta(solicitudNotaCreditoVenta);
		dto.setTmReciboCobro(reciboCobro);
		dto.setTmAnticipoCobro(anticipoCobro);
		dto.setTmCancelacionChequeRechazado(cancelacionCheque);
		dto.setTmReembolsoPrestamo(reembolsoPrestamo);
		dto.setTmNotaRemision(notaRemision);
		dto.setTmTransferenciaMercaderia(transferenciaInterna);
		dto.setTmAjustePositivo(ajustePositivo);
		dto.setTmAjusteNegativo(ajusteNegativo);
		dto.setTmInventario(inventario);
		dto.setTmOtrosComprobantes(otrosComprobantes);

		dto.setTmBancoEmisionCheque(chequePropio);
		dto.setTmBancoDepositoBancario(depositoEfectivo);

		dto.getTmBancoEmisionCheque().setPos7(false);
		dto.getTmBancoDepositoBancario().setPos7(true);

		/*********************************************************************************/

		// Tipos de Comprobante
		Tipo _comprobanteLegal = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_COMPROBANTE_LEGAL);
		Tipo _comprobanteInterno = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_COMPROBANTE_INTERNO);

		MyPair comprobanteLegal = this.tipoToMyPair(_comprobanteLegal);
		MyPair comprobanteInterno = this.tipoToMyPair(_comprobanteInterno);

		dto.setComprobanteLegal(comprobanteLegal);
		dto.setComprobanteInterno(comprobanteInterno);

		// Estados de Comprobantes
		List<Tipo> estadosComprobantes = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_COMPROBANTE);
		Tipo estadoComprobantePendiente = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_PENDIENTE);
		Tipo estadoComprobanteAprobado = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_APROBADO);
		Tipo estadoComprobanteCerrado = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CERRADO);
		Tipo estadoComprobanteAnulado = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_ANULADO);
		Tipo estadoComprobanteConfeccionado = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_COMPROBANTE_CONFECCIONADO);

		dto.setEstadosComprobantes(this
				.listaTiposToListaMyPair(estadosComprobantes));
		dto.setEstadoComprobantePendiente(this
				.tipoToMyPair(estadoComprobantePendiente));
		dto.setEstadoComprobanteAprobado(this
				.tipoToMyPair(estadoComprobanteAprobado));
		dto.setEstadoComprobanteCerrado(this
				.tipoToMyPair(estadoComprobanteCerrado));
		dto.setEstadoComprobanteAnulado(this
				.tipoToMyPair(estadoComprobanteAnulado));
		dto.setEstadoComprobanteConfeccionado(this
				.tipoToMyPair(estadoComprobanteConfeccionado));

		// Tipos de Empresa
		Tipo _empresaCliente = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_EMPRESA_CLIENTE);
		Tipo _empresaProveedor = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_EMPRESA_PROVEEDOR);
		Tipo _empresaFuncionario = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_EMPRESA_FUNCIONARIO);
		Tipo _empresaBanco = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_EMPRESA_BANCO);

		MyPair empresaCliente = this.tipoToMyPair(_empresaCliente);
		MyPair empresaProveedor = this.tipoToMyPair(_empresaFuncionario);
		MyPair empresaFuncionario = this.tipoToMyPair(_empresaProveedor);
		MyPair empresaBanco = this.tipoToMyPair(_empresaBanco);

		dto.setEmpresaCliente(empresaCliente);
		dto.setEmpresaProveedor(empresaProveedor);
		dto.setEmpresaFuncionario(empresaFuncionario);
		dto.setEmpresaBanco(empresaBanco);

		// Tipos de Conciliacion
		List<Tipo> _tiposConciliacion = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_CONCILIACION);
		Tipo _conciliado = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_CONCILIACION_CONCILIADO);
		Tipo _pendiente = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_CONCILIACION_PENDIENTE);
		Tipo _diferencia = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_CONCILIACION_DIFERENCIA);

		List<MyPair> tiposConciliacion = this
				.listaTiposToListaMyPair(_tiposConciliacion);
		MyPair conciliado = this.tipoToMyPair(_conciliado);
		MyPair pendiente = this.tipoToMyPair(_pendiente);
		MyPair diferencia = this.tipoToMyPair(_diferencia);

		dto.setEstadosConciliacion(tiposConciliacion);
		dto.setEstadoConciliacionConciliado(conciliado);
		dto.setEstadoConciliacionPendiente(pendiente);
		dto.setEstadoConciliacionDiferencia(diferencia);

		// Paises
		List<Tipo> paisEmpresas = rr
				.getTipos(Configuracion.ID_TIPO_PAIS_EMPRESA);
		dto.setPaisEmpresas(this.listaTiposToListaMyPair(paisEmpresas));
		Tipo paisPy = rr.getTipoPorSigla(Configuracion.SIGLA_PAIS_PARAGUAY);
		dto.setPaisParaguay(this.tipoToMyPair(paisPy));

		List<Tipo> emailEmpresas = rr
				.getTipos(Configuracion.ID_TIPO_EMPRESA_EMAILS);
		dto.setEmailEmpresa(this.listaTiposToListaMyPair(emailEmpresas));

		// Tipos de Persona
		List<Tipo> tipoPersonas = rr.getTipos(Configuracion.ID_TIPO_PERSONA);
		dto.setTipoPersonas(this.listaTiposToListaMyPair(tipoPersonas));
		Tipo tipoPersonaJuridica = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_PERSONA_JURIDICA);
		Tipo tipoPersonaFisica = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_PERSONA_FISICA);
		dto.setTipoPersonaFisica(this.tipoToMyPair(tipoPersonaFisica));
		dto.setTipoPersonaJuridica(this.tipoToMyPair(tipoPersonaJuridica));

		// Cuentas Contables
		dto.setCuentaClientesOcasionales(rr
				.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_CLIENTES_OCASIONALES));
		dto.setCuentaProveedoresVarios(rr
				.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_PROVEEDORES_VARIOS));
		dto.setCuentaIvaCF10(rr
				.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_IVA_10_CF));
		dto.setCuentaIvaCF5(rr
				.getCuentaContableByAlias(Configuracion.ALIAS_CUENTA_IVA_5_CF));
		List<Tipo> tiposCuentaContable = rr
				.getTipos(Configuracion.ID_TIPO_CUENTAS_CONTABLES);
		dto.setCuentasContablesTipos(this
				.listaTiposToListaMyPair(tiposCuentaContable));

		// Tipos de venta
		List<Tipo> tipoVentas = rr.getTipos(Configuracion.ID_TIPO_VENTA);
		dto.setTipoVenta(this.listaTiposToListaMyPair(tipoVentas));
		Tipo tipoVentaMostrador = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_MOSTRADOR);
		Tipo tipoVentaVendedor = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_VENDEDOR);
		Tipo tipoVentaTerminal = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_TERMINAL);
		Tipo tipoVentaEncomienda = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_ENCOMIENDA);
		Tipo tipoVentaColectivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_COLECTIVO);
		Tipo tipoVentaReparto = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_VENTA_REPARTO);
		dto.setTipoVentaMostrador(this.tipoToMyPair(tipoVentaMostrador));
		dto.setTipoVentaVendedor(this.tipoToMyPair(tipoVentaVendedor));
		dto.setTipoVentaTerminal(this.tipoToMyPair(tipoVentaTerminal));
		dto.setTipoVentaEncomienda(this.tipoToMyPair(tipoVentaEncomienda));
		dto.setTipoVentaColectivo(this.tipoToMyPair(tipoVentaColectivo));
		dto.setTipoVentaReparto(this.tipoToMyPair(tipoVentaReparto));

		// Cajas
		List<Tipo> cajaTipos = rr.getTipos(Configuracion.ID_TIPO_CAJA);
		dto.setCajaTipos(this.listaTiposToListaMyPair(cajaTipos));

		List<Tipo> cajaClasificaciones = rr
				.getTipos(Configuracion.ID_TIPO_CAJA_CLASIFICACION);
		dto.setCajaClasificaciones(this
				.listaTiposToListaMyPair(cajaClasificaciones));

		List<Tipo> cajaDuraciones = rr
				.getTipos(Configuracion.ID_TIPO_CAJA_DURACION);
		dto.setCajaDuraciones(this.listaTiposToListaMyPair(cajaDuraciones));

		List<Tipo> cajaEstados = rr.getTipos(Configuracion.ID_TIPO_CAJA_ESTADO);
		dto.setCajaEstados(this.listaTiposToListaMyPair(cajaEstados));

		List<Tipo> cajaReposiciones = rr
				.getTipos(Configuracion.ID_TIPO_CAJA_REPOSICION);
		dto.setCajaReposiciones(this.listaTiposToListaMyPair(cajaReposiciones));

		Tipo cajaReposicion_Egreso = rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO);
		Tipo cajaReposicion_Ingreso = rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_REPOSICION_INGRESO);

		dto.setCajaReposicion_Egreso(this.tipoToMyPair(cajaReposicion_Egreso));
		dto.setCajaReposicion_Ingreso(this.tipoToMyPair(cajaReposicion_Ingreso));

		List<Tipo> cajaReposicionEgresos = rr
				.getTipos(Configuracion.ID_TIPO_CAJA_REPOSICION_EGRESO);
		dto.setCajaReposicionEgresos(this
				.listaTiposToListaMyPair(cajaReposicionEgresos));

		Tipo cajaReposicionEgreso_SinComp = rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_SIN_COMP);
		Tipo cajaReposicionEgreso_Vale = rr
				.getTipoPorSigla(Configuracion.SIGLA_CAJA_REPOSICION_EGRESO_VALE);

		dto.setCajaReposicionEgreso_SinComprobante(this
				.tipoToMyPair(cajaReposicionEgreso_SinComp));
		dto.setCajaReposicionEgreso_Vale(this
				.tipoToMyPair(cajaReposicionEgreso_Vale));

		// Estados de Importacion
		List<Tipo> importacionEstados = rr
				.getTipos(Configuracion.ID_TIPO_IMPORTACION_ESTADO);

		dto.setImportacionEstados(this
				.listaTiposToListaMyPair(importacionEstados));

		Tipo impEstado1 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_CONFIRMADO);
		Tipo impEstado2 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_SOLICITANDO_COTIZACION);
		Tipo impEstado3 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_PROFORMA_RECIBIDA);
		Tipo impEstado4 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_PENDIENTE_DE_ENVIO);
		Tipo impEstado5 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_PEDIDO_ENVIADO);
		Tipo impEstado6 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_CERRADO);
		Tipo impEstado7 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_ESTADO_ANULADO);

		dto.setImportacionEstadoConfirmado(this.tipoToMyPair(impEstado1));
		dto.setImportacionEstadoSolicitandoCotizacion(this
				.tipoToMyPair(impEstado2));
		dto.setImportacionEstadoProformaRecibida(this.tipoToMyPair(impEstado3));
		dto.setImportacionEstadoPendienteEnvio(this.tipoToMyPair(impEstado4));
		dto.setImportacionEstadoPedidoEnviado(this.tipoToMyPair(impEstado5));
		dto.setImportacionEstadoCerrado(this.tipoToMyPair(impEstado6));
		dto.setImportacionEstadoAnulado(this.tipoToMyPair(impEstado7));

		// Estados de Recibo
		List<Tipo> reciboEstados = rr
				.getTipos(Configuracion.ID_TIPO_RECIBO_ESTADO);
		dto.setReciboEstados(this.listaTiposToListaMyPair(reciboEstados));

		// Tipos de Formas de Pago
		List<Tipo> formasDePago = rr
				.getTipos(Configuracion.ID_TIPO_RECIBO_FORMA_PAGO);
		dto.setFormasDePago(this.listaTiposToListaMyPair(formasDePago));

		Tipo formaPagoEfectivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_EFECTIVO);
		Tipo formaPagoChequePropio = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_PROPIO);
		Tipo formaPagoChequeTercero = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_TERCERO);
		Tipo formaPagoTarjetaCredito = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_CREDITO);
		Tipo formaPagoTarjetaDebito = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_TARJETA_DEBITO);
		Tipo formaPagoRetencion = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_RETENCION);
		Tipo formaPagoDepositoBancario = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_DEPOSITO_BANCARIO);
		Tipo formaPagoChequeAutoCobranza = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_CHEQUE_AUTOCOBRANZA);
		Tipo formaPagoDebitoCobranzaCentral = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_DEBITO_COBRANZA_CENTRAL);
		Tipo formaPagoRecaudacionCentral = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_RECAUDACION_CENTRAL);
		Tipo formaPagoTransferenciaCentral = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_TRANSFERENCIA_CENTRAL);
		Tipo formaPagoSaldoFavorGenerado = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_GENERADO);
		Tipo formaPagoSaldoFavorCobrado = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_SALDO_FAVOR_COBRADO);
		Tipo formaPagoDebitoCuentaBancaria = rr
				.getTipoPorSigla(Configuracion.SIGLA_FORMA_PAGO_DEBITO_CTA_BANCARIA);

		dto.setFormaPagoEfectivo(this.tipoToMyPair(formaPagoEfectivo));
		dto.setFormaPagoChequePropio(this.tipoToMyPair(formaPagoChequePropio));
		dto.setFormaPagoChequeTercero(this.tipoToMyPair(formaPagoChequeTercero));
		dto.setFormaPagoTarjetaCredito(this
				.tipoToMyPair(formaPagoTarjetaCredito));
		dto.setFormaPagoTarjetaDebito(this.tipoToMyPair(formaPagoTarjetaDebito));
		dto.setFormaPagoRetencion(this.tipoToMyPair(formaPagoRetencion));
		dto.setFormaPagoDepositoBancario(this
				.tipoToMyPair(formaPagoDepositoBancario));
		dto.setFormaPagoChequeAutoCobranza(this.tipoToMyPair(formaPagoChequeAutoCobranza));
		dto.setFormaPagoDebitoCobranzaCentral(this.tipoToMyPair(formaPagoDebitoCobranzaCentral));
		dto.setFormaPagoRecaudacionCentral(this.tipoToMyPair(formaPagoRecaudacionCentral));
		dto.setFormaPagoTransferenciaCentral(this.tipoToMyPair(formaPagoTransferenciaCentral));
		dto.setFormaPagoSaldoFavorGenerado(this.tipoToMyPair(formaPagoSaldoFavorGenerado));
		dto.setFormaPagoSaldoFavorCobrado(this.tipoToMyPair(formaPagoSaldoFavorCobrado));
		dto.setFormaPagoDebitoEnCuentaBancaria(this.tipoToMyPair(formaPagoDebitoCuentaBancaria));

		// Banco Cta Tipos
		List<Tipo> bancoCtaTipos = rr
				.getTipos(Configuracion.ID_TIPO_BANCO_CUENTA);
		dto.setBancoCtaTipos(this.listaTiposToListaMyPair(bancoCtaTipos));

		// Tipos de Gasto / Descuento / Prorrateos en Compras
		List<Tipo> compraDescuentoTipos = rr
				.getTipos(Configuracion.ID_TIPO_DESCUENTO_COMPRA);
		dto.setCompraTiposDescuento(this
				.listaTiposToListaMyPair(compraDescuentoTipos));

		List<Tipo> compraGastoTipos = rr
				.getTipos(Configuracion.ID_TIPO_GASTO_COMPRA);
		dto.setCompraTiposGastos(this.listaTiposToListaMyPair(compraGastoTipos));

		Tipo tipoCompraGastoFlete = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_COMPRA_GASTO_1);
		dto.setTipoCompraGastoFlete(this.tipoToMyPair(tipoCompraGastoFlete));

		Tipo tipoCompraGastoSeguro = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_COMPRA_GASTO_2);
		dto.setTipoCompraGastoSeguro(this.tipoToMyPair(tipoCompraGastoSeguro));

		List<Tipo> compraProrrateoTipos = rr
				.getTipos(Configuracion.ID_TIPO_PRORRATEO_COMPRA);
		dto.setCompraTiposProrrateo(this
				.listaTiposToListaMyPair(compraProrrateoTipos));

		Tipo tipoCompraProrrateoFlete = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_COMPRA_PRORRATEO_1);
		dto.setTipoCompraProrrateoFlete(this
				.tipoToMyPair(tipoCompraProrrateoFlete));

		Tipo tipoCompraProrrateoSeguro = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_COMPRA_PRORRATEO_2);
		dto.setTipoCompraProrrateoSeguro(this
				.tipoToMyPair(tipoCompraProrrateoSeguro));

		// seteo de los estados de los movimientos con respecto al reparto
		Tipo movEstadoRepartoPendiente = (Tipo) rr
				.getTipoPorSigla("MOV-REP-PEN");
		dto.setMovEstadoRepartoPendiente(this
				.tipoToMyPair(movEstadoRepartoPendiente));

		Tipo movEstadoRepartoEnPreparacion = (Tipo) rr
				.getTipoPorSigla("MOV-REP-ENPRE");
		dto.setMovEstadoRepartoPreparacion(this
				.tipoToMyPair(movEstadoRepartoEnPreparacion));

		Tipo movEstadoRepartoPreparado = (Tipo) rr
				.getTipoPorSigla("MOV-REP-PREP");
		dto.setMovEstadoRepartoPreparado(this
				.tipoToMyPair(movEstadoRepartoPreparado));

		Tipo movEstadoRepartoEnTransito = (Tipo) rr
				.getTipoPorSigla("MOV-REP-TRAN");
		dto.setMovEstadoRepartoEnTransito(this
				.tipoToMyPair(movEstadoRepartoEnTransito));

		Tipo movEstadoRepartoFinalizado = (Tipo) rr
				.getTipoPorSigla("MOV-REP-FINA");
		dto.setMovEstadoRepartoFinalizado(this
				.tipoToMyPair(movEstadoRepartoFinalizado));

		// Condiciones de Pago
		CondicionPago _condicionPagoContado = rr
				.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CONTADO);
		CondicionPago _condicionPagoCredito30 = rr
				.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_30);
		CondicionPago _condicionPagoCredito60 = rr
				.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_60);
		CondicionPago _condicionPagoCredito90 = rr
				.getCondicionPagoById(Configuracion.ID_CONDICION_PAGO_CREDITO_90);

		MyArray condicionPagoContado = this
				.condicionPagoToMyArray(_condicionPagoContado);
		MyArray condicionPagoCredito30 = this
				.condicionPagoToMyArray(_condicionPagoCredito30);
		MyArray condicionPagoCredito60 = this
				.condicionPagoToMyArray(_condicionPagoCredito60);
		MyArray condicionPagoCredito90 = this
				.condicionPagoToMyArray(_condicionPagoCredito90);

		dto.setCondicionPagoContado(condicionPagoContado);
		dto.setCondicionPagoCredito30(condicionPagoCredito30);
		dto.setCondicionPagoCredito60(condicionPagoCredito60);
		dto.setCondicionPagoCredito90(condicionPagoCredito90);

		// Tipos de Iva
		List<Tipo> tiposIva = rr.getTipos(Configuracion.ID_TIPO_IVA);
		dto.setTiposDeIva(this.listaTiposToListaMyArray(tiposIva));

		Tipo tipoIva10 = rr.getTipoPorSigla(Configuracion.SIGLA_IVA_10);
		Tipo tipoIva5 = rr.getTipoPorSigla(Configuracion.SIGLA_IVA_5);
		Tipo tipoIvaExenta = rr.getTipoPorSigla(Configuracion.SIGLA_IVA_EXENTO);

		dto.setTipoIva10(this.tipoToMyArray(tipoIva10));
		dto.setTipoIva5(this.tipoToMyArray(tipoIva5));
		dto.setTipoIvaExento(this.tipoToMyArray(tipoIvaExenta));

		// Regimen Tributario
		List<Tipo> tiposRegimen = rr
				.getTipos(Configuracion.ID_TIPO_REGIMEN_TRIBUTARIO);
		dto.setRegimenesTributarios(this.listaTiposToListaMyPair(tiposRegimen));
		Tipo tipoRegimen1 = rr
				.getTipoPorSigla(Configuracion.SIGLA_REGIMEN_TRIB_NO_EXENTA);
		dto.setRegimenTributarioNoExenta(this.tipoToMyPair(tipoRegimen1));

		// Grupos de Empresa
		EmpresaGrupoSociedad egs = rr
				.getGrupoEmpresaByDescripcion(Configuracion.EMPRESA_GRUPO_NO_DEFINIDO);
		MyPair egsmp = new MyPair(egs.getId(), egs.getDescripcion());
		dto.setGrupoEmpresaNoDefinido(egsmp);

		// Estado Cliente (Activo)
		Tipo estCliente = rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_ESTADO_CLIENTE, 1);
		MyPair estClientemp = new MyPair(estCliente.getId(),
				estCliente.getDescripcion());
		dto.setEstadoClienteActivo(estClientemp);

		// Categoria Cliente (Al dia)
		Tipo ccad = rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_CATEGORIA_CLIENTE, 2);
		MyPair ccadmp = new MyPair(ccad.getId(), ccad.getDescripcion());
		dto.setCategoriaClienteAldia(ccadmp);

		// Estados de Venta
		List<Tipo> estadosPedidoVenta = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_VENTA);
		dto.setEstadosVenta(this.listaTiposToListaMyArray(estadosPedidoVenta));

		Tipo estadoVtaPedido1 = rr
				.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_SOLO_PRESUPUESTO);
		Tipo estadoVtaPedido2 = rr
				.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_PASADO_A_PEDIDO);
		Tipo estadoVtaPedido3 = rr
				.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_SOLO_PEDIDO);
		Tipo estadoVtaPedido4 = rr
				.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_CERRADO);
		Tipo estadoVtaPedido5 = rr
				.getTipoPorSigla(Configuracion.SIGLA_VENTA_ESTADO_FACTURADO);

		dto.setEstadoVenta_soloPresupuesto(this.tipoToMyArray(estadoVtaPedido1));
		dto.setEstadoVenta_pasadoApedido(this.tipoToMyArray(estadoVtaPedido2));
		dto.setEstadoVenta_soloPedido(this.tipoToMyArray(estadoVtaPedido3));
		dto.setEstadoVenta_cerrado(this.tipoToMyArray(estadoVtaPedido4));
		dto.setEstadoVenta_facturado(this.tipoToMyArray(estadoVtaPedido5));

		// Modos de Venta
		List<Tipo> modosVenta = rr.getTipos(Configuracion.ID_TIPO_MODO_VENTA);
		dto.setModosVenta(this.listaTiposToListaMyPair(modosVenta));

		Tipo modVta_most = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_MODO_VENTA_MOSTRADOR);
		Tipo modVta_ext = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_MODO_VENTA_EXTERNA);
		Tipo modVta_tel = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_MODO_VENTA_TELEFONICA);

		dto.setModoVenta_mostrador(this.tipoToMyPair(modVta_most));
		dto.setModoVenta_externa(this.tipoToMyPair(modVta_ext));
		dto.setModoVenta_telefonica(this.tipoToMyPair(modVta_tel));

		// Tipos de Importacion
		List<Tipo> tiposImportacion = rr
				.getTipos(Configuracion.ID_TIPO_IMPORTACION);
		dto.setTiposImportacion(this.listaTiposToListaMyPair(tiposImportacion));

		Tipo tipoImportacion1 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_TIPO_1);
		Tipo tipoImportacion2 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_TIPO_2);
		Tipo tipoImportacion3 = rr
				.getTipoPorSigla(Configuracion.SIGLA_IMPORTACION_TIPO_3);

		dto.setTipoImportacionFOB(this.tipoToMyPair(tipoImportacion1));
		dto.setTipoImportacionCIF(this.tipoToMyPair(tipoImportacion2));
		dto.setTipoImportacionCF(this.tipoToMyPair(tipoImportacion3));

		// Motivos de Nota de Crédito
		List<Tipo> motivosNotaCredito = rr
				.getTipos(Configuracion.ID_TIPO_NOTA_CREDITO_MOTIVOS);
		Tipo motivoDescuento = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_DESCUENTO);
		Tipo motivoDevolucion = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_DEVOLUCION);
		Tipo motivoDiferenciaPrecio = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_DIF_PRECIO);
		Tipo motivoReclamo = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_NC_MOTIVO_RECLAMO);

		dto.setMotivosNotaCredito(this
				.listaTiposToListaMyPair(motivosNotaCredito));
		dto.setMotivoNotaCreditoDescuento(this.tipoToMyPair(motivoDescuento));
		dto.setMotivoNotaCreditoDevolucion(this.tipoToMyPair(motivoDevolucion));
		dto.setMotivoNotaCreditoReclamo(this.tipoToMyPair(motivoReclamo));
		dto.setMotivoNotaCreditoDifPrecio(this.tipoToMyPair(motivoDiferenciaPrecio));

		// Tipos de detalle en Notas de Credito
		Tipo notaCreditoDetalleFactura = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_NC_DETALLE_FACTURA);
		Tipo notaCreditoDetalleArticulo = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_NC_DETALLE_ARTICULO);

		dto.setNotaCreditoDetalleFactura(this
				.tipoToMyPair(notaCreditoDetalleFactura));
		dto.setNotaCreditoDetalleArticulo(this
				.tipoToMyPair(notaCreditoDetalleArticulo));

		// Regla de precio, falta todo lo de articulo, venta, vendero
		dto.setReglaCliente(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.REGLA_PRECIO_CLIENTE));
		dto.setReglaClienteCategoria(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_CLIENTE));
		dto.setReglaClienteRubro(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_RUBRO_EMPRESAS));

		dto.setReglaArticulo(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.REGLA_PRECIO_ARTICULO));
		dto.setReglaArticuloMarca(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_ARTICULO_MARCA));
		dto.setReglaArticuloFamilia(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_ARTICULO_FAMILIA));
		dto.setReglaArticuloParte(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_ARTICULO_PARTE));
		dto.setReglaArticuloRubro(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_ARTICULO_LINEA));// rubro

		dto.setReglaVenta(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.REGLA_PRECIO_VENTA));
		dto.setReglaVentaModoVenta(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_VENTA_MODO));
		dto.setReglaVentaSucursales(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_SUCURSAL));

		dto.setReglaVendedor(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.REGLA_PRECIO_VENDEDOR));
		dto.setReglaVendedorRubro(this.getTipo(Configuracion.REGLA_PRECIO,
				Configuracion.ID_TIPO_VENDEDOR_RUBRO));

		// Regla de precio por volumem
		List<Tipo> tiposEstadoReglaVolumen = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_REGLA_PRECIO_VOLUMEN);

		dto.setTipoEstadoReglaPrecioVolumen(this
				.listaTiposToListaMyPair(tiposEstadoReglaVolumen));

		Tipo tiposEstadoReglaVolumen1 = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_MAYOR);
		Tipo tiposEstadoReglaVolumen2 = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_MENOR);
		Tipo tiposEstadoReglaVolumen3 = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_IGUAL);
		Tipo tiposEstadoReglaVolumen4 = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_DIFERENTE);
		Tipo tiposEstadoReglaVolumen5 = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_REGLA_PRECIO_NINGUNO);

		dto.setTipoEstadoMayor(this.tipoToMyPair(tiposEstadoReglaVolumen1));
		dto.setTipoEstadoMenor(this.tipoToMyPair(tiposEstadoReglaVolumen2));
		dto.setTipoEstadoIgual(this.tipoToMyPair(tiposEstadoReglaVolumen3));
		dto.setTipoEstadoDiferente(this.tipoToMyPair(tiposEstadoReglaVolumen4));
		dto.setTipoEstadoNinguno(this.tipoToMyPair(tiposEstadoReglaVolumen5));

		// ========== tarejtas ===================
		Hashtable<String, MyArray> txId = new Hashtable<>();

		List<Tipo> tTarjetas = rr.getTipos(Configuracion.ID_TIPO_TARJETA);
		for (Iterator iterator = tTarjetas.iterator(); iterator.hasNext();) {
			Tipo t = (Tipo) iterator.next();
			MyPair tMp = this.tipoToMyPair(t);

			String sigla = t.getSigla();
			MyArray d = txId.get(sigla);
			if (d == null) {
				d = new MyArray();
				d.setPos1(sigla);
				d.setPos2(new ArrayList());
				txId.put(sigla, d);
			}
			((ArrayList) d.getPos2()).add(tMp);
		}
		List<MyArray> listaTarjeta = new ArrayList<>();
		Enumeration<MyArray> enu = txId.elements();
		while (enu.hasMoreElements() == true) {
			listaTarjeta.add(enu.nextElement());
		}
		dto.setTarjetas(listaTarjeta);

		// ========================================

		// Tipos de Reservas
		List<Tipo> tiposReserva = rr.getTipos(Configuracion.ID_TIPO_RESERVA);
		List<MyPair> _tiposReserva = this.listaTiposToListaMyPair(tiposReserva);
		dto.setTiposDeReserva(_tiposReserva);

		Tipo reservaInterna = rr
				.getTipoPorSigla(Configuracion.SIGLA_RESERVA_INTERNA);
		Tipo reservaReparto = rr
				.getTipoPorSigla(Configuracion.SIGLA_RESERVA_REPARTO);
		Tipo reservaVenta = rr
				.getTipoPorSigla(Configuracion.SIGLA_RESERVA_VENTA);
		Tipo reservaDevolucion = rr
				.getTipoPorSigla(Configuracion.SIGLA_RESERVA_DEVOLUCION);
		MyPair _reservaInterna = this.tipoToMyPair(reservaInterna);
		MyPair _reservaReparto = this.tipoToMyPair(reservaReparto);
		MyPair _reservaVenta = this.tipoToMyPair(reservaVenta);
		MyPair _reservaDevolucion = this.tipoToMyPair(reservaDevolucion);
		dto.setReservaInterna(_reservaInterna);
		dto.setReservaReparto(_reservaReparto);
		dto.setReservaVenta(_reservaVenta);
		dto.setReservaDevolucion(_reservaDevolucion);

		// Estados de Reservas
		List<Tipo> estadosReserva = rr
				.getTipos(Configuracion.ID_TIPO_ESTADO_RESERVA);
		List<MyPair> _estadosReserva = this
				.listaTiposToListaMyPair(estadosReserva);
		dto.setEstadosDeReserva(_estadosReserva);

		Tipo estRes_Activa = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_RESERVA_ACTIVA);
		Tipo estRes_Cancelada = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_RESERVA_CANCELADA);
		Tipo estRes_Finalizada = rr
				.getTipoPorSigla(Configuracion.SIGLA_ESTADO_RESERVA_FINALIZADA);
		MyPair _estRes_Activa = this.tipoToMyPair(estRes_Activa);
		MyPair _estRes_Cancelada = this.tipoToMyPair(estRes_Cancelada);
		MyPair _estRes_Finalizada = this.tipoToMyPair(estRes_Finalizada);
		dto.setEstado_reservaActiva(_estRes_Activa);
		dto.setEstado_reservaCancelada(_estRes_Cancelada);
		dto.setEstado_reservaFinalizada(_estRes_Finalizada);

		// Motivos de Devolucion

		List<Tipo> motivosDevolucion = rr
				.getTipos(Configuracion.ID_TIPO_MOTIVO_DEVOLUCION);
		List<MyPair> _motivosDevolucion = this
				.listaTiposToListaMyPair(motivosDevolucion);
		dto.setMotivosDeDevolucion(_motivosDevolucion);
		Tipo motivoDevolucionDiaSiguiente = rr
				.getTipoPorSigla(Configuracion.SIGLA_MOTIVO_DEVOLUCION_DIA_SIGUIENTE);
		Tipo motivoDevolucionEntregaParcial = rr
				.getTipoPorSigla(Configuracion.SIGLA_MOTIVO_DEVOLUCION_ENTREGA_PARCIAL);
		Tipo motivoDevolucionDefectuoso = rr
				.getTipoPorSigla(Configuracion.SIGLA_MOTIVO_DEVOLUCION_DEFECTUOSO);
		MyPair _motivoDevolucionDiaSiguiente = this
				.tipoToMyPair(motivoDevolucionDiaSiguiente);
		MyPair _motivoDevolucionEntregaParcial = this
				.tipoToMyPair(motivoDevolucionEntregaParcial);
		MyPair _motivoDevolucionDefectuoso = this
				.tipoToMyPair(motivoDevolucionDefectuoso);
		dto.setMotivoDevolucionDiaSiguiente(_motivoDevolucionDiaSiguiente);
		dto.setMotivoDevolucionEntregaParcial(_motivoDevolucionEntregaParcial);
		dto.setMotivoDevolucionDefectuoso(_motivoDevolucionDefectuoso);

		// Tipos de Boleta Deposito
		List<Tipo> tiposBancoDeposito = rr
				.getTipos(Configuracion.ID_TIPO_BOLETA_DEPOSITO);
		Tipo bancoDepositoEfectivo = rr
				.getTipoPorSigla(Configuracion.SIGLA_BANCO_DEPOSITO_EFECTIVO);
		Tipo bancoDepositoChequesBanco = rr
				.getTipoPorSigla(Configuracion.SIGLA_BANCO_DEPOSITO_CHEQUES_BANCO);
		Tipo bancoDepositoChequesOtrosBancos = rr
				.getTipoPorSigla(Configuracion.SIGLA_BANCO_DEPOSITO_CHEQUES_OTRO_BANCO);
		Tipo bancoDepositoTodos = rr
				.getTipoPorSigla(Configuracion.SIGLA_BANCO_DEPOSITO_TODOS);

		dto.setTiposBancoDeposito(this
				.listaTiposToListaMyPair(tiposBancoDeposito));
		dto.setBancoDepositoEfectivo(this.tipoToMyPair(bancoDepositoEfectivo));
		dto.setBancoDepositoChequesBanco(this
				.tipoToMyPair(bancoDepositoChequesBanco));
		dto.setBancoDepositoChequesOtrosBancos(this
				.tipoToMyPair(bancoDepositoChequesOtrosBancos));
		dto.setBancoDepositoTodos(this.tipoToMyPair(bancoDepositoTodos));

		// tipos de Cambios
		List<Tipo> tiposCambio = rr.getTipos("Tipo Cambio");
		dto.setTiposCambio(this.listaTiposToListaMyPair(tiposCambio));

		Tipo tipoCambioAPP = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CAMBIO_APP);
		Tipo tipoCambioBCP = rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CAMBIO_BCP);

		dto.setCambioAPP(this.tipoToMyPair(tipoCambioAPP));
		dto.setCambioBCP(this.tipoToMyPair(tipoCambioBCP));

		// refrescar los valores de cotizacion
		MonedasUtil mu = new MonedasUtil();
		mu.refrescaValoresDeMonedas(dto);

		// Procesadoras de tarjetas..
		this.cargarProcesadoras(dto);

		// Datos por defecto para MRA
		this.cargarDatosMRA(dto);

		// modo de creación de un cheque
		dto.setModosDeCreacionCheque(this.listaTiposToListaMyPair(rr
				.getTipos(Configuracion.ID_TIPO_MODO_DE_CREACION_CHEQUE)));

		dto.setChequeAutomatico(this.tipoToMyPair(rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CHEQUE_AUTOMATICO)));
		dto.setChequeManual(this.tipoToMyPair(rr
				.getTipoPorSigla(Configuracion.SIGLA_TIPO_CHEQUE_MANUAL)));

		// Bancos para cheques de terceros
		List<Tipo> bancos = rr.getTipos(Configuracion.ID_TIPO_BANCOS_TERCEROS);
		dto.setBancosTerceros(this.listaTiposToListaMyPair(bancos));

		// Cliente ocasional
		Cliente _clienteOcasional = rr
				.getClienteByCodigoMRA(Configuracion.CODIGO_CLIENTE_OCASIONAL);
		MyPair clienteOcasional = new MyPair(_clienteOcasional.getId(),
				_clienteOcasional.getRazonSocial());
		dto.setClienteOcasional(clienteOcasional);

		return dto;
	}

	public void refrescaValoresTiposDeCambio(UtilDTO dto) throws Exception {
		MonedasUtil mu = new MonedasUtil();
		mu.refrescaValoresDeMonedas(dto);
	}

	public void refrescarBancos(UtilDTO dto) {
		utilDomainToListaMyArray(dto, "bancos",
				com.yhaguy.domain.Banco.class.getName(), new String[] {
						"descripcion", "direccion", "telefono", "correo",
						"contacto", "sucursales", "bancoTipo" });
	}

	/**
	 * Procesadoras de Tarjetas..
	 */
	@SuppressWarnings("unchecked")
	private void cargarProcesadoras(UtilDTO dto) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();

		List<ProcesadoraTarjeta> procesadoras = rr
				.getObjects(ProcesadoraTarjeta.class.getName());

		for (ProcesadoraTarjeta pr : procesadoras) {
			MyArray mpr = new MyArray();
			mpr.setId(pr.getId());
			mpr.setPos1(pr.getNombre());
			mpr.setPos2(new MyPair(pr.getSucursal().getId(), pr.getSucursal()
					.getDescripcion()));
			mpr.setPos3(new MyPair(pr.getBanco().getId(), pr.getBanco()
					.getBancoDescripcion()));

			MyArray bancoCtaMyArray = new MyArray();
			bancoCtaMyArray.setId(pr.getBanco().getId());
			bancoCtaMyArray.setPos1(pr.getBanco().getBancoDescripcion());
			bancoCtaMyArray.setPos2(pr.getBanco().getNroCuenta());
			mpr.setPos4(bancoCtaMyArray);

			dto.getProcesadoras().add(mpr);
		}
	}

	/**
	 * Setea datos por defecto, para la implementacion de MRA..
	 */
	private void cargarDatosMRA(UtilDTO dto) throws Exception {

		RegisterDomain rr = RegisterDomain.getInstance();
		Funcionario _vendedor = rr.getFuncionarioById(1);

		MyArray vendedor = new MyArray();
		vendedor.setId(_vendedor.getId());

		MyPair deposito = dto.getDepositosMyPair().get(0);
		MyPair sucursal = dto.getSucursalesMyPair().get(0);
		MyPair modoVenta = dto.getModoVenta_mostrador();

		dto.setMRA_deposito(deposito);
		dto.setMRA_modoVenta(modoVenta);
		dto.setMRA_sucursal(sucursal);
		dto.setMRA_vendedor(vendedor);
	}

	/**
	 * Retorna el TipoMovimiento en forma de MyArray pos1:descripcion -
	 * pos2:sigla - pos3:clase pos4:TipoIva - pos5:TipoEmpresa
	 * pos6:TipoComprobante pos7: tipoDocumento
	 * 
	 * @param tipoMov
	 * @return
	 */
	private MyArray tipoMovToMyArray(TipoMovimiento tipoMov) {
		MyArray tmov = new MyArray();
		MyPair tiva = new MyPair();
		MyPair temp = new MyPair();
		MyPair tcbt = new MyPair();
		MyPair tdoc = new MyPair();

		tiva.setId(tipoMov.getTipoIva().getId());
		tiva.setText(tipoMov.getTipoIva().getDescripcion());

		temp.setId(tipoMov.getTipoEmpresa().getId());
		temp.setText(tipoMov.getTipoEmpresa().getDescripcion());

		tcbt.setId(tipoMov.getTipoComprobante().getId());
		tcbt.setText(tipoMov.getTipoComprobante().getDescripcion());

		tdoc.setId(tipoMov.getTipoDocumento().getId());
		tdoc.setText(tipoMov.getTipoComprobante().getDescripcion());
		tdoc.setSigla(tipoMov.getTipoComprobante().getSigla());

		tmov.setId(tipoMov.getId());
		tmov.setPos1(tipoMov.getDescripcion());
		tmov.setPos2(tipoMov.getSigla());
		tmov.setPos3(tipoMov.getClase());
		tmov.setPos4(tiva);
		tmov.setPos5(temp);
		tmov.setPos6(tcbt);
		tmov.setPos7(tdoc);

		return tmov;
	}

	/**
	 * Retorna una lista de MyArray de Tipos de Movimiento pos1:descripcion -
	 * pos2:sigla - pos3:clase pos4:TipoIva - pos5:TipoEmpresa
	 * pos6:TipoComprobante pos7: tipoDocumento
	 * 
	 * @param list
	 *            TipoMovimiento
	 * @return list MyArray
	 */
	private List<MyArray> listTipoMovToListMyArray(List<TipoMovimiento> list) {
		List<MyArray> out = new ArrayList<MyArray>();

		for (TipoMovimiento movim : list) {

			MyArray tm = new MyArray();
			MyPair tiva = new MyPair();
			MyPair temp = new MyPair();
			MyPair tcbt = new MyPair();
			MyPair tdoc = new MyPair();

			tiva.setId(movim.getTipoIva().getId());
			tiva.setText(movim.getTipoIva().getDescripcion());

			temp.setId(movim.getTipoEmpresa().getId());
			temp.setText(movim.getTipoEmpresa().getDescripcion());

			tcbt.setId(movim.getTipoComprobante().getId());
			tcbt.setText(movim.getTipoComprobante().getDescripcion());

			tdoc.setId(movim.getTipoDocumento().getId());
			tdoc.setText(movim.getTipoComprobante().getDescripcion());
			tdoc.setSigla(movim.getTipoComprobante().getSigla());

			tm.setId(movim.getId());
			tm.setPos1(movim.getDescripcion());
			tm.setPos2(movim.getSigla());
			tm.setPos3(movim.getClase());
			tm.setPos4(tiva);
			tm.setPos5(temp);
			tm.setPos6(tcbt);
			tm.setPos7(tdoc);
			out.add(tm);
		}
		return out;
	}

	/**
	 * Convierte las condiciones de Pago en MyArray pos1:descripcion -
	 * pos2:plazo pos3:cuotas
	 */
	private MyArray condicionPagoToMyArray(CondicionPago condicion) {
		MyArray out = new MyArray();
		out.setId(condicion.getId());
		out.setPos1(condicion.getDescripcion());
		out.setPos2(condicion.getPlazo());
		out.setPos3(condicion.getCuotas());
		return out;
	}
}

/**
 * Centraliza el manejo de como se obtienen los valores de las monedas
 * 
 * @author daniel
 */
class MonedasUtil {

	/**
	 * Busca en la base de datos los valores de los últimos cambios de monedas y
	 * los carga en el util;
	 * 
	 * @param util
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void refrescaValoresDeMonedas(UtilDTO util) throws Exception {
		RegisterDomain rr = RegisterDomain.getInstance();
		Misc m = new Misc();

		Tipo tipoAPP = rr
				.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_APP);
		Tipo tipoBCP = rr
				.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP);

		util.borrarListaCambioMonedas();
		Date f = m.getFechaManana();

		List<MyPair> monedas = util.getMonedas();
		for (Iterator iterator = monedas.iterator(); iterator.hasNext();) {
			MyPair mp = (MyPair) iterator.next();
			CambioMoneda cm = new CambioMoneda();

			long idTipoMoneda = mp.getId();

			List<TipoCambio> lTcAPP = rr.getUltimosTipoCambio(idTipoMoneda,
					tipoAPP.getId(), f);
			List<TipoCambio> lTcBCP = rr.getUltimosTipoCambio(idTipoMoneda,
					tipoBCP.getId(), f);

			/**
			 * La idea es es cargar las últimas monedas, para agilizar la
			 * búsqueda para fechas diferentes Pero por ahora sólo nos quedamos
			 * con el último. for (Iterator iterator2 = lTcAPP.iterator();
			 * iterator2.hasNext();) { TipoCambio tcApp = (TipoCambio)
			 * iterator2.next();
			 * 
			 * }
			 * 
			 * 
			 */

			TipoCambio tcAPP = null;
			if (lTcAPP.size() > 0) {
				tcAPP = lTcAPP.get(0);
			}
			TipoCambio tcBCP = null;
			if (lTcBCP.size() > 0) {
				tcBCP = lTcBCP.get(0);
			}

			cm.setTipoMoneda(mp.getId());

			if (tcAPP != null) {
				cm.setAPPCompra(tcAPP.getCompra());
				cm.setAPPVenta(tcAPP.getVenta());
				cm.setAPPFecha(tcAPP.getFecha());
				cm.setAPPFechaCarga(tcAPP.getModificado());
			}
			if (tcBCP != null) {
				cm.setBCPCompra(tcBCP.getCompra());
				cm.setBCPVenta(tcBCP.getVenta());
				cm.setBCPFecha(tcBCP.getFecha());
				cm.setBCPFechaCarga(tcBCP.getModificado());
			}
			util.addCambioMoneda(cm);
		}
	}
}
