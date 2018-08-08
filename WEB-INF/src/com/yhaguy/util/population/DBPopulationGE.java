package com.yhaguy.util.population;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.coreweb.domain.Domain;
import com.coreweb.domain.Tipo;
import com.coreweb.domain.TipoTipo;
import com.coreweb.domain.Usuario;
import com.coreweb.util.AutoNumeroControl;
import com.coreweb.util.Misc;
import com.yhaguy.Configuracion;
import com.yhaguy.domain.AccesoApp;
import com.yhaguy.domain.Articulo;
import com.yhaguy.domain.ArticuloGasto;
import com.yhaguy.domain.Banco;
import com.yhaguy.domain.BancoCta;
import com.yhaguy.domain.CentroCosto;
import com.yhaguy.domain.CtaCteLineaCredito;
import com.yhaguy.domain.CuentaContable;
import com.yhaguy.domain.DepartamentoApp;
import com.yhaguy.domain.Deposito;
import com.yhaguy.domain.Empresa;
import com.yhaguy.domain.Funcionario;
import com.yhaguy.domain.ProcesadoraTarjeta;
import com.yhaguy.domain.RegisterDomain;
import com.yhaguy.domain.SucursalApp;
import com.yhaguy.domain.TipoCambio;
import com.yhaguy.domain.Vehiculo;
import com.yhaguy.util.migracion.ArticuloMigracion;
import com.yhaguy.util.migracion.ClienteMigraConfig;
import com.yhaguy.util.migracion.MigracionArticuloCostoStock;
import com.yhaguy.util.migracion.MigracionPlanDeCuentas;

public class DBPopulationGE {

	/**
	 * Configuracion inicial de usuarios - funcionarios - accesos Grupo Elite
	 */
	
	private static RegisterDomain rr = RegisterDomain.getInstance();
	
	private void dropTablas() throws Exception {
		rr.dropAllTables();
	}
	
	private void grabarDB(Domain d) throws Exception {
		rr.saveObject(d, Configuracion.USER_SYSTEMA);
	}
	
	private DBPopulationTipos cargarTipos() throws Exception {
		DBPopulationTipos tt = new DBPopulationTipos();
		tt.cargaTipos();
		return tt;
	}
	
	private void cargarPerfiles() throws Exception{
		UsuarioPerfilParser.loadMenuConfig();
	}
	
	/**
	 * Carga los ítems de Gastos
	 */
	public void cargarItemsDeGastos() throws Exception {
		
		String[][] artGs = { { "DERECHO ADUANERO", Configuracion.ALIAS_CUENTA_DERECHO_ADUANERO}, 
				{ "TAZA INDI", Configuracion.ALIAS_CUENTA_TAZA_INDI },
				{ "SERVICIO DE VALORACION ADUANERO", Configuracion.ALIAS_CUENTA_VALORACION_ADUANERA },
				{ "IVA DESPACHO IMPORTACIÓN", Configuracion.ALIAS_CUENTA_IVA_DESPACHO }, 
				{ "CANON INFORMÁTICO", Configuracion.ALIAS_CUENTA_CANON_INFORMATICO },
				{ "GASTO DE IMPORTACION", Configuracion.ALIAS_CUENTA_GASTOS_IMPORTACION } };

		for (int i = 0; i < artGs.length; i++) {
			String descr = artGs[i][0];
			String alias = artGs[i][1];
			
			CuentaContable ct = rr.getCuentaContableByAlias_(alias);
			
			ArticuloGasto artG = new ArticuloGasto();
			artG.setCreadoPor("Migracion");
			artG.setVerificadoPor("Migracion");
			artG.setCuentaContable(ct);
			artG.setDescripcion(descr);
			grabarDB(artG);
		}
	}
	
	/**
	 * Población inicial de la BD..
	 */
	public void poblarBD() throws Exception {		
		
		System.out.println("-------------------- Poblando Tipos... --------------------");
		DBPopulationTipos tt = this.cargarTipos();
		MigracionPlanDeCuentas.main(null);
		
		System.out.println("-------------------- Poblando Perfiles... --------------------");
		this.cargarPerfiles();
		
		Usuario dayne  	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(14));
		Usuario leonor 	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(15));
		Usuario david  	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(16));
		Usuario juan   	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(17));
		Usuario adriana	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(18));
		Usuario lorenzo	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(19));
		Usuario mariela	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(20));
		Usuario federico= (Usuario) rr.getObject(Usuario.class.getName(), new Long(21));
		Usuario victor	= (Usuario) rr.getObject(Usuario.class.getName(), new Long(22));
		Usuario patricia= (Usuario) rr.getObject(Usuario.class.getName(), new Long(23));
		
		
		System.out.println("------------------- Poblando Funcionarios... -------------------");
		
		/** Un Depósito para las Importaciones y otro Central **/
		Deposito dep1 = new Deposito();
		dep1.setDescripcion("S1-D1");
		dep1.setObservacion("Depósito Importación");		
		grabarDB(dep1);
		
		Deposito dep2 = new Deposito();
		dep2.setDescripcion("S1-D2");
		dep2.setObservacion("Depósito Central");		
		grabarDB(dep2);
		
		Set<Deposito> deps = new HashSet<Deposito>();
		deps.add(dep1);
		deps.add(dep2);
		
		/** Una Sucursal Central **/
		SucursalApp suc = new SucursalApp();
		suc.setNombre("GE Central");
		suc.setDescripcion("GE Casa Central");
		suc.setEstablecimiento("1");
		suc.setDireccion("Angel Lopez");
		suc.setTelefono("111-222");
		suc.setDepositos(deps);
		grabarDB(suc);
		
		Set<SucursalApp> sucs = new HashSet<SucursalApp>();
		sucs.add(suc);
		
		/** Un centro de costo **/
		Set<CentroCosto> ccs = new HashSet<CentroCosto>();
		String key = Configuracion.NRO_CENTRO_COSTO;
		
		CentroCosto cc = new CentroCosto();
		cc.setNumero(key + "-" + AutoNumeroControl.getAutoNumero(key, 5));
		cc.setDescripcion("ADMINISTRACIÓN - GRUPO ÉLITE");
		cc.setMontoAsignado(100000000);
		ccs.add(cc);
		
		/** Un Departamento Administrativo **/
		List<CuentaContable> cuentas = rr.getCuentasContables();
		DepartamentoApp dto1 = new DepartamentoApp();
		dto1.setNombre("ADMINISTRACIÓN");
		dto1.setDescripcion("Departamento de Administración");
		dto1.setSucursal(suc);	
		dto1.setCentroCostos(ccs);
		dto1.setCuentas(new HashSet<CuentaContable>(cuentas));
		grabarDB(dto1);		
		
		/** Tipos **/
		Tipo rubroFuncionario = rr.getTipoPorSigla(Configuracion.SIGLA_RUBRO_EMPRESA_FUNCIONARIO);
		Tipo monedaLocal = rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI);
		Tipo paraguay = rr.getTipoPorSigla(Configuracion.SIGLA_PAIS_PARAGUAY);
		Tipo personaFisica = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_PERSONA_FISICA);
		Tipo auxiliarAdm = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_AUXILIAR_ADMINISTRATIVO);
		Tipo gerenteAdmi = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_GERENTE_ADMINISTRATIVO);
		Tipo encargadoCompras = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_ENCARGADO_COMPRAS);
		Tipo encargadoDeposito = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_ENCARGADO_DEPOSITO);
		Tipo vendedorExterno = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_CARGO_VENTAS_EXTERNAS);
		Tipo estadoActivo = rr.getTipoPorSigla(Configuracion.SIGLA_TIPO_FUNCIONARIO_ESTADO_ACTIVO);
		
		Set<Tipo> rubros = new HashSet<Tipo>();
		rubros.add(rubroFuncionario);
		
		Set<Tipo> monedas = new HashSet<Tipo>();		
		
		
		/***************** Configuración usuario Dayne Gonzalez ****************/
		
		Empresa empDayne = new Empresa();
		empDayne.setNombre("Gonzalez, Dayne");
		empDayne.setCodigoEmpresa("0");
		empDayne.setFechaIngreso(new Date());
		empDayne.setObservacion("sin obs");
		empDayne.setRazonSocial("Dayne Gonzalez");
		empDayne.setRubroEmpresas(rubros);
		empDayne.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empDayne.setMoneda(monedaLocal);
		empDayne.setMonedas(monedas);
		empDayne.setPais(paraguay);
		empDayne.setSigla("NDF");
		empDayne.setTipoPersona(personaFisica);
		grabarDB(empDayne);
		
		AccesoApp accDayne = new AccesoApp();
		accDayne.setDescripcion("Acceso Dayne Gonzalez");
		accDayne.setDepartamento(dto1);
		accDayne.setSucursales(sucs);
		accDayne.setUsuario(dayne);		
		grabarDB(accDayne);
		
		Set<AccesoApp> accsDayne = new HashSet<AccesoApp>();
		accsDayne.add(accDayne);
		
		Funcionario funDayne = new Funcionario();
		funDayne.setEmpresa(empDayne);
		funDayne.setFuncionarioCargo(gerenteAdmi);
		funDayne.setFuncionarioEstado(estadoActivo);
		funDayne.setAccesos(accsDayne);
		grabarDB(funDayne);
		
		/***********************************************************************/
		
		
		/***************** Configuración usuario Leonor Cibils *****************/
		
		Empresa empLeonor = new Empresa();
		empLeonor.setNombre("Cibils, Leonor");
		empLeonor.setCodigoEmpresa("0");
		empLeonor.setFechaIngreso(new Date());
		empLeonor.setObservacion("sin obs");
		empLeonor.setRazonSocial("Leonor Cibils");
		empLeonor.setRubroEmpresas(rubros);
		empLeonor.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empLeonor.setMoneda(monedaLocal);
		empLeonor.setMonedas(monedas);
		empLeonor.setPais(paraguay);
		empLeonor.setSigla("NDF");
		empLeonor.setTipoPersona(personaFisica);
		grabarDB(empLeonor);
		
		AccesoApp accLeonor = new AccesoApp();
		accLeonor.setDescripcion("Acceso Leonor Cibils");
		accLeonor.setDepartamento(dto1);
		accLeonor.setSucursales(sucs);
		accLeonor.setUsuario(leonor);		
		grabarDB(accLeonor);
		
		Set<AccesoApp> accsLeonor = new HashSet<AccesoApp>();
		accsLeonor.add(accLeonor);
		
		Funcionario funLeonor = new Funcionario();
		funLeonor.setEmpresa(empLeonor);
		funLeonor.setFuncionarioCargo(encargadoCompras);
		funLeonor.setFuncionarioEstado(estadoActivo);
		funLeonor.setAccesos(accsLeonor);
		grabarDB(funLeonor);
		
		/***********************************************************************/
		
		
		/***************** Configuración usuario David Aguilera ****************/
		
		Empresa empDavid = new Empresa();
		empDavid.setNombre("Aguilera, David");
		empDavid.setCodigoEmpresa("0");
		empDavid.setFechaIngreso(new Date());
		empDavid.setObservacion("sin obs");
		empDavid.setRazonSocial("David Aguilera");
		empDavid.setRubroEmpresas(rubros);
		empDavid.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empDavid.setMoneda(monedaLocal);
		empDavid.setMonedas(monedas);
		empDavid.setPais(paraguay);
		empDavid.setSigla("NDF");
		empDavid.setTipoPersona(personaFisica);
		grabarDB(empDavid);
		
		AccesoApp accDavid = new AccesoApp();
		accDavid.setDescripcion("Acceso David Aguilera");
		accDavid.setDepartamento(dto1);
		accDavid.setSucursales(sucs);
		accDavid.setUsuario(david);		
		grabarDB(accDavid);
		
		Set<AccesoApp> accsDavid = new HashSet<AccesoApp>();
		accsDavid.add(accDavid);
		
		Funcionario funDavid = new Funcionario();
		funDavid.setEmpresa(empDavid);
		funDavid.setFuncionarioCargo(auxiliarAdm);
		funDavid.setFuncionarioEstado(estadoActivo);
		funDavid.setAccesos(accsDavid);
		grabarDB(funDavid);
		
		/***********************************************************************/
		
		
		/***************** Configuración usuario Juan Caballero ****************/
		
		Empresa empJuan = new Empresa();
		empJuan.setNombre("Caballero, Juan");
		empJuan.setCodigoEmpresa("0");
		empJuan.setFechaIngreso(new Date());
		empJuan.setObservacion("sin obs");
		empJuan.setRazonSocial("Juan Caballero");
		empJuan.setRubroEmpresas(rubros);
		empJuan.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empJuan.setMoneda(monedaLocal);
		empJuan.setMonedas(monedas);
		empJuan.setPais(paraguay);
		empJuan.setSigla("NDF");
		empJuan.setTipoPersona(personaFisica);
		grabarDB(empJuan);
		
		AccesoApp accJuan = new AccesoApp();
		accJuan.setDescripcion("Acceso Juan Caballero");
		accJuan.setDepartamento(dto1);
		accJuan.setSucursales(sucs);
		accJuan.setUsuario(juan);		
		grabarDB(accJuan);
		
		Set<AccesoApp> accsJuan = new HashSet<AccesoApp>();
		accsJuan.add(accJuan);
		
		Funcionario funJuan = new Funcionario();
		funJuan.setEmpresa(empJuan);
		funJuan.setFuncionarioCargo(encargadoDeposito);
		funJuan.setFuncionarioEstado(estadoActivo);
		funJuan.setAccesos(accsJuan);
		grabarDB(funJuan);
		
		/***********************************************************************/
		
		
		/***************** Configuración usuario Adriana Escobar ***************/
		
		Empresa empAdri = new Empresa();
		empAdri.setNombre("Escobar, Adriana");
		empAdri.setCodigoEmpresa("0");
		empAdri.setFechaIngreso(new Date());
		empAdri.setObservacion("sin obs");
		empAdri.setRazonSocial("Adriana Escobar");
		empAdri.setRubroEmpresas(rubros);
		empAdri.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empAdri.setMoneda(monedaLocal);
		empAdri.setMonedas(monedas);
		empAdri.setPais(paraguay);
		empAdri.setSigla("NDF");
		empAdri.setTipoPersona(personaFisica);
		grabarDB(empAdri);
		
		AccesoApp accAdri = new AccesoApp();
		accAdri.setDescripcion("Acceso Adriana Escobar");
		accAdri.setDepartamento(dto1);
		accAdri.setSucursales(sucs);
		accAdri.setUsuario(adriana);		
		grabarDB(accAdri);
		
		Set<AccesoApp> accsAdri = new HashSet<AccesoApp>();
		accsAdri.add(accAdri);
		
		Funcionario funAdri = new Funcionario();
		funAdri.setEmpresa(empAdri);
		funAdri.setFuncionarioCargo(vendedorExterno);
		funAdri.setFuncionarioEstado(estadoActivo);
		funAdri.setAccesos(accsAdri);
		grabarDB(funAdri);
		
		/***********************************************************************/
		
		
		/****************** Configuración usuario Lorenzo Meza *****************/
		
		Empresa empLorenzo = new Empresa();
		empLorenzo.setNombre("Meza, Lorenzo");
		empLorenzo.setCodigoEmpresa("0");
		empLorenzo.setFechaIngreso(new Date());
		empLorenzo.setObservacion("sin obs");
		empLorenzo.setRazonSocial("Lorenzo Meza");
		empLorenzo.setRubroEmpresas(rubros);
		empLorenzo.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empLorenzo.setMoneda(monedaLocal);
		empLorenzo.setMonedas(monedas);
		empLorenzo.setPais(paraguay);
		empLorenzo.setSigla("NDF");
		empLorenzo.setTipoPersona(personaFisica);
		grabarDB(empLorenzo);
		
		AccesoApp accLorenzo = new AccesoApp();
		accLorenzo.setDescripcion("Acceso Lorenzo Meza");
		accLorenzo.setDepartamento(dto1);
		accLorenzo.setSucursales(sucs);
		accLorenzo.setUsuario(lorenzo);		
		grabarDB(accLorenzo);
		
		Set<AccesoApp> accsLorenzo = new HashSet<AccesoApp>();
		accsLorenzo.add(accLorenzo);
		
		Funcionario funLorenzo = new Funcionario();
		funLorenzo.setEmpresa(empLorenzo);
		funLorenzo.setFuncionarioCargo(vendedorExterno);
		funLorenzo.setFuncionarioEstado(estadoActivo);
		funLorenzo.setAccesos(accsLorenzo);
		grabarDB(funLorenzo);
		
		/***********************************************************************/
		
		
		/************** Configuración usuario Mariela Insaurralde **************/
		
		Empresa empMariela = new Empresa();
		empMariela.setNombre("Insaurralde, Mariela");
		empMariela.setCodigoEmpresa("0");
		empMariela.setFechaIngreso(new Date());
		empMariela.setObservacion("sin obs");
		empMariela.setRazonSocial("Mariela Insaurralde");
		empMariela.setRubroEmpresas(rubros);
		empMariela.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empMariela.setMoneda(monedaLocal);
		empMariela.setMonedas(monedas);
		empMariela.setPais(paraguay);
		empMariela.setSigla("NDF");
		empMariela.setTipoPersona(personaFisica);
		grabarDB(empMariela);
		
		AccesoApp accMariela = new AccesoApp();
		accMariela.setDescripcion("Acceso Mariela Insaurralde");
		accMariela.setDepartamento(dto1);
		accMariela.setSucursales(sucs);
		accMariela.setUsuario(mariela);		
		grabarDB(accMariela);
		
		Set<AccesoApp> accsMariela = new HashSet<AccesoApp>();
		accsMariela.add(accMariela);
		
		Funcionario funMariela = new Funcionario();
		funMariela.setEmpresa(empMariela);
		funMariela.setFuncionarioCargo(auxiliarAdm);
		funMariela.setFuncionarioEstado(estadoActivo);
		funMariela.setAccesos(accsMariela);
		grabarDB(funMariela);
		
		/***********************************************************************/
		
		
		/**************** Configuración usuario Federico Cabrera ***************/
		
		Empresa empFederico = new Empresa();
		empFederico.setNombre("Cabrera, Federico");
		empFederico.setCodigoEmpresa("0");
		empFederico.setFechaIngreso(new Date());
		empFederico.setObservacion("sin obs");
		empFederico.setRazonSocial("Federico Cabrera");
		empFederico.setRubroEmpresas(rubros);
		empFederico.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empFederico.setMoneda(monedaLocal);
		empFederico.setMonedas(monedas);
		empFederico.setPais(paraguay);
		empFederico.setSigla("NDF");
		empFederico.setTipoPersona(personaFisica);
		grabarDB(empFederico);
		
		AccesoApp accFederico = new AccesoApp();
		accFederico.setDescripcion("Acceso Federico Cabrera");
		accFederico.setDepartamento(dto1);
		accFederico.setSucursales(sucs);
		accFederico.setUsuario(federico);		
		grabarDB(accFederico);
		
		Set<AccesoApp> accsFederico = new HashSet<AccesoApp>();
		accsFederico.add(accFederico);
		
		Funcionario funFederico = new Funcionario();
		funFederico.setEmpresa(empFederico);
		funFederico.setFuncionarioCargo(auxiliarAdm);
		funFederico.setFuncionarioEstado(estadoActivo);
		funFederico.setAccesos(accsFederico);
		grabarDB(funFederico);
		
		/***********************************************************************/
		
		
		/**************** Configuración usuario Victor Dominguez ***************/
		
		Empresa empVictor = new Empresa();
		empVictor.setNombre("Dominguez, Victor");
		empVictor.setCodigoEmpresa("0");
		empVictor.setFechaIngreso(new Date());
		empVictor.setObservacion("sin obs");
		empVictor.setRazonSocial("Victor Dominguez");
		empVictor.setRubroEmpresas(rubros);
		empVictor.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empVictor.setMoneda(monedaLocal);
		empVictor.setMonedas(monedas);
		empVictor.setPais(paraguay);
		empVictor.setSigla("NDF");
		empVictor.setTipoPersona(personaFisica);
		grabarDB(empVictor);
		
		AccesoApp accVictor = new AccesoApp();
		accVictor.setDescripcion("Acceso Victor Dominguez");
		accVictor.setDepartamento(dto1);
		accVictor.setSucursales(sucs);
		accVictor.setUsuario(victor);		
		grabarDB(accVictor);
		
		Set<AccesoApp> accsVictor = new HashSet<AccesoApp>();
		accsVictor.add(accVictor);
		
		Funcionario funVictor = new Funcionario();
		funVictor.setEmpresa(empVictor);
		funVictor.setFuncionarioCargo(vendedorExterno);
		funVictor.setFuncionarioEstado(estadoActivo);
		funVictor.setAccesos(accsVictor);
		grabarDB(funVictor);
		
		/***********************************************************************/
		
		
		/*************** Configuración usuario Patricia Martinez ***************/
		
		Empresa empPaty = new Empresa();
		empPaty.setNombre("Martinez, Patricia");
		empPaty.setCodigoEmpresa("0");
		empPaty.setFechaIngreso(new Date());
		empPaty.setObservacion("sin obs");
		empPaty.setRazonSocial("Patricia Martinez");
		empPaty.setRubroEmpresas(rubros);
		empPaty.setRuc(Configuracion.RUC_EMPRESA_LOCAL);
		empPaty.setMoneda(monedaLocal);
		empPaty.setMonedas(monedas);
		empPaty.setPais(paraguay);
		empPaty.setSigla("NDF");
		empPaty.setTipoPersona(personaFisica);
		grabarDB(empPaty);
		
		AccesoApp accPaty = new AccesoApp();
		accPaty.setDescripcion("Acceso Patricia Martinez");
		accPaty.setDepartamento(dto1);
		accPaty.setSucursales(sucs);
		accPaty.setUsuario(patricia);		
		grabarDB(accPaty);
		
		Set<AccesoApp> accsPaty = new HashSet<AccesoApp>();
		accsPaty.add(accPaty);
		
		Funcionario funPaty = new Funcionario();
		funPaty.setEmpresa(empPaty);
		funPaty.setFuncionarioCargo(encargadoCompras);
		funPaty.setFuncionarioEstado(estadoActivo);
		funPaty.setAccesos(accsPaty);
		grabarDB(funPaty);
		
		/*************************** Datos Utilizados en Cta. Cte.**********************************/
		
		/**
		 * Los datos correspondientes a las lineas de creditos utilizadas en GE
		 */
		CtaCteLineaCredito l0 = new CtaCteLineaCredito();
		l0.setLinea(200000);
		l0.setDescripcion("A");
		grabarDB(l0);
		
		CtaCteLineaCredito l1 = new CtaCteLineaCredito();
		l1.setLinea(500000);
		l1.setDescripcion("B");
		grabarDB(l1);
		
		CtaCteLineaCredito l2 = new CtaCteLineaCredito();
		l2.setLinea(1000000);
		l2.setDescripcion("C");
		grabarDB(l2);
		
		CtaCteLineaCredito l3 = new CtaCteLineaCredito();
		l3.setLinea(2000000);
		l3.setDescripcion("D");
		grabarDB(l3);

		CtaCteLineaCredito l4 = new CtaCteLineaCredito();
		l4.setLinea(3000000);
		l4.setDescripcion("E");
		grabarDB(l4);
		
		CtaCteLineaCredito l5 = new CtaCteLineaCredito();
		l5.setLinea(5000000);
		l5.setDescripcion("F");
		grabarDB(l5);
		
		CtaCteLineaCredito l6 = new CtaCteLineaCredito();
		l6.setLinea(7000000);
		l6.setDescripcion("G");
		grabarDB(l6);
		
		CtaCteLineaCredito l7 = new CtaCteLineaCredito();
		l7.setLinea(8000000);
		l7.setDescripcion("H");
		grabarDB(l7);
		
		CtaCteLineaCredito l8 = new CtaCteLineaCredito();
		l8.setLinea(10000000);
		l8.setDescripcion("I");
		grabarDB(l8);
		
		CtaCteLineaCredito l9 = new CtaCteLineaCredito();
		l9.setLinea(12000000);
		l9.setDescripcion("J");
		grabarDB(l9);
		
		CtaCteLineaCredito l10 = new CtaCteLineaCredito();
		l10.setLinea(15000000);
		l10.setDescripcion("K");
		grabarDB(l10);
		
		CtaCteLineaCredito l11 =  new CtaCteLineaCredito();
		l11.setLinea(18000000);
		l11.setDescripcion("L");
		grabarDB(l11);
		
		CtaCteLineaCredito l12 = new CtaCteLineaCredito();
		l12.setLinea(20000000);
		l12.setDescripcion("M");
		grabarDB(l12);
		
		CtaCteLineaCredito l13 = new CtaCteLineaCredito();
		l13.setLinea(30000000);
		l13.setDescripcion("N");
		grabarDB(l13);
		
		/**
		 * El tipo de cambio del Guarani que siempre es igual a '1' tanto para App y para BCP
		 */		
		TipoCambio tc1 = new TipoCambio();
		tc1.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		tc1.setTipoCambio(rr.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_APP));
		tc1.setFecha(new Date());
		tc1.setCompra(1);
		tc1.setVenta(1);
		grabarDB(tc1);
		
		TipoCambio tc2 = new TipoCambio();
		tc2.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_GUARANI));
		tc2.setTipoCambio(rr.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP));
		tc2.setFecha(new Date());
		tc2.setCompra(1);
		tc2.setVenta(1);
		grabarDB(tc2);
		
		/**
		 * Tipo de Cambio para moneda Dolar..
		 */
		TipoCambio tc3 = new TipoCambio();
		tc3.setMoneda(rr.getTipoPorSigla(Configuracion.SIGLA_MONEDA_DOLAR));
		tc3.setTipoCambio(rr.getTipoPorDescripcion(Configuracion.ID_TIPO_CAMBIO_BCP));
		tc3.setFecha(new Date());
		tc3.setCompra(4550);
		tc3.setVenta(4550);
		grabarDB(tc3);
		
		
		/*************************** Banco**********************************/
		
		TipoTipo bancos = new TipoTipo();
		bancos.setDescripcion("Bancos");
		grabarDB(bancos);
		
		Tipo banco1 = new Tipo();
		banco1.setTipoTipo(bancos);
		banco1.setDescripcion("Banco Regional");
		banco1.setSigla("BNC-REGL");
		grabarDB(banco1);
		
		
		//para banco regional grupo elite
		Banco bc01 = new Banco();
		bc01.setDescripcion("Regional");
		bc01.setBancoTipo(banco1);
		grabarDB(bc01);

		// banco cuenta guaranies para regional ge
		BancoCta bcc01 = new BancoCta();
		bcc01.setBanco(bc01);
		bcc01.setFechaApertura(Misc.ParseFecha("11-11-2013 00:00:00",
				"dd-MM-YYYY HH:mm:ss"));
		bcc01.setMoneda(tt.guarani);
		bcc01.setNroCuenta("");
		bcc01.setTipo(tt.bancoCtaCte);
		grabarDB(bcc01);

		// banco cuenta dolares para regional ge

		BancoCta bcc02 = new BancoCta();
		bcc02.setBanco(bc01);
		bcc02.setFechaApertura(Misc.ParseFecha("11-11-2013 00:00:00",
				"dd-MM-YYYY HH:mm:ss"));
		bcc02.setMoneda(tt.dolar);
		bcc02.setNroCuenta("");
		bcc02.setTipo(tt.bancoCtaCte);
		grabarDB(bcc02);
		
				
		/*************************** Vehiculo **********************************/

		// vehiculos grupo Elite
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
		vehiculo03.setDescripcion("NISSAN SUNNY - BLANCO");
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
		
		
		/********************* Articulos de referencia *************************/
		
		String[] refs = { "@GASTOS", "@DESCUENTO", "@PRORRATEO" };
		
		for (int i = 0; i < refs.length; i++) {
			
			Articulo ref = new Articulo();
			ref.setDescripcion(refs[i]);
			ref.setCodigoInterno(refs[i]);
			ref.setCodigoOriginal(refs[i]);
			ref.setCodigoBarra(refs[i]);
			ref.setObservacion("");
			ref.setArticuloMarca(tt.sinReferenciaTipo);
			ref.setArticuloParte(tt.sinReferenciaTipo);
			ref.setArticuloLinea(tt.sinReferenciaTipo);
			ref.setArticuloFamilia(tt.sinReferenciaTipo);
			ref.setArticuloEstado(tt.estadoArticulo1);
			ref.setArticuloPresentacion(rr.getArticuloPresentacionById(1));
			ref.setImportado(false);
			
			grabarDB(ref);
		}
		
		
		/********************* Procesadoras de Tarjetas *************************/
		
		String[] procs = { "Procard", "Panal", "Bancard", "Cabal" };
		
		for (int i = 0; i < procs.length; i++) {
			
			ProcesadoraTarjeta pr = new ProcesadoraTarjeta();
			pr.setNombre(procs[i]);
			pr.setBanco(bcc01);			
			pr.setSucursal(suc);			
			grabarDB(pr);
		}
		
	}	
	
	public static void main(String[] args) throws Exception{
		
		boolean migrarClientes = false;
		boolean migrarArticulos = false;
		
		DBPopulationGE ge = new DBPopulationGE();
		ge.dropTablas();
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader bfr = new BufferedReader(isr);
		
		System.out.print("Migrar Clientes ? (Y/N):");
		String in = bfr.readLine();
		if (in.compareTo("Y") == 0) {
			migrarClientes = true;
		}
		
		System.out.print("Migrar Articulos? (Y/N):");
		in = bfr.readLine();
		if (in.compareTo("Y") == 0) {
			migrarArticulos = true;
		}
		
		ge.poblarBD();		
		ge.cargarItemsDeGastos();
		
		DBVistas.main(null); //Crear las vistas		
		
		if (migrarClientes == true) {
			ClienteMigraConfig.main(null);
		}
		
		if (migrarArticulos == true) {
			ArticuloMigracion.main(null);
			MigracionArticuloCostoStock.main(null);
		}
	}	
}
