package com.yhaguy.util.population;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;

import com.coreweb.domain.Alerta;
import com.coreweb.domain.AlertaDestinos;
import com.coreweb.domain.AutoNumero;
import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.domain.Usuario;
import com.coreweb.util.Misc;
import com.coreweb.util.population.DBChistes;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloCosto;
import com.yhaguy.domain.ArticuloDeposito;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.ArticuloMarcaAplicacion;
import com.yhaguy.domain.ArticuloModeloAplicacion;
import com.yhaguy.domain.ArticuloPrecioReferencia;
import com.yhaguy.domain.ArticuloPresentacion;
import com.yhaguy.domain.ArticuloReferencia;
import com.yhaguy.domain.Banco;
import com.yhaguy.domain.BancoCheque;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.BancoMovimiento;
import com.yhaguy.domain.BancoSucursal;
import com.yhaguy.domain.Caja;
import com.yhaguy.domain.CajaPeriodo;
import com.yhaguy.domain.CentroCosto;
import com.yhaguy.domain.Cliente;
import com.yhaguy.domain.CompraLocalFactura;
import com.yhaguy.domain.CompraLocalFacturaDetalle;
import com.yhaguy.domain.CompraLocalGasto;
import com.yhaguy.domain.CompraLocalOrden;
import com.yhaguy.domain.CompraLocalOrdenDetalle;
import com.yhaguy.domain.Contacto;
import com.yhaguy.domain.ContactoInterno;
import com.yhaguy.domain.CtaCteEmpresa;
import com.yhaguy.domain.CtaCteEmpresaMovimiento;
import com.yhaguy.domain.CtaCteImputacion;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.DepartamentoApp;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.EmpresaGrupoSociedad;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.Gasto;
import com.yhaguy.domain.GastoDetalle;
import com.yhaguy.domain.ImportacionFactura;
import com.yhaguy.domain.ImportacionFacturaDetalle;
import com.yhaguy.domain.ImportacionPedidoCompra;
import com.yhaguy.domain.ImportacionPedidoCompraDetalle;
import com.yhaguy.domain.ImportacionResumenGastosDespacho;
import com.yhaguy.domain.NotaCredito;
import com.yhaguy.domain.OrdenPedidoGasto;
import com.yhaguy.domain.OrdenPedidoGastoDetalle;
import com.yhaguy.domain.Proveedor;
import com.yhaguy.domain.ProveedorArticulo;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.Reparto;
import com.yhaguy.domain.RepartoDetalle;
import com.yhaguy.domain.Reserva;
import com.yhaguy.domain.ReservaDetalle;
import com.yhaguy.domain.SubDiario;
import com.yhaguy.domain.SubDiarioDetalle;
import com.yhaguy.domain.Sucursal;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.Timbrado;
import com.yhaguy.domain.TipoCambio;
import com.yhaguy.domain.TipoMovimiento;
import com.yhaguy.domain.Transferencia;
import com.yhaguy.domain.TransferenciaDetalle;
import com.yhaguy.domain.Transporte;
import com.yhaguy.domain.Vehiculo;
import com.yhaguy.domain.Venta;
import com.yhaguy.domain.VentaDetalle;
import com.yhaguy.util.migracion.MigracionPlanDeCuentas;

public class DBPopulation {

	private static RegisterDomain rr = RegisterDomain.getInstance();

	private static void grabarDB(Domain d) throws Exception {
		rr.saveObject(d, Configuracion.USER_SYSTEMA);
	}

	// Esta clase se encarga de Inicializar (Cargar Registros) a la Base de
	// Datos de Pruebas

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		
		//Session s = Sessions.getCurrent(false);
		//s.setAttribute(Config.USUARIO, "PoPu");
		
		//ControlAgendaEvento agenda = new ControlAgendaEvento();

		long ceroTime = System.currentTimeMillis();

		rr.dropAllTables();
		long iniTime = System.currentTimeMillis();

		// Primero cargar todos los tipos
		DBPopulationTipos tt = new DBPopulationTipos();
		tt.cargaTipos();
	
		DBVistas.main(null);


		MigracionPlanDeCuentas.main(null);
		
		// ----------------------------14----------------------------------------------------------------------
		// Menus, Usuario y Perfil
		// DBMenuUser menus = new DBMenuUser();
		// menus.testAddObject();
		UsuarioPerfilParser.loadMenuConfig();
		// DBMenuUserParser menus = new DBMenuUserParser();
		// menus.cargaMenusPerfilesUsuarios();

		// --------------------------------------------------------------------------------------------------

		/************************************** SUCURSALES **************************************/

		SucursalApp sucApp1 = new SucursalApp();
		sucApp1.setNombre("Central");
		sucApp1.setDescripcion("Casa Central");
		sucApp1.setEstablecimiento("1");
		sucApp1.setDireccion("Mcal. Estigarribia");
		sucApp1.setTelefono("111-222");

		SucursalApp sucApp2 = new SucursalApp();
		sucApp2.setNombre("Florentin");
		sucApp2.setDescripcion("Sucursal Florentin");
		sucApp2.setEstablecimiento("2");
		sucApp2.setDireccion("Direccion 2");
		sucApp2.setTelefono("222-333");

		SucursalApp sucApp3 = new SucursalApp();
		sucApp3.setNombre("Baterias");
		sucApp3.setDescripcion("Sucursal Baterias");
		sucApp3.setEstablecimiento("3");
		sucApp3.setDireccion("Direccion 3");
		sucApp3.setTelefono("333-444");

		grabarDB(sucApp1);
		grabarDB(sucApp2);
		grabarDB(sucApp3);

		Set<SucursalApp> sucursalesApp = new HashSet<SucursalApp>();
		sucursalesApp.add(sucApp1);
		sucursalesApp.add(sucApp2);
		sucursalesApp.add(sucApp3);

		/************************************* DEPOSITOS **********************************/

		Deposito dep1 = new Deposito();
		dep1.setDescripcion("S1-D1");
		dep1.setObservacion("Descripcion 1");

		Deposito dep2 = new Deposito();
		dep2.setDescripcion("S1-D2");
		dep2.setObservacion("Descripcion 2");

		Deposito dep3 = new Deposito();
		dep3.setDescripcion("S1-D3");
		dep3.setObservacion("Descripcion 3");

		grabarDB(dep1);
		grabarDB(dep2);
		grabarDB(dep3);

		Set<Deposito> depositos = new HashSet<Deposito>();
		depositos.add(dep1);
		depositos.add(dep2);
		depositos.add(dep3);
		sucApp1.setDepositos(depositos);

		Deposito dep4 = new Deposito();
		dep4.setDescripcion("S2-D1");
		dep4.setObservacion("Descripcion 1");

		Deposito dep5 = new Deposito();
		dep5.setDescripcion("S2-D2");
		dep5.setObservacion("Descripcion 2");

		grabarDB(dep4);
		grabarDB(dep5);

		Set<Deposito> depositos2 = new HashSet<Deposito>();
		depositos2.add(dep4);
		depositos2.add(dep5);
		sucApp2.setDepositos(depositos2);

		Deposito dep6 = new Deposito();
		dep6.setDescripcion("S3-D1");
		dep6.setObservacion("Descripcion 1");

		Deposito dep7 = new Deposito();
		dep7.setDescripcion("S3-D2");
		dep7.setObservacion("Descripcion 2");

		Deposito dep8 = new Deposito();
		dep8.setDescripcion("S3-D3");
		dep8.setObservacion("Descripcion 3");

		grabarDB(dep6);
		grabarDB(dep7);
		grabarDB(dep8);

		Set<Deposito> depositos3 = new HashSet<Deposito>();
		depositos3.add(dep6);
		depositos3.add(dep7);
		depositos3.add(dep8);
		sucApp3.setDepositos(depositos3);

		grabarDB(sucApp1);
		grabarDB(sucApp2);
		grabarDB(sucApp3);

		/************************************* CUENTAS CONTABLES **********************************/

		CuentaContable ct1 = new CuentaContable();
		ct1.setCodigo("CT-0001");
		ct1.setDescripcion("IMPORTACION EN CURSO");
		ct1.setAlias(Configuracion.ALIAS_CUENTA_IMPORTACION_EN_CURSO);
		grabarDB(ct1);

		CuentaContable ct2 = new CuentaContable();
		ct2.setCodigo("CT-0002");
		ct2.setDescripcion("IVA CRÉDITO FISCAL");
		ct2.setAlias(Configuracion.ALIAS_CUENTA_IVA_10_CF);
		grabarDB(ct2);

		CuentaContable ct3 = new CuentaContable();
		ct3.setCodigo("CT-0003");
		ct3.setDescripcion("EQUIPOS DE INFORMATICA");
		ct3.setAlias("A-EQU-INFORMATICA");
		grabarDB(ct3);

		CuentaContable ct4 = new CuentaContable();
		ct4.setCodigo("CT-0004");
		ct4.setDescripcion("AGENCIA CELIA LOPEZ GODOY");
		ct4.setAlias("P-PRV-LOPEZGODOY");
		grabarDB(ct4);

		CuentaContable ct6 = new CuentaContable();
		ct6.setCodigo("CT-0006");
		ct6.setDescripcion("VALVOLINE S.A.");
		ct6.setAlias("P-PRV-VALVOLINE");
		grabarDB(ct6);

		CuentaContable ct7 = new CuentaContable();
		ct7.setCodigo("CT-0007");
		ct7.setDescripcion("MERCADERÍAS GRAVADAS");
		ct7.setAlias(Configuracion.ALIAS_CUENTA_MERCADERIAS_GRAVADAS);
		grabarDB(ct7);

		CuentaContable ct9 = new CuentaContable();
		ct9.setCodigo("CT-0009");
		ct9.setDescripcion("TRANS-MULTIMARCAS");
		ct9.setAlias("P-PRV-TRANSMULTIMARCAS");
		grabarDB(ct9);

		CuentaContable ct10 = new CuentaContable();
		ct10.setCodigo("CT-0010");
		ct10.setDescripcion("KS PRODUCTOS");
		ct10.setAlias("P-PRV-KSPRODUCTOS");
		grabarDB(ct10);

		CuentaContable ct11 = new CuentaContable();
		ct11.setCodigo("CT-0011");
		ct11.setDescripcion("PARTNER S.A.");
		ct11.setAlias("P-PRV-PARTNER");
		grabarDB(ct11);

		CuentaContable ct12 = new CuentaContable();
		ct12.setCodigo("CT-0012");
		ct12.setDescripcion("UTILES E INSUMOS DE OFICINA");
		ct12.setAlias("A-UT-OF");
		grabarDB(ct12);

		CuentaContable ct13 = new CuentaContable();
		ct13.setCodigo("CT-0013");
		ct13.setDescripcion("AGUA LUZ TELEFONO");
		ct13.setAlias("A-AG-LUZ-TEL");
		grabarDB(ct13);

		CuentaContable ct14 = new CuentaContable();
		ct14.setCodigo("CT-0014");
		ct14.setDescripcion("DESCUENTOS OBTENIDOS");
		ct14.setAlias("A-DESC-OBT");
		grabarDB(ct14);
		
		CuentaContable ct15 = new CuentaContable();
		ct15.setCodigo("CT-0015");
		ct15.setDescripcion("GASTOS DE IMPORTACIÓN");
		ct15.setAlias("P-GAST-IMP");
		grabarDB(ct15);
		
		CuentaContable ct16 = new CuentaContable();
		ct16.setCodigo("CT-0016");
		ct16.setDescripcion("DERECHO ADUANERO");
		ct16.setAlias("P-GAST-IMP-DER-ADU");
		grabarDB(ct16);
		
		CuentaContable ct17 = new CuentaContable();
		ct17.setCodigo("CT-0017");
		ct17.setDescripcion("TAZA INDI");
		ct17.setAlias("P-GAST-IMP-INDI");
		grabarDB(ct17);
		
		CuentaContable ct18 = new CuentaContable();
		ct18.setCodigo("CT-0018");
		ct18.setDescripcion("SERVICIO DE VALORACION");
		ct18.setAlias("P-GAST-IMP-SERV-VAL");
		grabarDB(ct18);
		
		CuentaContable ct19 = new CuentaContable();
		ct19.setCodigo("CT-0019");
		ct19.setDescripcion("IVA DESPACHO IMPORTACIÓN");
		ct19.setAlias("P-GAST-IMP-IVA-DESP");
		grabarDB(ct19);
		
		CuentaContable ct20 = new CuentaContable();
		ct20.setCodigo("CT-0020");
		ct20.setDescripcion("CANON INFORMÁTICO");
		ct20.setAlias("P-GAST-IMP-CAN-INF");
		grabarDB(ct20);
		


		/************************************** DEPARTAMENTOS **************************************/

		// Asignacion de Centros de Costo a los Departamentos
		Set<CentroCosto> ccs1 = new HashSet<CentroCosto>();
		Set<CentroCosto> ccs2 = new HashSet<CentroCosto>();
		Set<CentroCosto> ccs3 = new HashSet<CentroCosto>();
		Set<CentroCosto> ccs4 = new HashSet<CentroCosto>();
		Set<CentroCosto> ccs5 = new HashSet<CentroCosto>();

		CentroCosto cc1 = new CentroCosto();
		cc1.setNumero("CC-00001");
		cc1.setDescripcion("UTILES E INSUMOS - ADMINISTRACION");
		cc1.setMontoAsignado(1500000);
		ccs1.add(cc1);

		CentroCosto cc2 = new CentroCosto();
		cc2.setNumero("CC-00002");
		cc2.setDescripcion("GASTOS IMPREVISTOS - ADMINISTRACION");
		cc2.setMontoAsignado(1000000);
		ccs1.add(cc2);

		CentroCosto cc3 = new CentroCosto();
		cc3.setNumero("CC-00003");
		cc3.setDescripcion("UTILES E INSUMOS - RR.HH");
		cc3.setMontoAsignado(1500000);
		ccs2.add(cc3);

		CentroCosto cc4 = new CentroCosto();
		cc4.setNumero("CC-00004");
		cc4.setDescripcion("GASTOS IMPREVISTOS - RR.HH");
		cc4.setMontoAsignado(1500000);
		ccs2.add(cc4);

		CentroCosto cc5 = new CentroCosto();
		cc5.setNumero("CC-00005");
		cc5.setDescripcion("UTILES E INSUMOS - INFORMATICA");
		cc5.setMontoAsignado(1500000);
		ccs3.add(cc5);

		CentroCosto cc6 = new CentroCosto();
		cc6.setNumero("CC-00006");
		cc6.setDescripcion("GASTOS IMPREVISTOS - INFORMATICA");
		cc6.setMontoAsignado(1500000);
		ccs3.add(cc6);

		CentroCosto cc7 = new CentroCosto();
		cc7.setNumero("CC-00007");
		cc7.setDescripcion("UTILES E INSUMOS - VENTAS");
		cc7.setMontoAsignado(1500000);
		ccs4.add(cc7);

		CentroCosto cc8 = new CentroCosto();
		cc8.setNumero("CC-00008");
		cc8.setDescripcion("GASTOS IMPREVISTOS - VENTAS");
		cc8.setMontoAsignado(1500000);
		ccs4.add(cc8);

		CentroCosto cc9 = new CentroCosto();
		cc9.setNumero("CC-00009");
		cc9.setDescripcion("GASTOS IMPORTACION - COMPRAS");
		cc9.setMontoAsignado(15000000);
		ccs5.add(cc9);

		// Asignacion de Cuentas a los Departamentos
		Set<CuentaContable> ctas1 = new HashSet<CuentaContable>();
		ctas1.add(ct3);
		ctas1.add(ct12);
		ctas1.add(ct15);

		Set<CuentaContable> ctas2 = new HashSet<CuentaContable>();
		ctas2.add(ct3);
		ctas2.add(ct12);
		ctas2.add(ct15);

		Set<CuentaContable> ctas3 = new HashSet<CuentaContable>();
		ctas3.add(ct3);
		ctas3.add(ct12);
		ctas3.add(ct15);

		Set<CuentaContable> ctas4 = new HashSet<CuentaContable>();
		ctas4.add(ct15);

		DepartamentoApp dto1 = new DepartamentoApp();
		dto1.setNombre("Administracion");
		dto1.setDescripcion("Departamento de Administración");
		dto1.setSucursal(sucApp1);
		dto1.setCentroCostos(ccs1);
		dto1.setCuentas(ctas1);

		DepartamentoApp dto2 = new DepartamentoApp();
		dto2.setNombre("Recursos Humanos");
		dto2.setDescripcion("Departamento de RRHH");
		dto2.setSucursal(sucApp2);
		dto2.setCentroCostos(ccs2);
		dto2.setCuentas(ctas2);

		DepartamentoApp dto3 = new DepartamentoApp();
		dto3.setNombre("Informática");
		dto3.setDescripcion("Departamento de Informática");
		dto3.setSucursal(sucApp1);
		dto3.setCentroCostos(ccs3);
		dto3.setCuentas(ctas3);

		DepartamentoApp dto4 = new DepartamentoApp();
		dto4.setNombre("Ventas");
		dto4.setDescripcion("Departamento de Ventas");
		dto4.setSucursal(sucApp1);
		dto4.setCentroCostos(ccs4);
		dto4.setCuentas(ctas3);

		DepartamentoApp dto5 = new DepartamentoApp();
		dto5.setNombre("Compras");
		dto5.setDescripcion("Departamento de Compras");
		dto5.setSucursal(sucApp1);
		dto5.setCentroCostos(ccs5);
		dto5.setCuentas(ctas4);

		grabarDB(dto1);
		grabarDB(dto2);
		grabarDB(dto3);
		grabarDB(dto4);
		grabarDB(dto5);

		/************************************** ACCESOS **************************************/

		Usuario usr1 = (Usuario) rr.getObject(
				com.coreweb.domain.Usuario.class.getName(), new Long(1));
		Usuario usr2 = (Usuario) rr.getObject(
				com.coreweb.domain.Usuario.class.getName(), new Long(2));
		Usuario usr3 = (Usuario) rr.getObject(
				com.coreweb.domain.Usuario.class.getName(), new Long(3));
		Usuario usr4 = (Usuario) rr.getObject(
				com.coreweb.domain.Usuario.class.getName(), new Long(6)); // Usuario
																			// celia..
		Usuario usr5 = (Usuario) rr.getObject(
				com.coreweb.domain.Usuario.class.getName(), new Long(10)); // Usuario
																			// clientes
		Usuario usr6 = (Usuario) rr.getObject(
				com.coreweb.domain.Usuario.class.getName(), new Long(12)); // Usuario
																			// proveedores
		Usuario usr7 = (Usuario) rr.getObject(
				com.coreweb.domain.Usuario.class.getName(), new Long(13)); // Usuario
																			// articulos

		AccesoApp ac1 = new AccesoApp();
		ac1.setDescripcion("acceso 1");
		ac1.setDepartamento(dto1);
		ac1.setSucursales(sucursalesApp);
		ac1.setUsuario(usr1);

		grabarDB(ac1);

		Set<AccesoApp> accesosApp = new HashSet<AccesoApp>();
		accesosApp.add(ac1);

		AccesoApp ac2 = new AccesoApp();
		ac2.setDescripcion("acceso 2");
		ac2.setDepartamento(dto1);
		ac2.setSucursales(sucursalesApp);
		ac2.setUsuario(usr2);

		grabarDB(ac2);

		// acceso sin sucursales
		AccesoApp ac3 = new AccesoApp();
		ac3.setDescripcion("acceso 3");
		ac3.setDepartamento(dto3);
		ac3.setUsuario(usr3);

		grabarDB(ac3);

		AccesoApp ac4 = new AccesoApp();
		ac4.setDescripcion("acceso 4");
		ac4.setDepartamento(dto1);
		ac4.setSucursales(sucursalesApp);
		ac4.setUsuario(usr4);

		AccesoApp ac5 = new AccesoApp();
		ac5.setDescripcion("acceso 5");
		ac5.setDepartamento(dto1);
		ac5.setUsuario(usr5);

		AccesoApp ac6 = new AccesoApp();
		ac6.setDescripcion("acceso 6");
		ac6.setDepartamento(dto1);
		ac6.setUsuario(usr6);

		AccesoApp ac7 = new AccesoApp();
		ac7.setDescripcion("acceso 7");
		ac7.setDepartamento(dto1);
		ac7.setUsuario(usr7);

		grabarDB(ac5);
		grabarDB(ac6);
		grabarDB(ac7);

		Set<AccesoApp> accesosApp2 = new HashSet<AccesoApp>();
		accesosApp2.add(ac2);
		accesosApp2.add(ac3);
		accesosApp2.add(ac4);
		accesosApp2.add(ac5);
		accesosApp2.add(ac6);
		accesosApp2.add(ac7);

		/******************************************* B.D. CLIENTES **********************************************/

		EmpresaGrupoSociedad egs2 = new EmpresaGrupoSociedad();
		egs2.setDescripcion("GRUPO AUTOMOTOR S.A.");
		grabarDB(egs2);

		EmpresaGrupoSociedad egs3 = new EmpresaGrupoSociedad();
		egs3.setDescripcion("GRUPO TOYOTOSHI");
		grabarDB(egs3);

		EmpresaGrupoSociedad egs4 = new EmpresaGrupoSociedad();
		egs4.setDescripcion("GRUPO HYUNDAI");
		grabarDB(egs4);

		
		CtaCteLineaCredito l1 = new CtaCteLineaCredito();
		l1.setLinea(5000000);
		l1.setDescripcion("A");
		grabarDB(l1);

		
		CtaCteLineaCredito l2 = new CtaCteLineaCredito();
		l2.setLinea(10000000);
		l2.setDescripcion("B");
		grabarDB(l2);

		Set<Tipo> monedas1 = new HashSet<Tipo>();
		Set<Tipo> monedas2 = new HashSet<Tipo>();
		Set<Tipo> monedas3 = new HashSet<Tipo>();
		Set<Tipo> monedas4 = new HashSet<Tipo>();

		monedas1.add(tt.guarani);
		monedas1.add(tt.dolar);
		monedas1.add(tt.peso);

		monedas2.add(tt.guarani);
		monedas2.add(tt.dolar);

		monedas3.add(tt.guarani);
		monedas3.add(tt.dolar);

		monedas4.add(tt.guarani);
		monedas4.add(tt.peso);
		
		// CUENTAS CONTABLES..
		CuentaContable ct5 = new CuentaContable();
		CuentaContable ct8 = new CuentaContable();
		
		ct8.setCodigo("CT-0008");
		ct8.setDescripcion("CLIENTES OCASIONALES");
		ct8.setAlias(Configuracion.ALIAS_CUENTA_CLIENTES_OCASIONALES);
		grabarDB(ct8);

		ct5.setCodigo("CT-0005");
		ct5.setDescripcion("PROVEEDORES VARIOS");
		ct5.setAlias(Configuracion.ALIAS_CUENTA_PROVEEDORES_VARIOS);
		grabarDB(ct5);

		// TIMBRADOS..
		Set<Timbrado> timbrados1 = new HashSet<Timbrado>();

		Timbrado t1 = new Timbrado();
		t1.setNumero("150100");
		t1.setVencimiento(DateUtils.addDays(new Date(), 5));
		timbrados1.add(t1);

		Timbrado t2 = new Timbrado();
		t2.setNumero("150102");
		t2.setVencimiento(DateUtils.addDays(new Date(), 5));
		timbrados1.add(t2);

		Timbrado t3 = new Timbrado();
		t3.setNumero("170102");
		t3.setVencimiento(DateUtils.addDays(new Date(), 5));
		timbrados1.add(t3);

		// Chequeado ok
		Sucursal suc1 = new Sucursal();
		suc1.setNombre("Sucursal 1-1");
		suc1.setTelefono("210 211");
		suc1.setDireccion("Bella Vista 320");
		suc1.setCorreo("suc1@correo");

		Sucursal suc2 = new Sucursal();
		suc2.setNombre("Sucursal 1-2");
		suc2.setTelefono("210 212");
		suc2.setDireccion("Eusebio Ayala 320");
		suc2.setCorreo("suc2@correo");

		// Chequeado ok
		Sucursal suc3 = new Sucursal();
		suc3.setNombre("Sucursal 1");
		suc3.setTelefono("210 211");
		suc3.setDireccion("Bella Vista 320");
		suc3.setCorreo("suc3@correo");

		// Chequeado ok
		Sucursal suc4 = new Sucursal();
		suc4.setNombre("Sucursal 2");
		suc4.setTelefono("210 212");
		suc4.setDireccion("Eusebio Ayala 320");
		suc4.setCorreo("suc4@correo");

		// Chequeado ok
		Empresa empresa5 = new Empresa();
		empresa5.setNombre("Perez, Juan");
		empresa5.setCodigoEmpresa("5");
		empresa5.setFechaIngreso(new Date());
		empresa5.setObservacion("sin obs");
		empresa5.setRazonSocial("Juan Perez");
		empresa5.getRubroEmpresas().add(tt.rubroEmpresaFuncionario);
		empresa5.setRuc("11111111-9");
		empresa5.setMoneda(tt.guarani);
		empresa5.setMonedas(monedas3);
		empresa5.setPais(tt.paisPry);
		empresa5.setSigla("NDF");
		empresa5.setTipoPersona(tt.personaFisica);
		grabarDB(empresa5);

		// Chequeado ok
		Empresa empresa6 = new Empresa();
		empresa6.setNombre("Sanchez, Sixto");
		empresa6.setFechaIngreso(new Date());
		empresa6.setObservacion("sin obs");
		empresa6.setRazonSocial("Sixto S");
		empresa6.getRubroEmpresas().add(tt.rubroEmpresaFuncionario);
		empresa6.setRuc("22222222-9");
		empresa6.setMonedas(monedas4);
		empresa6.setMoneda(tt.guarani);
		empresa6.setPais(tt.paisPry);
		empresa6.setSigla("NDF");
		empresa6.setTipoPersona(tt.personaFisica);
		grabarDB(empresa6);

		Empresa empresa13 = new Empresa();
		empresa13.setNombre("Aguilar, Diana");
		empresa13.setFechaIngreso(new Date());
		empresa13.setObservacion("sin obs");
		empresa13.setRazonSocial("Diana A.");
		empresa13.getRubroEmpresas().add(tt.rubroEmpresaFuncionario);
		empresa13.setRuc("33333333-9");
		empresa13.setMoneda(tt.guarani);
		empresa13.setMonedas(monedas4);
		empresa13.setPais(tt.paisPry);
		empresa13.setSigla("NDF");
		empresa13.setTipoPersona(tt.personaFisica);
		grabarDB(empresa13);

		Empresa empresa14 = new Empresa();
		empresa14.setNombre("Alvarenga, Celia");
		empresa14.setFechaIngreso(new Date());
		empresa14.setObservacion("sin obs");
		empresa14.setRazonSocial("Celia A.");
		empresa14.getRubroEmpresas().add(tt.rubroEmpresaFuncionario);
		empresa14.setRuc("44444444-9");
		empresa14.setMoneda(tt.guarani);
		empresa14.setMonedas(monedas4);
		empresa14.setPais(tt.paisPry);
		empresa14.setSigla("NDF");
		empresa14.setTipoPersona(tt.personaFisica);
		grabarDB(empresa14);

		Empresa empresa15 = new Empresa();
		empresa15.setNombre("Alviso, Rodrigo");
		empresa15.setFechaIngreso(new Date());
		empresa15.setObservacion("sin obs");
		empresa15.setRazonSocial("Rodrigo A.");
		empresa15.getRubroEmpresas().add(tt.rubroEmpresaFuncionario);
		empresa15.setRuc("5555555-9");
		empresa15.setMoneda(tt.guarani);
		empresa15.setMonedas(monedas4);
		empresa15.setPais(tt.paisPry);
		empresa15.setSigla("NDF");
		empresa15.setTipoPersona(tt.personaFisica);
		grabarDB(empresa15);

		Empresa empresa16 = new Empresa();
		empresa16.setNombre("Paredes, Celia");
		empresa16.setFechaIngreso(new Date());
		empresa16.setObservacion("sin obs");
		empresa16.setRazonSocial("Celia Paredes");
		empresa16.getRubroEmpresas().add(tt.rubroEmpresaFuncionario);
		empresa16.setRuc("6666666-9");
		empresa16.setMoneda(tt.guarani);
		empresa16.setMonedas(monedas4);
		empresa16.setPais(tt.paisPry);
		empresa16.setSigla("NDF");
		empresa16.setTipoPersona(tt.personaFisica);
		grabarDB(empresa16);

		// Chequeado ok
		Funcionario funcionario1 = new Funcionario();
		funcionario1.setEmpresa(empresa5);
		funcionario1.setFuncionarioCargo(tt.cargoAuxiliarAdministrativo);
		funcionario1.setFuncionarioEstado(tt.funcionarioEstadoActivo);
		// funcionario1.setFuncionarioCargo(funcionarioCargo3);
		// funcionario1.setFuncionarioEstado(funcionarioEstado1);
		funcionario1.setAccesos(accesosApp);
		grabarDB(funcionario1);

		// Chequeado ok
		Funcionario funcionario2 = new Funcionario();
		funcionario2.setEmpresa(empresa6);
		funcionario2.setFuncionarioCargo(tt.cargoAuxiliarAdministrativo);
		funcionario2.setFuncionarioEstado(tt.funcionarioEstadoActivo);
		// funcionario2.setFuncionarioCargo(funcionarioCargo2);
		// funcionario2.setFuncionarioEstado(funcionarioEstado1);
		funcionario2.setAccesos(accesosApp2);
		grabarDB(funcionario2);

		Funcionario funcionario3 = new Funcionario();
		funcionario3.setEmpresa(empresa13);
		funcionario3.setFuncionarioCargo(tt.cargoAuxiliarAdministrativo);
		funcionario3.setFuncionarioEstado(tt.funcionarioEstadoActivo);
		// funcionario3.setFuncionarioCargo(funcionarioCargo5);
		// funcionario3.setFuncionarioEstado(funcionarioEstado1);
		funcionario3.setAccesos(accesosApp2);
		grabarDB(funcionario3);

		Funcionario funcionario4 = new Funcionario();
		funcionario4.setEmpresa(empresa14);
		funcionario4.setFuncionarioCargo(tt.cargoAuxiliarAdministrativo);
		funcionario4.setFuncionarioEstado(tt.funcionarioEstadoActivo);
		// funcionario4.setFuncionarioCargo(funcionarioCargo5);
		// funcionario4.setFuncionarioEstado(funcionarioEstado1);
		funcionario4.setAccesos(accesosApp2);
		grabarDB(funcionario4);

		Funcionario funcionario5 = new Funcionario();
		funcionario5.setEmpresa(empresa15);
		funcionario5.setFuncionarioCargo(tt.cargoAuxiliarAdministrativo);
		funcionario5.setFuncionarioEstado(tt.funcionarioEstadoActivo);
		// funcionario5.setFuncionarioCargo(funcionarioCargo5);
		// funcionario5.setFuncionarioEstado(funcionarioEstado1);
		funcionario5.setAccesos(accesosApp2);
		grabarDB(funcionario5);

		Funcionario funcionario6 = new Funcionario();
		funcionario6.setEmpresa(empresa16);
		funcionario6.setFuncionarioCargo(tt.cargoAuxiliarAdministrativo);
		funcionario6.setFuncionarioEstado(tt.funcionarioEstadoActivo);
		// funcionario6.setFuncionarioCargo(funcionarioCargo5);
		// funcionario6.setFuncionarioEstado(funcionarioEstado1);
		funcionario6.setAccesos(accesosApp2);
		grabarDB(funcionario6);

		// Chequeado ok
		Empresa empresa1 = new Empresa();
		empresa1.setNombre("Valvoline");
		empresa1.setCodigoEmpresa("1");
		empresa1.setFechaIngreso(new Date());
		empresa1.setObservacion("sin obs");
		empresa1.setRazonSocial("VALVOLINE S.A.");
		empresa1.getRubroEmpresas().add(tt.rubroEmpresaDistribuidorLubricantes);
		empresa1.setRuc(Configuracion.RUC_EMPRESA_EXTERIOR);
		empresa1.setMoneda(tt.dolar);
		empresa1.setMonedas(monedas1);
		empresa1.getSucursales().add(suc1);
		empresa1.getSucursales().add(suc2);
		empresa1.getSucursales().add(suc3);
		empresa1.setPais(tt.paisArg);
		empresa1.setSigla("VAL");
		empresa1.setTipoPersona(tt.personaJuridica);
		
		CtaCteEmpresa cce1 = new CtaCteEmpresa();
		cce1.setLineaCredito(l1);
		cce1.setEstadoComoCliente(tt.ctaCteEmpresaEstado1);
		cce1.setEstadoComoProveedor(tt.ctaCteEmpresaEstado1);
		cce1.setFechaAperturaCuentaCliente(new Date());
		cce1.setFechaAperturaCuentaProveedor(new Date());
		grabarDB(cce1);
		empresa1.setCtaCteEmpresa(cce1);
		grabarDB(empresa1);

		// Chequeado ok
		Empresa empresa2 = new Empresa();
		empresa2.setCodigoEmpresa("2");
		empresa2.setNombre("KS PRODUCTOS");
		empresa2.setFechaIngreso(new Date());
		empresa2.setObservacion("sin obs");
		empresa2.setRazonSocial("KS PRODUCTOS");
		empresa1.getRubroEmpresas().add(tt.rubroEmpresaDistribuidorBaterias);
		empresa2.setRuc(Configuracion.RUC_EMPRESA_EXTERIOR);
		empresa2.setMoneda(tt.dolar);
		empresa2.setMonedas(monedas2);
		empresa2.setPais(tt.paisBra);
		empresa2.setSigla("KS");
		empresa2.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa2);

		// Chequeado ok
		Proveedor prov1 = new Proveedor();
		prov1.setEmpresa(empresa1);
		prov1.setEstadoProveedor(tt.tipoProvEstado1);
		prov1.setTipoProveedor(tt.tipoProveedor1);
		prov1.setCuentaContable(ct6);
		grabarDB(prov1);		

		// Chequeado ok
		Proveedor prov2 = new Proveedor();
		prov2.setEmpresa(empresa2);
		prov2.setEstadoProveedor(tt.tipoProvEstado1);
		prov2.setTipoProveedor(tt.tipoProveedor1);
		prov2.setCuentaContable(ct10);
		grabarDB(prov2);

		// Chequeado ok
		Empresa empresa7 = new Empresa();
		empresa7.setCodigoEmpresa("7");
		empresa7.setNombre("Continental do Brasil");
		empresa7.setFechaIngreso(new Date());
		empresa7.setObservacion("sin obs");
		empresa7.setRazonSocial("CONTINENTAL BRASIL");
		empresa7.getRubroEmpresas().add(tt.rubroEmpresaDistribuidorRepuestos);
		empresa7.setRuc(Configuracion.RUC_EMPRESA_EXTERIOR);
		empresa7.setMoneda(tt.dolar);
		empresa7.setMonedas(monedas1);
		empresa7.setPais(tt.paisBra);
		empresa7.setSigla("CBR");
		empresa7.setTipoPersona(tt.personaJuridica);
		
		CtaCteEmpresa cce3 = new CtaCteEmpresa();
		cce3.setLineaCredito(l2);
		cce3.setEstadoComoCliente(tt.ctaCteEmpresaEstado3);
		cce3.setFechaAperturaCuentaCliente(new Date());
		cce3.setFechaAperturaCuentaProveedor(new Date());
		grabarDB(cce3);
		
		empresa7.setCtaCteEmpresa(cce3);
		
		grabarDB(empresa7);

		Timbrado t = new Timbrado();
		t.setNumero("100102");
		t.setVencimiento(new Date());
		grabarDB(t);

		// Chequeado ok
		Proveedor prov3 = new Proveedor();
		prov3.setEmpresa(empresa7);
		prov3.setEstadoProveedor(tt.tipoProvEstado1);
		prov3.setTipoProveedor(tt.tipoProveedor1);
		prov3.setCuentaContable(ct10);
		prov3.getTimbrados().add(t);
		grabarDB(prov3);

		// Chequeado ok
		Empresa empresa8 = new Empresa();
		empresa8.setCodigoEmpresa("8");
		empresa8.setNombre("Giti");
		empresa8.setFechaIngreso(new Date());
		empresa8.setObservacion("sin obs");
		empresa8.setRazonSocial("GITI TIRE S.A.");
		empresa8.getRubroEmpresas().add(tt.rubroEmpresaDistribuidorCubiertas);
		empresa8.setRuc("99999999-8");
		empresa8.setMoneda(tt.guarani);
		empresa8.setMonedas(monedas1);
		empresa8.setPais(tt.paisUsa);
		empresa8.setSigla("GTI");
		empresa8.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa8);

		// Chequeado ok
		Proveedor prov4 = new Proveedor();
		prov4.setEmpresa(empresa8);
		prov4.setEstadoProveedor(tt.tipoProvEstado1);
		prov4.setTipoProveedor(tt.tipoProveedor1);
		prov4.setCuentaContable(ct5);
		grabarDB(prov4);

		// Chequeado ok
		Empresa empresa9 = new Empresa();
		empresa9.setCodigoEmpresa("9");
		empresa9.setNombre("Trans Multicargas S.A.");
		empresa9.setFechaIngreso(new Date());
		empresa9.setObservacion("sin obs");
		empresa9.setRazonSocial("TRANS MULTICARGAS S.A.");
		empresa9.getRubroEmpresas().add(tt.rubroEmpresaCasaRepuestos);
		empresa9.setRuc("12121212-8");
		empresa9.setMoneda(tt.guarani);
		empresa9.setMonedas(monedas1);
		empresa9.setPais(tt.paisPry);
		empresa9.setSigla("TMC");
		empresa9.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa9);

		// Chequeado ok
		Proveedor prov5 = new Proveedor();
		prov5.setEmpresa(empresa9);
		prov5.setEstadoProveedor(tt.tipoProvEstado1);
		prov5.setTipoProveedor(tt.tipoProveedor1);
		prov5.setCuentaContable(ct9);
		grabarDB(prov5);

		// Chequeado ok
		Empresa empresa10 = new Empresa();
		empresa10.setCodigoEmpresa("10");
		empresa10.setNombre("Navemar");
		empresa10.setFechaIngreso(new Date());
		empresa10.setObservacion("sin obs");
		empresa10.setRazonSocial("NAVEMAR S.A.");
		empresa10.setRuc("23232323-8");
		empresa10.setMoneda(tt.guarani);
		empresa10.setMonedas(monedas1);
		empresa10.setPais(tt.paisPry);
		empresa10.setSigla("NMR");
		empresa10.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa10);

		// Chequeado ok
		Proveedor prov6 = new Proveedor();
		prov6.setEmpresa(empresa10);
		prov6.setEstadoProveedor(tt.tipoProvEstado1);
		prov6.setTipoProveedor(tt.tipoProveedor1);
		prov6.setCuentaContable(ct5);
		grabarDB(prov6);

		// Chequeado ok
		Empresa empresa11 = new Empresa();
		empresa11.setCodigoEmpresa("11");
		empresa11.setNombre("Jose Ma. Godoy");
		empresa11.setFechaIngreso(new Date());
		empresa11.setObservacion("sin obs");
		empresa11.setRazonSocial("JOSE MA. GODOY S.A.");
		empresa11.getRubroEmpresas().add(tt.rubroEmpresaConsumidorFinal);
		empresa11.setRuc("34343434-5");
		empresa11.setMoneda(tt.guarani);
		empresa11.setMonedas(monedas1);
		empresa11.setPais(tt.paisPry);
		empresa11.setSigla("JMG");
		empresa11.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa11);

		// Chequeado ok
		Proveedor prov7 = new Proveedor();
		prov7.setEmpresa(empresa11);
		prov7.setEstadoProveedor(tt.tipoProvEstado1);
		prov7.setTipoProveedor(tt.tipoProveedor1);
		prov7.setCuentaContable(ct4);
		grabarDB(prov7);

		// Chequeado ok
		Empresa empresa12 = new Empresa();
		empresa12.setNombre("Partner");
		empresa12.setCodigoEmpresa("12");
		empresa12.setFechaIngreso(new Date());
		empresa12.setObservacion("sin obs");
		empresa12.setRazonSocial("PARTNER S.R.L");
		empresa12.getRubroEmpresas().add(tt.rubroEmpresaCasaRepuestos);
		empresa12.setRuc("45454545-5");
		empresa12.setMoneda(tt.guarani);
		empresa12.setMonedas(monedas1);
		empresa12.getSucursales().add(suc1);
		empresa12.setPais(tt.paisPry);
		empresa12.setSigla("PNR");
		empresa12.setTipoPersona(tt.personaJuridica);
		
		CtaCteEmpresa cce2 = new CtaCteEmpresa();
		cce2.setLineaCredito(l2);
		cce2.setEstadoComoCliente(tt.ctaCteEmpresaEstado1);
		cce2.setFechaAperturaCuentaCliente(new Date());
		cce2.setFechaAperturaCuentaProveedor(new Date());
		grabarDB(cce2);
		
		empresa12.setCtaCteEmpresa(cce2);
		
		grabarDB(empresa12);

		// Chequeado ok
		Proveedor prov8 = new Proveedor();
		prov8.setEmpresa(empresa12);
		prov8.setEstadoProveedor(tt.tipoProvEstado1);
		prov8.setTipoProveedor(tt.tipoProveedor1);
		prov8.setTimbrados(timbrados1);
		prov8.setCuentaContable(ct11);
		grabarDB(prov8);

		// Chequeado ok
		Empresa empresa3 = new Empresa();
		empresa3.setCodigoEmpresa("3");
		empresa3.setNombre("Chacomer");
		empresa3.setFechaIngreso(new Date());
		empresa3.setObservacion("sin obs");
		empresa3.setRazonSocial("Chacomer S.A.");
		empresa3.getRubroEmpresas().add(tt.rubroEmpresaDistribuidorCubiertas);
		empresa3.setRuc("56565656-9");
		empresa3.setMoneda(tt.guarani);
		empresa3.setMonedas(monedas3);
		empresa3.setPais(tt.paisPry);
		empresa3.setSigla("NDF");
		empresa3.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa3);

		// Chequeado ok
		Empresa empresa4 = new Empresa();
		empresa4.setCodigoEmpresa("4");
		empresa4.setNombre("Trincar");
		empresa4.setFechaIngreso(new Date());
		empresa4.setObservacion("sin obs");
		empresa4.setRazonSocial("Trinicar S.A.");
		empresa4.getRubroEmpresas().add(tt.rubroEmpresaCasaRepuestos);
		empresa4.setRuc("67676767-9");
		empresa4.setMoneda(tt.guarani);
		empresa4.setMonedas(monedas4);
		empresa4.getSucursales().add(suc4);
		empresa4.setPais(tt.paisPry);
		empresa4.setSigla("NDF");
		empresa4.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa4);

		Empresa empresa17 = new Empresa();
		empresa17.setCodigoEmpresa("");
		empresa17.setNombre("Consumidor Final");
		empresa17.setFechaIngreso(new Date());
		empresa17.setObservacion("");
		empresa17.setRazonSocial("Consumidor Final");
		empresa17.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empresa17.setPais(tt.paisPry);
		empresa17.setSigla("NDF");
		empresa17.setTipoPersona(tt.personaFisica);
		grabarDB(empresa17);

		Cliente cliente1 = new Cliente();
		cliente1.setEstadoCliente(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_ESTADO_CLIENTE, 1));
		cliente1.setCategoriaCliente(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_CATEGORIA_CLIENTE, 1));
		cliente1.setTipoCliente(tt.tipoCliente1);
		// cliente1.setEstadoCliente(rr.getEstadoClienteById(1));
		// cliente1.setCategoriaCliente(rr.getCategoriaClienteById(1));
		// cliente1.setTipoCliente(rr.getTipoClienteById(1));
		cliente1.setEmpresa(empresa1);
		cliente1.setCuentaContable(ct8);
		grabarDB(cliente1);

		Cliente cliente2 = new Cliente();
		cliente2.setEstadoCliente(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_ESTADO_CLIENTE, 2));
		cliente2.setCategoriaCliente(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_CATEGORIA_CLIENTE, 2));
		cliente2.setTipoCliente(tt.tipoCliente5);
		// cliente2.setEstadoCliente(rr.getEstadoClienteById(2));
		// cliente2.setCategoriaCliente(rr.getCategoriaClienteById(2));
		// cliente2.setTipoCliente(rr.getTipoClienteById(2));
		cliente2.setEmpresa(empresa4);
		cliente2.setCuentaContable(ct8);
		grabarDB(cliente2);

		Cliente cliente3 = new Cliente();
		cliente3.setCategoriaCliente(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_CATEGORIA_CLIENTE, 1));
		cliente3.setCuentaContable(ct8);
		cliente3.setEmpresa(empresa17);
		cliente3.setEstadoCliente(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_ESTADO_CLIENTE, 1));
		cliente3.setTipoCliente(tt.tipoCliente1);
		grabarDB(cliente3);

		// Chequeado ok
		Contacto cont1 = new Contacto();
		cont1.setNombre("Juan Perez");
		cont1.setCargo("Comprador");
		cont1.setCedula("3500400");
		cont1.setContactoSexo(tt.sexo1);
		cont1.setCorreo("juan@valvoline.com");
		cont1.setEstadoCivil(rr.getEstadoCivilById(1));
		cont1.setFechaCumpleanhos(new Date());
		cont1.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont1.setProfesion(rr.getProfesionById(1));
		cont1.setTelefono("510 500");

		// Chequeado ok
		Contacto cont2 = new Contacto();
		cont2.setNombre("Marcos Pereyra");
		cont2.setCargo("Contador");
		cont2.setCedula("3500600");
		cont2.setContactoSexo(tt.sexo1);
		cont2.setCorreo("marcos@batebol.com");
		cont2.setEstadoCivil(rr.getEstadoCivilById(1));
		cont2.setFechaCumpleanhos(new Date());
		cont2.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont2.setProfesion(rr.getProfesionById(1));
		cont2.setTelefono("510 600");

		// Chequeado ok
		Contacto cont3 = new Contacto();
		cont3.setNombre("Pedro Ojeda");
		cont3.setCargo("Comprador");
		cont3.setCedula("3500800");
		cont3.setContactoSexo(tt.sexo1);
		cont3.setCorreo("pedro@mail.com");
		cont3.setEstadoCivil(rr.getEstadoCivilById(1));
		cont3.setFechaCumpleanhos(new Date());
		cont3.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont3.setProfesion(rr.getProfesionById(1));
		cont3.setTelefono("510 900");

		// Chequeado ok
		Contacto cont4 = new Contacto();
		cont4.setNombre("Carlos Altamirano");
		cont4.setCargo("Contador");
		cont4.setCedula("3100600");
		cont4.setContactoSexo(tt.sexo1);
		cont4.setCorreo("carlos@mail.com");
		cont4.setEstadoCivil(rr.getEstadoCivilById(1));
		cont4.setFechaCumpleanhos(new Date());
		cont4.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont4.setProfesion(rr.getProfesionById(1));
		cont4.setTelefono("511 600");

		// Chequeado ok
		Contacto cont5 = new Contacto();
		cont5.setNombre("Carlos Aquino");
		cont5.setCargo("Contador");
		cont5.setCedula("3100600");
		cont5.setContactoSexo(tt.sexo1);
		cont5.setCorreo("carlos@sabo.com");
		cont5.setEstadoCivil(rr.getEstadoCivilById(1));
		cont5.setFechaCumpleanhos(new Date());
		cont5.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont5.setProfesion(rr.getProfesionById(1));
		cont5.setTelefono("511 600");

		// Chequeado ok
		Contacto cont6 = new Contacto();
		cont6.setNombre("Carlos Frutos");
		cont6.setCargo("Contador");
		cont6.setCedula("3100600");
		cont6.setContactoSexo(tt.sexo1);
		cont6.setCorreo("carlos@giti.com");
		cont6.setEstadoCivil(rr.getEstadoCivilById(1));
		cont6.setFechaCumpleanhos(new Date());
		cont6.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont6.setProfesion(rr.getProfesionById(1));
		cont6.setTelefono("511 600");

		// Chequeado ok
		Contacto cont7 = new Contacto();
		cont7.setNombre("Carlos Lopez");
		cont7.setCargo("Contador");
		cont7.setCedula("3100600");
		cont7.setContactoSexo(tt.sexo1);
		cont7.setCorreo("carlos@mail.com");
		cont7.setEstadoCivil(rr.getEstadoCivilById(1));
		cont7.setFechaCumpleanhos(new Date());
		cont7.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont7.setProfesion(rr.getProfesionById(1));
		cont7.setTelefono("511 600");

		Contacto cont8 = new Contacto();
		cont8.setNombre("Pedro Lopez");
		cont8.setCargo("Comprador");
		cont8.setCedula("3500400");
		cont8.setContactoSexo(tt.sexo1);
		cont8.setCorreo("pedro@valvoline.com");
		cont8.setEstadoCivil(rr.getEstadoCivilById(1));
		cont8.setFechaCumpleanhos(new Date());
		cont8.setProfesion(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_PROFESIONES, 1));
		// cont8.setProfesion(rr.getProfesionById(1));
		cont8.setTelefono("510 500");

		empresa1.getContactos().add(cont1);
		empresa1.getContactos().add(cont2);
		empresa1.getContactos().add(cont3);
		empresa1.getContactos().add(cont4);
		empresa1.getContactos().add(cont5);
		empresa1.getContactos().add(cont6);
		empresa1.getContactos().add(cont7);

		cont1.setSucursal(suc1);
		cont2.setSucursal(suc2);
		cont3.setSucursal(suc3);
		cont4.setSucursal(suc3);
		cont5.setSucursal(suc3);
		cont6.setSucursal(suc3);
		cont7.setSucursal(suc3);

		grabarDB(empresa1);

		empresa4.getContactos().add(cont8);
		cont8.setSucursal(suc4);
		grabarDB(empresa4);

		// Chequeado ok
		ContactoInterno contInterno1 = new ContactoInterno();
		contInterno1.setFuncionario(funcionario5);
		contInterno1.setTipoContactoInterno(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_TIPO_CONTACTO_INTERNO, 1));
		// contInterno1.setTipoContactoInterno(tipoContInt1);
		// grabarDB(contInterno1);

		// Chequeado ok
		ContactoInterno contInterno2 = new ContactoInterno();
		contInterno2.setFuncionario(funcionario5);
		contInterno1.setTipoContactoInterno(rr.getTipoPorSigla_Index(
				Configuracion.SIGLA_TIPO_CONTACTO_INTERNO, 2));
		// contInterno2.setTipoContactoInterno(tipoContInt2);
		// grabarDB(contInterno2);
		cliente1.getContactosInternos().add(contInterno1);
		cliente1.getContactosInternos().add(contInterno2);
		grabarDB(cliente1);
		
		
		Empresa empresa18 = new Empresa();
		empresa18.setCodigoEmpresa("6069");
		empresa18.setNombre("CINPAL CIA INDUSTRAL DE PECAS P/ AUTOMOVILES");
		empresa18.setFechaIngreso(new Date());
		empresa18.setObservacion("");
		empresa18.setRazonSocial("CINPAL CIA INDUSTRAL DE PECAS P/ AUTOMOVILES");
		empresa18.setRuc(Configuracion.RUC_EMPRESA_EXTERIOR);
		empresa18.setPais(tt.paisBra);
		empresa18.setSigla("NDF");
		empresa18.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa18);

		Proveedor prov9 = new Proveedor();
		prov9.setEmpresa(empresa18);
		prov9.setEstadoProveedor(tt.tipoProvEstado1);
		prov9.setTipoProveedor(tt.tipoProveedor2);
		grabarDB(prov9);
		
		
		Empresa empresa19 = new Empresa();
		empresa19.setCodigoEmpresa("53554");
		empresa19.setNombre("TRACTOPAR S.A.E");
		empresa19.setFechaIngreso(new Date());
		empresa19.setObservacion("");
		empresa19.setRazonSocial("TRACTOPAR S.A.E");
		empresa19.setRuc("80013570-9");
		empresa19.setPais(tt.paisPry);
		empresa19.setSigla("NDF");
		empresa19.setTipoPersona(tt.personaJuridica);
		grabarDB(empresa19);

		Proveedor prov10 = new Proveedor();
		prov10.setEmpresa(empresa19);
		prov10.setEstadoProveedor(tt.tipoProvEstado1);
		prov10.setTipoProveedor(tt.tipoProveedor2);
		grabarDB(prov10);
		
		/****************************************************************************************************************/

		/************************************************ B.D. ARTICULO *************************************************/

		// Faltan los precios de los articulos

		// Chequeado ok
		ArticuloPresentacion artPresentacion1 = new ArticuloPresentacion();
		artPresentacion1.setDescripcion("20 x 8");
		// artPresentacion1.setObservacion("20 Unidades x Caja, 8 Cajas x Pallets");
		artPresentacion1.setPeso(10);
		artPresentacion1.setUnidad(10);
		artPresentacion1.setUnidadMedida(tt.sinReferenciaTipo);
		// artPresentacion1.setUnidadMedida(artUnidad1);
		grabarDB(artPresentacion1);

		// Chequeado ok
		ArticuloPresentacion artPresentacion2 = new ArticuloPresentacion();
		artPresentacion2.setDescripcion("15 x 8");
		// artPresentacion2.setObservacion("15 Unidades x Caja, 8 Cajas x Pallets");
		artPresentacion2.setPeso(20);
		artPresentacion2.setUnidad(20);
		artPresentacion2.setUnidadMedida(tt.sinReferenciaTipo);
		// artPresentacion2.setUnidadMedida(artUnidad2);
		grabarDB(artPresentacion2);

		Set<ArticuloMarcaAplicacion> artMarcAplicaciones1 = new HashSet<ArticuloMarcaAplicacion>();

		// Chequeado ok
		ArticuloMarcaAplicacion artMarcaAplicacion1 = new ArticuloMarcaAplicacion();
		artMarcaAplicacion1.setDescripcion("Mercedez Benz Popu");
		artMarcaAplicacion1.setSigla("MB-Popu");
		grabarDB(artMarcaAplicacion1);
		artMarcAplicaciones1.add(artMarcaAplicacion1);

		// Chequeado ok
		ArticuloMarcaAplicacion artMarcaAplicacion2 = new ArticuloMarcaAplicacion();
		artMarcaAplicacion2.setDescripcion("Scania-Popu");
		artMarcaAplicacion2.setSigla("SC-Popu");
		grabarDB(artMarcaAplicacion2);
		artMarcAplicaciones1.add(artMarcaAplicacion2);

		Set<ArticuloModeloAplicacion> artModAplicaciones1 = new HashSet<ArticuloModeloAplicacion>();
		Set<ArticuloModeloAplicacion> artModAplicaciones2 = new HashSet<ArticuloModeloAplicacion>();
		Set<ArticuloModeloAplicacion> artModAplicaciones3 = new HashSet<ArticuloModeloAplicacion>();
		Set<ArticuloModeloAplicacion> artModAplicaciones4 = new HashSet<ArticuloModeloAplicacion>();

		// Chequeado ok
		ArticuloModeloAplicacion artModeloAplicacion1 = new ArticuloModeloAplicacion();
		artModeloAplicacion1.setDescripcion("1113-Popu");
		artModeloAplicacion1.setArticuloMarcaAplicacion(artMarcaAplicacion1);
		grabarDB(artModeloAplicacion1);
		artModAplicaciones1.add(artModeloAplicacion1);

		// Chequeado ok
		ArticuloModeloAplicacion artModeloAplicacion2 = new ArticuloModeloAplicacion();
		artModeloAplicacion2.setDescripcion("124-Popu");
		artModeloAplicacion2.setArticuloMarcaAplicacion(artMarcaAplicacion2);
		grabarDB(artModeloAplicacion2);
		artModAplicaciones1.add(artModeloAplicacion2);

		artModAplicaciones2.add(rr.getArticuloModeloAplicacionById(1));
		artModAplicaciones2.add(rr.getArticuloModeloAplicacionById(2));

		artModAplicaciones3.add(rr.getArticuloModeloAplicacionById(1));
		artModAplicaciones3.add(rr.getArticuloModeloAplicacionById(2));

		artModAplicaciones4.add(rr.getArticuloModeloAplicacionById(1));
		artModAplicaciones4.add(rr.getArticuloModeloAplicacionById(2));

		// Chequeado ok
		Articulo art1_Valvoline = new Articulo();
		art1_Valvoline.setDescripcion("KIT MOTOR SC 124 360 DSC12 NO ARTICULADO");
		art1_Valvoline.setCodigoInterno("KS 99.374.962");
		art1_Valvoline.setCodigoOriginal("KS 99.374.962");
		art1_Valvoline.setCodigoBarra("KS 99.374.962");
		art1_Valvoline.setObservacion("Sin Observaciones");
		art1_Valvoline.setPeso(1.5);
		art1_Valvoline.setVolumen(11.5);
		art1_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art1_Valvoline.setArticuloUnidadMedida(artUnidad3);
	    // art1_Valvoline.setArticuloModeloAplicaciones(artModAplicaciones1);

		art1_Valvoline.setArticuloMarca(tt.sinReferenciaTipo);
		art1_Valvoline.setArticuloParte(tt.sinReferenciaTipo);
		art1_Valvoline.setArticuloLinea(tt.sinReferenciaTipo);
		art1_Valvoline.setArticuloFamilia(tt.sinReferenciaTipo);
		art1_Valvoline.setArticuloEstado(tt.estadoArticulo1);

		// art1_Valvoline.setArticuloMarca(artMarca1);
		// art1_Valvoline.setArticuloParte(artParte1);
		// art1_Valvoline.setArticuloLinea(artLinea1);
		// art1_Valvoline.setArticuloFamilia(artFamilia1);
		// art1_Valvoline.setArticuloEstado(artEstado1);
		art1_Valvoline.setArticuloPresentacion(artPresentacion1);

		Set<ProveedorArticulo> prart = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa = new ProveedorArticulo();
		pa.setProveedor(prov2);
		pa.setCodigoFabrica("KS 99.374.962");
		pa.setDescripcionArticuloProveedor("KIT MOTOR SC 124 360 DSC12 NO ARTICULADO");

		prart.add(pa);
		art1_Valvoline.setProveedorArticulos(prart);

		// System.out.println("artUnidad3.getId():" + artUnidad3.getId());
		grabarDB(art1_Valvoline);

		// Chequeado ok
		Articulo art2_Valvoline = new Articulo();
		art2_Valvoline.setDescripcion("ARO MOTOR 352 STD SEMI CROMADO");
		art2_Valvoline.setCodigoInterno("KS 50.011.919");
		art2_Valvoline.setCodigoOriginal("KS 50.011.919");
		art2_Valvoline.setCodigoBarra("KS 50.011.919");
		art2_Valvoline.setObservacion("Sin Observaciones");
		art2_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art2_Valvoline.setArticuloUnidadMedida(artUnidad3);
		// art2_Valvoline.setArticuloModeloAplicaciones(artModAplicaciones1);

		art2_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art4_reparo.setArticuloUnidadMedida(artUnidad3);
		// art2_Valvoline.setArticuloModeloAplicaciones(artModAplicaciones1);
		art2_Valvoline.setArticuloMarca(tt.sinReferenciaTipo);
		art2_Valvoline.setArticuloParte(tt.sinReferenciaTipo);
		art2_Valvoline.setArticuloLinea(tt.sinReferenciaTipo);
		art2_Valvoline.setArticuloFamilia(tt.sinReferenciaTipo);
		art2_Valvoline.setArticuloEstado(tt.estadoArticulo1);
		art2_Valvoline.setArticuloPresentacion(artPresentacion1);
		art2_Valvoline.setPeso(1.2);
		art2_Valvoline.setVolumen(3.5);

		Set<ProveedorArticulo> prart2 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa2 = new ProveedorArticulo();
		// pa2.setArticulo(art2_Valvoline);
		pa2.setProveedor(prov2);
		pa2.setCodigoFabrica("KS 50.011.919");
		pa2.setDescripcionArticuloProveedor("ARO MOTOR 352 STD SEMI CROMADO");
		// ver grabarDB(pa2);
		prart2.add(pa2);
		art2_Valvoline.setProveedorArticulos(prart2);

		grabarDB(art2_Valvoline);

		// Chequeado ok
		Articulo art3_Valvoline = new Articulo();
		art3_Valvoline.setDescripcion("CAMISA MOTOR STD 352/366");
		art3_Valvoline.setCodigoInterno("KS 89.177.190");
		art3_Valvoline.setCodigoOriginal("KS 89.177.190");
		art3_Valvoline.setCodigoBarra("KS 89.177.190");
		art3_Valvoline.setObservacion("Sin Observaciones");
		art3_Valvoline.setPeso(1.1);
		art3_Valvoline.setVolumen(4.5);
		art3_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art3_Valvoline.setArticuloUnidadMedida(artUnidad3);
		art3_Valvoline.setArticuloMarca(tt.sinReferenciaTipo);
		art3_Valvoline.setArticuloParte(tt.sinReferenciaTipo);
		art3_Valvoline.setArticuloLinea(tt.sinReferenciaTipo);
		art3_Valvoline.setArticuloFamilia(tt.sinReferenciaTipo);
		art3_Valvoline.setArticuloEstado(tt.estadoArticulo1);

		// art3_Valvoline.setArticuloMarca(rr.getArticuloMarcaById(1));
		// art3_Valvoline.setArticuloParte(rr.getArticuloParteById(1));
		// art3_Valvoline.setArticuloLinea(rr.getArticuloLineaById(1));
		// art3_Valvoline.setArticuloFamilia(rr.getArticuloFamiliaById(2));
		// art3_Valvoline.setArticuloEstado(rr.getArticuloEstadoById(1));
		art3_Valvoline.setArticuloPresentacion(rr
				.getArticuloPresentacionById(1));

		Set<ProveedorArticulo> prart3 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa3 = new ProveedorArticulo();
		// pa3.setArticulo(art3_Valvoline);
		pa3.setProveedor(prov2);
		pa3.setCodigoFabrica("KS 89.177.190");
		pa3.setDescripcionArticuloProveedor("CAMISA MOTOR STD 352/366");
		// ver grabarDB(pa3);
		prart3.add(pa3);
		art3_Valvoline.setProveedorArticulos(prart3);

		grabarDB(art3_Valvoline);

		// Chequeado ok
		Articulo art4_Valvoline = new Articulo();
		art4_Valvoline.setDescripcion("CAMISA MOTOR STD SC P94");
		art4_Valvoline.setCodigoInterno("KS 89.844.810");
		art4_Valvoline.setCodigoOriginal("KS 89.844.810");
		art4_Valvoline.setCodigoBarra("KS 89.844.810");
		art4_Valvoline.setObservacion("Sin Observaciones");
		art4_Valvoline.setPeso(1.4);
		art4_Valvoline.setVolumen(7.7);
		art4_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art4_Valvoline.setArticuloUnidadMedida(artUnidad3);
		art4_Valvoline.setArticuloMarca(tt.sinReferenciaTipo);
		art4_Valvoline.setArticuloParte(tt.sinReferenciaTipo);
		art4_Valvoline.setArticuloLinea(tt.sinReferenciaTipo);
		art4_Valvoline.setArticuloFamilia(tt.sinReferenciaTipo);
		art4_Valvoline.setArticuloEstado(tt.estadoArticulo1);

		// art4_Valvoline.setArticuloEstado(rr.getArticuloEstadoById(1));
		// art4_Valvoline.setArticuloFamilia(rr.getArticuloFamiliaById(1));
		// art4_Valvoline.setArticuloLinea(rr.getArticuloLineaById(1));
		// art4_Valvoline.setArticuloMarca(rr.getArticuloMarcaById(1));
		// art4_Valvoline.setArticuloParte(rr.getArticuloParteById(1));
		art4_Valvoline.setArticuloPresentacion(rr
				.getArticuloPresentacionById(1));

		Set<ProveedorArticulo> prart4 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa4 = new ProveedorArticulo();
		// pa4.setArticulo(art4_Valvoline);
		pa4.setProveedor(prov2);
		pa4.setCodigoFabrica("KS 89.844.810");
		pa4.setDescripcionArticuloProveedor("CAMISA MOTOR STD SC P94");
		// ver grabarDB(pa4);
		prart4.add(pa4);
		art4_Valvoline.setProveedorArticulos(prart4);

		grabarDB(art4_Valvoline);

		// Chequeado ok
		Articulo art5_Valvoline = new Articulo();
		art5_Valvoline.setDescripcion("KIT MOTOR VW 1600 FUSCA/KOMBI");
		art5_Valvoline.setCodigoInterno("KS 91.511.960");
		art5_Valvoline.setCodigoOriginal("KS 91.511.960");
		art5_Valvoline.setCodigoBarra("KS 91.511.960");
		art5_Valvoline.setObservacion("Sin Observaciones");
		
		art5_Valvoline.setPeso(10.5);
		art5_Valvoline.setVolumen(5.6);
		art5_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art5_Valvoline.setArticuloUnidadMedida(artUnidad3);
		art5_Valvoline.setArticuloMarca(tt.sinReferenciaTipo);
		art5_Valvoline.setArticuloParte(tt.sinReferenciaTipo);
		art5_Valvoline.setArticuloLinea(tt.sinReferenciaTipo);
		art5_Valvoline.setArticuloFamilia(tt.sinReferenciaTipo);
		art5_Valvoline.setArticuloEstado(tt.estadoArticulo1);

		// art5_Valvoline.setArticuloEstado(rr.getArticuloEstadoById(1));
		// art5_Valvoline.setArticuloFamilia(rr.getArticuloFamiliaById(1));
		// art5_Valvoline.setArticuloLinea(rr.getArticuloLineaById(1));
		// art5_Valvoline.setArticuloMarca(rr.getArticuloMarcaById(1));
		// art5_Valvoline.setArticuloParte(rr.getArticuloParteById(1));
		art5_Valvoline.setArticuloPresentacion(rr
				.getArticuloPresentacionById(1));

		Set<ProveedorArticulo> prart5 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa5 = new ProveedorArticulo();
		// pa5.setArticulo(art5_Valvoline);
		pa5.setProveedor(prov2);
		pa5.setCodigoFabrica("KS 91.511.960");
		pa5.setDescripcionArticuloProveedor("KIT MOTOR VW 1600 FUSCA/KOMBI");
		// ver grabarDB(pa5);
		prart5.add(pa5);
		art5_Valvoline.setProveedorArticulos(prart5);

		grabarDB(art5_Valvoline);

		// Chequeado ok
		Articulo art6_Valvoline = new Articulo();
		art6_Valvoline.setDescripcion("PISTON MOTOR C/ARO 352 TURBO STD 5R");
		art6_Valvoline.setCodigoInterno("KS 90.276.604");
		art6_Valvoline.setCodigoOriginal("KS 90.276.604");
		art6_Valvoline.setCodigoBarra("KS 90.276.604");
		art6_Valvoline.setObservacion("Sin Observaciones");
		art6_Valvoline.setPeso(3.5);
		art6_Valvoline.setVolumen(12.3);
		art6_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art6_Valvoline.setArticuloUnidadMedida(artUnidad3);
		art6_Valvoline.setArticuloMarca(tt.sinReferenciaTipo);
		art6_Valvoline.setArticuloParte(tt.sinReferenciaTipo);
		art6_Valvoline.setArticuloLinea(tt.sinReferenciaTipo);
		art6_Valvoline.setArticuloFamilia(tt.sinReferenciaTipo);
		art6_Valvoline.setArticuloEstado(tt.estadoArticulo1);

		// art6_Valvoline.setArticuloEstado(rr.getArticuloEstadoById(1));
		// art6_Valvoline.setArticuloFamilia(rr.getArticuloFamiliaById(1));
		// art6_Valvoline.setArticuloLinea(rr.getArticuloLineaById(1));
		// art6_Valvoline.setArticuloMarca(rr.getArticuloMarcaById(1));
		// art6_Valvoline.setArticuloParte(rr.getArticuloParteById(1));
		art6_Valvoline.setArticuloPresentacion(rr
				.getArticuloPresentacionById(1));
		

		Set<ProveedorArticulo> prart6 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa6 = new ProveedorArticulo();
		// pa6.setArticulo(art6_Valvoline);
		pa6.setProveedor(prov2);
		pa6.setCodigoFabrica("KS 90.276.604");
		pa6.setDescripcionArticuloProveedor("PISTON MOTOR C/ARO 352 TURBO STD 5R");
		// ver grabarDB(pa6);
		prart6.add(pa6);
		art6_Valvoline.setProveedorArticulos(prart6);

		grabarDB(art6_Valvoline);

		// Chequeado ok
		Articulo art7_Valvoline = new Articulo();
		art7_Valvoline.setDescripcion("PISTON MOTOR STD ECOL.M/N SIN PESTAÑA");
		art7_Valvoline.setCodigoInterno("KS 92.525.800");
		art7_Valvoline.setCodigoOriginal("KS 92.525.800");
		art7_Valvoline.setCodigoBarra("KS 92.525.800");
		art7_Valvoline.setObservacion("Sin Observaciones");
		art7_Valvoline.setPeso(11.5);
		art7_Valvoline.setVolumen(7.0);
		art7_Valvoline.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art7_Valvoline.setArticuloUnidadMedida(artUnidad3);
		art7_Valvoline.setArticuloMarca(tt.sinReferenciaTipo);
		art7_Valvoline.setArticuloParte(tt.sinReferenciaTipo);
		art7_Valvoline.setArticuloLinea(tt.sinReferenciaTipo);
		art7_Valvoline.setArticuloFamilia(tt.sinReferenciaTipo);
		art7_Valvoline.setArticuloEstado(tt.estadoArticulo1);

		// art7_Valvoline.setArticuloEstado(rr.getArticuloEstadoById(1));
		// art7_Valvoline.setArticuloFamilia(rr.getArticuloFamiliaById(1));
		// art7_Valvoline.setArticuloLinea(rr.getArticuloLineaById(1));
		// art7_Valvoline.setArticuloMarca(rr.getArticuloMarcaById(1));
		// art7_Valvoline.setArticuloParte(rr.getArticuloParteById(1));
		art7_Valvoline.setArticuloPresentacion(rr
				.getArticuloPresentacionById(1));
	

		Set<ProveedorArticulo> prart7 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa7 = new ProveedorArticulo();
		// pa7.setArticulo(art7_Valvoline);
		pa7.setProveedor(prov2);
		pa7.setCodigoFabrica("KS 92.525.800");
		pa7.setDescripcionArticuloProveedor("PISTON MOTOR STD ECOL.M/N SIN PESTAÑA");
		// ver grabarDB(pa7);
		prart7.add(pa7);
		art7_Valvoline.setProveedorArticulos(prart7);

		grabarDB(art7_Valvoline);

		// Chequeado ok
		Articulo articulo8 = new Articulo();
		articulo8.setDescripcion("KIT MOTOR SC124 SERIE 400 ARTICULADO KS");
		articulo8.setCodigoInterno("KS 40.368.960");
		articulo8.setCodigoOriginal("KS 40.368.960");
		articulo8.setCodigoBarra("KS 40.368.960");
		articulo8.setObservacion("Sin Observaciones");
		articulo8.setPeso(2.1);
		articulo8.setVolumen(9.2);
		articulo8.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// articulo8.setArticuloUnidadMedida(artUnidad1);
		articulo8.setArticuloMarca(tt.sinReferenciaTipo);
		articulo8.setArticuloParte(tt.sinReferenciaTipo);
		articulo8.setArticuloLinea(tt.sinReferenciaTipo);
		articulo8.setArticuloFamilia(tt.sinReferenciaTipo);
		articulo8.setArticuloEstado(tt.estadoArticulo1);

		// articulo8.setArticuloEstado(rr.getArticuloEstadoById(1));
		// articulo8.setArticuloFamilia(rr.getArticuloFamiliaById(1));
		// articulo8.setArticuloLinea(rr.getArticuloLineaById(1));
		// articulo8.setArticuloMarca(rr.getArticuloMarcaById(1));
		// articulo8.setArticuloParte(rr.getArticuloParteById(1));
		articulo8.setArticuloPresentacion(rr.getArticuloPresentacionById(1));
		

		Set<ProveedorArticulo> prart8 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa8 = new ProveedorArticulo();
		pa8.setProveedor(prov2);
		pa8.setCodigoFabrica("KS 40.368.960");
		pa8.setDescripcionArticuloProveedor("KIT MOTOR SC124 SERIE 400 ARTICULADO KS");

		prart8.add(pa8);
		articulo8.setProveedorArticulos(prart8);

		grabarDB(articulo8);

		Articulo art_ref1 = new Articulo();
		art_ref1.setDescripcion("@GASTOS");
		art_ref1.setCodigoInterno("@GASTOS");
		art_ref1.setCodigoOriginal("@GASTOS");
		art_ref1.setCodigoBarra("@GASTOS");
		art_ref1.setObservacion("");
		art_ref1.setArticuloMarca(tt.sinReferenciaTipo);
		art_ref1.setArticuloParte(tt.sinReferenciaTipo);
		art_ref1.setArticuloLinea(tt.sinReferenciaTipo);
		art_ref1.setArticuloFamilia(tt.sinReferenciaTipo);
		art_ref1.setArticuloEstado(tt.estadoArticulo1);

		// articulo9.setArticuloEstado(rr.getArticuloEstadoById(1));
		// articulo9.setArticuloFamilia(rr.getArticuloFamiliaById(1));
		// articulo9.setArticuloLinea(rr.getArticuloLineaById(1));
		// articulo9.setArticuloMarca(rr.getArticuloMarcaById(1));
		// articulo9.setArticuloParte(rr.getArticuloParteById(1));
		art_ref1.setArticuloPresentacion(rr.getArticuloPresentacionById(1));
		

		Set<ProveedorArticulo> prart9 = new HashSet<ProveedorArticulo>();
		art_ref1.setProveedorArticulos(prart9);

		grabarDB(art_ref1);

		Articulo art_ref2 = new Articulo();
		art_ref2.setDescripcion("@DESCUENTO");
		art_ref2.setCodigoInterno("@DESC");
		art_ref2.setCodigoOriginal("@DESC");
		art_ref2.setCodigoBarra("@DESC");
		art_ref2.setObservacion("");
		art_ref2.setArticuloMarca(tt.sinReferenciaTipo);
		art_ref2.setArticuloParte(tt.sinReferenciaTipo);
		art_ref2.setArticuloLinea(tt.sinReferenciaTipo);
		art_ref2.setArticuloFamilia(tt.sinReferenciaTipo);
		art_ref2.setArticuloEstado(tt.estadoArticulo1);

		Set<ProveedorArticulo> prart10 = new HashSet<ProveedorArticulo>();
		art_ref2.setProveedorArticulos(prart10);

		grabarDB(art_ref2);

		Articulo art_ref3 = new Articulo();
		art_ref3.setDescripcion("@PRORRATEO");
		art_ref3.setCodigoInterno("@PRORRATEO");
		art_ref3.setCodigoOriginal("@PRORRATEO");
		art_ref3.setCodigoBarra("@PRORRATEO");
		art_ref3.setObservacion("");
		art_ref3.setArticuloMarca(tt.sinReferenciaTipo);
		art_ref3.setArticuloParte(tt.sinReferenciaTipo);
		art_ref3.setArticuloLinea(tt.sinReferenciaTipo);
		art_ref3.setArticuloFamilia(tt.sinReferenciaTipo);
		art_ref3.setArticuloEstado(tt.estadoArticulo1);

		Set<ProveedorArticulo> prart15 = new HashSet<ProveedorArticulo>();
		art_ref3.setProveedorArticulos(prart15);

		grabarDB(art_ref3);

		Articulo art1_reparo = new Articulo();
		art1_reparo.setDescripcion("KIT MOTOR SC P93 SUECO ARO CONICO 115mm.");
		art1_reparo.setCodigoInterno("KS 90.738.971");
		art1_reparo.setCodigoOriginal("KS 90.738.971");
		art1_reparo.setCodigoBarra("KS 90.738.971");
		art1_reparo.setObservacion("Sin Observaciones");
		
		art1_reparo.setPeso(1.5);
		art1_reparo.setVolumen(11.5);
		art1_reparo.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art1_reparo.setArticuloUnidadMedida(artUnidad3);
		// art1_reparo.setArticuloModeloAplicaciones(artModAplicaciones1);
		art1_reparo.setArticuloMarca(tt.sinReferenciaTipo);
		art1_reparo.setArticuloParte(tt.sinReferenciaTipo);
		art1_reparo.setArticuloLinea(tt.sinReferenciaTipo);
		art1_reparo.setArticuloFamilia(tt.sinReferenciaTipo);
		art1_reparo.setArticuloEstado(tt.estadoArticulo1);
		// art1_reparo.setArticuloMarca(artMarca1);
		// art1_reparo.setArticuloParte(artParte1);
		// art1_reparo.setArticuloLinea(artLinea1);
		// art1_reparo.setArticuloFamilia(artFamilia1);
		// art1_reparo.setArticuloEstado(artEstado1);
		art1_reparo.setArticuloPresentacion(artPresentacion1);

		Set<ProveedorArticulo> prart11 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa11 = new ProveedorArticulo();
		pa11.setProveedor(prov2);
		pa11.setCodigoFabrica("KS 90.738.971");
		pa11.setDescripcionArticuloProveedor("KIT MOTOR SC P93 SUECO ARO CONICO 115mm.");

		prart11.add(pa11);
		art1_reparo.setProveedorArticulos(prart11);
		grabarDB(art1_reparo);

		Articulo art2_reparo = new Articulo();
		art2_reparo.setDescripcion("ARO MOTOR SC 3 RANURAS CONICO");
		art2_reparo.setCodigoInterno("KS 800.024.813.000");
		art2_reparo.setCodigoOriginal("KS 800.024.813.000");
		art2_reparo.setCodigoBarra("KS 800.024.813.000");
		art2_reparo.setObservacion("Sin Observaciones");
		
		art2_reparo.setPeso(1.5);
		art2_reparo.setVolumen(11.5);
		art2_reparo.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art2_reparo.setArticuloUnidadMedida(artUnidad3);
		// art2_reparo.setArticuloModeloAplicaciones(artModAplicaciones1);
		art2_reparo.setArticuloMarca(tt.sinReferenciaTipo);
		art2_reparo.setArticuloParte(tt.sinReferenciaTipo);
		art2_reparo.setArticuloLinea(tt.sinReferenciaTipo);
		art2_reparo.setArticuloFamilia(tt.sinReferenciaTipo);
		art2_reparo.setArticuloEstado(tt.estadoArticulo1);
		// art2_reparo.setArticuloMarca(artMarca1);
		// art2_reparo.setArticuloParte(artParte1);
		// art2_reparo.setArticuloLinea(artLinea1);
		// art2_reparo.setArticuloFamilia(artFamilia1);
		// art2_reparo.setArticuloEstado(artEstado1);
		art2_reparo.setArticuloPresentacion(artPresentacion1);

		Set<ProveedorArticulo> prart12 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa12 = new ProveedorArticulo();
		pa12.setProveedor(prov2);
		pa12.setCodigoFabrica("KS 800.024.813.000");
		pa12.setDescripcionArticuloProveedor("ARO MOTOR SC 3 RANURAS CONICO");

		prart12.add(pa12);
		art2_reparo.setProveedorArticulos(prart12);
		grabarDB(art2_reparo);

		Articulo art3_reparo = new Articulo();
		art3_reparo.setDescripcion("REPARO BOMBA EMBR.AUX PICUDO");
		art3_reparo.setCodigoInterno("VAR RCCE 0015.3");
		art3_reparo.setCodigoOriginal("100200300");
		art3_reparo.setCodigoBarra("100200300");
		art3_reparo.setObservacion("Sin Observaciones");
		
		art3_reparo.setPeso(1.5);
		art3_reparo.setVolumen(11.5);
		art3_reparo.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art3_reparo.setArticuloUnidadMedida(artUnidad3);
		// art3_reparo.setArticuloModeloAplicaciones(artModAplicaciones1);
		art3_reparo.setArticuloMarca(tt.sinReferenciaTipo);
		art3_reparo.setArticuloParte(tt.sinReferenciaTipo);
		art3_reparo.setArticuloLinea(tt.sinReferenciaTipo);
		art3_reparo.setArticuloFamilia(tt.sinReferenciaTipo);
		art3_reparo.setArticuloEstado(tt.estadoArticulo1);
		// art3_reparo.setArticuloMarca(artMarca1);
		// art3_reparo.setArticuloParte(artParte1);
		// art3_reparo.setArticuloLinea(artLinea1);
		// art3_reparo.setArticuloFamilia(artFamilia1);
		// art3_reparo.setArticuloEstado(artEstado1);
		art3_reparo.setArticuloPresentacion(artPresentacion1);

		Set<ProveedorArticulo> prart13 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa13 = new ProveedorArticulo();
		pa13.setProveedor(prov3);
		pa13.setCodigoFabrica("100200300");
		pa13.setDescripcionArticuloProveedor("REPARO BOMBA EMBR.AUX PICUDO");

		prart13.add(pa13);
		art3_reparo.setProveedorArticulos(prart13);
		grabarDB(art3_reparo);

		Articulo art4_reparo = new Articulo();
		art4_reparo.setDescripcion("REPARO BOMBA EMBR.M/N PICUDO GRUESO");
		art4_reparo.setCodigoInterno("VAR RCCE 0032.3");
		art4_reparo.setCodigoOriginal("100200300");
		art4_reparo.setCodigoBarra("100200300");
		art4_reparo.setObservacion("Sin Observaciones");
		
		art4_reparo.setPeso(1.5);
		art4_reparo.setVolumen(11.5);
		art4_reparo.setArticuloUnidadMedida(tt.sinReferenciaTipo);
		// art4_reparo.setArticuloUnidadMedida(artUnidad3);
		// art4_reparo.setArticuloModeloAplicaciones(artModAplicaciones1);
		art4_reparo.setArticuloMarca(tt.sinReferenciaTipo);
		art4_reparo.setArticuloParte(tt.sinReferenciaTipo);
		art4_reparo.setArticuloLinea(tt.sinReferenciaTipo);
		art4_reparo.setArticuloFamilia(tt.sinReferenciaTipo);
		art4_reparo.setArticuloEstado(tt.estadoArticulo1);
		// art4_reparo.setArticuloMarca(artMarca1);
		// art4_reparo.setArticuloParte(artParte1);
		// art4_reparo.setArticuloLinea(artLinea1);
		// art4_reparo.setArticuloFamilia(artFamilia1);
		// art4_reparo.setArticuloEstado(artEstado1);
		art4_reparo.setArticuloPresentacion(artPresentacion1);

		Set<ProveedorArticulo> prart14 = new HashSet<ProveedorArticulo>();
		ProveedorArticulo pa14 = new ProveedorArticulo();
		pa14.setProveedor(prov3);
		pa14.setCodigoFabrica("100200300");
		pa14.setDescripcionArticuloProveedor("REPARO BOMBA EMBR.M/N PICUDO GRUESO");

		prart14.add(pa14);
		art4_reparo.setProveedorArticulos(prart14);
		grabarDB(art4_reparo);

		for (int i = 20; i < 200; i++) {

			Articulo ar = new Articulo();
			ar.setDescripcion("articulo " + i);
			ar.setCodigoInterno("int-" + i);
			ar.setCodigoOriginal("ori" + i);
			ar.setCodigoBarra("barra-" + i);
			ar.setObservacion("");
			ar.setArticuloMarca(tt.sinReferenciaTipo);
			ar.setArticuloParte(tt.sinReferenciaTipo);
			ar.setArticuloLinea(tt.sinReferenciaTipo);
			ar.setArticuloFamilia(tt.sinReferenciaTipo);
			ar.setArticuloEstado(tt.estadoArticulo1);
			// ar.setArticuloEstado(rr.getArticuloEstadoById(1));
			// ar.setArticuloFamilia(rr.getArticuloFamiliaById(1));
			// ar.setArticuloLinea(rr.getArticuloLineaById(1));
			// ar.setArticuloMarca(rr.getArticuloMarcaById(1));
			// ar.setArticuloParte(rr.getArticuloParteById(1));
			ar.setArticuloPresentacion(rr.getArticuloPresentacionById(1));

			Set<ProveedorArticulo> prartAR = new HashSet<ProveedorArticulo>();
			ar.setProveedorArticulos(prartAR);

			grabarDB(ar);

		}

		/************************************* ARTICULOS POR DEPOSITO **********************************/

		ArticuloDeposito artDep1 = new ArticuloDeposito();
		artDep1.setStock(50);
		artDep1.setStockMaximo(50);
		artDep1.setStockMinimo(5);
		artDep1.setArticulo(art4_reparo);

		ArticuloDeposito artDep2 = new ArticuloDeposito();
		artDep2.setStock(50);
		artDep2.setStockMaximo(50);
		artDep2.setStockMinimo(5);
		artDep2.setArticulo(art2_Valvoline);

		ArticuloDeposito artDep3 = new ArticuloDeposito();
		artDep3.setStock(50);
		artDep3.setStockMaximo(50);
		artDep3.setStockMinimo(5);
		artDep3.setArticulo(art3_Valvoline);

		ArticuloDeposito artDep4 = new ArticuloDeposito();
		artDep4.setStock(0);
		artDep4.setStockMaximo(50);
		artDep4.setStockMinimo(5);
		artDep4.setArticulo(art4_Valvoline);

		ArticuloDeposito artDep5 = new ArticuloDeposito();
		artDep5.setStock(0);
		artDep5.setStockMaximo(50);
		artDep5.setStockMinimo(5);
		artDep5.setArticulo(art5_Valvoline);

		ArticuloDeposito artDep6 = new ArticuloDeposito();
		artDep6.setStock(50);
		artDep6.setStockMaximo(50);
		artDep6.setStockMinimo(5);
		artDep6.setArticulo(art6_Valvoline);

		ArticuloDeposito artDep7 = new ArticuloDeposito();
		artDep7.setStock(50);
		artDep7.setStockMaximo(50);
		artDep7.setStockMinimo(5);
		artDep7.setArticulo(art7_Valvoline);

		ArticuloDeposito artDep8 = new ArticuloDeposito();
		artDep8.setStock(50);
		artDep8.setStockMaximo(50);
		artDep8.setStockMinimo(5);
		artDep8.setArticulo(articulo8);

		ArticuloDeposito artDep9 = new ArticuloDeposito();
		artDep9.setStock(50);
		artDep9.setStockMaximo(50);
		artDep9.setStockMinimo(5);
		artDep9.setArticulo(art3_Valvoline);

		ArticuloDeposito artDep10 = new ArticuloDeposito();
		artDep10.setStock(50);
		artDep10.setStockMaximo(50);
		artDep10.setStockMinimo(5);
		artDep10.setArticulo(art4_Valvoline);

		ArticuloDeposito artDep11 = new ArticuloDeposito();
		artDep11.setStock(50);
		artDep11.setStockMaximo(50);
		artDep11.setStockMinimo(5);
		artDep11.setArticulo(art5_Valvoline);

		ArticuloDeposito artDep12 = new ArticuloDeposito();
		artDep12.setStock(50);
		artDep12.setStockMaximo(50);
		artDep12.setStockMinimo(5);
		artDep12.setArticulo(art7_Valvoline);

		// agrega los depositos a los articulos
		artDep1.setDeposito(dep1);
		artDep2.setDeposito(dep1);

		artDep3.setDeposito(dep2);
		artDep4.setDeposito(dep2);

		artDep5.setDeposito(dep3);

		artDep6.setDeposito(dep4);
		artDep12.setDeposito(dep4);

		artDep7.setDeposito(dep5);

		artDep8.setDeposito(dep6);
		artDep9.setDeposito(dep6);

		artDep10.setDeposito(dep7);
		artDep11.setDeposito(dep7);
		
		grabarDB(artDep1);
		grabarDB(artDep2);
		grabarDB(artDep3);
		grabarDB(artDep4);
		grabarDB(artDep5);
		grabarDB(artDep6);
		grabarDB(artDep7);
		grabarDB(artDep8);
		grabarDB(artDep9);
		grabarDB(artDep10);
		grabarDB(artDep11);
		grabarDB(artDep12);


		/************************************* ARTICULOS COSTO **********************************/

		ArticuloCosto artCosto1 = new ArticuloCosto();
		artCosto1.setCostoFinalDs(2);
		artCosto1.setCostoFinalGs(9000);
		artCosto1.setFechaCompra(new Date());
		artCosto1.setIdMovimiento((long) 1);
		artCosto1.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto1);

		ArticuloCosto artCosto2 = new ArticuloCosto();
		artCosto2.setCostoFinalDs(3);
		artCosto2.setCostoFinalGs(13500);
		artCosto2.setFechaCompra(new Date());
		artCosto2.setIdMovimiento((long) 1);
		artCosto2.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto2);

		ArticuloCosto artCosto3 = new ArticuloCosto();
		artCosto3.setCostoFinalDs(2);
		artCosto3.setCostoFinalGs(9000);
		artCosto3.setFechaCompra(new Date());
		artCosto3.setIdMovimiento((long) 1);
		artCosto3.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto3);

		ArticuloCosto artCosto4 = new ArticuloCosto();
		artCosto4.setCostoFinalDs(4);
		artCosto4.setCostoFinalGs(18000);
		artCosto4.setFechaCompra(new Date());
		artCosto4.setIdMovimiento((long) 1);
		artCosto4.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto4);

		ArticuloCosto artCosto5 = new ArticuloCosto();
		artCosto5.setCostoFinalDs(5);
		artCosto5.setCostoFinalGs(22500);
		artCosto5.setFechaCompra(new Date());
		// ojo aca decia artDep6 cambie a artDep5 - vere
		artCosto5.setIdMovimiento((long) 1);
		artCosto5.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto5);

		ArticuloCosto artCosto6 = new ArticuloCosto();
		artCosto6.setCostoFinalDs(3);
		artCosto6.setCostoFinalGs(13500);
		artCosto6.setFechaCompra(new Date());
		artCosto6.setIdMovimiento((long) 1);
		artCosto6.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto6);

		ArticuloCosto artCosto7 = new ArticuloCosto();
		artCosto7.setCostoFinalDs(2);
		artCosto7.setCostoFinalGs(9000);
		artCosto7.setFechaCompra(new Date());
		artCosto7.setIdMovimiento((long) 1);
		artCosto7.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto7);

		ArticuloCosto artCosto8 = new ArticuloCosto();
		artCosto8.setCostoFinalDs(3);
		artCosto8.setCostoFinalGs(13500);
		artCosto8.setFechaCompra(new Date());
		artCosto8.setIdMovimiento((long) 1);
		artCosto8.setTipoMovimiento(tt.tipoMov3);
		grabarDB(artCosto8);

		/*********************************************** B.D. IMPORTACIONES *********************************************/

		// ----------------------- Una Factura con dos detalles

		Set<ImportacionFacturaDetalle> detalle = new HashSet<ImportacionFacturaDetalle>();
		Set<ImportacionFactura> facturas = new HashSet<ImportacionFactura>();

		ImportacionFacturaDetalle detalle1 = new ImportacionFacturaDetalle();
		detalle1.setCantidad(10);
		detalle1.setCostoDs(10);
		detalle1.setCostoGs(45000);
		detalle1.setArticulo(art1_Valvoline);
		detalle1.setTextoDescuento("");
		detalle1.setDescuentoDs(0);
		detalle1.setDescuentoGs(0);
		detalle.add(detalle1);

		ImportacionFacturaDetalle detalle2 = new ImportacionFacturaDetalle();
		detalle2.setCantidad(5);
		detalle2.setCostoDs(5);
		detalle2.setCostoGs(22500);
		detalle2.setArticulo(art2_Valvoline);
		detalle2.setTextoDescuento("");
		detalle2.setDescuentoDs(0);
		detalle2.setDescuentoGs(0);
		detalle.add(detalle2);

		ImportacionFacturaDetalle detalle3Fact1 = new ImportacionFacturaDetalle();
		detalle3Fact1.setCantidad(10);
		detalle3Fact1.setCostoDs(5);
		detalle3Fact1.setCostoGs(22500);
		detalle3Fact1.setArticulo(art3_Valvoline);
		detalle3Fact1.setTextoDescuento("");
		detalle3Fact1.setDescuentoDs(0);
		detalle3Fact1.setDescuentoGs(0);
		detalle.add(detalle3Fact1);

		ImportacionFacturaDetalle detalle4Fact1 = new ImportacionFacturaDetalle();
		detalle4Fact1.setCantidad(10);
		detalle4Fact1.setCostoDs(5);
		detalle4Fact1.setCostoGs(22500);
		detalle4Fact1.setArticulo(art4_Valvoline);
		detalle4Fact1.setTextoDescuento("");
		detalle4Fact1.setDescuentoDs(0);
		detalle4Fact1.setDescuentoGs(0);
		detalle.add(detalle4Fact1);

		ImportacionFacturaDetalle detalle5Fact1 = new ImportacionFacturaDetalle();
		detalle5Fact1.setCantidad(10);
		detalle5Fact1.setCostoDs(5);
		detalle5Fact1.setCostoGs(22500);
		detalle5Fact1.setArticulo(art5_Valvoline);
		detalle5Fact1.setTextoDescuento("");
		detalle5Fact1.setDescuentoDs(0);
		detalle5Fact1.setDescuentoGs(0);
		detalle.add(detalle5Fact1);

		ImportacionFactura factura1 = new ImportacionFactura();
		factura1.setNumero("001-001-0000100");
		factura1.setFechaOriginal(new Date());
		factura1.setFechaCreacion(new Date());
		factura1.setDetalles(detalle);
		factura1.setConfirmadoImportacion(false);
		factura1.setConfirmadoAuditoria(false);
		factura1.setConfirmadoVentas(false);
		factura1.setConfirmadoAdministracion(false);
		factura1.setPropietarioActual(2);
		factura1.setFacturaVerificada(false);
		factura1.setDescuentoDs(0);
		factura1.setDescuentoGs(0);
		factura1.setCondicionPago(tt.icp);
		factura1.setMoneda(tt.dolar);
		factura1.setObservacion("");
		factura1.setProveedor(prov1);
		factura1.setRecepcionConfirmada(false);
		factura1.setTotalAsignadoDs(10);
		factura1.setTotalAsignadoGs(45000);
		factura1.setTipoMovimiento(tt.tipoMov3);
		facturas.add(factura1);

		// --------------------------------- Una factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles2 = new HashSet<ImportacionFacturaDetalle>();

		ImportacionFacturaDetalle detalle3 = new ImportacionFacturaDetalle();
		detalle3.setCantidad(3);
		detalle3.setCostoDs(5);
		detalle3.setCostoGs(22500);
		detalle3.setArticulo(art5_Valvoline);
		detalle3.setTextoDescuento("");
		detalle3.setDescuentoDs(0);
		detalle3.setDescuentoGs(0);
		detalles2.add(detalle3);

		ImportacionFacturaDetalle detalle4 = new ImportacionFacturaDetalle();
		detalle4.setCantidad(3);
		detalle4.setCostoDs(5);
		detalle4.setCostoGs(22500);
		detalle4.setArticulo(art6_Valvoline);
		detalle4.setTextoDescuento("");
		detalle4.setDescuentoDs(0);
		detalle4.setDescuentoGs(0);
		detalles2.add(detalle4);

		ImportacionFacturaDetalle detalle3Fact2 = new ImportacionFacturaDetalle();
		detalle3Fact2.setCantidad(3);
		detalle3Fact2.setCostoDs(5);
		detalle3Fact2.setCostoGs(22500);
		detalle3Fact2.setArticulo(art7_Valvoline);
		detalle3Fact2.setTextoDescuento("");
		detalle3Fact2.setDescuentoDs(0);
		detalle3Fact2.setDescuentoGs(0);
		detalles2.add(detalle3Fact2);

		ImportacionFactura factura2 = new ImportacionFactura();
		factura2.setNumero("001-001-0000101");
		factura2.setFechaOriginal(new Date());
		factura2.setFechaCreacion(new Date());
		factura2.setDetalles(detalles2);
		factura2.setConfirmadoImportacion(false);
		factura2.setConfirmadoAuditoria(false);
		factura2.setConfirmadoVentas(false);
		factura2.setConfirmadoAdministracion(false);
		factura2.setPropietarioActual(1);
		factura2.setFacturaVerificada(false);
		factura2.setDescuentoDs(0);
		factura2.setDescuentoGs(0);
		factura2.setCondicionPago(tt.icp);
		factura2.setMoneda(tt.dolar);
		factura2.setObservacion("");
		factura2.setProveedor(prov1);
		factura2.setRecepcionConfirmada(false);
		factura2.setTotalAsignadoDs(10);
		factura2.setTotalAsignadoGs(45000);
		factura2.setTipoMovimiento(tt.tipoMov3);
		facturas.add(factura2);

		// --------------------------------- Una factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles_3 = new HashSet<ImportacionFacturaDetalle>();

		ImportacionFacturaDetalle detalle_3 = new ImportacionFacturaDetalle();
		detalle_3.setCantidad(3);
		detalle_3.setCostoDs(5);
		detalle_3.setCostoGs(22500);
		detalle_3.setArticulo(art5_Valvoline);
		detalle_3.setTextoDescuento("");
		detalle_3.setDescuentoDs(0);
		detalle_3.setDescuentoGs(0);
		detalles_3.add(detalle_3);

		ImportacionFacturaDetalle detalle_4 = new ImportacionFacturaDetalle();
		detalle_4.setCantidad(3);
		detalle_4.setCostoDs(5);
		detalle_4.setCostoGs(22500);
		detalle_4.setArticulo(art6_Valvoline);
		detalle_4.setTextoDescuento("");
		detalle_4.setDescuentoDs(0);
		detalle_4.setDescuentoGs(0);
		detalles_3.add(detalle_4);

		ImportacionFacturaDetalle detalle3_Fact2 = new ImportacionFacturaDetalle();
		detalle3_Fact2.setCantidad(4);
		detalle3_Fact2.setCostoDs(5);
		detalle3_Fact2.setCostoGs(22500);
		detalle3_Fact2.setArticulo(art7_Valvoline);
		detalle3_Fact2.setTextoDescuento("");
		detalle3_Fact2.setDescuentoDs(0);
		detalle3_Fact2.setDescuentoGs(0);
		detalles_3.add(detalle3_Fact2);

		ImportacionFactura factura_2 = new ImportacionFactura();
		factura_2.setNumero("001-001-0000103");
		factura_2.setFechaOriginal(new Date());
		factura_2.setFechaCreacion(new Date());
		factura_2.setDetalles(detalles_3);
		factura_2.setConfirmadoImportacion(false);
		factura_2.setConfirmadoAuditoria(false);
		factura_2.setConfirmadoVentas(false);
		factura_2.setConfirmadoAdministracion(false);
		factura_2.setPropietarioActual(1);
		factura_2.setFacturaVerificada(false);
		factura_2.setDescuentoDs(0);
		factura_2.setDescuentoGs(0);
		factura_2.setCondicionPago(tt.icp);
		factura_2.setMoneda(tt.dolar);
		factura_2.setObservacion("");
		factura_2.setProveedor(prov1);
		factura_2.setRecepcionConfirmada(false);
		factura_2.setTotalAsignadoDs(10);
		factura_2.setTotalAsignadoGs(45000);
		factura_2.setTipoMovimiento(tt.tipoMov3);
		facturas.add(factura_2);

		// ----------------------------- Gasto de Despacho

		ImportacionResumenGastosDespacho despacho1 = new ImportacionResumenGastosDespacho();
		despacho1.setNroDespacho("1245YZ");
		despacho1.setNroLiquidacion("13008IC4000011A");
		despacho1.setFechaFacturaDespacho(new Date());
		despacho1.setTipoCambio(4500);
		despacho1.setValorCIFds(1200);
		despacho1.setValorCIFgs(400000);
		despacho1.setValorFOBds(5000);
		despacho1.setValorFOBgs(22500000);
		despacho1.setValorFleteDs(100);
		despacho1.setValorFleteGs(450000);
		despacho1.setValorSeguroDs(50);
		despacho1.setValorSeguroGs(225000);
		despacho1.setTotalIVAds(600);
		despacho1.setTotalIVAgs(100000);
		despacho1.setTotalGastosDs(800);
		despacho1.setTotalGastosGs(900000);
		despacho1.setCambioGastoDespacho(false);
		despacho1.setCoeficiente(1.375);
		despacho1.setCoeficienteAsignado(1.480);
		despacho1.setDespachante(prov7);

		// ------------------------ Un Pedido Compra Confirmado con 7 detalles y
		// 2 facturas

		Set<ImportacionPedidoCompraDetalle> impPedCompDets = new HashSet<ImportacionPedidoCompraDetalle>();

		ImportacionPedidoCompraDetalle impPedCompDet = new ImportacionPedidoCompraDetalle();
		impPedCompDet.setArticulo(art1_Valvoline);
		impPedCompDet.setCantidad(5);

		impPedCompDet.setUltimoCostoDs(27000);
		impPedCompDet.setFechaUltimoCosto(new Date());
		impPedCompDet.setCostoProformaDs(1);
		impPedCompDet.setCostoProformaGs(4500);
		impPedCompDets.add(impPedCompDet);

		ImportacionPedidoCompraDetalle impPedCompDet2 = new ImportacionPedidoCompraDetalle();
		impPedCompDet2.setArticulo(art2_Valvoline);
		impPedCompDet2.setCantidad(3);

		impPedCompDet2.setUltimoCostoDs(27000);
		impPedCompDet2.setFechaUltimoCosto(new Date());
		impPedCompDet2.setCostoProformaDs(1);
		impPedCompDet2.setCostoProformaGs(4500);
		impPedCompDets.add(impPedCompDet2);

		ImportacionPedidoCompraDetalle impPedCompDet3 = new ImportacionPedidoCompraDetalle();
		impPedCompDet3.setArticulo(art3_Valvoline);
		impPedCompDet3.setCantidad(5);

		impPedCompDet3.setUltimoCostoDs(27000);
		impPedCompDet3.setFechaUltimoCosto(new Date());
		impPedCompDet3.setCostoProformaDs(2);
		impPedCompDet3.setCostoProformaGs(9000);

		impPedCompDets.add(impPedCompDet3);

		ImportacionPedidoCompraDetalle impPedCompDet4 = new ImportacionPedidoCompraDetalle();
		impPedCompDet4.setArticulo(art4_Valvoline);
		impPedCompDet4.setCantidad(5);

		impPedCompDet4.setUltimoCostoDs(27000);
		impPedCompDet4.setFechaUltimoCosto(new Date());
		impPedCompDet4.setCostoProformaDs(1);
		impPedCompDet4.setCostoProformaGs(4500);
		impPedCompDets.add(impPedCompDet4);

		ImportacionPedidoCompraDetalle impPedCompDet5 = new ImportacionPedidoCompraDetalle();
		impPedCompDet5.setArticulo(art5_Valvoline);
		impPedCompDet5.setCantidad(5);

		impPedCompDet5.setUltimoCostoDs(27000);
		impPedCompDet5.setFechaUltimoCosto(new Date());
		impPedCompDet5.setCostoProformaDs(1);
		impPedCompDet5.setCostoProformaGs(4500);
		impPedCompDets.add(impPedCompDet5);

		ImportacionPedidoCompraDetalle impPedCompDet6 = new ImportacionPedidoCompraDetalle();
		impPedCompDet6.setArticulo(art6_Valvoline);
		impPedCompDet6.setCantidad(3);

		impPedCompDet6.setUltimoCostoDs(27000);
		impPedCompDet6.setFechaUltimoCosto(new Date());
		impPedCompDet6.setCostoProformaDs(1);
		impPedCompDet6.setCostoProformaGs(9000);
		impPedCompDets.add(impPedCompDet6);

		ImportacionPedidoCompraDetalle impPedCompDet7 = new ImportacionPedidoCompraDetalle();
		impPedCompDet7.setArticulo(art7_Valvoline);
		impPedCompDet7.setCantidad(5);

		impPedCompDet7.setUltimoCostoDs(27000);
		impPedCompDet7.setFechaUltimoCosto(new Date());
		impPedCompDet7.setCostoProformaDs(1.05);
		impPedCompDet7.setCostoProformaGs(4500);
		impPedCompDets.add(impPedCompDet7);

		ImportacionPedidoCompra impPedComp1 = new ImportacionPedidoCompra();
		impPedComp1.setNumeroPedidoCompra("VAL-00001");
		impPedComp1.setFechaCreacion(new Date());
		impPedComp1.setFechaCierre(new Date());
		impPedComp1.setConfirmadoImportacion(true);
		impPedComp1.setConfirmadoVentas(true);
		impPedComp1.setConfirmadoAdministracion(true);
		impPedComp1.setPropietarioActual(4);
		impPedComp1.setPedidoConfirmado(true);
		impPedComp1.setSubDiarioConfirmado(false);
		impPedComp1.setImportacionConfirmada(false);
		impPedComp1.setImportacionPedidoCompraDetalle(impPedCompDets);
		impPedComp1.setImportacionFactura(facturas);
		impPedComp1.setResumenGastosDespacho(despacho1);
		impPedComp1.setEstado(tt.importacionEstado4);
		impPedComp1.setProveedor(prov1);
		impPedComp1.setProveedorCondicionPago(tt.icp);
		impPedComp1.setTipo(tt.tipoImportacion1);
		impPedComp1.setMoneda(tt.dolar);
		impPedComp1.setObservacion("PEDIDO COMPRA 1");
		impPedComp1.setCambio(4500);
		impPedComp1.setTipoMovimiento(tt.tipoMov8);
		impPedComp1.setCifProrrateado(false);
		grabarDB(impPedComp1);

		/**
		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se creo un Pedido de Compra", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se importo una Orden de Compra",
				"/yhaguy/archivos/ordenCompras/Population.csv");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se envio un correo de Cotizacion",
				"/yhaguy/archivos/pedidoCompra/Population.pdf");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se importo la Proforma",
				"/yhaguy/archivos/proformas/Population.csv");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0, "Se derivo a Ventas",
				null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se derivo a Importacion", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se confirmo el Pedido", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se envio un correo de Confirmacion",
				"/yhaguy/archivos/pedidoCompra/PopulationConfirmado.pdf");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se derivo a Administracion", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1.getNumeroPedidoCompra(), 0,
				"Se importo la Factura de Compra",
				"/yhaguy/archivos/facturaImportacion/Population.csv");**/

		// --------------------------------------------------------------------------------------------------

		// ----------------------- Una Factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles3 = new HashSet<ImportacionFacturaDetalle>();
		Set<ImportacionFactura> facturas2 = new HashSet<ImportacionFactura>();

		ImportacionFacturaDetalle detalle1Fact3 = new ImportacionFacturaDetalle();
		detalle1Fact3.setCantidad(10);
		detalle1Fact3.setCostoDs(5);
		detalle1Fact3.setCostoGs(22500);
		detalle1Fact3.setArticulo(art4_reparo);
		detalle1Fact3.setTextoDescuento("");
		detalle1Fact3.setDescuentoDs(0);
		detalle1Fact3.setDescuentoGs(0);
		detalles3.add(detalle1Fact3);

		ImportacionFacturaDetalle detalle2Fact3 = new ImportacionFacturaDetalle();
		detalle2Fact3.setCantidad(10);
		detalle2Fact3.setCostoDs(5);
		detalle2Fact3.setCostoGs(22500);
		detalle2Fact3.setArticulo(art4_reparo);
		detalle2Fact3.setTextoDescuento("");
		detalle2Fact3.setDescuentoDs(0);
		detalle2Fact3.setDescuentoGs(0);
		detalles3.add(detalle2Fact3);

		ImportacionFacturaDetalle detalle3Fact3 = new ImportacionFacturaDetalle();
		detalle3Fact3.setCantidad(10);
		detalle3Fact3.setCostoDs(5);
		detalle3Fact3.setCostoGs(22500);
		detalle3Fact3.setArticulo(art4_reparo);
		detalle3Fact3.setTextoDescuento("");
		detalle3Fact3.setDescuentoDs(0);
		detalle3Fact3.setDescuentoGs(0);
		detalles3.add(detalle3Fact3);

		ImportacionFacturaDetalle detalle4Fact3 = new ImportacionFacturaDetalle();
		detalle4Fact3.setCantidad(10);
		detalle4Fact3.setCostoDs(5);
		detalle4Fact3.setCostoGs(22500);
		detalle4Fact3.setArticulo(art4_reparo);
		detalle4Fact3.setTextoDescuento("");
		detalle4Fact3.setDescuentoDs(0);
		detalle4Fact3.setDescuentoGs(0);
		detalles3.add(detalle4Fact3);

		ImportacionFactura factura3 = new ImportacionFactura();
		factura3.setNumero("001-001-0000102");
		factura3.setFechaOriginal(new Date());
		factura3.setFechaCreacion(new Date());
		factura3.setDetalles(detalles3);
		factura3.setConfirmadoImportacion(false);
		factura3.setConfirmadoAuditoria(false);
		factura3.setConfirmadoVentas(false);
		factura3.setConfirmadoAdministracion(false);
		factura3.setPropietarioActual(2);
		factura3.setFacturaVerificada(false);
		factura3.setDescuentoDs(0);
		factura3.setDescuentoGs(0);
		factura3.setCondicionPago(tt.icp);
		factura3.setMoneda(tt.dolar);
		factura3.setObservacion("");
		factura3.setProveedor(prov1);
		factura3.setRecepcionConfirmada(false);
		factura3.setTotalAsignadoDs(10);
		factura3.setTotalAsignadoGs(45000);
		factura3.setTipoMovimiento(tt.tipoMov3);
		facturas2.add(factura3);

		// ----------------------------------------------------------------------------------------------

		// ----------------------------------- Una factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles4 = new HashSet<ImportacionFacturaDetalle>();

		ImportacionFacturaDetalle detalle1Fact4 = new ImportacionFacturaDetalle();
		detalle1Fact4.setCantidad(10);
		detalle1Fact4.setCostoDs(5);
		detalle1Fact4.setCostoGs(22500);
		detalle1Fact4.setArticulo(art5_Valvoline);
		detalle1Fact4.setTextoDescuento("");
		detalle1Fact4.setDescuentoDs(0);
		detalle1Fact4.setDescuentoGs(0);
		detalles4.add(detalle1Fact4);

		ImportacionFacturaDetalle detalle2Fact4 = new ImportacionFacturaDetalle();
		detalle2Fact4.setCantidad(10);
		detalle2Fact4.setCostoDs(5);
		detalle2Fact4.setCostoGs(22500);
		detalle2Fact4.setArticulo(art6_Valvoline);
		detalle2Fact4.setTextoDescuento("");
		detalle2Fact4.setDescuentoDs(0);
		detalle2Fact4.setDescuentoGs(0);
		detalles4.add(detalle2Fact4);

		ImportacionFacturaDetalle detalle3Fact4 = new ImportacionFacturaDetalle();
		detalle3Fact4.setCantidad(10);
		detalle3Fact4.setCostoDs(5);
		detalle3Fact4.setCostoGs(22500);
		detalle3Fact4.setArticulo(art7_Valvoline);
		detalle3Fact4.setDescuentoDs(0);
		detalle3Fact4.setDescuentoGs(0);
		detalles4.add(detalle3Fact4);

		ImportacionFactura factura4 = new ImportacionFactura();
		factura4.setNumero("001-001-0000105");
		factura4.setFechaOriginal(new Date());
		factura4.setFechaCreacion(new Date());
		factura4.setDetalles(detalles4);
		factura4.setConfirmadoImportacion(false);
		factura4.setConfirmadoAuditoria(false);
		factura4.setConfirmadoVentas(false);
		factura4.setConfirmadoAdministracion(false);
		factura4.setPropietarioActual(1);
		factura4.setFacturaVerificada(false);
		factura4.setDescuentoDs(0);
		factura4.setDescuentoGs(0);
		factura4.setCondicionPago(tt.icp);
		factura4.setMoneda(tt.dolar);
		factura4.setObservacion("");
		factura4.setProveedor(prov1);
		factura4.setRecepcionConfirmada(false);
		factura4.setTotalAsignadoDs(10);
		factura4.setTotalAsignadoGs(45000);
		factura4.setTipoMovimiento(tt.tipoMov3);
		facturas2.add(factura4);

		// ----------------------- Una Factura con dos detalles

		Set<ImportacionFacturaDetalle> detallei2 = new HashSet<ImportacionFacturaDetalle>();
		Set<ImportacionFactura> facturasi2 = new HashSet<ImportacionFactura>();

		ImportacionFacturaDetalle detalle1i2 = new ImportacionFacturaDetalle();
		detalle1i2.setCantidad(3);
		detalle1i2.setCostoDs(10);
		detalle1i2.setCostoGs(45000);
		detalle1i2.setArticulo(art1_Valvoline);
		detalle1i2.setTextoDescuento("");
		detalle1i2.setDescuentoDs(0);
		detalle1i2.setDescuentoGs(0);
		detallei2.add(detalle1i2);

		ImportacionFacturaDetalle detalle2i2 = new ImportacionFacturaDetalle();
		detalle2i2.setCantidad(3);
		detalle2i2.setCostoDs(5);
		detalle2i2.setCostoGs(22500);
		detalle2i2.setArticulo(art2_Valvoline);
		detalle2i2.setTextoDescuento("");
		detalle2i2.setDescuentoDs(0);
		detalle2i2.setDescuentoGs(0);
		detallei2.add(detalle2i2);

		ImportacionFacturaDetalle detalle3Fact1i2 = new ImportacionFacturaDetalle();
		detalle3Fact1i2.setCantidad(3);
		detalle3Fact1i2.setCostoDs(5);
		detalle3Fact1i2.setCostoGs(22500);
		detalle3Fact1i2.setArticulo(art3_Valvoline);
		detalle3Fact1i2.setTextoDescuento("");
		detalle3Fact1i2.setDescuentoDs(0);
		detalle3Fact1i2.setDescuentoGs(0);
		detallei2.add(detalle3Fact1i2);

		ImportacionFacturaDetalle detalle4Fact1i2 = new ImportacionFacturaDetalle();
		detalle4Fact1i2.setCantidad(3);
		detalle4Fact1i2.setCostoDs(5);
		detalle4Fact1i2.setCostoGs(22500);
		detalle4Fact1i2.setArticulo(art4_Valvoline);
		detalle4Fact1i2.setTextoDescuento("");
		detalle4Fact1i2.setDescuentoDs(0);
		detalle4Fact1i2.setDescuentoGs(0);
		detallei2.add(detalle4Fact1i2);

		ImportacionFacturaDetalle detalle5Fact1i2 = new ImportacionFacturaDetalle();
		detalle5Fact1i2.setCantidad(3);
		detalle5Fact1i2.setCostoDs(5);
		detalle5Fact1i2.setCostoGs(22500);
		detalle5Fact1i2.setArticulo(art5_Valvoline);
		detalle5Fact1i2.setTextoDescuento("");
		detalle5Fact1i2.setDescuentoDs(0);
		detalle5Fact1i2.setDescuentoGs(0);
		detallei2.add(detalle5Fact1i2);

		ImportacionFactura factura1i2 = new ImportacionFactura();
		factura1i2.setNumero("001-001-0000200");
		factura1i2.setFechaOriginal(new Date());
		factura1i2.setFechaCreacion(new Date());
		factura1i2.setDetalles(detallei2);
		factura1i2.setConfirmadoImportacion(false);
		factura1i2.setConfirmadoAuditoria(false);
		factura1i2.setConfirmadoVentas(false);
		factura1i2.setConfirmadoAdministracion(false);
		factura1i2.setPropietarioActual(2);
		factura1i2.setFacturaVerificada(false);
		factura1i2.setDescuentoDs(0);
		factura1i2.setDescuentoGs(0);
		factura1i2.setCondicionPago(tt.icp);
		factura1i2.setMoneda(tt.dolar);
		factura1i2.setObservacion("");
		factura1i2.setProveedor(prov1);
		factura1i2.setRecepcionConfirmada(false);
		factura1i2.setTotalAsignadoDs(10);
		factura1i2.setTotalAsignadoGs(45000);
		factura1i2.setTipoMovimiento(tt.tipoMov3);
		facturasi2.add(factura1i2);

		// --------------------------------- Una factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles2i2 = new HashSet<ImportacionFacturaDetalle>();

		ImportacionFacturaDetalle detalle3i2 = new ImportacionFacturaDetalle();
		detalle3i2.setCantidad(10);
		detalle3i2.setCostoDs(5);
		detalle3i2.setCostoGs(22500);
		detalle3i2.setArticulo(art5_Valvoline);
		detalle3i2.setTextoDescuento("");
		detalle3i2.setDescuentoDs(0);
		detalle3i2.setDescuentoGs(0);
		detalles2i2.add(detalle3i2);

		ImportacionFacturaDetalle detalle4i2 = new ImportacionFacturaDetalle();
		detalle4i2.setCantidad(3);
		detalle4i2.setCostoDs(5);
		detalle4i2.setCostoGs(22500);
		detalle4i2.setArticulo(art6_Valvoline);
		detalle4i2.setTextoDescuento("");
		detalle4i2.setDescuentoDs(0);
		detalle4i2.setDescuentoGs(0);
		detalles2i2.add(detalle4i2);

		ImportacionFacturaDetalle detalle3Fact2i2 = new ImportacionFacturaDetalle();
		detalle3Fact2i2.setCantidad(6);
		detalle3Fact2i2.setCostoDs(5);
		detalle3Fact2i2.setCostoGs(22500);
		detalle3Fact2i2.setArticulo(art7_Valvoline);
		detalle3Fact2i2.setTextoDescuento("");
		detalle3Fact2i2.setDescuentoDs(0);
		detalle3Fact2i2.setDescuentoGs(0);
		detalles2i2.add(detalle3Fact2i2);

		ImportacionFactura factura2i2 = new ImportacionFactura();
		factura2i2.setNumero("001-001-0000201");
		factura2i2.setFechaOriginal(new Date());
		factura2i2.setFechaCreacion(new Date());
		factura2i2.setDetalles(detalles2i2);
		factura2i2.setConfirmadoImportacion(false);
		factura2i2.setConfirmadoAuditoria(false);
		factura2i2.setConfirmadoVentas(false);
		factura2i2.setConfirmadoAdministracion(false);
		factura2i2.setPropietarioActual(1);
		factura2i2.setFacturaVerificada(false);
		factura2i2.setDescuentoDs(0);
		factura2i2.setDescuentoGs(0);
		factura2i2.setCondicionPago(tt.icp);
		factura2i2.setMoneda(tt.dolar);
		factura2i2.setObservacion("");
		factura2i2.setProveedor(prov1);
		factura2i2.setRecepcionConfirmada(false);
		factura2i2.setTotalAsignadoDs(10);
		factura2i2.setTotalAsignadoGs(45000);
		factura2i2.setTipoMovimiento(tt.tipoMov3);
		facturasi2.add(factura2i2);

		// --------------------------------- Una factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles_3i2 = new HashSet<ImportacionFacturaDetalle>();

		ImportacionFacturaDetalle detalle_3i2 = new ImportacionFacturaDetalle();
		detalle_3i2.setCantidad(10);
		detalle_3i2.setCostoDs(5);
		detalle_3i2.setCostoGs(22500);
		detalle_3i2.setArticulo(art5_Valvoline);
		detalle_3i2.setTextoDescuento("");
		detalle_3i2.setDescuentoDs(0);
		detalle_3i2.setDescuentoGs(0);
		detalles_3i2.add(detalle_3i2);

		ImportacionFacturaDetalle detalle_4i2 = new ImportacionFacturaDetalle();
		detalle_4i2.setCantidad(5);
		detalle_4i2.setCostoDs(5);
		detalle_4i2.setCostoGs(22500);
		detalle_4i2.setArticulo(art6_Valvoline);
		detalle_4i2.setTextoDescuento("");
		detalle_4i2.setDescuentoDs(0);
		detalle_4i2.setDescuentoGs(0);
		detalles_3i2.add(detalle_4i2);

		ImportacionFacturaDetalle detalle3_Fact2i2 = new ImportacionFacturaDetalle();
		detalle3_Fact2i2.setCantidad(5);
		detalle3_Fact2i2.setCostoDs(5);
		detalle3_Fact2i2.setCostoGs(22500);
		detalle3_Fact2i2.setArticulo(art7_Valvoline);
		detalle3_Fact2i2.setTextoDescuento("");
		detalle3_Fact2i2.setDescuentoDs(0);
		detalle3_Fact2i2.setDescuentoGs(0);
		detalles_3i2.add(detalle3_Fact2i2);

		ImportacionFactura factura_2i2 = new ImportacionFactura();
		factura_2i2.setNumero("001-001-0000203");
		factura_2i2.setFechaOriginal(new Date());
		factura_2i2.setFechaCreacion(new Date());
		factura_2i2.setDetalles(detalles_3i2);
		factura_2i2.setConfirmadoImportacion(false);
		factura_2i2.setConfirmadoAuditoria(false);
		factura_2i2.setConfirmadoVentas(false);
		factura_2i2.setConfirmadoAdministracion(false);
		factura_2i2.setPropietarioActual(1);
		factura_2i2.setFacturaVerificada(false);
		factura_2i2.setDescuentoDs(0);
		factura_2i2.setDescuentoGs(0);
		factura_2i2.setCondicionPago(tt.icp);
		factura_2i2.setMoneda(tt.dolar);
		factura_2i2.setObservacion("");
		factura_2i2.setProveedor(prov1);
		factura_2i2.setRecepcionConfirmada(false);
		factura_2i2.setTotalAsignadoDs(10);
		factura_2i2.setTotalAsignadoGs(45000);
		factura_2i2.setTipoMovimiento(tt.tipoMov3);
		facturasi2.add(factura_2i2);

		// ----------------------------- Gasto de Despacho

		ImportacionResumenGastosDespacho despacho1i2 = new ImportacionResumenGastosDespacho();
		despacho1i2.setNroDespacho("37E1R");
		despacho1i2.setNroLiquidacion("13008IC4000011B");
		despacho1i2.setFechaFacturaDespacho(new Date());
		despacho1i2.setTipoCambio(4500);
		despacho1i2.setValorCIFds(1200);
		despacho1i2.setValorCIFgs(400000);
		despacho1i2.setValorFOBds(7000);
		despacho1i2.setValorFOBgs(31500000);
		despacho1i2.setValorFleteDs(100);
		despacho1i2.setValorFleteGs(450000);
		despacho1i2.setValorSeguroDs(50);
		despacho1i2.setValorSeguroGs(225000);
		despacho1i2.setTotalIVAds(600);
		despacho1i2.setTotalIVAgs(100000);
		despacho1i2.setTotalGastosDs(800);
		despacho1i2.setTotalGastosGs(900000);
		despacho1i2.setCambioGastoDespacho(false);
		despacho1i2.setCoeficiente(1.237);
		despacho1i2.setCoeficienteAsignado(1.421);
		despacho1i2.setDespachante(prov7);

		// ------------------------ Un Pedido Compra Confirmado con 7 detalles y
		// 2 facturas

		Set<ImportacionPedidoCompraDetalle> impPedCompDetsi2 = new HashSet<ImportacionPedidoCompraDetalle>();

		ImportacionPedidoCompraDetalle impPedCompDeti2 = new ImportacionPedidoCompraDetalle();
		impPedCompDeti2.setArticulo(art1_Valvoline);
		impPedCompDeti2.setCantidad(5);

		impPedCompDeti2.setUltimoCostoDs(27000);
		impPedCompDeti2.setFechaUltimoCosto(new Date());
		impPedCompDeti2.setCostoProformaDs(1);
		impPedCompDeti2.setCostoProformaGs(4500);
		impPedCompDetsi2.add(impPedCompDeti2);

		ImportacionPedidoCompraDetalle impPedCompDet2i2 = new ImportacionPedidoCompraDetalle();
		impPedCompDet2i2.setArticulo(art2_Valvoline);
		impPedCompDet2i2.setCantidad(5);

		impPedCompDet2i2.setUltimoCostoDs(27000);
		impPedCompDet2i2.setFechaUltimoCosto(new Date());
		impPedCompDet2i2.setCostoProformaDs(1);
		impPedCompDet2i2.setCostoProformaGs(4500);
		impPedCompDetsi2.add(impPedCompDet2i2);

		ImportacionPedidoCompraDetalle impPedCompDet3i2 = new ImportacionPedidoCompraDetalle();
		impPedCompDet3i2.setArticulo(art3_Valvoline);
		impPedCompDet3i2.setCantidad(10);

		impPedCompDet3i2.setUltimoCostoDs(27000);
		impPedCompDet3i2.setFechaUltimoCosto(new Date());
		impPedCompDet3i2.setCostoProformaDs(2);
		impPedCompDet3i2.setCostoProformaGs(9000);

		impPedCompDetsi2.add(impPedCompDet3i2);

		ImportacionPedidoCompraDetalle impPedCompDet4i2 = new ImportacionPedidoCompraDetalle();
		impPedCompDet4i2.setArticulo(art4_Valvoline);
		impPedCompDet4i2.setCantidad(10);

		impPedCompDet4i2.setUltimoCostoDs(27000);
		impPedCompDet4i2.setFechaUltimoCosto(new Date());
		impPedCompDet4i2.setCostoProformaDs(1);
		impPedCompDet4i2.setCostoProformaGs(4500);
		impPedCompDetsi2.add(impPedCompDet4i2);

		ImportacionPedidoCompraDetalle impPedCompDet5i2 = new ImportacionPedidoCompraDetalle();
		impPedCompDet5i2.setArticulo(art5_Valvoline);
		impPedCompDet5i2.setCantidad(10);

		impPedCompDet5i2.setUltimoCostoDs(27000);
		impPedCompDet5i2.setFechaUltimoCosto(new Date());
		impPedCompDet5i2.setCostoProformaDs(1);
		impPedCompDet5i2.setCostoProformaGs(4500);
		impPedCompDetsi2.add(impPedCompDet5i2);

		ImportacionPedidoCompraDetalle impPedCompDet6i2 = new ImportacionPedidoCompraDetalle();
		impPedCompDet6i2.setArticulo(art6_Valvoline);
		impPedCompDet6i2.setCantidad(5);

		impPedCompDet6i2.setUltimoCostoDs(27000);
		impPedCompDet6i2.setFechaUltimoCosto(new Date());
		impPedCompDet6i2.setCostoProformaDs(1);
		impPedCompDet6i2.setCostoProformaGs(9000);
		impPedCompDetsi2.add(impPedCompDet6i2);

		ImportacionPedidoCompraDetalle impPedCompDet7i2 = new ImportacionPedidoCompraDetalle();
		impPedCompDet7i2.setArticulo(art7_Valvoline);
		impPedCompDet7i2.setCantidad(10);

		impPedCompDet7i2.setUltimoCostoDs(27000);
		impPedCompDet7i2.setFechaUltimoCosto(new Date());
		impPedCompDet7i2.setCostoProformaDs(1.05);
		impPedCompDet7i2.setCostoProformaGs(4500);
		impPedCompDetsi2.add(impPedCompDet7i2);

		ImportacionPedidoCompra impPedComp1i2 = new ImportacionPedidoCompra();
		impPedComp1i2.setNumeroPedidoCompra("VAL-00002");
		impPedComp1i2.setFechaCreacion(new Date());
		impPedComp1i2.setFechaCierre(new Date());
		impPedComp1i2.setConfirmadoImportacion(true);
		impPedComp1i2.setConfirmadoVentas(true);
		impPedComp1i2.setConfirmadoAdministracion(true);
		impPedComp1i2.setPropietarioActual(4);
		impPedComp1i2.setPedidoConfirmado(true);
		impPedComp1i2.setSubDiarioConfirmado(false);
		impPedComp1i2.setImportacionConfirmada(false);
		impPedComp1i2.setImportacionPedidoCompraDetalle(impPedCompDetsi2);
		impPedComp1i2.setImportacionFactura(facturasi2);
		impPedComp1i2.setResumenGastosDespacho(despacho1i2);
		impPedComp1i2.setEstado(tt.importacionEstado4);
		impPedComp1i2.setProveedor(prov1);
		impPedComp1i2.setProveedorCondicionPago(tt.icp);
		impPedComp1i2.setTipo(tt.tipoImportacion1);
		impPedComp1i2.setMoneda(tt.dolar);
		impPedComp1i2.setObservacion("PEDIDO COMPRA 1");
		impPedComp1i2.setCambio(4500);
		impPedComp1i2.setTipoMovimiento(tt.tipoMov8);
		impPedComp1i2.setCifProrrateado(false);
		grabarDB(impPedComp1i2);

		/**
		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se creo un Pedido de Compra", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se importo una Orden de Compra",
				"/yhaguy/archivos/ordenCompras/Population.csv");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se envio un correo de Cotizacion",
				"/yhaguy/archivos/pedidoCompra/Population.pdf");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se importo la Proforma",
				"/yhaguy/archivos/proformas/Population.csv");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0, "Se derivo a Ventas",
				null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se derivo a Importacion", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se confirmo el Pedido", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se envio un correo de Confirmacion",
				"/yhaguy/archivos/pedidoCompra/PopulationConfirmado.pdf");

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se derivo a Administracion", null);

		agenda.addDetalle(agenda.COTIZACION,
				impPedComp1i2.getNumeroPedidoCompra(), 0,
				"Se importo la Factura de Compra",
				"/yhaguy/archivos/facturaImportacion/Population.csv");**/

		// --------------------------------------------------------------------------------------------------

		// ----------------------- Una Factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles3i2 = new HashSet<ImportacionFacturaDetalle>();
		Set<ImportacionFactura> facturas2i2 = new HashSet<ImportacionFactura>();

		ImportacionFacturaDetalle detalle1Fact3i2 = new ImportacionFacturaDetalle();
		detalle1Fact3i2.setCantidad(10);
		detalle1Fact3i2.setCostoDs(5);
		detalle1Fact3i2.setCostoGs(22500);
		detalle1Fact3i2.setArticulo(art4_reparo);
		detalle1Fact3i2.setTextoDescuento("");
		detalle1Fact3i2.setDescuentoDs(0);
		detalle1Fact3i2.setDescuentoGs(0);
		detalles3i2.add(detalle1Fact3i2);

		ImportacionFacturaDetalle detalle2Fact3i2 = new ImportacionFacturaDetalle();
		detalle2Fact3i2.setCantidad(10);
		detalle2Fact3i2.setCostoDs(5);
		detalle2Fact3i2.setCostoGs(22500);
		detalle2Fact3i2.setArticulo(art4_reparo);
		detalle2Fact3i2.setTextoDescuento("");
		detalle2Fact3i2.setDescuentoDs(0);
		detalle2Fact3i2.setDescuentoGs(0);
		detalles3i2.add(detalle2Fact3i2);

		ImportacionFacturaDetalle detalle3Fact3i2 = new ImportacionFacturaDetalle();
		detalle3Fact3i2.setCantidad(10);
		detalle3Fact3i2.setCostoDs(5);
		detalle3Fact3i2.setCostoGs(22500);
		detalle3Fact3i2.setArticulo(art4_reparo);
		detalle3Fact3i2.setTextoDescuento("");
		detalle3Fact3i2.setDescuentoDs(0);
		detalle3Fact3i2.setDescuentoGs(0);
		detalles3i2.add(detalle3Fact3i2);

		ImportacionFacturaDetalle detalle4Fact3i2 = new ImportacionFacturaDetalle();
		detalle4Fact3i2.setCantidad(10);
		detalle4Fact3i2.setCostoDs(5);
		detalle4Fact3i2.setCostoGs(22500);
		detalle4Fact3i2.setArticulo(art4_reparo);
		detalle4Fact3i2.setTextoDescuento("");
		detalle4Fact3i2.setDescuentoDs(0);
		detalle4Fact3i2.setDescuentoGs(0);
		detalles3i2.add(detalle4Fact3i2);

		ImportacionFactura factura3i2 = new ImportacionFactura();
		factura3i2.setNumero("001-001-0000202");
		factura3i2.setFechaOriginal(new Date());
		factura3i2.setFechaCreacion(new Date());
		factura3i2.setDetalles(detalles3i2);
		factura3i2.setConfirmadoImportacion(false);
		factura3i2.setConfirmadoAuditoria(false);
		factura3i2.setConfirmadoVentas(false);
		factura3i2.setConfirmadoAdministracion(false);
		factura3i2.setPropietarioActual(2);
		factura3i2.setFacturaVerificada(false);
		factura3i2.setDescuentoDs(0);
		factura3i2.setDescuentoGs(0);
		factura3i2.setCondicionPago(tt.icp);
		factura3i2.setMoneda(tt.dolar);
		factura3i2.setObservacion("");
		factura3i2.setProveedor(prov1);
		factura3i2.setRecepcionConfirmada(false);
		factura3i2.setTotalAsignadoDs(10);
		factura3i2.setTotalAsignadoGs(45000);
		factura3i2.setTipoMovimiento(tt.tipoMov3);
		facturas2i2.add(factura3i2);

		// ----------------------------------------------------------------------------------------------

		// ----------------------------------- Una factura con dos detalles

		Set<ImportacionFacturaDetalle> detalles4i2 = new HashSet<ImportacionFacturaDetalle>();

		ImportacionFacturaDetalle detalle1Fact4i2 = new ImportacionFacturaDetalle();
		detalle1Fact4i2.setCantidad(18);
		detalle1Fact4i2.setCostoDs(5);
		detalle1Fact4i2.setCostoGs(22500);
		detalle1Fact4i2.setArticulo(art5_Valvoline);
		detalle1Fact4i2.setTextoDescuento("");
		detalle1Fact4i2.setDescuentoDs(0);
		detalle1Fact4i2.setDescuentoGs(0);
		detalles4i2.add(detalle1Fact4i2);

		ImportacionFacturaDetalle detalle2Fact4i2 = new ImportacionFacturaDetalle();
		detalle2Fact4i2.setCantidad(10);
		detalle2Fact4i2.setCostoDs(5);
		detalle2Fact4i2.setCostoGs(22500);
		detalle2Fact4i2.setArticulo(art6_Valvoline);
		detalle2Fact4i2.setTextoDescuento("");
		detalle2Fact4i2.setDescuentoDs(0);
		detalle2Fact4i2.setDescuentoGs(0);
		detalles4i2.add(detalle2Fact4i2);

		ImportacionFacturaDetalle detalle3Fact4i2 = new ImportacionFacturaDetalle();
		detalle3Fact4i2.setCantidad(10);
		detalle3Fact4i2.setCostoDs(5);
		detalle3Fact4i2.setCostoGs(22500);
		detalle3Fact4i2.setArticulo(art7_Valvoline);
		detalle3Fact4i2.setDescuentoDs(0);
		detalle3Fact4i2.setDescuentoGs(0);
		detalles4i2.add(detalle3Fact4i2);

		ImportacionFactura factura4i2 = new ImportacionFactura();
		factura4i2.setNumero("001-001-0000105");
		factura4i2.setFechaOriginal(new Date());
		factura4i2.setFechaCreacion(new Date());
		factura4i2.setDetalles(detalles4i2);
		factura4i2.setConfirmadoImportacion(false);
		factura4i2.setConfirmadoAuditoria(false);
		factura4i2.setConfirmadoVentas(false);
		factura4i2.setConfirmadoAdministracion(false);
		factura4i2.setPropietarioActual(1);
		factura4i2.setFacturaVerificada(false);
		factura4i2.setDescuentoDs(0);
		factura4i2.setDescuentoGs(0);
		factura4i2.setCondicionPago(tt.icp);
		factura4i2.setMoneda(tt.dolar);
		factura4i2.setObservacion("");
		factura4i2.setProveedor(prov1);
		factura4i2.setRecepcionConfirmada(false);
		factura4i2.setTotalAsignadoDs(10);
		factura4i2.setTotalAsignadoGs(45000);
		factura4i2.setTipoMovimiento(tt.tipoMov3);
		facturas2i2.add(factura4i2);

		AutoNumero autoNroImp = new AutoNumero();
		autoNroImp.setDescripcion("Importacion Pedido Compra");
		autoNroImp.setFecha(new Date());
		autoNroImp.setKey("VAL");
		autoNroImp.setNumero(2); // ultimo numero Pedido Compra
		grabarDB(autoNroImp);

		long referencia1 = 1111;
		ArticuloReferencia art1Ref1 = new ArticuloReferencia();
		art1Ref1.setIdReferencia(referencia1);
		art1Ref1.setIdArticulo(1);
		grabarDB(art1Ref1);

		ArticuloReferencia art1Ref2 = new ArticuloReferencia();
		art1Ref2.setIdReferencia(referencia1);
		art1Ref2.setIdArticulo(3);
		grabarDB(art1Ref2);

		long referencia2 = 2222;
		ArticuloReferencia art1Ref3 = new ArticuloReferencia();
		art1Ref3.setIdReferencia(referencia2);
		art1Ref3.setIdArticulo(2);
		grabarDB(art1Ref3);

		ArticuloReferencia art1Ref4 = new ArticuloReferencia();
		art1Ref4.setIdReferencia(referencia2);
		art1Ref4.setIdArticulo(4);
		grabarDB(art1Ref4);

		ArticuloReferencia art1Ref5 = new ArticuloReferencia();
		art1Ref5.setIdReferencia(referencia2);
		art1Ref5.setIdArticulo(6);
		grabarDB(art1Ref5);
		

		/************************************* CUENTA CTE PROVEEDORES **********************************/

		CtaCteEmpresaMovimiento ccmp1 = new CtaCteEmpresaMovimiento();
		ccmp1.setIdEmpresa(empresa1.getId());
		ccmp1.setNroComprobante("FAC-CRE 001-002-0000540");
		ccmp1.setImporteOriginal(500000);
		ccmp1.setSaldo(250000);
		ccmp1.setFechaEmision(new Date());
		ccmp1.setFechaVencimiento(new Date());
		ccmp1.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento2);
		ccmp1.setIdMovimientoOriginal(1);
		ccmp1.setTipoMovimiento(tt.tipoMov2);
		ccmp1.setSucursal(sucApp1);
		ccmp1.setMoneda(tt.guarani);
		grabarDB(ccmp1);		

		/************************************** ORDEN PEDIDO GASTO **************************************/

		OrdenPedidoGastoDetalle ordDet1 = new OrdenPedidoGastoDetalle();
		ordDet1.setDepartamento(dto1);
		ordDet1.setCentroCosto(cc1);
		ordDet1.setImporte(105000);

		OrdenPedidoGastoDetalle ordDet2 = new OrdenPedidoGastoDetalle();
		ordDet2.setDepartamento(dto2);
		ordDet2.setCentroCosto(cc4);
		ordDet2.setImporte(150000);

		Set<OrdenPedidoGastoDetalle> ordDets1 = new HashSet<OrdenPedidoGastoDetalle>();
		ordDets1.add(ordDet1);
		ordDets1.add(ordDet2);

		OrdenPedidoGasto ped1 = new OrdenPedidoGasto();
		ped1.setNumero("OPG-00001");
		ped1.setIdUsuarioCarga(1);
		ped1.setNombreUsuarioCarga("Juan-Population");
		ped1.setDepartamento(dto3);
		ped1.setSucursal(sucApp2);
		ped1.setFechaCarga(new Date());
		ped1.setDescripcion("COMPRAS EQUIPOS INFORMATICOS RR HH.");
		ped1.setEstado("AUT");
		ped1.setAutorizado(true);
		ped1.setPresupuesto(false);
		ped1.setIdUsuarioAutoriza(1);
		ped1.setNombreUsuarioAutoriza("Supervisor");
		ped1.setFechaAutorizacion(new Date());
		ped1.setProveedor(prov7);
		ped1.setCondicionPago(tt.icp2);
		ped1.setOrdenPedidoGastoDetalle(ordDets1);

		grabarDB(ped1);

		/**
		agenda.addDetalle(agenda.NORMAL, ped1.getNumero(), 0,
				"Se creo el OrdenPedidoGasto", null);

		agenda.addDetalle(agenda.NORMAL, ped1.getNumero(), 0,
				"Se modifico el OrdenPedidoGasto", null);

		agenda.addDetalle(agenda.NORMAL, ped1.getNumero(), 0,
				"Se autorizo el OrdenPedidoGasto", null);**/

		OrdenPedidoGastoDetalle ordDet3 = new OrdenPedidoGastoDetalle();
		ordDet3.setDepartamento(dto3);
		ordDet3.setCentroCosto(cc3);
		ordDet3.setImporte(300000);

		OrdenPedidoGastoDetalle ordDet4 = new OrdenPedidoGastoDetalle();
		ordDet4.setDepartamento(dto4);
		ordDet4.setCentroCosto(cc4);
		ordDet4.setImporte(220000);

		Set<OrdenPedidoGastoDetalle> ordDets2 = new HashSet<OrdenPedidoGastoDetalle>();
		ordDets2.add(ordDet3);
		ordDets2.add(ordDet4);

		OrdenPedidoGasto ped2 = new OrdenPedidoGasto();
		ped2.setNumero("OPG-00002");
		ped2.setIdUsuarioCarga(2);
		ped2.setNombreUsuarioCarga("Maria-Population");
		ped2.setDepartamento(dto4);
		ped2.setSucursal(sucApp1);
		ped2.setFechaCarga(new Date());
		ped2.setDescripcion("COMPRAS EQUIPOS INFORMATICOS PARA AUDITORIA");
		ped2.setEstado("PEN");
		ped2.setAutorizado(false);
		ped2.setPresupuesto(false);
		ped2.setIdUsuarioAutoriza(0);
		ped2.setNombreUsuarioAutoriza("");
		ped2.setFechaAutorizacion(new Date());
		ped2.setProveedor(prov8);
		ped2.setCondicionPago(tt.icp);
		ped2.setOrdenPedidoGastoDetalle(ordDets2);

		grabarDB(ped2);

		/**
		agenda.addDetalle(agenda.NORMAL, ped2.getNumero(), 0,
				"Se creo el OrdenPedidoGasto", null);

		agenda.addDetalle(agenda.NORMAL, ped2.getNumero(), 0,
				"Se modifico el OrdenPedidoGasto", null);

		agenda.addDetalle(agenda.NORMAL, ped2.getNumero(), 0,
				"Se autorizo el OrdenPedidoGasto", null);**/

		AutoNumero autoNroPed = new AutoNumero();
		autoNroPed.setDescripcion("Orden Pedido Gasto");
		autoNroPed.setFecha(new Date());
		autoNroPed.setKey("OPG");
		autoNroPed.setNumero(2); // ultimo numero OrdenPedidoGasto
		grabarDB(autoNroPed);

		/************************************** TRASNPORTE ************************************/

		Transporte transporte1 = new Transporte();
		transporte1.setDescripcion("Camion");

		Transporte transporte2 = new Transporte();
		transporte2.setDescripcion("Auto");

		Transporte transporte3 = new Transporte();
		transporte3.setDescripcion("Moto");

		Transporte transporte4 = new Transporte();
		transporte4.setDescripcion("Camioneta");

		grabarDB(transporte1);
		grabarDB(transporte2);
		grabarDB(transporte3);
		grabarDB(transporte4);

		/************************************** TRANSFERENCIA ************************************/

		Transferencia transferencia = new Transferencia();
		transferencia.setNumero("TRF-00001");
		transferencia.setFechaCreacion(new Date());
		transferencia.setFechaRecepcion(new Date());
		transferencia.setFuncionarioCreador(funcionario1);
		transferencia.setFuncionarioReceptor(funcionario5);
		transferencia.setDepositoSalida(dep2);
		transferencia.setDepositoEntrada(dep3);

		Set<TransferenciaDetalle> transferenciaDetalles = new HashSet<TransferenciaDetalle>();

		TransferenciaDetalle transfDetalle1 = new TransferenciaDetalle();
		transfDetalle1.setCantidadPedida(11);
		transfDetalle1.setCantidadEnviada(3);
		transfDetalle1.setCosto(18000);
		transfDetalle1.setArticulo(art4_Valvoline);
		TransferenciaDetalle transfDetalle2 = new TransferenciaDetalle();
		transfDetalle2.setCantidadPedida(5);
		transfDetalle2.setCantidadEnviada(5);
		transfDetalle2.setCosto(22500);
		transfDetalle2.setArticulo(art3_Valvoline);
		/*
		 * TransferenciaDetalle transfDetalle3 = new TransferenciaDetalle();
		 * transfDetalle3.setCantidadPedida(8);
		 * transfDetalle3.setCantidadEnviada(7); transfDetalle3.setCosto(13500);
		 * transfDetalle3.setArticulo(art6_Valvoline);
		 */

		transferenciaDetalles.add(transfDetalle1);
		transferenciaDetalles.add(transfDetalle2);
		// transferenciaDetalles.add(transfDetalle3);
		transferencia.setDetalles(transferenciaDetalles);
		transferencia.setTransferenciaEstado(tt.transfEstado6);
		transferencia.setTransferenciaTipo(tt.transfTipo1);

		grabarDB(transferencia);

		Transferencia transferencia2 = new Transferencia();
		transferencia2.setNumero("TRF-00002");
		transferencia2.setFechaCreacion(new Date());
		transferencia2.setFechaRecepcion(new Date());
		transferencia2.setFuncionarioCreador(funcionario1);
		transferencia2.setFuncionarioReceptor(funcionario5);
		transferencia2.setDepositoSalida(dep5);
		transferencia2.setDepositoEntrada(dep4);

		Set<TransferenciaDetalle> transferenciaDetalles2 = new HashSet<TransferenciaDetalle>();

		TransferenciaDetalle transfDetalle4 = new TransferenciaDetalle();
		transfDetalle4.setCantidadPedida(5);
		transfDetalle4.setCantidadEnviada(5);
		transfDetalle4.setCosto(9000);
		transfDetalle4.setArticulo(art7_Valvoline);
		/*
		 * TransferenciaDetalle transfDetalle5 = new TransferenciaDetalle();
		 * transfDetalle5.setCantidadPedida(9);
		 * transfDetalle5.setCantidadEnviada(9); transfDetalle5.setCosto(18000);
		 * transfDetalle5.setArticulo(art4_Valvoline); TransferenciaDetalle
		 * transfDetalle6 = new TransferenciaDetalle();
		 * transfDetalle6.setCantidadPedida(8);
		 * transfDetalle6.setCantidadEnviada(7); transfDetalle6.setCosto(13500);
		 * transfDetalle6.setArticulo(articulo8);
		 */

		transferenciaDetalles2.add(transfDetalle4);
		// transferenciaDetalles2.add(transfDetalle5);
		// transferenciaDetalles2.add(transfDetalle6);
		transferencia2.setDetalles(transferenciaDetalles2);
		transferencia2.setTransferenciaEstado(tt.transfEstado2);
		transferencia2.setTransferenciaTipo(tt.transfTipo1);

		grabarDB(transferencia2);

		Transferencia transferencia3 = new Transferencia();
		transferencia3.setNumero("TRF-00003");
		transferencia3.setFechaCreacion(new Date());
		transferencia3.setFechaRecepcion(new Date());
		transferencia3.setFuncionarioCreador(funcionario1);
		transferencia3.setFuncionarioReceptor(funcionario5);
		transferencia3.setDepositoSalida(dep2);
		transferencia3.setDepositoEntrada(dep6);

		Set<TransferenciaDetalle> transferenciaDetalles3 = new HashSet<TransferenciaDetalle>();

		TransferenciaDetalle transfDetalle7 = new TransferenciaDetalle();
		transfDetalle7.setCantidadPedida(5);
		transfDetalle7.setCantidadEnviada(5);
		transfDetalle7.setCosto(9000);
		transfDetalle7.setArticulo(art3_Valvoline);
		TransferenciaDetalle transfDetalle8 = new TransferenciaDetalle();
		transfDetalle8.setCantidadPedida(10);
		transfDetalle8.setCantidadEnviada(10);
		transfDetalle8.setCosto(22500);
		transfDetalle8.setArticulo(art3_Valvoline);
		/*
		 * TransferenciaDetalle transfDetalle9 = new TransferenciaDetalle();
		 * transfDetalle9.setCantidadPedida(8);
		 * transfDetalle9.setCantidadEnviada(7); transfDetalle9.setCosto(13500);
		 * transfDetalle9.setArticulo(art3_Valvoline);
		 */

		transferenciaDetalles3.add(transfDetalle7);
		transferenciaDetalles3.add(transfDetalle8);
		// transferenciaDetalles3.add(transfDetalle9);
		transferencia3.setDetalles(transferenciaDetalles3);
		transferencia3.setTransferenciaEstado(tt.transfEstado3); // para reparto
		transferencia3.setTransferenciaTipo(tt.transfTipo2);

		grabarDB(transferencia3);

		Transferencia transferencia4 = new Transferencia();
		transferencia4.setNumero("TRF-00004");
		transferencia4.setFechaCreacion(new Date());
		transferencia4.setFechaRecepcion(new Date());
		transferencia4.setFuncionarioCreador(funcionario1);
		transferencia4.setFuncionarioReceptor(funcionario5);
		transferencia4.setDepositoSalida(dep2);
		transferencia4.setDepositoEntrada(dep7);

		Set<TransferenciaDetalle> transferenciaDetalles4 = new HashSet<TransferenciaDetalle>();

		TransferenciaDetalle transfDetalle10 = new TransferenciaDetalle();
		transfDetalle10.setCantidadPedida(5);
		transfDetalle10.setCantidadEnviada(5);
		transfDetalle10.setCosto(18000);
		transfDetalle10.setArticulo(art4_Valvoline);
		/*
		 * TransferenciaDetalle transfDetalle11 = new TransferenciaDetalle();
		 * transfDetalle11.setCantidadPedida(15);
		 * transfDetalle11.setCantidadEnviada(15);
		 * transfDetalle11.setCosto(13500);
		 * transfDetalle11.setArticulo(articulo8);
		 */

		transferenciaDetalles4.add(transfDetalle10);
		// transferenciaDetalles4.add(transfDetalle11);
		transferencia4.setDetalles(transferenciaDetalles4);
		transferencia4.setTransferenciaEstado(tt.transfEstado3); // para reparto
		transferencia4.setTransferenciaTipo(tt.transfTipo2);

		grabarDB(transferencia4);

		/************************************* RESERVAS **********************************/

		Reserva reserva = new Reserva();
		reserva.setDescripcion("reserva de prueba");
		reserva.setFecha(new Date());
		reserva.setFuncionarioEmisor(funcionario1);
		reserva.setDepositoSalida(dep2);
		reserva.setTipoReserva(tt.reservaReparto);
		reserva.setEstadoReserva(tt.estadoReservaFinalizada);

		ReservaDetalle reservaDetalle1 = new ReservaDetalle();
		reservaDetalle1.setCantidadReservada(3);
		reservaDetalle1.setArticulo(art3_Valvoline);
		//reservaDetalle1
		//		.setEstadoReserva(Configuracion.ESTADO_RESERVA_FINALIZADA);
		ReservaDetalle reservaDetalle2 = new ReservaDetalle();
		reservaDetalle2.setCantidadReservada(5);
		reservaDetalle2.setArticulo(art4_Valvoline);
		//reservaDetalle2
		//		.setEstadoReserva(Configuracion.ESTADO_RESERVA_FINALIZADA);

		Set<ReservaDetalle> reservaDetalles = new HashSet<ReservaDetalle>();
		reservaDetalles.add(reservaDetalle1);
		reservaDetalles.add(reservaDetalle2);
		reserva.setDetalles(reservaDetalles);

		Reserva reserva2 = new Reserva();
		reserva2.setDescripcion("reserva de prueba");
		reserva2.setFecha(new Date());
		reserva2.setFuncionarioEmisor(funcionario2);
		reserva2.setDepositoSalida(dep3);
		reserva2.setTipoReserva(tt.reservaReparto);
		reserva2.setEstadoReserva(tt.estadoReservaActiva);

		ReservaDetalle reservaDetalle3 = new ReservaDetalle();
		reservaDetalle3.setCantidadReservada(4);
		reservaDetalle3.setArticulo(art5_Valvoline);
		//reservaDetalle3.setEstadoReserva(Configuracion.ESTADO_RESERVA_ACTIVA);
		
		ReservaDetalle reservaDetalle05 = new ReservaDetalle();
		reservaDetalle05.setCantidadReservada(9);
		reservaDetalle05.setArticulo(art4_reparo);

		Set<ReservaDetalle> reservaDetalles02 = new HashSet<ReservaDetalle>();
		reservaDetalles02.add(reservaDetalle3);
		reservaDetalles02.add(reservaDetalle05);
		reserva2.setDetalles(reservaDetalles02);

		Reserva reserva3 = new Reserva();
		reserva3.setDescripcion("reserva de prueba");
		reserva3.setFecha(new Date());
		reserva3.setFuncionarioEmisor(funcionario3);
		reserva3.setDepositoSalida(dep4);
		reserva3.setTipoReserva(tt.reservaReparto);
		reserva3.setEstadoReserva(tt.estadoReservaActiva);

		ReservaDetalle reservaDetalle4 = new ReservaDetalle();
		reservaDetalle4.setCantidadReservada(2);
		reservaDetalle4.setArticulo(art5_Valvoline);
		//reservaDetalle4.setEstadoReserva(Configuracion.ESTADO_RESERVA_ACTIVA);

		Set<ReservaDetalle> reservaDetalles3 = new HashSet<ReservaDetalle>();
		reservaDetalles3.add(reservaDetalle4);
		reserva3.setDetalles(reservaDetalles3);

		reservaDetalle1.setArticuloDeposito(artDep3);// dep2 suc1
		reservaDetalle2.setArticuloDeposito(artDep4); // dep7 suc3
		reservaDetalle3.setArticuloDeposito(artDep5);// dep3 suc1
		reservaDetalle4.setArticuloDeposito(artDep12);// dep4 suc2

		grabarDB(reserva);
		grabarDB(reserva2);
		grabarDB(reserva3);

		grabarDB(artDep3);
		grabarDB(artDep4);
		grabarDB(artDep5);
		grabarDB(artDep12);

		/*
		 * transferencia.setReserva(reserva2); grabarDB(transferencia);
		 * 
		 * transferencia3.setReserva(reserva); grabarDB(transferencia3);
		 */

		/****************************************** PAGOS *****************************************/	

		Set<Funcionario> supvs = new HashSet<Funcionario>();
		supvs.add(funcionario4);
		supvs.add(funcionario5);
		
		Caja caj1 = new Caja();
		caj1.setNumero("CAJ-00001");
		caj1.setDescripcion("CAJA PARA PAGOS A PROVEEDORES");
		caj1.setFecha(new Date());
		caj1.setClasificacion(tt.cajaClasificacion1);
		caj1.setTipo(tt.cajaTipo1);
		caj1.setEstado(tt.cajaEstado1);
		caj1.setResponsable(funcionario3);
		caj1.setCreador(funcionario4);
		caj1.setSupervisores(supvs);
		caj1.setSucursal(sucApp1);
		caj1.setDuracion(tt.cajaDuracion1);
		grabarDB(caj1);

		AutoNumero autoNro2 = new AutoNumero();
		autoNro2.setDescripcion("Caja de Pago");
		autoNro2.setFecha(new Date());
		autoNro2.setKey("CAJ");
		autoNro2.setNumero(1); // ultimo numero Caja de Pago
		grabarDB(autoNro2);

		CajaPeriodo cpp1 = new CajaPeriodo();
		cpp1.setNumero("CJP-00001");
		cpp1.setVerificador(funcionario1);
		cpp1.setResponsable(funcionario3);
		cpp1.setApertura(new Date());
		cpp1.setCierre(new Date());
		cpp1.setCaja(caj1);
		grabarDB(cpp1);

		AutoNumero autoNrocpp = new AutoNumero();
		autoNrocpp.setDescripcion("Caja Pago Periodo");
		autoNrocpp.setFecha(new Date());
		autoNrocpp.setKey("CJP");
		autoNrocpp.setNumero(1); // ultimo numero Caja Pago Periodo
		grabarDB(autoNrocpp);		

		/************************************* BANCOS **********************************/

		Set<BancoSucursal> bss = new HashSet<BancoSucursal>();
		Set<BancoSucursal> bss2 = new HashSet<BancoSucursal>();

		BancoSucursal bs = new BancoSucursal();
		bs.setDescripcion("CONTINENTAL - ENCARNACION");
		bs.setDireccion("Las Mercedez 1080");
		bs.setCorreo("continental@mail.com");
		bs.setContacto("Carlos Mendez");
		bs.setTelefono("700-480");
		bss.add(bs);

		BancoSucursal bs2 = new BancoSucursal();
		bs2.setDescripcion("ITAU - CIUDAD DEL ESTE");
		bs2.setDireccion("25 De Mayo 1970");
		bs2.setCorreo("itau@mail.com");
		bs2.setContacto("Jose Mendez");
		bs2.setTelefono("700-580");
		bss2.add(bs2);

		Banco bc = new Banco();
		bc.setDescripcion("CONTINENTAL");
		bc.setDireccion("Avda Mcal Lopez 780");
		bc.setTelefono("021-500-400");
		bc.setCorreo("continental@mail.com");
		bc.setContacto("Ricardo Perez");
		bc.setSucursales(bss);
		grabarDB(bc);

		Banco bc2 = new Banco();
		bc2.setDescripcion("ITAU");
		bc2.setDireccion("Avda Mcal Lopez 1680");
		bc2.setTelefono("021-500-600");
		bc2.setCorreo("itau@mail.com");
		bc2.setContacto("Juan Gonzalez");
		bc2.setSucursales(bss2);
		grabarDB(bc2);
		//para banco regional grupo elite
		Banco bc01 = new Banco();
		bc01.setDescripcion("Regional");
		grabarDB(bc01);
		
		
		// banco cuenta guaranies para regional ge
		BancoCta bcc01 = new BancoCta();
		bcc01.setBanco(bc01);
		bcc01.setFechaApertura(Misc.ParseFecha("11-11-2013 00:00:00", "dd-MM-YYYY HH:mm:ss")); // setear fecha de apertura 11/11/2013 00:00:00
		bcc01.setMoneda(tt.guarani);
		bcc01.setNroCuenta("");
		bcc01.setTipo(tt.bancoCtaCte);
		grabarDB(bcc01);
		
		// banco cuenta dolares para regional ge
		
		BancoCta bcc02 = new BancoCta();
		bcc02.setBanco(bc01);
		bcc02.setFechaApertura(Misc.ParseFecha("11-11-2013 00:00:00", "dd-MM-YYYY HH:mm:ss"));// setear fecha de apertura 11/11/2013 00:00:00
		bcc02.setMoneda(tt.dolar);
		bcc02.setNroCuenta("");
		bcc02.setTipo(tt.bancoCtaCte);
		grabarDB(bcc02);
		
		

		// Set<BancoMovimiento> mvs = new HashSet<BancoMovimiento>();
		// Set<BancoMovimiento> mvs2 = new HashSet<BancoMovimiento>();

		BancoMovimiento mvt1 = new BancoMovimiento();
		mvt1.setFecha(new Date());
		mvt1.setTipoMovimiento(tt.tipoMov27);
		mvt1.setMonto(1000000);
		mvt1.setNroReferencia("11111");
		// mvs.add(mvt1);

		BancoMovimiento mvt2 = new BancoMovimiento();
		mvt2.setFecha(new Date());
		mvt2.setTipoMovimiento(tt.tipoMov28);
		mvt2.setMonto(2000000);
		mvt2.setNroReferencia("22222");
		// mvs.add(mvt2);

		BancoMovimiento mvt3 = new BancoMovimiento();
		mvt3.setFecha(new Date());
		mvt3.setTipoMovimiento(tt.tipoMov29);
		mvt3.setMonto(2000000);
		mvt3.setNroReferencia("33333");
		// mvs.add(mvt3);

		BancoMovimiento mvt4 = new BancoMovimiento();
		mvt4.setFecha(new Date());
		mvt4.setTipoMovimiento(tt.tipoMov30);
		mvt4.setMonto(2500);
		mvt4.setNroReferencia("11111");
		// mvs.add(mvt4);

		BancoMovimiento mvt5 = new BancoMovimiento();
		mvt5.setFecha(new Date());
		mvt5.setTipoMovimiento(tt.tipoMov27);
		mvt5.setMonto(3000);
		mvt5.setNroReferencia("222222");
		// mvs2.add(mvt5);

		BancoMovimiento mvt6 = new BancoMovimiento();
		mvt6.setFecha(new Date());
		mvt6.setTipoMovimiento(tt.tipoMov28);
		mvt6.setMonto(6000);
		mvt6.setNroReferencia("333333");
		// mvs2.add(mvt6);

		BancoMovimiento mvt7 = new BancoMovimiento();
		mvt7.setFecha(new Date());
		mvt7.setTipoMovimiento(tt.tipoMov29);
		mvt7.setMonto(2500);
		mvt7.setNroReferencia("444444");
		// mvs2.add(mvt7);
		
		BancoMovimiento mvt8 = new BancoMovimiento();
		mvt8.setFecha(new Date());
		mvt8.setTipoMovimiento(tt.tipoMov32);
		mvt8.setMonto(500000);
		mvt8.setNroReferencia("55555");
		
		BancoMovimiento mvt9 = new BancoMovimiento();
		mvt9.setFecha(new Date());
		mvt9.setTipoMovimiento(tt.tipoMov33);
		mvt9.setMonto(956789);
		mvt9.setNroReferencia("66666");

		AutoNumero autoNro3 = new AutoNumero();
		autoNro3.setDescripcion("Chequera");
		autoNro3.setFecha(new Date());
		autoNro3.setKey("CHQ-00001");
		autoNro3.setNumero(52640510); // ultimo numero Orden de Pago
		autoNro3.setNumeroHasta(52640600);
		grabarDB(autoNro3);

		AutoNumero autoNro4 = new AutoNumero();
		autoNro4.setDescripcion("Chequera");
		autoNro4.setFecha(new Date());
		autoNro4.setKey("CHQ-00002");
		autoNro4.setNumero(50640500); // ultimo numero Orden de Pago
		autoNro4.setNumeroHasta(50640600);
		grabarDB(autoNro4);

		AutoNumero autoNr5 = new AutoNumero();
		autoNr5.setDescripcion("Chequera");
		autoNr5.setFecha(new Date());
		autoNr5.setKey("CHQ");
		autoNr5.setNumero(2); // ultimo numero Orden de Pago
		grabarDB(autoNr5);

		BancoCta bcc = new BancoCta();
		bcc.setBanco(bc);
		bcc.setFechaApertura(new Date());
		bcc.setMoneda(tt.dolar);
		// bcc.setMovimientos(mvs);
		bcc.setNroCuenta("");
		bcc.setTipo(tt.bancoCtaCte);
		grabarDB(bcc);

		mvt1.setNroCuenta(bcc);
		grabarDB(mvt1);
		mvt2.setNroCuenta(bcc);
		grabarDB(mvt2);
		mvt3.setNroCuenta(bcc);
		grabarDB(mvt3);
		mvt8.setNroCuenta(bcc);
		grabarDB(mvt8);
		mvt9.setNroCuenta(bcc);
		grabarDB(mvt9);

		BancoCta bcc2 = new BancoCta();
		bcc2.setBanco(bc2);
		bcc2.setFechaApertura(new Date());
		bcc2.setMoneda(tt.dolar);
		// bcc2.setMovimientos(mvs2);
		bcc2.setNroCuenta("");
		bcc2.setTipo(tt.bancoCtaCte);
		grabarDB(bcc2);
		
		mvt4.setNroCuenta(bcc2);
		grabarDB(mvt4);
		mvt5.setNroCuenta(bcc2);
		grabarDB(mvt5);
		mvt6.setNroCuenta(bcc2);
		grabarDB(mvt6);
		mvt7.setNroCuenta(bcc2);
		grabarDB(mvt7);

		BancoCta bcc3 = new BancoCta();
		bcc3.setBanco(bc);
		bcc3.setFechaApertura(new Date());
		bcc3.setMoneda(tt.guarani);
		bcc3.setNroCuenta("");
		bcc3.setTipo(tt.bancoCtaCte);
		grabarDB(bcc3);

		BancoCheque bch = new BancoCheque();
		bch.setNumero(52600400);
		bch.setFechaEmision(new Date());
		bch.setFechaVencimiento(new Date());
		bch.setBeneficiario("Partner S.R.L");
		bch.setMoneda(tt.dolar);
		bch.setMonto(1500);
		bch.setConcepto("ORDEN DE PAGO NRO: PAG-00054");
		bch.setMovimiento(mvt1);
		grabarDB(bch);

		BancoCheque bch2 = new BancoCheque();
		bch2.setNumero(52600401);
		bch2.setFechaEmision(new Date());
		bch2.setFechaVencimiento(new Date());
		bch2.setBeneficiario("Navemar S.A.");
		bch2.setMoneda(tt.dolar);
		bch2.setMonto(3000);
		bch2.setConcepto("ORDEN DE PAGO NRO: PAG-00055");
		bch2.setMovimiento(mvt2);
		grabarDB(bch2);

		BancoCheque bch3 = new BancoCheque();
		bch3.setNumero(52600402);
		bch3.setFechaEmision(new Date());
		bch3.setFechaVencimiento(new Date());
		bch3.setBeneficiario("José Ma. Godoy");
		bch3.setMoneda(tt.dolar);
		bch3.setMonto(5000);
		bch3.setConcepto("ORDEN DE PAGO NRO: PAG-00056");
		bch3.setMovimiento(mvt3);
		grabarDB(bch3);

		BancoCheque bch4 = new BancoCheque();
		bch4.setNumero(52600403);
		bch4.setFechaEmision(new Date());
		bch4.setFechaVencimiento(new Date());
		bch4.setBeneficiario("Sabo S.A.");
		bch4.setMoneda(tt.dolar);
		bch4.setMonto(2500);
		bch4.setConcepto("ORDEN DE PAGO NRO: PAG-00057");
		bch4.setMovimiento(mvt1);
		grabarDB(bch4);

		BancoCheque bch5 = new BancoCheque();
		bch5.setNumero(52600404);
		bch5.setFechaEmision(new Date());
		bch5.setFechaVencimiento(new Date());
		bch5.setBeneficiario("Navemar S.A.");
		bch5.setMoneda(tt.dolar);
		bch5.setMonto(3000);
		bch5.setConcepto("ORDEN DE PAGO NRO: PAG-00060");
		bch5.setMovimiento(mvt2);
		grabarDB(bch5);

		BancoCheque bch6 = new BancoCheque();
		bch6.setNumero(52600405);
		bch6.setFechaEmision(new Date());
		bch6.setFechaVencimiento(new Date());
		bch6.setBeneficiario("José Ma. Godoy");
		bch6.setMoneda(tt.dolar);
		bch6.setMonto(5000);
		bch6.setConcepto("ORDEN DE PAGO NRO: PAG-00061");
		bch6.setMovimiento(mvt3);
		grabarDB(bch6);

		BancoCheque bch7 = new BancoCheque();
		bch7.setNumero(52600406);
		bch7.setFechaEmision(new Date());
		bch7.setFechaVencimiento(new Date());
		bch7.setBeneficiario("Sabo S.A.");
		bch7.setMoneda(tt.dolar);
		bch7.setMonto(2500);
		bch7.setConcepto("ORDEN DE PAGO NRO: PAG-00062");
		bch7.setMovimiento(mvt1);
		grabarDB(bch7);
		
		//grabarDB(bEx1);

		/************************************* COMPRAS LOCALES **********************************/

		Set<CompraLocalFactura> cmlfs1 = new HashSet<CompraLocalFactura>();
		Set<CompraLocalFacturaDetalle> clfds1 = new HashSet<CompraLocalFacturaDetalle>();

		CompraLocalFacturaDetalle clfd1 = new CompraLocalFacturaDetalle();
		clfd1.setArticulo(art1_reparo);
		clfd1.setCantidad(10);
		clfd1.setCantidadRecibida(0);
		clfd1.setCostoGs(22500);
		clfd1.setCostoDs(5);
		clfd1.setImporteExentaDs(0);
		clfd1.setImporteExentaGs(0);
		clfd1.setImporteGravadaGs(225000);
		clfd1.setImporteGravadaDs(10);
		clfd1.setDescuentoGs(4500);
		clfd1.setDescuentoDs(1);
		clfd1.setTextoDescuento("20%");
		clfd1.setImporteDescuentoDs(0);
		clfd1.setImporteDescuentoGs(0);
		clfd1.setDescuento(false);
		clfds1.add(clfd1);

		CompraLocalFacturaDetalle clfd2 = new CompraLocalFacturaDetalle();
		clfd2.setArticulo(art2_reparo);
		clfd2.setCantidad(10);
		clfd2.setCantidadRecibida(0);
		clfd2.setCostoGs(4500);
		clfd2.setCostoDs(1);
		clfd2.setImporteExentaDs(0);
		clfd2.setImporteExentaGs(0);
		clfd2.setImporteGravadaGs(45000);
		clfd2.setImporteGravadaDs(10);
		clfd2.setDescuentoGs(0);
		clfd2.setDescuentoDs(0);
		clfd2.setTextoDescuento("0");
		clfd2.setImporteDescuentoDs(0);
		clfd2.setImporteDescuentoGs(0);
		clfd2.setDescuento(false);
		clfds1.add(clfd2);

		CompraLocalFacturaDetalle clfd3 = new CompraLocalFacturaDetalle();
		clfd3.setArticulo(art3_reparo);
		clfd3.setCantidad(10);
		clfd3.setCantidadRecibida(0);
		clfd3.setCostoGs(4500);
		clfd3.setCostoDs(1);
		clfd3.setImporteExentaDs(0);
		clfd3.setImporteExentaGs(0);
		clfd3.setImporteGravadaGs(45000);
		clfd3.setImporteGravadaDs(10);
		clfd3.setDescuentoGs(0);
		clfd3.setDescuentoDs(0);
		clfd3.setTextoDescuento("0");
		clfd3.setImporteDescuentoDs(0);
		clfd3.setImporteDescuentoGs(0);
		clfd3.setDescuento(false);
		clfds1.add(clfd3);

		CompraLocalFacturaDetalle clfd4 = new CompraLocalFacturaDetalle();
		clfd4.setArticulo(art4_reparo);
		clfd4.setCantidad(10);
		clfd4.setCantidadRecibida(0);
		clfd4.setCostoGs(4500);
		clfd4.setCostoDs(1);
		clfd4.setImporteExentaDs(0);
		clfd4.setImporteExentaGs(0);
		clfd4.setImporteGravadaGs(45000);
		clfd4.setImporteGravadaDs(10);
		clfd4.setDescuentoGs(0);
		clfd4.setDescuentoDs(0);
		clfd4.setTextoDescuento("0");
		clfd4.setImporteDescuentoDs(0);
		clfd4.setImporteDescuentoGs(0);
		clfd4.setDescuento(false);
		clfds1.add(clfd4);

		CompraLocalFactura cmlf = new CompraLocalFactura();
		cmlf.setCondicionPago(tt.icp);
		cmlf.setFechaCreacion(new Date());
		cmlf.setFechaOriginal(new Date());
		cmlf.setMoneda(tt.dolar);
		cmlf.setNumero("001-001-0000755");
		cmlf.setTimbrado(t2);
		cmlf.setObservacion("SIN OBSERVACION..");
		cmlf.setProveedor(prov1);
		cmlf.setSucursal(sucApp1);
		cmlf.setTipoCambio(4500);
		cmlf.setDetalles(clfds1);
		cmlf.setTipoMovimiento(tt.tipoMov1);
		cmlf.setDescuentoGs(4500);
		cmlf.setDescuentoDs(1);
		cmlf.setTotalAsignadoGs(265500);
		cmlf.setTotalAsignadoDs(59);
		cmlf.setRecepcionConfirmada(false);
		cmlfs1.add(cmlf);

		Set<CompraLocalGasto> cgs1 = new HashSet<CompraLocalGasto>();

		CompraLocalGasto cg1 = new CompraLocalGasto();
		cg1.setDescripcion("FLETE INTERNO..");
		cg1.setMontoDs(10);
		cg1.setMontoGs(45000);
		cgs1.add(cg1);

		CompraLocalGasto cg2 = new CompraLocalGasto();
		cg2.setDescripcion("GASTO IMPREVISTO..");
		cg2.setMontoDs(5);
		cg2.setMontoGs(22500);
		cgs1.add(cg2);

		Set<CompraLocalOrdenDetalle> cldets1 = new HashSet<CompraLocalOrdenDetalle>();

		CompraLocalOrdenDetalle cldet1 = new CompraLocalOrdenDetalle();
		cldet1.setArticulo(art1_reparo);
		cldet1.setCostoDs(5);
		cldet1.setCostoGs(22500);
		cldet1.setUltCostoGs(22000);
		cldet1.setCantidad(10);
		cldets1.add(cldet1);

		CompraLocalOrdenDetalle cldet2 = new CompraLocalOrdenDetalle();
		cldet2.setArticulo(art2_reparo);
		cldet2.setCostoDs(5);
		cldet2.setCostoGs(22500);
		cldet2.setUltCostoGs(22400);
		cldet2.setCantidad(10);
		cldets1.add(cldet2);

		CompraLocalOrdenDetalle cldet3 = new CompraLocalOrdenDetalle();
		cldet3.setArticulo(art3_reparo);
		cldet3.setCostoDs(5);
		cldet3.setCostoGs(22500);
		cldet3.setUltCostoGs(23000);
		cldet3.setCantidad(10);
		cldets1.add(cldet3);

		CompraLocalOrdenDetalle cldet4 = new CompraLocalOrdenDetalle();
		cldet4.setArticulo(art4_reparo);
		cldet4.setCostoDs(5);
		cldet4.setCostoGs(22500);
		cldet4.setUltCostoGs(22500);
		cldet4.setCantidad(10);
		cldets1.add(cldet4);

		CompraLocalOrden clped1 = new CompraLocalOrden();
		clped1.setNumero("OCL-00001");
		clped1.setFechaCreacion(new Date());
		clped1.setTipoCambio(4500);
		clped1.setObservacion("SIN OBSERVACION..");
		clped1.setCondicionPago(tt.icp);
		clped1.setMoneda(tt.dolar);
		clped1.setProveedor(prov3);
		clped1.setAutorizado(true);
		clped1.setSucursal(sucApp1);
		clped1.setDetalles(cldets1);
		clped1.setFacturas(cmlfs1);
		clped1.setTipoMovimiento(tt.tipoMov7);
		clped1.setResumenGastos(cgs1);
		clped1.setCerrado(false);
		grabarDB(clped1);

		AutoNumero autoNr6 = new AutoNumero();
		autoNr6.setDescripcion("Orden de Compra");
		autoNr6.setFecha(new Date());
		autoNr6.setKey("OCL");
		autoNr6.setNumero(1); // ultimo numero Orden de Compra..
		grabarDB(autoNr6);

		/**
		agenda.addDetalle(agenda.COTIZACION, clped1.getNumero(), 0,
				"Se creo la Orden de Compra..", null);
		agenda.addDetalle(agenda.COTIZACION, clped1.getNumero(), 0,
				"Se autorizo la orden de compra..", null);
		agenda.addDetalle(agenda.COTIZACION, clped1.getNumero(), 0,
				"Se agrego una nueva factura nro: 001-001-0000755..", null);
		agenda.addDetalle(agenda.COTIZACION, clped1.getNumero(), 0,
				"Se agrego un gasto..", null);
		agenda.addDetalle(
				agenda.COTIZACION,
				clped1.getNumero(),
				0,
				"Se confirmo la recepcion de mercaderia de la factura nro: 001-001-0000755",
				null);**/

		/************************************* VEHICULOS **********************************/
		
		//vehiculos grupo Elite
		Vehiculo vehiculo01 = new Vehiculo();
		vehiculo01.setDescripcion("NISSAN SUNNY  - GRIS");
		vehiculo01.setCodigo("001");
		vehiculo01.setMarca("NISSAN");
		vehiculo01.setModelo("SUNNY");
		vehiculo01.setColor("GRIS");
		vehiculo01.setChapa("PXF-207");
		vehiculo01.setConductor(null);
		grabarDB(vehiculo01);
		
		Vehiculo vehiculo02 = new Vehiculo();
		vehiculo02.setDescripcion("KIA K2700");
		vehiculo02.setCodigo("002");
		vehiculo02.setMarca("KIA");
		vehiculo02.setModelo("K2700");
		vehiculo02.setColor("");
		vehiculo02.setChapa("PVZ-191");
		vehiculo02.setConductor(null);
		grabarDB(vehiculo02);
		
		Vehiculo vehiculo03 = new Vehiculo();
		vehiculo03.setDescripcion("NISSAN SUNNY -  BLANCO");
		vehiculo03.setCodigo("003");
		vehiculo03.setMarca("NISSAN");
		vehiculo03.setModelo("SUNNY");
		vehiculo03.setColor("BLANCO");
		vehiculo03.setChapa("PVJ-230");
		vehiculo03.setConductor(null);
		grabarDB(vehiculo03);
		
		Vehiculo vehiculo04 = new Vehiculo();
		vehiculo04.setDescripcion("MOTO LEOPARD");
		vehiculo04.setCodigo("004");
		vehiculo04.setMarca("LEOPARD");
		vehiculo04.setModelo("");
		vehiculo04.setColor("");
		vehiculo04.setChapa("019-BED");
		vehiculo04.setConductor(null);
		grabarDB(vehiculo04);
		
		Vehiculo vehiculo1 = new Vehiculo();
		vehiculo1.setCodigo("001");
		vehiculo1.setDescripcion("Envios rapidos");
		vehiculo1.setConsumo(10.5);
		vehiculo1.setPeso(100);
		vehiculo1.setVolumen(200);
		vehiculo1.setMarca("TOYOTA");
		vehiculo1.setModelo("DYNA 387 - 2009");
		vehiculo1.setColor("BLANCO");
		vehiculo1.setChapa("AAU-603");
		vehiculo1.setObservaciones("");
		vehiculo1.setConductor(funcionario1);
		grabarDB(vehiculo1);

		Vehiculo vehiculo2 = new Vehiculo();
		vehiculo2.setCodigo("002");
		vehiculo2.setDescripcion("Envios lejanos");
		vehiculo2.setConsumo(13.5);
		vehiculo2.setPeso(200);
		vehiculo2.setVolumen(400);
		vehiculo2.setMarca("FORD");
		vehiculo2.setModelo("CARGO 2628e");
		vehiculo2.setColor("BLANCO");
		vehiculo2.setChapa("BHD-543");
		vehiculo2.setObservaciones("");
		vehiculo2.setConductor(funcionario2);
		grabarDB(vehiculo2);

		Vehiculo vehiculo3 = new Vehiculo();
		vehiculo3.setCodigo("003");
		vehiculo3.setDescripcion("Envios lejanos");
		vehiculo3.setConsumo(13.5);
		vehiculo3.setPeso(200);
		vehiculo3.setVolumen(400);
		vehiculo3.setMarca("HYUNDAI");
		vehiculo3.setModelo("HD78");
		vehiculo3.setColor("AZUL");
		vehiculo3.setChapa("AGD-456");
		vehiculo3.setObservaciones("");
		vehiculo3.setConductor(funcionario3);
		grabarDB(vehiculo3);

		Vehiculo vehiculo4 = new Vehiculo();
		vehiculo4.setCodigo("004");
		vehiculo4.setDescripcion("Envios lejanos");
		vehiculo4.setConsumo(13.5);
		vehiculo4.setPeso(200);
		vehiculo4.setVolumen(400);
		vehiculo4.setMarca("HYUNDAI");
		vehiculo4.setModelo("HD65");
		vehiculo4.setColor("AZUL");
		vehiculo4.setChapa("BAY-693");
		vehiculo4.setObservaciones("");
		vehiculo4.setConductor(funcionario4);
		grabarDB(vehiculo4);

		Vehiculo vehiculo5 = new Vehiculo();
		vehiculo5.setCodigo("005");
		vehiculo5.setDescripcion("Envios urgentes");
		vehiculo5.setConsumo(9);
		vehiculo5.setPeso(50);
		vehiculo5.setVolumen(100);
		vehiculo5.setMarca("HYUNDAI");
		vehiculo5.setModelo("HD72");
		vehiculo5.setColor("BLANCO");
		vehiculo5.setChapa("OBE-777");
		vehiculo5.setObservaciones("");
		vehiculo5.setConductor(funcionario5);
		grabarDB(vehiculo5);

		/************************************* REPARTOS **********************************/

		Reparto reparto1 = new Reparto();
		reparto1.setNumero("REP-YHA-0000001");
		reparto1.setFechaCreacion(new Date());
		reparto1.setCreador(funcionario2);
		reparto1.setVehiculo(vehiculo1);
		reparto1.setRepartidor(funcionario1);
		reparto1.setEstadoReparto(tt.estadoReparto3);
		reparto1.setTipoReparto(tt.tipoReparto4);
		reparto1.setSucursal(sucApp1);

		RepartoDetalle repartoDet1 = new RepartoDetalle();
		repartoDet1.setIdMovimiento(new Long(3));
		repartoDet1.setTipoMovimiento(tt.tipoMov17);
		repartoDet1.setObservacion("");
		RepartoDetalle repartoDet2 = new RepartoDetalle();
		repartoDet2.setIdMovimiento(new Long(4));
		repartoDet2.setTipoMovimiento(tt.tipoMov18);
		repartoDet2.setObservacion("");

		Set<RepartoDetalle> rep1detalles = new HashSet<RepartoDetalle>();
		rep1detalles.add(repartoDet1);
		rep1detalles.add(repartoDet2);

		reparto1.setDetalles(rep1detalles);
		grabarDB(reparto1);

		Reparto reparto2 = new Reparto();
		reparto2.setNumero("REP-0000002");
		reparto2.setFechaCreacion(new Date());
		reparto2.setCreador(funcionario1);
		reparto2.setVehiculo(vehiculo2);
		reparto2.setRepartidor(funcionario2);
		reparto2.setEstadoReparto(tt.estadoReparto1);
		reparto2.setTipoReparto(tt.tipoReparto3);
		reparto2.setSucursal(sucApp1);
		reparto2.setProveedor(prov5);
		reparto2.setCosto(150000);

		RepartoDetalle repartoDet3 = new RepartoDetalle();
		repartoDet3.setIdMovimiento(new Long(3));
		repartoDet3.setTipoMovimiento(tt.tipoMov6);
		repartoDet3.setObservacion("");

		Set<RepartoDetalle> rep2detalles = new HashSet<RepartoDetalle>();
		rep2detalles.add(repartoDet3);

		reparto2.setDetalles(rep2detalles);
		//grabarDB(reparto2);

		Reparto reparto3 = new Reparto();
		reparto3.setNumero("REP-YHA-0000002");
		reparto3.setFechaCreacion(new Date());
		reparto3.setCreador(funcionario1);
		reparto3.setVehiculo(vehiculo3);
		reparto3.setRepartidor(funcionario3);
		reparto3.setEstadoReparto(tt.estadoReparto1);
		reparto3.setTipoReparto(tt.tipoReparto4);
		reparto3.setSucursal(sucApp1);

		RepartoDetalle repartoDet4 = new RepartoDetalle();
		repartoDet4.setIdMovimiento(new Long(1));
		repartoDet4.setTipoMovimiento(tt.tipoMov1);
		repartoDet4.setObservacion("");
		RepartoDetalle repartoDet5 = new RepartoDetalle();
		repartoDet5.setIdMovimiento(new Long(2));
		repartoDet5.setTipoMovimiento(tt.tipoMov2);
		repartoDet5.setObservacion("");

		Set<RepartoDetalle> rep3detalles = new HashSet<RepartoDetalle>();
		rep3detalles.add(repartoDet4);
		rep3detalles.add(repartoDet5);

		reparto3.setDetalles(rep3detalles);
		//grabarDB(reparto3);

		AutoNumero autoNroRep = new AutoNumero();
		autoNroRep.setDescripcion("Reparto Yhaguy");
		autoNroRep.setFecha(new Date());
		autoNroRep.setKey("REP-YHA");
		autoNroRep.setNumero(2); // ultimo numero Venta Reparto Yhaguy
		grabarDB(autoNroRep);

		/************************************* TIPOS DE CAMBIO **********************************/

		TipoCambio tc1 = new TipoCambio();
		tc1.setMoneda(tt.dolar);
		tc1.setTipoCambio(tt.tipoCambioOficial);
		tc1.setFecha(new Date());
		tc1.setCompra(4670);
		tc1.setVenta(4500);
		grabarDB(tc1);

		TipoCambio tc2 = new TipoCambio();
		tc2.setMoneda(tt.dolar);
		tc2.setTipoCambio(tt.tipoCambioOficial);
		tc2.setCompra(4370);
		tc2.setVenta(4800);
		grabarDB(tc2);

		TipoCambio tc3 = new TipoCambio();
		tc3.setMoneda(tt.peso);
		tc3.setTipoCambio(tt.tipoCambioOficial);
		tc3.setCompra(2500);
		tc3.setVenta(2700);
		grabarDB(tc3);

		TipoCambio tc4 = new TipoCambio();
		tc4.setMoneda(tt.guarani);
		tc4.setTipoCambio(tt.tipoCambioOficial);
		tc4.setFecha(new Date());
		tc4.setCompra(1);
		tc4.setVenta(1);
		grabarDB(tc4);
		
		TipoCambio tc5 = new TipoCambio();
		tc5.setMoneda(tt.guarani);
		tc5.setTipoCambio(tt.tipoCambioYhaguy);
		tc5.setFecha(new Date());
		tc5.setCompra(1);
		tc5.setVenta(1);
		grabarDB(tc5);

		TipoCambio tc6 = new TipoCambio();
		tc6.setMoneda(tt.dolar);
		tc6.setTipoCambio(tt.tipoCambioYhaguy);
		tc6.setFecha(new Date());
		tc6.setCompra(4550);
		tc6.setVenta(4450);
		grabarDB(tc6);
		
		TipoCambio tc7 = new TipoCambio();
		tc7.setMoneda(tt.peso);
		tc7.setTipoCambio(tt.tipoCambioYhaguy);
		tc7.setFecha(new Date());
		tc7.setCompra(2500);
		tc7.setVenta(2700);
		grabarDB(tc7);
	
		TipoCambio tc8 = new TipoCambio();
		tc8.setMoneda(tt.euro);
		tc8.setTipoCambio(tt.tipoCambioOficial);
		tc8.setFecha(new Date());
		tc8.setCompra(6200);
		tc8.setVenta(5980);
		grabarDB(tc8);
		
		TipoCambio tc9 = new TipoCambio();
		tc9.setMoneda(tt.euro);
		tc9.setTipoCambio(tt.tipoCambioYhaguy);
		tc9.setFecha(new Date());
		tc9.setCompra(6200);
		tc9.setVenta(5980);
		grabarDB(tc9);
		
		// CHISTES
		DBChistes chistes = new DBChistes();
		chistes.chistes();

		/************************************* ARTICULO-GASTO **********************************/

		ArticuloGasto artG1 = new ArticuloGasto();
		artG1.setCreadoPor("popu");
		artG1.setVerificadoPor("popu");
		artG1.setCuentaContable(ct16);
		artG1.setDescripcion("DERECHO ADUANERO");
		grabarDB(artG1);

		ArticuloGasto artG2 = new ArticuloGasto();
		artG2.setCreadoPor("popu");
		artG2.setVerificadoPor("popu");
		artG2.setCuentaContable(ct17);
		artG2.setDescripcion("TAZA INDI");
		grabarDB(artG2);

		ArticuloGasto artG3 = new ArticuloGasto();
		artG3.setCreadoPor("popu");
		artG3.setVerificadoPor("popu");
		artG3.setCuentaContable(ct18);
		artG3.setDescripcion("SERVICIO DE VALORACION ADUANERO");
		grabarDB(artG3);

		ArticuloGasto artG4 = new ArticuloGasto();
		artG4.setCreadoPor("popu");
		artG4.setVerificadoPor("popu");
		artG4.setCuentaContable(ct19);
		artG4.setDescripcion("IVA DESPACHO IMPORTACIÓN");
		grabarDB(artG4);

		ArticuloGasto artG5 = new ArticuloGasto();
		artG5.setCreadoPor("popu");
		artG5.setVerificadoPor("popu");
		artG5.setCuentaContable(ct20);
		artG5.setDescripcion("CANON INFORMÁTICO");
		grabarDB(artG5);

		ArticuloGasto artG6 = new ArticuloGasto();
		artG6.setCreadoPor("popu");
		artG6.setVerificadoPor("popu");
		artG6.setCuentaContable(ct15);
		artG6.setDescripcion("GASTO DE IMPORTACION");
		grabarDB(artG6);

		/************************************* GASTO-DIRECTO **********************************/

		Set<GastoDetalle> gdets1 = new HashSet<GastoDetalle>();

		GastoDetalle gdet1 = new GastoDetalle();
		gdet1.setArticuloGasto(artG1);
		gdet1.setCantidad(2);
		gdet1.setCentroCosto(cc1);
		gdet1.setMontoDs(2);
		gdet1.setMontoGs(9000);
		gdet1.setObservacion("Test");
		gdet1.setTipoIva(tt.tipoIva3);

		GastoDetalle gdet2 = new GastoDetalle();
		gdet2.setArticuloGasto(artG2);
		gdet2.setCantidad(2);
		gdet2.setCentroCosto(cc1);
		gdet2.setMontoDs(2);
		gdet2.setMontoGs(9000);
		gdet2.setObservacion("Test");
		gdet2.setTipoIva(tt.tipoIva3);

		GastoDetalle gdet3 = new GastoDetalle();
		gdet3.setArticuloGasto(artG3);
		gdet3.setCantidad(2);
		gdet3.setCentroCosto(cc1);
		gdet3.setMontoDs(2);
		gdet3.setMontoGs(9000);
		gdet3.setObservacion("Test");
		gdet3.setTipoIva(tt.tipoIva3);

		gdets1.add(gdet1);
		gdets1.add(gdet2);
		gdets1.add(gdet3);

		Gasto g1 = new Gasto();
		g1.setTipoMovimiento(tt.tipoMov18);
		g1.setProveedor(prov8);
		g1.setFecha(new Date());
		g1.setTimbrado(t1);
		g1.setNumeroFactura("001-001-0000110");
		g1.setNumeroTimbrado("150100");
		g1.setCondicionPago(tt.icp);
		g1.setVencimiento(new Date());
		g1.setMoneda(tt.guarani);
		g1.setTipoCambio(1);
		g1.setTotalAsignado(400000);
		g1.setCajaPagoNumero("");
		g1.setExisteComprobanteFisico(false);
		g1.setMotivoComprobanteFisico("");
		g1.setObservacion("");
		g1.setDetalles(gdets1);
		grabarDB(g1);

		/************************************* Venta Pedido **********************************/

		for (int i = 0; i < 4; i++) {
			TipoMovimiento tm = tt.tipoMov18;
			if(i==2) {
				tm = tt.tipoMov17;
			}
			
			Venta vp = new Venta();
			vp.setTipoMovimiento(tm);
			vp.setModoVenta(tt.modoVenta_mostrador);
			vp.setSucursal(sucApp1);
			vp.setFecha(new Date());
			vp.setCuotas(0);
			vp.setCliente(cliente1);
			vp.setEstado(tt.venta_pedidoFacturado);
			vp.setMoneda(tt.guarani);
			vp.setTipoCambio(1);
			vp.setNumero("V-FAC-000000" + (i + 1));
			vp.setNumeroPresupuesto("V-FAC-0000001");
			vp.setObservacion("factura..");
			vp.setAtendido(funcionario1);
			vp.setVendedor(funcionario1);
			vp.setCondicionPago(tt.icp);
			vp.setDeposito(dep1);
			if(i==1) {
				vp.setReserva(reserva2);
			}
			
			if(i==2) {
				vp.setReserva(reserva3);
			}
			
			for (int j = 0; j < 4; j++) {
				
				VentaDetalle vpd = new VentaDetalle();
				vpd.setArticulo(art2_Valvoline);
				vpd.setDescripcion(art2_Valvoline.getDescripcion());
				vpd.setCantidad(10);
				vpd.setCostoUnitarioGs(5000);
				vpd.setPrecioVentaUnitarioGs(10000);
				vpd.setNombreRegla("Mayorista");
				vpd.setDescuentoUnitarioGs(2000);
				vpd.setPrecioVentaFinalUnitarioGs(8000);
				vpd.setImpuestoUnitario(0);
				vpd.setPrecioVentaFinalGs(80000);
				vpd.setImpuestoFinal(0);
				vpd.setReservado(false);

				vp.getDetalles().add(vpd);
			}
			
			grabarDB(vp);			
		}	

		AutoNumero autoNroVP = new AutoNumero();
		autoNroVP.setDescripcion("Venta Factura");
		autoNroVP.setFecha(new Date());
		autoNroVP.setKey("V-FAC");
		autoNroVP.setNumero(2); // ultimo numero Venta Presupuesto
		grabarDB(autoNroVP);

		/*************************** Subdiarios *****************************/

		SubDiarioDetalle subDD1 = new SubDiarioDetalle();
		subDD1.setTipo(1);
		subDD1.setDescripcion("detalle1");
		subDD1.setImporte(200000);

		SubDiarioDetalle subDD2 = new SubDiarioDetalle();
		subDD2.setTipo(2);
		subDD2.setDescripcion("detalle2");
		subDD2.setImporte(200000);

		Set<SubDiarioDetalle> subdDetalles = new HashSet<SubDiarioDetalle>();
		subdDetalles.add(subDD1);
		subdDetalles.add(subDD2);

		SubDiario subdiario1 = new SubDiario();
		subdiario1.setNumero("1");
		subdiario1.setFecha(new Date());
		subdiario1.setDescripcion("test1");
		subdiario1.setDetalles(subdDetalles);

		grabarDB(subdiario1);

		SubDiarioDetalle subDD3 = new SubDiarioDetalle();
		subDD3.setTipo(1);
		subDD3.setDescripcion("detalle3");
		subDD3.setImporte(500000);

		SubDiarioDetalle subDD4 = new SubDiarioDetalle();
		subDD4.setTipo(2);
		subDD4.setDescripcion("detalle4");
		subDD4.setImporte(500000);

		Set<SubDiarioDetalle> subdDetalles2 = new HashSet<SubDiarioDetalle>();
		subdDetalles2.add(subDD3);
		subdDetalles2.add(subDD4);

		SubDiario subdiario2 = new SubDiario();
		subdiario2.setNumero("2");
		subdiario2.setFecha(new Date());
		subdiario2.setDescripcion("test2");
		subdiario2.setDetalles(subdDetalles2);

		grabarDB(subdiario2);

		/*************************** Alertas de prueba *********************/

		// dos alertas no canceladas
		Alerta alert = new Alerta();
		alert.setFechaCreacion(new Date());
		alert.setCreador("pepe");
		alert.setDescripcion("Diferencia de cantidades en articulos xx y xx.");
		alert.setDestino(",juan,");
		alert.setCancelada(false);
		alert.setTipo(tt.alertaUno);
		alert.setNivel(tt.alertaError);

		Alerta alert2 = new Alerta();
		alert2.setFechaCreacion(new Date());
		alert2.setCreador("pepe");
		alert2.setDescripcion("Cambiaron cantidades enviadas en transferencia xx.");
		alert2.setDestino(",juan,,celia,,evelin,");
		alert.setCancelada(false);
		alert2.setTipo(tt.alertaMuchos);
		alert2.setNivel(tt.alertaInformativa);

		// una alerta cancelada
		Alerta alert3 = new Alerta();
		alert3.setFechaCreacion(new Date());
		alert3.setCreador("pepe");
		alert3.setDescripcion("Diferencia de cantidades en articulos xx y xx.");
		alert3.setDestino(",juan,");
		alert3.setFechaCancelacion(new Date());
		alert3.setObservacion("La diferencia ha sido ajustada.");
		alert3.setCancelada(true);
		alert3.setTipo(tt.alertaUno);
		alert3.setNivel(tt.alertaError);

		grabarDB(alert);
		grabarDB(alert2);
		grabarDB(alert3);

		// lsitas de destinos predefinidos para alertas

		AlertaDestinos dest1 = new AlertaDestinos();
		dest1.setDescripcion(Configuracion.DESTINOS_TRF_INTERNA);
		dest1.setDestinos(",juan,,celia,");

		AlertaDestinos dest2 = new AlertaDestinos();
		dest2.setDescripcion(Configuracion.DESTINOS_TRF_REMISION);
		dest2.setDestinos(",juan,,celia,");

		grabarDB(dest1);
		grabarDB(dest2);

		/*
		 * for (int j = 0; j < 30 ; j++) { Alerta a = new Alerta();
		 * a.setFechaCreacion(new Date()); a.setCreador("pepe");
		 * a.setDescripcion("Prueba "+j); a.setDestino(",juan,");
		 * a.setCancelada(false); a.setTipo(tt.alertaUno);
		 * a.setNivel(tt.alertaError); grabarDB(a); }
		 * 
		 * for (int j = 30; j < 60 ; j++) { Alerta a = new Alerta();
		 * a.setFechaCreacion(new Date()); a.setCreador("pepe");
		 * a.setDescripcion("Prueba "+j); a.setDestino(",juan,");
		 * a.setCancelada(false); a.setTipo(tt.alertaUno);
		 * a.setNivel(tt.alertaInformativa); grabarDB(a); }
		 * 
		 * for (int j = 60; j < 90 ; j++) { Alerta a = new Alerta();
		 * a.setFechaCreacion(new Date()); a.setCreador("pepe");
		 * a.setDescripcion("Prueba "+j); a.setDestino(",juan,,celia,,evelin,");
		 * a.setCancelada(true); a.setTipo(tt.alertaMuchos);
		 * a.setNivel(tt.alertaError); grabarDB(a); }
		 * 
		 * for (int j = 90; j < 120 ; j++) { Alerta a = new Alerta();
		 * a.setFechaCreacion(new Date()); a.setCreador("pepe");
		 * a.setDescripcion("Prueba "+j); a.setDestino(",juan,,celia,,evelin,");
		 * a.setCancelada(true); a.setTipo(tt.alertaMuchos);
		 * a.setNivel(tt.alertaInformativa); grabarDB(a); }
		 */

		long finTime = System.currentTimeMillis();

		System.out.println("----- FIN POPULATION ------\n" + "Drop DB: "
				+ (iniTime - ceroTime) + "\n" + "Cargar BD:"
				+ (finTime - iniTime));

		/************************************* CTA CTE EMPRESA *******************************/

		CtaCteImputacion ccip1 = new CtaCteImputacion();
		ccip1.setDondeImputa("FAC-CRE 001-00001");
		ccip1.setQuienImputa("REC 001762");
		ccip1.setMontoImputado(250000);
		ccip1.setTipoImputacion(tt.ctaCtetipoImputacion1);
		ccip1.setMoneda(tt.guarani);
		ccip1.setTipoCambio(1);
		grabarDB(ccip1);

		CtaCteEmpresaMovimiento ccem1 = new CtaCteEmpresaMovimiento();
		ccem1.setIdEmpresa(empresa1.getId());
		ccem1.setNroComprobante("FAC-CRE 001-001-0000130");
		ccem1.setImporteOriginal(250000);
		ccem1.setSaldo(250000);
		ccem1.setFechaEmision(new Date());
		ccem1.setFechaVencimiento(new Date());
		ccem1.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento1);
		ccem1.setIdMovimientoOriginal(123);
		ccem1.setTipoMovimiento(tt.tipoMov18);
		ccem1.setSucursal(sucApp1);
		ccem1.setMoneda(tt.guarani);
		ccem1.getImputaciones().add(ccip1);
		grabarDB(ccem1);

		CtaCteEmpresaMovimiento ccem2 = new CtaCteEmpresaMovimiento();
		ccem2.setIdEmpresa(empresa1.getId());
		ccem2.setNroComprobante("FAC-CRE 001-001-0000220");
		ccem2.setImporteOriginal(1300000);
		ccem2.setSaldo(1300000);
		ccem2.setFechaEmision(new Date());
		ccem2.setFechaVencimiento(new Date());
		ccem2.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento1);
		ccem2.setIdMovimientoOriginal(321);
		ccem2.setTipoMovimiento(tt.tipoMov18);
		ccem2.setSucursal(sucApp1);
		ccem2.setMoneda(tt.guarani);
		grabarDB(ccem2);

		CtaCteEmpresaMovimiento ccem3 = new CtaCteEmpresaMovimiento();
		ccem3.setIdEmpresa(empresa1.getId());
		ccem3.setNroComprobante("FAC-CRE 001-001-0000330");
		ccem3.setImporteOriginal(800000);
		ccem3.setSaldo(800000);
		ccem3.setFechaEmision(new Date());
		ccem3.setFechaVencimiento(new Date());
		ccem3.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento1);
		ccem3.setIdMovimientoOriginal(45);
		ccem3.setTipoMovimiento(tt.tipoMov18);
		ccem3.setSucursal(sucApp1);
		ccem3.setMoneda(tt.guarani);
		grabarDB(ccem3);

		CtaCteEmpresaMovimiento ccem4 = new CtaCteEmpresaMovimiento();
		ccem4.setIdEmpresa(empresa1.getId());
		ccem4.setNroComprobante("REC 001762");
		ccem4.setImporteOriginal(500000);
		ccem4.setSaldo(-250000);
		ccem4.setFechaEmision(new Date());
		ccem4.setFechaVencimiento(new Date());
		ccem4.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento1);
		ccem4.setIdMovimientoOriginal(12);
		ccem4.setTipoMovimiento(tt.tipoMov23);
		ccem4.getImputaciones().add(ccip1);
		ccem4.setSucursal(sucApp1);
		ccem4.setMoneda(tt.guarani);
		grabarDB(ccem4);

		CtaCteEmpresaMovimiento ccem5 = new CtaCteEmpresaMovimiento();
		ccem5.setIdEmpresa(empresa1.getId());
		ccem5.setNroComprobante("FAC-CON 001-001-0000150");
		ccem5.setImporteOriginal(1000);
		ccem5.setSaldo(0);
		ccem5.setFechaEmision(new Date());
		ccem5.setFechaVencimiento(null);
		ccem5.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento1);
		ccem5.setIdMovimientoOriginal(60);
		ccem5.setTipoMovimiento(tt.tipoMov17);
		ccem5.setSucursal(sucApp1);
		ccem5.setMoneda(tt.dolar);
		grabarDB(ccem5);

		CtaCteEmpresaMovimiento ccem6 = new CtaCteEmpresaMovimiento();
		ccem6.setIdEmpresa(empresa1.getId());
		ccem6.setNroComprobante("FAC-CRE 001-001-0002310");
		ccem6.setImporteOriginal(1000);
		ccem6.setSaldo(1000);
		ccem6.setFechaEmision(new Date());
		ccem6.setFechaVencimiento(new Date());
		ccem6.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento2);
		ccem6.setIdMovimientoOriginal(78);
		ccem6.setTipoMovimiento(tt.tipoMov2);
		ccem6.setSucursal(sucApp1);
		ccem6.setMoneda(tt.dolar);
		grabarDB(ccem6);
		
		CtaCteEmpresaMovimiento ccem7 = new CtaCteEmpresaMovimiento();
		ccem7.setIdEmpresa(empresa1.getId());
		ccem7.setNroComprobante("FAC-CRE 001-001-0008888");
		ccem7.setImporteOriginal(500);
		ccem7.setSaldo(500);
		ccem7.setFechaEmision(new Date());
		ccem7.setFechaVencimiento(new Date());
		ccem7.setTipoCaracterMovimiento(tt.ctaCteEmpresaCaracterMovimiento2);
		ccem7.setIdMovimientoOriginal(654);
		ccem7.setTipoMovimiento(tt.tipoMov2);
		ccem7.setSucursal(sucApp1);
		ccem7.setMoneda(tt.euro);
		grabarDB(ccem7);
		
		
		ArticuloPrecioReferencia apr1 = new ArticuloPrecioReferencia();
		apr1.setActivo(true);
		apr1.setArticulo(art2_Valvoline);
		apr1.setDescripcion("");
		apr1.setMoneda(tt.guarani);
		apr1.setPrecio(20000);
		
		grabarDB(apr1);
		
		ArticuloPrecioReferencia apr2 = new ArticuloPrecioReferencia();
		apr2.setActivo(true);
		apr2.setArticulo(art4_reparo);
		apr2.setDescripcion("");
		apr2.setMoneda(tt.guarani);
		apr2.setPrecio(50000);
		
		
		/**************************** NOTA DE CREDITO **************************/
		
		NotaCredito nc = new NotaCredito();		
		nc.setTipoMovimiento(tt.tipoMov19);
		nc.setMoneda(tt.guarani);
		nc.setCliente(cliente1);
		nc.setFechaEmision(new Date());
		nc.setSucursal(sucApp1);
		nc.setNumero("NCV-0000001");
		nc.setObservacion("ítems con Avería..");
		nc.setImporteGs(450000);
		nc.setImporteDs(100);
		nc.setMotivo(tt.nc_motivo_descuento);
		grabarDB(nc);
		
		grabarDB(apr2);
	}
}